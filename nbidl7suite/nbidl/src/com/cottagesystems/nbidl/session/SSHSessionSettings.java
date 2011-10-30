/*
 * SSHSessionSettings.java
 *
 * Created on July 18, 2007, 2:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.session;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

/**
 *
 * @author jbf
 */
public class SSHSessionSettings implements Serializable {
    
    /**
     * Creates a new instance of SSHSessionSettings
     */
    public SSHSessionSettings() {
        String os= System.getProperty("os.name");
        String username= System.getProperty("user.name");
        if ( username.equals("") ) username="USER";
        
        if ( os.startsWith("Windows") ) {
            host= username + "@linuxhost";
            idlCommand= "idl";
            translateFilename= false;
            shellStartupCommands= "setenv DISPLAY localhost:1.0";
        } else if ( os.equals("Mac OS") ) {
            host= username + "@localhost";
            idlCommand= "/Applications/rsi/idl/bin/idl";
        } else {
            host= username + "@localhost";
            idlCommand= "/Applications/rsi/idl/bin/idl";            
            shellStartupCommands= "setenv DISPLAY :0.0";
        }
            
    }
    
    /**
     * Holds value of property host.
     */
    private String host;
    
    /**
     * Getter for property host.
     * @return Value of property host.
     */
    public String getHost() {
        return this.host;
    }
    
    /**
     * Setter for property host.
     * @param host New value of property host.
     */
    public void setHost(String host) {
        this.host = host;
    }
    
    /**
     * Holds value of property password.
     */
    private String password="";
    
    /**
     * Getter for property password.
     * @return Value of property password.
     */
    public String getPassword() {
        return this.password;
    }
    
    /**
     * Setter for property password.
     * @param password New value of property password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Holds value of property savePassword.
     */
    private boolean savePassword= false;
    
    /**
     * Getter for property savePassword.
     * @return Value of property savePassword.
     */
    public boolean isSavePassword() {
        return this.savePassword;
    }
    
    /**
     * Setter for property savePassword.
     * @param savePassword New value of property savePassword.
     */
    public void setSavePassword(boolean savePassword) {
        this.savePassword = savePassword;
    }
    
    /**
     * Holds value of property shellPrompt.
     */
    private String shellPrompt="$";
    
    /**
     * Getter for property unixPrompt.
     * @return Value of property unixPrompt.
     */
    public String getShellPrompt() {
        return this.shellPrompt;
    }
    
    /**
     * Setter for property unixPrompt.
     * @param unixPrompt New value of property unixPrompt.
     */
    public void setShellPrompt(String shellPrompt) {
        this.shellPrompt = shellPrompt;
    }
    
    /**
     * Holds value of property idlPrompt.
     */
    private String idlPrompt="IDL>";
    
    /**
     * Getter for property idlPrompt.
     * @return Value of property idlPrompt.
     */
    public String getIdlPrompt() {
        return this.idlPrompt;
    }
    
    /**
     * Setter for property idlPrompt.
     * @param idlPrompt New value of property idlPrompt.
     */
    public void setIdlPrompt(String idlPrompt) {
        this.idlPrompt = idlPrompt;
    }
    
    
    
    /**
     * Holds value of property translateFilename.
     */
    private boolean translateFilename= false;
    
    /**
     * Getter for property translateFilename.
     * @return Value of property translateFilename.
     */
    public boolean isTranslateFilename() {
        return this.translateFilename;
    }
    
    /**
     * Setter for property translateFilename.
     * @param translateFilename New value of property translateFilename.
     */
    public void setTranslateFilename(boolean translateFilename) {
        this.translateFilename = translateFilename;
    }
    
    /**
     * Holds value of property hostFileSystem.
     */
    private String hostFileSystem="/home/USER/";
    
    /**
     * Getter for property hostFileSystem.
     * @return Value of property hostFileSystem.
     */
    public String getHostFileSystem() {
        return this.hostFileSystem;
    }
    
    /**
     * Setter for property hostFileSystem.
     * @param hostFileSystem New value of property hostFileSystem.
     */
    public void setHostFileSystem(String hostFileSystem) {
        this.hostFileSystem = hostFileSystem;
    }
    
    /**
     * Holds value of property localFileSystem.
     */
    private String localFileSystem="L:/";
    
    /**
     * Getter for property localFileSystem.
     * @return Value of property localFileSystem.
     */
    public String getLocalFileSystem() {
        return this.localFileSystem;
    }
    
    /**
     * Setter for property localFileSystem.
     * @param localFileSystem New value of property localFileSystem.
     */
    public void setLocalFileSystem(String localFileSystem) {
        this.localFileSystem = localFileSystem;
    }
    
    /**
     * Holds value of property shellStartupCommands.
     */
    private String shellStartupCommands="";
    
    /**
     * Getter for property shellStartupCommands.
     * @return Value of property shellStartupCommands.
     */
    public String getShellStartupCommands() {
        return this.shellStartupCommands;
    }
    
    /**
     * Setter for property shellStartupCommands.
     * @param shellStartupCommands New value of property shellStartupCommands.
     */
    public void setShellStartupCommands(String shellStartupCommands) {
        this.shellStartupCommands = shellStartupCommands;
    }
    
