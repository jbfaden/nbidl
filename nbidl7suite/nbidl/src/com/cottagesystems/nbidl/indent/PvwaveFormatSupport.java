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

import com.cottagesystems.nbidl.syntax.PvwaveTokenContext;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.TokenItem;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.TokenItem;
import org.netbeans.editor.ext.FormatTokenPosition;
import org.netbeans.editor.ext.ExtFormatSupport;
import org.netbeans.editor.ext.FormatWriter;

/**
 * Pvwave indentation services are located here
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public class PvwaveFormatSupport extends ExtFormatSupport {

    private TokenContextPath tokenContextPath;

    public PvwaveFormatSupport(FormatWriter formatWriter) {
        this(formatWriter, PvwaveTokenContext.contextPath);
    }

    public PvwaveFormatSupport(FormatWriter formatWriter, TokenContextPath tokenContextPath) {
        super(formatWriter);
        this.tokenContextPath = tokenContextPath;
    }

    public TokenContextPath getTokenContextPath() {
        return tokenContextPath;
    }

    public boolean isComment(TokenItem token, int offset) {
        TokenID tokenID = token.getTokenID();
        return (token.getTokenContextPath() == tokenContextPath
                && (tokenID == PvwaveTokenContext.LINE_COMMENT
                || tokenID == PvwaveTokenContext.BLOCK_COMMENT));
    }

    public boolean isMultiLineComment(TokenItem token) {
        return (token.getTokenID() == PvwaveTokenContext.BLOCK_COMMENT);
    }

    public boolean isMultiLineComment(FormatTokenPosition pos) {
        TokenItem token = pos.getToken();
        return (token == null) ? false : isMultiLineComment(token);
    }

    /** Check whether the given token is multi-line comment
     * that starts with slash and two stars.
     */
    public boolean isPvwaveDocComment(TokenItem token) {
        return isMultiLineComment(token)
        && token.getImage().startsWith("/**");
    }

    public TokenID getWhitespaceTokenID() {
        return PvwaveTokenContext.WHITESPACE;
    }

    public TokenContextPath getWhitespaceTokenContextPath() {
        return tokenContextPath;
    }

    public boolean canModifyWhitespace(TokenItem inToken) {
        if (inToken.getTokenContextPath() == PvwaveTokenContext.contextPath) {
            switch (inToken.getTokenID().getNumericID()) {
                case PvwaveTokenContext.BLOCK_COMMENT_ID:
                case PvwaveTokenContext.WHITESPACE_ID:
                    return true;
            }
        }

        return false;
    }


    /** Find the starting token of the statement before
     * the given position and also return all the command
     * delimiters. It searches in the backward direction
     * for all the delimiters and statement starts and
     * return all the tokens that are either command starts
     * or delimiters. As the first step it uses
     * <code>getPreviousToken()</code> so it ignores the initial token.
     * @param token token before which the statement-start
     *  and delimiter is being searched.
     * @return token that is start of the given statement
     *  or command delimiter.
     *  If the start of the statement is not found, null is retrurned.
     */
    public TokenItem findStatement(TokenItem token) {
        TokenItem lit = null; // last important token
        TokenItem t = getPreviousToken(token);

        while (t != null) {
            if (t.getTokenContextPath() == tokenContextPath) {

                switch (t.getTokenID().getNumericID()) {

                    case PvwaveTokenContext.BEGIN_ID:
                    case PvwaveTokenContext.ELSE_ID:
                        return (lit != null) ? lit : t;

                    case PvwaveTokenContext.COLON_ID:
                        TokenItem tt = findAnyToken(t, null, new TokenID[] { PvwaveTokenContext.SWITCH, PvwaveTokenContext.CASE, PvwaveTokenContext.ELSE, PvwaveTokenContext.FOR, PvwaveTokenContext.QUESTION, }, t.getTokenContextPath(), true);
                        if (tt != null) {
                            switch (tt.getTokenID().getNumericID()) {
                                case PvwaveTokenContext.CASE_ID:
                                case PvwaveTokenContext.SWITCH_ID:
                                case PvwaveTokenContext.FOR_ID:
                                    return (lit != null) ? lit : t;
                            }
                        }
                        break;

                    case PvwaveTokenContext.DO_ID:
                    case PvwaveTokenContext.SWITCH_ID:
                    case PvwaveTokenContext.CASE_ID:
                    case PvwaveTokenContext.DEFAULT_ID:
                        return t;

                    case PvwaveTokenContext.FOR_ID:
                    case PvwaveTokenContext.IF_ID:
                    case PvwaveTokenContext.WHILE_ID:
                        /* Try to find the statement after ( ... )
                         * If it exists, then the first important
                         * token after it is the stmt start. Otherwise
                         * it's this token.
                         */
                        if (lit != null && lit.getTokenID() == PvwaveTokenContext.LPAREN) {
                            // Find matching right paren in fwd dir
                            TokenItem mt = findMatchingToken(lit, token,
                                    PvwaveTokenContext.RPAREN, false);
                            if (mt != null && mt.getNext() != null) {
                                mt = findImportantToken(mt.getNext(), token, false);
                                if (mt != null) {
                                    return mt;
                                }
                            }
                        }

                        // No further stmt found, return this one
                        return t;

                }

                // Remember last important token
                if (isImportant(t, 0)) {
                    lit = t;
                }

            }

            t = t.getPrevious();
        }

        return lit;
    }


    /** Find the 'if' when the 'else' is provided.
     * @param elseToken the token with the 'else' command
     *  for which the 'if' is being searched.
     * @return corresponding 'if' token or null if there's
     *  no corresponding 'if' statement.
     */
    public TokenItem findIf(TokenItem elseToken) {
        if (elseToken == null || !tokenEquals(elseToken,
                PvwaveTokenContext.ELSE, tokenContextPath)
                ) {
            throw new IllegalArgumentException("Only accept 'else'."); // NOI18N
        }

        int braceDepth = 0; // depth of the braces
        int elseDepth = 0; // depth of multiple else stmts
        while (true) {
            elseToken = findStatement(elseToken);
            if (elseToken == null) {
                return null;
            }

            switch (elseToken.getTokenID().getNumericID()) {
                case PvwaveTokenContext.BEGIN_ID:
                    if (--braceDepth < 0) {
                        return null; // no corresponding right brace
                    }
                    break;
                case PvwaveTokenContext.END_ID:
                case PvwaveTokenContext.ENDCASE_ID:
                case PvwaveTokenContext.ENDELSE_ID:
                case PvwaveTokenContext.ENDFOR_ID:
                case PvwaveTokenContext.ENDREP_ID:
                case PvwaveTokenContext.ENDSWITCH_ID:
                case PvwaveTokenContext.ENDWHILE_ID:
                    braceDepth++;
                    break;

                case PvwaveTokenContext.ELSE_ID:
                    if (braceDepth == 0) {
                        elseDepth++;
                    }
                    break;

                case PvwaveTokenContext.SEMICOLON_ID:
                case PvwaveTokenContext.COLON_ID:
                case PvwaveTokenContext.DO_ID:
                case PvwaveTokenContext.CASE_ID:
                case PvwaveTokenContext.DEFAULT_ID:
                case PvwaveTokenContext.FOR_ID:
                case PvwaveTokenContext.WHILE_ID:
                    break;

                case PvwaveTokenContext.IF_ID:
                    if (braceDepth == 0) {
                        if (elseDepth-- == 0) {
                            return elseToken; // successful search
                        }
                    }
                    break;
            }
        }
    }


    /** Find the 'switch' when the 'case' is provided.
     * @param caseToken the token with the 'case' command
     *  for which the 'switch' is being searched.
     * @return corresponding 'switch' token or null if there's
     *  no corresponding 'switch' statement.
     */
    public TokenItem findSwitch(TokenItem caseToken) {
        if (caseToken == null ||
                (!tokenEquals(caseToken, PvwaveTokenContext.CASE,
                tokenContextPath)
                && !tokenEquals(caseToken, PvwaveTokenContext.ELSE,
                tokenContextPath))
                ) {
            throw new IllegalArgumentException("Only accept 'case' or 'else'."); // NOI18N
        }

        int braceDepth = 1; // depth of the braces - need one more left
        while (true) {
            caseToken = findStatement(caseToken);
            if (caseToken == null) {
                return null;
            }

            switch (caseToken.getTokenID().getNumericID()) {
                case PvwaveTokenContext.LBRACE_ID:
                    if (--braceDepth < 0) {
                        return null; // no corresponding right brace
                    }
                    break;

                case PvwaveTokenContext.END_ID:
                case PvwaveTokenContext.ENDCASE_ID:
                case PvwaveTokenContext.ENDIF_ID:  // old IDL allowed this
                    braceDepth++;
                    break;

                case PvwaveTokenContext.SWITCH_ID:
                case PvwaveTokenContext.DEFAULT_ID:
                    if (braceDepth == 0) {
                        return caseToken;
                    }
                    break;
            }
        }
    }


    /** Find the start of the statement.
     * @param token token from which to start. It searches
     *  backward using <code>findStatement()</code> so it ignores
     *  the given token.
     * @return the statement start token (outer statement start for nested
     *  statements).
     *  It returns the same token if there is '{' before
     *  the given token.
     */
    public TokenItem findStatementStart(TokenItem token) {
        TokenItem t = findStatement(token);
        if (t != null) {
            switch (t.getTokenID().getNumericID()) {
                case PvwaveTokenContext.SEMICOLON_ID: // ';' found
                    TokenItem scss = findStatement(t);

                    // fix for issue 14274
                    if (scss == null)
                        return token;

                    switch (scss.getTokenID().getNumericID()) {
                        case PvwaveTokenContext.LBRACE_ID: // '{' then ';'
                        case PvwaveTokenContext.RBRACE_ID: // '}' then ';'
                        case PvwaveTokenContext.COLON_ID: // ':' then ';'
                        case PvwaveTokenContext.CASE_ID: // 'case' then ';'
                        case PvwaveTokenContext.DEFAULT_ID:
                        case PvwaveTokenContext.SEMICOLON_ID: // ';' then ';'
                            return t; // return ';'

                        case PvwaveTokenContext.DO_ID:
                        case PvwaveTokenContext.FOR_ID:
                        case PvwaveTokenContext.IF_ID:
                        case PvwaveTokenContext.WHILE_ID:

                        case PvwaveTokenContext.ELSE_ID: // 'else' then ';'
                            // Find the corresponding 'if'
                            TokenItem ifss = findIf(scss);
                            if (ifss != null) { // 'if' ... 'else' then ';'
                                return findStatementStart(ifss);

                            } else { // no valid starting 'if'
                                return scss; // return 'else'
                            }

                        default: // something usual then ';'
                            TokenItem bscss = findStatement(scss);
                            if (bscss != null) {
                                switch (bscss.getTokenID().getNumericID()) {
                                    case PvwaveTokenContext.SEMICOLON_ID: // ';' then stmt ending with ';'
                                    case PvwaveTokenContext.LBRACE_ID:
                                    case PvwaveTokenContext.RBRACE_ID:
                                    case PvwaveTokenContext.COLON_ID:
                                        return scss; //

                                    case PvwaveTokenContext.DO_ID:
                                    case PvwaveTokenContext.FOR_ID:
                                    case PvwaveTokenContext.IF_ID:
                                    case PvwaveTokenContext.WHILE_ID:

                                    case PvwaveTokenContext.ELSE_ID:
                                        // Find the corresponding 'if'
                                        ifss = findIf(bscss);
                                        if (ifss != null) { // 'if' ... 'else' ... ';'
                                            return findStatementStart(ifss);

                                        } else { // no valid starting 'if'
                                            return bscss; // return 'else'
                                        }
                                }
                            }

                            return scss;
                    } // semicolon servicing end

                case PvwaveTokenContext.LBRACE_ID: // '{' found
                    return token; // return original token

                case PvwaveTokenContext.RBRACE_ID: // '}' found
                    TokenItem lb = findMatchingToken(t, null,
                            PvwaveTokenContext.LBRACE, true);
                    if (lb != null) { // valid matching left-brace
                        // Find a stmt-start of the '{'
                        TokenItem lbss = findStatement(lb);
                        if (lbss != null) {
                            switch (lbss.getTokenID().getNumericID()) {
                                case PvwaveTokenContext.ELSE_ID: // 'else {'
                                    // Find the corresponding 'if'
                                    TokenItem ifss = findIf(lbss);
                                    if (ifss != null) { // valid 'if'
                                        return findStatementStart(ifss);
                                    } else {
                                        return lbss; // return 'else'
                                    }

                                case PvwaveTokenContext.DO_ID:
                                case PvwaveTokenContext.FOR_ID:
                                case PvwaveTokenContext.IF_ID:
                                case PvwaveTokenContext.WHILE_ID:

                            }

                            // another hack to prevent problem described in issue 17033
                            if (lbss.getTokenID().getNumericID() == PvwaveTokenContext.LBRACE_ID) {
                                return t; // return right brace
                            }

                            return lbss;
                        }

                    }
                    return t; // return right brace

                case PvwaveTokenContext.COLON_ID:
                case PvwaveTokenContext.CASE_ID:
                case PvwaveTokenContext.DEFAULT_ID:
                    return token;

                case PvwaveTokenContext.ELSE_ID:
                    // Find the corresponding 'if'
                    TokenItem ifss = findIf(t);
                    return (ifss != null) ? findStatementStart(ifss) : t;

                case PvwaveTokenContext.DO_ID:
                case PvwaveTokenContext.FOR_ID:
                case PvwaveTokenContext.IF_ID:
                case PvwaveTokenContext.WHILE_ID:

                case PvwaveTokenContext.IDENTIFIER_ID:
                    return t;
                default:
                    return t;
            }
        }

        return token; // return original token
    }

    /** Get the indentation for the given token.
     * It first searches whether there's an non-whitespace and a non-leftbrace
     * character on the line with the token and if so,
     * it takes indent of the non-ws char instead.
     * @param token token for which the indent is being searched.
     *  The token itself is ignored and the previous token
     *  is used as a base for the search.
     * @param forceFirstNonWhitespace set true to ignore leftbrace and search
     * directly for first non-whitespace
     */
    public int getTokenIndent(TokenItem token, boolean forceFirstNonWhitespace) {
        FormatTokenPosition tp = getPosition(token, 0);
        FormatTokenPosition fnw;
        fnw = findLineFirstNonWhitespace(tp);
        
        if (fnw != null) { // valid first non-whitespace
            tp = fnw;
        }
        return getVisualColumnOffset(tp);
    }

    public int getTokenIndent(TokenItem token) {
        return getTokenIndent(token, false);
    }

    /** Find the indentation for the first token on the line.
     * The given token is also examined in some cases.
     */
    public int findIndent(TokenItem token) {
        int indent = -1; // assign invalid indent

        // First check the given token
        if (token != null) {
            switch (token.getTokenID().getNumericID()) {
                case PvwaveTokenContext.ELSE_ID:
                    TokenItem ifss = findIf(token);
                    if (ifss != null) {
                        indent = getTokenIndent(ifss);
                    }
                    break;

                case PvwaveTokenContext.LBRACE_ID:
                    TokenItem stmt = findStatement(token);
                    if (stmt == null) {
                        indent = 0;

                    } else {
                        switch (stmt.getTokenID().getNumericID()) {
                            case PvwaveTokenContext.DO_ID:
                            case PvwaveTokenContext.FOR_ID:
                            case PvwaveTokenContext.IF_ID:
                            case PvwaveTokenContext.WHILE_ID:
                            case PvwaveTokenContext.ELSE_ID:
                                indent = getTokenIndent(stmt);
                                break;

                            case PvwaveTokenContext.LBRACE_ID:
                                indent = getTokenIndent(stmt) + getShiftWidth();
                                break;

                            default:
                                stmt = findStatementStart(token);
                                if (stmt == null) {
                                    indent = 0;

                                } else if (stmt == token) {
                                    stmt = findStatement(token); // search for delimiter
                                    indent = (stmt != null) ? indent = getTokenIndent(stmt) : 0;

                                } else { // valid statement
                                    indent = getTokenIndent(stmt);
                                    switch (stmt.getTokenID().getNumericID()) {
                                        case PvwaveTokenContext.LBRACE_ID:
                                            indent += getShiftWidth();
                                            break;
                                    }
                                }
                        }
                    }
                    break;

                case PvwaveTokenContext.RBRACE_ID:
                    TokenItem rbmt = findMatchingToken(token, null,
                            PvwaveTokenContext.LBRACE, true);
                    if (rbmt != null) { // valid matching left-brace
                        TokenItem t = findStatement(rbmt);
                        boolean forceFirstNonWhitespace = false;
                        if (t == null) {
                            t = rbmt; // will get indent of the matching brace

                        } else {
                            switch (t.getTokenID().getNumericID()) {
                                case PvwaveTokenContext.SEMICOLON_ID:
                                case PvwaveTokenContext.LBRACE_ID:
                                case PvwaveTokenContext.RBRACE_ID:
                                {
                                    t = rbmt;
                                    forceFirstNonWhitespace = true;
                                }
                            }
                        }
                        // the right brace must be indented to the first
                        // non-whitespace char - forceFirstNonWhitespace=true
                        indent = getTokenIndent(t, forceFirstNonWhitespace);

                    } else { // no matching left brace
                        indent = getTokenIndent(token); // leave as is
                    }
                    break;

                case PvwaveTokenContext.CASE_ID:
                case PvwaveTokenContext.DEFAULT_ID:
                    TokenItem swss = findSwitch(token);
                    if (swss != null) {
                        indent = getTokenIndent(swss) + getShiftWidth();
                    }
                    break;

            }
        }

        // If indent not found, search back for the first important token
        if (indent < 0) { // if not yet resolved
            TokenItem t = findImportantToken(token, null, true);
            if (t != null) { // valid important token
                if (t.getTokenContextPath() != tokenContextPath) {
                    // For non-Pvwave tokens such as jsp return indent
                    // of last non-Pvwave line
                    return getTokenIndent(t);
                }

                switch (t.getTokenID().getNumericID()) {
                    case PvwaveTokenContext.SEMICOLON_ID: // semicolon found
                        TokenItem tt = findStatementStart(token);
                        indent = getTokenIndent(tt);

                        break;

                    case PvwaveTokenContext.BEGIN_ID:
                        TokenItem lbss = findStatementStart(t);
                        if (lbss == null) {
                            lbss = t;
                        }
                        indent = getTokenIndent(lbss) + getShiftWidth();
                        break;

                    case PvwaveTokenContext.END_ID:
                    case PvwaveTokenContext.ENDFOR_ID:
                        if (true) {
                            TokenItem t3 = findStatementStart(token);
                            indent = getTokenIndent(t3);
                            break;
                        }

                        /** Check whether the following situation occurs:
                         *  if (t1)
                         *    if (t2) {
                         *      ...
                         *    }
                         *
                         *  In this case the indentation must be shifted
                         *  one level back.
                         */
                        TokenItem rbmt = findMatchingToken(t, null,
                                PvwaveTokenContext.LBRACE, true);
                        if (rbmt != null) { // valid matching left-brace
                            // Check whether there's a indent stmt
                            TokenItem t6 = findStatement(rbmt);
                            if (t6 != null) {
                                switch (t6.getTokenID().getNumericID()) {
                                    case PvwaveTokenContext.ELSE_ID:
                                        /* Check the following situation:
                                         * if (t1)
                                         *   if (t2)
                                         *     c1();
                                         *   else {
                                         *     c2();
                                         *   }
                                         */

                                        // Find the corresponding 'if'
                                        t6 = findIf(t6);
                                        if (t6 != null) { // valid 'if'
                                            TokenItem t7 = findStatement(t6);
                                            if (t7 != null) {
                                                switch (t7.getTokenID().getNumericID()) {
                                                    case PvwaveTokenContext.DO_ID:
                                                    case PvwaveTokenContext.FOR_ID:
                                                    case PvwaveTokenContext.IF_ID:
                                                    case PvwaveTokenContext.WHILE_ID:
                                                        indent = getTokenIndent(t7);
                                                        break;

                                                    case PvwaveTokenContext.ELSE_ID:
                                                        indent = getTokenIndent(findStatementStart(t6));
                                                }
                                            }
                                        }
                                        break;

                                    case PvwaveTokenContext.DO_ID:
                                    case PvwaveTokenContext.FOR_ID:
                                    case PvwaveTokenContext.IF_ID:
                                    case PvwaveTokenContext.WHILE_ID:
                                        /* Check the following:
                                         * if (t1)
                                         *   if (t2) {
                                         *     c1();
                                         *   }
                                         */
                                        TokenItem t7 = findStatement(t6);
                                        if (t7 != null) {
                                            switch (t7.getTokenID().getNumericID()) {
                                                case PvwaveTokenContext.DO_ID:
                                                case PvwaveTokenContext.FOR_ID:
                                                case PvwaveTokenContext.IF_ID:
                                                case PvwaveTokenContext.WHILE_ID:
                                                    indent = getTokenIndent(t7);
                                                    break;

                                                case PvwaveTokenContext.ELSE_ID:
                                                    indent = getTokenIndent(findStatementStart(t6));

                                            }
                                        }
                                        break;

                                    case PvwaveTokenContext.BEGIN_ID: // '{' ... '{'
                                        indent = getTokenIndent(rbmt);
                                        break;

                                }

                            }

                            if (indent < 0) {
                                indent = getTokenIndent(t); // indent of original rbrace
                            }

                        } else { // no matching left-brace
                            indent = getTokenIndent(t); // return indent of '}'
                        }
                        break;

                    case PvwaveTokenContext.RPAREN_ID:
                        // Try to find the matching left paren
                        TokenItem rpmt = findMatchingToken(t, null, PvwaveTokenContext.LPAREN, true);
                        if (rpmt != null) {
                            rpmt = findImportantToken(rpmt, null, true);
                            // Check whether there are the indent changing kwds
                            if (rpmt != null && rpmt.getTokenContextPath() == tokenContextPath) {
                                switch (rpmt.getTokenID().getNumericID()) {
                                    case PvwaveTokenContext.FOR_ID:
                                    case PvwaveTokenContext.IF_ID:
                                    case PvwaveTokenContext.WHILE_ID:
                                        // Indent one level
                                        indent = getTokenIndent(rpmt) + getShiftWidth();
                                        break;
                                }
                            }
                        }
                        break;

                    case PvwaveTokenContext.COLON_ID:
                        TokenItem ttt = findAnyToken(t, null, new TokenID[] { PvwaveTokenContext.SWITCH, PvwaveTokenContext.CASE, PvwaveTokenContext.ELSE, PvwaveTokenContext.FOR, PvwaveTokenContext.QUESTION, }, t.getTokenContextPath(), true);
                        if (ttt != null && ttt.getTokenID().getNumericID() == PvwaveTokenContext.QUESTION_ID) {
                            indent = getTokenIndent(ttt) + getShiftWidth();
                        } else {
                            // Indent of line with ':' plus one indent level
                            indent = getTokenIndent(t) + getShiftWidth();
                        }
                        break;

                    case PvwaveTokenContext.QUESTION_ID:
                    case PvwaveTokenContext.DO_ID:
                    case PvwaveTokenContext.ELSE_ID:
                        indent = getTokenIndent(t) + getShiftWidth();
                        break;

                    case PvwaveTokenContext.COMMA_ID:
                    default: {
                        // Find stmt start and add continuation indent
                        TokenItem stmtStart = findStatementStart(t);
                        indent = getTokenIndent(stmtStart);
                        if (stmtStart != null) {
                            // Check whether there is a comma on the previous line end
                            // and if so then also check whether the present
                            // statement is inside array initialization statement
                            // and not inside parens and if so then do not indent
                            // statement continuation
                            if (t != null && tokenEquals(t, PvwaveTokenContext.COMMA, tokenContextPath)) {
                                if (isArrayInitializationBraceBlock(t, null) && !isInsideParens(t, stmtStart)) {
                                    // Eliminate the later effect of statement continuation shifting
                                    indent -= getFormatStatementContinuationIndent();
                                }
                            }
                            indent += getFormatStatementContinuationIndent();
                        }

                        break;
                    }
                }

                if (indent < 0) { // no indent found yet
                    indent = getTokenIndent(t);
                }
            }
        }

        if (indent < 0) { // no important token found
            indent = 0;
        }

        return indent;
    }

    public FormatTokenPosition indentLine(FormatTokenPosition pos) {
        int indent = 0; // Desired indent

        // Get the first non-whitespace position on the line
        FormatTokenPosition firstNWS = findLineFirstNonWhitespace(pos);
        if (firstNWS != null) { // some non-WS on the line
            if (isComment(firstNWS)) { // comment is first on the line
                if (isMultiLineComment(firstNWS) && firstNWS.getOffset() != 0) {

                    // Indent the inner lines of the multi-line comment by one
                    indent = getLineIndent(getPosition(firstNWS.getToken(), 0), true) + 1;

                    // If the line is inside multi-line comment and doesn't contain '*'
                    if (!isIndentOnly()) {
                        if (getChar(firstNWS) != '*') {
                            if (isPvwaveDocComment(firstNWS.getToken())) {
                                if (getFormatLeadingStarInComment()) {
                                    // For Pvwave-doc it should be OK to add the star
                                    insertString(firstNWS, "* "); // NOI18N
                                }

                            } else {
                                // For non-Pvwave-doc not because it can be commented code
                                indent = getLineIndent(pos, true);
                            }
                        }

                    } else { // in indent mode (not formatting)
                        if (getChar(firstNWS) != '*') { // e.g. not for '*/'
                            if (isPvwaveDocComment(firstNWS.getToken())) {
                                if (getFormatLeadingStarInComment()) {
                                    insertString(firstNWS, "* "); // NOI18N
                                    setIndentShift(2);
                                }
                            }
                        }
                    }

                } else if (!isMultiLineComment(firstNWS)) { // line-comment
                    indent = firstNWS.equals(findLineStart(firstNWS)) ? getLineIndent(firstNWS, true) : findIndent(firstNWS.getToken());
                } else { // multi-line comment
                    if (isPvwaveDocComment(firstNWS.getToken())) {
                        indent = findIndent(firstNWS.getToken());
                    } else {
                        // check whether the multiline comment isn't finished on the same line (see issue 12821)
                        if (firstNWS.getToken().getImage().indexOf('\n') == -1)
                            indent = findIndent(firstNWS.getToken());
                        else
                            indent = getLineIndent(firstNWS, true);
                    }
                }

            } else { // first non-WS char is not comment
                indent = findIndent(firstNWS.getToken());
            }

        } else { // whole line is WS
            // Can be empty line inside multi-line comment
            TokenItem token = pos.getToken();
            if (token == null) {
                token = findLineStart(pos).getToken();
                if (token == null) { // empty line
                    token = getLastToken();
                }
            }

            if (token != null && isMultiLineComment(token)) {
                if (getFormatLeadingStarInComment()
                && (isIndentOnly() || isPvwaveDocComment(token))
                ) {
                    // Insert initial '*'
                    insertString(pos, "*"); // NOI18N
                    setIndentShift(1);
                }

                // Indent the multi-comment by one more space
                indent = getVisualColumnOffset(getPosition(token, 0)) + 1;

            } else { // non-multi-line comment
                indent = findIndent(pos.getToken());
            }
        }

        // For indent-only always indent
        return changeLineIndent(pos, indent);
    }


    /**
     * Check whether there are left parenthesis before the given token
     * until the limit token.
     *
     * @param token non-null token from which to start searching back.
     * @param limitToken limit token when reached the search will stop
     *  with returning false.
     * @return true if there is LPAREN token before the given token
     *  (while respecting paren nesting).
     */
    private boolean isInsideParens(TokenItem token, TokenItem limitToken) {
        int depth = 0;
        token = token.getPrevious();

        while (token != null && token != limitToken) {
            if (tokenEquals(token, PvwaveTokenContext.LPAREN, tokenContextPath)) {
                if (--depth < 0) {
                    return true;
                }

            } else if (tokenEquals(token, PvwaveTokenContext.RPAREN, tokenContextPath)) {
                depth++;
            }
            token = token.getPrevious();
        }
        return false;
    }

    // I'm going on a hunch that this is for [ 2,3,4,5 ]
    private boolean isArrayInitializationBraceBlock(TokenItem token, TokenItem limitToken) {
        int depth = 0;
        token = token.getPrevious();

        while (token != null && token != limitToken && token.getTokenContextPath() == tokenContextPath) {
            switch (token.getTokenID().getNumericID()) {
                case PvwaveTokenContext.RBRACKET_ID:
                    depth++;
                    break;

                case PvwaveTokenContext.LBRACKET_ID:
                    depth--;
                    if (depth < 0) {
                        TokenItem prev = findImportantToken(token, limitToken, true);
                        
                        return (prev != null && prev.getTokenContextPath() == tokenContextPath
                                && (PvwaveTokenContext.EQ.equals(prev.getTokenID())));
                    }
                    break;

                // Array initialization block should not contain statements or ';'
                case PvwaveTokenContext.DO_ID:
                case PvwaveTokenContext.FOR_ID:
                case PvwaveTokenContext.IF_ID:
                case PvwaveTokenContext.WHILE_ID:
                case PvwaveTokenContext.SEMICOLON_ID:
                    if (depth == 0) {
                        return false;
                    }
            }                    
            token = token.getPrevious();
        }
        return false;
    }


    public boolean getFormatSpaceBeforeParenthesis() {
        return false;
        // return getSettingBoolean(PvwaveSettingsNames.Pvwave_FORMAT_SPACE_BEFORE_PARENTHESIS,
        //                          PvwaveSettingsDefaults.defaultPvwaveFormatSpaceBeforeParenthesis);
    }

    public boolean getFormatSpaceAfterComma() {
        return false;
        //return getSettingBoolean(PvwaveSettingsNames.Pvwave_FORMAT_SPACE_AFTER_COMMA,
        //                         PvwaveSettingsDefaults.defaultPvwaveFormatSpaceAfterComma);
    }

    public boolean getFormatNewlineBeforeBrace() {
        return false;
        //return getSettingBoolean(PvwaveSettingsNames.Pvwave_FORMAT_NEWLINE_BEFORE_BRACE,
        //                         PvwaveSettingsDefaults.defaultPvwaveFormatNewlineBeforeBrace);
    }

    public boolean getFormatLeadingSpaceInComment() {
        return false;
        //return getSettingBoolean(PvwaveSettingsNames.Pvwave_FORMAT_LEADING_SPACE_IN_COMMENT,
        //                         PvwaveSettingsDefaults.defaultPvwaveFormatLeadingSpaceInComment);
    }

    public boolean getFormatLeadingStarInComment() {
        return false;
        //return getSettingBoolean(PvwaveSettingsNames.Pvwave_FORMAT_LEADING_STAR_IN_COMMENT,
        //                         PvwaveSettingsDefaults.defaultPvwaveFormatLeadingStarInComment);
    }

    private int getFormatStatementContinuationIndent() {
        return 8;
        //return getSettingInteger(PvwaveSettingsNames.Pvwave_FORMAT_STATEMENT_CONTINUATION_INDENT,
        //                         PvwaveSettingsDefaults.defaultPvwaveFormatStatementContinuationIndent);
    }

    
}
