package com.cottagesystems.nbidl.dataobject;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;

public class ProceduresFileDataLoader extends UniFileLoader {
    
    public static final String REQUIRED_MIME = "text/x-application-pvwave";
    
    private static final long serialVersionUID = 1L;
    
    public ProceduresFileDataLoader() {
        super("com.cottagesystems.nbidl.ProceduresFileDataObject");
    }
    
    public String defaultDisplayName() {
        return NbBundle.getMessage(ProceduresFileDataLoader.class, "LBL_ProceduresFile_loader_name");
    }
    
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(REQUIRED_MIME);
    }
    
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new ProceduresFileDataObject(primaryFile, this);
    }
    
    protected String actionsContext() {
        return "Loaders/" + REQUIRED_MIME + "/Actions";
    }
    
}
