package com.cottagesystems.nbidl.projecttype.actions;

import com.cottagesystems.nbidl.debugger.PvwaveDebugger;
import com.cottagesystems.nbidl.session.SessionSupport;
import org.netbeans.api.project.Project;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class StartIdlSession extends CookieAction {
    
    static StartIdlSession INSTANCE;
    
    static public synchronized StartIdlSession getInstance() {
        if ( INSTANCE==null ) INSTANCE= new StartIdlSession();
        return INSTANCE;
    }
    
    protected void performAction(Node[] activatedNodes) {
        Project c = (Project) activatedNodes[0].getLookup().lookup(Project.class);
        PvwaveDebugger debug= SessionSupport.startDebugging();
        SessionSupport.getSessionInstance().setProject(c);
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(StartIdlSession.class, "CTL_StartIdlSession");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            Project.class
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

