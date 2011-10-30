package com.cottagesystems.nbidl.debugger.actions;

import com.cottagesystems.nbidl.actions.*;
import com.cottagesystems.nbidl.session.Session;
import com.cottagesystems.nbidl.session.SessionSupport;
import java.io.IOException;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class PvwaveReloadAction extends CookieAction {
    
    private static PvwaveReloadAction INSTANCE= null;
    
    public static synchronized PvwaveReloadAction getInstance() {
        if ( INSTANCE==null  ) {
            INSTANCE= new PvwaveReloadAction();
        }
        return INSTANCE;
    }
    
    protected void performAction(Node[] activatedNodes) {
        for ( int i=0; i<activatedNodes.length; i++ ) {
            DataObject c = (DataObject) activatedNodes[i].getCookie(DataObject.class);
            EditorCookie cc= (EditorCookie) activatedNodes[i].getCookie( EditorCookie.class );
            try {
                cc.saveDocument();
                
                Session session= SessionSupport.getSessionInstance();
                session.reload( session.getFilenameForFileObject(c.getPrimaryFile()));
            } catch ( IOException e ) {
                ErrorManager.getDefault().notify(e);
            }
        }
        //SessionSupport.startDebugging(activatedNodes[0]);
    }
    
    protected int mode() {
        return CookieAction.MODE_ANY;
    }
    
    public String getName() {
        return NbBundle.getMessage(PvwaveReloadAction.class, "CTL_PvwaveReloadAction");
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

