/*
 * IDLRoutinesDataBaseTest.java
 * JUnit based test
 *
 * Created on August 12, 2007, 8:11 AM
 */

package com.cottagesystems.nbidl.syntax;

import junit.framework.*;
import com.cottagesystems.nbidl.completion.PvwaveCompletionItem;
import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.model.IdlClass;
import com.cottagesystems.nbidl.model.IdlProperty;
import com.cottagesystems.nbidl.model.IdlSession;
import com.cottagesystems.nbidl.options.PvwaveSettingUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.editor.TokenID;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author jbf
 */
public class IDLRoutinesDataBaseTest extends TestCase {
    
    public IDLRoutinesDataBaseTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(IDLRoutinesDataBaseTest.class);
        
        return suite;
    }

    /**
     * Test of rescan method, of class com.cottagesystems.nbidl.syntax.IDLRoutinesDataBase.
     */
    public void testRescan() {
        System.out.println("rescan");
        
        IDLRoutinesDataBase instance = null;
        
        instance.rescan();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInstance method, of class com.cottagesystems.nbidl.syntax.IDLRoutinesDataBase.
     */
    public void testGetInstance() {
        System.out.println("getInstance");
        
        IDLRoutinesDataBase expResult = null;
        IDLRoutinesDataBase result = IDLRoutinesDataBase.getInstance();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProcedure method, of class com.cottagesystems.nbidl.syntax.IDLRoutinesDataBase.
     */
    public void testGetProcedure() {
        System.out.println("getProcedure");
        
        String name = "";
        IDLRoutinesDataBase instance = null;
        
        Procedure expResult = null;
        Procedure result = instance.getProcedure(name);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSession method, of class com.cottagesystems.nbidl.syntax.IDLRoutinesDataBase.
     */
    public void testGetSession() {
        System.out.println("getSession");
        
        IDLRoutinesDataBase instance = null;
        
        IdlSession expResult = null;
        IdlSession result = instance.getSession();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of matchKeyword method, of class com.cottagesystems.nbidl.syntax.IDLRoutinesDataBase.
     */
    public void testMatchKeyword() {
        System.out.println("matchKeyword");
        
        char[] buffer = null;
        int offset = 0;
        int len = 0;
        IDLRoutinesDataBase instance = null;
        
        TokenID expResult = null;
        TokenID result = instance.matchKeyword(buffer, offset, len);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMatching method, of class com.cottagesystems.nbidl.syntax.IDLRoutinesDataBase.
     */
    public void testGetMatching() {
        System.out.println("getMatching");
        
        String startsWith = "";
        boolean pros = true;
        boolean funcs = true;
        IDLRoutinesDataBase instance = null;
        
        List expResult = null;
        List result = instance.getMatching(startsWith, pros, funcs);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
