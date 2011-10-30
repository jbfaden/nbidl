/*
 * PvwaveSyntaxTest.java
 * JUnit based test
 *
 * Created on August 12, 2007, 8:11 AM
 */

package com.cottagesystems.nbidl.syntax;

import junit.framework.*;
import com.cottagesystems.nbidl.syntax.UserRoutinesDataBase;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;

/**
 *
 * @author jbf
 */
public class PvwaveSyntaxTest extends TestCase {
    
    public PvwaveSyntaxTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PvwaveSyntaxTest.class);
        
        return suite;
    }

    /**
     * Test of parseToken method, of class com.cottagesystems.nbidl.syntax.PvwaveSyntax.
     */
    public void testParseToken() {
        System.out.println("parseToken");
        
        PvwaveSyntax instance = new PvwaveSyntax();
        
        TokenID expResult = null;
        TokenID result = instance.parseToken();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStateName method, of class com.cottagesystems.nbidl.syntax.PvwaveSyntax.
     */
    public void testGetStateName() {
        System.out.println("getStateName");
        
        int stateNumber = 0;
        PvwaveSyntax instance = new PvwaveSyntax();
        
        String expResult = "";
        String result = instance.getStateName(stateNumber);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of matchKeyword method, of class com.cottagesystems.nbidl.syntax.PvwaveSyntax.
     */
    public void testMatchKeyword() {
        System.out.println("matchKeyword");
        
        char[] buffer = null;
        int offset = 0;
        int len = 0;
        PvwaveSyntax instance = new PvwaveSyntax();
        
        TokenID expResult = null;
        TokenID result = instance.matchKeyword(buffer, offset, len);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLineOffset method, of class com.cottagesystems.nbidl.syntax.PvwaveSyntax.
     */
    public void testGetLineOffset() {
        System.out.println("getLineOffset");
        
        PvwaveSyntax instance = new PvwaveSyntax();
        
        int expResult = 0;
        int result = instance.getLineOffset();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLineOffset method, of class com.cottagesystems.nbidl.syntax.PvwaveSyntax.
     */
    public void testSetLineOffset() {
        System.out.println("setLineOffset");
        
        int offset = 0;
        PvwaveSyntax instance = new PvwaveSyntax();
        
        instance.setLineOffset(offset);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
