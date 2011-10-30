package com.cottagesystems.nbidl.actions;

import com.cottagesystems.nbidl.dataobject.ProceduresFileDataNode;
import com.cottagesystems.nbidl.dataobject.ProceduresFileDataObject;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.util.actions.SystemAction;

public final class OpenFellowAction extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        ProceduresFileDataNode dataNode= (ProceduresFileDataNode)activatedNodes[0];
        FileObject fo= ((ProceduresFileDataObject)dataNode.getDataObject()).getPrimaryFile();
        FileObject containingFolder= fo.getParent();
        FileSystem fs;
        
        // get the Action for opening a file open dialog, in this guy's directory.
        try {
            fs = containingFolder.getFileSystem();
            SystemAction[] fsas= fs.getActions();
            for ( int i=0; i<fsas.length; i++ ) {
                Logger.getLogger("nbidl").info(fsas.toString());
            }
        } catch (FileStateInvalidException ex) {
            ex.printStackTrace();
            // do nothing
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(OpenFellowAction.class, "CTL_OpenFellow");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            DataObject.class
        };
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}

