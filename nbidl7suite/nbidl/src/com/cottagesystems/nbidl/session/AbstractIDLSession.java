/*
 * AbstractIDLSession.java
 *
 * Created on April 4, 2006, 4:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.session;

import com.cottagesystems.nbidl.debugger.IDLSessionEvent;
import java.io.Reader;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;

/**
 *
 * @author Jeremy
 */
public abstract class AbstractIDLSession implements Session {
    
    protected IDLOutputHandler idlOutputHandler;
    protected Reader stdin;
    
    protected int pendingCommandCount= 1;  // We expect the first IDL> prompt without any command issue.
    
    protected static Logger logger= Logger.getLogger("pvwave.session");
    
    private IDLOutputHandler.IDLOutputListener outputListener= new IDLOutputHandler.IDLOutputListener() {
        public void event( IDLSessionEvent event ) {
            if ( event.getType()==event.TYPE_PROMPT ) {
                pendingCommandCount--;
                logger.fine("pendingCommandCount-- = "+pendingCommandCount);
                if ( pendingCommandCount<0 ) {
                    pendingCommandCount=0;
                    logger.fine("  pendingCommandCount reset to 0 ");
                }
            }
        }
    };
    
    public void clearBreakpoint(Session.Breakpoint breakpoint) {
    }
    
    public abstract void close();
    
    public abstract void invokeCommand(String command);
    
    
    /* call for invokeCommandWait support */
    protected void incrementCommandCount() {
        pendingCommandCount++;
        logger.fine("pendingCommandCount++ = "+pendingCommandCount);
    }
    
    public void invokeCommandWait( String command ) {
        invokeCommand(command);
        logger.finest( "pendingCommandCount after invoke="+pendingCommandCount );
        long t0= System.currentTimeMillis();
        while ( pendingCommandCount>0 ) {
            try {
                Thread.sleep(50);
                logger.fine("waiting for IDL commandCount="+pendingCommandCount);
                if ( System.currentTimeMillis()-t0 > 10000 ) {
                    logger.fine("resetting pendingCommandCount commandCount="+pendingCommandCount);
                    pendingCommandCount=0;
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException( ex );
            }
        }
    }
    
    public boolean isBusy() {
        logger.finer("isBusy pendingCommandCount="+pendingCommandCount);
        return pendingCommandCount>0;
        //return false;
    }
    
    public void reload(String file) {
        invokeCommand( ".compile "+file );
    }
    
    /**
     * set a breakpoint at line number lineNum, where 1 is the first line.
     */
    public Session.Breakpoint setBreakpoint(String file, int lineNum) {
        String s= SessionSupport.getCommandResponse( this, "breakpoint, '"+file+"', "+lineNum );
        return null;
    }
    
    public void setOutputHandler(IDLOutputHandler handler) {
        this.idlOutputHandler= handler;
        handler.addIDLOutputListener( outputListener );
    }
    
    public IDLOutputHandler getOutputHandler( ) {
        return this.idlOutputHandler;
    }
    
    public void setStdin(Reader reader) {
        this.stdin= reader;
    }
    
    public abstract void start() ;
    
    public abstract boolean isStarted();
    
    Project project=null;
    
    public Project getProject() {
        return project;
    }
    
    public void setProject( Project p ) {
        this.project= p;
    }

}
