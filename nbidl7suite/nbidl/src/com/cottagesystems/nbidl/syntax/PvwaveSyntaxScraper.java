/*
 * PvwaveSyntaxSupport.java
 *
 * Created on March 21, 2006, 8:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.syntax;

import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.dataobject.ProceduresFile;
import com.cottagesystems.nbidl.model.IdlStruct;
import com.cottagesystems.nbidl.model.IdlValue;
import com.cottagesystems.nbidl.model.StructTag;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jeremy
 */
public class PvwaveSyntaxScraper {
    private static class Mark{
        int offset, len;
        Mark( Syntax syntax ) {
            this.offset= syntax.getTokenOffset();
            this.len= syntax.getTokenLength();
        }
    }
    
    private static TokenID eatWhiteSpace( PvwaveSyntax syntax ) {
        TokenID tok= syntax.nextToken();
        //String tt= new String( syntax.getBuffer(), syntax.getTokenOffset(), syntax.getTokenLength() );
        //System.err.println(syntax.hashCode()+" "+syntax.getLineOffset()+": "+tt);
        while ( tok!=null && tok.getNumericID()==PvwaveTokenContext.WHITESPACE_ID ) {
            tok=syntax.nextToken();
            //tt= new String( syntax.getBuffer(), syntax.getTokenOffset(), syntax.getTokenLength() );
            //System.err.println(syntax.hashCode()+" "+syntax.getLineOffset()+": "+tt);
        }
        return tok;
    }
    
    /**
     * This consumes all the tokens of the procedure signature.
     * Last token consumed by Syntax was procedure name.
     */
    private static TokenID eatSignature( PvwaveSyntax syntax, Procedure procedure ) {
        StringBuffer sig= new StringBuffer( 100 );
        sig.append( procedure.isFunction() ? "function " : "pro " );
        sig.append( procedure.getName() );
        
        boolean firstComma= false; // insert extra comma after name, it gets lost.
        
        TokenID tok=null;
        TokenID lastTok= null;
        String lastTokString= null;
        TokenID lastLastTok= null;
        
        boolean notDone=true;
        boolean cont=false;
        while( notDone ) {
            tok= syntax.nextToken();
            if ( tok==null ) break;
            switch (tok.getNumericID() ) {
                case ( PvwaveTokenContext.DOLLAR_ID ): cont=true; break;
                case ( PvwaveTokenContext.ENDOFLINE_ID ):
                    if ( !cont ) {
                        notDone= false;
                        if ( lastTokString!=null ) {
                            if ( lastLastTok!=null ) {
                                if ( lastLastTok.getNumericID()!=PvwaveTokenContext.EQ_ID ) {
                                    procedure.addParameter(lastTokString);
                                }
                            }
                        }
                    } else {
                        cont= false;
                    }
                    break;
                case ( PvwaveTokenContext.EQ_ID ):
                    if ( lastTokString==null ) {
                        //TODO: look into this
                        System.err.println("syntax/semantic error");
                    } else {
                        String kw= lastTokString.toLowerCase();
                        procedure.addKeyword(kw);
                    }
                    break;
                case ( PvwaveTokenContext.COMMA_ID ):
                    if ( lastLastTok==null || ( lastLastTok.getNumericID()!=PvwaveTokenContext.EQ_ID ) ) {
                        procedure.addParameter(lastTokString);
                    }
                    break;
            }
            if ( tok.getNumericID()!=PvwaveTokenContext.WHITESPACE_ID ) {
                if ( firstComma==false ) {
                    sig.append(",");
                    firstComma= true;
                }
            }
            if ( notDone ) sig.append( syntax.getBuffer(), syntax.getTokenOffset(), syntax.getTokenLength() );
            if ( tok.getNumericID()!=PvwaveTokenContext.WHITESPACE_ID ) {
                lastLastTok= lastTok;
                lastTok= tok;
                lastTokString= new String( syntax.getBuffer(), syntax.getTokenOffset(), syntax.getTokenLength() );
            }
        }
        
        if ( lastLastTok!=null ) {
            if ( lastLastTok.getNumericID()!=PvwaveTokenContext.EQ_ID ) {
                procedure.addParameter(lastTokString);
            }
        }
        
        procedure.setSignature( sig.toString() );
        return tok;
    }
    
