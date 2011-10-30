/*
 * PvwaveTokenContextTest.java
 * JUnit based test
 *
 * Created on August 12, 2007, 8:11 AM
 */

package com.cottagesystems.nbidl.syntax;

import junit.framework.*;
import java.util.HashMap;
import org.netbeans.editor.BaseTokenCategory;
import org.netbeans.editor.BaseTokenID;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.BaseImageTokenID;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.Utilities;

/**
 *
 * @author jbf
 */
public class PvwaveTokenContextTest extends TestCase {
    
    public PvwaveTokenContextTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PvwaveTokenContextTest.class);
        
        return suite;
    }

    /**
     * Test of isType method, of class com.cottagesystems.nbidl.syntax.PvwaveTokenContext.
     */
    public void testIsType() {
        System.out.println("isType");
        
        TokenID keywordTokenID = null;
        
        boolean expResult = true;
        boolean result = PvwaveTokenContext.isType(keywordTokenID);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isTypeOrVoid method, of class com.cottagesystems.nbidl.syntax.PvwaveTokenContext.
     */
    public void testIsTypeOrVoid() {
        System.out.println("isTypeOrVoid");
        
        TokenID keywordTokenID = null;
        
        boolean expResult = true;
        boolean result = PvwaveTokenContext.isTypeOrVoid(keywordTokenID);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getKeyword method, of class com.cottagesystems.nbidl.syntax.PvwaveTokenContext.
     */
    public void testGetKeyword() {
        System.out.println("getKeyword");
        
        String s = "";
        
        TokenID expResult = null;
        TokenID result = PvwaveTokenContext.getKeyword(s);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
