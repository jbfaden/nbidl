/*
 * IDLOutputHandler.java
 *
 * Created on April 3, 2006, 1:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.session;

import com.cottagesystems.nbidl.debugger.IDLSessionEvent;
import com.cottagesystems.nbidl.debugger.PvwaveStop;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.ErrorManager;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;

/**
 * Scrape the IDL Output for line information.
 *
 * Note: assumes "IDL>" is the prompt.
 *
 * @author Jeremy
 */
public class IDLOutputHandler {
    
    private static Logger logger= Logger.getLogger("pvwave.session" );
    /** Creates a new instance of IDLOutputHandler */
    public IDLOutputHandler( OutputWriter stdout, OutputWriter stderr ) {
        this.stdout= stdout;
        this.stderr= stderr;
        this.listeners= new ArrayList();
    }
    
    OutputWriter stdout;
    OutputWriter stderr;
    
    boolean appearsBusy= true;
    
    // TODO: consider breaking up into delegates: one to listen for links, one to trap output, one to check for prompt
    boolean gatheringResponse= false;
    boolean echoOn= true;
    
    StringBuffer responseBuffer;
    
    private ArrayList<IDLOutputHandler.IDLOutputListener> listeners;
    
    public interface IDLOutputListener {
        public void event( IDLSessionEvent e );
    }
    
    final Pattern fileRefPattern= Pattern.compile(".*? *([A-Z_]+) *([0-9]+)? (.+).pro\\s*");
    final String prompt="IDL>";
    
    
    public void addIDLOutputListener( IDLOutputListener listener ) {
        if ( !this.listeners.contains(listener) ) {
            this.listeners.add( listener );
            if ( listeners.size() > 3 ) {
                throw new IllegalStateException("so many listeners, what's going on here?!?!?");
            }
        }
    }
    
    public void removeIDLOutputListener(  IDLOutputListener listener ) {
        this.listeners.remove(listener);
    }
    
    private void checkStop( String line ) {
        // TODO: this is horribly worded
        final Pattern programCounterPattern1= Pattern.compile("% Stepped to: *([A-Z_:]+) *([0-9]+)? (.+).pro\\s*");
        final Pattern programCounterPattern2= Pattern.compile("% Breakpoint at: *([A-Z_:]+) *([0-9]+)? (.+).pro\\s*");
        final Pattern programCounterPattern3= Pattern.compile("% Stop encountered: *([A-Z_:]+) *([0-9]+)? (.+).pro\\s*");
        final Pattern programCounterPattern4= Pattern.compile("% Execution halted at: *([A-Z_:]+) *([0-9]+)? (.+).pro\\s*");
        final Pattern programCounterPattern5= Pattern.compile("% Return encountered: *([A-Z_:]+) *([0-9]+)? (.+).pro\\s*");
        final Pattern programCounterPattern6= Pattern.compile("% Interrupted at: *([A-Z_:]+) *([0-9]+)? (.+).pro\\s*");
        Matcher matcher=null;
        if ( (matcher=programCounterPattern1.matcher(line)).matches()
        || (matcher=programCounterPattern2.matcher(line)).matches()
        || (matcher=programCounterPattern3.matcher(line)).matches()
        || (matcher=programCounterPattern4.matcher(line)).matches() 
        || (matcher=programCounterPattern5.matcher(line)).matches() 
        || (matcher=programCounterPattern6.matcher(line)).matches() ) {
            String linenum= matcher.group(2); // may be null
            String source= matcher.group(3) + ".pro";
            int ilinenum= Integer.parseInt(linenum);
            fireSessionEvent( new IDLSessionEvent( IDLSessionEvent.TYPE_STOP,  new PvwaveStop( source, ilinenum ) ) );
        }
    }
    
    public void fireSessionEvent( IDLSessionEvent event ) {
        for ( IDLOutputListener l:listeners ) {
            l.event( event );
        }
    }
    
    /**
     * returns a OutputListener for the line (underline that can be clicked on), or null.
     */
    private OutputListener getLineListener( final String lineStr ) {
        if ( lineStr.indexOf(".pro") == -1 ) {
            return null;
        }
        
        final Pattern fileRefPattern2= Pattern.compile(".*? *([A-Z_]+) *([0-9]+)? (.+).pro\\s*");
        final Pattern compileErrorPattern= Pattern.compile("\\s*At: (.+).pro, Line ([0-9]+)\\s*");
        
        Matcher matcher;
        
        if ( !( ( matcher=fileRefPattern2.matcher(lineStr)).matches()
        || ( matcher=compileErrorPattern.matcher(lineStr) ).matches() ) ) {
            return null;
        } else {
            
            OutputListener lineListener=new OutputListener() {
                public void outputLineSelected(OutputEvent outputEvent) {
                }
                
                public void outputLineAction(OutputEvent outputEvent) {
                    Line oline;
                    try {
                        oline = SessionSupport.getLineReference(lineStr);
                        oline.show( Line.SHOW_GOTO );
                    } catch (DataObjectNotFoundException ex) {
                        ErrorManager.getDefault().notify( ex );
                    } catch (FileNotFoundException ex) {
                        ErrorManager.getDefault().notify( ex );
                    } catch (IOException ex) {
                        ErrorManager.getDefault().notify( ex );
                    }
                    
                }
                
                public void outputLineCleared(OutputEvent outputEvent) {
                    
                }
            };
            return lineListener;
        }
    }
    
