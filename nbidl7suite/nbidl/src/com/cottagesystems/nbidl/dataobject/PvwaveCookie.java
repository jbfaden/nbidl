/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.cottagesystems.nbidl.dataobject;

import java.io.IOException;
import javax.swing.event.ChangeListener;

import org.openide.nodes.Node;
import org.openide.util.Task;

public interface PvwaveCookie extends Node.Cookie {
    /**
     * parse the file into a model
     */
    public Task prepare();
    public ProceduresFile getProceduresFile() throws IOException;
    public ProceduresFileDataObject getProceduresFileDataObject();
    public void setProceduresFile(ProceduresFile pvwave) throws IOException;
    public boolean isValid();
    public void addChangeListener(ChangeListener l);
    public void removeChangeListener(ChangeListener l);

}
