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

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.Font;
import java.awt.Color;
import javax.swing.KeyStroke;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import org.netbeans.editor.Acceptor;
import org.netbeans.editor.AcceptorFactory;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsNames;
import org.netbeans.editor.SettingsDefaults;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.TokenCategory;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.MultiKeyBinding;
import org.netbeans.editor.ext.ExtSettingsNames;
import org.netbeans.editor.ext.ExtSettingsDefaults;
import org.netbeans.editor.ext.ExtKit;

/**
* Default settings values for Java.
*
* @author Miloslav Metelka
* @version 1.00
*/

public class PvwaveSettingsDefaults extends ExtSettingsDefaults {

    public static final Boolean defaultCaretSimpleMatchBrace = Boolean.FALSE;
    public static final Boolean defaultHighlightMatchingBracket = Boolean.TRUE;

    public static final Acceptor defaultIdentifierAcceptor = AcceptorFactory.JAVA_IDENTIFIER;
    public static final Acceptor defaultAbbrevResetAcceptor = AcceptorFactory.NON_JAVA_IDENTIFIER;
    public static final Boolean defaultWordMatchMatchCase = Boolean.TRUE;

    // Formatting
    public static final Boolean defaultJavaFormatSpaceBeforeParenthesis = Boolean.FALSE;
    public static final Boolean defaultJavaFormatSpaceAfterComma = Boolean.TRUE;
    public static final Boolean defaultJavaFormatNewlineBeforeBrace = Boolean.FALSE;
    public static final Boolean defaultJavaFormatLeadingSpaceInComment = Boolean.FALSE;
    public static final Boolean defaultJavaFormatLeadingStarInComment = Boolean.TRUE;
    public static final Integer defaultJavaFormatStatementContinuationIndent = new Integer(8);

    public static final Boolean defaultPairCharactersCompletion = Boolean.TRUE;

    /** @deprecated */
    public static final Boolean defaultFormatSpaceBeforeParenthesis = defaultJavaFormatSpaceBeforeParenthesis;
    /** @deprecated */
    public static final Boolean defaultFormatSpaceAfterComma = defaultJavaFormatSpaceAfterComma;
    /** @deprecated */
    public static final Boolean defaultFormatNewlineBeforeBrace = defaultJavaFormatNewlineBeforeBrace;
    /** @deprecated */
    public static final Boolean defaultFormatLeadingSpaceInComment = defaultJavaFormatLeadingSpaceInComment;
    
    public static final Boolean defaultCodeFoldingEnable = Boolean.TRUE;
    public static final Boolean defaultCodeFoldingCollapseMethod = Boolean.FALSE;
    public static final Boolean defaultCodeFoldingCollapseInnerClass = Boolean.FALSE;
    public static final Boolean defaultCodeFoldingCollapseImport = Boolean.FALSE;
    public static final Boolean defaultCodeFoldingCollapseJavadoc = Boolean.FALSE;
    public static final Boolean defaultCodeFoldingCollapseInitialComment = Boolean.FALSE;

    public static final Boolean defaultGotoClassCaseSensitive = Boolean.FALSE;
    public static final Boolean defaultGotoClassShowInnerClasses = Boolean.FALSE;
    public static final Boolean defaultGotoClassShowLibraryClasses = Boolean.TRUE;


    public static final Acceptor defaultIndentHotCharsAcceptor
        = new Acceptor() {
            public boolean accept(char ch) {
                switch (ch) {
                    case '{':
                    case '}':
                        return true;
                }

                return false;
            }
        };


    public static final String defaultWordMatchStaticWords
    = "Exception IntrospectionException FileNotFoundException IOException" // NOI18N
      + " ArrayIndexOutOfBoundsException ClassCastException ClassNotFoundException" // NOI18N
      + " CloneNotSupportedException NullPointerException NumberFormatException" // NOI18N
      + " SQLException IllegalAccessException IllegalArgumentException"; // NOI18N

    public static Map getJavaAbbrevMap() {
        Map javaAbbrevMap = new TreeMap();
//        javaAbbrevMap.put("sout", "System.out.println(\"${cursor}\");"); // NOI18N
//        javaAbbrevMap.put("serr", "System.err.println(\"${cursor}\");"); // NOI18N
              
// Code templates -- ${condition} is replaced with the highlighted string "condition"
//        javaAbbrevMap.put("dowhile", // NOI18N
//                "do {\n" // NOI18N
 //               + "    ${cursor}\n" // NOI18N
 //               + "} while (${condition});" // NOI18N
 //       );
        return javaAbbrevMap;
    }

    public static MultiKeyBinding[] getJavaKeyBindings() {
        int MENU_MASK = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        
        return new MultiKeyBinding[] {
                   new MultiKeyBinding(
                       new KeyStroke[] {
                           KeyStroke.getKeyStroke(KeyEvent.VK_J, MENU_MASK),
                           KeyStroke.getKeyStroke(KeyEvent.VK_D, 0)
                       },
                       "macro-debug-var" // NOI18N
                   ),
                   new MultiKeyBinding(
                       KeyStroke.getKeyStroke(KeyEvent.VK_T, MENU_MASK | InputEvent.SHIFT_MASK),
                       ExtKit.commentAction
                   ),
                  new MultiKeyBinding(
                      KeyStroke.getKeyStroke(KeyEvent.VK_D, MENU_MASK | InputEvent.SHIFT_MASK),
                      ExtKit.uncommentAction
                  )
               };
    }
    
