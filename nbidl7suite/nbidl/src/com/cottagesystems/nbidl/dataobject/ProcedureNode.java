/*
 * ProcedureNode.java
 *
 * Created on March 17, 2006, 4:51 PM
 *
 *
 */

package com.cottagesystems.nbidl.dataobject;

import com.cottagesystems.nbidl.actions.ShowUsagesAction;
import com.cottagesystems.nbidl.debugger.actions.PvwaveReloadAction;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;
import org.openide.util.NbBundle;

/**
 *
 * @author Jeremy
 */
public class ProcedureNode extends AbstractNode {
    Procedure procedure;
    Action gotoAction;
    private static Logger logger= Logger.getLogger("pvwave");
    public ProcedureNode( Procedure procedure ) {
        super(Children.LEAF);
        this.procedure= procedure;
        this.gotoAction= new GotoAction();
    }
    
    public Node.Cookie getCookie(Class clazz) {
        Cookie cookie= super.getCookie(clazz);
        logger.fine("getCookie"+clazz+" -> "+String.valueOf(cookie));
        return cookie;
    }
    
    class GotoAction extends AbstractAction {
        
        GotoAction() {
            putValue( Action.NAME, NbBundle.getMessage(ProcedureNode.class, 
            "ACTION_OpenToRoutine") );
        }
        
        public boolean isEnabled() {
            return true;
        }

        public void actionPerformed(ActionEvent e) {
            ProceduresFileDataNode pnode= (ProceduresFileDataNode)getParentNode();            
            EditorCookie cookie= (EditorCookie) pnode.getCookie( EditorCookie.class );
            cookie.open();
            JEditorPane[] panes= cookie.getOpenedPanes();
            panes[0].setCaretPosition( procedure.getOffset() );
        }
    }
    
    public Action getPreferredAction() {
        return gotoAction;
    }

    public Action[] getActions( boolean context ) {
        return new Action[] { gotoAction, ShowUsagesAction.getInstance() };
    }
  
}
