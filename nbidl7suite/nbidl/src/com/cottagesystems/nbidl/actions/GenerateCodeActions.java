package com.cottagesystems.nbidl.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.util.actions.Presenter;

public final class GenerateCodeActions extends CookieAction implements Presenter.Popup {
    
    protected void performAction(Node[] activatedNodes) {
        EditorCookie c = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);
        System.err.println(c);
    }
    
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(GenerateCodeActions.class, "CTL_GenerateCodeActions");
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
        putValue(Action.NAME,NbBundle.getMessage(GenerateCodeActions.class, "CTL_GenerateCodeActions"));
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }

    public JMenuItem getPopupPresenter() {
        JMenu menu= new JMenu(NbBundle.getMessage(GenerateCodeActions.class, "CTL_GenerateCodeActions"));
        
        menu.add( new JMenuItem( InsertDocBlock.getInstance() ) );
        menu.add( new JMenuItem( InsertUnitTestAction.getInstance() ) );
        
        return menu;
    }
    
    
}

