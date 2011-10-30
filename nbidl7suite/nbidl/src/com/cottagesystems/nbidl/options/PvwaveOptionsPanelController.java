/*
 * PvwaveOptionsPanelController.java
 *
 * Created on August 1, 2006, 10:18 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author jbf
 */
public class PvwaveOptionsPanelController extends OptionsPanelController {
    
    PvwaveCustomizer customizer= new PvwaveCustomizer();
    PvwaveSettingUtil settingUtil= new PvwaveSettingUtil();
    
    private String sureTocancel="<html><b>Do you want to save your changes?</b><html>";
    
    public PvwaveOptionsPanelController() {
    }

    public void update() {
        customizer.populateUI( settingUtil.retrieveSetting() );
    }

    public void applyChanges() {
        // this method is called when OK button has been pressed
	// save values here
        if (customizer.changed) {
            settingUtil.storeSetting(customizer.returnSetting());
        }
    }

    public void cancel() {
        // this method is called when Cancel button has been pressed
	// revert any possible changes here
        if (customizer.changed) {
            DialogDescriptor ddesc = new DialogDescriptor((Object)sureTocancel,"Changes will be lost",
                    true,DialogDescriptor.YES_NO_OPTION,DialogDescriptor.CANCEL_OPTION,new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(e.getActionCommand().equalsIgnoreCase("yes")){
                        settingUtil.storeSetting(customizer.returnSetting());}
                }});
                DialogDisplayer.getDefault().createDialog(ddesc).setVisible(true);
        }
    }

    public boolean isValid() {
        return true;
    }

    public boolean isChanged() {
        return customizer.changed;
    }

    public JComponent getComponent(Lookup lookup) {
        return customizer;
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx ("com.cottagesystems.nbidl.options.GCheckerOptionsPanelController");
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    }
    
}
