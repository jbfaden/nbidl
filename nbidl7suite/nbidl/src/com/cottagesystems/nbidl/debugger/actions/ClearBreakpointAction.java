package com.cottagesystems.nbidl.debugger.actions;

import com.cottagesystems.nbidl.actions.*;
import com.cottagesystems.nbidl.debugger.breakpoints.PvwaveBreakpoint;
import javax.swing.JEditorPane;
import javax.swing.text.StyledDocument;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerManager;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.text.NbDocument;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class ClearBreakpointAction extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        EditorCookie editorCookie = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);
        JEditorPane[] panes= editorCookie.getOpenedPanes();
        if ( panes.length==1 ) {
            int caretPosition= panes[0].getCaretPosition();
            StyledDocument styledDocument = editorCookie.getDocument();
            int lineNum = NbDocument.findLineNumber(styledDocument,caretPosition);
            Line line= editorCookie.getLineSet().getCurrent(lineNum);
            Breakpoint[] bps= DebuggerManager.getDebuggerManager().getBreakpoints();
            PvwaveBreakpoint theBp=null;
            for ( int i=0; i<bps.length; i++ ) {
                if ( bps[i] instanceof PvwaveBreakpoint ) {
                    PvwaveBreakpoint pvbp= (PvwaveBreakpoint)bps[i];
                    if ( line.equals( pvbp.getLine().getLineObject() ) ) {
                        theBp= pvbp; 
                        break;
                    }
                }
            }
            if ( theBp!=null ) {
                DebuggerManager.getDebuggerManager().removeBreakpoint( theBp );
            }
            
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(SetBreakpointAction.class, "CTL_ClearBreakpointAction");
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

