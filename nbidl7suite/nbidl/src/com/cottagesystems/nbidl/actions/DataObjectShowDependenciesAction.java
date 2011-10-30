package com.cottagesystems.nbidl.actions;

import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.dataobject.ProceduresFile;
import com.cottagesystems.nbidl.dataobject.ProceduresFileDataObject;
import com.cottagesystems.nbidl.syntax.PvwaveSyntaxScraper;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

public final class DataObjectShowDependenciesAction extends CookieAction {
    
    private static DataObjectShowDependenciesAction INSTANCE= null;
    
    public static synchronized DataObjectShowDependenciesAction getInstance() {
        if ( INSTANCE==null  ) {
            INSTANCE= new DataObjectShowDependenciesAction();
        }
        return INSTANCE;
    }
    
    private String getLabel( Node[] activatedNodes ) {
        StringBuffer s= new StringBuffer();
        for ( int i=0; i<activatedNodes.length; i++ ) {
            ProceduresFileDataObject c = (ProceduresFileDataObject) activatedNodes[i].getCookie(ProceduresFileDataObject.class);
            s.append(", "+c.getName()+".pro");
        }
        return s.length()==0 ? "" : s.substring(2);
    }
    
    protected void performAction(Node[] activatedNodes) {
        try {
            InputOutput inOut= IOProvider.getDefault().getIO("Dependencies: " + getLabel(activatedNodes),false);
            inOut.select();
            OutputWriter out= inOut.getOut();
            out.reset();
            for ( int i=0; i<activatedNodes.length; i++ ) {
                ProceduresFileDataObject c = (ProceduresFileDataObject) activatedNodes[i].getCookie(ProceduresFileDataObject.class);
                out.println("Dependencies of "+c.getName()+":");
                ProceduresFile f = PvwaveSyntaxScraper.parse(c.getPrimaryFile());
                for ( int j=0; j<f.procedureCount(); j++ ) {
                    Procedure p= f.getProcedure(j);
                    out.println("Dependencies of "+p.getName()+":");
                    Map map= p.getAllUsages();
                    for ( Iterator it= map.keySet().iterator(); it.hasNext(); ) {
                        String s=(String) it.next();
                        out.println( s );
                    }
                }
            }
            
        } catch ( IOException e ) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_ALL;
    }
    
    public String getName() {
        return NbBundle.getMessage(DataObjectShowDependenciesAction.class, "CTL_DataObjectShowDependenciesAction");
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

