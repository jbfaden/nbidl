/*
 * PvwaveDocumentationItem.java
 *
 * Created on July 31, 2006, 7:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.completion;

import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.options.PvwaveSettingUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.Action;
import org.netbeans.spi.editor.completion.CompletionDocumentation;

/**
 *
 * @author jbf
 */
public class PvwaveDocumentationItem implements CompletionDocumentation {
    
    Procedure procedure;
    
    /** Creates a new instance of PvwaveCompletionItem */
    public PvwaveDocumentationItem( Procedure p ) {
        this.procedure= p;
    }
    
    public static File getDocRoot() {
        File pvwaveHome= new File( PvwaveSettingUtil.getDefault().retrieveSetting().getIDLHome() );
        File DOC_ROOT= new File( pvwaveHome, "help/online_help" );
        return DOC_ROOT;
    }
    
    public String getText() {
        String text= procedure.getDocumentationString();
        
        StringBuffer sbuf= new StringBuffer( text );
        
        // copy syntax up to the top
        int iInsert= sbuf.indexOf("<table");
        int iSyntax= sbuf.indexOf("Syntax");
        iSyntax= sbuf.indexOf("Syntax", iSyntax+1 );
        iSyntax= sbuf.indexOf( "</h3>", iSyntax );
        iSyntax= sbuf.indexOf( "</a>", iSyntax );
        iSyntax+=4;
        int iSyntax2= sbuf.indexOf("</p>", iSyntax );
        
        if ( iSyntax < iSyntax2 && iSyntax>0 ) {
            String syntaxStr= sbuf.substring(iSyntax,iSyntax2+4);
            syntaxStr+= "<br><br>";
            sbuf.insert( iInsert, syntaxStr );
        }
        
        String doc= sbuf.toString();
        
        String noDoc= "<html>No documentation for "+text+"<br>"+
                "in "+getDocRoot()+"</html>";
        return doc==null ? noDoc : doc;
    }
    
    /**
     * @throws RuntimeException for MalformedURLException.
     */
    public URL getURL() {
        try {
            if ( procedure.getDocumentationLink()!=null ) {
                return new File( getDocRoot(), procedure.getDocumentationLink() ).toURL();
            } else {
                return null;
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public CompletionDocumentation resolveLink(String link) {
        return null;
    }
    
    public Action getGotoSourceAction() {
        return null;
    }
    
}
