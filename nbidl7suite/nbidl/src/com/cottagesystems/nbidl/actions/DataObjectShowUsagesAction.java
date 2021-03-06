package com.cottagesystems.nbidl.actions;

import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.dataobject.ProceduresFile;
import com.cottagesystems.nbidl.dataobject.ProceduresFileDataObject;
import com.cottagesystems.nbidl.syntax.PvwaveSyntaxScraper;
import com.cottagesystems.nbidl.syntax.UserRoutinesDataBase;
import java.io.IOException;
import java.util.Set;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

public final class DataObjectShowUsagesAction extends CookieAction {
    
    private static DataObjectShowUsagesAction INSTANCE= null;
    
    public static synchronized DataObjectShowUsagesAction getInstance() {
        if ( INSTANCE==null  ) {
            INSTANCE= new DataObjectShowUsagesAction();
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
            InputOutput inOut= IOProvider.getDefault().getIO("Usages: "+getLabel(activatedNodes),false);
            inOut.select();
            OutputWriter out= inOut.getOut();
            out.reset();
            for ( int i=0; i<activatedNodes.length; i++ ) {
                ProceduresFileDataObject c = (ProceduresFileDataObject) activatedNodes[i].getCookie(ProceduresFileDataObject.class);
                DataObject dso= (DataObject) activatedNodes[i].getCookie(DataObject.class);
                Set<FileObject> ff= dso.files();
                FileObject fo= ff.iterator().next();
                //FileObject fo=  (FileObject) activatedNodes[i].getCookie(FileObject.class);
                out.println("Usages of "+c.getName()+":");
                ProceduresFile f = PvwaveSyntaxScraper.parse(c.getPrimaryFile());
                for ( int j=0; j<f.procedureCount(); j++ ) {
                    Procedure p= f.getProcedure(j);
                    out.println("Usages of "+p.getName()+":");
                    UserRoutinesDataBase.getInstance(fo).showUsages( p.getName(), out, fo );
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
        return NbBundle.getMessage(DataObjectShowUsagesAction.class, "CTL_DataObjectShowUsagesAction");
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
