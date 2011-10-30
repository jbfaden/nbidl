/*
 * RestoreColoring.java
 *
 * Created on October 20, 2005, 5:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.syntax;

import org.netbeans.editor.Settings;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbBundle;

/**
 *
 * @author Administrator
 */
public class RestoreColoring extends ModuleInstall {
    
    
    /**
     * Registers properties editor, installs options and copies settings.
     * Overrides superclass method.
     */
    public void restored() {
        addInitializer();
        installOptions();
    }
    
    /**
     * Uninstalls properties options.
     * And cleans up editor settings copy.
     * Overrides superclass method.
     */
    public void uninstalled() {
        uninstallOptions();
    }
    
    /**
     * Adds initializer and registers editor kit.
     */
    public void addInitializer() {
        Settings.addInitializer(new PvwaveSettingsInitializer());
    }
    
    /**
     * Installs properties editor and print options.
     */
    public void installOptions() {
    }
    
    /** Uninstalls properties editor and print options. */
    public void uninstallOptions() {

    }
    
}