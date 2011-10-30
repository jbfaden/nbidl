/*
 * CompletionSupport.java
 *
 * Created on March 11, 2007, 9:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.completion;

import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.dataobject.ProceduresFile;
import com.cottagesystems.nbidl.dataobject.ProceduresFileDataObject;
import com.cottagesystems.nbidl.debugger.PvwaveDebugger;
import com.cottagesystems.nbidl.debugger.PvwaveStop;
import com.cottagesystems.nbidl.model.IdlClass;
import com.cottagesystems.nbidl.model.IdlStruct;
import com.cottagesystems.nbidl.model.StructTag;
import com.cottagesystems.nbidl.session.Session;
import com.cottagesystems.nbidl.session.SessionSupport;
import com.cottagesystems.nbidl.syntax.IDLRoutinesDataBase;
import com.cottagesystems.nbidl.syntax.PvwaveSyntax;
import com.cottagesystems.nbidl.syntax.PvwaveSyntaxScraper;
import com.cottagesystems.nbidl.syntax.PvwaveTokenContext;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Registry;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.TokenProcessor;
import org.netbeans.editor.Utilities;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;

/**
 *
 * @author jbf
 */
public class CompletionSupport {
    
    
    public class CompletionContext {
        TokenID contextTok;
        /**
         * the context of the string to complete.  This is the name of a function,
         * an expression, etc, to be interpretted based on contextTok.
         */
        String contextString;
        
        /**
         * the beginning of the keyword
         */
        String completable;
        
        /**
         * a keyword is to be completed.
         */
        boolean inKeywords;
        
        /**
         * the completion is a command line completion.
         */
        boolean inCommandLine;
        
        final static String COMPLETION_TYPE_NONE="none";
        final static String COMPLETION_TYPE_KEYWORD="kw";
        
        
        /**
         * struct.**
         */
        final static String COMPLETION_TYPE_STRUCT="tag";
        
        /**
         * obj->**
         */
        final static String COMPLETION_TYPE_OBJECT="obj";
        
        /**
         * obj->call( **
         */
        final static String COMPLETION_TYPE_OBJECT_KW_FUNC="obj_kw_func";
        
        /**
         * this.**
         */
        final static String COMPLETION_TYPE_OBJECT_FIELD="obj_field";
        
        /**
         * mypr**,
         */
        final static String COMPLETION_TYPE_PRO="pro";
        
        /**
         * x= myfunc**
         */
        final static String COMPLETION_TYPE_FUNC="func";
        
        /**
         * x= myfunction( ** )
         */
        final static String COMPLETION_TYPE_KW_FUNC="kw_func";
        
        String completionType=COMPLETION_TYPE_PRO;
        
        public String toString() {
            return contextString + " "+completionType+" " + String.valueOf(completable);
        }
    }
    
    class MyTokenProcessor implements TokenProcessor {
        BaseDocument doc;
        int pos;
        
        int bufferShift;
        
        /**
         * result of the last parsing
         */
        CompletionContext functionToken;
        
        /**
         * current context of the scan.
         */
        CompletionContext cc;
        
        /**
         * context created by the last token.  null if last token wasn't a function.
         */
        CompletionContext lastCC;
        Stack contextStack;
        
        int nonWhiteSpaceCount=0;
        
        boolean inCommandLine;
        
        MyTokenProcessor( BaseDocument doc, int pos ) {
            this.doc= doc;
            this.pos= pos;
            contextStack= new Stack();
            cc= new CompletionContext();
            PvwaveDebugger debug= PvwaveDebugger.getInstance();
            inCommandLine= debug!=null && debug.isCommandLine(doc);
            System.err.println( "---- completion syntax for pos="+pos+" ----" );
        }
        
