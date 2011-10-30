/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.cottagesystems.nbidl.debugger;

import com.cottagesystems.nbidl.debugger.breakpoints.PvwaveBreakpoint;
import com.cottagesystems.nbidl.session.AbstractIDLSession;
import com.cottagesystems.nbidl.session.SessionSupport;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.netbeans.api.debugger.ActionsManager;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import com.cottagesystems.nbidl.debugger.breakpoints.BreakpointModel;
import com.cottagesystems.nbidl.idloutput2.PvwaveIO;
import com.cottagesystems.nbidl.idloutput2.PvwaveIOProvider;
import com.cottagesystems.nbidl.session.IDLOutputHandler;
import com.cottagesystems.nbidl.session.Session;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerManagerListener;
import org.netbeans.api.debugger.Watch;
import org.netbeans.api.project.Project;
import org.netbeans.editor.BaseDocument;
import org.netbeans.spi.debugger.ActionsProviderSupport;
import org.netbeans.spi.debugger.ContextProvider;
import org.netbeans.spi.debugger.DebuggerEngineProvider;
import org.netbeans.spi.viewmodel.ModelEvent;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.TableModel;
import org.netbeans.spi.viewmodel.TreeModel;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;

/**
 * Ant debugger.
 *
 * @author  Honza
 */
public class PvwaveDebugger extends ActionsProviderSupport {
    
    
    /** The ReqeustProcessor used by action performers. */
    private static RequestProcessor     actionsRequestProcessor;
    
    private static Logger logger= Logger.getLogger("pvwave.debugger");
    
    private PvwaveStop              currentStop;
    
    private Session pvwaveSession;
    private PvwaveDebuggerEngineProvider   engineProvider;
    private ContextProvider             contextProvider;
    private Object                      LOCK = new Object();
    private Object                      LOCK_ACTIONS = new Object();
    private boolean                     actionRunning = false;
    
    private Object                      currentLine;
    private LinkedList                  callStackList = new LinkedList();
    private File                        currentFile;
    private String                      currentTargetName;
    private String                      currentTaskName;
    private int                         originatingIndex = -1; // Current index of the virtual originating target in the call stack
    private PvwaveIO io;
    
    private Reader inputReader;
    private Writer inputWriter; // write to IDL Session
    
    // for run-to-cursor.
    private PvwaveBreakpoint clearMeBreakpoint=null;
    
    /** danger **/
    private static PvwaveDebugger instance;
    
    public PvwaveDebugger( ContextProvider contextProvider ) {
        this.contextProvider = contextProvider;
        
        this.pvwaveSession= SessionSupport.getSessionInstance();
        
        instance= this;
        
        // init engineProvider
        engineProvider = (PvwaveDebuggerEngineProvider) contextProvider.lookupFirst
                (null, DebuggerEngineProvider.class);
        
        // init actions
        setActionsEnabled(true);
        
        String tabName = "pvwave";
        
        //InputOutput io = IOProvider.getDefault().getIO(tabName, new Action[] { SessionSupport.getStopAction() } );
        io = PvwaveIOProvider.getDefault().getIO(tabName, false );
        io.select();
        
        io.getOut().println("Starting IDL Session...");
        
        IDLOutputHandler outputHandler= new IDLOutputHandler( io.getOut(), io.getErr() );
        pvwaveSession.setOutputHandler( outputHandler );
        outputHandler.addIDLOutputListener( getIDLOutputListener() );
        
        inputReader= io.getIn();
        
        io.setInputVisible(true);
        
        DebuggerManager.getDebuggerManager().getDebuggerEngines();
        
        Runnable run= new Runnable() {
            public void run() {
                try {
                    pvwaveSession.setStdin( startInputThread() );
                } catch (IOException ex) {
                    throw new RuntimeException( ex );
                }
                
                pvwaveSession.start();
                
                if ( !pvwaveSession.isStarted() ) {
                    throw new RuntimeException("failure to start session");
                }
                
                DebuggerManager.getDebuggerManager().addDebuggerListener( getDebuggerListener() ) ;
                
                resetBreakpoints();
                currentStop= PvwaveStop.MAIN;
                
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                
                getCallStackModel().invalidate();
                getCallStackModel().fireChanges();
            }
        };
        
        new Thread( run, "startSessionThread" ).start();
        
        //    resetBreakpoints();
        
    }
    
//    Annotation pcAnnotation;
    
