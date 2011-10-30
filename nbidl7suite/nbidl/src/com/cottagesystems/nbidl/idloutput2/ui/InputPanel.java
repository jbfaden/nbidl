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
/*
 * InputPanel.java
 *
 * Created on May 14, 2004, 8:03 PM
 */

package com.cottagesystems.nbidl.idloutput2.ui;

import com.cottagesystems.nbidl.debugger.PvwaveDebugger;
import com.cottagesystems.nbidl.syntax.PvwaveEditorKit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import com.cottagesystems.nbidl.idloutput2.History;
import com.cottagesystems.nbidl.idloutput2.PvwaveController;

import org.openide.util.NbBundle;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.text.EditorKit;
import com.cottagesystems.nbidl.idloutput2.History2;
import org.netbeans.editor.BaseDocument;
import org.openide.util.Utilities;

/**
 * Panel to enable user entered input.
 *
 * @author  Tim Boudreau
 */
final class InputPanel extends JPanel implements ActionListener, FocusListener {
    public static final String ACTION_EOF = "eof"; //NOI18N
    public static final String ACTION_NEWTEXT = "text"; //NOI18N
    
    private JTextField field = new JTextField();
    private JEditorPane lineEditorPane= new JEditorPane();
    private JButton eof =
            new JButton(NbBundle.getMessage(InputPanel.class, "LBL_EOF")); //NOI18N
    
    private ActionListener listener = null;
    private JLabel lbl =
            new JLabel(NbBundle.getMessage(InputPanel.class, "LBL_INPUT")); //NOI18N;
    
    /** Creates a new instance of InputPanel */
    public InputPanel() {
        init();
        setFocusable(false);
    }
    
    public void requestFocus() {
        //field.requestFocus();
        lineEditorPane.requestFocus();
    }
    
    public boolean requestFocusInWindow() {
        //return field.requestFocusInWindow();
        return lineEditorPane.requestFocusInWindow();
        
    }
    
    History commandHistory;
    History2 commandHistory2;
    
    private Action clearCommandAction= new AbstractAction( "clearCommand" ) {
        public void actionPerformed( ActionEvent ev ) {
            lineEditorPane.setText("");
                                    
            ActionEvent e = new ActionEvent(InputPanel.this, ActionEvent.ACTION_PERFORMED,
                    ACTION_NEWTEXT);
            
            listener.actionPerformed(e);
        }
        
    };
    
    private Action executeCommandAction= new AbstractAction( "executeCommand" ) {
        public void actionPerformed( ActionEvent ev ) {
            
            if (PvwaveController.log) PvwaveController.log("Got action event from " +"xxx" ); //NOI18N
            if (PvwaveController.log) PvwaveController.log("  Posting event to listener(tab) " ); //NOI18N
            
            if ( lineEditorPane.getText().length() > 0) {
                lineEditorPane.setSelectionStart(0);
                lineEditorPane.setSelectionEnd(lineEditorPane.getText().length());
                commandHistory2.add( lineEditorPane.getText() );
            }
            
            ActionEvent e = new ActionEvent(InputPanel.this, ActionEvent.ACTION_PERFORMED,
                    ACTION_NEWTEXT);
            
            listener.actionPerformed(e);
            lineEditorPane.setText("");
        }
    };
    
