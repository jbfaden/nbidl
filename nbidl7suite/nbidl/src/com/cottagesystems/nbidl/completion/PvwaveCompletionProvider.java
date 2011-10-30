/*
 * PvwaveCompletionProvider.java
 *
 * Created on May 6, 2006, 2:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.completion;

import java.util.logging.Logger;
import javax.swing.text.JTextComponent;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionTask;

/**
 *
 * @author jbf
 */
public class PvwaveCompletionProvider implements CompletionProvider {
    final static Logger log= Logger.getLogger("pvwave");
    /** Creates a new instance of PvwaveCompletionProvider */
    public PvwaveCompletionProvider() {
        log.fine("PvwaveCompletionProvider.instance");
    }

    public CompletionTask createTask(int i, JTextComponent jTextComponent) {
        log.fine("PvwaveCompletionProvider.createTask");
        return new PvwaveCompletionTask();
    }

    public int getAutoQueryTypes(JTextComponent jTextComponent, String string) {
        log.fine("PvwaveCompletionProvider.getAutoQueryTypes(null,\""+string+"\"");
        return 0;
    }
    
}
