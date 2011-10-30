package com.cottagesystems.nbidl.actions;

import com.cottagesystems.nbidl.syntax.UserRoutinesDataBase;
import com.cottagesystems.nbidl.syntax.PvwaveTokenContext;
import com.cottagesystems.nbidl.syntax.SyntaxUtil;
import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.editor.Registry;
import org.netbeans.editor.Utilities;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class ShowUsagesAction extends CookieAction {
    
    private static ShowUsagesAction INSTANCE= null;
    
    public static synchronized ShowUsagesAction getInstance() {
        if ( INSTANCE==null  ) {
            INSTANCE= new ShowUsagesAction();
        }
        return INSTANCE;
    }
    
    protected void performAction(Node[] activatedNodes) {
        
        JTextComponent editor = Registry.getMostActiveComponent();
        int pos = editor.getCaretPosition();
        try {
            SyntaxUtil.TokenSentence tok= SyntaxUtil.getTokenAt( Utilities.getDocument(editor), pos );
            if ( tok.tokenId==null ) {
                NotifyDescriptor.Message msg = new NotifyDescriptor.Message("Select a user function or a common block name, not <whitespace>");
                DialogDisplayer.getDefault().notify(msg);
            } else {
                DataObject c = (DataObject) activatedNodes[0].getCookie(DataObject.class);
                FileObject fo= c.getPrimaryFile();
                
                UserRoutinesDataBase udb= UserRoutinesDataBase.getInstance(fo);
                
                if ( tok.tokenId.getNumericID()==PvwaveTokenContext.USERFUNC_ID ) {
                    udb.showUsages( tok.text);
                } else if ( tok.index > 0 && tok.tokens.get(tok.index-1).getNumericID()==PvwaveTokenContext.COMMON_ID ) {
                    String key= "COMMON "+tok.text;
                    udb.showUsages( key );
                } else {
                    String type= tok.tokenId.getName();
                    NotifyDescriptor.Message msg = new NotifyDescriptor.Message("Select a user function or a common block name, not <"+type+">");
                    DialogDisplayer.getDefault().notify(msg);
                }
            }
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(DumpSyntax.class, "CTL_ShowUsagesAction");
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

