package com.cottagesystems.nbidl.debugger.actions;

import com.cottagesystems.nbidl.session.SessionSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class StartIDLSessionAction extends CallableSystemAction {
    
    static StartIDLSessionAction instance;
    
    public StartIDLSessionAction() {
        if ( instance!=null ) throw new IllegalArgumentException("only one instance kludge!!!"); else instance=this;
    }
    
    public static synchronized StartIDLSessionAction getDefault() {
        if ( instance==null ) {
            instance= new StartIDLSessionAction();
        }
        return instance;
    }
    
    public void performAction() {
        SessionSupport.startDebugging();
    }
    
    public String getName() {
        return NbBundle.getMessage(StartIDLSessionAction.class, "CTL_StartIDLSessionAction");
    }
    
    protected String iconResource() {
        return null;
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
