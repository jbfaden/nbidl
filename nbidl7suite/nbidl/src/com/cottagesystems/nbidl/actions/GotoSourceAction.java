package com.cottagesystems.nbidl.actions;

import com.cottagesystems.nbidl.syntax.UserRoutinesDataBase;
import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.dataobject.ProceduresFile;
import com.cottagesystems.nbidl.util.PvwaveUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
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

public final class GotoSourceAction extends CookieAction {
    static final Logger logger= Logger.getLogger("pvwave");
    
    /**
     * identify the Line object by looking at the PathExplorer
     */
    private Line findSourceEditor( FileObject context, String selection ) throws DataObjectNotFoundException {
        Line theLine= null;
        Procedure pro= UserRoutinesDataBase.getInstance(context).getProcedure( selection );
        if (  pro!=null ) {
            theLine= PvwaveUtil.getLine( pro );
        }
        return theLine;
    }
    
    
    
    private void showSource( FileObject context, String selection ) {
        try {
            Line line= findSourceEditor( context, selection );
            //if ( line==null )  line= findSourceSession( selection );
            if ( line!=null ) line.show( Line.SHOW_GOTO );
        } catch (DataObjectNotFoundException ex) {
            ErrorManager.getDefault().notify(ex);
       // } catch (FileNotFoundException ex) {
       //     ErrorManager.getDefault().notify(ex);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    protected void performAction(Node[] activatedNodes) {
        DataObject c = (DataObject) activatedNodes[0].getCookie(DataObject.class);
        JTextComponent editor = Registry.getMostActiveComponent();
        int pos = editor.getCaretPosition();
        try {
            String selection = Utilities.getIdentifier(Utilities.getDocument(editor),pos);
            if (selection == null || selection.equals("")) {
                NotifyDescriptor.Message msg = new NotifyDescriptor.Message("Select a word!");
                DialogDisplayer.getDefault().notify(msg);
            } else {
                showSource(c.getPrimaryFile(),selection);
            }
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(GotoSourceAction.class, "CTL_GotoSourceAction");
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