        public boolean token(TokenID tok, TokenContextPath tokenContextPath, int tpos, int len) {
            try {
                if ( functionToken!=null ) return true;
                if ( tok.getNumericID()!=PvwaveTokenContext.WHITESPACE_ID ) nonWhiteSpaceCount++;
                
                if ( tok.getNumericID()==PvwaveTokenContext.USERFUNC_ID
                        || tok.getNumericID()==PvwaveTokenContext.PVFUNC_ID ) {
                    lastCC= new CompletionContext();
                    lastCC.contextString= doc.getText(tpos+bufferShift, len);
                    lastCC.contextTok= tok;
                    if ( nonWhiteSpaceCount>1 ) {
                        lastCC.completionType= lastCC.COMPLETION_TYPE_KW_FUNC;
                    }
                    
                } else if ( tok.getNumericID()==PvwaveTokenContext.IDENTIFIER_ID ) {
                    if ( cc.completionType==cc.COMPLETION_TYPE_STRUCT ) {
                        cc.completable= doc.getText(tpos+bufferShift,len);
                        lastCC= new CompletionContext();
                        lastCC.contextTok= cc.contextTok;
                        lastCC.contextString= cc.contextString + "." + doc.getText(tpos+bufferShift,len);
                        lastCC.completionType= cc.completionType;
                    } else if ( cc.completionType==cc.COMPLETION_TYPE_OBJECT ) {
                        cc.completable= doc.getText(tpos+bufferShift,len);
                        lastCC= new CompletionContext();
                        lastCC.contextTok= cc.contextTok;
                        lastCC.contextString= cc.contextString + "->" + doc.getText(tpos+bufferShift,len);
                        lastCC.completionType= cc.COMPLETION_TYPE_OBJECT_KW_FUNC;
                    } else {
                        lastCC= new CompletionContext();
                        lastCC.contextString= doc.getText(tpos+bufferShift,len);
                        lastCC.contextTok= tok;
                        lastCC.completionType= nonWhiteSpaceCount==1 ? lastCC.COMPLETION_TYPE_PRO : lastCC.COMPLETION_TYPE_KW_FUNC;
                        cc.completable= doc.getText(tpos+bufferShift,len);
                    }
                    
                } else if ( tok.getNumericID()==PvwaveTokenContext.LPAREN_ID ) {
                    if ( lastCC!=null && lastCC.completionType.equals(cc.COMPLETION_TYPE_OBJECT_KW_FUNC ) ) {
                        contextStack.push(cc);
                        lastCC.completionType= cc.COMPLETION_TYPE_OBJECT_KW_FUNC ;
                        cc= lastCC;
                    } else  if (lastCC!=null) {
                        contextStack.push(cc);
                        lastCC.completionType= cc.COMPLETION_TYPE_KW_FUNC;
                        cc= lastCC;
                    } else { // expression
                        contextStack.push(cc);
                        cc= new CompletionContext();
                        cc.completionType= cc.COMPLETION_TYPE_FUNC;
                    }
                    lastCC= null;
                    
                } else if ( tok.getNumericID()==PvwaveTokenContext.RPAREN_ID ) {
                    if ( contextStack.size()>0 ) {
                        cc= (CompletionContext) contextStack.pop();
                    }
                    lastCC= null;
                    
                } else if ( tok.getNumericID()==PvwaveTokenContext.LBRACKET_ID ) {
                    contextStack.push(cc);
                    cc= new CompletionContext();
                    cc.completionType= cc.COMPLETION_TYPE_FUNC;
                    lastCC= null;
                    
                } else if ( tok.getNumericID()==PvwaveTokenContext.RBRACKET_ID ) {
                    if ( contextStack.size()>0 ) cc= (CompletionContext) contextStack.pop();
                    lastCC= null;
                    
                } else if ( tok.getNumericID()==PvwaveTokenContext.COMMA_ID ) {
                    if ( lastCC!=null && cc.completionType==cc.COMPLETION_TYPE_PRO ) {
                        lastCC.completionType= lastCC.COMPLETION_TYPE_KW_FUNC;
                        cc= lastCC;
                    } else if ( cc.completionType==cc.COMPLETION_TYPE_STRUCT ) {
                        if ( contextStack.size()>0 ) cc= (CompletionContext) contextStack.pop();
                    }
                    
                } else if ( tok.getNumericID()==PvwaveTokenContext.DOT_ID ) {
                    if ( lastCC!=null ) {
                        if ( cc.completionType!=cc.COMPLETION_TYPE_STRUCT ) contextStack.push(cc);
                        lastCC.completionType= lastCC.COMPLETION_TYPE_STRUCT;
                        cc= lastCC;
                    }
                } else if ( tok.getNumericID()==PvwaveTokenContext.MINUS_GT_ID ) {
                    if ( lastCC!=null ) {
                        if ( cc.completionType!=cc.COMPLETION_TYPE_OBJECT ) contextStack.push(cc);
                        lastCC.completionType= lastCC.COMPLETION_TYPE_OBJECT;
                        cc= lastCC;
                    }
                } else if ( tok.getNumericID()==PvwaveTokenContext.EQ_ID ) {
                    lastCC= null;
                    if ( cc.completionType==cc.COMPLETION_TYPE_PRO ) cc.completionType= cc.COMPLETION_TYPE_KW_FUNC;
                    
                } else {
                    lastCC= null;
                }
                
                System.err.println( ": "+(tpos+bufferShift)+" "+tok.getName()+" cc:"+cc+"\t\t  pendingCC:"+lastCC );
                
                // we're done if the pointer position is here
                if ( tpos+bufferShift < pos &&  pos <= tpos+bufferShift + len ) {
                    System.err.println(cc);
                    functionToken= cc;
                    if ( functionToken!=null ) {
                        if ( tok.getNumericID()==PvwaveTokenContext.DOT_ID ) {
                            functionToken.completable= "";
                        } else if ( tok.getNumericID()==PvwaveTokenContext.MINUS_GT_ID ) {
                            functionToken.completable= "";
                        } else if ( tok.getNumericID()==PvwaveTokenContext.WHITESPACE_ID ) {
                            functionToken.completable= "";
                        } else if ( tok.getNumericID()==PvwaveTokenContext.DIV_ID ) {
                            functionToken.completable= "";
                        } else if ( tok.getNumericID()==PvwaveTokenContext.LPAREN_ID ) {
                            functionToken.completable= "";
                        } else if ( tok.getNumericID()==PvwaveTokenContext.USERFUNC_ID ) {
                            functionToken= lastCC;
                            functionToken.completable= doc.getText( tpos+bufferShift, pos-(tpos+bufferShift) );
                        } else if ( tok.getNumericID()==PvwaveTokenContext.USERFUNC_ID || tok.getNumericID()==PvwaveTokenContext.PVFUNC_ID ) {
                            functionToken= lastCC;
                            functionToken.completable= doc.getText( tpos+bufferShift, pos-(tpos+bufferShift) );
                        } else {
                            functionToken.completable= doc.getText( tpos+bufferShift, pos-(tpos+bufferShift) );
                        }
                        
                        if ( functionToken.completable==null ) functionToken.completable="";
                        
                        if ( ( functionToken.completionType==functionToken.COMPLETION_TYPE_KW_FUNC
                                || functionToken.completionType==functionToken.COMPLETION_TYPE_FUNC ) && functionToken.completable.equals("") ) {
                            functionToken.completionType= functionToken.COMPLETION_TYPE_KEYWORD;
                        }
                        
                    }
                    functionToken.inCommandLine= this.inCommandLine;
                    
                } else {
                    // a procedure can only be the first token on the line, so don't complete on procedures after here.
                    if ( tok.getNumericID()!=PvwaveTokenContext.WHITESPACE_ID && cc.completionType==cc.COMPLETION_TYPE_PRO ) {
                        //cc.completionType= cc.COMPLETION_TYPE_FUNC;
                    }
                }
                
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            return true;
        }
        
        public int eot(int i) {
            return 0;
        }
        
        public void nextBuffer(char[] c, int offset, int len, int startPos, int preScan, boolean lastBuffer ) {
            bufferShift= -1 * offset + startPos;
        }
        
        public CompletionContext getCompletionContext() {
            return functionToken;
        }
    }
    
    
    synchronized  CompletionContext getCompletionContext( final BaseDocument doc, int pos ) {
        int i0, i1;
        
        try {
            i0= Utilities.getRowStart( doc, pos );
            i1= Utilities.getRowEnd( doc, pos );
            
            SyntaxSupport support= new SyntaxSupport( doc );
            Syntax syntax= new PvwaveSyntax();
            
            String theString= doc.getText( i0, i1-i0 );
            
            MyTokenProcessor myProcessor= new MyTokenProcessor( doc, pos );
            
            support.tokenizeText( myProcessor, i0, i1, true );
            CompletionContext context= myProcessor.getCompletionContext();
            return context;
            
        } catch ( BadLocationException ex ) {
            ex.printStackTrace();
            return null;
        }
    }
    
