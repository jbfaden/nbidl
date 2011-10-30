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

package com.cottagesystems.nbidl.debugger;

import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.spi.debugger.DebuggerEngineProvider;


public class PvwaveDebuggerEngineProvider extends DebuggerEngineProvider {

    private DebuggerEngine.Destructor desctuctor;
    
    
    public String[] getLanguages () {
        return new String[] {"pvwave"};
    }

    public String getEngineTypeID () {
        return "PvwaveDebuggerEngine";
    }

    public Object[] getServices () {
        return new Object[] {};
    }
    
    public void setDestructor (DebuggerEngine.Destructor desctuctor) {
        this.desctuctor = desctuctor;
    }
    
    public DebuggerEngine.Destructor getDestructor () {
        return desctuctor;
    }
}