    public static PvwaveDebugger getInstance() {
        return instance;
    }
    
    
    private  Reader startInputThread() throws IOException {
        PipedOutputStream pout= new PipedOutputStream();
        final OutputStreamWriter writer= new OutputStreamWriter(pout);
        PipedInputStream pin= new PipedInputStream(pout);
        Reader result= new InputStreamReader(pin);
        
        Runnable run= new Runnable() {
            public void run() {
                try {
                    int charsRead;
                    char[] buffer= new char[512];
                    while ( ( charsRead= inputReader.read(buffer) ) > -1 ) {
                        String cmd=  new String( buffer, 0, charsRead-1 );
                        pvwaveSession.invokeCommand( cmd ); // -1 omits the \n
                    }
                } catch ( IOException e ) {
                    throw new RuntimeException(e);
                }
            }
        };
        new Thread( run, "userInputThread" ).start();
        
        return result;
    }
    
    private void setActionsEnabled( boolean enabled ) {
        for (Iterator it = actions.iterator(); it.hasNext(); ) {
            Object a= it.next();
            if ( !a.equals(ActionsManager.ACTION_PAUSE ) ) setEnabled( a, enabled );
            if ( a.equals(ActionsManager.ACTION_KILL ) ) setEnabled(a,true);
        }
    }
    
    private ArrayList<PvwaveBreakpoint> sessionBreakpoints( ) {
        Pattern bpPattern= Pattern.compile("([0-9]+)\\s+([0-9]+) +(.*)\\s+(.*).pro\\s*");
        String result= SessionSupport.getCommandResponse( pvwaveSession, "help, /breakpoints" );
        String[] lines= result.split("\n");
        for ( int i=1; i<lines.length-1; i++ ) {
            if ( !Character.isDigit(lines[i+1].charAt(0)) && !lines[i+1].startsWith("IDL>" ) ) {
                lines[i]= lines[i]+lines[i+1];
                lines[i+1]="";
            }
        }
        ArrayList<PvwaveBreakpoint> bps= new ArrayList<PvwaveBreakpoint>();
        for ( int i=1; i<lines.length; i++ ) {
            Matcher m= bpPattern.matcher(lines[i]);
            if ( m.matches() ) {
                PvwaveStop stop=null;
                
                stop= new PvwaveStop( m.group(4) + ".pro" , Integer.parseInt(m.group(2)) );
                int index= Integer.parseInt(m.group(1));
                
                PvwaveBreakpoint bp= new PvwaveBreakpoint(stop);
                bp.setIndex( index );
                
                if ( index>bps.size() ) {
                    for ( int i2=bps.size(), n=index; i2<=n; i2++ ) {
                        bps.add( i2, bp );
                    }
                }
                
                try{
                    bps.add( index, bp );
                } catch (Exception e) {
                    logger.fine(e.toString());
                    throw new RuntimeException(e);
                }
            }
        }
        return bps;
    }
    
