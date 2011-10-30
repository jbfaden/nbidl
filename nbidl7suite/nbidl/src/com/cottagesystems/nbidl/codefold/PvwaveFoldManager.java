/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.cottagesystems.nbidl.codefold;

import com.cottagesystems.nbidl.syntax.PvwaveSyntax;
import com.cottagesystems.nbidl.syntax.PvwaveTokenContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;

import org.netbeans.spi.editor.fold.*;
import org.netbeans.api.editor.fold.*;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.Utilities;

import org.openide.ErrorManager;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;

/**
 * This class is an implementation of @see org.netbeans.spi.editor.fold.FoldManager
 * responsible for creating, deleting and updating code folds.
 *
 */

public class PvwaveFoldManager implements FoldManager {
    
    public static Logger logger= Logger.getLogger("pvwave");
    
    /** Manifest group fold type */
    public static final FoldType GROUP = new FoldType("routine"); // NOI18N
    //folds update timeout (should be read from settings...)
    public static final int UPDATE_TIMEOUT = 500; //ms
    
    private Task task = null;
    
    private FoldOperation operation;
    
    private FoldOperation getOperation() {
        return operation;
    }
    
    public void init(FoldOperation operation) {
        this.operation = operation;
    }
    
    public void initFolds(FoldHierarchyTransaction transaction) {
        Document doc = getOperation().getHierarchy().getComponent().getDocument();
        if(doc instanceof BaseDocument) {
            updateFolds(transaction);
        }
    }
    
