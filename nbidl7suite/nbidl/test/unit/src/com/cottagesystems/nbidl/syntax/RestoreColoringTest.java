/*
 * RestoreColoringTest.java
 * JUnit based test
 *
 * Created on August 12, 2007, 8:11 AM
 */

package com.cottagesystems.nbidl.syntax;

import junit.framework.*;
import org.netbeans.editor.Settings;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbBundle;

/**
 *
 * @author jbf
 */
public class RestoreColoringTest extends TestCase {
    
    public RestoreColoringTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(RestoreColoringTest.class);
        
        return suite;
    }

    /**
     * Test of restored method, of class com.cottagesystems.nbidl.syntax.RestoreColoring.
     */
    public void testRestored() {
        System.out.println("restored");
        
        RestoreColoring instance = new RestoreColoring();
        
        instance.restored();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of uninstalled method, of class com.cottagesystems.nbidl.syntax.RestoreColoring.
     */
    public void testUninstalled() {
        System.out.println("uninstalled");
        
        RestoreColoring instance = new RestoreColoring();
        
        instance.uninstalled();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addInitializer method, of class com.cottagesystems.nbidl.syntax.RestoreColoring.
     */
    public void testAddInitializer() {
        System.out.println("addInitializer");
        
        RestoreColoring instance = new RestoreColoring();
        
        instance.addInitializer();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of installOptions method, of class com.cottagesystems.nbidl.syntax.RestoreColoring.
     */
    public void testInstallOptions() {
        System.out.println("installOptions");
        
        RestoreColoring instance = new RestoreColoring();
        
        instance.installOptions();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of uninstallOptions method, of class com.cottagesystems.nbidl.syntax.RestoreColoring.
     */
    public void testUninstallOptions() {
        System.out.println("uninstallOptions");
        
        RestoreColoring instance = new RestoreColoring();
        
        instance.uninstallOptions();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
