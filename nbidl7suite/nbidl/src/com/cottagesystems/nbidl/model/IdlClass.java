/*
 * IdlObject.java
 *
 * Created on May 18, 2007, 11:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.model;

import com.cottagesystems.nbidl.dataobject.Procedure;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author jbf
 */
public class IdlClass {
    
    String id;
    IdlClass parentClass;
    HashMap<String,Procedure> methods;
    IdlStruct fields;
    HashMap<String,IdlProperty> properties=null;
    
    public IdlClass( String id ) {
        this.id= id;
        methods= new HashMap<String,Procedure>();
    }
    
    public void setParentClass( IdlClass idlClass ) {
        this.parentClass= idlClass;
    }
    
    public List methods() {
        return new ArrayList(methods.values());
    }
    
    public void addMethod( Procedure pro ) {
        String name= pro.getName();
        int i= name.indexOf("::");
        if ( i==-1 ) {
            int j= name.indexOf(":");  // kludge for bug in IDL catalog
            if ( j==-1 ) {
                System.err.println( "expected :: in class method name \""+name+"\"" );
            } else {
                name= name.substring(j+1).toLowerCase();
            }
        } else {
            name= name.substring(i+2).toLowerCase();
        }
        methods.put(name,pro);
    }
    
    public Procedure getMethod( String name ) {
        Procedure result= methods.get(name.toLowerCase());
        if ( result==null && parentClass !=null ) {
            result= parentClass.getMethod(name);
        }
        return result;
    }
    
    public String getName() {
        return this.id;
    }
    
    public IdlStruct getFields() {
        if ( fields==null ) {
            fields= new IdlStruct();
            fields.setId(id);
        }
        return fields;
    }

    public String getId() {
        return id;
    }
    
    public void addProperty( IdlProperty prop ) {
        if ( properties==null ) {
            properties= new HashMap<String,IdlProperty>();
        }
        properties.put( prop.getName(), prop );
    }
}
