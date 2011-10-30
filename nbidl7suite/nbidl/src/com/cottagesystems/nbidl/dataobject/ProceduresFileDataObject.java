package com.cottagesystems.nbidl.dataobject;

import com.cottagesystems.nbidl.syntax.UserRoutinesDataBase;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.Children;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.DataEditorSupport;

public class ProceduresFileDataObject extends MultiDataObject {
    
    public ProceduresFileDataObject(final FileObject pf, ProceduresFileDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        CloneableEditorSupport editorCookie= DataEditorSupport.create(this, getPrimaryEntry(), cookies);
        cookies.add((Node.Cookie)editorCookie);
        cookies.add(new PvwaveSupport(this,(EditorCookie)editorCookie));
        addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ( evt.getPropertyName().equals(ProceduresFileDataObject.this.PROP_MODIFIED) && evt.getNewValue().equals(Boolean.FALSE) ) {
                    try {
                        UserRoutinesDataBase.getInstance(pf).scanFile(getPrimaryFile(),null);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } );
    }
        
    protected Node createNodeDelegate() {
        PvwaveCookie cookie= (PvwaveCookie) getCookie(PvwaveCookie.class);
        Children children;
        if (cookie!=null ) {
            children= new ProceduresFileChildren(cookie);
        } else {
            children= Children.LEAF;
        }
        return new ProceduresFileDataNode(this,children);
    }
    
}
