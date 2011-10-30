/*
 * ManifestEditorKit.java
 *
 * Created on October 20, 2005, 5:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.syntax;

import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Syntax;
import org.netbeans.modules.editor.NbEditorKit;
import org.openide.ErrorManager;

/**
 *
 * @author Administrator
 */
public class PvwaveEditorKit extends NbEditorKit {
    
    private static final ErrorManager LOGGER = ErrorManager.getDefault().getInstance("com.cottagesystems.nbidl.PvwaveEditorKit");
    private static final boolean LOG = LOGGER.isLoggable(ErrorManager.INFORMATIONAL);
    
    public static final String MIME_TYPE = "text/x-application-pvwave"; // NOI18N
    
    /** 
     * Creates a new instance of ManifestEditorKit 
     */
    public PvwaveEditorKit() { 
    }
    
    /**
     * Create a syntax object suitable for highlighting file syntax
     */
    public Syntax createSyntax(Document doc) {
        if (LOG) {
            LOGGER.log(ErrorManager.INFORMATIONAL, "createSyntax"); // NOI18N
        }
        return new PvwaveSyntax();
    }
    
    
    /**
     * Retrieves the content type for this editor kit
     */
    public String getContentType() {
        return MIME_TYPE;
    }

    
}