/*
 * PvwaveIndentEngineBeanInfo.java
 *
 * Created on August 10, 2007, 9:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.indent;

import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ResourceBundle;
import org.openide.util.NbBundle;

/**
 *
 * @author jbf
 */
public class PvwaveIndentEngineBeanInfo extends SimpleBeanInfo {
    
    
    private BeanDescriptor beanDescriptor;
    
    public PvwaveIndentEngineBeanInfo() {
    }
    
    public BeanDescriptor getBeanDescriptor() {
        if (beanDescriptor != null) {
            beanDescriptor = new BeanDescriptor(PvwaveIndentEngine.class);
            ResourceBundle bundle = NbBundle.getBundle(PvwaveIndentEngine.class);
            beanDescriptor.setDisplayName(bundle.getString("LAB_MyIndentEngine"));
            beanDescriptor.setShortDescription(bundle.getString("HINT_MyIndentEngine"));
            beanDescriptor.setValue("global", Boolean.TRUE); // NOI18N
        }
        return beanDescriptor;
    }
    
}