    /**
     * set the IDl session breakpoints to be the same as those specified in the
     * Netbeans model.
     */
    private void resetBreakpoints() {
        logger.fine("reset breakpoints");
        
        if ( pvwaveSession==null || !pvwaveSession.isStarted() ) return;
        ArrayList<PvwaveBreakpoint> sessionBreakpoints= sessionBreakpoints();
        logger.fine("session breakpoint count: "+sessionBreakpoints.size());
        DebuggerManager dbman= DebuggerManager.getDebuggerManager();
        Breakpoint[] bs = dbman.getBreakpoints();
        
        List<Breakpoint> ideBreakpoints= Arrays.asList(bs);
        logger.fine("ide breakpoint count: "+ideBreakpoints.size());
        for ( int i=0; i<sessionBreakpoints.size(); i++ ) {
            PvwaveBreakpoint pb1= sessionBreakpoints.get(i) ;
            if ( !ideBreakpoints.contains( pb1 ) ) {
                SessionSupport.getCommandResponse( pvwaveSession, "breakpoint, /clear, "+pb1.getIndex() );
            }
        }
        for ( int i=0; i<ideBreakpoints.size(); i++ ) {
            Breakpoint bp=  ideBreakpoints.get(i);
            if ( bp instanceof PvwaveBreakpoint
                    && !sessionBreakpoints.contains(bp) ) {
                PvwaveBreakpoint pvb= (PvwaveBreakpoint) bp;
                SessionSupport.setBreakpoint( pvwaveSession, pvb );
            }
        }
    }
    
    private IDLOutputHandler.IDLOutputListener getIDLOutputListener() {
        return new IDLOutputHandler.IDLOutputListener() {
            public void event( IDLSessionEvent ev ) {
                int type= ev.getType();
                if ( type==ev.TYPE_PROMPT ) {
                    io.setInputVisible(true);
                    setActionsEnabled(true);
                    io.setPrompt("IDL>");
                } else if ( type==ev.TYPE_STOP ) {
                    stopHere( ev.getPvwaveStop() );
                    setActionsEnabled(true);
                    setEnabled( ActionsManager.ACTION_PAUSE, false );
                    io.setPrompt("IDL#");
                } else if ( type==ev.TYPE_BUSY ) {
                    setActionsEnabled(false);
                    setEnabled( ActionsManager.ACTION_PAUSE, true );
                    //io.setInputVisible(false);
                    //don't unmark--instead we'll look for .continue... Utils.unmarkCurrent();
                    // unicode 29D6, white hourglass
                    // 221E, infinity
                    // 231B, another hourglass
                    io.setPrompt("IDL\u221E");
                } else if ( type==ev.TYPE_CONTINUE ) {
                    Utils.unmarkCurrent();
                }
            }
        };
    }
    
// ActionsProvider .........................................................
    
    private static final Set actions = new HashSet();
    static {
        actions.add(ActionsManager.ACTION_KILL);
        actions.add(ActionsManager.ACTION_CONTINUE);
        actions.add(ActionsManager.ACTION_PAUSE);
        actions.add(ActionsManager.ACTION_FIX);
        actions.add(ActionsManager.ACTION_START);
        actions.add(ActionsManager.ACTION_STEP_INTO);
        actions.add(ActionsManager.ACTION_STEP_OVER);
        actions.add(ActionsManager.ACTION_STEP_OUT);
        actions.add(ActionsManager.ACTION_RUN_TO_CURSOR);
        actions.add(ActionsManager.ACTION_TOGGLE_BREAKPOINT);
    }
    
    public Set getActions() {
        return actions;
    }
    
    public void doAction(Object action) {
        logger.fine("action: "+action);
        synchronized (LOCK_ACTIONS) {
            actionRunning = true;
        }
        if (action == ActionsManager.ACTION_KILL) {
            finish();
            this.engineProvider.getDestructor().killEngine();
        } else if ( action==ActionsManager.ACTION_PAUSE ) {
            doPause();
            updateUI();
        } else if ( action==ActionsManager.ACTION_FIX ) {
            doFix();
        } else if ( action==ActionsManager.ACTION_RUN_TO_CURSOR ) {
            doRunToCursor();
        } else {
            if (action == ActionsManager.ACTION_CONTINUE) {
                doContinue();
            } else
                if (action == ActionsManager.ACTION_START) {
                } else
                    if ( action == ActionsManager.ACTION_STEP_INTO ||
                    action == ActionsManager.ACTION_STEP_OUT ||
                    action == ActionsManager.ACTION_STEP_OVER
                    ) {
                doStep(action);
                    }
        }
    }
    
