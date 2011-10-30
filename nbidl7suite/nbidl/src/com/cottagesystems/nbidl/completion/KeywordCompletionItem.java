/*
 * UserCompletionItem.java
 *
 * Created on August 2, 2006, 3:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.completion;

import com.cottagesystems.nbidl.dataobject.Procedure;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.editor.Utilities;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
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
public class KeywordCompletionItem implements CompletionItem {
    
    String text;
    String keyword;
    int offset;
    Procedure procedure;
    
    final static Logger logger= Logger.getLogger( "pvwave" );
    
    /** Creates a new instance of UserCompletionItem */
    public KeywordCompletionItem( String text, int offset, Procedure procedure, String keyword ) {
        this.text= text;
        this.offset= offset;
        this.procedure= procedure;
        this.keyword= keyword;
    }
    
    public void defaultAction(JTextComponent jTextComponent) {
        try {
            int pos= jTextComponent.getCaretPosition();
            int pos1= Utilities.getWordEnd(jTextComponent,pos);
            Document d= jTextComponent.getDocument();
            d.insertString( pos, keyword.substring(offset), null );
        } catch ( BadLocationException ex ) {
            throw new RuntimeException(ex);
        }
        Completion.get().hideCompletion();
    }
    
    public void processKeyEvent(KeyEvent keyEvent) {
    }
    
    public int getPreferredWidth(Graphics graphics, Font font) {
        return 210;
    }
    
    public void render(Graphics graphics, Font font, Color color, Color color0, int i, int i0, boolean b) {
        logger.fine("KeywordCompletionItem.render");
        CompletionUtilities.renderHtml(null,keyword+" kw "+procedure.getName(),null,graphics,font, color,i,i0,b);
    }
    
    public CompletionTask createDocumentationTask() {
        //return new PvwaveDocumentationTask();
        
        final CompletionDocumentation di;
        
        if ( procedure.isUserDefined() ) {
            di= new UserDocumentationItem(procedure);
        } else {
            di= new PvwaveDocumentationItem( procedure );
        }
        
        return new AsyncCompletionTask(
                new AsyncCompletionQuery() {
            protected void query(
                    CompletionResultSet completionResultSet,
                    Document document, int i) {
                completionResultSet.setDocumentation(di);
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
        return 1;
    }
    
    public CharSequence getSortText() {
        return text;
    }
    
    public CharSequence getInsertPrefix() {
        return text.substring(0,offset);
    }
    
}
