/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.cottagesystems.nbidl.codefold;

import java.util.logging.Logger;
import org.netbeans.editor.CodeFoldingSideBar;
import org.netbeans.editor.SideBarFactory;

/**
 *  HTML Code Folding Side Bar Factory, responsible for creating CodeFoldingSideBar
 *  Plugged via layer.xml
 *
 */
public class PvwaveCodeFoldingSideBarFactory implements SideBarFactory{
    
    public PvwaveCodeFoldingSideBarFactory() {
        Logger.getLogger("pvwave").info("construct");
    }
    
    public javax.swing.JComponent createSideBar(javax.swing.text.JTextComponent target) {
        return new CodeFoldingSideBar(target);
    }

}