    public void postAction(final Object action, final Runnable actionPerformedNotifier) {
        setActionsEnabled(false);
        Utils.unmarkCurrent();
        synchronized (PvwaveDebugger.class) {
            if (actionsRequestProcessor == null) {
                actionsRequestProcessor = new RequestProcessor("Pvwave debugger actions RP", 1);
            }
        }
        actionsRequestProcessor.post(new Runnable() {
            public void run() {
                try {
                    doAction(action);
                } finally {
                    actionPerformedNotifier.run();
                    // if ( action!=ActionsManager.ACTION_KILL ) {
                    //    setActionsEnabled( true );
                    // }
                }
            }
        });
    }
    
    
// other methods ...........................................................
    
    private void stopHere( PvwaveStop stop ) {
        
        if ( stop==currentStop ) return;
        
        this.currentStop= stop;
        
        //if ( clearMeBreakpoint!=null && clearMeBreakpoint.getLine().equals(stop) ) {
        //    DebuggerManager.getDebuggerManager().removeBreakpoint( clearMeBreakpoint );
       // }
        
        updateUI();
        
        // update variable values
        //jbf1242  getVariablesModel().fireChanges();
        //jbf1242   getBreakpointModel().fireChanges();
        
       /* // enable actions
        synchronized (LOCK_ACTIONS) {
            actionRunning = false;
            LOCK_ACTIONS.notifyAll();
        }*/
        
    /*    // wait for next stepping orders
        synchronized (LOCK) {
            try {
                LOCK.wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }*/
    }
    
    private void updatePC() {
        if ( currentStop!=null && currentStop!=PvwaveStop.MAIN ) {
            try {
                currentLine = new Line[] { SessionSupport.getLineObject( currentStop.getSource(), currentStop.getLine() ) };
                // TODO: getCallStack can produce an array of lines to visualize the whole stack trace
                Utils.markCurrent( currentLine );
            } catch ( IllegalArgumentException ex ) {
                currentLine=null;
                Utils.unmarkCurrent();
            } catch ( IndexOutOfBoundsException e ) {
                currentLine=null;
                Utils.unmarkCurrent();
            }
        } else {
            Utils.unmarkCurrent();
        }
    }
    
    
    private void updateUI() {
        Runnable run=  new Runnable() {
            public void run() {
                updatePC();
                getCallStackModel().invalidate();
                getCallStackModel().fireChanges();
                getWatchesModel().fireChanges();
            }
        };
        SwingUtilities.invokeLater(run);
    }
    
    public Object getCurrentLine() {
        return currentLine;
    }
    
    
// stepping hell ...........................................................
    
    private Object      lastAction;
    private Set         finishedTasks = new HashSet();
    
    private File        fileToStopAt = null;
    
    private void doContinue() {
        Utils.unmarkCurrent();
        logger.fine("continue");
        pvwaveSession.invokeCommand(".continue");
        doEngineStep();
    }
    
    
    private void doRunToCursor() {
        Node node= (Node) TopComponent.getRegistry().getActivatedNodes()[0];
        Line line= Utils.getCarotLine( (EditorCookie) node.getCookie( EditorCookie.class ) );
        clearMeBreakpoint= new PvwaveBreakpoint( PvwaveStop.createStop(line) );
        DebuggerManager.getDebuggerManager().addBreakpoint( clearMeBreakpoint );
        Utils.unmarkCurrent();
        logger.fine("runToCursor");
        pvwaveSession.invokeCommand(".continue");
        doEngineStep();
    }
    /**
     * should define callStack based on callStackInternal & action.
     */
    private void doStep(Object action) {
        if ( ((AbstractIDLSession)pvwaveSession).isBusy() ) {
            logger.fine("busy before step");
        }
        if (action == ActionsManager.ACTION_STEP_INTO) {
            logger.fine("step");
            pvwaveSession.invokeCommand(".step");
        } else if (action == ActionsManager.ACTION_STEP_OVER) {
            logger.fine("step over");
            pvwaveSession.invokeCommand(".stepover");
        } else if (action == ActionsManager.ACTION_STEP_OUT) {
            logger.fine("step out");
            pvwaveSession.invokeCommand(".out");
        } else {
            throw new IllegalArgumentException(action.toString());
        }
        doEngineStep();
    }
    
    
    private void doEngineStep() {
        synchronized (LOCK) {
            LOCK.notify();
        }
    }
    
