/*
 * PvwaveOptionsCatagory.java
 *
 * Created on August 1, 2006, 10:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.options;

import java.awt.Image;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.spi.options.OptionsCategory;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.Utilities;

/**
 *
 * @author jbf
 */
public class PvwaveOptionsCategory extends OptionsCategory {
    
    public PvwaveOptionsCategory() {
        Logger.getLogger("pvwave").fine("instanciate PvwaveOptionsCategory");
    }
    
    public String getCategoryName () {
        return "IDL";
    }

    public String getTitle () {
        return "IDL Settings";
    }  
    
    public Icon getIcon() {
        Image image = Utilities.loadImage("com/cottagesystems/nbidl/resources/pvwaveIcon.PNG");
        return new ImageIcon( image );
    }

    private String getTooltip() {
        return "Provide settings for the IDL Environment";
    }

    public OptionsPanelController create () {
        return new PvwaveOptionsPanelController();
    } 
    
}
