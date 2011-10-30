package com.cottagesystems.nbidl.actions;

import com.cottagesystems.nbidl.completion.CompletionSupport;
import com.cottagesystems.nbidl.dataobject.Procedure;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class ShowCurrentProcedure extends CookieAction {

    public ShowCurrentProcedure() {

    }
    
    protected void performAction(Node[] activatedNodes) {
        EditorCookie c = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);
        Procedure p= CompletionSupport.getCarotProcedure();
        DialogDisplayer.getDefault().notify( new NotifyDescriptor.Message("p="+p) );
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(ShowCurrentProcedure.class, "CTL_ShowCurrentProcedure");
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

