/*
 * NbidlProjectFactory.java
 *
 * Created on October 31, 2007, 10:07 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.projecttype;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author jbf
 */
public class NbidlProjectFactory implements ProjectFactory {
    
    public static final String PROJECT_DIR = "nbidlproject";
    public static final String PROJECT_PROPFILE = "project.properties";
    
    /** Creates a new instance of NbidlProjectFactory */
    public NbidlProjectFactory() {
    }
    
    public boolean isProject(FileObject projectDirectory ) {
        return projectDirectory.getFileObject(PROJECT_DIR) != null;
    }
    
    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
        return isProject(dir) ? new NbidlProject(dir, state) : null;
    }
    
    public void saveProject(Project project) throws IOException, ClassCastException {
        FileObject projectRoot = project.getProjectDirectory();
        if (projectRoot.getFileObject(PROJECT_DIR) == null) {
            throw new IOException("Project dir " + projectRoot.getPath() + " deleted," +
                    " cannot save project");
        }

        //Find the properties file pvproject/project.properties,
        //creating it if necessary
        String propsPath = PROJECT_DIR + "/" + PROJECT_PROPFILE;
        FileObject propertiesFile = projectRoot.getFileObject(propsPath);
        if (propertiesFile == null) {
            //Recreate the properties file if needed
            FileObject projectDir= projectRoot.getFileObject(PROJECT_DIR);
            propertiesFile = projectDir.createData(PROJECT_PROPFILE);
        }
        
        Properties properties = (Properties) project.getLookup().lookup(Properties.class);
        
        File f = FileUtil.toFile(propertiesFile);
        properties.store(new FileOutputStream(f), "NetBeans Nbidl Project Properties");
    }
    
}
