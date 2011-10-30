/*
 * Pvwave.java
 *
 * Created on March 17, 2006, 4:28 PM
 *
 *
 */

package com.cottagesystems.nbidl.dataobject;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;

/**
 * data model for a pvwave .pro file.  This contains function and class definitions.
 * @author Jeremy
 */
public class ProceduresFile {
    
    private static Logger logger= Logger.getLogger("pvwave");
    
    /*
     *  return the number of elements in the file
     */
    public int procedureCount() {
        return procedures.size();
    }
    
    List procedures= new ArrayList();
    
    private FileObject fileObject;
    
    /*
     * return element of pvwave file--single function
     */
    public Procedure getProcedure( int i ) {
        return (Procedure)procedures.get(i);
    }
    
    /**
     * returns the procedure with the given name
     */
    public Procedure getProcedure( String name ) {
        for ( int i=0; i<procedures.size(); i++ ) {
            Procedure p= (Procedure) procedures.get(i);
            if ( p.getName().equalsIgnoreCase(name) ) {
                return p;
            }
        }
        return null;
    }
    
    /** Write a pvwave to a text stream. */
    public static void generate(ProceduresFile s, Writer w) throws IOException {
        logger.fine( "Pvwave.generate" );
    }
    
    public void addProcedure(Procedure procedure) {
        procedures.add( procedure );
    }
    
    public void setFileObject(FileObject file) {
        this.fileObject= file;
    }
    
    public FileObject getFileObject() {
        return fileObject;
    }
    
    /**
     * return the procedure at character offset i.  If not within a 
     * procedure, return null.
     */
    public Procedure getProcedureAtOffset(int ipos ) {
        Procedure result= null;
        for ( int i=0; i<procedures.size(); i++ ) {
            Procedure p= (Procedure) procedures.get(i);
            if ( p.getOffset() > ipos ) {
                return result;
            } else {
                result= p;
            }
        }
        return result;
    }
    
    public String toString() {
        return fileObject.getName();
    }
}
