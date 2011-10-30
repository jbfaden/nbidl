/*
 * PvwaveEditorKitTest.java
 * JUnit based test
 *
 * Created on August 12, 2007, 8:11 AM
 */

package com.cottagesystems.nbidl.syntax;

import junit.framework.*;
import java.util.HashMap;
import javax.swing.text.Document;
import org.netbeans.editor.Syntax;
import org.netbeans.modules.editor.NbEditorKit;
import org.openide.ErrorManager;

/**
 *
 * @author jbf
 */
public class PvwaveEditorKitTest extends TestCase {
    
    public PvwaveEditorKitTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PvwaveEditorKitTest.class);
        
        return suite;
    }

    /**
     * Test of createSyntax method, of class com.cottagesystems.nbidl.syntax.PvwaveEditorKit.
     */
    public void testCreateSyntax() {
        System.out.println("createSyntax");
        
        Document doc = null;
        PvwaveEditorKit instance = new PvwaveEditorKit();
        
        Syntax expResult = null;
        Syntax result = instance.createSyntax(doc);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getContentType method, of class com.cottagesystems.nbidl.syntax.PvwaveEditorKit.
     */
    public void testGetContentType() {
        System.out.println("getContentType");
        
        PvwaveEditorKit instance = new PvwaveEditorKit();
        
        String expResult = "";
        String result = instance.getContentType();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }


    
}
