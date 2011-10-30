/*
 * CompletionSupportTest.java
 * JUnit based test
 *
 * Created on August 4, 2007, 6:44 AM
 */

package com.cottagesystems.nbidl.completion;

import junit.framework.*;
import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.dataobject.ProceduresFile;
import com.cottagesystems.nbidl.dataobject.ProceduresFileDataObject;
import com.cottagesystems.nbidl.debugger.PvwaveDebugger;
import com.cottagesystems.nbidl.debugger.PvwaveStop;
import com.cottagesystems.nbidl.model.IdlClass;
import com.cottagesystems.nbidl.model.IdlStruct;
import com.cottagesystems.nbidl.model.StructTag;
import com.cottagesystems.nbidl.session.Session;
import com.cottagesystems.nbidl.session.SessionSupport;
import com.cottagesystems.nbidl.syntax.IDLRoutinesDataBase;
import com.cottagesystems.nbidl.syntax.PvwaveSyntax;
import com.cottagesystems.nbidl.syntax.PvwaveSyntaxScraper;
import com.cottagesystems.nbidl.syntax.PvwaveTokenContext;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Registry;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.TokenProcessor;
import org.netbeans.editor.Utilities;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;

/**
 *
 * @author jbf
 */
public class CompletionSupportTest extends TestCase {
    
    public CompletionSupportTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getCompletionContext method, of class com.cottagesystems.nbidl.completion.CompletionSupport.
     */
    public void testGetCompletionContext() {
        System.out.println("getCompletionContext");
        
        BaseDocument doc = null;
        int pos = 0;
        CompletionSupport instance = new CompletionSupport();
        
        CompletionSupport.CompletionContext expResult = null;
        CompletionSupport.CompletionContext result = instance.getCompletionContext(doc, pos);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMessageCompletionItem method, of class com.cottagesystems.nbidl.completion.CompletionSupport.
     */
    public void testGetMessageCompletionItem() {
        System.out.println("getMessageCompletionItem");
        
        String message = "";
        
        CompletionItem expResult = null;
        CompletionItem result = CompletionSupport.getMessageCompletionItem(message);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCarotProcedure method, of class com.cottagesystems.nbidl.completion.CompletionSupport.
     */
    public void testGetCarotProcedure() {
        System.out.println("getCarotProcedure");
        
        Procedure expResult = null;
        Procedure result = CompletionSupport.getCarotProcedure();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addLiveCompletions method, of class com.cottagesystems.nbidl.completion.CompletionSupport.
     */
    public void testAddLiveCompletions() {
        System.out.println("addLiveCompletions");
        
        CompletionSupport.CompletionContext context = null;
        
        List expResult = null;
        List result = CompletionSupport.addLiveCompletions(context);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of htmlify method, of class com.cottagesystems.nbidl.completion.CompletionSupport.
     */
    public void testHtmlify() {
        System.out.println("htmlify");
        
        String str = "";
        
        String expResult = "";
        String result = CompletionSupport.htmlify(str);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of htmlifyDocumentation method, of class com.cottagesystems.nbidl.completion.CompletionSupport.
     */
    public void testHtmlifyDocumentation() {
        System.out.println("htmlifyDocumentation");
        
        String str = "";
        
        String expResult = "";
        String result = CompletionSupport.htmlifyDocumentation(str);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
