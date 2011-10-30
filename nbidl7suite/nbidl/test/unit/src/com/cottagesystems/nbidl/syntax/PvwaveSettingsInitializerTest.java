/*
 * PvwaveSettingsInitializerTest.java
 * JUnit based test
 *
 * Created on August 12, 2007, 8:10 AM
 */

package com.cottagesystems.nbidl.syntax;

import junit.framework.*;
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
 *
 * @author jbf
 */
public class PvwaveSettingsInitializerTest extends TestCase {
    
    public PvwaveSettingsInitializerTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PvwaveSettingsInitializerTest.class);
        
        return suite;
    }

    /**
     * Test of updateSettingsMap method, of class com.cottagesystems.nbidl.syntax.PvwaveSettingsInitializer.
     */
    public void testUpdateSettingsMap() {
        System.out.println("updateSettingsMap");
        
        Class kitClass = null;
        Map settingsMap = null;
        PvwaveSettingsInitializer instance = new PvwaveSettingsInitializer();
        
        instance.updateSettingsMap(kitClass, settingsMap);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
