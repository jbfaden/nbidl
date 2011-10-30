/*
 * UserRoutinesDataBase.java
 *
 * Created on August 1, 2006, 9:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.syntax;

import com.cottagesystems.nbidl.completion.*;
import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.dataobject.ProceduresFile;
import com.cottagesystems.nbidl.model.IdlClass;
import com.cottagesystems.nbidl.model.IdlSession;
import com.cottagesystems.nbidl.session.SessionSupport;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.text.JTextComponent;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.editor.TokenID;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.DataShadow;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;

/**
 *
 * @author jbf
 */
public class UserRoutinesDataBase {
    
    static UserRoutinesDataBase instance;
    boolean scanning= false;
    boolean needToScan= true;
    boolean cancel= false;
    
    private HashMap userRoutines;
    private HashMap lastUserRoutines;
    
    CompletionItem scanningCompletionItem= new ScanningCompletionItem();
    
    /**
     * a model for the session.  This contains the names of structures, objects, etc.  Eventually
     * everything in UserRoutinesDataBase and IDLRoutinesDataBase will be moved into
     * one unified session model.
     */
    IdlSession idlSession;
    
    class ScanningCompletionItem implements CompletionItem {
        public void defaultAction(JTextComponent jTextComponent) {  }
        public void processKeyEvent(KeyEvent keyEvent) { }
        public int getPreferredWidth(Graphics graphics, Font font) {
            return 210;
        }
        public void render(Graphics graphics, Font font, Color color, Color color0, int i, int i0, boolean b) {
            CompletionUtilities.renderHtml( null,"<em>Scanning in of IDL Path in progress</em>",null,graphics,font, color,i,i0,b);
        }
        public CompletionTask createDocumentationTask() { return null; }
        public CompletionTask createToolTipTask() { return null; }
        public boolean instantSubstitution(JTextComponent jTextComponent) { return false; }
        public int getSortPriority() { return 1;  }
        public CharSequence getSortText() { return "aaa"; }
        public CharSequence getInsertPrefix() { return ""; }
    }
    
    /** Creates a new instance of UserRoutinesDataBase */
    public UserRoutinesDataBase() {
        userRoutines= new HashMap();
        idlSession= SessionSupport.getIdlSessionInstance();
    }
    
    public synchronized static UserRoutinesDataBase getInstance() {
        if ( instance==null ) {
            instance= new UserRoutinesDataBase();
        }
        return instance;
    }
    
    /**
     * return the UserRoutinesDataBase for the context of the DataObject.  This
     * checks to see if the dobj has a Project associated with it, and returns
     * the project's database.  If not found, then behavior is indeterminate. (Right
     * now, I'll just return the static instance, but that behavior may change.)
     *
     * This is introduced to ease the transition to projects.
     */
    public synchronized static UserRoutinesDataBase getInstance( FileObject fo ) {
        UserRoutinesDataBase result= getInstance();
        if ( fo==null ) {
            System.err.println("unable to ID fileObject--completion from commandline?" );
            return result;
        }
        Project p= FileOwnerQuery.getOwner(fo);
        if ( p!=null ) {
            result= getInstance( p );
        }
        return result;
        
    }
    
    /**
     * return the UserRoutinesDataBase for the context of the DataObject.  This
     * checks to see if the dobj has a Project associated with it, and returns
     * the project's database.  If not found, then behavior is indeterminate. (Right
     * now, I'll just return the static instance, but that behavior may change.)
     *
     * This is introduced to ease the transition to projects.
     */
    public synchronized static UserRoutinesDataBase getInstance( Project p ) {
        UserRoutinesDataBase result= getInstance();
        result= (UserRoutinesDataBase) p.getLookup().lookup( UserRoutinesDataBase.class );
        if ( result==null ) throw new RuntimeException( "Project doesn't gave UserRoutinesDataBase");
        return result;
        
    }
    
