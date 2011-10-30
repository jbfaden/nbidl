/*
 * PvwaveCompletionItem.java
 *
 * Created on May 6, 2006, 2:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.completion;

import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.syntax.IDLRoutinesDataBase;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.editor.Utilities;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;

/**
 *
 * @author jbf
 */
public class PvwaveCompletionItem implements CompletionItem {
    
    String text;
    int offset;
    Procedure procedure;
    
    /** Creates a new instance of PvwaveCompletionItem */
    public PvwaveCompletionItem( String text, int offset ) {
        this.text= text;
        this.offset= offset;
        procedure= IDLRoutinesDataBase.getInstance().getProcedure(text);
    }
    
    public void defaultAction(JTextComponent jTextComponent) {
        try {
            int pos= jTextComponent.getCaretPosition();
            int pos1= Utilities.getWordEnd(jTextComponent,pos);
            Document d= jTextComponent.getDocument();
          //  d.remove(pos,pos1-pos);
            jTextComponent.getDocument().insertString( pos, text.substring(offset), null );
        } catch ( BadLocationException ex ) {
            throw new RuntimeException(ex);
        }
        Completion.get().hideCompletion();
    }
    
    public void processKeyEvent(KeyEvent keyEvent) {
        //System.err.println("here");
    }
    
    public int getPreferredWidth(Graphics graphics, Font font) {
        return 210;
    }
    
    public void render(Graphics graphics, Font font, Color color, Color color0, int i, int i0, boolean b) {
        CompletionUtilities.renderHtml(null,text,null,graphics,font, color,i,i0,b);
    }
    
    public CompletionTask createDocumentationTask() {
        return new AsyncCompletionTask(
                new AsyncCompletionQuery() {
            private PvwaveDocumentationItem item= new PvwaveDocumentationItem(procedure);
            protected void query(
                    CompletionResultSet completionResultSet,
                    Document document, int i) {
                completionResultSet.setDocumentation(item);
                completionResultSet.finish();
            }
        }
        );
    }
    
    public CompletionTask createToolTipTask() {
        return null;
    }
    
    public boolean instantSubstitution(JTextComponent jTextComponent) {
        defaultAction(jTextComponent);
        return true;
    }
    
    public int getSortPriority() {
        return 0;
    }
    
    public CharSequence getSortText() {
        return text;
    }
    
    public CharSequence getInsertPrefix() {
        return text.substring(0,offset);
    }
    
}
