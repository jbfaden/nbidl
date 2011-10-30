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
public class DefaultDocumentationItem implements CompletionDocumentation {
    
    String link;
    
    /** Creates a new instance of UserDocumentationItem */
    public DefaultDocumentationItem( String link ) {
        this.link= link;
    }

    public String getText() {
        return "<i>link not resolvable: "+link+"</i>";
    }

    public URL getURL() {
        return null;
    }

    public CompletionDocumentation resolveLink(String string) {
        return null;
    }

    public Action getGotoSourceAction() {
        return null;
    }
    
}
