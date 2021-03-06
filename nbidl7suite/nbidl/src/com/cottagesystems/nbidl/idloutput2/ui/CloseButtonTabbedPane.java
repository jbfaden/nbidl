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

package com.cottagesystems.nbidl.idloutput2.ui;

import java.awt.Component;
import org.openide.windows.TopComponent;
import org.openide.util.Utilities;
import org.openide.ErrorManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import com.cottagesystems.nbidl.idloutput2.PvwaveController;

// #21380.
/**
 * Copy of original CloseButtonTabbedPane from the NetBeans 3.4 winsys.  Old code never dies.
 *
 * @author Tran Duc Trung
 *
 */
final class CloseButtonTabbedPane extends JTabbedPane implements ChangeListener /*, Runnable */ {

    private final Image closeTabImage =
        org.openide.util.Utilities.loadImage("org/netbeans/core/output2/resources/tabclose.gif"); // NOI18N
    private final Image closeTabInactiveImage =
        org.openide.util.Utilities.loadImage("org/netbeans/core/output2/resources/tabcloseinactive.gif"); // NOI18N

    public static final String PROP_CLOSE = "close";

    CloseButtonTabbedPane() {
        addChangeListener(this);
        CloseButtonListener.install();
        //Bugfix #28263: Disable focus.
        setFocusable(false);
        setBorder(javax.swing.BorderFactory.createEmptyBorder());
        setFocusCycleRoot(true);
        setFocusTraversalPolicy(new CBTPPolicy());
    }

    private Component sel() {
        Component c = getSelectedComponent();
        return c == null ? this : c;
    }

    private class CBTPPolicy extends FocusTraversalPolicy {
        public Component getComponentAfter(Container aContainer, Component aComponent) {
            return sel();
        }

        public Component getComponentBefore(Container aContainer, Component aComponent) {
            return sel();
        }

        public Component getFirstComponent(Container aContainer) {
            return sel();
        }

        public Component getLastComponent(Container aContainer) {
            return sel();
        }

        public Component getDefaultComponent(Container aContainer) {
            return sel();
        }
    }

    public int tabForCoordinate(int x, int y) {
        return getUI().tabForCoordinate(this, x, y);
    }

    private int pressedCloseButtonIndex = -1;
    private int mouseOverCloseButtonIndex = -1;
    private boolean draggedOut = false;

    public void stateChanged (ChangeEvent e) {
        reset();
        if (getSelectedComponent() instanceof AbstractOutputPane) {
            ((AbstractOutputPane) getSelectedComponent()).ensureCaretPosition();
        }
    }
    
    public Component add (Component c) {
        Component result = super.add(c);
        String s = c.getName();
        if (s != null) {
            s += " ";
        }
        setTitleAt (getComponentCount() - 1, s);
        return result;
    }

    public void setTitleAt(int idx, String title) {
        String nue = title.indexOf("</html>") != -1 ? //NOI18N
            Utilities.replaceString(title, "</html>", "&nbsp;&nbsp;</html>") //NOI18N
            : title + "  ";
        if (!title.equals(getTitleAt(idx))) {
            super.setTitleAt(idx, nue);
        }
    }

    private void reset() {
        setMouseOverCloseButtonIndex(-1);
        setPressedCloseButtonIndex(-1);
        draggedOut = false;
    }

    private Rectangle getCloseButtonBoundsAt(int i) {
        Rectangle b = getBoundsAt(i);
        if (b == null)
            return null;
        else {
            b = new Rectangle(b);
            fixGetBoundsAt(b);

            Dimension tabsz = getSize();
            if (b.x + b.width >= tabsz.width
                || b.y + b.height >= tabsz.height)
                return null;

            return new Rectangle(b.x + b.width - 13,
                                 b.y + b.height / 2 - 5,
                                 8,
                                 8);
        }
    }


    /** Checks whether current L&F sets used keys for colors.
     * If not puts default values. */
    private static void checkUIColors() {
        if(UIManager.getColor("Button.shadow") == null) { // NOI18N
            UIManager.put("Button.shadow", // NOI18N
                new ColorUIResource(153, 153, 153));
        }
        if(UIManager.getColor("Button.darkShadow") == null) { // NOI18N
            UIManager.put("Button.darkShadow", // NOI18N
                new ColorUIResource(102, 102, 102));
        }
        if(UIManager.getColor("Button.highlight") == null) { // NOI18N
            UIManager.put("Button.highlight", // NOI18N
                new ColorUIResource(Color.white));
        }
        if(UIManager.getColor("Button.background") == null) { // NOI18N
            UIManager.put("Button.background", // NOI18N
                new ColorUIResource(204, 204, 204));
        }
    }
    
