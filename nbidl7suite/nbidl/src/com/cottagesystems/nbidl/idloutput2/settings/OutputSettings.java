/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.cottagesystems.nbidl.idloutput2.settings;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * @author Milos Kleint
 */

public class OutputSettings extends SystemOption {

    public static final String PROP_WRAP = "wrap"; // NOI18N
    
    private static final long serialVersionUID = -4457782585534382966L;
    
    
    protected void initialize () {
        super.initialize();
        setWrap(false);
    }

    public String displayName () {
        return "idloutput"; //NOI18N - is not shown in UI
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }

    public static OutputSettings getDefault () {
        return (OutputSettings) findObject (OutputSettings.class, true);
    }

    public boolean isWrap() {
        return ((Boolean) getProperty(PROP_WRAP)).booleanValue();
    }

    public void setWrap(boolean wrap) {
        putProperty(PROP_WRAP, Boolean.valueOf(wrap), true);
    }
}
