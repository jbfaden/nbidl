/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.cottagesystems.nbidl.indent;

import com.cottagesystems.nbidl.syntax.PvwaveSyntax;
import com.cottagesystems.nbidl.syntax.PvwaveTokenContext;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.TokenItem;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.ext.AbstractFormatLayer;
import org.netbeans.editor.ext.FormatTokenPosition;
import org.netbeans.editor.ext.ExtFormatter;
import org.netbeans.editor.ext.FormatSupport;
import org.netbeans.editor.ext.FormatWriter;

/**
 * Pvwave indentation services are located here
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public class PvwaveFormatter extends ExtFormatter {

    public PvwaveFormatter(Class kitClass) {
        super(kitClass);
    }

    protected boolean acceptSyntax(Syntax syntax) {
        return (syntax instanceof PvwaveSyntax);
    }

    public int[] getReformatBlock(JTextComponent target, String typedText) {
        int[] ret = null;
        BaseDocument doc = Utilities.getDocument(target);
        int dotPos = target.getCaret().getDot();
        if (doc != null) {
            /* Check whether the user has written the ending 'e'
             * of the first 'else' on the line.
             */
            if ("e".equals(typedText)) { // NOI18N
                try {
                    int fnw = Utilities.getRowFirstNonWhite(doc, dotPos);
                    if (fnw >= 0 && fnw + 4 == dotPos
                            && "else".equals(doc.getText(fnw, 4)) // NOI18N
                            ) {
                        ret = new int[] { fnw, fnw + 4 };
                    }
                } catch (BadLocationException e) {
                }

            } else if (":".equals(typedText)) { // NOI18N
                try {
                    int fnw = Utilities.getRowFirstNonWhite(doc, dotPos);
                    if (fnw >= 0 && fnw + 4 <= doc.getLength()
                    && "case".equals(doc.getText(fnw, 4)) // NOI18N
                    ) {
                        ret = new int[] { fnw, fnw + 4 };
                    } else {
                        if (fnw >= 0 & fnw + 7 <= doc.getLength()
                        && "else".equals(doc.getText(fnw, 4)) // NOI18N
                        ) {
                            ret = new int[] {fnw, fnw + 4 };
                        }
                    }
                } catch (BadLocationException e) {
                }

            } else {
                ret = super.getReformatBlock(target, typedText);
            }
        }

        return ret;
    }

    protected void initFormatLayers() {
        addFormatLayer(new StripEndWhitespaceLayer());
        addFormatLayer(new PvwaveLayer());
    }

    public FormatSupport createFormatSupport(FormatWriter fw) {
        return new PvwaveFormatSupport(fw);
    }

    public class StripEndWhitespaceLayer extends AbstractFormatLayer {

        public StripEndWhitespaceLayer() {
            super("Pvwave-strip-whitespace-at-line-end"); // NOI18N
        }

        protected FormatSupport createFormatSupport(FormatWriter fw) {
            return new PvwaveFormatSupport(fw);
        }

        public void format(FormatWriter fw) {
            PvwaveFormatSupport jfs = (PvwaveFormatSupport)createFormatSupport(fw);

            FormatTokenPosition pos = jfs.getFormatStartPosition();
            if (jfs.isIndentOnly()) { // don't do anything

            } else { // remove end-line whitespace
                while (pos.getToken() != null) {
                    FormatTokenPosition startPos = pos;
                    pos = jfs.removeLineEndWhitespace(pos);
                    if (pos.getToken() != null) {
                        pos = jfs.getNextPosition(pos);
                    }
                    // fix for issue 14725
                    // this is more hack than correct fix. It happens that
                    // jfs.removeLineEndWhitespace() does not move to next
                    // position. The reason is that token from which the
                    // endline whitespaces must be removed is not 'modifiable' -
                    // FormatWritter.canModifyToken() returns false in
                    // FormatWritter.remove. I don't dare to fix this problem
                    // in ExtFormatSupport and so I'm patching this
                    // loop to check whether we are still on the same position
                    // and if we are, let's do break. If similar problem reappear
                    // we will have to find better fix. Hopefully, with the planned
                    // conversion of indentation engines to new lexel module
                    // all this code will be replaced in next verison.
                    if (startPos.equals(pos)) {
                        break;
                    }
                }
            }
        }

    }

    public class PvwaveLayer extends AbstractFormatLayer {

        public PvwaveLayer() {
            super("Pvwave-layer"); // NOI18N
        }

        protected FormatSupport createFormatSupport(FormatWriter fw) {
            return new PvwaveFormatSupport(fw);
        }

        public void format(FormatWriter fw) {
            try {
                PvwaveFormatSupport jfs = (PvwaveFormatSupport)createFormatSupport(fw);

                FormatTokenPosition pos = jfs.getFormatStartPosition();

                if (jfs.isIndentOnly()) {  // create indentation only
                    jfs.indentLine(pos);

                } else { // regular formatting

                    while (pos != null) {

                        // Indent the current line
                        jfs.indentLine(pos);

                        // Format the line by additional rules
                        formatLine(jfs, pos);

                        // Goto next line
                        FormatTokenPosition pos2 = jfs.findLineEnd(pos);
                        if (pos2 == null || pos2.getToken() == null)
                            break; // the last line was processed

                        pos = jfs.getNextPosition(pos2, javax.swing.text.Position.Bias.Forward);
                        if (pos == pos2)
                            break; // in case there is no next position
                        if (pos == null || pos.getToken() == null)
                            break; // there is nothing after the end of line

                        FormatTokenPosition fnw = jfs.findLineFirstNonWhitespace(pos);
                        if (fnw != null) {
                            pos = fnw;
                        } else { // no non-whitespace char on the line
                            pos = jfs.findLineStart(pos);
                        }
                    }
                }
            } catch (IllegalStateException e) {
            }
        }


        private void removeLineBeforeToken(TokenItem token, PvwaveFormatSupport jfs, boolean checkRBraceBefore){
            FormatTokenPosition tokenPos = jfs.getPosition(token, 0);
            // Check that nothing exists before token
            if (jfs.findNonWhitespace(tokenPos, null, true, true) != null){
                return;
            }

            // Check that the backward nonWhite is }
            if (checkRBraceBefore){
                FormatTokenPosition ftpos = jfs.findNonWhitespace(tokenPos, null, false, true);
                if (ftpos == null || ftpos.getToken().getTokenID().getNumericID() != PvwaveTokenContext.END_ID){
                    return;
                }
            }

            // Check that nothing exists after token, but ignore comments
            if (jfs.getNextPosition(tokenPos) != null){
                FormatTokenPosition ftp = jfs.findImportant(jfs.getNextPosition(tokenPos), null, true, false);
                if (ftp != null){
                    insertNewLineBeforeToken(ftp.getToken(), jfs);
                }
            }

            // check that on previous line is some stmt
            FormatTokenPosition ftp = jfs.findLineStart(tokenPos); // find start of current line
            FormatTokenPosition endOfPreviousLine = jfs.getPreviousPosition(ftp); // go one position back - means previous line
            if (endOfPreviousLine == null || endOfPreviousLine.getToken().getTokenID() != PvwaveTokenContext.WHITESPACE){
                return;
            }
            ftp = jfs.findLineStart(endOfPreviousLine); // find start of the previous line - now we have limit position
            ftp = jfs.findImportant(tokenPos, ftp, false, true); // find something important till the limit
            if (ftp == null){
                return;
            }

            // check that previous line does not end with "{" or line comment
            ftp = jfs.findNonWhitespace(endOfPreviousLine, null, true, true);
            if (ftp.getToken().getTokenID() == PvwaveTokenContext.LINE_COMMENT ||
                    ftp.getToken().getTokenID() == PvwaveTokenContext.BEGIN){
                return;
            }

            // now move the token to the end of previous line
            boolean remove = true;
            while (remove) {
                if (token.getPrevious() == endOfPreviousLine.getToken()){
                    remove = false;
                }
                if (jfs.canRemoveToken(token.getPrevious())){
                    jfs.removeToken(token.getPrevious());
                }else{
                    return;  // should never get here!
                }
            }
            // insert one space before token
            if (jfs.canInsertToken(token)){
                jfs.insertSpaces(token, 1);
            }

        }

        /** insertNewLineBeforeKeyword
         *  if getFormatNewlineBeforeBrace is true
         */
        private void insertNewLineBeforeToken(TokenItem token, PvwaveFormatSupport jfs){
            FormatTokenPosition elsePos = jfs.getPosition(token, 0);
            FormatTokenPosition imp = jfs.findImportant(elsePos,
                    null, true, true); // stop on line start
            if (imp != null && imp.getToken().getTokenContextPath()
            == jfs.getTokenContextPath()
            ) {
                // Insert new-line
                if (jfs.canInsertToken(token)) {
                    jfs.insertToken(token, jfs.getValidWhitespaceTokenID(),
                            jfs.getValidWhitespaceTokenContextPath(), "\n"); // NOI18N
                    jfs.removeLineEndWhitespace(imp);
                    // reindent newly created line
                    jfs.indentLine(elsePos);
                }
            }
        }

        protected void formatLine(PvwaveFormatSupport jfs, FormatTokenPosition pos) {
            TokenItem token = jfs.findLineStart(pos).getToken();
            while (token != null) {
/*                if (jfs.findLineEnd(jfs.getPosition(token, 0)).getToken() == token) {
                    break; // at line end
                }
 */

                if (token.getTokenContextPath() == jfs.getTokenContextPath()) {
                    switch (token.getTokenID().getNumericID()) {
                        case PvwaveTokenContext.BEGIN_ID: // 'BEGIN'
                            if (!jfs.isIndentOnly()) {
                                //remove line before token if applicable
                                    FormatTokenPosition tokenPos = jfs.getPosition(token, 0);
                                    FormatTokenPosition ftpos = jfs.findNonWhitespace(tokenPos, null, false, true);
                                    if (ftpos != null){
                                        switch (ftpos.getToken().getTokenID().getNumericID()) {
                                            case PvwaveTokenContext.RPAREN_ID: // ) {
                                            case PvwaveTokenContext.IDENTIFIER_ID: // public class Hello {
                                            case PvwaveTokenContext.ELSE_ID:
                                        }
                                    }

                            } // !jfs.isIndentOnly()
                            break;

                        case PvwaveTokenContext.LPAREN_ID:
                            if (jfs.getFormatSpaceBeforeParenthesis()) {
                                TokenItem prevToken = token.getPrevious();
                                if (prevToken != null &&
                                        (prevToken.getTokenID() == PvwaveTokenContext.IDENTIFIER ) ) {
                                    if (jfs.canInsertToken(token)) {
                                        jfs.insertToken(token, jfs.getWhitespaceTokenID(),
                                                jfs.getWhitespaceTokenContextPath(), " "); // NOI18N
                                    }
                                }
                            } else {
                                // bugfix 9813: remove space before left parenthesis
                                TokenItem prevToken = token.getPrevious();
                                if (prevToken != null && prevToken.getTokenID() == PvwaveTokenContext.WHITESPACE &&
                                        prevToken.getImage().length() == 1) {
                                    TokenItem prevprevToken = prevToken.getPrevious();
                                    if (prevprevToken != null &&
                                            (prevprevToken.getTokenID() == PvwaveTokenContext.IDENTIFIER ) ) {
                                        if (jfs.canRemoveToken(prevToken)) {
                                            jfs.removeToken(prevToken);
                                        }
                                    }
                                }

                            }
                            break;
                    }
                }

                token = token.getNext();
            }
        }

    }

}
