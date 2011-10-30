/*
 * MyTerminalWindow.java
 *
 * Created on August 19, 2007, 8:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.session;

import com.mindbright.terminal.SearchContext;
import com.mindbright.terminal.TerminalInputListener;
import com.mindbright.terminal.TerminalOption;
import com.mindbright.terminal.TerminalOutputListener;
import com.mindbright.terminal.TerminalPrinter;
import com.mindbright.terminal.TerminalWindow;
import com.mindbright.util.RandomSeed;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 *
 * @author jbf
 */
public class MindtermTerminalWindow implements TerminalWindow {
    
    List<TerminalInputListener> inputListeners= new ArrayList<TerminalInputListener>();
    List<TerminalOutputListener> outputListeners= new ArrayList<TerminalOutputListener>();
    TerminalPrinter printer= null;
    String TERM_TYPE= "dumb";
    String title;
    Properties properties;
    
    StringBuffer buf= new StringBuffer();
    ByteBuffer bbuf= ByteBuffer.allocate(50000);
    int bytesRead=0;
    
    IDLOutputHandler idlOutputHandler;
    
/*    Runnable inputRun= new Runnable() {
        private void sendBytes(byte[] buf, int count) {
            for ( TerminalInputListener listener: inputListeners ) {
                listener.sendBytes(new String(buf,0,count).getBytes());
            }
        }
        public void run() {
            byte[] buf=new byte[2048];
            while ( true ) {
                int bytes;
                try {
                    bytes = System.in.read(buf);
                    if (bytes>0) sendBytes( buf, bytes );
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
 
            }
        }
    };*/
    
    
    MindtermTerminalWindow( IDLOutputHandler idlOutputHandler ) {
        this.idlOutputHandler= idlOutputHandler;
    }
    
    public void setTitle(String title) {
        this.title= title;
    }
    
    public String getTitle() {
        return title;
    }
    
    public int rows() {
        return 500; // avoid more pagers
    }
    
    public int cols() {
        return 256;
    }
    
    public int vpixels() {
        return 768;
    }
    
    public int hpixels() {
        return 1024;
    }
    
    public synchronized void write(byte b) {
        //idlOutputHandler.stdoutReceived( new String( new byte[] { b } ) );
        //this.buf.append( new String( new byte[] { b } ) );
        bbuf.put(b);
    }
    
    public synchronized void write(char c) {
        //idlOutputHandler.stdoutReceived( ""+ c );
        //this.buf.append(  ""+ c  );
        bbuf.putChar(c);
    }
    
    public synchronized void write(char[] c, int off, int len) {
        //idlOutputHandler.stdoutReceived( new String( c, off, len ) );
        //this.buf.append( new String( c, off, len ) );
        bbuf.put( new String( c, off, len ).getBytes() );
    }
    
    public void write(byte[] c, int off, int len) {
        while ( len<bbuf.limit() && bbuf.limit()-bbuf.position() < len ) {
            try {
                Thread.sleep(100);
                System.err.println("Blocking : len="+len+"  limit="+(bbuf.limit()-bbuf.position())  +"  pos="+bbuf.position() );
            } catch ( InterruptedException e ) { throw new RuntimeException(e); }
        }
        synchronized( this ) {
            try {
                //idlOutputHandler.stdoutReceived( new String( c, off, len ) );
                // this.buf.append( new String( c, off, len ) );
                bbuf.put( c, off, len );
                //System.err.println("Okay : len="+len+"  limit="+(bbuf.limit()-bbuf.position())  +"  pos="+bbuf.position() );
            } catch ( BufferOverflowException ex ) {
                //System.err.println("dropping bytes: len="+len+"  limit="+(bbuf.limit()-bbuf.position())  +"  pos="+bbuf.position()  );
                int newLen= bbuf.limit()-bbuf.position();
                int newOff= off + ( len - newLen );
                bbuf.put( c, newOff, newLen );
            }
        }
    }
    
    public synchronized void write(String str) {
        //idlOutputHandler.stdoutReceived( str );
        //this.buf.append( str );
        bbuf.put( str.getBytes() );
    }
    
    public synchronized int read( byte[] buf ) {
        int bytesWritten= this.bbuf.position() - bytesRead;
        if ( bytesWritten == 0 ) return 0;
        
        int newPosition;
        boolean reset;
        if ( bytesWritten > buf.length ) {
            bytesWritten= buf.length;
            newPosition= bbuf.position();
            reset= false;
        } else {
            newPosition= 0; // not used to be explicit
            reset= true;
        }
        bbuf.position( bytesRead );
        bbuf.get( buf, 0, bytesWritten );
        
        if ( reset ) {
            bytesRead= 0;
            bbuf.position( 0 );
        } else {
            bytesRead+= bytesWritten;
            bbuf.position( newPosition );
        }
        return bytesWritten;
    }
    
    public void addInputListener(TerminalInputListener listener) {
        inputListeners.add(listener);
    }
    
    public void removeInputListener(TerminalInputListener listener) {
        inputListeners.remove(listener);
    }
    
    public void addOutputListener(TerminalOutputListener listener) {
        outputListeners.add(listener);
    }
    
    public void removeOutputListener(TerminalOutputListener listener) {
        outputListeners.remove(listener);
    }
    
    public void attachPrinter(TerminalPrinter printer) {
        this.printer= printer;
    }
    
    public void detachPrinter() {
        this.printer= null;
    }
    
    public void typedChar(char c) {
        System.err.println( c );
    }
    
    public void sendBytes(byte[] b) {
        System.err.println( b );
    }
    
    public void sendBreak() {
        System.err.println("break");
    }
    
    public void reset() {
        System.err.println("reset");
    }
    
    public void printScreen() {
    }
    
    public void startPrinter() {
    }
    
    public void stopPrinter() {
    }
    
    public String terminalType() {
        return TERM_TYPE;
    }
    
    public void setProperties(Properties newProps, boolean merge) throws IllegalArgumentException, NoSuchElementException {
        properties.putAll(newProps);
    }
    
    public void setProperty(String key, String value) throws IllegalArgumentException, NoSuchElementException {
        
    }
    
    public Properties getProperties() {
        return properties;
    }
    
    public String getProperty(String key) {
        return (String) properties.get(key);
    }
    
    public void resetToDefaults() {
    }
    
    public boolean getPropsChanged() {
        return false;
    }
    
    public void setPropsChanged(boolean value) {
    }
    
    public TerminalOption[] getOptions() {
        return new TerminalOption[0];
    }
    
    public SearchContext search(SearchContext lastContext, String key, boolean reverse, boolean caseSens) {
        return null;
    }
    
    public void addAsEntropyGenerator(RandomSeed seed) {
    }
    
    public void setAttributeBold(boolean set) {
    }
    
    public void clearScreen() {
    }
    
    public void ringBell() {
    }
    
    public void setCursorPos(int row, int col) {
    }
    
    public void clearLine() {
    }
    
}
