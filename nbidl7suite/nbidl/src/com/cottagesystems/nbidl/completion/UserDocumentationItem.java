/*
 * UserDocumentationItem.java
 *
 * Created on August 3, 2006, 10:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.completion;

import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.dataobject.Support;
import java.net.URL;
import javax.swing.Action;
import org.netbeans.spi.editor.completion.CompletionDocumentation;

/**
 *
 * @author jbf
 */
public class UserDocumentationItem implements CompletionDocumentation {
    
    Procedure procedure;
    
    /** Creates a new instance of UserDocumentationItem */
    public UserDocumentationItem( Procedure procedure ) {
        this.procedure= procedure;
    }

    public String getText() {
        String doc= procedure.getDocumentation();
        if ( doc==null ) {
            return "<i>documentation not available</i>";
        } else {
            return procedure.getSignature() + "<br><br>" + doc;
        }
    }

    public URL getURL() {
        return null;
    }

    public CompletionDocumentation resolveLink(String string) {
        return null;
    }

    public Action getGotoSourceAction() {
        return Support.getGotoAction( this.procedure );
    }
    
}