    /**
     * if the output is anything besides a prompt, then let the
     * listener know it's busy.  This basically allows for PCAnnotation.
     * TODO: This is cheesy.
     * @returns index of the end of the prompt, or 0;
     */
    private int checkPrompt( String line ) {
        int i=0;
        boolean hasPrompt= false;
        int result=0;
        while ( line.indexOf(prompt,i) != -1 ) {
            i= line.indexOf(prompt,i) + prompt.length();
            result=i;
            hasPrompt= true;
            logger.fine("checkPrompt(\""+line+"\") -> promptReceived()" );
            for ( IDLOutputListener l:listeners ) fireSessionEvent( new IDLSessionEvent(IDLSessionEvent.TYPE_PROMPT) );
        }
        // TODO: check to see if there's junk after the prompt->busy
        if ( !hasPrompt ) {
            fireSessionEvent( new IDLSessionEvent( IDLSessionEvent.TYPE_BUSY ) );
            appearsBusy= true;
        }
        return result;
    }
    
    StringBuffer stdoutBuffer= new StringBuffer();
    
    /**
     * position within stdoutBuffer that has been checked for prompt.
     */
    int stdoutCheckPromptPos=0;
    
    public void stdoutReceived( String text ) {
        logger.fine("stdoutReceived("+text+")");
        stdoutBuffer.append(text);
        
        int i;
        while ( (i=stdoutBuffer.indexOf("\n")) != -1 ) {
            String line= stdoutBuffer.substring(0,i+1);
            stdoutBuffer.delete(0,i+1);
            
            OutputListener lineListener;
            if ( gatheringResponse ) {
                responseBuffer.append(line);
                lineListener= null;
            } else {
                if ( line.contains( "% At $MAIN$" ) ) {
                    fireSessionEvent( new IDLSessionEvent( IDLSessionEvent.TYPE_STOP, PvwaveStop.MAIN ) );
                }
                if ( line.contains( "Procedure was compiled while active" ) ) {
                    fireSessionEvent( new IDLSessionEvent( IDLSessionEvent.TYPE_STOP ) );
                }
                lineListener= getLineListener( line );
            }
            
            if ( lineListener!=null ) {
                try {
                    if ( echoOn ) stdout.println(line,lineListener);
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
                checkStop(line);
            } else {
                if ( echoOn ) stdout.write(line);
            }
            
            checkPrompt(line.substring(stdoutCheckPromptPos));
            stdoutCheckPromptPos=0;
            stdout.flush();
        }
        
        stdoutCheckPromptPos= checkPrompt(stdoutBuffer.substring(stdoutCheckPromptPos)); // check for IDL> that was not followed by \n.
        
        if ( stdoutCheckPromptPos>0 ) { // go ahead and flush it
            if ( stdoutBuffer.length() < stdoutCheckPromptPos ) {
                System.err.println("bad index.");
            }
            String partialLine= stdoutBuffer.substring(0,stdoutCheckPromptPos );
            stdoutBuffer.delete(0,stdoutCheckPromptPos );
            if ( echoOn ) stdout.write( partialLine );
            if ( gatheringResponse ) responseBuffer.append(partialLine); // TODO: check this.
            stdoutCheckPromptPos=0;
        }
        if ( stdoutCheckPromptPos<0 ) {
            stdoutCheckPromptPos=0;
        }
        //stdoutCheckPromptPos= 0;
        
        
    }
    
    StringBuffer stderrBuffer= new StringBuffer();
    
    public void stderrReceived( String text ) {
        logger.fine("stderrReceived("+text+")");
        stderrBuffer.append(text);
        int i;
        while ( (i=stderrBuffer.indexOf("\n")) != -1 ) {
            String line= stderrBuffer.substring(0,i+1);
            stderrBuffer.delete(0,i+1);
            
            if ( gatheringResponse ) responseBuffer.append(line);
            
            OutputListener lineListener= getLineListener( line );
            if ( lineListener!=null ) {
                try {
                    if ( echoOn ) stderr.println(line,lineListener);
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            } else {
                if ( echoOn ) stderr.write(line);
                checkPrompt(line); //TODO: check if prompt comes on stderr
            }
            stderr.flush();
        }
        
        // check for prompt at end of buffer.  We know it's not going to
        // be a actionable line so flush it.
        // TODO: note that this isn't sent to the responseBuffer.
        if ( (i=stderrBuffer.indexOf(prompt)) != -1 ) {
            String partialLine= stderrBuffer.substring(0,i+prompt.length() );
            stderrBuffer.delete(0,i+prompt.length() );
            stderr.write( partialLine );
        }
    }
    
    public void startGatheringResponse() {
        responseBuffer= new StringBuffer(400);
        gatheringResponse= true;
        echoOn= false;
    }
    
    public String getResponse() {
        String result= responseBuffer.toString();
        gatheringResponse= false;
        echoOn= true;
        responseBuffer= null;
        return result;
    }
    
    
}
