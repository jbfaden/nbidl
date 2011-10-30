/*
 * Util.java
 *
 * Created on April 10, 2007, 10:33 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.util;

import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.dataobject.ProceduresFile;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;

/**
 * Useful routines.
 * @author jbf
 */
public class PvwaveUtil {
    
    /**
     * return the line object for the procedure.
     */
    public static Line getLine( Procedure pro ) {
        Line theLine= null;
        ProceduresFile src= pro.getSourceFile();
        if ( src!=null ) {
            FileObject fo= src.getFileObject();
            DataObject dataObject;
            try {
                dataObject = DataObject.find(fo);
                LineCookie cookie= (LineCookie) dataObject.getCookie( LineCookie.class );
                int lineNum= pro.getLineNum();
                theLine= cookie.getLineSet().getCurrent(lineNum);    
            } catch (DataObjectNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return theLine;
    }
    
}
