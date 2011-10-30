/*
 * UserRoutinesDataBaseTest.java
 * JUnit based test
 *
 * Created on August 12, 2007, 8:11 AM
 */

package com.cottagesystems.nbidl.syntax;

import junit.framework.*;
import com.cottagesystems.nbidl.completion.*;
import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.dataobject.ProceduresFile;
import com.cottagesystems.nbidl.model.IdlClass;
import com.cottagesystems.nbidl.model.IdlSession;
import com.cottagesystems.nbidl.pathExplorer.Tab;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.text.JTextComponent;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.editor.TokenID;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.DataShadow;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;

/**
 *
 * @author jbf
 */
public class UserRoutinesDataBaseTest extends TestCase {
    
    public UserRoutinesDataBaseTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(UserRoutinesDataBaseTest.class);
        
        return suite;
    }

    /**
     * Test of getInstance method, of class com.cottagesystems.nbidl.syntax.UserRoutinesDataBase.
     */
    public void testGetInstance() {
        System.out.println("getInstance");
        
        UserRoutinesDataBase expResult = null;
        UserRoutinesDataBase result = UserRoutinesDataBase.getInstance();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of scanFile method, of class com.cottagesystems.nbidl.syntax.UserRoutinesDataBase.
     */
    public void testScanFile() throws Exception {
        System.out.println("scanFile");
        
        FileObject file = null;
        OutputWriter out = null;
        UserRoutinesDataBase instance = null;
        
        instance.scanFile(file, out);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of scan method, of class com.cottagesystems.nbidl.syntax.UserRoutinesDataBase.
     */
    public void testScan() {
        System.out.println("scan");
        
        UserRoutinesDataBase instance = null;
        
        Task expResult = null;
        Task result = instance.scan();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProcedure method, of class com.cottagesystems.nbidl.syntax.UserRoutinesDataBase.
     */
    public void testGetProcedure() {
        System.out.println("getProcedure");
        
        String name = "";
        UserRoutinesDataBase instance = null;
        
        Procedure expResult = null;
        Procedure result = instance.getProcedure(name);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of matchKeyword method, of class com.cottagesystems.nbidl.syntax.UserRoutinesDataBase.
     */
    public void testMatchKeyword() {
        System.out.println("matchKeyword");
        
        char[] buffer = null;
        int offset = 0;
        int len = 0;
        UserRoutinesDataBase instance = null;
        
        TokenID expResult = null;
        TokenID result = instance.matchKeyword(buffer, offset, len);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMatching method, of class com.cottagesystems.nbidl.syntax.UserRoutinesDataBase.
     */
    public void testGetMatching() {
        System.out.println("getMatching");
        
        String startsWith = "";
        boolean pros = true;
        boolean funcs = true;
        UserRoutinesDataBase instance = null;
        
        List expResult = null;
        List result = instance.getMatching(startsWith, pros, funcs);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class com.cottagesystems.nbidl.syntax.UserRoutinesDataBase.
     */
    public void testToString() {
        System.out.println("toString");
        
        UserRoutinesDataBase instance = null;
        
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of rescan method, of class com.cottagesystems.nbidl.syntax.UserRoutinesDataBase.
     */
    public void testRescan() {
        System.out.println("rescan");
        
        UserRoutinesDataBase instance = null;
        
        instance.rescan();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of showUsagesSummary method, of class com.cottagesystems.nbidl.syntax.UserRoutinesDataBase.
     */
    public void testShowUsagesSummary() throws Exception {
        System.out.println("showUsagesSummary");
        
        UserRoutinesDataBase instance = null;
        
        instance.showUsagesSummary();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of showUsages method, of class com.cottagesystems.nbidl.syntax.UserRoutinesDataBase.
     */
    public void testShowUsages() {
        System.out.println("showUsages");
        
        String keyword = "";
        UserRoutinesDataBase instance = null;
        
        instance.showUsages(keyword);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
