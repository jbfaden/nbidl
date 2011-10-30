/*
 * GetResponseIDLOutputHandler.java
 *
 * Created on May 8, 2006, 9:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.session;

/**
 * Grabs output from IDL, collecting it for later use.  The trick is to make something that is asynchronous synchronous.
 *
 * @author Jeremy
 */
public class GetResponseIDLOutputHandler extends IDLOutputHandler {

    StringBuffer responseBuffer;
    
    public String getResponse() {
        String result= responseBuffer.toString();
        responseBuffer.delete(0,responseBuffer.length());
        return result;
    }
    
    public GetResponseIDLOutputHandler( IDLOutputHandler handler ) {
        super( handler.stdout, handler.stderr );
        responseBuffer= new StringBuffer(400);
    }

    public void stdoutReceived(String text) {
        super.stdoutReceived(text);
        responseBuffer.append(text);
    }

    public void stderrReceived(String text) {
        super.stderrReceived(text);
        responseBuffer.append(text);
    }

    public StringBuffer getResponseBuffer() {
        return responseBuffer;
    }
    
}
