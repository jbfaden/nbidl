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

import com.cottagesystems.nbidl.syntax.PvwaveSettingsDefaults;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.SettingsNames;
import org.netbeans.editor.SettingsDefaults;
import org.netbeans.editor.MultiKeyBinding;
import org.netbeans.editor.BaseKit;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.ext.ExtSettingsNames;

/**
* Extended settings for Java.
*
* @author Miloslav Metelka
* @version 1.00
*/

public class PvwaveSettingsInitializer extends Settings.AbstractInitializer {

    /** Name assigned to initializer */
    public static final String NAME = "pvwave-settings-initializer"; // NOI18N   

    /** Construct new java-settings-initializer.        
    */
    public PvwaveSettingsInitializer() {
        super(NAME);
    }

    /** Update map filled with the settings.
    * @param kitClass kit class for which the settings are being updated.
    *   It is always non-null value.
    * @param settingsMap map holding [setting-name, setting-value] pairs.
    *   The map can be empty if this is the first initializer
    *   that updates it or if no previous initializers updated it.
    */
    public void updateSettingsMap(Class kitClass, Map settingsMap) {

        // Update java colorings
        if (kitClass == BaseKit.class) {

            new PvwaveSettingsDefaults.JavaTokenColoringInitializer().updateSettingsMap(kitClass, settingsMap);
            new PvwaveSettingsDefaults.JavaLayerTokenColoringInitializer().updateSettingsMap(kitClass, settingsMap);

        }

        if (kitClass == PvwaveEditorKit.class ) {

            SettingsUtil.updateListSetting(settingsMap, SettingsNames.KEY_BINDING_LIST,
                PvwaveSettingsDefaults.getJavaKeyBindings());

            SettingsUtil.updateListSetting(settingsMap, SettingsNames.TOKEN_CONTEXT_LIST,
                new TokenContext[] {
                    PvwaveTokenContext.context,
                }
            );

            settingsMap.put(SettingsNames.ABBREV_MAP, PvwaveSettingsDefaults.getJavaAbbrevMap());

            settingsMap.put(SettingsNames.MACRO_MAP, PvwaveSettingsDefaults.getJavaMacroMap());

            settingsMap.put(ExtSettingsNames.CARET_SIMPLE_MATCH_BRACE,
                            PvwaveSettingsDefaults.defaultCaretSimpleMatchBrace);

            settingsMap.put(ExtSettingsNames.HIGHLIGHT_MATCH_BRACE,
                            PvwaveSettingsDefaults.defaultHighlightMatchBrace);

            settingsMap.put(SettingsNames.IDENTIFIER_ACCEPTOR,
                            PvwaveSettingsDefaults.defaultIdentifierAcceptor);

            settingsMap.put(SettingsNames.ABBREV_RESET_ACCEPTOR,
                            PvwaveSettingsDefaults.defaultAbbrevResetAcceptor);

            settingsMap.put(SettingsNames.WORD_MATCH_MATCH_CASE,
                            PvwaveSettingsDefaults.defaultWordMatchMatchCase);

            settingsMap.put(SettingsNames.WORD_MATCH_STATIC_WORDS,
                            PvwaveSettingsDefaults.defaultWordMatchStaticWords);

            // Formatting settings
            settingsMap.put(PvwaveSettingsNames.JAVA_FORMAT_SPACE_BEFORE_PARENTHESIS,
                            PvwaveSettingsDefaults.defaultJavaFormatSpaceBeforeParenthesis);

            settingsMap.put(PvwaveSettingsNames.JAVA_FORMAT_SPACE_AFTER_COMMA,
                            PvwaveSettingsDefaults.defaultJavaFormatSpaceAfterComma);

            settingsMap.put(PvwaveSettingsNames.JAVA_FORMAT_NEWLINE_BEFORE_BRACE,
                            PvwaveSettingsDefaults.defaultJavaFormatNewlineBeforeBrace);

            settingsMap.put(PvwaveSettingsNames.JAVA_FORMAT_LEADING_SPACE_IN_COMMENT,
                            PvwaveSettingsDefaults.defaultJavaFormatLeadingSpaceInComment);

            settingsMap.put(PvwaveSettingsNames.JAVA_FORMAT_LEADING_STAR_IN_COMMENT,
                            PvwaveSettingsDefaults.defaultJavaFormatLeadingStarInComment);

            settingsMap.put(PvwaveSettingsNames.INDENT_HOT_CHARS_ACCEPTOR,
                            PvwaveSettingsDefaults.defaultIndentHotCharsAcceptor);

            settingsMap.put(ExtSettingsNames.REINDENT_WITH_TEXT_BEFORE,
                            Boolean.FALSE);

	    settingsMap.put(PvwaveSettingsNames.PAIR_CHARACTERS_COMPLETION,
			    PvwaveSettingsDefaults.defaultPairCharactersCompletion);

            settingsMap.put(PvwaveSettingsNames.GOTO_CLASS_CASE_SENSITIVE,
                            PvwaveSettingsDefaults.defaultGotoClassCaseSensitive);

            settingsMap.put(PvwaveSettingsNames.GOTO_CLASS_SHOW_INNER_CLASSES,
                            PvwaveSettingsDefaults.defaultGotoClassShowInnerClasses);

            settingsMap.put(PvwaveSettingsNames.GOTO_CLASS_SHOW_LIBRARY_CLASSES,
                            PvwaveSettingsDefaults.defaultGotoClassShowLibraryClasses);
        }

    }

}
