/*
 * PvwaveSetting.java
 *
 * Created on August 1, 2006, 10:07 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.options;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author jbf
 */
public class PvwaveSetting implements Serializable {
    
    /** Creates a new instance of PvwaveSetting */
    public PvwaveSetting() {
        String os= System.getProperty("os.name");
        if ( os.startsWith("Windows") ) idlHome=  "c:/rsi/idl63" ; 
        else if ( os.equals("Mac OS") ) idlHome=  "/Applications/rsi/idl" ;
        else idlHome= "/usr/local/rsi/idl";
    }
    
    private double reparseDelaySeconds= 5;
    
    public double getReparseDelaySeconds() {
        return reparseDelaySeconds;
    }
    
    public void setReparseDelaySeconds( double x ) {
        this.reparseDelaySeconds= x;
    }

    /**
     * Holds value of property pvwaveHome.
     */
    private String idlHome;

    /**
     * Getter for property pvwaveHome.
     * @return Value of property pvwaveHome.
     */
    public String getIDLHome() {
        return this.idlHome;
    }

    /**
     * Setter for property pvwaveHome.
     * @param pvwaveHome New value of property pvwaveHome.
     */
    public void setIDLHome(String pvwaveHome) {
        this.idlHome = pvwaveHome;
    }
    
}
