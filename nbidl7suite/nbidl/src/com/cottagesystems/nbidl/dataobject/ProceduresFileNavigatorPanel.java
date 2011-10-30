/*
 * NavigatorPanel.java
 *
 * Created on May 2, 2006, 9:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.dataobject;

import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.util.Collection;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.explorer.view.ListView;

/**
 *
 * @author Jeremy
 */
public class ProceduresFileNavigatorPanel implements NavigatorPanel {

    public ProceduresFileNavigatorPanel() {
    }

    private Lookup.Result selection;
    private final LookupListener selectionListener = new LookupListener() {
        public void resultChanged(LookupEvent ev) {
            Mutex.EVENT.readAccess(new Runnable() { // #69355: safest to run in EQ
                public void run() {
                    display(selection.allInstances());
                }
            });
        }
    };

    private JComponent panel;
    private final ExplorerManager manager = new ExplorerManager();

    public String getDisplayName() {
        return NbBundle.getMessage(ProceduresFileNavigatorPanel.class, "NAV_displayName");
    }

    public String getDisplayHint() {
        return NbBundle.getMessage(ProceduresFileNavigatorPanel.class, "NAV_hint");
    }

    public JComponent getComponent() {
        if (panel == null) {
            final ListView view = new ListView();
            view.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            class Panel extends JPanel implements ExplorerManager.Provider, Lookup.Provider {
                // Make sure action context works correctly:
                private final Lookup lookup = ExplorerUtils.createLookup(manager, new ActionMap());
                {
                    setLayout(new BorderLayout());
                    add(view, BorderLayout.CENTER);
                }
                public ExplorerManager getExplorerManager() {
                    return manager;
                }
                // Make sure list gets focus, with first node initially selected:
                public boolean requestFocusInWindow() {
                    boolean b = view.requestFocusInWindow();
                    if (manager.getSelectedNodes().length == 0) {
                        Node[] children = manager.getRootContext().getChildren().getNodes(true);
                        if (children.length > 0) {
                            try {
                                manager.setSelectedNodes(new Node[] {children[0]});
                            } catch (PropertyVetoException e) {
                                assert false : e;
                            }
                        }
                    }
                    return b;
                }
                public Lookup getLookup() {
                    return lookup;
                }
            }
            panel = new Panel();
        }
        return panel;
    }

    public void panelActivated(Lookup context) {
        selection = context.lookup(new Lookup.Template(DataObject.class));
        selection.addLookupListener(selectionListener);
        selectionListener.resultChanged(null);
    }

    public void panelDeactivated() {
        selection.removeLookupListener(selectionListener);
        selection = null;

    }

    public Lookup getLookup() {
        return null;
    }

    private void display(Collection/*<DataObject>*/ selectedFiles) {
        // Show list of targets for selected file:
        if (selectedFiles.size() == 1) {
            DataObject d = (DataObject) selectedFiles.iterator().next();
            manager.setRootContext(d.getNodeDelegate());
            return;
        }
        // Fallback:
        manager.setRootContext(Node.EMPTY);
    }

}
