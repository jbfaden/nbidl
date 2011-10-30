/*
 * Session.java
 *
 * Created on March 23, 2006, 12:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.session;

import java.io.Reader;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jeremy
 */
public interface Session {
    class Breakpoint {
    }
    class Response {
    }
    void start();
    void reload( String file );
    Breakpoint setBreakpoint( String file, int lineNum );
    void clearBreakpoint( Breakpoint breakpoint );
    void invokeCommand( String command );
    void invokeCommandWait(String command);
    
    void setOutputHandler( IDLOutputHandler handler );
    IDLOutputHandler getOutputHandler();
    void setStdin( Reader reader );
    
    void close();

    boolean isStarted();

    FileObject getFileObjectForFilename( String filename );
    String getFilenameForFileObject( FileObject fileobject );
    
    /**
     * returns the associated project, if available. Null otherwise
     */
    Project getProject( );
    void setProject(Project c);
}
