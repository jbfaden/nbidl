/*
 * PvwaveSettingsNamesTest.java
 * JUnit based test
 *
 * Created on August 12, 2007, 8:11 AM
 */

package com.cottagesystems.nbidl.syntax;

import junit.framework.*;
import org.netbeans.editor.ext.ExtSettingsNames;

/**
 *
 * @author jbf
 */
public class PvwaveSettingsNamesTest extends TestCase {
    
    public PvwaveSettingsNamesTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PvwaveSettingsNamesTest.class);
        
        return suite;
    }
    
}
