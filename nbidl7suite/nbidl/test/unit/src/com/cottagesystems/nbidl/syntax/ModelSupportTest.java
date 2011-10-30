/*
 * ModelSupportTest.java
 * JUnit based test
 *
 * Created on August 12, 2007, 8:11 AM
 */

package com.cottagesystems.nbidl.syntax;

import junit.framework.*;
import com.cottagesystems.nbidl.dataobject.Procedure;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 *
 * @author jbf
 */
public class ModelSupportTest extends TestCase {
    
    public ModelSupportTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ModelSupportTest.class);
        
        return suite;
    }

    /**
     * Test of parseIDLDocumentationItem method, of class com.cottagesystems.nbidl.syntax.ModelSupport.
     */
    public void testParseIDLDocumentationItem() throws Exception {
        System.out.println("parseIDLDocumentationItem");
        
        File f = null;
        Procedure p = null;
        
        ModelSupport.parseIDLDocumentationItem(f, p);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