    public void paint(Graphics g) {
        super.paint(g);

        // #29181 All L&F doesn't support the colors used.
        checkUIColors();

        // Have a look at
        // http://ui.netbeans.org/docs/ui/closeButton/closeButtonUISpec.html
        // to see how the buttons are specified to be drawn.

        int selectedIndex = getSelectedIndex();
        for (int i = 0, n = getTabCount(); i < n; i++) {
            Rectangle r = getCloseButtonBoundsAt(i);
            if (r == null)
                continue;
            
            if(i == pressedCloseButtonIndex && !draggedOut) {
                g.setColor(UIManager.getColor("Button.shadow")); //NOI18N
                g.fillRect(r.x , r.y, r.width, r.height);
            }
            
            if (i != selectedIndex)
                g.drawImage(closeTabInactiveImage, r.x + 2, r.y + 2, this);
            else
                g.drawImage(closeTabImage, r.x + 2, r.y + 2, this);
            
            if (i == mouseOverCloseButtonIndex
            || (i == pressedCloseButtonIndex && draggedOut)) {
                g.setColor(UIManager.getColor("Button.darkShadow")); //NOI18N
                g.drawRect(r.x, r.y, r.width, r.height);
                g.setColor(i == selectedIndex
                    ? UIManager.getColor("Button.highlight") //NOI18N
                    : UIManager.getColor("Button.background")); //NOI18N
                g.drawRect(r.x + 1, r.y + 1, r.width, r.height);
                
                // Draw the dots.
                g.setColor (UIManager.getColor ("Button.highlight").brighter()); //NOI18N
                g.drawLine(r.x + r.width, r.y + 1, r.x + r.width, r.y + 1);
                g.drawLine(r.x + 1, r.y + r.height, r.x + 1, r.y + r.height);
            } else if (i == pressedCloseButtonIndex) {
                g.setColor(UIManager.getColor("Button.shadow")); //NOI18N
                g.drawRect(r.x, r.y, r.width, r.height);
                g.setColor(i == selectedIndex
                    ? UIManager.getColor("Button.highlight") //NOI18N
                    : UIManager.getColor("Button.background")); //NOI18N
                g.drawLine(r.x + 1,
                           r.y + r.height + 1,
                           r.x + r.width + 1,
                           r.y + r.height + 1);
                g.drawLine(r.x + r.width + 1,
                           r.y + 1,
                           r.x + r.width + 1,
                           r.y + r.height + 1);
                
                // Draw the lines.
                g.setColor(UIManager.getColor("Button.background")); //NOI18N
                g.drawLine(r.x + 1, r.y + 1, r.x + r.width, r.y + 1);
                g.drawLine(r.x + 1, r.y + 1, r.x + 1, r.y + r.height);
            }
        }
    }

    private void setPressedCloseButtonIndex(int index) {
        if (pressedCloseButtonIndex == index)
            return;

        if (pressedCloseButtonIndex >= 0
        && pressedCloseButtonIndex < getTabCount()) {
            Rectangle r = getCloseButtonBoundsAt(pressedCloseButtonIndex);
            repaint(r.x, r.y, r.width + 2, r.height + 2);

            JComponent c = (JComponent)
                getComponentAt(pressedCloseButtonIndex);
            setToolTipTextAt(pressedCloseButtonIndex, c.getToolTipText());
        }

        pressedCloseButtonIndex = index;

        if (pressedCloseButtonIndex >= 0
        && pressedCloseButtonIndex < getTabCount()) {
            Rectangle r = getCloseButtonBoundsAt(pressedCloseButtonIndex);
            repaint(r.x, r.y, r.width + 2, r.height + 2);
            setMouseOverCloseButtonIndex(-1);
            setToolTipTextAt(pressedCloseButtonIndex, null);
        }
    }

    private void setMouseOverCloseButtonIndex(int index) {
        if (mouseOverCloseButtonIndex == index)
            return;

        if (mouseOverCloseButtonIndex >= 0
        && mouseOverCloseButtonIndex < getTabCount()) {
            Rectangle r = getCloseButtonBoundsAt(mouseOverCloseButtonIndex);
            repaint(r.x, r.y, r.width + 2, r.height + 2);
            JComponent c =  (JComponent)
                getComponentAt(mouseOverCloseButtonIndex);
            setToolTipTextAt(mouseOverCloseButtonIndex, c.getToolTipText());
        }

        mouseOverCloseButtonIndex = index;

        if (mouseOverCloseButtonIndex >= 0
        && mouseOverCloseButtonIndex < getTabCount()) {
            Rectangle r = getCloseButtonBoundsAt(mouseOverCloseButtonIndex);
            repaint(r.x, r.y, r.width + 2, r.height + 2);
            setPressedCloseButtonIndex(-1);
            setToolTipTextAt(mouseOverCloseButtonIndex, null);
        }
    }