    private static final String STATE_STRUCT_ID="struct_id";
    private static final String STATE_STRUCT_TAG_ID="struct_tag_id";
    private static final String STATE_STRUCT_TAG_VALUE="struct_tag_value";
    
    /**
     * Parses the code defining an IDL value.  This can be a constant, a function call, etc.
     */
    private static IdlValue parseExpression( char [] buffer, Syntax syntax ) {
        return null;
    }
    
    /**
     * Parses the code defining an IDL structure.  The syntax object
     * should have just parsed the '{', and the next token should be a
     * structure name.  If this is not the case (for example the structure
     * is anonymous), or the code is not valid, then null is returned and
     * the syntax will be returned at the parse failure point.)
     *
     */
    private static IdlStruct parseStruct( char[] buffer, Syntax syntax ) {
        TokenID tok;
        String state=STATE_STRUCT_ID;
        
        Logger logger= Logger.getLogger("pvwave.syntax");
        
        String structID=null;
        
        // last ID parsed.
        String lastID= null;
        
        IdlStruct result= new IdlStruct();
        
        while ((tok=syntax.nextToken()) != null ) {
            int itok= tok.getNumericID();
            if ( itok==PvwaveTokenContext.WHITESPACE_ID ) continue;
            if ( itok==PvwaveTokenContext.IDENTIFIER_ID ) {
                lastID=  new String( buffer, syntax.getTokenOffset(), syntax.getTokenLength() );
                if ( state==STATE_STRUCT_ID ) {
                    tok=syntax.nextToken();
                    itok= tok.getNumericID();
                    if ( itok==PvwaveTokenContext.COMMA_ID ) {
                        result.setId(lastID);
                    } else if ( itok==PvwaveTokenContext.COLON_ID ) {
                        result.addTag( new StructTag( lastID ) );
                        state= STATE_STRUCT_TAG_VALUE;
                    }
                }
            } else {
            }
        }
        return null;
    }
    
    private static TokenID parseStruct( PvwaveSyntax syntax, IdlStruct struct ) {
        
        TokenID tok;
        boolean continueLine=false;
        
        while ( (tok=syntax.nextToken()) != null ) {
            int itok= tok.getNumericID();
            
            if ( itok==PvwaveTokenContext.RBRACE_ID ) {
                return tok;
            } else if ( itok==PvwaveTokenContext.DOLLAR_ID ) {
                continueLine= true;
            } else if ( itok==PvwaveTokenContext.ENDOFLINE_ID ) {
                if ( continueLine ) {
                    continueLine= false;
                } else {
                    return tok;
                }
            }
        }
        return tok;
    }
    
    /**
     * we need an object for parsing, instead of a bunch of static methods
     */
    private static TokenID parsePro( PvwaveSyntax syntax, Procedure pro ) {
        return null;
    }
    
    public static ProceduresFile parse( FileObject file ) throws FileNotFoundException, IOException {
        return parse( file.getInputStream(), (int)file.getSize() );
    }
    
