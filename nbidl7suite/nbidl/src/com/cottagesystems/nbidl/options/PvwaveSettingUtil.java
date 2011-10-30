package com.cottagesystems.nbidl.options;

import com.cottagesystems.nbidl.session.SSHSessionSettings;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
/**
 *
 * @author Administrator
 */
public class PvwaveSettingUtil {
    FileObject folderObject = null;
    FileObject SettingFile=null;
    PvwaveSetting gsetting = new PvwaveSetting();
    FileLock lock =null;
    /** Creates a new instance of GSettingUtil */
    
    public PvwaveSettingUtil() {
        folderObject= Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Settings");
        if (folderObject==null){
            try {
                folderObject=Repository.getDefault().getDefaultFileSystem().getRoot().createFolder("Settings");
                storeSetting(gsetting);
            } catch (IOException ex) {
                ex.printStackTrace();
                // TODO file can not be created , do something about it
            }
        }
    }
    public boolean storeSetting(PvwaveSetting settings){
        try {
            
            
            if (folderObject.getFileObject("nbidl","xml")==null){
                SettingFile= folderObject.createData("nbidl","xml");
                
            }
            SettingFile= folderObject.getFileObject("nbidl","xml");
            
            lock = SettingFile.lock();
            
            XMLEncoder e= new XMLEncoder( SettingFile.getOutputStream( lock ) );
            e.writeObject( settings );
            e.close();
            lock.releaseLock();
            
        } catch (IOException ex) {
            // TODO file can not be created , do something about it
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    
    public PvwaveSetting retrieveSetting(  ) {
        try {
            
            FileObject settingFile= folderObject.getFileObject( "nbidl", "xml" );
            
            PvwaveSetting settings;
            
            if ( settingFile==null ) {
                settings= new PvwaveSetting();
                storeSetting(  settings );
                return settings;
            }
            
            XMLDecoder decode= new XMLDecoder( settingFile.getInputStream() );
            settings=   (PvwaveSetting) decode.readObject();
            decode.close();
            return settings;
            
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
            
        }
    }
    
    
    public static PvwaveSettingUtil getDefault() {
        return new PvwaveSettingUtil();
    }
}
