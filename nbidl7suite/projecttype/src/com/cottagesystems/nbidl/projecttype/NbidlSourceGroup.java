/*
 * NbidlSourceGroup.java
 *
 * Created on November 1, 2007, 11:40 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.projecttype;

import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.Icon;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author jbf
 */
public class NbidlSourceGroup implements SourceGroup {
    
    String folderName;
    FileObject rootFileObject;
    
    /** Creates a new instance of NbidlSourceGroup */
    public NbidlSourceGroup( String folderName ) {
        this.folderName= folderName;
    }

    public FileObject getRootFolder() {
        if ( rootFileObject==null ) {
            rootFileObject= FileUtil.toFileObject( new File( folderName ) );
        }
        return rootFileObject;
    }

    public String getName() {
        return folderName;
    }

    public String getDisplayName() {
        return folderName;
    }

    public Icon getIcon(boolean b) {
        return null;
    }

    public boolean contains(FileObject fileObject) throws IllegalArgumentException {
        return FileUtil.getRelativePath( rootFileObject, fileObject ) != null;
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }

    private java.beans.PropertyChangeSupport propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);
    
}
