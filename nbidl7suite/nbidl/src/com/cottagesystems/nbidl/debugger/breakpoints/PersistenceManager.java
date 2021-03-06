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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerEngine;

import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerListener;
import org.netbeans.api.debugger.LazyDebuggerManagerListener;
import org.netbeans.api.debugger.Properties;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.Watch;

/**
 * Listens on DebuggerManager and:
 * - loads all breakpoints & watches on startup
 * - listens on all changes of breakpoints and watches (like breakoint / watch
 *     added / removed, or some property change) and saves a new values
 *
 * @author Jan Jancura
 */
public class PersistenceManager implements LazyDebuggerManagerListener {
    
    public Breakpoint[] initBreakpoints () {
        Properties p = Properties.getDefault ().getProperties ("debugger").
            getProperties (DebuggerManager.PROP_BREAKPOINTS);
        return (Breakpoint[]) p.getArray (
            "pvwave", 
            new Breakpoint [0]
        );
    }
    
    public void initWatches () {
    }
    
    public String[] getProperties () {
        return new String [] {
            DebuggerManager.PROP_BREAKPOINTS_INIT,
            DebuggerManager.PROP_BREAKPOINTS,
        };
    }
    
    public void breakpointAdded (Breakpoint breakpoint) {
        Properties p = Properties.getDefault ().getProperties ("debugger").
            getProperties (DebuggerManager.PROP_BREAKPOINTS);
        p.setArray (
            "pvwave", 
            getBreakpoints ()
        );
        breakpoint.addPropertyChangeListener(this);
    }

    public void breakpointRemoved (Breakpoint breakpoint) {
        Properties p = Properties.getDefault ().getProperties ("debugger").
            getProperties (DebuggerManager.PROP_BREAKPOINTS);
        p.setArray (
            "pvwave", 
            getBreakpoints ()
        );
        breakpoint.removePropertyChangeListener(this);
    }
    public void watchAdded (Watch watch) {
    }
    
    public void watchRemoved (Watch watch) {
    }
    
    public void propertyChange (PropertyChangeEvent evt) {
        if (evt.getSource() instanceof Breakpoint) {
            Properties.getDefault ().getProperties ("debugger").
                getProperties (DebuggerManager.PROP_BREAKPOINTS).setArray (
                    "pvwave",
                    getBreakpoints ()
                );
        }
    }
    
    public void sessionAdded (Session session) {}
    public void sessionRemoved (Session session) {}
    public void engineAdded (DebuggerEngine engine) {}
    public void engineRemoved (DebuggerEngine engine) {}
    
    
    private static Breakpoint[] getBreakpoints () {
        Breakpoint[] bs = DebuggerManager.getDebuggerManager ().
            getBreakpoints ();
        int i, k = bs.length;
        ArrayList bb = new ArrayList ();
        for (i = 0; i < k; i++)
            // Don't store hidden breakpoints
            if (bs[i] instanceof PvwaveBreakpoint)
                bb.add (bs [i]);
        bs = new Breakpoint [bb.size ()];
        return (Breakpoint[]) bb.toArray (bs);
    }
}