    public void scanFile( FileObject file, OutputWriter out ) throws FileNotFoundException, IOException {
        //Reader reader= new InputStreamReader( file.getInputStream() );
        ProceduresFile pf= PvwaveSyntaxScraper.parse( file );
        pf.setFileObject( file );
        //reader.close();
        
        for ( int i=0; i<pf.procedureCount(); i++ ) {
            Procedure p= pf.getProcedure(i);
            
            String key= p.getName().toUpperCase();
            if ( key.length()<3 ) {
                System.err.println("too short for the user routines database, skipping: "+key);
                continue;
            }
            if ( userRoutines.get(key)!=null ) {
                Procedure pexist= (Procedure) userRoutines.get(key);
                if ( !pexist.getSourceFile().getFileObject().equals(p.getSourceFile().getFileObject())) {
                    
                    if ( out!=null ) {
                        out.println("multiple definition: "+key);
                        //TODO: put in links with outputListener
                        out.println("   existing def: "+pexist.getSourceFile().getFileObject().getPath() );
                        out.println("       this def: "+p.getSourceFile().getFileObject().getPath() );
                        out.println(" " );
                    }
                } else {
                    userRoutines.put( key, p );
                    if ( this!=instance) instance.userRoutines.put( key, p );
                }
            } else {
                userRoutines.put( key, p );
                if ( this!=instance) instance.userRoutines.put( key, p );
            }
            
            int ip= p.getName().indexOf("::");
            if ( ip != -1 ) {
                String className= p.getName().substring(0,ip);
                IdlClass idlClass= idlSession.getClass(className);
                if ( idlClass==null ) {
                    idlClass= idlSession.newClass(className);
                }
                idlClass.addMethod( p );
            }
        }
    }
    
    private int scanFolder( FileObject fo, ProgressHandle monitor, int folderCount, OutputWriter out )throws FileNotFoundException, IOException {
        if ( cancel ) return folderCount;
        monitor.progress( FileUtil.getFileDisplayName(fo) );
        monitor.progress( folderCount );
        FileObject[] children= fo.getChildren();
        for ( int i=0; i<children.length; i++ ) {
            if ( children[i].isFolder() && !children[i].getName().equals("CVS") ) {
                folderCount++;
                folderCount= scanFolder( children[i], monitor, folderCount, out );
            } else {
                if ( children[i].getExt().equals( "pro" ) ) {
                    scanFile( children[i], out );
                }
            }
        }
        return folderCount;
    }
    
    /**
     * returns the number of folders within fo.  This is used to support progress
     */
    private int countFolder( FileObject fo ) {
        if ( cancel ) return 0;
        FileObject[] children= fo.getChildren();
        int count=0;
        for ( int i=0; i<children.length; i++ ) {
            if ( children[i].isFolder()  && !children[i].getName().equals("CVS") ) {
                count+= (1+countFolder( children[i] ));
            } else {
            }
        }
        return count;
    }
    
    private void scanFolder( DataObject folder, ProgressHandle monitor ) throws FileNotFoundException, IOException {
        if ( folder==null ) return;
        if ( cancel ) return;
        if ( folder instanceof DataShadow ) {
            DataShadow ds= (DataShadow)folder;
            folder= ds.getOriginal();
        }
        monitor.start();
        FileObject fo= folder.getPrimaryFile();
        
        monitor.progress("counting folders");
        int folderCount= countFolder(fo);
        monitor.switchToDeterminate(folderCount);
        
        InputOutput inOut=null;
        OutputWriter out=null;
        if ( inOut==null ) {
            inOut= IOProvider.getDefault().getIO("Warnings during path scan:",false);
            inOut.select();
            out= inOut.getOut();
            out.reset();
        }
        
        scanFolder(fo,monitor,0,out);
        monitor.finish();
    }
    
    class ScanTask implements Runnable {
        
