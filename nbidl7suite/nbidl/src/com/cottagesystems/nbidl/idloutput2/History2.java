/*
 * History.java
 *
 * Created on August 8, 2006, 6:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.idloutput2;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.text.Keymap;

/**
 *
 * @author jbf
 */
public class History2 {
    
    List commands;
    JEditorPane field;
    String current;
    int commandIndex=0;
    
    /** Creates a new instance of History */
    public History2( JEditorPane field ) {
        this.field= field;
        commands= new LinkedList();
    }
    
    public Action HIST_PREV_ACTION= new AbstractAction("HistPrev") {
        public void actionPerformed(ActionEvent e ) {
            fetchPreviousCommand();
        }
    };
    
    public Action HIST_NEXT_ACTION= new AbstractAction("HistNext") {
        public void actionPerformed(ActionEvent e ) {
            fetchNextCommand();
        }
    };
    
    
    public void add( String command ) {        
        if ( commands.size()>0 ) {
            String lastCommand= (String) commands.get( commands.size()-1);
            if ( command.equals(lastCommand) ) return;
        }
        if ( command.equals("" ) ) return;
        commands.add(command);
        commandIndex=commands.size();
    }
    
    public void fetchPreviousCommand( ) {
        if ( commands.size()==0 ) return;
        if ( commandIndex==commands.size() ) current= field.getText();
        commandIndex--;
        if ( commandIndex<0 ) commandIndex=0;
        String cmd= (String) commands.get(commandIndex);
        field.setText(cmd);
    }
    
    public void fetchNextCommand() {
        if ( commands.size()==0 ) return;
        commandIndex++;
        if ( commandIndex>commands.size() ) commandIndex= commands.size();
        if ( commandIndex==commands.size() ) {
            field.setText(current);
        } else {
            String cmd= (String) commands.get(commandIndex);
            field.setText(cmd);
        }
    }
}
