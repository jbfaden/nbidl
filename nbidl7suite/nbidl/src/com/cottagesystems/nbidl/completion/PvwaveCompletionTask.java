/*
 * PvwaveCompletionTask.java
 *
 * Created on May 6, 2006, 2:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.completion;

import com.cottagesystems.nbidl.completion.CompletionSupport.CompletionContext;
import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.model.IdlClass;
import com.cottagesystems.nbidl.session.SessionSupport;
import com.cottagesystems.nbidl.syntax.IDLRoutinesDataBase;
import com.cottagesystems.nbidl.syntax.PvwaveTokenContext;
import com.cottagesystems.nbidl.syntax.UserRoutinesDataBase;
import com.cottagesystems.nbidl.util.GoToSupport;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.text.JTextComponent;
import org.netbeans.api.project.Project;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Registry;
import org.netbeans.editor.Utilities;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.openide.filesystems.FileObject;

/**
 *
 * @author jbf
 */
public class PvwaveCompletionTask implements CompletionTask {
    
    private static Logger logger= Logger.getLogger("pvwave");
    
    CompletionSupport support;
    
    /** Creates a new instance of PvwaveCompletionTask */
    public PvwaveCompletionTask() {
        support= new CompletionSupport();
    }
    
    private List getKeywordsMatching( CompletionContext c, Procedure p ) {
        List kws=p.getKeywords();
        Collections.sort( kws );
        ArrayList result= new ArrayList();
        String lookFor= c.completable.toLowerCase();
        for ( Iterator i= kws.iterator(); i.hasNext(); ) {
            String kw= (String) i.next();
            if ( kw.startsWith( lookFor ) ) {
                CompletionItem item= new KeywordCompletionItem( c.completable, c.completable.length(), p, kw );
                result.add( item );
            }
        }
        return result;
    }
    
    public void query(CompletionResultSet completionResultSet) {
        logger.fine("completionTask.query");
        JTextComponent editor = Registry.getMostActiveComponent();
        int pos = editor.getCaretPosition();
        
        BaseDocument doc= Utilities.getDocument(editor);
        FileObject fo= GoToSupport.getFileObject(doc);
        
        UserRoutinesDataBase userRoutinesDataBase;
        
        if ( fo==null ) {
            Project p= SessionSupport.getSessionInstance().getProject();
            if (p==null ) {
                userRoutinesDataBase= UserRoutinesDataBase.getInstance();
            } else {
                userRoutinesDataBase= UserRoutinesDataBase.getInstance( p );
            }
        } else {
            userRoutinesDataBase= UserRoutinesDataBase.getInstance( fo );
        }
        
        CompletionSupport.CompletionContext context= support.getCompletionContext( doc, pos );
        
        List matches;
        
        if ( context==null ) {
            completionResultSet.finish();
            return;
        }
        
        matches= new ArrayList();
        
        if ( context!=null && context.contextTok!=null ) {
            if ( context.contextTok.getNumericID()==PvwaveTokenContext.USERFUNC_ID ) {
                if ( context.completionType==context.COMPLETION_TYPE_KEYWORD || context.completionType==context.COMPLETION_TYPE_KW_FUNC ) {
                    Procedure p= userRoutinesDataBase.getProcedure(context.contextString);
                    List m= this.getKeywordsMatching(context, p);
                    if ( m.size()==0 ) {
                        matches.addAll( userRoutinesDataBase.getMatching(context.contextString , false, true)  );
                    } else {
                        matches.addAll( m );
                    }
                } else {
                    matches.addAll( userRoutinesDataBase.getMatching(context.contextString , true, false ) );
                }
            } else if ( context.contextTok.getNumericID()==PvwaveTokenContext.PVFUNC_ID  ) {
                if ( context.completionType==context.COMPLETION_TYPE_KEYWORD || context.completionType==context.COMPLETION_TYPE_KW_FUNC ) {
                    if ( context.contextString.equals("obj_new") &&
                            ( context.completable.startsWith("'") || context.completable.startsWith("\"")  )  ){
                        //TODO: lookup init, do completions based on it.
                        matches.addAll( getObjectTypeCompletions(context) );
                    } else {
                        if ( ( context.completable.startsWith("'") || context.completable.startsWith("@") ) &&
                                ( ( context.completable.length()>1 && context.completable.charAt(1)=='/' ) ||
                                (context.completable.length()>2 && context.completable.charAt(2)==':' ) ) ) {
                            matches.addAll( getFilenameCompletions( context ) );
                        }
                        Procedure p= IDLRoutinesDataBase.getInstance().getProcedure( context.contextString );
                        matches.addAll( this.getKeywordsMatching(context, p) );
                    }
                } else {
                    IDLRoutinesDataBase.getInstance().getMatching( context.contextString , true, true);
                }
            }  else if ( context.contextTok.getNumericID()==PvwaveTokenContext.IDENTIFIER_ID ) {
                if ( context.completionType!=context.COMPLETION_TYPE_OBJECT ) {
                    List list= CompletionSupport.addLiveCompletions( context );
                    matches.addAll( list );
                }
            }
        }
        
        if ( context.completionType==context.COMPLETION_TYPE_OBJECT ) {
            List list= CompletionSupport.addLiveCompletions( context );
            matches.addAll( list );
        }
        
        // offline completions for self.
        if ( context.completionType==context.COMPLETION_TYPE_STRUCT && context.contextString.equalsIgnoreCase("self") ) {
            if ( context.inCommandLine==false ) {
                Procedure p= CompletionSupport.getCarotProcedure();
                String name= p.getName();
                int i;
                if ( (i=name.indexOf("::"))!=-1 ) {
                    String clas= name.substring(0,i);
                    matches.addAll( getClassFieldCompletions( clas, context.completable ) );
                }
            }
        }
        
        if ( context!=null ) {
            if ( context.completionType==context.COMPLETION_TYPE_PRO ) {
                matches.addAll( IDLRoutinesDataBase.getInstance().getMatching( context.completable, true, false ) );
                matches.addAll( userRoutinesDataBase.getMatching( context.completable , true, false ) );
            } else if ( context.completionType==context.COMPLETION_TYPE_FUNC || context.completionType==context.COMPLETION_TYPE_KW_FUNC ) {
                matches.addAll( IDLRoutinesDataBase.getInstance().getMatching( context.completable, false, true ) );
                matches.addAll( userRoutinesDataBase.getMatching( context.completable , false, true ) );
            }
            
            if ( context.completionType==context.COMPLETION_TYPE_KEYWORD && matches.size()==0 ) {
                matches.addAll( IDLRoutinesDataBase.getInstance().getMatching( context.contextString, true, true ) );
                matches.addAll( userRoutinesDataBase.getMatching( context.contextString, true, true ) );
            }
        }
        
        for ( Iterator i=matches.iterator(); i.hasNext() ; ) {
            completionResultSet.addItem( (CompletionItem) i.next() );
        }
        
        completionResultSet.finish();
        
    }
    