    private void update(final FoldHierarchyTransaction tran) {
        //very simple impl, not ideal one!!!
        if(task != null && !task.isFinished()) return ;
        
        task = RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                updateFolds(tran);
            }
        }, UPDATE_TIMEOUT);
    }
    
    public void release() {
        //deatach potential settings listeners, stop the RP.Task is runs etc...
    }
    
    public void insertUpdate(DocumentEvent evt, FoldHierarchyTransaction transaction) {
        update(transaction);
    }
    
    public void removeUpdate(DocumentEvent evt, FoldHierarchyTransaction transaction) {
        update(transaction);
    }
    
    public void changedUpdate(DocumentEvent evt, FoldHierarchyTransaction transaction) {
        //do nothing - the updates are catched in insertUpdate and removeUpdate methods
    }
    
    public void removeEmptyNotify(Fold epmtyFold) {
    }
    
    public void removeDamagedNotify(Fold damagedFold) {
    }
    
    public void expandNotify(Fold expandedFold) {
    }
    
    private List/*<FoldInfo>*/ generateFolds2(Document ddoc) throws BadLocationException {
        
        BaseDocument doc= (BaseDocument) ddoc;
        ArrayList foldList = new ArrayList();
        Stack foldStack= new Stack();
        try {
            int i0= 0;
            int i1= doc.getLength();
            
            SyntaxSupport support= new SyntaxSupport( doc );
            Syntax syntax= new PvwaveSyntax();
            
            support.initSyntax( syntax, i0, i1, true, false );
            TokenID tok;
            FoldInfo fi;
            String s;
            while ((tok=syntax.nextToken()) != null ) {
                //System.err.println(tok);
                if ( tok.getNumericID()==PvwaveTokenContext.BLOCK_COMMENT_ID ) {
                    int j0= Utilities.getRowStart( doc, syntax.getTokenOffset() );
                    int j1= Utilities.getRowEnd( doc, syntax.getTokenOffset() );
                    int j2= syntax.getTokenOffset()+syntax.getTokenLength();
                    s= doc.getText(j0,j1-j0);
                    int l0= Utilities.getLineOffset(doc,j0);
                    int l1= Utilities.getLineOffset(doc,j2);
                    if ( l1-l0 > 1 ) {
                        fi= new FoldInfo( j0, j2, Utilities.getLineOffset(doc,syntax.getOffset()), s );
                        foldList.add(fi);
                    }
                } else if ( tok.getCategory()==PvwaveTokenContext.KEYWORDS ) {
                    switch ( tok.getNumericID() ) {
                        case  PvwaveTokenContext.PRO_ID:
                        case  PvwaveTokenContext.CASE_ID:
                        case  PvwaveTokenContext.FUNCTION_ID:
                        case  PvwaveTokenContext.BEGIN_ID:
                            int j0= Utilities.getRowStart( doc, syntax.getOffset() );
                            int j1= Utilities.getRowEnd( doc, syntax.getOffset() );
                            s= doc.getText(j0,j1-j0);
                            fi= new FoldInfo( j0, -1, Utilities.getLineOffset(doc,syntax.getOffset()), s );
                            foldStack.push(fi);
                            break;
                        case PvwaveTokenContext.END_ID:
                        case PvwaveTokenContext.ENDIF_ID:
                        case PvwaveTokenContext.ENDWHILE_ID:
                        case PvwaveTokenContext.ENDFOR_ID:
                        case PvwaveTokenContext.ENDSWITCH_ID:
                        case PvwaveTokenContext.ENDCASE_ID:
                        case PvwaveTokenContext.ENDREP_ID:
                        case PvwaveTokenContext.ENDELSE_ID:
                            if ( foldStack.size()>0 ) {
                                int lineNum= Utilities.getLineOffset(doc,syntax.getOffset());
                                fi= (FoldInfo)foldStack.pop();
                                j0= Utilities.getRowStart( doc, syntax.getOffset() );
                                j1= Utilities.getRowEnd( doc, syntax.getOffset() );
                                s= doc.getText(j0,j1-j0);
                                logger.fine("end "+foldStack.size()+" "+s+"\n  "+fi.label);
                                if ( lineNum-fi.startLine > 1 ) {
                                    fi.endOffset= j0;
                                    foldList.add( fi );
                                }
                            }
                            break;
                            
                        default:
                    }
                    
                }
            }
            
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        return foldList;
    }
    
    
    /** The heart of this class. This method parses the manifest and based on
     * syntax parser information creates appropriate folds.
     */
    private synchronized void updateFolds(final FoldHierarchyTransaction tran) {
        final FoldHierarchy fh = getOperation().getHierarchy();
        final BaseDocument doc = (BaseDocument)getOperation().getHierarchy().getComponent().getDocument();
        try {
            //parse document and create an array of folds
            List/*<FoldInfo>*/ generated = generateFolds2(doc);
            logger.fine("generated " + generated.size());
            
            //get existing folds
            List existingFolds = FoldUtilities.findRecursive(fh.getRootFold());
            Iterator itr = existingFolds.iterator();
            
            final ArrayList newborns = new ArrayList(generated.size() / 2);
            final ArrayList/*<Fold>*/ zombies = new ArrayList(generated.size() / 2);
            
            //delete unexisting
            while(itr.hasNext()) {
                Fold f = (Fold)itr.next();
                if(!generated.contains(new FoldInfo(f.getStartOffset(), f.getEndOffset(), -1, ""))) {
                    //delete this one
                    logger.fine("adding " + f + " to zombies");
                    zombies.add(f);
                }
            }
            
            //and create new ones
            itr = generated.iterator();
            while(itr.hasNext()) {
                FoldInfo fi = (FoldInfo)itr.next();
                Iterator existingItr = existingFolds.iterator();
                boolean add = true;
                while(existingItr.hasNext()) {
                    Fold f = (Fold)existingItr.next();
                    if(f.getStartOffset() == fi.startOffset && f.getEndOffset() == fi.endOffset) {
                        add = false;
                    }
                }
                if(add) {
                    newborns.add(fi);
                    logger.fine("adding " + fi + " to newborns");
                }
            }
            
            //run folds update in event dispatching thread
            Runnable updateTask = new Runnable() {
                public void run() {
                    //lock the document for changes
                    doc.readLock();
                    try {
                        //lock the hierarchy
                        fh.lock();
                        try {
                            try {
                                //remove outdated folds
                                Iterator i = zombies.iterator();
                                while(i.hasNext()) {
                                    Fold f = (Fold)i.next();
                                    getOperation().removeFromHierarchy(f, tran);
                                }
                                
                                //add new folds
                                Iterator newFolds = newborns.iterator();
                                while(newFolds.hasNext()) {
                                    FoldInfo f = (FoldInfo)newFolds.next();
                                    getOperation().addToHierarchy(GROUP, f.label, false, f.startOffset , f.endOffset , 0, 0, null, tran);
                                }
                            }catch(BadLocationException ble) {
                                //when the document is closing the hierarchy returns different empty document, grrrr
                                ErrorManager.getDefault().notify(ble);
                            }
//                            }finally {
//                                tran.commit();
//                            }
                        } finally {
                            fh.unlock();
                        }
                    } finally {
                        doc.readUnlock();
                    }
                }
            };
            
            if(SwingUtilities.isEventDispatchThread()) {
                updateTask.run();
            } else {
                SwingUtilities.invokeAndWait(updateTask);
            }
            
            
        }catch(BadLocationException e) {
            ErrorManager.getDefault().notify(e);
        }catch(InterruptedException ie) {
            ;
        }catch(InvocationTargetException ite) {
            ErrorManager.getDefault().notify(ite);
        }
    }
    
    static class FoldInfo {
        public int startOffset, endOffset;
        public int startLine;
        String label;
        public FoldInfo(int startOffset, int endOffset, int startLine, String label ) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.startLine= startLine;
            this.label= label;
        }
        public boolean equals(Object o) {
            return ((FoldInfo)o).startOffset == startOffset &&
                    ((FoldInfo)o).endOffset == endOffset;
        }
    }
    
}