    static CompletionItem getMessageCompletionItem( final String message ) {
        return getMessageCompletionItem( message, null );
    }
    
    static CompletionItem getMessageCompletionItem( final String message, final String doc ) {
        return new CompletionItem() {
            public void defaultAction( JTextComponent jTextComponent ) {  }
            public void processKeyEvent(KeyEvent keyEvent) { }
            public int getPreferredWidth(Graphics graphics, Font font) {
                return 210;
            }
            public void render(Graphics graphics, Font font, Color color, Color color0, int i, int i0, boolean b) {
                CompletionUtilities.renderHtml( null,"<em>"+message+"</em>",null,graphics,font, color,i,i0,b);
            }
            public CompletionTask createDocumentationTask() { return null; }
            public CompletionTask createToolTipTask() { return null; }
            public boolean instantSubstitution(JTextComponent jTextComponent) { return false; }
            public int getSortPriority() { return 1;  }
            public CharSequence getSortText() { return "aaa"; }
            public CharSequence getInsertPrefix() { return ""; }
        };
    }
    
    public static Procedure getCarotProcedure() {
        Node[] activatedNodes= TopComponent.getRegistry().getActivatedNodes();
        System.err.println(activatedNodes);
        
        JTextComponent editor = Registry.getMostActiveComponent();
        int pos = editor.getCaretPosition();
        
        Procedure result=null;
        
        for ( int i=0; i<activatedNodes.length; i++ ) {
            DataObject c = (DataObject) activatedNodes[i].getCookie(DataObject.class);
            if ( c instanceof ProceduresFileDataObject ) {
                ProceduresFileDataObject pf= (ProceduresFileDataObject)c;
                ProceduresFile f;
                try {
                    f = PvwaveSyntaxScraper.parse(c.getPrimaryFile());
                    result= f.getProcedureAtOffset( pos );
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return result;
    }
    
    
    static List addLiveCompletions(CompletionContext context) {
        PvwaveDebugger debugger= PvwaveDebugger.getInstance();
        if ( debugger==null ) return new ArrayList();
        Session session= debugger.getSession();
        ArrayList result= new ArrayList();
        if ( session==null || !session.isStarted() ) {
            result.add( CompletionSupport.getMessageCompletionItem( "IDL session not available" ) );
            return result;
        } else {
            // is the debugger stopped in the current file?  If so, then live completion is enabled.
            //   1. getFileObject for the editor
            //   2. getFileObject for session.
            PvwaveStop stop= debugger.getCurrentStop();
            
            if ( stop==null ) {
                return result;
            }
            
            boolean canComplete= true;
            
            if ( stop!=PvwaveStop.MAIN ) {
                FileObject sessionFo= (FileObject) stop.getLineObject().getLookup().lookup( FileObject.class );
                Node[] activatedNodes= TopComponent.getRegistry().getActivatedNodes();
                DataObject c = (DataObject) activatedNodes[0].getCookie(DataObject.class);
                
                FileObject caratFo = null;
                if ( c instanceof ProceduresFileDataObject ) {
                    caratFo= c.getPrimaryFile();
                }
                canComplete= caratFo!=null && caratFo.equals(sessionFo);
            }
            
            if ( canComplete ) {
                if ( context.completionType==context.COMPLETION_TYPE_STRUCT ) {
                    String response= SessionSupport.getCommandResponse( session, "help, /struct, " + context.contextString );
                    try {
                        IdlStruct struct= IdlStruct.parseHelpStruct( new BufferedReader( new StringReader( response ) ) );
                        if ( struct==null ) {
                            result.add( CompletionSupport.getMessageCompletionItem( "unable to parse response", response ) );
                            return result;
                        }
                        List tags= struct.tags();
                        for ( Iterator i= tags.iterator(); i.hasNext(); ) {
                            StructTag tag= (StructTag) i.next();
                            if ( tag.getName().startsWith( context.completable.toLowerCase() ) ) {
                                StructTagCompletionItem item= new StructTagCompletionItem( context.completable, context.completable.length(), struct, tag.getName() );
                                item.setDocumentationString( response );
                                result.add( item );
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else if ( context.completionType==context.COMPLETION_TYPE_OBJECT) {
                    String response= SessionSupport.getCommandResponse( session, "print, obj_valid( " + context.contextString +")" );
                    
                    if ( !response.equals("1" ) ) {
                        return Collections.singletonList( CompletionSupport.getMessageCompletionItem( "not an object reference: " +context.contextString ) );
                    }
                    
                    response= SessionSupport.getCommandResponse( session, "print, obj_class( " + context.contextString +")" );
                    
                    IdlClass clas= IDLRoutinesDataBase.getInstance().getSession().getClass(response);
                    
                    if ( clas==null ) {
                        return Collections.singletonList( CompletionSupport.getMessageCompletionItem( "no definition found: "+response ) );
                    }
                    
                    System.err.println(clas);
                    
                    List methods= clas.methods();
                    
                    int n= response.length()+2;
                    
                    for ( Iterator i= methods.iterator(); i.hasNext(); ) {
                        Procedure p= (Procedure) i.next();
                        if ( p.getName().substring( n, n+context.completable.length() ).equals( context.completable ) ) {
                            CompletionItem item= new ObjectMethodCompletionItem( context.completable, context.completable.length(), p );
                            
                            result.add( item );
                        }
                        
                    }
                    
                    
                } else if ( context.completionType==context.COMPLETION_TYPE_OBJECT_KW_FUNC ) {
                    String[] objMethod= context.contextString.split("->");
                    String objName= objMethod[0];
                    String method= objMethod[1];
                    
                    String response= SessionSupport.getCommandResponse( session, "print, obj_valid( " + objName +")" );
                    
                    if ( !response.equals("1" ) ) {
                        System.err.println("not a good object ref");
                    }
                    
                    response= SessionSupport.getCommandResponse( session, "print, obj_class( " + objName +")" );
                    
                    IdlClass clas= IDLRoutinesDataBase.getInstance().getSession().getClass(response);
                    
                    System.err.println(clas);
                    
                    Procedure proc= clas.getMethod( method );
                    
                    if ( proc==null ) {
                        result.add( CompletionSupport.getMessageCompletionItem("unable to find method "+method+" of "+objName ) );
                    } else {
                        List keywords= proc.getKeywords();
                        for ( Iterator i= keywords.iterator(); i.hasNext(); ) {
                            String keyword= (String)i.next();  // TODO: keyword object
                            CompletionItem item= new KeywordCompletionItem( context.completable, context.completable.length(), proc, keyword );
                            
                            result.add( item );
                        }
                    }
                    
                }
            } else {
                result.add( CompletionSupport.getMessageCompletionItem( "session is not in this context" ) );
            }
            return result;
        }
    }
    
    /**
     * insert <br>'s and such into string.
     */
    public static String htmlify( String str ) {
        return htmlifyDocumentation(str);
    }
    
    /**
     * this will eventually do the IDL makehtml formatting of documentation.
     */
    public static String htmlifyDocumentation( String str ) {
        StringBuffer buffer= new StringBuffer( Math.min( 10000, 110*str.length()*100 ) );
        String[] ss= str.split("\n");
        for ( int i=0; i<ss.length; i++ ) {
            ss[i]= ss[i].replaceAll(" ","&nbsp;");
            ss[i]= ss[i].replaceAll("<","&lt;");
            ss[i]= ss[i].replaceAll(">","&gt;");
            ss[i]= ss[i].replaceAll(";\\*\\*+","<hr>");
            ss[i]= ss[i].replaceAll(";\\*", "&nbsp;");
            buffer.append(ss[i]).append("<br>");
            
        }
        return buffer.toString();
    }
    
    static List addObjectFields(CompletionContext context) {
        return new ArrayList();
        //TODO: implement me
    }
    
    
}
