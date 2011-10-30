package com.cottagesystems.nbidl.actions;

import com.cottagesystems.nbidl.actions.*;
import com.cottagesystems.nbidl.syntax.UserRoutinesDataBase;
import java.io.IOException;
import org.openide.ErrorManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class ShowUsagesSummary extends CallableSystemAction {
    
    public void performAction() {
        try {
            UserRoutinesDataBase.getInstance().showUsagesSummary();
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(ShowUsagesSummary.class, "CTL_ShowUsagesSummary");
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
