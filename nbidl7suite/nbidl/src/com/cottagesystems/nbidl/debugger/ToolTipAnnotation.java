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

import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.editor.BaseDocument;

import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.text.Annotation;
import org.openide.text.DataEditorSupport;
import org.openide.text.Line;
import org.openide.text.NbDocument;
import org.openide.text.Line.Part;
import org.openide.util.RequestProcessor;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.Watch;

import org.openide.nodes.Node;
import org.openide.windows.TopComponent;
import org.netbeans.editor.Utilities;

public class ToolTipAnnotation extends Annotation implements Runnable {

    private String expression;

    public String getShortDescription () {
        DebuggerEngine currentEngine = DebuggerManager.getDebuggerManager ().
            getCurrentEngine ();
        if (currentEngine == null) return null;
        PvwaveDebugger d = (PvwaveDebugger) currentEngine.lookupFirst(null, PvwaveDebugger.class);
        if (d == null) return "unable to find IDL debugger";

        Part lp = (Part)
            getAttachedAnnotatable();
        if (lp==null) return null;
        Line line = lp.getLine ();
        DataObject dob = DataEditorSupport.findDataObject (line);
        if (dob == null) return null;
        EditorCookie ec = 
            (EditorCookie) dob.getCookie 
            (EditorCookie.class);

        if (ec == null) return null;
        try {
            StyledDocument doc = ec.openDocument ();                    
            JEditorPane ep = getCurrentEditor ();
            if (ep == null) return null;
            expression = getIdentifier (
                doc, 
                ep,
                NbDocument.findLineOffset (
                    doc,
                    lp.getLine ().getLineNumber ()
                ) + lp.getColumn ()
            );
            if (expression == null) return null;
            if ( expression.equals("$") ) return null;
            if ( expression.contains("\n") ) return null;
            RequestProcessor.getDefault ().post (this);                    
        } catch (IOException e) {
        }
        return null;
    }

    public void run () {
        if (expression == null) return;
        DebuggerEngine currentEngine = DebuggerManager.getDebuggerManager ().
            getCurrentEngine ();
        if (currentEngine == null) return;
        // HERE'S HOW TO GET THE CURRENT DEBUGGER SESSION.
        PvwaveDebugger d = (PvwaveDebugger) currentEngine.lookupFirst 
            (null, PvwaveDebugger.class);
        if (d == null) return;
        String value = d.evaluate (expression);
        if ( value.contains("% Syntax error" ) ) return;
        if ( value == null ||
             value.equals (expression)
        ) return;
        if ( value.length()>750 ) {
            value= value.substring(0,750)+"\n...";
        }
        String toolTipText = expression + " = " + value;
        firePropertyChange (PROP_SHORT_DESCRIPTION, null, toolTipText);
    }

    public String getAnnotationType () {
        return null; // Currently return null annotation type
    }

    private static String getIdentifier (
        StyledDocument doc, 
        JEditorPane ep, 
        int offset
    ) {
        String t = null;
        if ( (ep.getSelectionStart () <= offset) &&
             (offset <= ep.getSelectionEnd ())
        )   t = ep.getSelectedText ();
        if ( t==null ) {
            String selection;
            try {
                selection = Utilities.getIdentifier((BaseDocument) doc, offset);
                t= selection;
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            
        }
        if (t != null) return t; else return null;
        
    }
    
    /** 
     * Returns current editor component instance.
     *
     * Used in: ToolTipAnnotation
     */
    static JEditorPane getCurrentEditor () {
        EditorCookie e = getCurrentEditorCookie ();
        if (e == null) return null;
        JEditorPane[] op = e.getOpenedPanes ();
        if ((op == null) || (op.length < 1)) return null;
        return op [0];
    }
    
    /** 
     * Returns current editor component instance.
     *
     * @return current editor component instance
     */
    private static EditorCookie getCurrentEditorCookie () {
        Node[] nodes = TopComponent.getRegistry ().getActivatedNodes ();
        if ( (nodes == null) ||
             (nodes.length != 1) ) return null;
        Node n = nodes [0];
        return (EditorCookie) n.getCookie (
            EditorCookie.class
        );
    }
}

