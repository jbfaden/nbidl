/*
 * IDLSessionEvent.java
 *
 * Created on March 1, 2007, 10:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.debugger;

/**
 *
 * @author jbf
 */
public class IDLSessionEvent {
    
    public static final int TYPE_STOP=1;
    public static final int TYPE_PROMPT=2;
    public static final int TYPE_BUSY=3;
    public static final int TYPE_CONTINUE=4;
    
    int type;
    
    PvwaveStop stop=null;  
    
    /** Creates a new instance of IDLSessionEvent */
    public IDLSessionEvent( int type ) {
        this.type= type;
        this.stop= null;
    }
    
    public IDLSessionEvent( int type, PvwaveStop stop ) {
        this(type);
        this.stop= stop;
    }
    /**
     * return the stop object indicating where the PC (program counter) is.  null
     * indicates IDL is at the main level.
     */
    public PvwaveStop getPvwaveStop() {
        return this.stop;
    }
    
    public int getType() {
        return this.type;
    }
}
