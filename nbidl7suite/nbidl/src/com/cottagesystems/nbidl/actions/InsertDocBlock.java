package com.cottagesystems.nbidl.actions;



import com.cottagesystems.nbidl.dataobject.Procedure;

import com.cottagesystems.nbidl.syntax.UserRoutinesDataBase;
import com.cottagesystems.nbidl.util.GoToSupport;

import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;

import java.io.Reader;

import java.util.List;

import javax.swing.JEditorPane;

import javax.swing.text.BadLocationException;

import org.netbeans.editor.BaseDocument;

import org.openide.cookies.EditorCookie;

import org.openide.nodes.Node;

import org.openide.util.HelpCtx;

import org.openide.util.NbBundle;

import org.netbeans.editor.Utilities;

import org.openide.util.actions.CookieAction;



public final class InsertDocBlock extends CookieAction {

    

    private static InsertDocBlock INSTANCE;

    

    public static synchronized InsertDocBlock getInstance() {

        if ( INSTANCE==null ) {

            INSTANCE= new InsertDocBlock();

        }

        return INSTANCE;

    }

    

    private StringBuffer doDocBlock( Procedure pro ) throws IOException {

        StringBuffer insertMe= new StringBuffer();

        

        if ( pro==null ) {

            

            Reader in= new InputStreamReader( InsertDocBlock.class.getResourceAsStream( "fragments/documentationBlock.txt" ) );

            BufferedReader reader= new BufferedReader(in);

            String s= reader.readLine();

            while ( s!=null ) {

                insertMe.append(s).append("\n");

                s= reader.readLine();

            }

            reader.close();

        } else {

            insertMe.append( ";*******************************************************************************\n" );

            insertMe.append( ";* NAME: " + pro.getName()+"\n" );

            insertMe.append( ";* DESCRIPTION:  \n" );

            insertMe.append( ";* INPUTS:   \n" );

            List list;

            list= pro.getParameters();

            for ( int i=0; i<list.size(); i++ ) {

                String s= ((String)list.get(i)).trim();

                if ( s.length() > 0 ) {

                    insertMe.append( ";*   "+s.toUpperCase()+", type, description\n" );

                }

            }

            insertMe.append( ";* KEYWORDS:\n" );

            list= pro.getKeywords();

            for ( int i=0; i<list.size(); i++ ) {

               insertMe.append( ";*   "+((String)list.get(i)).toUpperCase()+", type, description\n" );

            }

            if ( pro.isFunction() ) {

                insertMe.append( ";* RETURNS: \n" );

                insertMe.append( ";*   type, description\n" );

            }

            insertMe.append( ";* SIDE EFFECTS:\n" );

            insertMe.append( ";* EXCEPTIONS:\n" );

            insertMe.append( ";* EXAMPLES:\n" );

            insertMe.append( ";* UNIT TEST: \n" );

            insertMe.append( ";* CATEGORY: \n" );

            insertMe.append( ";* CVSTAG: \n" );

            insertMe.append( ";*   $Name:  $\n" );

            insertMe.append( ";*   $Revision: 1.2 $\n" );

            insertMe.append( ";* CURATOR: NAME\n" );

            insertMe.append( ";* HISTORY:  \n" );

            insertMe.append( ";*   DATE, REV, written by NAME\n" );

            insertMe.append( ";*******************************************************************************\n" );

            // code me

            

        }

        

        return insertMe;

    }

    

    protected void performAction(Node[] activatedNodes) {

        try {

            EditorCookie c = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);

            JEditorPane pane= c.getOpenedPanes()[0];

            

            final BaseDocument doc= (BaseDocument) pane.getDocument();

            

            int len= pane.getSelectionEnd() - pane.getSelectionStart();

            System.err.println( "selection= [" + pane.getSelectionStart() + "," + pane.getSelectionEnd() +"] ("+len+" chars)" );

            

            String name= doc.getText( pane.getSelectionStart(), len );

            

            Procedure pro= UserRoutinesDataBase.getInstance( GoToSupport.getFileObject(doc) ).getProcedure(name);

            

            StringBuffer insertMe= doDocBlock(  pro );

            

            int i0, i1;

            

            i0= Utilities.getRowStart( doc, pane.getCaretPosition() );

            doc.insertString( i0, insertMe.toString(), null );

        } catch (BadLocationException ex) {

            ex.printStackTrace();

            

        } catch ( IOException ex ) {

            ex.printStackTrace();

        }

        

        

        

    }

    

    protected int mode() {

        return CookieAction.MODE_EXACTLY_ONE;

    }

    

    public String getName() {

        return NbBundle.getMessage(InsertDocBlock.class, "CTL_InsertDocBlock");

    }

    

    protected Class[] cookieClasses() {

        return new Class[] {

            EditorCookie.class

        };

    }

    

    protected void initialize() {

        super.initialize();

        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details

        putValue("noIconInMenu", Boolean.TRUE);

    }

    

    public HelpCtx getHelpCtx() {

        return HelpCtx.DEFAULT_HELP;

    }

    

    protected boolean asynchronous() {

        return false;

    }

    

}



