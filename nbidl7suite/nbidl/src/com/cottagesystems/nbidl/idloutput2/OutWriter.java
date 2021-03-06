/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * OutWriter.java
 *
 * Created on February 27, 2004, 7:24 PM
 */

package com.cottagesystems.nbidl.idloutput2;

import org.openide.ErrorManager;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.StringTokenizer;
import java.util.TooManyListenersException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.util.Utilities;

/**
 * Implementation of OutputWriter backed by an implementation of Storage (memory mapped file, heap array, etc).
 *
 * @author  Tim Boudreau
 */
class OutWriter extends PrintWriter {
    /** A flag indicating an io exception occured */
    private boolean trouble = false;

    private PvwaveIO owner;
    
    private boolean disposed = false;

    //IZ 44375 - Memory mapping fails with bad file handle on win 98
    private static final boolean USE_HEAP_STORAGE =
        Boolean.getBoolean("nb.output.heap") || Utilities.getOperatingSystem() == //NOI18N
        Utilities.OS_WIN98 || 
        Utilities.getOperatingSystem() == Utilities.OS_WIN95;

    /**
     * Byte array used to write the line separator after line writes.
     */
    static byte[] lineSepBytes;
    static {
        if (Utilities.isWindows()) {
            lineSepBytes = new byte[] { '\0', '\r', '\0', '\n'};
        } else {
            lineSepBytes = new byte[] { '\0', '\n'};
        }
    }
    /** The read-write backing storage.  May be heap or */
    private Storage storage;
    
    /** The Lines object that will be used for reading data out of the 
     * storage */
    private AbstractLines lines = new LinesImpl();
    
    /** Flag set if one of several exceptions occurred while writing which
     * mean the process doing the writing was brutally terminated.  Data will
     * be readable but not writable if set to true */
    private boolean terminated = false;
    
    /** Flag set if a write failed due to disk space limits.  Subsequent
     * instances will use HeapStorage in this case */
    static boolean lowDiskSpace = false;

    
    /**
     * Need to remember the line start and lenght over multiple calls to
     * write(ByteBuffer), needed to facilitate other calls than println()
     */
    private int lineStart;
    private int lineLength;

    /** Creates a new instance of OutWriter */
    OutWriter(PvwaveIO owner) {
        this();
        this.owner = owner;
    }

    /**
     * Package constructor for unit tests
     */
    OutWriter() {
        super (new DummyWriter());
        lineStart = -1;
        lineLength = 0;
    }

    Storage getStorage() {
        if (disposed) {
            throw new IllegalStateException ("Output file has been disposed!");
        }
        if (storage == null) {
            storage = USE_HEAP_STORAGE || lowDiskSpace ? 
                (Storage)new HeapStorage() : (Storage)new FileMapStorage();
        }
        return storage;
    }
    
    boolean hasStorage() {
        return storage != null;
    }
    
    boolean isDisposed() {
        return disposed;
    }
    
    boolean isEmpty() {
        return storage == null ? true : lines == null ? true : 
            lines.getLineCount() == 0;
    }

    public String toString() {
        return "OutWriter@" + System.identityHashCode(this) + " for " + owner + " closed ";
    }

