/*
 * PrimativeType.java
 *
 * Created on March 16, 2007, 8:37 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.model;

import java.util.HashMap;

/**
 * Enumeration of IDL primative types.
 * @author jbf
 */
public class PrimativeType {
    
    int type;
    String name;
    static HashMap types= new HashMap(14);
    
    /** Creates a new instance of PrimativeType */
    private PrimativeType( int type, String name ) {
        this.type= type;
        this.name= name;
        types.put( name, this );
    }
    
    public String toString() {
        return name;
    }
    
    public IdlValue parseValue( String value ) {
        return new IdlValue( this, value );
    }
    
    
    public static PrimativeType forName( String name ) {
        return (PrimativeType) types.get(name);
    }
    
    public static PrimativeType UNDEFINED= new PrimativeType( 0, "UNDEFINED" );
    public static PrimativeType BYTE= new PrimativeType( 1, "BYTE" );
    public static PrimativeType INT= new PrimativeType( 2, "INT" );
    public static PrimativeType LONG= new PrimativeType( 3, "LONG" );
    public static PrimativeType FLOAT= new PrimativeType( 4, "FLOAT" );
    public static PrimativeType DOUBLE= new PrimativeType( 5, "DOUBLE" );
    public static PrimativeType COMPLEX= new PrimativeType( 6, "COMPLEX" );
    public static PrimativeType STRING= new PrimativeType( 7, "STRING" );
    public static PrimativeType STRUCT= new PrimativeType( 8, "STRUCT" );
    public static PrimativeType DCOMPLEX= new PrimativeType( 9, "DCOMPLEX" );
    public static PrimativeType POINTER= new PrimativeType( 10, "POINTER" );
    public static PrimativeType OBJREF= new PrimativeType( 11, "OBJREF" );
    public static PrimativeType UINT= new PrimativeType( 12, "UINT" );
    public static PrimativeType ULONG= new PrimativeType( 13, "ULONG" );
    public static PrimativeType LONG64= new PrimativeType( 14, "LONG64" );
    public static PrimativeType ULONG64= new PrimativeType( 15, "ULONG64" );
    
}