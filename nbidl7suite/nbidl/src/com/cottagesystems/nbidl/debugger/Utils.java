/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.cottagesystems.nbidl.debugger;

import com.cottagesystems.nbidl.debugger.breakpoints.PvwaveBreakpoint;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledDocument;
import org.netbeans.api.debugger.DebuggerManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.text.Annotatable;
import org.openide.text.Line;
import org.openide.text.NbDocument;
import org.xml.sax.InputSource;

        
        
/*
 * AntTest.java
 *
 * Created on 19. leden 2004, 20:03
 */

/**
 *
 * @author  Honza
 */
public class Utils {
            
    private static Object currentLine;
    
    static void markCurrent (final Object line) {
        unmarkCurrent ();
        
        Annotatable[] annotatables = (Annotatable[]) line;
        int i = 0, k = annotatables.length;
        
        // first line with icon in gutter
        DebuggerAnnotation[] annotations = new DebuggerAnnotation [k];
        if (annotatables [i] instanceof Line.Part)
            annotations [i] = new DebuggerAnnotation (
                DebuggerAnnotation.CURRENT_LINE_PART_ANNOTATION_TYPE,
                annotatables [i]
            );
        else
            annotations [i] = new DebuggerAnnotation (
                DebuggerAnnotation.CURRENT_LINE_ANNOTATION_TYPE,
                annotatables [i]
            );
        
        // other lines
        for (i = 1; i < k; i++)
            if (annotatables [i] instanceof Line.Part)
                annotations [i] = new DebuggerAnnotation (
                    DebuggerAnnotation.CURRENT_LINE_PART_ANNOTATION_TYPE2,
                    annotatables [i]
                );
            else
                annotations [i] = new DebuggerAnnotation (
                    DebuggerAnnotation.CURRENT_LINE_ANNOTATION_TYPE2,
                    annotatables [i]
                );
        currentLine = annotations;
        
        showLine (line);
    }
    
    static void unmarkCurrent () {
        if (currentLine != null) {
            
//            ((DebuggerAnnotation) currentLine).detach ();
            int i, k = ((DebuggerAnnotation[]) currentLine).length;
            for (i = 0; i < k; i++)
                ((DebuggerAnnotation[]) currentLine) [i].detach ();
            
            currentLine = null;
        }
    }
    
    static void showLine (final Object line) {
//        SwingUtilities.invokeLater (new Runnable () {
//            public void run () {
//                ((Line) line).show (Line.SHOW_GOTO);
//            }
//        });
        
        final Annotatable[] a = (Annotatable[]) line;
        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                if (a [0] instanceof Line)
                    ((Line) a [0]).show (Line.SHOW_GOTO);
                else
                if (a [0] instanceof Line.Part)
                    ((Line.Part) a [0]).getLine ().show (Line.SHOW_GOTO);
                else
                    throw new InternalError ();
            }
        });
    }
    
    static int getLineNumber (Object line) {
//        return ((Line) line).getLineNumber ();
        
        final Annotatable[] a = (Annotatable[]) line;
        if (a [0] instanceof Line)
            return ((Line) a [0]).getLineNumber ();
        else
        if (a [0] instanceof Line.Part)
            return ((Line.Part) a [0]).getLine ().getLineNumber ();
        else
            throw new InternalError ();
    }
    
    public static boolean contains (Object currentLine, Line line) {
        if (currentLine == null) return false;
        final Annotatable[] a = (Annotatable[]) currentLine;
        int i, k = a.length;
        for (i = 0; i < k; i++) {
            if (a [i].equals (line)) return true;
            if ( a [i] instanceof Line.Part &&
                 ((Line.Part) a [i]).getLine ().equals (line)
            ) return true;
        }
        return false;
    }

    
    private static int findIndexOf(String text, String target) {
        int index = 0;
        while ((index = text.indexOf(target, index)) > 0) {
            char c = text.charAt(index - 1);
            if (!Character.isWhitespace(c) && c != ',' && c != '\"') {
                // begins with some text => is not the target
                index++;
                continue;
            }
            if (text.length() > index + target.length()) {
                c = text.charAt(index + target.length());
                if (!Character.isWhitespace(c) && c != ',' && c != '\"') {
                    // ends with some text => is not the target
                    index++;
                    continue;
                }
            }
            break;
        }
        return index;
    }
    
    /**
     * Utility method to get a properly configured XML input source for a script.
     */
    private static InputSource createInputSource (
        FileObject fo, 
        EditorCookie editor, 
        final StyledDocument document
    ) throws IOException, BadLocationException {
        final StringWriter w = new StringWriter (document.getLength ());
        final EditorKit kit = findKit (editor.getOpenedPanes ());
        final IOException[] ioe = new IOException [1];
        final BadLocationException[] ble = new BadLocationException [1];
        document.render(new Runnable () {
            public void run() {
                try {
                    kit.write (w, document, 0, document.getLength ());
                } catch (IOException e) {
                    ioe [0] = e;
                } catch (BadLocationException e) {
                    ble [0] = e;
                }
            }
        });
        if (ioe[0] != null) {
            throw ioe [0];
        } else if (ble [0] != null) {
            throw ble [0];
        }
        InputSource in = new InputSource (new StringReader (w.toString ()));
        if (fo != null) { // #10348
            try {
                in.setSystemId (fo.getURL ().toExternalForm ());
            } catch (FileStateInvalidException e) {
                assert false : e;
            }
            // [PENDING] Ant's ProjectHelper has an elaborate set of work-
            // arounds for inconsistent parser behavior, e.g. file:foo.xml
            // works in Ant but not with Xerces parser. You must use just foo.xml
            // as the system ID. If necessary, Ant's algorithm could be copied
            // here to make the behavior match perfectly, but it ought not be necessary.
        }
        return in;
    }
    
    private static EditorKit findKit (JEditorPane[] panes) {
        EditorKit kit;
        if (panes != null) {
            kit = panes[0].getEditorKit ();
        } else {
            kit = JEditorPane.createEditorKitForContentType ("text/xml"); // NOI18N
            if (kit == null) {
                // #39301: fallback; can happen if xml/text-edit is disabled
                kit = new DefaultEditorKit ();
            }
        }
        assert kit != null;
        return kit;
    }
    
    /**
     * returns the Line that the carot is on.
     */
    public static Line getCarotLine( EditorCookie editorCookie ) {
        JEditorPane[] panes= editorCookie.getOpenedPanes();
        if ( panes.length==1 ) {
            int caretPosition= panes[0].getCaretPosition();
            StyledDocument styledDocument = editorCookie.getDocument();
            int lineNum = NbDocument.findLineNumber(styledDocument,caretPosition);
            
            Line line= editorCookie.getLineSet().getCurrent(lineNum);
            return line;
        } else {
            return null;
        }
    }

}