    public static Map getJavaMacroMap() {
        Map javaMacroMap = new HashMap();
        javaMacroMap.put( "debug-var", "select-identifier copy-to-clipboard " + // NOI18N
                "caret-end-line insert-break \"System.err.println(\\\"\"" + 
                "paste-from-clipboard \" = \\\" + \" paste-from-clipboard \" );" ); // NOI18N
        
        return javaMacroMap;
    }

    static class JavaTokenColoringInitializer
    extends SettingsUtil.TokenColoringInitializer {

        Font boldFont = SettingsDefaults.defaultFont.deriveFont(Font.BOLD);
        Font italicFont = SettingsDefaults.defaultFont.deriveFont(Font.ITALIC);
        Settings.Evaluator boldSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.BOLD);
        Settings.Evaluator italicSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.ITALIC);
        Settings.Evaluator lightGraySubst = new SettingsUtil.ForeColorPrintColoringEvaluator(new Color(120, 120, 120));

        Coloring commentColoring = new Coloring(null, new Color(115, 115, 115), null);

        Coloring numbersColoring = new Coloring(null, new Color(120, 0, 0), null);

        public JavaTokenColoringInitializer() {
            super(PvwaveTokenContext.context);
        }

        public Object getTokenColoring(TokenContextPath tokenContextPath,
        TokenCategory tokenIDOrCategory, boolean printingSet) {
            if (!printingSet) {
                switch (tokenIDOrCategory.getNumericID()) {
                    case PvwaveTokenContext.WHITESPACE_ID:
                    case PvwaveTokenContext.IDENTIFIER_ID:
                    case PvwaveTokenContext.OPERATORS_ID:
                        return SettingsDefaults.emptyColoring;

                    case PvwaveTokenContext.ERRORS_ID:
                        return new Coloring(null, Color.white, Color.red);

                    case PvwaveTokenContext.KEYWORDS_ID:
                        return new Coloring(boldFont, Coloring.FONT_MODE_APPLY_STYLE,
                            new Color(0, 0, 153), null);

                    case PvwaveTokenContext.PVFUNCTIONS_ID:
                        return new Coloring(boldFont, Coloring.FONT_MODE_APPLY_STYLE,
                            new Color(0, 111, 0), null);

                   case PvwaveTokenContext.USERFUNCTIONS_ID:
                        return new Coloring(boldFont, Coloring.FONT_MODE_APPLY_STYLE,
                            new Color(0,111,0), null);

                    case PvwaveTokenContext.LINE_COMMENT_ID:
                    case PvwaveTokenContext.BLOCK_COMMENT_ID:
                        return commentColoring;

                    case PvwaveTokenContext.CHAR_LITERAL_ID:
                        return new Coloring(null, new Color(0, 111, 0), null);

                    case PvwaveTokenContext.STRING_LITERAL_ID:
                        return new Coloring(null, new Color(153, 0, 107), null);

                    case PvwaveTokenContext.NUMERIC_LITERALS_ID:
                        return numbersColoring;

                    case PvwaveTokenContext.ANNOTATION_ID: // JDK 1.5 annotations
                        return new Coloring(null, new Color(0, 111, 0), null);

                }

            } else { // printing set
                switch (tokenIDOrCategory.getNumericID()) {
                    case PvwaveTokenContext.LINE_COMMENT_ID:
                    case PvwaveTokenContext.BLOCK_COMMENT_ID:
                         return lightGraySubst; // print fore color will be gray

                    default:
                         return SettingsUtil.defaultPrintColoringEvaluator;
                }

            }

            return null;

        }

    }

    static class JavaLayerTokenColoringInitializer
    extends SettingsUtil.TokenColoringInitializer {

        Font boldFont = SettingsDefaults.defaultFont.deriveFont(Font.BOLD);
        Settings.Evaluator italicSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.ITALIC);

        public JavaLayerTokenColoringInitializer() {
            super(JavaLayerTokenContext.context);
        }

        public Object getTokenColoring(TokenContextPath tokenContextPath,
        TokenCategory tokenIDOrCategory, boolean printingSet) {
            if (!printingSet) {
                switch (tokenIDOrCategory.getNumericID()) {
                    case JavaLayerTokenContext.METHOD_ID:
                        return new Coloring(boldFont, Coloring.FONT_MODE_APPLY_STYLE,
                            null, null);

                }

            } else { // printing set
                switch (tokenIDOrCategory.getNumericID()) {
                    case JavaLayerTokenContext.METHOD_ID:
                        return italicSubst;

                    default:
                         return SettingsUtil.defaultPrintColoringEvaluator;
                }

            }

            return null;
        }

    }

}
