/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.cottagesystems.nbidl.dataobject;

import com.cottagesystems.nbidl.syntax.PvwaveSyntaxScraper;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.lang.ref.*;
import java.util.*;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.*;
import javax.swing.text.*;
import org.netbeans.editor.Syntax;
import org.netbeans.modules.editor.NbEditorDocument;

import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.*;
import org.openide.util.*;

public class PvwaveSupport implements PvwaveCookie, Runnable, DocumentListener, ChangeListener, PropertyChangeListener {
    private final ErrorManager err;
    private final DataObject obj;
    private final EditorCookie edit;
    private Task prepareTask = null;
    private final Set listeners = new HashSet();
    private ProceduresFile proceduresFile = null;
    private IOException parseException = null;
    private boolean addedEditorListener = false;
    private Reference lastUsedDocument = null; // Reference<Document>
    final static Logger log= Logger.getLogger("pvwave");

    public PvwaveSupport(DataObject obj, EditorCookie edit) {
        this.obj = obj;
        err= ErrorManager.getDefault().getInstance( "com.cottagesystems.nbidl.dataobject.PvwaveSupport" );
        log.fine("instanciate PvwaveSupport for "+obj.getPrimaryFile());
        this.edit = edit;
    }
    public void stateChanged(ChangeEvent ev) {
        err.log("Editor state changed");
        log.fine("Editor state changed");
        invalidate();
    }
    public synchronized void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }
    public synchronized void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }
    protected synchronized void fireChange() {
        final ChangeListener[] ls = (ChangeListener[])listeners.toArray(new ChangeListener[listeners.size()]);
        if (ls.length == 0) return;
        final ChangeEvent ev = new ChangeEvent(this);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                err.log("firing change");
                log.fine("firing change");
                for (int i = 0; i < ls.length; i++) {
                    ls[i].stateChanged(ev);
                }
            }
        });
    }
    
    
    public synchronized Task prepare() {
        log.fine( "PvwaveSupport.prepare "+obj.getPrimaryFile()+" "+Thread.currentThread().getName() );
        if (prepareTask == null) {
            err.log("preparing to parse");
            log.fine("preparing to parse");
            // XXX for 3.4, do not use public RP
            prepareTask= RequestProcessor.getDefault().post(this);
        }
        return prepareTask;
    }

    public void run() {
        err.log("run");
        log.fine("run");
        if (!obj.isValid()) {
            err.log("object invalidated");
            log.fine("object invalidated");
            String resource= String.valueOf( FileUtil.toFile( obj.getPrimaryFile() ) );
            setPvwaveAndParseException(null, new FileNotFoundException( resource ));
            return;
        }
        edit.prepareDocument().waitFinished();
        final Document doc = edit.getDocument();
        NbEditorDocument bdoc= (NbEditorDocument)doc;

        if (doc == null) {
            // Should not happen:
            err.log(ErrorManager.WARNING, "WARNING: Doc was null!");
            log.warning( "WARNING: Doc was null!");
            return;
        }
        if (!addedEditorListener) {
            err.log("adding editor listener");
            log.fine("adding editor listener");
            addedEditorListener = true;
            if (edit instanceof CloneableEditorSupport) {
                ((CloneableEditorSupport)edit).addPropertyChangeListener(WeakListeners.propertyChange(this, edit));
            } else {
                err.log(ErrorManager.WARNING, "Warning: no CloneableEditorSupport found");
                log.warning("Warning: no CloneableEditorSupport found");
            }
        }
        doc.render(new Runnable() {
            public void run() {
                try {
                    setPvwaveAndParseException(parse(doc), null);
                } catch (IOException ioe) {
                    setPvwaveAndParseException(proceduresFile, ioe);
                } catch (BadLocationException ble) {
                    IOException ioe = new IOException(ble.toString());
                    err.annotate(ioe, ble);
                    setPvwaveAndParseException(proceduresFile, ioe);
                }
            }
        });
        Document lastDoc = null;
        if (lastUsedDocument != null) {
            lastDoc = (Document)lastUsedDocument.get();
        }
        if (lastDoc != doc) {
            if (lastDoc != null) {
                err.log("removing listener from old document");
                log.fine("removing listener from old document");
                lastDoc.removeDocumentListener(this);
            }
            err.log("adding fresh document listener");
            log.fine("adding fresh document listener");
            doc.addDocumentListener(this);
            lastUsedDocument = new WeakReference(doc);
        }
        
        log.fine("run finished");
    }
    
    private synchronized void setPvwaveAndParseException( ProceduresFile s, IOException e) {
        if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
            err.log("parsed; exception=" + e + "; Pvwave size=" + s.procedureCount());
        }
        log.fine("parsed; exception=" + e + "; Pvwave size=" + s.procedureCount());
        this.proceduresFile = s;
        parseException = e;
        fireChange();
    }
    public boolean isValid() {
        return parseException == null;
    }

    public void changedUpdate(DocumentEvent ev) {
    }

    public void insertUpdate(DocumentEvent ev) {
        invalidate();
    }
    public void removeUpdate(DocumentEvent ev) {
        invalidate();
    }

    protected synchronized void invalidate() {
        err.log("invalidated");
        log.fine("invalidated");
        if (prepareTask != null) {
            prepareTask = null;
            fireChange();
        }
    }
    public synchronized void setProceduresFile(final ProceduresFile s) throws IOException {
        final ProceduresFile oldPvwave = proceduresFile;
        if (s.equals(oldPvwave)) {
            return;
        }
        err.log("setPvwave");
        log.fine("setPvwave");
        prepareTask = Task.EMPTY;
        proceduresFile = s;
        parseException = null;
        final StyledDocument doc = edit.openDocument();
        final BadLocationException[] e = new BadLocationException[] {null};
        try {
            NbDocument.runAtomic(doc, new Runnable() {
                public void run() {
                    doc.removeDocumentListener(PvwaveSupport.this);
                    err.log("removed doc listener");
                    log.fine("removed doc listener");
                    try {
                        generate(s, oldPvwave, doc);
                    } catch (BadLocationException ble) {
                        e[0] = ble;
                    } finally {
                        err.log("readded doc listener");
                        log.fine("readded doc listener");
                        doc.addDocumentListener(PvwaveSupport.this);
                    }
                }
            });
            if (e[0] != null) throw e[0];
        } catch (BadLocationException ble) {
            IOException ioe = new IOException(ble.toString());
            err.annotate(ioe, ble);
            throw ioe;
        }
        fireChange();
    }
    public ProceduresFile getProceduresFile() throws IOException {
        prepare().waitFinished();
        synchronized (this) {
            if (proceduresFile != null && (parseException == null || obj.isModified())) {
                return proceduresFile;
            } else {
                if (parseException == null) {
                    // Should not happen:
                    throw new IOException("parse did not finish as expected");
                }
                throw parseException;
            }
        }
    }

    protected ProceduresFile parse(Document doc) throws IOException, BadLocationException {
        String text = doc.getText(0, doc.getLength());
        return PvwaveSyntaxScraper.parse(new StringReader(text));
    }

    protected void generate(ProceduresFile s, ProceduresFile oldPvwave, Document doc) throws BadLocationException {
        CharArrayWriter wr = new CharArrayWriter();
        try {
            ProceduresFile.generate(s, wr);
        } catch (IOException ioe) {
            // Should not happen.
            err.notify(ioe);
            return;
        }
        doc.remove(0, doc.getLength());
        doc.insertString(0, wr.toString(), null);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        err.log("Editor state changed(propertyChange)");
        log.fine("Editor state changed(propertyChange)");
        invalidate();
    }

    public ProceduresFileDataObject getProceduresFileDataObject() {
        return (ProceduresFileDataObject)obj;
    }

}
