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

package com.cottagesystems.nbidl.debugger;

import com.cottagesystems.nbidl.pathExplorer.Actions;
import com.cottagesystems.nbidl.session.SessionSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;

/**
 * represents a stack element.  Either lineString or line must be non-null.
 *
 * @author  Honza
 */
class StackElement {
    
    private Line        line;
    private File          file;
    private String  lineString;
    
    public static StackElement WAIT= new StackElement(null,null) {
        public String toString() {
            return "wait...";
        }
    };
    
    static StackElement create( String line ) {
        StackElement result= new StackElement(null,null);
        result.lineString= line;
        return result;
    }
    
    StackElement( Line line, File file  ) {
        this.line = line;
        this.file = file;
    }
    
    Line getLine() {
        if ( line==null ) {
            try {
                line= SessionSupport.getLineReference(lineString);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (DataObjectNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return line;
    }
    
    File getFile() {
        return file;
    }
    
    public String toString() {
        if ( lineString!=null ) {
            return lineString;
        } else {
            return line.toString();
        }
    }
    
    private List actions=null;
    
    public List getActions() {
        if (actions==null ) actions= new ArrayList();
        return actions;
    }
}
