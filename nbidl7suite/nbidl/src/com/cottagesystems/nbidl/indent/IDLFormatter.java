/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package com.cottagesystems.nbidl.indent;

import com.cottagesystems.nbidl.syntax.PvwaveSyntax;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import org.netbeans.editor.Syntax;
import org.netbeans.editor.ext.AbstractFormatLayer;
import org.netbeans.editor.ext.FormatTokenPosition;
import org.netbeans.editor.ext.FormatSupport;
import org.netbeans.editor.ext.FormatWriter;
import org.netbeans.editor.ext.ExtFormatter;

/**
 * @author Tomasz.Slota@Sun.COM
 */
public class IDLFormatter extends ExtFormatter {
    
    //at least one character
    private static final Pattern VALID_TAG_NAME = Pattern.compile("[\\w+|-]*"); // NOI18N
    
    private static final int WORKUNITS_MAX = 100;
    
    public IDLFormatter(Class kitClass) {
        super(kitClass);
    }
    
    public FormatSupport createFormatSupport(FormatWriter fw) {
        return new IDLFormatSupport(fw);
    }
    
    protected boolean acceptSyntax(Syntax syntax) {
        return (syntax instanceof PvwaveSyntax );
    }
    
    protected void initFormatLayers() {
        addFormatLayer(new StripEndWhitespaceLayer());
    }
    
    private int indentNewLineToOldLine( Document doc, int offset ) {
        try {
            Element rootElem = doc.getDefaultRootElement();
            // Offset should be valid -&gt; no check for lineIndex -1
            int lineIndex = rootElem.getElementIndex(offset);
            String lineText;
            int whitespaceEndIndex;
            do {
                Element lineElem = rootElem.getElement(lineIndex);
                lineText = doc.getText(lineElem.getStartOffset(),
                        lineElem.getEndOffset() - lineElem.getStartOffset() - 1); // strip ending '\n'
                whitespaceEndIndex = 0;
                while (whitespaceEndIndex < lineText.length()) {
                    // Break on non-whitespace char
                    if (!Character.isWhitespace(lineText.charAt(whitespaceEndIndex))) {
                        lineIndex = 0; // stop outer loop
                        break;
                    }
                    whitespaceEndIndex++;
                }
                lineIndex--; // continue to search for previous non-whitespace line
            } while (lineIndex >= 0);
            
            String nlPlusIndent = "\n" + lineText.substring(0, whitespaceEndIndex);
            doc.insertString(offset, nlPlusIndent, null); // NOI18N
            offset += nlPlusIndent.length();
        } catch (BadLocationException ex) {
            // ignore
        }
        
        return offset;
    }
    
    public int indentNewLine(Document document, int i) {
        int retValue;
        //
        //retValue = super.indentNewLine(document, i);
        retValue= indentNewLineToOldLine(document,i);
        return retValue;
    }
    
    public int indentLine(Document document, int i) {
        int retValue;
        
        retValue = super.indentLine(document, i);
        return retValue;
    }
    
    public class StripEndWhitespaceLayer extends AbstractFormatLayer {
        
        public StripEndWhitespaceLayer() {
            super("idl-strip-whitespace-at-line-end-layer"); // NOI18N
        }
        
        protected FormatSupport createFormatSupport(FormatWriter fw) {
            return new IDLFormatSupport(fw);
        }
        
        public void format(FormatWriter fw) {
            IDLFormatSupport xfs = (IDLFormatSupport)createFormatSupport(fw);
            
            FormatTokenPosition pos = xfs.getFormatStartPosition();
            
            if ( (xfs.isLineStart(pos) == false) ||
                    xfs.isIndentOnly() ) { // don't do anything
            } else { // remove end-line whitespace
                while (pos.getToken() != null) {
                    System.err.println( pos.getOffset() + ": " + pos );
                    
                    pos = xfs.removeLineEndWhitespace(pos);
                    if (pos.getToken() != null) {
                        pos = xfs.getNextPosition(pos);
                    }
                }
            }
        }
    }
}