    private void fireCloseRequest(Component c) {
        firePropertyChange(PROP_CLOSE, null, c);
    }

    public static void fixGetBoundsAt(Rectangle b) {
        if (b.y < 0)
            b.y = -b.y;
        if (b.x < 0)
            b.x = -b.x;
    }

    public static int findTabForCoordinate(JTabbedPane tab, int x, int y) {
        for (int i = 0; i < tab.getTabCount(); i++) {
            Rectangle b = tab.getBoundsAt(i);
            if (b != null) {
                b = new Rectangle(b);
                fixGetBoundsAt(b);

                if (b.contains(x, y)) {
                    return i;
                }
            }
        }
        return -1;
    }
    

    protected void processMouseEvent (MouseEvent me) {
        try {
            super.processMouseEvent (me);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            //Bug in BasicTabbedPaneUI$Handler:  The focusIndex field is not
            //updated when tabs are removed programmatically, so it will try to
            //repaint a tab that's not there
            ErrorManager.getDefault().annotate(aioobe, "Suppressed " + //NOI18N
                    "AIOOBE bug in BasicTabbedPaneUI"); //NOI18N
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, aioobe);
        }
    }


    private static class CloseButtonListener implements AWTEventListener
    {
        private static boolean installed = false;

        private CloseButtonListener() {}

        private static synchronized void install() {
            if (installed)
                return;

            installed = true;
            Toolkit.getDefaultToolkit().addAWTEventListener(
                new CloseButtonListener(),
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        }

        public void eventDispatched (AWTEvent ev) {
            MouseEvent e = (MouseEvent) ev;

            Component c = (Component) e.getSource();
            while (c != null && !(c instanceof CloseButtonTabbedPane))
                c = c.getParent();
            if (c == null)
                return;
            final CloseButtonTabbedPane tab = (CloseButtonTabbedPane) c;

            Point p = SwingUtilities.convertPoint((Component) e.getSource(),
                                                  e.getPoint(),
                                                  tab);

            if (e.getID() == MouseEvent.MOUSE_CLICKED) {
                //Not interested in clicked, and it can cause an NPE
                return;
            }
            
            int index = findTabForCoordinate(tab, p.x, p.y);

            Rectangle r = null;
            if (index >= 0)
                r = tab.getCloseButtonBoundsAt(index);
            if (r == null)
                r = new Rectangle(0,0,0,0);

            switch(e.getID()) {
                case MouseEvent.MOUSE_PRESSED:
                    if (r.contains(p)) {
                        tab.setPressedCloseButtonIndex(index);
                        tab.draggedOut = false;
                        e.consume();
                        return;
                    }
                    break;

                case MouseEvent.MOUSE_RELEASED:
                    if (r.contains(p) && tab.pressedCloseButtonIndex >= 0) {
                        Component tc =
                            tab.getComponentAt(tab.pressedCloseButtonIndex);
                        tab.reset();

                        tab.fireCloseRequest(tc);
                        e.consume();
                        return;
                    }
                    else {
                        tab.reset();
                    }
                    break;

                case MouseEvent.MOUSE_ENTERED:
                    break;

                case MouseEvent.MOUSE_EXITED:
                    //tab.reset();

                    // XXX(-ttran) when the user clicks on the close button on
                    // an unfocused (internal) frame the focus is transferred
                    // to the frame and an unexpected MOUSE_EXITED event is
                    // fired.  If we call reset() at every MOUSE_EXITED event
                    // then when the mouse button is released the tab is not
                    // closed.  See bug #24450
                    
                    break;

                case MouseEvent.MOUSE_MOVED:
                    if (r.contains(p)) {
                        tab.setMouseOverCloseButtonIndex(index);
                        tab.draggedOut = false;
                        e.consume();
                        return;
                    }
                    else if (tab.mouseOverCloseButtonIndex >= 0) {
                        tab.setMouseOverCloseButtonIndex(-1);
                        tab.draggedOut = false;
                        e.consume();
                    }
                    break;

                case MouseEvent.MOUSE_DRAGGED:
                    if (tab.pressedCloseButtonIndex >= 0) {
                        if (tab.draggedOut != !r.contains(p)) {
                            tab.draggedOut = !r.contains(p);
                            tab.repaint(r.x, r.y, r.width + 2, r.height + 2);
                        }
                        e.consume();
                        return;
                    }
                    break;
            }
        }
    }
}
