/*
 * SyntaxUtil.java
 *
 * Created on August 14, 2007, 10:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.syntax;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.TokenProcessor;
import org.netbeans.editor.Utilities;

/**
 * Place for syntax-based utility routines.
 * @author jbf
 */
public class SyntaxUtil {
    
    public static class TokenSentence {
        public List<TokenID> tokens;
        public int index;
        public TokenID tokenId;
        public String text;
        public int offset;
        public int length;
    }
    
    public static TokenSentence getTokenAt( final BaseDocument doc, final int offset ) throws BadLocationException {
        
        final TokenSentence result= new TokenSentence();
        final ArrayList<TokenID> tokens= new ArrayList<TokenID>();
        
        result.tokens= tokens;
        result.index= -1;
        result.tokenId= null;
        
        int i0= Utilities.getRowStart( doc, offset );
        int i1= Utilities.getRowEnd( doc, offset );
        
        SyntaxSupport support= new SyntaxSupport( doc );
        
        
        TokenProcessor tp= new TokenProcessor() {
            int shift=0;
            
            public boolean token(TokenID tok, TokenContextPath tokenContextPath, int t0, int tl ) {
                if ( tok.getNumericID() != PvwaveTokenContext.WHITESPACE_ID ) {
                    tokens.add(tok);
                    t0+= shift;
                    if ( t0 <= offset && offset < t0+tl ) {
                        result.tokens= tokens;
                        result.tokenId= tok;
                        result.index= tokens.size()-1;
                        result.offset= t0;
                        result.length= tl;
                        try {
                            result.text= doc.getText( t0, tl );
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                return true;
            }
            
            public int eot(int i) {
                return 0;
            }
            
            public void nextBuffer(char[] c, int offset, int len, int startPos, int preScan, boolean lastBuffer ) {
                shift=  -1 * offset + startPos;
            }
            
        };
        
        support.tokenizeText( tp, i0, i1, true );
        
        return result;
    }
    
    /** Creates a new instance of SyntaxUtil */
    private SyntaxUtil() {
    }
    
}
