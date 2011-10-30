/*
 * Support.java
 *
 * Created on August 4, 2006, 7:04 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.dataobject;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;

/**
 *
 * @author jbf
 */
public class Support {
    
    /**
     * returns an Action to jump to the procedure, or null.
     */
    public static Action getGotoAction( final Procedure pro ) {
        return new AbstractAction("Goto "+pro.getName() ) {
            public void actionPerformed(ActionEvent e) {
                try {
                    ProceduresFile src= pro.getSourceFile();
                    Line theLine=null;
                    if ( src!=null ) {
                        FileObject fo= src.getFileObject();
                        DataObject dataObject;
                        
                        dataObject = DataObject.find(fo);
                        LineCookie cookie= (LineCookie) dataObject.getCookie( LineCookie.class );
                        int lineNum= pro.getLineNum();
                        theLine= cookie.getLineSet().getCurrent(lineNum);
                    }
                    if ( theLine!=null ) {
                        theLine.show( Line.SHOW_GOTO );
                    }
                } catch ( DataObjectNotFoundException ex ) {
                    
                }
            }
        };
    }
    
    
    /** Creates a new instance of Support */
    private Support() {
    }
    
}
