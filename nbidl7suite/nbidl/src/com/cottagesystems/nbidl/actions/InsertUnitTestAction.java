package com.cottagesystems.nbidl.actions;

import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class InsertUnitTestAction extends CookieAction {
    
    private static InsertUnitTestAction INSTANCE;
    
    public static synchronized InsertUnitTestAction getInstance() {
        if ( INSTANCE==null ) {
            INSTANCE= new InsertUnitTestAction();
        }
        return INSTANCE;
    }
    
    protected void performAction(Node[] activatedNodes) {
        EditorCookie c = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);
        System.err.println("ccc:"+c);
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(InsertUnitTestAction.class, "CTL_InsertUnitTestAction");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            EditorCookie.class
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

