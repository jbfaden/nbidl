/*
 * IdlProperty.java
 *
 * Created on August 12, 2007, 7:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.model;

/**
 *
 * @author jbf
 */
public class IdlProperty {
    
    /** Creates a new instance of IdlProperty */
    public IdlProperty( String name ) {
        this.name= name;
    }

    /**
     * Holds value of property name.
     */
    private String name;

    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Holds value of property link.
     */
    private String link;

    /**
     * Getter for property link.
     * @return Value of property link.
     */
    public String getLink() {
        return this.link;
    }

    /**
     * Setter for property link.
     * @param link New value of property link.
     */
    public void setLink(String link) {
        this.link = link;
    }

    
}
