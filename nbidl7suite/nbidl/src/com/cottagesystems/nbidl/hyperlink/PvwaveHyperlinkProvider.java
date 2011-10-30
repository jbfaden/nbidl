/*
 * PvwaveHyperlinkProvider.java
 *
 * Created on August 14, 2007, 9:38 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.hyperlink;

import com.cottagesystems.nbidl.syntax.PvwaveTokenContext;
import com.cottagesystems.nbidl.syntax.SyntaxUtil;
import com.cottagesystems.nbidl.syntax.UserRoutinesDataBase;
import com.cottagesystems.nbidl.util.GoToSupport;
import com.cottagesystems.nbidl.util.PvwaveUtil;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.Line;

/**
 *
 * @author jbf
 */
public class PvwaveHyperlinkProvider implements HyperlinkProvider {
    
    /** Creates a new instance of PvwaveHyperlinkProvider */
    public PvwaveHyperlinkProvider() {
     
    }
    
    SyntaxUtil.TokenSentence focus=null;
    
    public boolean isHyperlinkPoint(Document document, int pos ) {
        try {
            SyntaxUtil.TokenSentence tok= SyntaxUtil.getTokenAt( (BaseDocument)document, pos );
            focus= tok;
            if ( tok.tokenId==null ) {
                return false;
            } else {
                if ( tok.tokenId.getNumericID()==PvwaveTokenContext.USERFUNC_ID ) {
                    return true;
                } else if ( tok.index > 0 && tok.tokens.get(tok.index-1).getNumericID()==PvwaveTokenContext.COMMON_ID ) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (BadLocationException ex) {
            return false;
        }
    }
    
    public int[] getHyperlinkSpan(Document document, int i) {
        return new int[] { focus.offset, focus.offset + focus.length };
    }
    
    public void performClickAction(Document document, int i) {
        if (  focus.tokenId.getNumericID()==PvwaveTokenContext.USERFUNC_ID ) {
            FileObject fo= GoToSupport.getFileObject(document);
            Line line= PvwaveUtil.getLine( UserRoutinesDataBase.getInstance(fo).getProcedure(focus.text) );
            if ( line!=null ) line.show( Line.SHOW_GOTO );
        }
    }
    
}