    private int doPrintln (String s) {
        try {
            int idx = s.indexOf("\n");
            int result = 1;
            if (idx != -1) { //XXX platform specific line sep?
                //XXX this can be much more efficient by slicing buffers
                StringTokenizer tok = new StringTokenizer(s, "\n", true); //NOI18N
                result = 0;
                boolean lastWasNewLine = true;
                while (tok.hasMoreTokens()) {
                    String token = tok.nextToken();
                    if (token.equals("\n")) {
                        if (lastWasNewLine) {
                            doPrintln("");
                            result++;
                        }
                        lastWasNewLine = true;
                    } else {
                        lastWasNewLine = false;
                        doPrintln(token);
                        result++;
                    }
                }
            } else {
                ByteBuffer buf;
                synchronized (this) {
                    if (s.startsWith("\t")) { //NOI18N
                        char[] c = s.toCharArray();
                        int ix = 0;
                        //Temporary handling of leading tab characters, so they
                        //at least have some width.  Note this does affect output
                        //written with save-as
                        StringBuffer sb = new StringBuffer (s.length() + 10);
                        for (int i=0; i < c.length; i++) {
                            if ('\t' == c[i]) {
                                sb.append("        "); // NOI18N
                            } else {
                                sb.append (c[i]);
                            }
                        }
                        s = sb.toString();
                    }
                    buf = getStorage().getWriteBuffer(AbstractLines.toByteIndex(s.length()));
                    buf.asCharBuffer().put(s);
                    buf.position (buf.position() + AbstractLines.toByteIndex(s.length()));
                    write (buf, true);
                }
            }
            return result;
        } catch (IOException ioe) {
            handleException (ioe);
            return 0;
        }
    }


    /** Generic exception handling, marking the error flag and notifying it with ErrorManager */
    private void handleException (Exception e) {
        setError();
	    ErrorManager em = ErrorManager.getDefault();
        if (em.findAnnotations(e) == null || em.findAnnotations(e).length == 0) {
            em.annotate(e, NbBundle.getMessage(OutWriter.class, 
                "MSG_GenericError")); //NOI18N
        }
        if (PvwaveController.log) {
            StackTraceElement[] el = e.getStackTrace();
            PvwaveController.log ("EXCEPTION: " + e.getClass() + e.getMessage());
            for (int i=1; i < el.length; i++) {
                PvwaveController.log (el[i].toString());
            }
        }
        em.notify(e);
    }

    /**
     * Write the passed buffer to the backing storage, recording the line start in the mapping of lines to
     * byte offsets.
     *
     * @param bb
     * @throws IOException
     */
    public synchronized void write(ByteBuffer bb, boolean completeLine) throws IOException {
        if (checkError() || terminated) {
            return;
        }
        lines.markDirty();
        int length = bb.limit();
        closed = false;
        int start = -1;
        try {
            start = getStorage().write(bb, completeLine);
        } catch (java.nio.channels.ClosedByInterruptException cbie) {
            //Execution termination has sent ThreadDeath to the process in the
            //middle of a write
            threadDeathClose();
        } catch (java.nio.channels.AsynchronousCloseException ace) {
            //Execution termination has sent ThreadDeath to the process in the
            //middle of a write
            threadDeathClose();
        } catch (IOException ioe) {
            //Out of disk space
            if (ioe.getMessage().indexOf("There is not enough space on the disk") != -1) { //NOI18N
                lowDiskSpace = true;
                String msg = NbBundle.getMessage(OutWriter.class, 
                    "MSG_DiskSpace", storage); //NOI18N
                ErrorManager.getDefault().annotate (ioe, ErrorManager.USER, 
                    ioe.getMessage(), msg, ioe, null);
                ErrorManager.getDefault().notify(ioe);
                setError();
                storage.dispose();
            } else {
                //Existing output may still be readable - close, but leave
                //open for reads - if there's a problem there too, the error
                //flag will be set when a read is attempted
                ErrorManager.getDefault().notify(ioe);
                threadDeathClose();
            }
        }
        boolean startedNow = false;
        if (start >= 0 && lineStart == -1) {
            lineStart = start;
            lineLength = lineLength + length;
            startedNow = true;
        }
        if (completeLine) {
            if (lineStart >= 0 && !terminated && lines != null) {
                if (PvwaveController.verbose) PvwaveController.log (this + ": Wrote " +
                        ((ByteBuffer)bb.flip()).asCharBuffer() + " at " + start);
                if (startedNow) {
                    lines.lineWritten (lineStart, lineLength);
                } else {
                    lines.lineFinished(lineLength);
                }
                lineStart = -1;
                lineLength = 0;
                if (owner != null && owner.hasStreamClosed()) {
                    owner.setStreamClosed(false);
                    lines.fire();
                }
            }
        } else {
            if (startedNow && lineStart >= 0 && !terminated && lines != null) {
                lines.lineStarted(lineStart);
                if (owner != null && owner.hasStreamClosed()) {
                    owner.setStreamClosed(false);
                    lines.fire();
                }
            }
        }
    }

