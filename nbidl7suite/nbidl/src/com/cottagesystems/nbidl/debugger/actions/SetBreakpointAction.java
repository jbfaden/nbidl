package com.cottagesystems.nbidl.debugger.actions;

import com.cottagesystems.nbidl.actions.*;
import com.cottagesystems.nbidl.debugger.PvwaveStop;
import com.cottagesystems.nbidl.debugger.breakpoints.PvwaveBreakpoint;
import javax.swing.JEditorPane;
import javax.swing.text.StyledDocument;
import org.netbeans.api.debugger.DebuggerManager;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.text.NbDocument;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class SetBreakpointAction extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        EditorCookie editorCookie = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);
        JEditorPane[] panes= editorCookie.getOpenedPanes();
        if ( panes.length==1 ) {
            int caretPosition= panes[0].getCaretPosition();
            StyledDocument styledDocument = editorCookie.getDocument();
            int lineNum = NbDocument.findLineNumber(styledDocument,caretPosition);
            
            Line line= editorCookie.getLineSet().getCurrent(lineNum);
            DebuggerManager.getDebuggerManager().addBreakpoint( new PvwaveBreakpoint( PvwaveStop.createStop(line) ) );
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(SetBreakpointAction.class, "CTL_SetBreakpointAction");
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