    private void finish() {
        if ( pvwaveSession.isStarted() ) {
            Utils.unmarkCurrent();
            pvwaveSession.getOutputHandler().stdoutReceived("\n\n(Session ended)\n\n");
            pvwaveSession.invokeCommand( new String( new byte[] { 3 } ) );
            pvwaveSession.invokeCommand("exit");
            pvwaveSession.close();
        }
        
        fileToStopAt = null;
        for (Iterator it = actions.iterator(); it.hasNext(); ) {
            Object a= it.next();
            setEnabled(a,false);
        }
        synchronized (LOCK) {
            LOCK.notify();
        }
    }
    
    
// support for call stack ..................................................
    
    private CallStackModel              callStackModel;
    
    private CallStackModel getCallStackModel() {
        if (callStackModel == null)
            callStackModel = (CallStackModel) contextProvider.lookupFirst
                    ("CallStackView", TreeModel.class);
        return callStackModel;
    }
    
    Object[] getCallStack() {
        if ( pvwaveSession.isStarted() ) {
            String result= SessionSupport.getCommandResponse( pvwaveSession, "help, /trace" );
            String[] lines= result.split("\n");
            Object[] elements= new Object[lines.length];
            for ( int i=0; i<elements.length; i++ ) {
                PvwaveStop stop=null;
                try {
                    stop = SessionSupport.getStopReference( lines[i], false );
                    if ( i==0 ) {
                        if ( ! ( this.currentStop==stop ) ) {
                            this.currentStop= stop;
                            updatePC();
                        }
                    }
                } catch (DataObjectNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                if ( stop!=null ) {
                    elements[i]= StackElement.create( lines[i] );
                    if ( i==0 ) {
                        ((StackElement)elements[i]).getActions().add( new AbstractAction("retall") {
                            public void actionPerformed( ActionEvent e ) {
                                pvwaveSession.invokeCommand("retall");
                            }
                        });
                    } else if ( i==1 ) {
                        ((StackElement)elements[i]).getActions().add( new AbstractAction("pop to here") {
                            public void actionPerformed( ActionEvent e ) {
                                pvwaveSession.invokeCommand("on_error,2");
                                pvwaveSession.invokeCommand(".cont");
                            }
                        });
                    }
                } else {
                    elements[i]= lines[i];
                }
            }
            return elements;
        } else {
            return new Object[] { "Session not started" };
        }
    }
    
    /**
     * File as a script location is a key. Values are maps of name to Target.
     */
    private Map nameToTargetByFiles = new HashMap();
    /**
     * File as a script location is a key, values are project names.
     */
    private Map projectNamesByFiles = new HashMap();
    
// support for variables ...................................................
    
    private VariablesModel              variablesModel;
    
    private VariablesModel getVariablesModel() {
        if (variablesModel == null)
            variablesModel = (VariablesModel) contextProvider.lookupFirst
                    ("LocalsView", TreeModel.class);
        return variablesModel;
    }
    
    private BreakpointModel             breakpointModel;
    
    private BreakpointModel getBreakpointModel() {
        if (breakpointModel == null) {
            Iterator it = DebuggerManager.getDebuggerManager().lookup
                    ("BreakpointsView", TableModel.class).iterator();
            while (it.hasNext()) {
                TableModel model = (TableModel) it.next();
                if (model instanceof BreakpointModel) {
                    breakpointModel = (BreakpointModel) model;
                    break;
                }
            }
        }
        return breakpointModel;
    }
    
    
    
    // support for call stack ..................................................
    
    private WatchesModel              watchesModel;
    
    private WatchesModel getWatchesModel() {
        if (watchesModel == null)
            watchesModel = (WatchesModel) contextProvider.lookupFirst
                    ("WatchesView", TreeModel.class);
        return watchesModel;
    }
    
    
    String evaluate(String expression) {
        String value = getExpressionValue(expression);
        System.err.println( "'"+expression+"'="+value );
        
        if (value != null) return value;
        return "unable to evaluate";
    }
    
    private String[] variables = new String [0];
    
    String[] getVariables() {
        String variables= evaluate( "help, names='*'" );
        String[] ss= variables.split("/n");
        int n= Math.min( ss.length, 20 );
        ArrayList vars= new ArrayList(n);
        for ( int i=0; i<n; i++ ) {
            vars.add("a");
        }
        return (String[]) vars.toArray(new String[n]);           
    }
    
    String getExpressionValue(String variableName) {
        if ( !pvwaveSession.isStarted() ) {
            return "(pvwave session not started)";
        } else {
            try {
                String cmd= "nbidl_pprint, "+variableName;
                String response= SessionSupport.getCommandResponse( pvwaveSession, cmd );
                String lineEnd= "\r\n";
                return response;
            } catch ( Exception e ) {
                return "(exception occurred)";
            }
        }
    }
    
    private ModelListener getBreakpointModelListener() {
        return new ModelListener() {
            public void modelChanged(ModelEvent event) {
                resetBreakpoints();
            }
        };
    }
    
    private DebuggerManagerListener getDebuggerListener() {
        return new DebuggerManagerListener() {
            public Breakpoint[] initBreakpoints() {
                return new Breakpoint[0];
            }
            
            public void breakpointAdded(Breakpoint breakpoint) {
                if ( breakpoint instanceof PvwaveBreakpoint ) {
                    //SessionSupport.setBreakpoint( pvwaveSession, (PvwaveBreakpoint)breakpoint );
                    resetBreakpoints();
                }
            }
            
            public void breakpointRemoved(Breakpoint breakpoint) {
                resetBreakpoints();
            }
            
            public void initWatches() {
            }
            
            public void watchAdded(Watch watch) {
                updateUI();
            }
            
            public void watchRemoved(Watch watch) {
                updateUI();
            }
            
            public void sessionAdded(org.netbeans.api.debugger.Session session) {
                System.err.println("sessionAdded");
            }
            
            public void sessionRemoved(org.netbeans.api.debugger.Session session) {
                System.err.println("sessionRemoved");
            }
            
            public void engineAdded(DebuggerEngine engine) {
            }
            
            public void engineRemoved(DebuggerEngine engine) {
            }
            
            public void propertyChange(PropertyChangeEvent evt) {
            }
        };
    }
    
    private void doPause() {
        logger.fine("break");
        pvwaveSession.invokeCommand( new String( new byte[] { 3 } ) );
    }
    
    private void doFix() {
        logger.fine("fix");
        
        Node[] nodes = TopComponent.getRegistry().getCurrentNodes();
        if (nodes == null) return ;
        if (nodes.length != 1) return ;
        Node n = nodes [0];
        
        DataObject c = (DataObject) n.getCookie(DataObject.class);
        EditorCookie cc= (EditorCookie) n.getCookie( EditorCookie.class );
        try {
            cc.saveDocument();
            pvwaveSession.reload( pvwaveSession.getFilenameForFileObject(c.getPrimaryFile()));
        } catch ( IOException e ) {
            ErrorManager.getDefault().notify(e);
        }
    }

    /**
     * returns the current stop for the session.
     * null indicates main.
     */
    public PvwaveStop getCurrentStop() {
        return currentStop;
    }

    public Session getSession() {
        return this.pvwaveSession;
    }

    BaseDocument commandLineDoc;

    /** provide means to identify command line when completing
     */
    public void setCommandLineDoc( BaseDocument doc ) {
        this.commandLineDoc= doc;
    }
    
    public boolean isCommandLine(BaseDocument doc) {
        return doc==commandLineDoc;
    }

}