        public void run() {
            Children children= root.getChildren();
            Node[] nodes= children.getNodes();
            lastUserRoutines= userRoutines; // keep for symbols
            userRoutines= new HashMap();
            try {
                for ( int i=0; i<nodes.length; i++ ) { // TODO: support multiple nodes.
                    Node node= nodes[i];
                    try {
                        // this will crash is nodes.length>1.  We need subtask monitor
                        ProgressHandle monitor= ProgressHandleFactory.createHandle( "IDL Path Scan" + (nodes.length>1?"("+i+")":"") );
                        scanFolder( (DataObject)nodes[i].getCookie( DataObject.class ), monitor );
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                if ( nodes.length>0 )  needToScan= false;
            } finally {
                scanning= false;
                cancel= false;
            }
            
            // go ahead and do a second pass scan if it hasn't been done already.
            if ( lastUserRoutines==null ) {
                scan();
            }
            
        }
    }
    
    public Task scan() {
        synchronized (this) {
            if ( scanning ) return null;
            scanning= true;
        }
        return RequestProcessor.getDefault().post( new ScanTask() );
    }
    
    /**
     * @returns the corresponding Procedure or null.
     */
    public Procedure getProcedure( String name ) {
        if ( scanning ) {
            if ( lastUserRoutines==null ) {
                return null;
            } else {
                return (Procedure) lastUserRoutines.get( name.toUpperCase()  );
            }
        } else {
            return (Procedure) userRoutines.get( name.toUpperCase()  );
        }
    }
    
    public TokenID matchKeyword( char[] buffer, int offset, int len) {
        String target= new String( buffer, offset, len ).toUpperCase();
        HashMap use= scanning ? lastUserRoutines : userRoutines;
        if ( use!=null && use.containsKey(target) ) {
            return PvwaveTokenContext.USERFUNC;
        }
        return null;
    }
    
    public List getMatching( String startsWith , boolean pros, boolean funcs) {
        if ( needToScan ) {
            scan();
            return Collections.singletonList( scanningCompletionItem );
        }
        HashMap use= scanning ? lastUserRoutines : userRoutines;
        int offset= startsWith.length();
        List result= new ArrayList();
        startsWith= startsWith.toUpperCase();
        for ( Iterator i= use.keySet().iterator(); i.hasNext(); ) {
            String kw= (String)i.next();
            if ( kw.startsWith(startsWith) ) {
                Procedure p= (Procedure) use.get(kw);
                if ( p.isFunction() ) {
                    if ( funcs ) {
                        CompletionItem item= new UserCompletionItem( kw, offset, (Procedure)use.get(kw) );
                        result.add(item);
                    }
                } else {
                    if ( pros ) {
                        CompletionItem item= new UserCompletionItem( kw, offset, (Procedure)use.get(kw) );
                        result.add(item);
                    }
                }
            }
        }
        return result;
    }
    
    public String toString() {
        return "UserRoutinesDataBase: scanning="+scanning+" needToScan="+needToScan;
    }
    
    public void rescan() {
        Runnable run= new Runnable() {
            public void run() {
                if ( scanning ) {
                    cancel= true;
                    while ( scanning ) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                scan();
            }
        };
        new Thread( run, "rescanRequest" ).start();
    }
    
    private OutputListener getOutputListener( final Procedure p, final int lineNum ) {
        return new OutputListener() {
            public void outputLineSelected(OutputEvent ev) {
            }
            
            public void outputLineAction(OutputEvent ev) {
                FileObject fo= p.getSourceFile().getFileObject();
                DataObject dataObject;
                try {
                    dataObject = DataObject.find(fo);
                    LineCookie cookie= (LineCookie) dataObject.getCookie( LineCookie.class );
                    Line theLine= cookie.getLineSet().getCurrent(lineNum);
                    theLine.show(Line.SHOW_GOTO);
                } catch (DataObjectNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
            
            public void outputLineCleared(OutputEvent ev) {
            }
            
        };
    }
    
    /**
     * show most used functions, etc.
     */
    public void showUsagesSummary() throws IOException {
        InputOutput inOut= IOProvider.getDefault().getIO("User Routine Greatest Hits",false);
        inOut.select();
        OutputWriter out= inOut.getOut();
        out.reset();
        
        HashMap use= scanning ? lastUserRoutines : userRoutines;
        
        HashMap useCount= new HashMap();
        
        for ( Iterator i=use.values().iterator(); i.hasNext(); ) {
            Procedure p= (Procedure) i.next();
            Map usages= p.getAllUsages();
            for ( Iterator j= usages.keySet().iterator(); j.hasNext(); ) {
                String ause= (String) j.next();
                
                Integer c= (Integer) useCount.get( ause );
                if ( c==null ) {
                    useCount.put( ause, Integer.valueOf(1) );
                } else {
                    useCount.put( ause, Integer.valueOf(c.intValue()+1) );
                }
            }
        }
        
        List entries= new ArrayList( useCount.entrySet() );
        Collections.sort( entries, new Comparator() {
            public int compare(Object o1, Object o2) {
                Integer c1= (Integer) ((Entry)o1).getValue();
                Integer c2= (Integer) ((Entry)o2).getValue();
                return c1.compareTo(c2);
            }
        } );
        
        out.println( "Top 200 used routines: ");
        for ( int i=0; i<200; i++ ) {
            Entry e= (Entry) entries.get(entries.size()-i-1);
            Integer c1= (Integer) e.getValue();
            String s1= (String)e.getKey();
            Procedure p=  (Procedure) use.get(e.getKey()  );
            out.println( "  "+(i+1)+". "+e, p==null ? null : getOutputListener(p,p.getLineNum() ) );
        }
        
    }
    
    public void showUsages( String keyword, OutputWriter out, FileObject exclude ) throws IOException {
        if ( lastUserRoutines==null ) {
            out.print(" two passes of the IDL path must be performed before usages can be calculated, ");
            final String kw= keyword;
            out.println( "  second pass scan has not been performed, " );
            out.println( "  try again", new OutputListener() {
                public void outputLineSelected(OutputEvent outputEvent) {      }
                public void outputLineAction(OutputEvent outputEvent) {  showUsages( kw );    }
                public void outputLineCleared(OutputEvent outputEvent) {   }
            } );
            out.println("  shortly." );
            rescan();        
        }
        if ( lastUserRoutines.size()==0 ) {
            final String kw= keyword;
            out.println( "  second pass scan has not been performed, " );
            out.println( "  try again", new OutputListener() {
                public void outputLineSelected(OutputEvent outputEvent) { }
                public void outputLineAction(OutputEvent outputEvent) { showUsages( kw );  }
                public void outputLineCleared(OutputEvent outputEvent) {  }
            } );
            out.println("  shortly." );
            rescan();
        }
        
        keyword= keyword.toUpperCase();
        HashMap use= scanning ? lastUserRoutines : userRoutines;
        for ( Iterator i=use.entrySet().iterator(); i.hasNext(); ) {
            Entry e= (Entry) i.next();
            Procedure p= (Procedure) e.getValue();
            int[] usages= p.getUsages(keyword);
            if ( usages!=null ) {
                for ( int j=0; j<usages.length;j++ ) {
                    FileObject fo= p.getSourceFile().getFileObject();
                    if ( exclude==null || !exclude.equals(fo) ) {
                        out.println(""+p.getName()+" \t"+fo.getPath()+":"+usages[j], getOutputListener(p,usages[j]) );
                    }
                }
            }
        }
        
    }
    
    public void showUsages( String keyword ) {
        try {
            InputOutput inOut= IOProvider.getDefault().getIO("Usages:"+keyword,false);
            inOut.select();
            OutputWriter out= inOut.getOut();
            out.reset();
            out.println("Usages of "+keyword+":");
            showUsages( keyword, out, null );
            
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }
    }

    /**
     * Holds value of property root.
     */
    private Node root;

    /**
     * Utility field used by bound properties.
     */
    private java.beans.PropertyChangeSupport propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);

    /**
     * Adds a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    /**
     * Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    /**
     * Getter for property root.
     * @return Value of property root.
     */
    public Node getRoot() {
        return this.root;
    }

    /**
     * set the root for the scanning.  This root node must have Children that
     * have DataObjects.
     */
    public void setRoot(Node root) {
        Node oldRoot = this.root;
        this.root = root;
        propertyChangeSupport.firePropertyChange ("root", oldRoot, root);
    }
}
