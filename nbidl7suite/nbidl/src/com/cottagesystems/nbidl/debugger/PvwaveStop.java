/*
 * PvwaveStop.java
 *
 * Created on April 23, 2006, 7:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.debugger;

import com.cottagesystems.nbidl.session.SessionSupport;
import java.awt.EventQueue;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.Line;

/**
 * Represents a session stop that was detected, containing the source and line 
 * number of the stop, and maybe stack trace info.
 *
 * @author Jeremy
 */
public class PvwaveStop {
    
    /**
     * the name of the source code in the session's namespace.  e.g. if the idl session is on a linux box and the
     * debugger is on Windows, this is the linux name, not the Windows name.
     * 
     */
    String source;
    int line;
    Line lineObject;
    
    public static PvwaveStop MAIN= new PvwaveStop( "MAIN", 0 );
    
    /** Creates a new instance of PvwaveStop */
    public PvwaveStop( String source, int line ) {
        this.source= source;
        this.line= line;
    }
    
    public static PvwaveStop createStop( Line line ) {
        FileObject fo= (FileObject) line.getLookup().lookup(FileObject.class);
        String source= SessionSupport.getSessionInstance().getFilenameForFileObject( fo );
        int lineNum=line.getLineNumber() + 1;
        PvwaveStop result= new PvwaveStop(source,lineNum);
        result.lineObject= line;
        return result;
    }
    
    public String getSource() { return this.source; }
    
    /**
     * returns the current line number within the source.  1 is the first line.
     */
    public int getLine() { return this.line; }
    
    /**
     * this must be called from the event thread
     */
    public Line getLineObject() {
        if ( this.lineObject!=null ) return this.lineObject;
        if ( !EventQueue.isDispatchThread() ) throw new IllegalStateException("must be called from the EventQueue");
        Line line= SessionSupport.getLineObject( source, this.line );
        return line;
    }
    
    public boolean equals( Object o ) {
        if ( ! ( o instanceof PvwaveStop) ) return false;
        PvwaveStop c= (PvwaveStop)o;
        return ( c.getLine()==this.getLine() && c.getSource().equals(this.getSource()) );
    }
    
    public String toString() {
        return source+":"+line;
    }
    
}
