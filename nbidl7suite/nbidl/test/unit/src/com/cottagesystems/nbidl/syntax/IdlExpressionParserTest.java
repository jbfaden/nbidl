/*
 * IdlExpressionParserTest.java
 * JUnit based test
 *
 * Created on August 12, 2007, 8:11 AM
 */

package com.cottagesystems.nbidl.syntax;

import junit.framework.*;
import com.cottagesystems.nbidl.model.IdlValue;
import com.cottagesystems.nbidl.model.PrimativeType;
import java.util.Stack;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;

/**
 *
 * @author jbf
 */
public class IdlExpressionParserTest extends TestCase {
    
    public IdlExpressionParserTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(IdlExpressionParserTest.class);
        
        return suite;
    }

    /**
     * Test of nextExpression method, of class com.cottagesystems.nbidl.syntax.IdlExpressionParser.
     */
    public void testNextExpression() {
        System.out.println("nextExpression");
        
        char[] buffer = null;
        Syntax syntax = null;
        IdlExpressionParser instance = new IdlExpressionParser();
        
        IdlValue expResult = null;
        IdlValue result = instance.nextExpression(buffer, syntax);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLastToken method, of class com.cottagesystems.nbidl.syntax.IdlExpressionParser.
     */
    public void testGetLastToken() {
        System.out.println("getLastToken");
        
        IdlExpressionParser instance = new IdlExpressionParser();
        
        TokenID expResult = null;
        TokenID result = instance.getLastToken();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
