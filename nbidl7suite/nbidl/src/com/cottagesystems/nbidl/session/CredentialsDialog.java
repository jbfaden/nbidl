/*
 * CredentialsDialog.java
 *
 * Created on April 13, 2006, 8:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.session;

import java.awt.Dialog;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 * @author Jeremy
 */
public class CredentialsDialog {
    
    public String getCredentials() {
        
        JPanel panel= new JPanel();
        JPasswordField pass=new JPasswordField();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        panel.add( new JLabel("Enter Password: ") );
        panel.add( pass );
        
        DialogDescriptor desc= new DialogDescriptor( panel, "Enter ssh password" );
        Dialog dialog= DialogDisplayer.getDefault().createDialog( desc );
        dialog.setVisible(true);
        return new String(pass.getPassword());

    }

    /** Creates a new instance of CredentialsDialog */
    public CredentialsDialog() {
    }

}
