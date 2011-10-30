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

package com.cottagesystems.nbidl.idloutput2;

import java.io.IOException;
import javax.swing.Action;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * Supplies Output Window implementation through Lookup.
 * @author Jesse Glick, Tim Boudreau
 */
public final class PvwaveIOProvider extends IOProvider {
    private static final PairMap namesToIos = new PairMap();
    
    private static final String STDOUT = NbBundle.getMessage(PvwaveIOProvider.class,
        "LBL_STDOUT"); //NOI18N
    
    public OutputWriter getStdOut() {
        if (PvwaveController.log) {
            PvwaveController.log("NbIOProvider.getStdOut");
        }
        InputOutput stdout = getIO (STDOUT, false, null);
        NbWriter out = ((PvwaveIO)stdout).writer();
        
        PvwaveController.ensureViewInDefault ((PvwaveIO) stdout, true);
        //ensure it is not closed
        if (out != null && out.isClosed()) {
            try {
                out.reset();
                out = (NbWriter) stdout.getOut();
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
                stdout = getIO (STDOUT, true, null);
                out = (NbWriter) stdout.getOut();
            }
        } else {
            out = (NbWriter) stdout.getOut();
        }
        return out;
    }
    
    
    public PvwaveIO getIO(String name, boolean newIO) {
        return getIO (name, newIO, new Action[0]);
    }
    
    public PvwaveIO getIO(String name, Action[] toolbarActions) {
        return getIO (name, true, toolbarActions);
    }
    
    static PvwaveIOProvider instance;
    public static PvwaveIOProvider getDefault() {
        if ( instance==null ) {
            instance= new PvwaveIOProvider();
        }
        return instance;
    }
    
    public PvwaveIO getPvwaveIO(String name, boolean newIO) {
        return getIO (name, newIO, new Action[0]);
    }
    
    public PvwaveIO getPvwaveIO(String name, Action[] toolbarActions) {
        return getIO (name, true, toolbarActions);
    }
    
    private PvwaveIO getIO(String name, boolean newIO, Action[] toolbarActions) {
        if (PvwaveController.log) {
            PvwaveController.log("GETIO: " + name + " new:" + newIO);
        }
        PvwaveIO result = namesToIos.get(name);

        if (result == null || newIO) {
            result = new PvwaveIO(name, toolbarActions);
            namesToIos.add (name, result);
            PvwaveController.ensureViewInDefault (result, newIO);
        } else {
            // mkleint ignore actions if reuse of tabs.
//            result.setToolbarActions(toolbarActions);
        }
        return result;
    }
    
    
    static void dispose (PvwaveIO io) {
        namesToIos.remove (io);
    }
    
    /**
     * Called when the output window is hidden.  Switches the caching of IOs
     * to weak references, so that only those still alive will be shown the 
     * next time the output window is opened.
     */
    static void setWeak(boolean value) {
        namesToIos.setWeak(value);
    }
}

