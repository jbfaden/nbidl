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

package com.cottagesystems.nbidl.syntax;

import com.cottagesystems.nbidl.syntax.UserRoutinesDataBase;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;

/**
 * Syntax analyzes for Java source files.
 * Tokens and internal states are given below.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public class PvwaveSyntax extends Syntax {
    
    // Internal states
    private static final int ISI_WHITESPACE = 2; // inside white space
    private static final int ISI_LINE_COMMENT = 4; // inside line comment //
    private static final int ISI_BLOCK_COMMENT = 5; // inside block comment /* ... */
    private static final int ISI_STRING_SQUOTE = 91; // inside string constant
    private static final int ISI_STRING_DQUOTE = 90; // string delimited by double quotes
    private static final int ISI_STRING_A_BSLASH = 7; // inside string constant after backslash
    private static final int ISI_CHAR = 8; // inside char constant
    private static final int ISI_CHAR_A_BSLASH = 9; // inside char constant after backslash
    private static final int ISI_IDENTIFIER = 10; // inside identifier
    private static final int ISA_SLASH = 11; // slash char
    private static final int ISA_EQ = 12; // after '='
    private static final int ISA_GT = 13; // after '>'
    private static final int ISA_GTGT = 14; // after '>>'
    private static final int ISA_GTGTGT = 15; // after '>>>'
    private static final int ISA_LT = 16; // after '<'
    private static final int ISA_LTLT = 17; // after '<<'
    private static final int ISA_PLUS = 18; // after '+'
    private static final int ISA_MINUS = 19; // after '-'
    private static final int ISA_STAR = 20; // after '*'
    private static final int ISA_SEMICOLON_IN_BLOCK_COMMENT = 21; // after ';'
    private static final int ISA_SEMICOLON = 100; // after ;
    private static final int ISI_LASTLINE_BLOCK_COMMENT= 101;
    private static final int INI_NEWLINE_IN_BLOCK_COMMENT= 102;
    private static final int ISA_COLON= 103;
    private static final int ISA_PIPE = 22; // after '|'
    private static final int ISA_PERCENT = 23; // after '%'
    private static final int ISA_AND = 24; // after '&'
    private static final int ISA_XOR = 25; // after '^'
    private static final int ISA_EXCLAMATION = 26; // after '!'
    private static final int ISA_ZERO = 27; // after '0'
    private static final int ISI_INT = 28; // integer number
    private static final int ISI_OCTAL = 29; // octal number
    private static final int ISI_DOUBLE = 30; // double number
    private static final int ISI_DOUBLE_EXP = 31; // double number
    private static final int ISI_HEX = 32; // hex number
    private static final int ISA_DOT = 33; // after '.'
    
    private IDLRoutinesDataBase idlKeywords;
    private UserRoutinesDataBase userRoutinesDataBase;
    private int lineCount=-1;
    private int lineOffset=-1; // offset of last '\n'
    
    public PvwaveSyntax() {
        tokenContextPath = PvwaveTokenContext.contextPath;
        idlKeywords= IDLRoutinesDataBase.getInstance();
        userRoutinesDataBase= UserRoutinesDataBase.getInstance();
    }
    
    protected TokenID parseToken() {
        char actChar;
        
        while(offset < stopOffset) {
            actChar = buffer[offset];
            if ( actChar=='\n' && offset!=lineOffset ) {
                lineCount++;
                lineOffset= offset;
            }
            switch (state) {
                case INIT:
                    switch (actChar) {
                        case '"': // NOI18N
                            state = ISI_STRING_DQUOTE;
                            break;
                        case '\'':
                            state = ISI_STRING_SQUOTE;
                            break;
                        case '/':
                            state = ISA_SLASH;
                            break;
                        case '=':
                            state = ISA_EQ;
                            break;
                        case '>':
                            state = ISA_GT;
                            break;
                        case '<':
                            state = ISA_LT;
                            break;
                        case '+':
                            state = ISA_PLUS;
                            break;
                        case '-':
                            state = ISA_MINUS;
                            break;
                        case '*':
                            state = ISA_STAR;
                            break;
                        case '|':
                            state = ISA_PIPE;
                            break;
                        case '%':
                            state = ISA_PERCENT;
                            break;
                        case '&':
                            state = ISA_AND;
                            break;
                        case '^':
                            state = ISA_XOR;
                            break;
                        case '~':
                            offset++;
                            return PvwaveTokenContext.NEG;
                        case '!':
                            state = ISA_EXCLAMATION;
                            break;
                        case '0':
                            state = ISA_ZERO;
                            break;
                        case '.':
                            state = ISA_DOT;
                            break;
                        case ',':
                            offset++;
                            return PvwaveTokenContext.COMMA;
                        case ';':
                            boolean atNewLine= offset==0 || buffer[offset-1]=='\n';
                            if (atNewLine) {
                                state= ISI_BLOCK_COMMENT;
                            } else {
                                state= ISI_LINE_COMMENT;
                            }
                            break;
                        case '$':
                            offset++;
                            return PvwaveTokenContext.DOLLAR;
                        case ':':
                            state = ISA_COLON;
                            break;
                        case '?':
                            offset++;
                            return PvwaveTokenContext.QUESTION;
                        case '(':
                            offset++;
                            return PvwaveTokenContext.LPAREN;
                        case ')':
                            offset++;
                            return PvwaveTokenContext.RPAREN;
                        case '[':
                            offset++;
                            return PvwaveTokenContext.LBRACKET;
                        case ']':
                            offset++;
                            return PvwaveTokenContext.RBRACKET;
                        case '{':
                            offset++;
                            return PvwaveTokenContext.LBRACE;
                        case '}':
                            offset++;
                            return PvwaveTokenContext.RBRACE;
                        case '@': // 1.5 "@ident" annotation // NOI18N
                            offset++;
                            return PvwaveTokenContext.ANNOTATION;
                        case '\n':
                            offset++;
                            return PvwaveTokenContext.ENDOFLINE;
                        default:
                            // Check for whitespace
                            if (Character.isWhitespace(actChar)) {
                                state = ISI_WHITESPACE;
                                break;
                            }
                            
                            // Check for digit
                            if (Character.isDigit(actChar)) {
                                state = ISI_INT;
                                break;
                            }
                            
                            // Check for identifier
                            if (Character.isJavaIdentifierStart(actChar)) {
                                state = ISI_IDENTIFIER;
                                break;
                            }
                            
                            offset++;
                            return PvwaveTokenContext.INVALID_CHAR;
                    } // switch (actChar)
                    break;
                    
                case ISI_WHITESPACE: // white space
                    if ( actChar=='\n' || !Character.isWhitespace(actChar) ) {
                        state = INIT;
                        return PvwaveTokenContext.WHITESPACE;
                    }
                    break;
                    
                case ISA_SEMICOLON:
                    switch (actChar) {
                        case '+':
                            state= ISI_BLOCK_COMMENT;
                            break;
                        case '\n':
                            state= INIT;
                            return PvwaveTokenContext.LINE_COMMENT;
                        default:
                            state= ISI_LINE_COMMENT;
                            break;
                    }
                    break;
                    
                case ISI_LINE_COMMENT:
                    switch (actChar) {
                        case '\n':
                            state = INIT;
                            return PvwaveTokenContext.LINE_COMMENT;
                    }
                    break;
                    
                case ISI_LASTLINE_BLOCK_COMMENT:
                    switch (actChar) {
                        case '\n':
                            state = INIT;
                            return PvwaveTokenContext.BLOCK_COMMENT;
                    }
                    break;
                    
                case INI_NEWLINE_IN_BLOCK_COMMENT: // need to do something when the ;- isn't found
                    switch (actChar) {
                        case ';':
                            state = ISA_SEMICOLON_IN_BLOCK_COMMENT;
                            break;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.BLOCK_COMMENT;
                    }
                    
                case ISI_BLOCK_COMMENT:
                    switch (actChar) {
                        case ';':
                            state = ISA_SEMICOLON_IN_BLOCK_COMMENT;
                            break;
                            //create a block comment token for each line of the comment - a performance fix for #55628
                        case '\n':
                            state = INI_NEWLINE_IN_BLOCK_COMMENT;
                            break;
                    }
                    break;
                    
                case ISA_SEMICOLON_IN_BLOCK_COMMENT:
                    switch (actChar) {
                        case '-':
                            state= ISI_LASTLINE_BLOCK_COMMENT;
                            break;
                        case '\n':
                            state= INI_NEWLINE_IN_BLOCK_COMMENT;
                            break;
                        default:
                            state= ISI_BLOCK_COMMENT;
                            break;
                    }
                    break;
                    
                case ISI_STRING_SQUOTE:
                    switch (actChar) {
                        case '\n':
                            state = INIT;
                            supposedTokenID = PvwaveTokenContext.STRING_LITERAL;
                            return supposedTokenID;
                        case '\'':
                            offset++;
                            state= INIT;
                            return PvwaveTokenContext.STRING_LITERAL;
                    }
                    break;
                    
                case ISI_STRING_DQUOTE:
                    switch (actChar) {
                        case '\n':
                            state = INIT;
                            supposedTokenID = PvwaveTokenContext.STRING_LITERAL;
                            return supposedTokenID;
                        case '"': // NOI18N
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.STRING_LITERAL;
                    }
                    break;
                    
                case ISI_STRING_A_BSLASH:
                    switch (actChar) {
                        case '"': // NOI18N
                        case '\\':
                            break;
                        default:
                            offset--;
                            break;
                    }
                    state = ISI_STRING_DQUOTE;
                    break;
                    
                case ISI_CHAR:
                    switch (actChar) {
                        case '\\':
                            state = ISI_CHAR_A_BSLASH;
                            break;
                        case '\n':
                            state = INIT;
                            supposedTokenID = PvwaveTokenContext.CHAR_LITERAL;
                            // !!!                    return JavaTokenContext.INCOMPLETE_CHAR_LITERAL;
                            return supposedTokenID;
                        case '\'':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.CHAR_LITERAL;
                    }
                    break;
                    
                case ISI_CHAR_A_BSLASH:
                    switch (actChar) {
                        case '\'':
                        case '\\':
                            break;
                        default:
                            offset--;
                            break;
                    }
                    state = ISI_CHAR;
                    break;
                    
                case ISI_IDENTIFIER:
                    if (!(Character.isJavaIdentifierPart(actChar))) {
                        state = INIT;
                        TokenID tid=null;
                        if ( actChar!='=' ) {
                            String target= new String( buffer, tokenOffset,  offset - tokenOffset );
                            tid= matchKeyword(buffer, tokenOffset, offset - tokenOffset);
                        }
                        return (tid != null) ? tid : PvwaveTokenContext.IDENTIFIER;
                    }
                    break;
                    
                case ISA_SLASH:
                    switch (actChar) {
                        case '=':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.DIV_EQ;
                        case '/':
                            state = INIT; // syntax error, but don't mark it as a comment.
                            break;
                        case '*':
                            state = ISI_BLOCK_COMMENT;
                            break;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.DIV;
                    }
                    break;
                    
                case ISA_COLON:
                    switch (actChar) {
                        case ':':
                            offset++;
                            return PvwaveTokenContext.COLON_COLON;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.COLON;
                    }
                    // break;
                    
                case ISA_EQ:
                    switch (actChar) {
                        case '=':
                            offset++;
                            return  PvwaveTokenContext.EQ_EQ;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.EQ;
                    }
                    // break;
                    
                case ISA_GT:
                    switch (actChar) {
                        case '>':
                            state = ISA_GTGT;
                            break;
                        case '=':
                            offset++;
                            return PvwaveTokenContext.GT_EQ;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.GT;
                    }
                    break;
                    
                case ISA_GTGT:
                    switch (actChar) {
                        case '>':
                            state = ISA_GTGTGT;
                            break;
                        case '=':
                            offset++;
                            return PvwaveTokenContext.RSSHIFT_EQ;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.RSSHIFT;
                    }
                    break;
                    
                case ISA_GTGTGT:
                    switch (actChar) {
                        case '=':
                            offset++;
                            return PvwaveTokenContext.RUSHIFT_EQ;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.RUSHIFT;
                    }
                    // break;
                    
                    
                case ISA_LT:
                    switch (actChar) {
                        case '<':
                            state = ISA_LTLT;
                            break;
                        case '=':
                            offset++;
                            return PvwaveTokenContext.LT_EQ;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.LT;
                    }
                    break;
                    
                case ISA_LTLT:
                    switch (actChar) {
                        case '<':
                            state = INIT;
                            offset++;
                            return PvwaveTokenContext.INVALID_OPERATOR;
                        case '=':
                            offset++;
                            return PvwaveTokenContext.LSHIFT_EQ;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.LSHIFT;
                    }
                    
                case ISA_PLUS:
                    switch (actChar) {
                        case '+':
                            offset++;
                            return PvwaveTokenContext.PLUS_PLUS;
                        case '=':
                            offset++;
                            return PvwaveTokenContext.PLUS_EQ;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.PLUS;
                    }
                    
                case ISA_MINUS:
                    switch (actChar) {
                        case '-':
                            offset++;
                            return PvwaveTokenContext.MINUS_MINUS;
                        case '=':
                            offset++;
                            return PvwaveTokenContext.MINUS_EQ;
                        case '>':
                            offset++;
                            return PvwaveTokenContext.MINUS_GT;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.MINUS;
                    }
                    
                case ISA_STAR:
                    switch (actChar) {
                        case '=':
                            offset++;
                            return PvwaveTokenContext.MUL_EQ;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.MUL;
                    }
                    
                    
                case ISA_PIPE:
                    switch (actChar) {
                        case '=':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.OR_EQ;
                        case '|':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.OR_OR;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.OR;
                    }
                    // break;
                    
                case ISA_PERCENT:
                    switch (actChar) {
                        case '=':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.MOD_EQ;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.MOD;
                    }
                    // break;
                    
                case ISA_AND:
                    switch (actChar) {
                        case '=':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.AND_EQ;
                        case '&':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.AND_AND;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.AND;
                    }
                    // break;
                    
                case ISA_XOR:
                    switch (actChar) {
                        case '=':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.XOR_EQ;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.XOR;
                    }
                    // break;
                    
                case ISA_EXCLAMATION:
                    switch (actChar) {
                        case '=':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.NOT_EQ;
                        default:
                            state = INIT;
                            return PvwaveTokenContext.NOT;
                    }
                    // break;
                    
                case ISA_ZERO:
                    switch (actChar) {
                        case '.':
                            state = ISI_DOUBLE;
                            break;
                        case 'x':
                        case 'X':
                            state = ISI_HEX;
                            break;
                        case 'l':
                        case 'L':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.LONG_LITERAL;
                        case 'f':
                        case 'F':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.FLOAT_LITERAL;
                        case 'd':
                        case 'D':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.DOUBLE_LITERAL;
                        case '8': // it's error to have '8' and '9' in octal number
                        case '9':
                            state = INIT;
                            offset++;
                            return PvwaveTokenContext.INVALID_OCTAL_LITERAL;
                        case 'e':
                        case 'E':
                            state = ISI_DOUBLE_EXP;
                            break;
                        default:
                            if (Character.isDigit(actChar)) { // '8' and '9' already handled
                                state = ISI_OCTAL;
                                break;
                            }
                            state = INIT;
                            return PvwaveTokenContext.INT_LITERAL;
                    }
                    break;
                    
                case ISI_INT:
                    switch (actChar) {
                        case 'l':
                        case 'L':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.LONG_LITERAL;
                        case '.':
                            state = ISI_DOUBLE;
                            break;
                        case 'f':
                        case 'F':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.FLOAT_LITERAL;
                        case 'd':
                        case 'D':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.DOUBLE_LITERAL;
                        case 'e':
                        case 'E':
                            state = ISI_DOUBLE_EXP;
                            break;
                        default:
                            if (!(actChar >= '0' && actChar <= '9')) {
                                state = INIT;
                                return PvwaveTokenContext.INT_LITERAL;
                            }
                    }
                    break;
                    
                case ISI_OCTAL:
                    if (!(actChar >= '0' && actChar <= '7')) {
                        
                        state = INIT;
                        return PvwaveTokenContext.OCTAL_LITERAL;
                    }
                    break;
                    
                case ISI_DOUBLE:
                    switch (actChar) {
                        case 'f':
                        case 'F':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.FLOAT_LITERAL;
                        case 'd':
                        case 'D':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.DOUBLE_LITERAL;
                        case 'e':
                        case 'E':
                            state = ISI_DOUBLE_EXP;
                            break;
                        default:
                            if (!((actChar >= '0' && actChar <= '9')
                            || actChar == '.')) {
                                
                                state = INIT;
                                return PvwaveTokenContext.DOUBLE_LITERAL;
                            }
                    }
                    break;
                    
                case ISI_DOUBLE_EXP:
                    switch (actChar) {
                        case 'f':
                        case 'F':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.FLOAT_LITERAL;
                        case 'd':
                        case 'D':
                            offset++;
                            state = INIT;
                            return PvwaveTokenContext.DOUBLE_LITERAL;
                        default:
                            if (!(Character.isDigit(actChar)
                            || actChar == '-' || actChar == '+')) {
                                state = INIT;
                                return PvwaveTokenContext.DOUBLE_LITERAL;
                            }
                    }
                    break;
                    
                case ISI_HEX:
                    if (!((actChar >= 'a' && actChar <= 'f')
                    || (actChar >= 'A' && actChar <= 'F')
                    || Character.isDigit(actChar))
                    ) {
                        
                        state = INIT;
                        return PvwaveTokenContext.HEX_LITERAL;
                    }
                    break;
                    
                case ISA_DOT:
                    if (Character.isDigit(actChar)) {
                        state = ISI_DOUBLE;
                    } else if (actChar == '.' && offset + 1 < stopOffset && buffer[offset + 1] == '.') {
                        offset += 2;
                        state = INIT;
                        return PvwaveTokenContext.ELLIPSIS;
                    } else { // only single dot
                        state = INIT;
                        return PvwaveTokenContext.DOT;
                    }
                    break;
                    
            } // end of switch(state)
            
            offset++;
        } // end of while(offset...)
        
        /** At this stage there's no more text in the scanned buffer.
         * Scanner first checks whether this is completely the last
         * available buffer.
         */
        
        if (lastBuffer) {
            switch(state) {
                case ISI_WHITESPACE:
                    state = INIT;
                    return PvwaveTokenContext.WHITESPACE;
                case ISI_IDENTIFIER:
                    state = INIT;
                    TokenID kwd = matchKeyword(buffer, tokenOffset, offset - tokenOffset);
                    return (kwd != null) ? kwd : PvwaveTokenContext.IDENTIFIER;
                case ISI_LINE_COMMENT:
                    return PvwaveTokenContext.LINE_COMMENT; // stay in line-comment state
                case ISI_BLOCK_COMMENT:
                case ISA_SEMICOLON_IN_BLOCK_COMMENT:
                    return PvwaveTokenContext.BLOCK_COMMENT; // stay in block-comment state
                case ISI_STRING_DQUOTE:
                case ISI_STRING_SQUOTE:
                case ISI_STRING_A_BSLASH:
                    return PvwaveTokenContext.STRING_LITERAL; // hold the state
                case ISI_CHAR:
                case ISI_CHAR_A_BSLASH:
                    return PvwaveTokenContext.CHAR_LITERAL; // hold the state
                case ISA_ZERO:
                case ISI_INT:
                    state = INIT;
                    return PvwaveTokenContext.INT_LITERAL;
                case ISI_OCTAL:
                    state = INIT;
                    return PvwaveTokenContext.OCTAL_LITERAL;
                case ISI_DOUBLE:
                case ISI_DOUBLE_EXP:
                    state = INIT;
                    return PvwaveTokenContext.DOUBLE_LITERAL;
                case ISI_HEX:
                    state = INIT;
                    return PvwaveTokenContext.HEX_LITERAL;
                case ISA_DOT:
                    state = INIT;
                    return PvwaveTokenContext.DOT;
                case ISA_SLASH:
                    state = INIT;
                    return PvwaveTokenContext.DIV;
                case ISA_EQ:
                    state = INIT;
                    return PvwaveTokenContext.EQ;
                case ISA_COLON:
                    state= INIT;
                    return PvwaveTokenContext.COLON;
                case ISA_GT:
                    state = INIT;
                    return PvwaveTokenContext.GT;
                case ISA_GTGT:
                    state = INIT;
                    return PvwaveTokenContext.RSSHIFT;
                case ISA_GTGTGT:
                    state = INIT;
                    return PvwaveTokenContext.RUSHIFT;
                case ISA_LT:
                    state = INIT;
                    return PvwaveTokenContext.LT;
                case ISA_LTLT:
                    state = INIT;
                    return PvwaveTokenContext.LSHIFT;
                case ISA_PLUS:
                    state = INIT;
                    return PvwaveTokenContext.PLUS;
                case ISA_MINUS:
                    state = INIT;
                    return PvwaveTokenContext.MINUS;
                case ISA_STAR:
                    state = INIT;
                    return PvwaveTokenContext.MUL;
                case ISA_PIPE:
                    state = INIT;
                    return PvwaveTokenContext.OR;
                case ISA_PERCENT:
                    state = INIT;
                    return PvwaveTokenContext.MOD;
                case ISA_AND:
                    state = INIT;
                    return PvwaveTokenContext.AND;
                case ISA_XOR:
                    state = INIT;
                    return PvwaveTokenContext.XOR;
                case ISA_EXCLAMATION:
                    state = INIT;
                    return PvwaveTokenContext.NOT;
            }
        }
        
        /* At this stage there's no more text in the scanned buffer, but
         * this buffer is not the last so the scan will continue on another buffer.
         * The scanner tries to minimize the amount of characters
         * that will be prescanned in the next buffer by returning the token
         * where possible.
         */
        
        switch (state) {
            case ISI_WHITESPACE:
                return PvwaveTokenContext.WHITESPACE;
        }
        
        return null; // nothing found
    }
    
    public String getStateName(int stateNumber) {
        switch(stateNumber) {
            case ISI_WHITESPACE:
                return "ISI_WHITESPACE"; // NOI18N
            case ISI_LINE_COMMENT:
                return "ISI_LINE_COMMENT"; // NOI18N
            case ISI_BLOCK_COMMENT:
                return "ISI_BLOCK_COMMENT"; // NOI18N
            case ISI_STRING_SQUOTE:
                return "ISI_STRING_SQUOTE"; // NOI18N
            case ISI_STRING_DQUOTE:
                return "ISI_STRING_DQUOTE"; // NOI18N
            case ISI_STRING_A_BSLASH:
                return "ISI_STRING_A_BSLASH"; // NOI18N
            case ISI_CHAR:
                return "ISI_CHAR"; // NOI18N
            case ISI_CHAR_A_BSLASH:
                return "ISI_CHAR_A_BSLASH"; // NOI18N
            case ISI_IDENTIFIER:
                return "ISI_IDENTIFIER"; // NOI18N
            case ISA_SLASH:
                return "ISA_SLASH"; // NOI18N
            case ISA_EQ:
                return "ISA_EQ"; // NOI18N
            case ISA_GT:
                return "ISA_GT"; // NOI18N
            case ISA_GTGT:
                return "ISA_GTGT"; // NOI18N
            case ISA_GTGTGT:
                return "ISA_GTGTGT"; // NOI18N
            case ISA_LT:
                return "ISA_LT"; // NOI18N
            case ISA_LTLT:
                return "ISA_LTLT"; // NOI18N
            case ISA_PLUS:
                return "ISA_PLUS"; // NOI18N
            case ISA_MINUS:
                return "ISA_MINUS"; // NOI18N
            case ISA_STAR:
                return "ISA_STAR"; // NOI18N
            case ISA_SEMICOLON_IN_BLOCK_COMMENT:
                return "ISA_STAR_I_BLOCK_COMMENT"; // NOI18N
            case ISA_PIPE:
                return "ISA_PIPE"; // NOI18N
            case ISA_PERCENT:
                return "ISA_PERCENT"; // NOI18N
            case ISA_AND:
                return "ISA_AND"; // NOI18N
            case ISA_XOR:
                return "ISA_XOR"; // NOI18N
            case ISA_EXCLAMATION:
                return "ISA_EXCLAMATION"; // NOI18N
            case ISA_ZERO:
                return "ISA_ZERO"; // NOI18N
            case ISI_INT:
                return "ISI_INT"; // NOI18N
            case ISI_OCTAL:
                return "ISI_OCTAL"; // NOI18N
            case ISI_DOUBLE:
                return "ISI_DOUBLE"; // NOI18N
            case ISI_DOUBLE_EXP:
                return "ISI_DOUBLE_EXP"; // NOI18N
            case ISI_HEX:
                return "ISI_HEX"; // NOI18N
            case ISA_DOT:
                return "ISA_DOT"; // NOI18N
                
            default:
                return super.getStateName(stateNumber);
        }
    }
    
    final private char downCase( char ch ) {
        return (char)(ch | 32);
    }
    
    public TokenID matchKeyword(char[] buffer, int offset, int len) {
        TokenID kw= idlKeywords.matchKeyword( buffer, offset, len);
        if ( kw!=null ) return kw;
        
        TokenID ukw= userRoutinesDataBase.matchKeyword( buffer, offset, len );
        if ( ukw!=null ) return ukw;
        
        // kludge so that the example IDL code for selecting coloring will show user tokens.
        if ( len>5 && buffer[offset]=='u' && buffer[offset+1]=='s'
                && buffer[offset+2]=='e' && buffer[offset+3]=='r' && buffer[offset+4]=='_' ) {
            String token= new String( buffer, offset, len );
            if ( token.equals( "user_add_em" ) || token.equals( "user_mult_em" ) ) {
                return PvwaveTokenContext.USERFUNC;
            }
        }
        
        if (len > 16)
            return null;
        if (len <= 1)
            return null;
        
        switch (downCase(buffer[offset++])) {
            case 'a':
                if (len < 3)
                    return null;
                switch (downCase(buffer[offset++])) {
                    case 'n':
                        return (len == 3
                                && downCase(buffer[offset++]) == 'd')
                                ? PvwaveTokenContext.PVAND : null;
                    default:
                        return null;
                }
            case 'b':
                if (len < 5)
                    return null;
                switch (downCase(buffer[offset++])) {
                    case 'e':
                        return (len == 5
                                && downCase(buffer[offset++]) == 'g'
                                && downCase(buffer[offset++]) == 'i'
                                && downCase(buffer[offset++]) == 'n')
                                ? PvwaveTokenContext.BEGIN : null;
                    case 'r':
                        return (len == 5
                                && downCase(buffer[offset++]) == 'e'
                                && downCase(buffer[offset++]) == 'a'
                                && downCase(buffer[offset++]) == 'k')
                                ? PvwaveTokenContext.BREAK : null;
                    default:
                        return null;
                }
            case 'c':
                if (len < 4)
                    return null;
                switch (downCase(buffer[offset++])) {
                    case 'a':
                        switch (downCase(buffer[offset++])) {
                            case 's':
                                return (len == 4
                                        && downCase(buffer[offset++]) == 'e')
                                        ? PvwaveTokenContext.CASE : null;
                            default:
                                return null;
                        }
                    case 'o':
                        switch ( downCase(buffer[offset++]) ) {
                            case 'm':
                                switch( downCase(buffer[offset++]) ) {
                                    case 'm':
                                        
                                        return (len == 6
                                                && downCase(buffer[offset++]) == 'o'
                                                && downCase(buffer[offset++]) == 'n' )
                                                ? PvwaveTokenContext.COMMON : null;
                                    case 'p':
                                        return (len == 11
                                                && downCase(buffer[offset++]) == 'i'
                                                && downCase(buffer[offset++]) == 'l'
                                                && downCase(buffer[offset++]) == 'e'
                                                && downCase(buffer[offset++]) == '_'
                                                && downCase(buffer[offset++]) == 'o'
                                                && downCase(buffer[offset++]) == 'p'
                                                && downCase(buffer[offset++]) == 't'
                                                )
                                                ? PvwaveTokenContext.COMPILE_OPT : null;
                                        
                                }
                            case 'n':
                                return (len == 8
                                        && downCase(buffer[offset++]) == 't'
                                        && downCase(buffer[offset++]) == 'i'
                                        && downCase(buffer[offset++]) == 'n'
                                        && downCase(buffer[offset++]) == 'u'
                                        && downCase(buffer[offset++]) == 'e'
                                        ) ? PvwaveTokenContext.CONTINUE : null;
                        }
                        
                    default:
                        return null;
                }
            case 'd':
                if ( len==2 && downCase(buffer[offset++])=='o' ) return PvwaveTokenContext.DO; else return null;
            case 'e':
                if (len < 2)
                    return null;
                switch (downCase(buffer[offset++])) {
                    case 'l':
                        return (len == 4
                                && downCase(buffer[offset++]) == 's'
                                && downCase(buffer[offset++]) == 'e')
                                ? PvwaveTokenContext.ELSE : null;
                    case 'n':
                        if ( downCase(buffer[offset++]) != 'd' ) {
                            return null;
                        } else if ( len==3 ) {
                            return PvwaveTokenContext.END;
                        } else {
                            switch ( downCase(buffer[offset++]) ) {
                                case 'c': return ( len==7
                                        && downCase(buffer[offset++]) == 'a'
                                        && downCase(buffer[offset++]) == 's'
                                        && downCase(buffer[offset++]) == 'e'
                                        ) ? PvwaveTokenContext.ENDCASE : null;
                                case 'e': return ( len==7
                                        && downCase(buffer[offset++]) == 'l'
                                        && downCase(buffer[offset++]) == 's'
                                        && downCase(buffer[offset++]) == 'e'
                                        ) ? PvwaveTokenContext.ENDELSE : null;
                                case 'f': return ( len==6
                                        && downCase(buffer[offset++]) == 'o'
                                        && downCase(buffer[offset++]) == 'r'
                                        ) ? PvwaveTokenContext.ENDFOR : null;
                                case 'i': return ( len==5
                                        && downCase(buffer[offset++]) == 'f'
                                        ) ? PvwaveTokenContext.ENDIF : null;
                                case 'r': return ( len==6
                                        && downCase(buffer[offset++]) == 'e'
                                        && downCase(buffer[offset++]) == 'p'
                                        ) ? PvwaveTokenContext.ENDREP : null;
                                case 's': return ( len==9
                                        && downCase(buffer[offset++]) == 'w'
                                        && downCase(buffer[offset++]) == 'i'
                                        && downCase(buffer[offset++]) == 't'
                                        && downCase(buffer[offset++]) == 'c'
                                        && downCase(buffer[offset++]) == 'h'
                                        ) ? PvwaveTokenContext.ENDSWITCH : null;
                                case 'w': return ( len==8
                                        && downCase(buffer[offset++]) == 'h'
                                        && downCase(buffer[offset++]) == 'i'
                                        && downCase(buffer[offset++]) == 'l'
                                        && downCase(buffer[offset++]) == 'e'
                                        ) ? PvwaveTokenContext.ENDWHILE : null;
                            }
                        }
                        
                    case 'q': return ( len==2 ) ? PvwaveTokenContext.PVEQ : null;
                    default: return null;
                }
            case 'f':
                if (len <= 2)
                    return null;
                switch (downCase(buffer[offset++])) {
                    case 'o':
                        switch( downCase(buffer[offset++]) ) {
                            case 'r':
                                if ( len==3 ) return PvwaveTokenContext.FOR;
                                return ( len==16
                                        && downCase(buffer[offset++]) == 'w'
                                        && downCase(buffer[offset++]) == 'a'
                                        && downCase(buffer[offset++]) == 'r'
                                        && downCase(buffer[offset++]) == 'd'
                                        && downCase(buffer[offset++]) == '_'
                                        && downCase(buffer[offset++]) == 'f'
                                        && downCase(buffer[offset++]) == 'u'
                                        && downCase(buffer[offset++]) == 'n'
                                        && downCase(buffer[offset++]) == 'c'
                                        && downCase(buffer[offset++]) == 't'
                                        && downCase(buffer[offset++]) == 'i'
                                        && downCase(buffer[offset++]) == 'o'
                                        && downCase(buffer[offset++]) == 'n'
                                        ) ? PvwaveTokenContext.FORWARD_FUNCTION : null;
                            default: return null;
                        }
                    case 'u':
                        return ( len==8
                                && downCase(buffer[offset++]) == 'n'
                                && downCase(buffer[offset++]) == 'c'
                                && downCase(buffer[offset++]) == 't'
                                && downCase(buffer[offset++]) == 'i'
                                && downCase(buffer[offset++]) == 'o'
                                && downCase(buffer[offset++]) == 'n' ) ? PvwaveTokenContext.FUNCTION : null;
                    default: return null;
                }
            case 'g':
                switch ( downCase(buffer[offset++]) ) {
                    case 'e':
                        return ( len==2 ) ? PvwaveTokenContext.PVGE : null;
                    case 't':
                        return (len==2 ) ? PvwaveTokenContext.PVGT : null;
                    case 'o':
                        return (len == 4
                                && downCase(buffer[offset++]) == 't'
                                && downCase(buffer[offset++]) == 'o')
                                ? PvwaveTokenContext.GOTO : null;
                    default: return null;
                }
            case 'i':
                switch (downCase(buffer[offset++])) {
                    case 'f':
                        return (len == 2)
                        ? PvwaveTokenContext.IF : null;
                    case 'n':
                        return (len == 8
                                && downCase(buffer[offset++]) == 'h'
                                && downCase(buffer[offset++]) == 'e'
                                && downCase(buffer[offset++]) == 'r'
                                && downCase(buffer[offset++]) == 'i'
                                && downCase(buffer[offset++]) == 't'
                                && downCase(buffer[offset++]) == 's' ) ? PvwaveTokenContext.INHERITS : null;
                    default:
                        return null;
                }
                
                
            case 'l':
                if ( len!= 2 ) return null;
                switch ( downCase(buffer[offset++]) ) {
                    case 'e': return PvwaveTokenContext.PVLE;
                    case 't': return PvwaveTokenContext.PVLT;
                    default: return null;
                }
            case 'n':
                switch ( downCase(buffer[offset++]) ) {
                    case 'e': return len==2 ? PvwaveTokenContext.PVNE : null;
                    case 'o': return ( len==3
                            && downCase(buffer[offset++])=='t' ) ? PvwaveTokenContext.PVNOT : null;
                    default: return null;
                }
            case 'o':
                switch( downCase(buffer[offset++]) ) {
                    case 'f': return len==2 ? PvwaveTokenContext.OF : null;
                    case 'r': return len==2 ? PvwaveTokenContext.PVOR : null;
                    case 'n': return ( len==10
                            && downCase(buffer[offset++]) == '_'
                            && downCase(buffer[offset++]) == 'i'
                            && downCase(buffer[offset++]) == 'o'
                            && downCase(buffer[offset++]) == 'e'
                            && downCase(buffer[offset++]) == 'r'
                            && downCase(buffer[offset++]) == 'r'
                            && downCase(buffer[offset++]) == 'o'
                            && downCase(buffer[offset++]) == 'r'
                            ) ? PvwaveTokenContext.ON_IOERROR : null;
                    default: return null;
                }
            case 'p':
                return ( len==3
                        && downCase(buffer[offset++]) == 'r'
                        && downCase(buffer[offset++]) == 'o'
                        ) ? PvwaveTokenContext.PRO : null;
            case 'r':
                return (len == 6
                        && downCase(buffer[offset++]) == 'e'
                        && downCase(buffer[offset++]) == 'p'
                        && downCase(buffer[offset++]) == 'e'
                        && downCase(buffer[offset++]) == 'a'
                        && downCase(buffer[offset++]) == 't')
                        ? PvwaveTokenContext.REPEAT : null;
            case 's':
                if (len <= 4)
                    return null;
                switch (downCase(buffer[offset++])) {
                    case 'w':
                        return (len == 6
                                && downCase(buffer[offset++]) == 'i'
                                && downCase(buffer[offset++]) == 't'
                                && downCase(buffer[offset++]) == 'c'
                                && downCase(buffer[offset++]) == 'h')
                                ? PvwaveTokenContext.SWITCH : null;
                    default:
                        return null;
                }
            case 't':
                return ( len==4
                        && downCase(buffer[offset++]) == 'h'
                        && downCase(buffer[offset++]) == 'e'
                        && downCase(buffer[offset++]) == 'n' ) ? PvwaveTokenContext.THEN : null;
            case 'u':
                return ( len==5
                        && downCase(buffer[offset++]) == 'n'
                        && downCase(buffer[offset++]) == 't'
                        && downCase(buffer[offset++]) == 'i'
                        && downCase(buffer[offset++]) == 'l'
                        ) ? PvwaveTokenContext.UNTIL : null;
                
            case 'w':
                return (len == 5
                        && downCase(buffer[offset++]) == 'h'
                        && downCase(buffer[offset++]) == 'i'
                        && downCase(buffer[offset++]) == 'l'
                        && downCase(buffer[offset++]) == 'e')
                        ? PvwaveTokenContext.WHILE : null;
            case 'x':
                return (len == 3
                        && downCase(buffer[offset++]) == 'o'
                        && downCase(buffer[offset++]) == 'r' )
                        ? PvwaveTokenContext.PVXOR : null;
                
            default:
                return null;
        }
    }
    
    public int getLineOffset() {
        return lineCount;
    }
    
    public void setLineOffset( int offset ) {
        this.lineCount= offset;
    }
}
