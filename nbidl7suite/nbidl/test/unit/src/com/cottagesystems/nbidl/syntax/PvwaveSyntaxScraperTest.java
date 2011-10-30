/*
 * PvwaveSyntaxScraperTest.java
 * JUnit based test
 *
 * Created on August 12, 2007, 8:10 AM
 */

package com.cottagesystems.nbidl.syntax;

import junit.framework.*;
import com.cottagesystems.nbidl.dataobject.ProceduresFile;
import java.net.URL;

/**
 *
 * @author jbf
 */
public class PvwaveSyntaxScraperTest extends TestCase {
    
    public PvwaveSyntaxScraperTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PvwaveSyntaxScraperTest.class);
        
        return suite;
    }

    public void testScrape() throws Exception {
        System.out.println("parse");
        
        URL url= this.getClass().getResource("testclass.pro");
        
        ProceduresFile expResult = null;
        ProceduresFile result = PvwaveSyntaxScraper.parse(url.openStream(),1000);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");        
    }
    
    /**
     * Test of parse method, of class com.cottagesystems.nbidl.syntax.PvwaveSyntaxScraper.
     */
    public void testParse() throws Exception {
        System.out.println("parse");
        
        URL url= this.getClass().getResource("testparse.pro");
        
        ProceduresFile expResult = null;
        ProceduresFile result = PvwaveSyntaxScraper.parse(url.openStream(),1000);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
