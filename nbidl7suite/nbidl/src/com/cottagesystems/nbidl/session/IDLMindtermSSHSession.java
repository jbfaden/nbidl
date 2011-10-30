/*
 * IDLSSHSession.java
 *
 * Created on April 6, 2006, 11:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.session;

import com.cottagesystems.nbidl.debugger.IDLSessionEvent;
import java.awt.Component;
import java.awt.Dialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Jeremy
 */
public class IDLMindtermSSHSession extends AbstractIDLSession {
    SSHWrapper ssh;
    boolean started;
    
    /** Creates a new instance of IDLSSHSession */
    public IDLMindtermSSHSession() {
        started= false;
    }
    
    
    public void invokeCommand(String command) {
        logger.fine("IDLSSHSession.invokeCommand(\""+command+"\")") ;
        incrementCommandCount();
        if ( !started ) {
            NotifyDescriptor desc= new NotifyDescriptor.Message("Session is not started.");
            DialogDisplayer.getDefault().notify(desc);
            return;
        }
        String checkCommand= command.trim().toLowerCase();
        if ( checkCommand.equals("retall") ) {
            idlOutputHandler.fireSessionEvent( new IDLSessionEvent( IDLSessionEvent.TYPE_STOP ) );
        } else if ( checkCommand.startsWith("." ) && !checkCommand.startsWith(".com") ) {
            idlOutputHandler.fireSessionEvent( new IDLSessionEvent( IDLSessionEvent.TYPE_CONTINUE ) );
        }
        
        try {
            ssh.send( command );
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    private void handleException( Exception e ) {
        ErrorManager.getDefault().notify(e);
    }
    
    boolean connected= true;
    
    private void startOutputThread() {
        final byte[] buf2= new byte[2000];
        final StringBuffer buffer= new StringBuffer();
        
        Runnable runnable= new Runnable() {
            public void run() {
                while (connected) {
                    try {
                        int bytesRead= ssh.read(buf2);
                        if ( bytesRead==-1 ) connected=false;
                        if ( bytesRead<=0 ) continue;
                        idlOutputHandler.stdoutReceived( new String(buf2, 0, bytesRead) );
                    } catch ( IOException e ) {
                        connected= false;
                        handleException(e);
                    }
                }
                started= false;
            }
        };
        new Thread( runnable, "readOutputThread" ).start();
    }
    
    private void startInputThread() {
        final char[] buf2= new char[1024];
        Runnable run= new Runnable() {
            public void run() {
                while ( connected ) {
                    
                    try {
                        int charsRead= stdin.read(buf2);
                        if ( charsRead>0 ) {
                            ssh.send( new String( buf2, 0, charsRead ) );
                        }
                    } catch ( IOException e ) {
                        connected= false;
                        handleException(e);
                    }
                }
                started= false;
            }
        };
        new Thread(run,"readInputThread").start();
    }
    
    private void interact() {
        connected= true;
        startOutputThread();
        startInputThread();
    }
    
    private WizardDescriptor.Panel[] panels;
    WizardDescriptor wizardDescriptor;
    
    private void showWizard() {
        if ( wizardDescriptor==null ) {
            wizardDescriptor= new WizardDescriptor(getPanels());
        }
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
        wizardDescriptor.setTitle("Start IDL via SSH Session");
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            // do something
        }
    }
    
    private void loadMyRoutines() throws IOException {
        
        URL myRoutinesSrc= this.getClass().getResource( "/com/cottagesystems/nbidl/debugger/resources/nbidl_varprt.pro" );
        SessionSupport.compileSource( this, myRoutinesSrc );
        
        myRoutinesSrc= this.getClass().getResource( "/com/cottagesystems/nbidl/debugger/resources/nbidl_pprint.pro" );
        SessionSupport.compileSource( this, myRoutinesSrc );
        
    }
    
    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[] {
                new StartSessionWizardPanel1()
            };
            String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.FALSE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.FALSE);
                }
            }
        }
        return panels;
    }
    
    public void start() {
        
        if ( !started ) {
            try {
                
                showWizard();
                boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
                
                if ( cancelled ) return;
                
                SSHSessionSettings settings= (SSHSessionSettings)wizardDescriptor.getProperty( "settings" );
                String shellPrompt= settings.getShellPrompt();
                String idlPrompt= settings.getIdlPrompt();
                String password= (String) wizardDescriptor.getProperty("password");
                
                translate= settings.isTranslateFilename();
                this.remote= settings.getHostFileSystem().replaceAll("\\\\","/").replaceAll("//", "/" );
                this.local= settings.getLocalFileSystem().replaceAll("\\\\","/").replaceAll("//", "/" );
                
                String host= settings.getHost();;
                String username;
                int ipos= host.indexOf("@");
                if ( ipos==-1 ) {
                    username= System.getProperty("user.name");
                } else {
                    username= host.substring(0,ipos);
                    host= host.substring(ipos+1);
                }
                
                ssh = new MindTermSSHWrapper( username,  password, idlOutputHandler );
                
                try {
                    ssh.connect( host, 22 );
                } catch ( IllegalArgumentException e ) {
                    idlOutputHandler.stderrReceived("SSHSESSION: bad login\n");
                    return;
                }
                
                idlOutputHandler.stderrReceived("SSHSESSION: after connect...\n");
                
                String stuff= ssh.waitfor(shellPrompt,50000);
                
                if ( stuff.contains(shellPrompt) ) {
                    idlOutputHandler.stdoutReceived( stuff );
                    started= true;
                } else {
                    idlOutputHandler.stderrReceived( "SSHSESSION: nothing received after connect, check username and password.\n");
                    //interact();
                    return;
                }
                
                String unixCommandss= settings.getShellStartupCommands();
                String[] commands= unixCommandss.split("\n");
                
                for ( int i=0; i<commands.length; i++ ) {
                    ssh.send( commands[i] );
                    stuff= ssh.waitfor(shellPrompt,5000);
                    idlOutputHandler.stdoutReceived( stuff );
                    if ( !stuff.contains(shellPrompt) ) {
                        idlOutputHandler.stderrReceived("SSHSESSION: failed to get next unix prompt!\n");
                        break;
                    }
                }
                
                idlOutputHandler.stderrReceived( "SSHSESSION: done with shell commands....\n");
                
                String idlCommand= settings.getIdlCommand();
                ssh.send(idlCommand);
                
                idlOutputHandler.stderrReceived( "SSHSESSION: done with invoke IDL....\n");
                
                String commandss= (String) settings.getIdlStartupCommands();
                commands= commandss.split("\n");
                
                for ( int i=0; i<commands.length; i++ ) {
                    stuff= ssh.waitfor(idlPrompt,5000);
                    idlOutputHandler.stdoutReceived( stuff );
                    if ( !stuff.contains(idlPrompt) ) {
                        idlOutputHandler.stderrReceived("SSHSESSION: failed to get IDL prompt!\n");
                        break;
                    }
                    ssh.send( commands[i] );
                }
                
                stuff= ssh.waitfor(idlPrompt,5000);
                idlOutputHandler.stdoutReceived( stuff );
                
                idlOutputHandler.stderrReceived( "SSHSESSION: done with IDL commands....\n");
                
                loadMyRoutines();
                
                interact();
                
                idlOutputHandler.stderrReceived( "SSHSESSION: control passed to user....\n");
                
            } catch ( IOException e ) {
                idlOutputHandler.stderrReceived( e.getMessage() );
            }
        }
        
    }
    
    public void close() {
        try {
            connected= false;
            started= false;
            ssh.disconnect();
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    boolean translate;
    
    String local;
    String remote;
    
    /*
     * okay, this is a little weird because we might map the name from the
     * remote server to the local filename.
     */
    public FileObject getFileObjectForFilename( String filename ) {
        if ( translate && filename.startsWith(remote) ) {
            filename= local + filename.substring( remote.length() );
        }
        FileObject fo= FileUtil.toFileObject( new File(filename) ); // filename must be normalized.
        return fo;
    }
    
    public String getFilenameForFileObject( FileObject fo ) {
        String name= FileUtil.toFile(fo).getPath();
        if ( translate ) {
            name= name.replaceAll("\\\\","/");
            if ( name.startsWith(local) ) {
                name= remote + name.substring( local.length() );
            }
        }
        return name;
    }
}
