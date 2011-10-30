package com.cottagesystems.nbidl.actions;

import com.cottagesystems.nbidl.syntax.PvwaveEditorKit;
import com.cottagesystems.nbidl.syntax.PvwaveSyntax;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.TokenProcessor;
import org.netbeans.editor.Utilities;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class DumpSyntax extends CookieAction {
    
    private String padString( String s, int len ) {
        StringBuffer result=new StringBuffer(s);
        while( result.length() < len ) result.append(" ");
        return result.toString();
    }
    
    protected void performAction(Node[] activatedNodes) {
        DataObject c = (DataObject) activatedNodes[0].getCookie(DataObject.class);
        EditorCookie e= (EditorCookie) c.getCookie( EditorCookie.class );
        JEditorPane pane= e.getOpenedPanes()[0];
        PvwaveEditorKit pek= (PvwaveEditorKit) pane.getEditorKit();
        final BaseDocument doc= (BaseDocument) pane.getDocument();
        
        int i0, i1;
        try {
            i0= Utilities.getRowStart( doc, pane.getSelectionStart() );
            i1= Utilities.getRowEnd( doc, pane.getSelectionEnd() );
            
            final int tokOffset= i0;
            
            int len= pane.getSelectionEnd() - pane.getSelectionStart();
            System.err.println( "selection= [" + pane.getSelectionStart() + "," + pane.getSelectionEnd() +"] ("+len+" chars)" );
            System.err.println( "i0= "+i0 );
            SyntaxSupport support= new SyntaxSupport( doc );
            
            TokenProcessor myProcessor= new TokenProcessor() {
                int bufferOffset;
                int bufferStartPos;
                
                public boolean token(TokenID tok, TokenContextPath tokenContextPath, int i, int i0) {
                    try {
                        String text;
                        int shift= -1 * bufferOffset + bufferStartPos;
                        text = doc.getText( i+shift, i0 );
                        System.err.println( padString( tok.getName(), 20 ) + " \"" + text + "\"  ("+(i+shift)+")" );
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                    return true;
                }
                
                public int eot(int i) {
                    return 0;
                }
                
                public void nextBuffer(char[] c, int offset, int len, int startPos, int preScan, boolean lastBuffer ) {
                    System.err.println( "nextBuffer: \n offset="+offset+"\n startPos="+startPos );
                    this.bufferOffset= offset;
                    this.bufferStartPos= startPos;
                }
                
            };
            
            support.tokenizeText( myProcessor, i0, i1, true );
        } catch ( BadLocationException ex ) {
            ex.printStackTrace();
            return;
        }
        
        
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(DumpSyntax.class, "CTL_DumpSyntax");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            DataObject.class
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

