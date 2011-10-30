package com.cottagesystems.nbidl.debugger.actions;

import com.cottagesystems.nbidl.actions.*;
import com.cottagesystems.nbidl.syntax.UserRoutinesDataBase;
import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.dataobject.ProceduresFile;
import com.cottagesystems.nbidl.session.SessionSupport;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Registry;
import org.netbeans.editor.Utilities;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class ExecuteLineAction extends CookieAction {
    static final Logger logger= Logger.getLogger("pvwave");
    
    
    protected void performAction(Node[] activatedNodes) {
        if ( SessionSupport.getSessionInstance().isStarted() ) {
            DataObject c = (DataObject) activatedNodes[0].getCookie(DataObject.class);
            JTextComponent editor = Registry.getMostActiveComponent();
            int pos = editor.getCaretPosition();
            try {
                BaseDocument doc= Utilities.getDocument(editor);
                int i0= Utilities.getRowStart( doc, pos );
                int i1= Utilities.getRowEnd( doc, pos );
                String line= doc.getText( new int[] { i0, i1 } );
                
                SessionSupport.getSessionInstance().invokeCommand(line);
                
            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        } else {
            NotifyDescriptor.Message msg = new NotifyDescriptor.Message("Session is not started");
            DialogDisplayer.getDefault().notify(msg);  
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(ExecuteLineAction.class, "CTL_ExecuteLineAction");
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
