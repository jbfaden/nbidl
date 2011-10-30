/*
 * MindTermSSHWrapper.java
 *
 * Created on August 19, 2007, 10:05 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.session;

import com.mindbright.jca.security.SecureRandom;
import com.mindbright.ssh2.SSH2ConsoleRemote;
import com.mindbright.ssh2.SSH2FatalException;
import com.mindbright.ssh2.SSH2SimpleClient;
import com.mindbright.ssh2.SSH2TerminalAdapterImpl;
import com.mindbright.ssh2.SSH2Transport;
import com.mindbright.terminal.TerminalWindow;
import com.mindbright.util.RandomSeed;
import com.mindbright.util.SecureRandomAndPad;
import java.io.File;
import java.net.Socket;

/**
 *
 * @author jbf
 */
public class MindTermSSHWrapper extends SSHWrapper {
    
    String username, password;
    MindtermTerminalWindow termWindow;
    SSH2Transport transport;
    SSH2SimpleClient client;
    SSH2ConsoleRemote console;
    SSH2TerminalAdapterImpl termAdapter;
    IDLOutputHandler idlOutputHandler;
    
    /** Creates a new instance of MindTermSSHWrapper */
    public MindTermSSHWrapper( String username, String password, IDLOutputHandler idlOutputHandler ) {
        this.username= username;
        this.password= password;
        this.idlOutputHandler= idlOutputHandler;
    }
    
    /**
     * Create a random number generator. This implementation uses the
     * system random device if available to generate good random
     * numbers. Otherwise it falls back to some low-entropy garbage.
     */
    private static SecureRandomAndPad createSecureRandom() {
        byte[] seed;
        File devRandom = new File("/dev/urandom");
        if (devRandom.exists()) {
            RandomSeed rs = new RandomSeed("/dev/urandom", "/dev/urandom");
            seed = rs.getBytesBlocking(20);
        } else {
            seed = RandomSeed.getSystemStateHash();
        }
        return new SecureRandomAndPad(new SecureRandom(seed));
    }
    
    public void connect(String host, int port) {
        try {
            /*
             * Connect to the server and authenticate using plain password
             * authentication (if other authentication methods are needed
             * check other constructors for SSH2SimpleClient).
             */
            Socket serverSocket     = new Socket(host, port);
            transport = new SSH2Transport(serverSocket,
                    createSecureRandom());
            client = new SSH2SimpleClient(transport,
                    username, password);
            
            /*
             * Create the remote console to use for command execution.
             */
            console =
                    new SSH2ConsoleRemote(client.getConnection());
            
            termWindow= new MindtermTerminalWindow( idlOutputHandler ) ;
            
            termAdapter =
                    new SSH2TerminalAdapterImpl( termWindow );
            
            console.terminal( termAdapter );
            
        } catch ( SSH2FatalException e ) {
            throw new IllegalArgumentException(e);
            
        } catch ( Exception e ) {
            throw new RuntimeException(e);
        }
        
    }
    
        
    public void send(String command) {
        termAdapter.sendBytes( (command+"\n").getBytes() );
    }
    
    public int read(byte[] buf) {
        return termWindow.read(buf);
    }
    
    public void disconnect() {
        transport.normalDisconnect("User disconnect");
    }
    
    
}
