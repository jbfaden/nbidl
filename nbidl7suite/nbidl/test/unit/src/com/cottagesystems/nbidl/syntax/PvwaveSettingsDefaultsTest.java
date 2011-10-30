/*
 * PvwaveSettingsDefaultsTest.java
 * JUnit based test
 *
 * Created on August 12, 2007, 8:11 AM
 */

package com.cottagesystems.nbidl.syntax;

import junit.framework.*;
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
 *
 * @author jbf
 */
public class PvwaveSettingsDefaultsTest extends TestCase {
    
    public PvwaveSettingsDefaultsTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PvwaveSettingsDefaultsTest.class);
        
        return suite;
    }

    /**
     * Test of getJavaAbbrevMap method, of class com.cottagesystems.nbidl.syntax.PvwaveSettingsDefaults.
     */
    public void testGetJavaAbbrevMap() {
        System.out.println("getJavaAbbrevMap");
        
        Map expResult = null;
        Map result = PvwaveSettingsDefaults.getJavaAbbrevMap();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getJavaKeyBindings method, of class com.cottagesystems.nbidl.syntax.PvwaveSettingsDefaults.
     */
    public void testGetJavaKeyBindings() {
        System.out.println("getJavaKeyBindings");
        
        MultiKeyBinding[] expResult = null;
        MultiKeyBinding[] result = PvwaveSettingsDefaults.getJavaKeyBindings();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getJavaMacroMap method, of class com.cottagesystems.nbidl.syntax.PvwaveSettingsDefaults.
     */
    public void testGetJavaMacroMap() {
        System.out.println("getJavaMacroMap");
        
        Map expResult = null;
        Map result = PvwaveSettingsDefaults.getJavaMacroMap();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
