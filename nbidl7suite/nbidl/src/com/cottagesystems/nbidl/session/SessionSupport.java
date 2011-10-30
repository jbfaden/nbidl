/*
 * SessionSupport.java
 *
 * Created on April 3, 2006, 4:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.session;

import com.cottagesystems.nbidl.dataobject.PvwaveSupport;
import com.cottagesystems.nbidl.debugger.PvwaveDebugger;
import com.cottagesystems.nbidl.debugger.PvwaveStop;
import com.cottagesystems.nbidl.debugger.breakpoints.PvwaveBreakpoint;
import com.cottagesystems.nbidl.model.IdlSession;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerInfo;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.spi.debugger.SessionProvider;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;

/**
 * Place to put all the junk having to do with Sessions that doesn't have a
 * better place, and also contain the default session type.
 *
 * @author Jeremy
 */
public class SessionSupport {
    
    final static Logger logger= Logger.getLogger("pvwave.session");
    
    static boolean useSSH= true;
    
    private static Session instance=null;
    
    private static IdlSession idlSession;
    
    public static void waitUntilIdle( Session session ) {
        long t0= System.currentTimeMillis();
        boolean debug=false;
        AbstractIDLSession pvsession= (AbstractIDLSession)session;
        if ( ! pvsession.isStarted() ) throw new IllegalStateException( "Session is not started" );
        if ( pvsession.isBusy() && debug ) instance.getOutputHandler().stdoutReceived("(waiting for idl prompt)");
        while ( pvsession.isBusy() ) {
            logger.fine("waiting for idle");
            try {
                Thread.sleep(30);
                if ( System.currentTimeMillis()-t0 > 1000 ) break;
            } catch (InterruptedException ex) {
                throw new RuntimeException( ex );
            }
        }
        logger.fine("waitUntilIdle="+(System.currentTimeMillis()-t0));
        
        if ( debug ) instance.getOutputHandler().stdoutReceived("okay\n");
    }
    
    /**
     * feeds the source in URL into the session.  Note the
     * contents can only have one routine.
     *
     * URL myRoutinesSrc= this.getClass().getResource( "/com/cottagesystems/nbidl/debugger/resources/nbidl_varprt.pro" );
     * SessionSupport.compileSource( session, myRoutinesSrc );
     */
    public static void compileSource( Session session, URL source ) throws IOException {
        
        waitUntilIdle(session);
        BufferedReader reader= new BufferedReader( new InputStreamReader( source.openStream() ) );
        
        IDLOutputHandler handler= session.getOutputHandler();
        handler.startGatheringResponse();
        
        session.invokeCommand( ".run" );
        
        String s;
        while ( (s=reader.readLine() )!=null ) {
            session.invokeCommand( s );
        }
        
        String result= handler.getResponse();
    }
    
    
    public static synchronized String getCommandResponse( Session session, String command ) {
        IDLOutputHandler handler= session.getOutputHandler();
        
        waitUntilIdle(session);
        
        handler.startGatheringResponse();
        session.invokeCommandWait(command);
        
        String result= handler.getResponse();
        
        int idx;
        idx= result.lastIndexOf("IDL>");
        if ( idx!=-1 ) result= result.substring(0,idx);
        //result= result.trim(); // allow for "" to be the result.
        idx= result.indexOf("\n");
        if ( idx!=-1 ) result= result.substring(idx).trim();
        
        return result;
    }
    
    public static synchronized Session getSessionInstance() {
        if ( instance==null ) {
            logger.fine("creating IDL Server Client instance");
            //instance= new IdlServerClient();
            //instance= new IDLSSHSession();
            instance= new IDLMindtermSSHSession();
        } else {
            logger.fine("using exising instance");
        }
        return instance;
    }
    
    
    /**
     * returns a model of an IDL session based on the context IDL path.
     * This is independent of an actual running session.  Note also that
     * there may be multiple Session objects, which are wrappers for
     * real interactive IDL sessions that can be sent commands. However,
     * there is just one IdlSession object, which is based on source code
     * and modelling IDL's behavior.
     */
    public static synchronized IdlSession getIdlSessionInstance() {
        if ( idlSession==null ) {
            idlSession= new IdlSession();
        }
        return idlSession;
    }
    
    /*
     * okay, this is a little weird because we might map the name from the
     * remote server to the local filename.
     */
    public static FileObject getFileObjectForFilename( String filename ) {
        FileObject fo= FileUtil.toFileObject( new File(filename) ); // filename must be normalized.
        return fo;
    }
    
