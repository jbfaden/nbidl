/*
 * SyntaxSuite.java
 * JUnit based test
 *
 * Created on August 12, 2007, 8:11 AM
 */

package com.cottagesystems.nbidl.syntax;

import junit.framework.*;

/**
 *
 * @author jbf
 */
public class SyntaxSuite extends TestCase {
    
    public SyntaxSuite(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * suite method automatically generated by JUnit module
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("SyntaxSuite");
        suite.addTest(com.cottagesystems.nbidl.syntax.PvwaveSyntaxScraperTest.suite());
        suite.addTest(com.cottagesystems.nbidl.syntax.PvwaveSettingsInitializerTest.suite());
        suite.addTest(com.cottagesystems.nbidl.syntax.PvwaveSettingsDefaultsTest.suite());
        suite.addTest(com.cottagesystems.nbidl.syntax.PvwaveTokenContextTest.suite());
        suite.addTest(com.cottagesystems.nbidl.syntax.PvwaveSettingsNamesTest.suite());
        suite.addTest(com.cottagesystems.nbidl.syntax.PvwaveSyntaxTest.suite());
        suite.addTest(com.cottagesystems.nbidl.syntax.UserRoutinesDataBaseTest.suite());
        suite.addTest(com.cottagesystems.nbidl.syntax.PvwaveEditorKitTest.suite());
        suite.addTest(com.cottagesystems.nbidl.syntax.RestoreColoringTest.suite());
        suite.addTest(com.cottagesystems.nbidl.syntax.JavaLayerTokenContextTest.suite());
        suite.addTest(com.cottagesystems.nbidl.syntax.IDLRoutinesDataBaseTest.suite());
        suite.addTest(com.cottagesystems.nbidl.syntax.ModelSupportTest.suite());
        suite.addTest(com.cottagesystems.nbidl.syntax.IdlExpressionParserTest.suite());
        return suite;
    }
    
}
