/*
 * ModelSupport.java
 *
 * Created on March 12, 2007, 10:01 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.syntax;

import com.cottagesystems.nbidl.dataobject.Procedure;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * Static routines for supporting model of IDL session.
 * @author jbf
 */
public class ModelSupport {
    
    private static class MyCallBack extends HTMLEditorKit.ParserCallback {
        String varName;
        String recDim;
        
        Procedure procedure;
        
        String sectionName=null;
        
        int state=0;
        int STATE_SECTION=1;    // inside section tag
        int STATE_KEYWORD=2; // inside keyword
        
        MyCallBack( Procedure procedure ) {
            this.procedure= procedure;
        }
        
        public void handleText(char[] data, int pos) {
            if ( state==STATE_SECTION ) {
                sectionName= new String(data);
            } else if ( state==STATE_KEYWORD ) {
                String kw= new String(data);
                procedure.addKeyword(kw.toLowerCase());
            }
        
        }
        
        
        public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            super.handleStartTag(t, a, pos);
            
            state= 0;
            
            if ( t==HTML.Tag.H3 ) {
                state= STATE_SECTION;
                
            } else if ( t==HTML.Tag.H4 ) {
                if ( sectionName.equals("Keywords") ) {
                    state= STATE_KEYWORD;
                }
                
            } else {
                varName=null;
            }
        }
        
        public void handleEndTag(HTML.Tag t, int pos) {
            super.handleEndTag(t, pos);
            state= 0;
        }
        
        
    }

    public static void parseIDLDocumentationItem( File f, Procedure p ) throws FileNotFoundException, IOException {
        InputStream in= new FileInputStream( f );
        
        new ParserDelegator().parse( new InputStreamReader(in), new MyCallBack( p ), true );
        in.close();
    }
    
}
