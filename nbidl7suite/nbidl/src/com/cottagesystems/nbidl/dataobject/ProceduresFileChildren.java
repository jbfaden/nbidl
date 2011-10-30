/*
 * ProceduresFileChildren.java
 *
 * Created on March 17, 2006, 4:49 PM
 *
 *
 */

package com.cottagesystems.nbidl.dataobject;

import com.cottagesystems.nbidl.util.TickleTimer;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author Jeremy
 */
public class ProceduresFileChildren extends Children.Keys implements ChangeListener, TaskListener {
    private final PvwaveCookie cookie;
    private boolean childNodesAdded = false;
    final static Logger log= Logger.getLogger("pvwave");
    
    Object WAIT_KEY= new Object() {  // object indicates put up the "Please wait..." node
        public String toString() { return "WAIT_KEY"; }
    };
    Node[] WAIT_NODES= new Node[] {createWaitNode() };
    
    public ProceduresFileChildren(PvwaveCookie cookie) {
        this.cookie = cookie;
    }
    
    private Node createWaitNode() {
        AbstractNode n = new AbstractNode(Children.LEAF);
        n.setName("Please wait...");
        return n;
    }
    
    protected Node[] createNodes(Object key) {
        log.fine("children.createNodes("+key+")");
        if ( key==WAIT_KEY ) {
            return WAIT_NODES;
        } else {
            Node resultNode= new ProcedureNode( (Procedure)key );
            resultNode.setDisplayName(String.valueOf(key));
            return new Node[] { resultNode };
        }
    }
    
    protected void addNotify() {
        super.addNotify();
        setKeys( Collections.singletonList(WAIT_KEY) );
        cookie.addChangeListener(this);
        childNodesAdded = true;
        recalculateChildren();
    }
    protected void removeNotify() {
        cookie.removeChangeListener(this);
        childNodesAdded = false;
        setKeys(Collections.EMPTY_SET);
        super.removeNotify();
    }
    
    private void recalculateChildren() {
        Runnable run= new Runnable() {
            public void run() {
                recalculateChildrenImmediately();
            }
        };
        DataObject fo= cookie.getProceduresFileDataObject();
        new Thread( run, "recalculateChildren of "+ fo ).start();
    }
    
    private void recalculateChildrenImmediately() {
        if ( childNodesAdded ) {
            cookie.prepare().waitFinished(); // TODO: should this be here?2006-05-11
            // This is a kludgy way to get around a bug in Netbeans' Task.addTaskListener,
            //which includes too much code in the synchronized block.  (It calls the taskFinished
            // from within the synchronized block.)  They should call isFinished() and limit the
            // synchronized code.  Ed thinks they should have separate lock objects for the
            // listener list and the finished field.
            cookie.prepare().addTaskListener(ProceduresFileChildren.this);
        }
    }
    
    ChangeListener realChangeListener= new ChangeListener() {
        public void stateChanged( ChangeEvent e ) {
            log.fine( "real stateChanged" );
            recalculateChildrenImmediately();
        }
    };
    
    // wait ten seconds of idle before invoking changes.
    TickleTimer timer= new TickleTimer( 10000, realChangeListener ); 
    
    public void stateChanged(ChangeEvent e) {
        log.fine( "stateChanged" );
        timer.tickle();
    }
    
    public void taskFinished(Task task) {
        log.fine("children.taskFinished() "+Thread.currentThread().getName() );
        if (cookie.isValid()) {
            log.fine("cookie.isValid()=true");
            try {
                // TODO: Navigator hangs here on step over.
                ProceduresFile proceduresFile = cookie.getProceduresFile();
                int c = proceduresFile.procedureCount();
                Procedure[] procedures = new Procedure[c];
                for (int i = 0; i < c; i++) {
                    procedures[i] = proceduresFile.getProcedure(i);
                }
                setKeys(procedures);
            } catch (IOException ioe) {
                // ignore
            }
        }
    }
    
}
