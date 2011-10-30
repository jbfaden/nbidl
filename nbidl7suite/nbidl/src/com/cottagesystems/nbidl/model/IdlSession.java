/*
 * IdlSession.java
 *
 * Created on May 19, 2007, 9:54:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.model;

import com.cottagesystems.nbidl.model.IdlCommonBlock;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jbf
 */
public class IdlSession {
    
    //TODO: not thread safe.
    
    private HashMap<String, IdlStruct> structs;
    private HashMap<String, IdlClass> classes;
    private HashMap<String, IdlCommonBlock > commonBlocks;
    
    public IdlSession() {
        structs= new HashMap<String,IdlStruct>();
        classes=  new HashMap<String,IdlClass>();
    }
    
    public void defineStruct( IdlStruct struct ) {
        structs.put( struct.getId().toLowerCase(), struct );
    }
    
    public void defineObject( IdlClass idlClass ) {
        classes.put( idlClass.getName().toLowerCase(), idlClass );
    }
    
    public IdlStruct getStruct( String name ) {
        return structs.get(name.toLowerCase());
    }
    
    public IdlClass getClass( String name ) {
        return classes.get(name.toLowerCase());
    }
    
    public IdlClass newClass( String name ) {
        IdlClass idlClass= classes.get(name);
        if ( idlClass==null ) {
            idlClass= new IdlClass(name);
            classes.put( name.toLowerCase(), idlClass );
        }
        return idlClass;
    }
    
    public IdlCommonBlock newCommonBlock( String name ) {
        IdlCommonBlock result= commonBlocks.get(name);
        if ( result==null ) {
            result= new IdlCommonBlock();
            result.setId(name);
        }
        return result;
    }
    
    public Map<String,IdlClass> classes() {
        return Collections.unmodifiableMap( classes );
    }
    
    public Map<String,IdlStruct> structs() {
        return Collections.unmodifiableMap( structs );
    }
    
    public Map<String,IdlCommonBlock>  commonBlocks() {
        return Collections.unmodifiableMap( commonBlocks );
    }
}
