package com.cottagesystems.nbidl.dataobject;

import com.cottagesystems.nbidl.actions.DataObjectShowDependenciesAction;
import com.cottagesystems.nbidl.actions.DataObjectShowUsagesAction;
import com.cottagesystems.nbidl.debugger.actions.PvwaveReloadAction;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.Action;
import org.netbeans.editor.Registry;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

public class ProceduresFileDataNode extends DataNode {
    
    private static final String IMAGE_ICON_BASE = "com/cottagesystems/nbidl/dataobject/icon2.png";
    
    ProceduresFileChildren children;
    
    public ProceduresFileDataNode(ProceduresFileDataObject obj,Children children) {
        super( obj, children );
        setIconBaseWithExtension(IMAGE_ICON_BASE);
    }
    
    
//    /** Creates a property sheet. */
//    protected Sheet createSheet() {
//        Sheet s = super.createSheet();
//        Sheet.Set ss = s.get(Sheet.PROPERTIES);
//        if (ss == null) {
//            ss = Sheet.createPropertiesSet();
//            s.put(ss);
//        }
//        // TODO add some relevant properties: ss.put(...)
//        return s;
//    }

    public Action[] getActions(boolean b) {
        Action[] action= super.getActions(b);
        ArrayList list= new ArrayList( Arrays.asList(action) );
        Action anaction= (Action) Lookup.getDefault().lookup( DataObjectShowUsagesAction.class );
        list.add( DataObjectShowUsagesAction.getInstance() );
        list.add( DataObjectShowDependenciesAction.getInstance() );
        list.add( PvwaveReloadAction.getInstance() );
        return (Action[]) list.toArray( new Action[list.size()] );
    }

    

}
