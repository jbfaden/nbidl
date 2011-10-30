/*
 * Struct.java
 *
 * Created on March 16, 2007, 8:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jbf
 */
public class IdlStruct {
    
    ArrayList tags;
    String id;
    IdlStruct parentStruct;
    
    /** Creates a new instance of Struct */
    public IdlStruct() {
        tags= new ArrayList();
    }
    
    public ArrayList tags() {
        return tags;
    }
    
    public void addTag( StructTag tag ) {
        tags.add(tag);
    }
    
    public void setId( String id ) {
        this.id= id;
    }
    
    public String getId( ) {
        return this.id;
    }
    
    public String getName() {
        return this.id;
    }
    
    public void setParentStruct( IdlStruct struct ) {
        this.parentStruct= struct;
    }
    
    /**
     * derive a Struct from the text returned by "help, /struct"
     *
     * ** Structure <844d64c>, 2 tags, length=4, data length=4, refs=1:
     * TAGA            INT              1
     * TAGB            INT              2
     *
     */
    public static IdlStruct parseHelpStruct( BufferedReader in ) throws IOException {
        String line= in.readLine();
        IdlStruct result=null;
        System.err.println(line);
        Pattern p= Pattern.compile( "\\s+(.+?)\\s+(.+?)\\s+(.+?)\\s*" );
        Pattern ph= Pattern.compile( "\\*\\* Structure (.+?), .*" );
        while ( line!=null ) {
            Matcher m;
            if ( (m=ph.matcher(line)).matches() ) {
                result= new IdlStruct();
                result.setId( m.group(1) );
            } else {
                m= p.matcher( line );
                if ( !m.matches() ) {
                    System.err.println("no match!!!");
                    String line2= in.readLine();
                    if ( line2!=null && p.matcher(line+line2).matches() ) {
                        line= line+line2;
                        continue;
                    }
                } else {
                    StructTag t= new StructTag( m.group(1).toLowerCase() );
                    PrimativeType type= PrimativeType.forName( m.group(2) );
                    t.setType( type );
                    t.setValue( type.parseValue( m.group(3) ) );
                    result.addTag(t);
                }
            }
            line= in.readLine();
        }
        return result;
    }
}