    /**
     * An exception has occurred while writing, which has left us in a readable state, but not
     * a writable one.  Typically this happens when an executing process was sent Thread.stop()
     * in the middle of a write.
     */
    void threadDeathClose() {
        terminated = true;
        if (PvwaveController.log) PvwaveController.log (this + " Close due to termination");
        ErrWriter err = owner.writer().err();
        if (err != null) {
            err.closed=true;
        }
        owner.setStreamClosed(true);
        close();
    }

    /**
     * Dispose this writer.  If reuse is true, the underlying storage will be disposed, but the
     * OutWriter will still be usable.  If reuse if false, note that any current ChangeListener is cleared.
     *
     */
    public synchronized void dispose() {
        if (disposed) {
            //This can happen if a tab was closed, so we were already disposed, but then the
            //ant module tries to reuse the tab -
            return;
        }
        if (PvwaveController.log) PvwaveController.log (this + ": OutWriter.dispose - owner is " + (owner == null ? "null" : owner.getName()));
        clearListeners();
        if (storage != null) {
            storage.dispose();
            storage = null;
        }
        if (lines != null) {
            lines.clear();
        }
        trouble = true;
        if (PvwaveController.log) PvwaveController.log (this + ": Setting owner to null, trouble to true, dirty to false.  This OutWriter is officially dead.");
        owner = null;
        disposed = true;
    }


