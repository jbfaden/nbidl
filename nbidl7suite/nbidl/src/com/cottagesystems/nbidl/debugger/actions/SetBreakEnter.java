package com.cottagesystems.nbidl.debugger.actions;

import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.debugger.PvwaveStop;
import com.cottagesystems.nbidl.debugger.breakpoints.PvwaveBreakpoint;
import com.cottagesystems.nbidl.session.SessionSupport;
import com.cottagesystems.nbidl.syntax.UserRoutinesDataBase;
import com.cottagesystems.nbidl.util.PvwaveUtil;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Registry;
import org.netbeans.editor.Utilities;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class SetBreakEnter extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        EditorCookie c = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);
        
        DataObject dobj = (DataObject) activatedNodes[0].getCookie(DataObject.class);
        JTextComponent editor = Registry.getMostActiveComponent();
        int pos = editor.getCaretPosition();
        try {
            BaseDocument doc= Utilities.getDocument(editor);
            String selection = Utilities.getIdentifier( doc,pos);
            if (selection == null || selection.equals("")) {
                NotifyDescriptor.Message msg = new NotifyDescriptor.Message("Select a word!");
                DialogDisplayer.getDefault().notify(msg);
            } else {
                Procedure p= UserRoutinesDataBase.getInstance( dobj.getPrimaryFile() ).getProcedure(selection);
                if ( p==null ) {
                    DialogDisplayer.getDefault().notify( new NotifyDescriptor.Message("No such procedure!") );
                    return;
                }
                Line line= PvwaveUtil.getLine( p );
                if ( line==null ) {
                    DialogDisplayer.getDefault().notify( new NotifyDescriptor.Message("No such line!") );
                    return;
                }
                DebuggerManager.getDebuggerManager().addBreakpoint( new PvwaveBreakpoint( PvwaveStop.createStop(line) ) );
                
                int i0= Utilities.getRowStart( doc, pos );
                int i1= Utilities.getRowEnd( doc, pos );
                String lineStr= doc.getText( new int[] { i0, i1 } );
                
                SessionSupport.getSessionInstance().invokeCommand(lineStr);
                
            }
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ex);
        }
        
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(SetBreakEnter.class, "CTL_SetBreakEnter");
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

