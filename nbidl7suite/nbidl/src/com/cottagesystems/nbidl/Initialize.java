/*
 * Initialize.java
 *
 * Created on October 7, 2006, 6:59 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.LogManager;

/**
 *
 * @author jbf
 */
public class Initialize {
    public static URL configUrl=  Initialize.class.getResource("logging.properties");
    public static void loadLoggingConfig() {
        try {
            if ( configUrl!=null ) {
                InputStream in= configUrl.openStream();
                LogManager.getLogManager().readConfiguration( in );
                in.close();
                System.err.println( "read log configuration from "+configUrl );
            } else {
                System.err.println("unable to read logger config");
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