    public void refresh(CompletionResultSet completionResultSet) {
        logger.fine("completionTask.refresh");
    }
    
    public void cancel() {
        logger.fine("completionTask.cancel");
    }
    
    private Collection getClassFieldCompletions(String classname, String completable) {
        IdlClass classs= IDLRoutinesDataBase.getInstance().getSession().getClass( classname );
        System.err.println(classs);
        return Collections.emptyList();
    }
    
    private Collection getObjectTypeCompletions(CompletionContext context) {
        String startsWith= context.completable.substring(1);
        
        ArrayList result= new ArrayList();
        
        Map<String,IdlClass> classes= SessionSupport.getIdlSessionInstance().classes();
        System.err.println(context);
        for ( String clas:classes.keySet() ) {
            if ( clas.startsWith(startsWith) ) {
                result.add( new DefaultCompletionItem( startsWith, startsWith.length(), clas, null, null ) );
            }
        }
        return result;
    }
    
    private Collection getFilenameCompletions(CompletionContext context) {
        String startsWith= context.completable;
        int i= startsWith.lastIndexOf('/');
        String firstPart= startsWith.substring(0,1);
        String dir= startsWith.substring(1,i+1);
        final String fnameprefix= startsWith.substring(i+1);
        
        ArrayList result= new ArrayList();
        
        if ( context.inCommandLine ) {
            String cmd= "print, strjoin( file_search( '"+dir+"*', /mark_dir ), '~~~' )";
            String list= SessionSupport.getCommandResponse( SessionSupport.getSessionInstance(), cmd );
            
            if ( list.length() > 0 ) {
                String[] files= list.split("~~~");
                
                for ( int j=0; j<files.length; j++ ) {
                    String ff= files[j].substring(dir.length()).toLowerCase();
                    if ( ff.startsWith(fnameprefix) ) {
                        String complete= firstPart + files[j].trim();  // windows/unix
                        String label= complete.length() > 30 ? "... "+complete.substring(complete.length()-26) : complete;
                        result.add( new DefaultCompletionItem( startsWith, startsWith.length(), complete, label, null ) );
                    }
                }
            }
            
            
        } else {
            File[] files= new File(dir).listFiles( new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith(fnameprefix);
                }
            });
            
            if ( files==null ) return new ArrayList();
            
            for ( int j=0; j<files.length; j++ ) {
                String complete= "'"+files[j].toString() + ( files[j].isDirectory() ? "/" : "" );
                String label= complete.length() > 30 ? "... "+complete.substring(complete.length()-26) : complete;
                result.add( new DefaultCompletionItem( startsWith, startsWith.length(), complete, label, null ) );
            }
            
        }
        
        
        return result;
    }
    
}