    final KeyStroke ENTER=     KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 );
    final KeyStroke SHIFT_ENTER=    KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, KeyEvent.SHIFT_MASK );
    final KeyStroke CONTROL_ENTER=    KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK );
    final KeyStroke CONTROL_C=    KeyStroke.getKeyStroke( KeyEvent.VK_C, KeyEvent.CTRL_MASK );
    final KeyStroke HIST_PREV= KeyStroke.getKeyStroke( KeyEvent.VK_UP, 0 );
    final KeyStroke HIST_NEXT= KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, 0 );
    
    private void init() {
        setLayout(new BorderLayout());
        
        add(lbl, BorderLayout.WEST);
        //add(field, BorderLayout.CENTER);
        add( lineEditorPane,  BorderLayout.CENTER );
        
        lineEditorPane.setMinimumSize(new Dimension(100,20) );
        field.setEditable(true);
        add(eof, BorderLayout.EAST);
        
        // install editor kit to provide completions at command line.
        EditorKit kit=  JEditorPane.createEditorKitForContentType("text/x-application-pvwave");
        lineEditorPane.setEditorKit( kit );
        
        PvwaveDebugger.getInstance().setCommandLineDoc((BaseDocument)lineEditorPane.getDocument());
        
        commandHistory= new History(field);
        commandHistory2= new History2(lineEditorPane);
        
        field.addActionListener(this);
        
        ActionMap map= lineEditorPane.getActionMap();
        InputMap inputMap= lineEditorPane.getInputMap();
        inputMap.put( ENTER, "executeCommand" );
        inputMap.put( SHIFT_ENTER, "executeCommand" );
        inputMap.put( CONTROL_ENTER, "executeCommand" );
        inputMap.put( CONTROL_C, "clearCommand" );
        map.put( "executeCommand", executeCommandAction );
        map.put( "clearCommand", clearCommandAction );
        
        inputMap.put( HIST_NEXT, "nextCommand" );
        map.put( "nextCommand", commandHistory2.HIST_NEXT_ACTION );
        
        inputMap.put( HIST_PREV, "prevCommand" );
        map.put( "prevCommand", commandHistory2.HIST_PREV_ACTION );  
        
        eof.addActionListener(this);
        
        eof.setToolTipText(NbBundle.getMessage(InputPanel.class, "TIP_EOF")); //NOI18N
        field.setToolTipText(NbBundle.getMessage(InputPanel.class, "TIP_INPUT")); //NOI18N
        lineEditorPane.setToolTipText( NbBundle.getMessage(InputPanel.class, "TIP_INPUT")); //NOI18N
        
        field.getAccessibleContext().setAccessibleName(lbl.getText());
        lineEditorPane.getAccessibleContext().setAccessibleName(lbl.getText());
        eof.getAccessibleContext().setAccessibleName(eof.getText());
        field.getAccessibleContext().setAccessibleDescription(field.getToolTipText());
        lineEditorPane.getAccessibleContext().setAccessibleDescription(lineEditorPane.getToolTipText());
        eof.getAccessibleContext().setAccessibleDescription(eof.getToolTipText());
        
        // XXX use o.o.awt.Mnemonics instead, simpler...
        lbl.setDisplayedMnemonic(
                Utilities.stringToKey(
                NbBundle.getMessage(InputPanel.class, "INPUT.mnemonic")).getKeyCode()); //NOI18N
        lbl.setDisplayedMnemonicIndex(
                Integer.parseInt(
                NbBundle.getMessage(InputPanel.class, "INPUT.index"))); //NOI18N
        
        eof.setMnemonic(NbBundle.getMessage(
                InputPanel.
                class, "EOF.mnemonic").charAt(0)); //NOI18N
        
        Border b = field.getBorder();
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 3, 3, 3, getBackground()), b));
        
        b= lineEditorPane.getBorder();
        lineEditorPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 3, 3, 3, getBackground()), b));
        
        lbl.setBorder(BorderFactory.createEmptyBorder(0,0,0,5));
        
        lbl.setLabelFor(field);
        lbl.setLabelFor( lineEditorPane );
        
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                1,0,0,0,UIManager.getColor("controlShadow")),
                BorderFactory.createEmptyBorder(4,4,4,4)));
        
        field.addFocusListener(this);
        lineEditorPane.addFocusListener(this);
    }
    
    public void doLayout() {
        Dimension lblp = lbl.getPreferredSize();
        Dimension eofp = eof.getPreferredSize();
        Dimension fp = field.getPreferredSize();
        
        Insets ins = getInsets();
        
        lbl.setBounds(ins.left, ins.top, lblp.width, getHeight() - (ins.top + ins.bottom));
        
        int ftop = (getHeight() / 2) - (fp.height / 2);
        
        int fright = getWidth() - (ins.right + eofp.width);
        int fleft = ins.left + lblp.width;
        
        //field.setBounds(fleft, ftop, fright - fleft, fp.height);
        lineEditorPane.setBounds(fleft, ftop, fright - fleft, fp.height);
        
        int btop = (getHeight() / 2) - (eofp.height / 2);
        eof.setBounds(fright, btop, getWidth() - (fright + ins.right), eofp.height);
    }
    
    public Dimension getPreferredSize() {
        Dimension lblp = lbl.getPreferredSize();
        Dimension eofp = eof.getPreferredSize();
        //Dimension fp = field.getPreferredSize();
        Dimension fp = lineEditorPane.getPreferredSize();
        
        Insets ins = getInsets();
        
        int h = ins.top + ins.bottom + Math.max(Math.max(lblp.height, eofp.height), fp.height);
        int w = ins.left + lblp.width + eofp.width + fp.width + ins.right;
        
        return new Dimension(w, h);
    }
    
    public Dimension getMinimumSize() {
        Dimension lblp = lbl.getMinimumSize();
        Dimension eofp = eof.getMinimumSize();
        //Dimension fp = field.getMinimumSize();
        Dimension fp = lineEditorPane.getMinimumSize();
        
        Insets ins = getInsets();
        
        int h = ins.top + ins.bottom + Math.max(Math.max(lblp.height, eofp.height), fp.height);
        int w = ins.left + lblp.width + eofp.width + fp.width + ins.right;
        
        return new Dimension(w, h);
    }
    
    
    public String getText() {
        return lineEditorPane.getText();
    }
    
    public void addActionListener(ActionListener listener) {
        if (this.listener != null) {
            throw new IllegalStateException(this.listener + " is already " + //NOI18N
                    "listening"); //NOI18N
        }
        this.listener = listener;
    }
    
    public void removeActionListener(ActionListener listener) {
        if (listener != this.listener) {
            throw new IllegalArgumentException(listener + " is not " +  //NOI18N
                    this.listener);
        }
        this.listener = null;
    }
    
    public void actionPerformed(ActionEvent ae) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                ae.getSource() == eof ? ACTION_EOF : ACTION_NEWTEXT);
        if (PvwaveController.log) PvwaveController.log("Got action event from " + ae.getSource()); //NOI18N
        if (PvwaveController.log) PvwaveController.log("  Posting event to listener(tab) " + e); //NOI18N
        
        if (ae.getSource() == field && field.getText().length() > 0) {
            field.setSelectionStart(0);
            field.setSelectionEnd(field.getText().length());
            commandHistory.add( field.getText() );
        }
        
        listener.actionPerformed(e);
        field.setText("");
    }
    
    private AbstractOutputTab findOutputTab() {
        if (getParent() != null) {
            return (AbstractOutputTab) SwingUtilities.getAncestorOfClass(AbstractOutputTab.class, this);
        } else {
            return null;
        }
    }
    
    public void focusGained(FocusEvent fe) {
        AbstractOutputTab tab = findOutputTab();
        if (tab != null) {
            tab.notifyInputFocusGained();
        }
    }
    
    public void focusLost(FocusEvent fe) {
        
    }
    
    public void setPrompt( String prompt ) {
        lbl.setText( prompt );
    }
}