    /**
     * Holds value of property idlStartupCommands.
     */
    private String idlStartupCommands="";
    
    /**
     * Getter for property idlStartupCommands.
     * @return Value of property idlStartupCommands.
     */
    public String getIdlStartupCommands() {
        return this.idlStartupCommands;
    }
    
    /**
     * Setter for property idlStartupCommands.
     * @param idlStartupCommands New value of property idlStartupCommands.
     */
    public void setIdlStartupCommands(String idlStartupCommands) {
        this.idlStartupCommands = idlStartupCommands;
    }
    
    private static FileObject mkdirs( String folder ) throws IOException {
        String[] ss= folder.split("/");
        String s= "";
        FileObject folderObject=null;
        for ( int i=0; i<ss.length; i++  ) {
            s+= ss[i];
            folderObject= Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(s);
            if ( folderObject==null ) {
                System.err.println("create "+s);
                folderObject=Repository.getDefault().getDefaultFileSystem().getRoot().createFolder(s);
                
            }
            s= s+"/";
        }
        return folderObject;
    }
    
    private synchronized static FileObject  getSettingsFolder() throws IOException {
        FileObject folderObject= Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Settings/sshSession");
        if (folderObject==null){
            folderObject= mkdirs( "Settings/sshSession" );
        }
        return folderObject;
    }
    
    public synchronized static void removeProfile( String profile ) {
        FileObject settingFile;
        try {
            settingFile = getSettingsFolder().getFileObject("SSHSession." + profile, "cfg");
            if ( settingFile==null ) return;
            settingFile.delete();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public synchronized static void storeSetting( String profile, SSHSessionSettings settings ) {
        try {
            {
                FileObject settingFile= getSettingsFolder().getFileObject( "SSHSession."+profile, "cfg" );
                if ( settingFile==null ) {
                    settingFile= getSettingsFolder().createData("SSHSession."+profile, "cfg");
                }
                FileLock lock = settingFile.lock();
                ObjectOutputStream objectOutStr = new ObjectOutputStream(settingFile.getOutputStream(lock));
                objectOutStr.writeObject(settings);
                objectOutStr.close();
                lock.releaseLock();
            }
            {
                FileObject settingFile= getSettingsFolder().getFileObject( "SSHSession."+profile, "xml" );
                if ( settingFile==null ) {
                    settingFile= getSettingsFolder().createData("SSHSession."+profile, "xml");
                }
                FileLock lock = settingFile.lock();
                XMLEncoder e= new XMLEncoder( settingFile.getOutputStream( lock ) );
                e.writeObject( settings );
                e.close();
                lock.releaseLock();
            }
        } catch ( IOException ex ) {
            throw new RuntimeException(ex);
        }
    }
    
    public synchronized static SSHSessionSettings retrieveSetting( String profile ) {
        try {
            
            FileObject settingFile= getSettingsFolder().getFileObject( "SSHSession."+profile, "xml" );
            
            SSHSessionSettings settings;
            
            if ( settingFile==null ) {
                FileObject settingFile2= getSettingsFolder().getFileObject( "SSHSession."+profile, "cfg" );
                
                if ( settingFile==null ) {
                    settings= new SSHSessionSettings();
                    storeSetting( profile, settings );
                    return settings;
                } else {
                    ObjectInputStream objectInStr = new ObjectInputStream(settingFile2.getInputStream());
                    settings=   (SSHSessionSettings) objectInStr.readObject();
                    objectInStr.close();
                    storeSetting( profile, settings );  // save it in new format
                    settingFile= getSettingsFolder().getFileObject( "SSHSession."+profile, "xml" );
                    
                    if ( settingFile==null ) {
                        System.err.println("unable to create new format file");
                        return settings;
                    } else {
                        settingFile2.delete();
                    }           
                }                
            }
            
            XMLDecoder decode= new XMLDecoder( settingFile.getInputStream() );
            settings=   (SSHSessionSettings) decode.readObject();
            decode.close();
            return settings;
            
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
            
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    
    
    /**
     * Holds value of property idlCommand.
     */
    private String idlCommand;
    
    /**
     * Getter for property idlCommand.
     * @return Value of property idlCommand.
     */
    public String getIdlCommand() {
        return this.idlCommand;
    }
    
    /**
     * Setter for property idlCommand.
     * @param idlCommand New value of property idlCommand.
     */
    public void setIdlCommand(String idlCommand) {
        this.idlCommand = idlCommand;
    }
    
    public static List getProfiles() {
        try {
            FileObject[] kids;
            kids = getSettingsFolder().getChildren();
            ArrayList result= new ArrayList();
            int prefix= "SSHSession.".length();
            for ( int i=0; i<kids.length; i++ ) {
                String profileName= kids[i].getName();
                String s= profileName.substring( prefix );
                if ( !result.contains( s ) ) result.add( s );
            }
            return result;
            
        } catch (IOException ex) {
            ex.printStackTrace();
            return new ArrayList();
        }
    }
}
