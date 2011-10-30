/*
 * IdlValue.java
 *
 * Created on March 16, 2007, 8:59 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.model;

/**
 *
 * @author jbf
 */
public class IdlValue {
    
    PrimativeType type;
    Object value;
    
    public IdlValue( PrimativeType type, Object value ) {
        this.type= type;
        this.value= value;
    }
    
}
