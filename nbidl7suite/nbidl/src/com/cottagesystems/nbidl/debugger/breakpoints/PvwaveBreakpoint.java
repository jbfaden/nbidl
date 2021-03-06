/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.cottagesystems.nbidl.debugger.breakpoints;

import com.cottagesystems.nbidl.debugger.PvwaveStop;
import com.cottagesystems.nbidl.session.SessionSupport;
import org.netbeans.api.debugger.Breakpoint;
import org.openide.text.Line;



/**
 *
 * @author  Honza
 */
public class PvwaveBreakpoint extends Breakpoint {
    
    private boolean enabled = true;
    private PvwaveStop    line;
    private int index;

    public PvwaveBreakpoint (PvwaveStop line) {
        this.line = line;
    }
    
    public static PvwaveBreakpoint create( Line line ) {
        return new PvwaveBreakpoint(  PvwaveStop.createStop(line) );
    }
    
    public PvwaveStop getLine () {
        return line;
    }
        
    /**
     * Test whether the breakpoint is enabled.
     *
     * @return <code>true</code> if so
     */
    public boolean isEnabled () {
        return enabled;
    }
    
    /**
     * Disables the breakpoint.
     */
    public void disable () {
        if (!enabled) return;
        enabled = false;
        firePropertyChange (PROP_ENABLED, Boolean.TRUE, Boolean.FALSE);
    }
    
    /**
     * Enables the breakpoint.
     */
    public void enable () {
        if (enabled) return;
        enabled = true;
        firePropertyChange (PROP_ENABLED, Boolean.FALSE, Boolean.TRUE);
    }
    
    public int hashCode() {
        return line.hashCode() + ( enabled ? 1 : 0 );
    }
    
    public boolean equals( Object o ) {
        if ( !( o instanceof PvwaveBreakpoint ) ) return false;
        PvwaveBreakpoint pvb= (PvwaveBreakpoint)o;
        return ( line.equals( pvb.getLine() ) && enabled==pvb.isEnabled() );
    }
    
    public String toString() {
        return line.getLineObject().getDisplayName();
    }
    
    public int getIndex() {
        return index;
    } 
    
    public void setIndex( int index ) {
        this.index= index;
    }
}
