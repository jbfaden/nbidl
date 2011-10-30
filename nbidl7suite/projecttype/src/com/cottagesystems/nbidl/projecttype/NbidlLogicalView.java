/*
 * NbidlLogicalView.java
 *
 * Created on October 31, 2007, 10:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.projecttype;

import com.cottagesystems.nbidl.projecttype.actions.Actions;
import com.cottagesystems.nbidl.projecttype.actions.StartIdlSession;
import com.cottagesystems.nbidl.syntax.UserRoutinesDataBase;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.Action;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author jbf
 */
public class NbidlLogicalView implements LogicalViewProvider {
    
    NbidlProject project;
    Node root;
    
    PropertyChangeListener projectListener= new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if (root!=null ) {
                ProjectChildren children= (ProjectChildren)root.getChildren();
                children.update();
            }
        }
    };
    
    /** Creates a new instance of NbidlLogicalView */
    public NbidlLogicalView(NbidlProject project) {
        this.project= project;
        project.addPropertyChangeListener( projectListener );
    }
    
    
    public Node createLogicalView() {
        try {
            root= new ProjectNode( project );
            UserRoutinesDataBase db= (UserRoutinesDataBase) project.getLookup().lookup( UserRoutinesDataBase.class );
            db.setRoot(root);
        } catch (DataObjectNotFoundException ex) {
            root= new AbstractNode(Children.LEAF);
            throw new RuntimeException(ex);
        }
        return root;
    }
    
    private static final class ProjectNode extends AbstractNode {
        final NbidlProject project;
        public ProjectNode( NbidlProject project) throws DataObjectNotFoundException {
            super( new ProjectChildren(project), new ProxyLookup( new Lookup[] { Lookups.singleton(project), project.getLookup() } ) );
            this.project = project;
        }
        
        public Image getOpenedIcon(int i) {
            return getIcon(i);
        }
        
        public Image getIcon(int i) {
            return Utilities.loadImage(
                    "com/cottagesystems/nbidl/projecttype/pvwaveIcon.PNG" );
        }
        
        public Action[] getActions(boolean b) {
            return new Action[] {
                Actions.add(), StartIdlSession.getInstance(), Actions.rescan() };
        }
        
        public String getDisplayName() {
            ProjectInformation info= (ProjectInformation) project.getLookup().lookup(ProjectInformation.class);
            return info.getDisplayName();
        }
        
        public String getHtmlDisplayName() {
            return getDisplayName();
        }
        
    }
    
    private static final class ProjectChildren extends Children.Keys {
        NbidlProject project;
        public ProjectChildren( NbidlProject project ) {
            setKeys( project.getSearchPath() );
            this.project= project;
        }
        
        protected Node[] createNodes( final Object object ) {
            Node node= DataFolder.findFolder( FileUtil.toFileObject( new File( (String)object) ) ).getNodeDelegate();
            try {
                node= new SearchPathNode( node, (String)object, project );
                return new Node[] { node };
            } catch ( final DataObjectNotFoundException e ) {
                return new Node[] { new AbstractNode( Children.LEAF ) {
                    public String getDisplayName() {
                        return "Exception: " + e.getMessage();
                    }
                } };
            }
        }
        
        private void update() {
            setKeys( project.getSearchPath() );
        }
    }
    
    private static final class SearchPathNode extends FilterNode {
        final NbidlProject project;
        final String folderName;
        public SearchPathNode( Node node, String folderName, NbidlProject project ) throws DataObjectNotFoundException {
            super( node, new FilterNode.Children(node),
                    //The projects system wants the project in the Node's lookup.
                    //NewAction and friends want the original Node's lookup.
                    //Make a merge of both
                    new ProxyLookup(new Lookup[] { Lookups.singleton(project),
                    node.getLookup() }));
            this.project = project;
            this.folderName= folderName;
        }
        
        public String getDisplayName() {
            return "["+super.getDisplayName()+"] "+ (String)folderName;
        }
        
        public Action[] getActions(boolean b) {
            List<Action> actions= new ArrayList( Arrays.asList(super.getActions(b)) );
            Action removeAction= Actions.remove();
            actions.add( 0, removeAction );
            actions.add( 1, null );
            return actions.toArray( new Action[actions.size()] );
        }
    }
    
    public Node findPath( Node node, Object target ) {
        NbidlProject p= (NbidlProject) node.getLookup().lookup( NbidlProject.class );
        if ( p==null ) {
            return null;
        } else {
            
            Node[] kids = node.getChildren().getNodes(true);

            for (int i = 0; i < kids.length; i++) {
                
                if ( target instanceof DataObject || target instanceof FileObject ) {
                    DataObject d = (DataObject) kids[i].getLookup().lookup(DataObject.class);
                    if (d == null) {
                        continue;
                    }
                    // Copied from org.netbeans.spi.java.project.support.ui.TreeRootNode.PathFinder.findPath:
                    FileObject kidFO = d.getPrimaryFile();
                    FileObject targetFO = target instanceof DataObject ? ((DataObject) target).getPrimaryFile() : (FileObject) target;
                    if (kidFO == targetFO) {
                        return kids[i];
                    } else if (FileUtil.isParentOf(kidFO, targetFO)) {
                        String relPath = FileUtil.getRelativePath(kidFO, targetFO);
                        List/*<String>*/ path = Collections.list(new StringTokenizer(relPath, "/")); // NOI18N
                        // XXX see original code for justification
                        path.set(path.size() - 1, targetFO.getName());
                        try {
                            Node result= NodeOp.findPath(kids[i], Collections.enumeration(path));
                            return result;
                            //return kids[i];
                        } catch (NodeNotFoundException e) {
                            return null;
                        }
                    }
                }
                
            }
        }
        
        return null;
    }
 

}
