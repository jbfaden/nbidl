/*
 * SSHWrapper.java
 *
 * Created on August 19, 2007, 10:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.session;

import java.io.IOException;

/**
 *
 * @author jbf
 */
public abstract class SSHWrapper {
    
    /**
     * @throws IllegalArgumentException if the credentials are incorrect
     */
    abstract void connect( String host, int port ) throws IllegalArgumentException;
    
    abstract void send( String command ) throws IOException ;
    
    private int matchPos;		// current position in the match
    private byte[] match;		// the current bytes to look for
    private boolean done = true;	// nothing to look for!
    
    /**
     * Setup the parser using the passed string.
     * @param match the string to look for
     */
    public void setup(String match) {
        if(match == null) return;
        this.match = match.getBytes();
        matchPos = 0;
        done = false;
    }
    
    private boolean  match( byte[] s, int length ){
        if (done) return true;
        
        for(int i = 0; !done && i < length; i++) {
            if(s[i] == match[matchPos]) {
                // the whole thing matched so, return the match answer
                // and reset to use the next match
                if(++matchPos >= match.length) {
                    done = true;
                    return true;
                }
            } else
                matchPos = 0; // get back to the beginning
        }
        return false;
    }
    
    /** 
     * returns when the search string is found, or when timeout is reached.
     * @param search the string to search for.
     * @param timeout the millseconds to wait, or 0 if no timeout is used.
     */
    public String waitfor( String search, int timeout ) throws IOException {
        
        byte[] b1 = new byte[1];
        int n = 0;
        StringBuffer ret = new StringBuffer();
        String current;
        
        if ( timeout==0 ) timeout=Integer.MAX_VALUE;
        
        long t0= System.currentTimeMillis();
        
        setup( search );
        
        while(n >= 0 && ( timeout > ( System.currentTimeMillis() - t0 ) ) ) {
            n = read(b1);
            if(n > 0) {
                current = new String( b1, 0, n );
                ret.append( current );
                
                //System.err.println( "looking for "+search+" in :"+ret );
                
                if ( match( ret.toString().getBytes(), ret.length() ) ) {
                    return ret.toString();
                } // if
                
            } // if
        } // while
        
        return ret.toString(); // timeout
        
    }
    
    abstract int read( byte[] buf ) throws IOException;
    
    abstract void disconnect() throws IOException;
    
}
