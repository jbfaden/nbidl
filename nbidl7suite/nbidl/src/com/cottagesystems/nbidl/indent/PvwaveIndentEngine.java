/*
 * PvwaveIndentEngine.java
 *
 * Created on April 14, 2006, 1:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.indent;

import com.cottagesystems.nbidl.syntax.PvwaveEditorKit;
import org.netbeans.editor.ext.ExtFormatter;
import org.netbeans.modules.editor.FormatterIndentEngine;

/**
 *
 * @author Jeremy
 */
public class PvwaveIndentEngine extends FormatterIndentEngine {
    public PvwaveIndentEngine() {
         System.err.println("construct indent engine");
    }
    
    protected ExtFormatter createFormatter() {
        return new IDLFormatter( PvwaveEditorKit.class);
    }
}
