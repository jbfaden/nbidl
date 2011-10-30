/*
 * StructTag.java
 *
 * Created on March 16, 2007, 8:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.model;

/**
 *
 * @author jbf
 */
public class StructTag {
    
    String name;
    
    /**
     * the value of the tag, either a IdlStruct or a String for now.
     */
    Object value;
    
    /**
     * the tags data type.
     */
    PrimativeType type;
    
    /**
     * Creates a new instance of StructTag 
     * @param name name of the tag.
     */
    public StructTag( String name ) {
        this.name= name;
    }
    
    public void setValue( Object value ) {
        this.value= value;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public void setType ( PrimativeType type ) {
        this.type= type;
    }
    
    public PrimativeType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }
}
