/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.cottagesystems.nbidl.debugger.breakpoints;

import javax.swing.Action;

import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.spi.viewmodel.NodeActionsProvider;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.netbeans.spi.viewmodel.Models;
import org.netbeans.spi.viewmodel.NodeActionsProviderFilter;
import org.openide.text.Line;
import org.openide.util.NbBundle;


/**
 * @author   Jan Jancura
 */
public class BreakpointsActionsProvider implements NodeActionsProviderFilter {
    
    private static final Action GO_TO_SOURCE_ACTION = Models.createAction(
            "Goto Source",
            new Models.ActionPerformer() {
        public boolean isEnabled(Object node) {
            return true;
        }
        public void perform(Object[] nodes) {
            gotoSource( (PvwaveBreakpoint)nodes[0] ) ;
        }
    },
            Models.MULTISELECTION_TYPE_EXACTLY_ONE
            );
    
    private static String loc(String key) {
        return NbBundle.getBundle(BreakpointsActionsProvider.class).getString(key);
    }
    
    public Action[] getActions(NodeActionsProvider original, Object node)
    throws UnknownTypeException {
        if (!(node instanceof PvwaveBreakpoint))
            return original.getActions(node);
        
        Action[] oas = original.getActions(node);
        if (node instanceof PvwaveBreakpoint) {
            Action[] as = new Action [oas.length + 1];
            as [0] = GO_TO_SOURCE_ACTION;
            System.arraycopy(oas, 0, as, 1, oas.length);
            return as;
        } else {
            return oas;
        }
    }
    
    public void performDefaultAction(NodeActionsProvider original, Object node) throws UnknownTypeException {
        if (node instanceof PvwaveBreakpoint) {
            gotoSource( (PvwaveBreakpoint)node ) ;
        } else{
            original.performDefaultAction(node);
        }
    }
    
    private static void gotoSource( PvwaveBreakpoint pb ) {
        pb.getLine().getLineObject().show( Line.SHOW_GOTO );
    }
    
    public void addModelListener(ModelListener l) {
    }
    
    public void removeModelListener(ModelListener l) {
    }
    
    public static void customize(Breakpoint b) {
    }
    
}
