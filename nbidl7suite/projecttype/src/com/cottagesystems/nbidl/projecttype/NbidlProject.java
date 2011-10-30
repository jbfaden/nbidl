/*
 * NbidlProject.java
 *
 * Created on October 31, 2007, 10:10 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.projecttype;

import com.cottagesystems.nbidl.syntax.IDLRoutinesDataBase;
import com.cottagesystems.nbidl.syntax.UserRoutinesDataBase;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author jbf
 */
public class NbidlProject implements Project {
    
    private final FileObject projectDir;
    NbidlLogicalView logicalView;
    private final ProjectState state;
    private Lookup lkp;
    private UserRoutinesDataBase userRoutinesDataBase;
    private IDLRoutinesDataBase idlRoutinesDataBase;
    
    private String PROPERTY_PATH="path";
    
    public NbidlProject( FileObject dir, ProjectState state ) {
        this.projectDir= dir;
        this.state= state;
        logicalView= new NbidlLogicalView(this);
        idlRoutinesDataBase= IDLRoutinesDataBase.getInstance();
        for ( String s : getSearchPath() ) FileOwnerQuery.markExternalOwner( new File(s).toURI(), this, FileOwnerQuery.EXTERNAL_ALGORITHM_TRANSIENT );
    }
    
    public FileObject getProjectDirectory() {
        return projectDir;
    }
    
    private UserRoutinesDataBase getUserRoutineDataBase() {
        if ( userRoutinesDataBase==null ) {
            userRoutinesDataBase= new UserRoutinesDataBase();
        }
        return userRoutinesDataBase;
    }
    
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(new Object[] {
                this,  //project spec requires a project be in its own lookup
                state, //allow outside code to mark the project as needing saving
                new ActionProviderImpl(), //Provides standard actions like Build and Clean
                loadProperties(), //The project properties
                new Info(), //Project information implementation
                logicalView, //Logical view of project implementation
                getUserRoutineDataBase(), 
                new NbidlSources(this),
            });
        }
        return lkp;
    }

    public List<String> getSearchPath() {
        Properties p= (Properties) getLookup().lookup( Properties.class );
        String path= p.getProperty( PROPERTY_PATH, "" );
        if ( path.equals("") ) {
            return Collections.emptyList();
        } else {
            return Arrays.asList( path.split(";") );
        }
    }
    
    private static String join(Collection s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }
    
    public void setSearchPath( List<String> list ) {
        List<String> oldValue= getSearchPath();
        Properties p= (Properties) getLookup().lookup( Properties.class );
        p.put( PROPERTY_PATH, join( list, ";" ) );
        idlRoutinesDataBase.rescan();
        propertyChangeSupport.firePropertyChange ("searchPath", oldValue, list );
    }
    
    public void addToSearchPath(String string) {
        addToSearchPath( Collections.singletonList(string) );
    }

    public void addToSearchPath(List<String> add) {
        List<String> list= new ArrayList( getSearchPath() );
        list.addAll(add);
        for ( String s: add ) FileOwnerQuery.markExternalOwner( new File(s).toURI(), this, FileOwnerQuery.EXTERNAL_ALGORITHM_TRANSIENT );
        setSearchPath(list);
    }
        
    public void removeFromSearchPath( String string ) {
        removeFromSearchPath( Collections.singletonList(string) );
    }

    public void removeFromSearchPath(List<String> remove) {
        List<String> list= new ArrayList( getSearchPath() );
        list.removeAll(remove);
        for ( String s: remove ) FileOwnerQuery.markExternalOwner( new File(s).toURI(), null, FileOwnerQuery.EXTERNAL_ALGORITHM_TRANSIENT );
        setSearchPath(list);
    }
    
    
    private Properties loadProperties() {
        FileObject fob = projectDir.getFileObject(NbidlProjectFactory.PROJECT_DIR +
                "/" + NbidlProjectFactory.PROJECT_PROPFILE);
        NotifyProperties properties = new NotifyProperties();
        if (fob != null) {
            try {
                properties.load(fob.getInputStream());
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        properties.setState(state);
        return properties;
    }
    
    private static class NotifyProperties extends Properties {
        private ProjectState state;
        NotifyProperties() {
        }
        
        public Object put(Object key, Object val) {
            Object result = super.put(key, val);
            if (((result == null) != (val == null)) || (result != null &&
                    val != null && !val.equals(result))) {
                if ( state!=null ) state.markModified();
            }
            return result;
        }
        private void setState( ProjectState state ) {
            this.state= state;
        }
    }
    
    
    private final class ActionProviderImpl implements ActionProvider {
        public String[] getSupportedActions() {
            return new String[0];
        }
        
        public void invokeAction(String string, Lookup lookup) throws IllegalArgumentException {
            //do nothing
        }
        
        public boolean isActionEnabled(String string, Lookup lookup) throws IllegalArgumentException {
            return false;
        }
    }
    
    private final class Info implements ProjectInformation {
        public Icon getIcon() {
            return new ImageIcon( Utilities.loadImage( "com/cottagesystems/nbidl/projecttype/pvwaveIcon.PNG") ) ;
        }
        
        public String getName() {
            return getProjectDirectory().getName();
        }
        
        public String getDisplayName() {
            return getName();
        }
        
        public void addPropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }
        
        public void removePropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }
        
        public Project getProject() {
            return NbidlProject.this;
        }
    }

    /**
     * Holds value of property foo.
     */
    private String foo;

    /**
     * Utility field used by bound properties.
     */
    private java.beans.PropertyChangeSupport propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);

    /**
     * Adds a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    /**
     * Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    
}