    public static ProceduresFile parse( InputStream in, int len ) throws IOException {
        Reader reader= new InputStreamReader( in );
        PvwaveSyntax syntax= new PvwaveSyntax();
        
        Mark docMark=null;
        
        char[] buffer= new char[len];
        reader.read(buffer,0,len);
        reader.close();
        syntax.relocate(buffer,0,len,true,len);
        syntax.setLineOffset(0);
        
        ProceduresFile result= new ProceduresFile();
        TokenID tok;
        
        Procedure procedure=null;
        
        while ((tok=syntax.nextToken()) != null ) {
            int itok= tok.getNumericID();
            String tt= new String( buffer, syntax.getTokenOffset(), syntax.getTokenLength() );
            //System.err.println(syntax.getLineOffset()+": "+tt);
            if ( itok==PvwaveTokenContext.BLOCK_COMMENT_ID ) {
                docMark= new Mark( syntax );
                
            } else if ( itok==PvwaveTokenContext.LBRACE_ID ) {
                IdlStruct struct= new IdlStruct();
                tok= parseStruct( syntax, struct );
                System.err.println(struct);
                
            } else if ( itok==PvwaveTokenContext.PRO_ID ) {
                
                // push the implicitly completed procedure into the result.
                if ( procedure!=null ) result.addProcedure(procedure);
                
                tok= eatWhiteSpace(syntax); // assumes no newlines
                String name= new String( buffer, syntax.getTokenOffset(), syntax.getTokenLength() );
                TokenID tok2= syntax.nextToken(); // check for ::
                String tt2= new String( syntax.getBuffer(), syntax.getTokenOffset(), syntax.getTokenLength() );
                if ( tok2== PvwaveTokenContext.COLON_COLON ) {
                    TokenID tok3= syntax.nextToken(); // check for ::
                    String tt3= new String( syntax.getBuffer(), syntax.getTokenOffset(), syntax.getTokenLength() );
                    name+= "::"+tt3;
                }
                procedure= new Procedure( name, result, syntax.getLineOffset(), syntax.getTokenOffset(), -1, false);
                if ( docMark!=null ) {
                    procedure.markDocumentation( docMark.offset, docMark.len );
                    docMark=null;
                }
                tok= eatSignature( syntax, procedure );
                
            } else if ( itok==PvwaveTokenContext.FUNCTION_ID ) {
                if ( procedure!=null ) result.addProcedure(procedure);
                tok= eatWhiteSpace(syntax);
                String name= new String( buffer, syntax.getTokenOffset(), syntax.getTokenLength() );
                TokenID tok2= syntax.nextToken(); // check for ::
                String tt2= new String( syntax.getBuffer(), syntax.getTokenOffset(), syntax.getTokenLength() );
                if ( tok2== PvwaveTokenContext.COLON_COLON ) {
                    TokenID tok3= syntax.nextToken(); // check for ::
                    String tt3= new String( syntax.getBuffer(), syntax.getTokenOffset(), syntax.getTokenLength() );
                    name+= "::"+tt3;
                }
                procedure= new Procedure(name,result,syntax.getLineOffset(),syntax.getOffset(),-1,true);
                if ( docMark!=null ) {
                    procedure.markDocumentation( docMark.offset, docMark.len );
                    docMark=null;
                }
                tok= eatSignature( syntax, procedure );
                
            } if ( itok==PvwaveTokenContext.USERFUNC_ID ) {
                String name= new String( buffer, syntax.getTokenOffset(), syntax.getTokenLength() );
                if ( procedure!=null ) procedure.addUsage(name,syntax.getLineOffset()); // TODO where nulls come from?
                
            } if ( itok==PvwaveTokenContext.COMMON_ID ) {
                tok= eatWhiteSpace(syntax);
                String name= new String( buffer, syntax.getTokenOffset(), syntax.getTokenLength() );
                if ( procedure!=null ) procedure.addUsage( "COMMON "+name, syntax.getLineOffset()) ;
            }
        }
        if ( procedure!=null ) {
            result.addProcedure(procedure);
        }
        return result;
    }
    
    public static ProceduresFile parse( Reader reader ) throws IOException {
        String s;
        boolean inDoc= false;
        int docStart=0, docEnd;
        BufferedReader breader= new BufferedReader( reader );
        
        Pattern proPattern= Pattern.compile( "(?i)\\s*(pro)\\s+(.+?)(,\\s*(.*?))*\\s*" );
        Pattern funcPattern= Pattern.compile( "(?i)\\s*(function)\\s+(.+?)(,\\s*(.*?))*\\s*" );
        Matcher matcher;
        ArrayList procedures= new ArrayList();
        
        ProceduresFile result= new ProceduresFile();
        int fileOffset= 0;
        int lineNum= 0;
        while ( ( s= breader.readLine() ) != null ) {
            lineNum++;
            if ( s.indexOf(';')==0 && !inDoc ) {
                docStart= fileOffset;
                inDoc= true;
            }
            Procedure p= null;
            if ( (matcher=proPattern.matcher(s)).matches() ) {
                p= new Procedure( matcher.group(2), result, lineNum, fileOffset, 0, false );
                result.addProcedure( p );
                p.setSignature( s );
            }
            if ( (matcher=funcPattern.matcher(s)).matches() ) {
                p= new Procedure( matcher.group(2), result, lineNum, fileOffset, 0, true );
                result.addProcedure( p );
                p.setSignature( s );
            }
            if ( p!=null && inDoc ) {
                docEnd= fileOffset;
                p.markDocumentation( docStart, docEnd-docStart );
            }
            if ( s.indexOf(';')!=0 ) inDoc=false;
            fileOffset+= s.length() + 1;
        }
        
        return result;
    }
    
}