    public static String getFilenameForFileObject( FileObject fo ) {
        String name= FileUtil.toFile(fo).getPath();
        return name;
    }
    
    
    /**
     * get a source, line from a string, that is not dependent on NB objects.
     *
     * returns a PvwaveStop object or null for the given string.  Here are some example strings:
     * Breakpoint at: ACTION_DRAW       414 J:\ct\papco\working\papco\papco\papco_actions.pro
     * At ACTION_DRAW       414 J:\ct\papco\working\papco\papco\papco_actions.pro
     *    PAPCO_MAIN_EVENT  415 J:\ct\papco\working\papco\papco\papco_event.pro
     *
     *% Syntax error.
     * At: /net/spot/home/jbf/ct/papco/working/papco/papco_lib/dataset/papco_ds_util.pro, Line 42
     * % 1 Compilation error(s) in module PAPCO_EXTRACT_STRUCT.
     */
    public static PvwaveStop getStopReference( String line , boolean doLookup) throws FileNotFoundException, DataObjectNotFoundException, IOException {
        final Pattern fileRefPattern2= Pattern.compile(".*? *([A-Z_0-9]+) +([0-9]+)? (.+).pro\\s*");
        final Pattern compileErrorPattern= Pattern.compile("\\s*At: (.+).pro, Line ([0-9]+)\\s*");
        final Pattern atMainPattern= Pattern.compile("\\s*At $MAIN$\\s*");
        
        String source;
        String linenum;
        String routine;
        
        Matcher matcher;
        if ( !(matcher=fileRefPattern2.matcher(line)).matches() ) {
            if ( (matcher=compileErrorPattern.matcher(line)).matches() ) {
                routine= null;
                linenum= matcher.group(2); // may be null
                source= matcher.group(1) + ".pro";
            } else {
                if ( atMainPattern.matcher(line).matches() ) {
                    return PvwaveStop.MAIN;
                } else {
                    return null;
                }
            }
        } else {
            routine= matcher.group(1);
            linenum= matcher.group(2); // may be null
            source= matcher.group(3) + ".pro";
        }
        
        int ilinenum=0;
        
        if ( linenum==null ) {
            if ( doLookup ) {
                FileObject fo= SessionSupport.getSessionInstance().getFileObjectForFilename( source );
                if ( fo==null ) {
                    throw new FileNotFoundException(source);
                }
                
                DataObject dataObject = DataObject.find(fo);
                
                PvwaveSupport pvwaveSupport= (PvwaveSupport) dataObject.getCookie( PvwaveSupport.class );
                ilinenum= pvwaveSupport.getProceduresFile().getProcedure(routine).getLineNum();
            } else {
                ilinenum= 0;
            }
            
        } else {
            
            ilinenum= Integer.parseInt(linenum);
            
        }
        
        if ( ilinenum==0 ) {
            return null;
        } else {
            PvwaveStop result= new PvwaveStop( source, ilinenum );
            return result;
        }
    }
    
    public static Line getLineReference( String line ) throws FileNotFoundException, DataObjectNotFoundException, IOException {
        PvwaveStop stop= getStopReference( line , true);
        if ( stop==null ) {
            return null;
        } else {
            return getLineObject( stop.getSource(), stop.getLine() );
        }
        
    }
    
    /**
     * Calculates the Line object for the given source and line number within
     * the session context.
     * Line numbering starts at 1.
     * @throws IllegalArgumentException if the line does not exist.
     * @throws IndexOutOfBoundsException
     */
    public static Line getLineObject( String source, int lineNum ) throws IllegalArgumentException, IndexOutOfBoundsException {
        FileObject fo= instance.getFileObjectForFilename( source );
        if ( fo==null ) {
            throw new IllegalArgumentException( "File does not exist: "+source );
        }
        DataObject dataObject;
        try {
            dataObject = DataObject.find(fo);
            EditorCookie editorCookie= (EditorCookie)dataObject.getCookie( EditorCookie.class );
            editorCookie.open();
            JEditorPane[] panes= editorCookie.getOpenedPanes();
            Line line= editorCookie.getLineSet().getCurrent(lineNum-1);
            return line;
        } catch (DataObjectNotFoundException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch ( IllegalArgumentException ex ) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }
    
    public static void setBreakpoint( Session session, PvwaveBreakpoint breakpoint ) {
        FileObject fo= (FileObject) breakpoint.getLine().getLineObject().getLookup().lookup(FileObject.class);
        String source= session.getFilenameForFileObject( fo );
        int lineNum= breakpoint.getLine().getLine();
        logger.fine("setBreakpoint("+source+":"+lineNum+")");
        session.setBreakpoint( source, lineNum );
    }
    
    public static Action getStopAction() {
        return new AbstractAction("STOP") {
            public void actionPerformed( ActionEvent e ) {
                instance.invokeCommand( new String( new byte[] { (byte)3 } ) );
            }
        };
    }
    
    public static PvwaveDebugger startDebugging( ) {
        getSessionInstance();
        
        DebuggerInfo di = DebuggerInfo.create( "PvwaveDebuggerInfo",
                new Object[] {
            new SessionProvider() {
                public String getSessionName() {
                    return "PvwaveDebugSession";
                }
                
                public String getLocationName() {
                    return "localhost";
                }
                
                public String getTypeID() {
                    return "PvwaveSession"; // PvwaveSession  // "AntSession" gets 1 instance.
                }
                
                public Object[] getServices() {
                    return new Object[] {};
                }
            },
            new Object[0],
        } );
        
        DebuggerEngine[] es = DebuggerManager.getDebuggerManager().
                startDebugging(di);
        if ( es.length== 0 ) {
            throw new RuntimeException("no debugger engines found");
        }
        return (PvwaveDebugger) es [0].lookupFirst(null, PvwaveDebugger.class);
        
    }
}