    private void clearListeners() {
        if (PvwaveController.log) PvwaveController.log (this + ": Sending outputLineCleared to all listeners");
        if (owner == null) {
            //Somebody called reset() twice
            return;
        }
        synchronized (this) {
            if (lines != null && lines.hasHyperlinks()) {
                int[] listenerLines = lines.allListenerLines();
                PvwaveController.ControllerOutputEvent e = new PvwaveController.ControllerOutputEvent(owner, 0);
                for (int i=0; i < listenerLines.length; i++) {
                    OutputListener ol = (OutputListener) lines.getListenerForLine(listenerLines[i]);
                    if (PvwaveController.log) {
                        PvwaveController.log("Clearing listener " + ol);
                    }
                    e.setLine(listenerLines[i]);
                    if (ol != null) {
                        ol.outputLineCleared(e);
                    } else {
                        //#56826 - debug messaging
                        ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "Warning: issue #56826 - There was a null OutputListener on line:" + listenerLines[i]);
                    }
                }
            } else {
                if (PvwaveController.log) PvwaveController.log (this + ": No listeners to clear");
            }
        }
    }

    public synchronized boolean isClosed() {
        if (checkError() || storage == null || storage.isClosed()) {
            return true;
        } else {
            return closed;
        }
    }

    public Lines getLines() {
        return lines;
    }

    private boolean closed = false;
    public synchronized void close() {
        closed = true;
        try {
            //#49955 - possible (but difficult) to end up with close()
            //called twice
            if (storage != null) {
                storage.close();
            }
            if (lines != null) {
                lines.fire();
            }
        } catch (IOException ioe) {
            handleException (ioe);
        }
    }

       public synchronized void println(String s) {
            if (checkError()) {
                return;
            }
            doPrintln(s);
        }

        public synchronized void flush() {
            if (checkError()) {
                return;
            }
            try {
                getStorage().flush();
                if (lines != null) {
                    lines.fire();
                }
            } catch (IOException e) {
                handleException (e);
            }
        }


        public boolean checkError() {
            return disposed || trouble;
        }

        protected void setError() {
            trouble = true;
        }

        public synchronized void write(int c) {
            if (checkError()) {
                return;
            }
            try {
                ByteBuffer buf = getStorage().getWriteBuffer(AbstractLines.toByteIndex(1));
                buf.asCharBuffer().put((char)c);
                buf.position (buf.position() + AbstractLines.toByteIndex(1));
                write (buf, false);
            } catch (IOException ioe) {
                handleException (ioe);
            }
        }

        public synchronized void write(char data[], int off, int len) {
            if (checkError()) {
                return;
            }
            if (len > 0 && data[off] == '\t') {
                //Temporary handling of leading tab characters, so they
                //at least have some width.  Note this does affect output
                //written with save-as
                StringBuffer sb = new StringBuffer(data.length + 10);
                int cnt = 0;
                for (int i=0; i < data.length; i++) {
                    if (data[i] == '\t') {
                        sb.append("        "); // NOI18N
                        cnt = cnt + 1;
                    } else {
                        sb.append (data[i]);
                    }
                }
                data = sb.toString().toCharArray();
                len = len + (cnt * 4);
            }
            
            int count = off;
            int start = off;
            while (count < len + off) {
                char curr = data[count];
                if (curr == '\n') { //NOI18N
                    //TODO we can optimize the array writing a bit by not delegating to the 
                    //println metod which perform a physical write on each line, 
                    // but to write just once, when everything is processed.
                    String sx = new String(data, start, (count + 1 - start));
                    println (sx);
                    start = count + 1;
                    if (start >= len + off) {
                        return;
                    }
                }
                count++;
            }
            try {
                synchronized (this) {
                    int lenght = count - (start);
                    ByteBuffer buf = getStorage().getWriteBuffer(AbstractLines.toByteIndex(lenght));
                    buf.asCharBuffer().put(data, start, lenght);
                    buf.position(buf.position() + AbstractLines.toByteIndex(lenght));
                    write(buf, false);
                }
            } catch (IOException ioe) {
                handleException(ioe);
                return;
            }
        }

        public synchronized void write(char data[]) {
            write (data, 0, data.length);
        }
        
        public synchronized void println() {
            doPrintln("");
        }

        /**
         * Write a portion of a string.
         * @param s A String
         * @param off Offset from which to start writing characters
         * @param len Number of characters to write
         */
        public synchronized void write(String s, int off, int len) {
            write (s.toCharArray(), off, len);
        }

        public synchronized void write(String s) {
            write (s.toCharArray());
        }


        public synchronized void println(String s, OutputListener l) throws IOException {
            println(s, l, false);
        }

        
        public synchronized void println(String s, OutputListener l, boolean important) throws IOException {
            if (checkError()) {
                return;
            }
            int addedCount = doPrintln (s);
            int newCount = lines.getLineCount();
            for (int i=newCount - addedCount; i < newCount; i++) {
                lines.addListener (i, l, important);
                //#48485 we should update the UI, since the lines are in the model
                // and jump next/previous can't jump to appropriate place.
                lines.fire();
            }
        }
        
    /**
     * A useless writer object to pass to the superclass constructor.  We override all methods
     * of it anyway.
     */
    static class DummyWriter extends Writer {
        
        DummyWriter() {
            super (new Object());
        }
        
        public void close() throws IOException {
        }
        
        public void flush() throws IOException {
        }
        
        public void write(char[] cbuf, int off, int len) throws IOException {
        }
    }

    private class LinesImpl extends AbstractLines {
        LinesImpl() {
            super();
        }

        protected Storage getStorage() {
            return OutWriter.this.getStorage();
        }

        protected boolean isDisposed() {
             return OutWriter.this.disposed;
        }

        protected boolean isTrouble() {
            return OutWriter.this.trouble;
        }

        public Object readLock() {
            return OutWriter.this;
        }

        public boolean isGrowing() {
            return !isClosed();
        }

        protected void handleException (Exception e) {
            OutWriter.this.handleException(e);
        }
    }
}
