/*
 * NbidlSources.java
 *
 * Created on November 1, 2007, 10:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.projecttype;

import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;

/**
 *
 * @author jbf
 */
public class NbidlSources implements Sources {
    
    NbidlProject project;
    
    /** Creates a new instance of NbidlSources */
    public NbidlSources( NbidlProject project ) {
        this.project= project;
    }

    public SourceGroup[] getSourceGroups(String string) {
        List<String> path= project.getSearchPath();
        SourceGroup[] sources= new SourceGroup[path.size()];
        for ( int i=0; i<path.size(); i++ ) {
            sources[i]= new NbidlSourceGroup(path.get(i));
        }
        return sources;
    }

    public void addChangeListener(ChangeListener changeListener) {
    }

    public void removeChangeListener(ChangeListener changeListener) {
    }
    
}
