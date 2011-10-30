/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.cottagesystems.nbidl.codefold;

import java.util.logging.Logger;
import org.netbeans.spi.editor.fold.FoldManager;
import org.netbeans.spi.editor.fold.FoldManagerFactory;

/**
 * Implementation of FoldManagerFactory for HTML code folding.
 * Used to create an instance of @see org.netbeans.spi.editor.fold.FoldManagerFactory
 *
 */

public class PvwaveFoldManagerFactory implements FoldManagerFactory {
    public PvwaveFoldManagerFactory() {
        Logger.getLogger("pvwave").fine("construct");
    }
    
    public FoldManager createFoldManager() {
        return new PvwaveFoldManager();
    }
    
}

