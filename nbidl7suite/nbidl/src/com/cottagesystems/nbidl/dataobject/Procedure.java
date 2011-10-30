package com.cottagesystems.nbidl.dataobject;

import com.cottagesystems.nbidl.completion.CompletionSupport;
import com.cottagesystems.nbidl.options.PvwaveSettingUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 * data model of an IDL procedure or function.
 */
public class Procedure {
    private String name;
    private ProceduresFile sourceFile;
    private int fileOffset;
    private int fileLength;
    private boolean isFunction;
    private int lineNum;
    
    /**
     * int[2], db[0]= offset.  db[1]=length
     */
    private int[] documentationBounds;
    
    /**
     * user-defined procedure
     */
    private boolean userDefined;
    
    ArrayList parameters = new ArrayList();
    ArrayList keywords = new ArrayList();
    
    HashMap<String,int[]> usages= new HashMap<String,int[]>();
    
    private String signature;
    
    public Procedure(String name, ProceduresFile sourceFile, int lineNum, int fileOffset, int length, boolean isFunction ) {
        this.name= name;
        this.sourceFile= sourceFile;
        this.lineNum= lineNum;
        this.fileOffset= fileOffset;
        this.fileLength= length;
        this.isFunction= isFunction;
        this.documentationBounds= null;
        this.userDefined= true;
        this.keywords= new ArrayList();
        this.parameters= new ArrayList();
    }
    
    public static Procedure newInternalProcedure( String name, boolean isFunction ) {
        Procedure result= new Procedure( name, null, 0, 0, 0, isFunction );
        result.userDefined= false;
        return result;
    }
    
    public String getName() { return this.name; }
    public String toString() { return "" + ( isFunction ? "function " : "pro " ) + this.name; }
    
    public int getOffset() {
        return this.fileOffset;
    }
    
    public ProceduresFile getSourceFile() {
        return this.sourceFile;
    }
    
    /**
     * returns an html stream of documentation, either htmlified from the documentation block
     * or discovered from a database of html files.
     */
    public String getDocumentationString() {
        if ( isUserDefined() ) {
            return getDocumentation();
        } else {
            File docHome= new File( PvwaveSettingUtil.getDefault().retrieveSetting().getIDLHome() );
            if ( docHome==null || !docHome.exists() ) {
                String resp= "no documentation found for "+this.getName();
                if ( docHome!=null ) {
                    resp+=" in "+docHome;
                }
                resp+=".";
                return resp;
            } else {
                File pvwaveHome= new File( PvwaveSettingUtil.getDefault().retrieveSetting().getIDLHome() );
                File DOC_ROOT= new File( pvwaveHome, "help/online_help" );
                File docFile= new File( DOC_ROOT, documentationLink );
                StringBuffer sbuf= new StringBuffer( (int)docFile.length() );
                BufferedInputStream in=null;
                try {
                    in = new BufferedInputStream(new FileInputStream(docFile));
                    byte[] buf= new byte[2048];
                    int bytesRead= in.read( buf );
                    
                    while ( bytesRead!=-1 ) {
                        sbuf.append( new String( buf ) );
                        bytesRead= in.read( buf );
                    }
                    in.close();
                    
                    return sbuf.toString();
                    
                } catch (IOException ex) {
                    String resp= "no documentation found for "+this.getName();
                    if ( docHome!=null ) {
                        resp+=" in "+docHome;
                    }
                    resp+=".";
                    return resp;
                }
                
            }
        }
    }
    
    public String getDocumentation() {
        String doc;
        InputStream ins=null;
        try {
            if ( documentationBounds!=null ) {
                DataObject dataObject = DataObject.find(sourceFile.getFileObject());
                FileObject fo= dataObject.getPrimaryFile();
                
                ins= fo.getInputStream();
                int byteSkipped= 0;
                
                ins.skip( documentationBounds[0] ); // TODO: check read lengths returned
                // TODO: check read lengths returned
                byte[] buf= new byte[ documentationBounds[1] ];
                ins.read(buf);
                ins.close();
                doc= CompletionSupport.htmlifyDocumentation( new String( buf ) );
                
//                String doc= document.getText( documentationBounds[0], documentationBounds[1] );
//                return htmlifyDocumentation( doc );
            } else {
                doc= "<i>documentation not provided for "+name+"</i>";
            }
        } catch ( DataObjectNotFoundException e ) {
            doc= "<i>unable to find DataObject for FileObject</i>";
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            doc= ex.getMessage();
        } catch (IOException ex) {
            ex.printStackTrace();
            doc= ex.getMessage();
        }
        
        return doc;
    }
    
    // for internal routines catalog.xml.
    private String documentationLink=null;
    
    public String getDocumentationLink() {
        return documentationLink;
    }
    
    /**
     * sets the location within the documentation database of the html documentation.
     */
    public void setDocumentationLink( String val ) {
        this.documentationLink= val;
    }
    
    public String getSignature() {
        return CompletionSupport.htmlifyDocumentation(signature);
    }
    
    public List getKeywords() {
        return new ArrayList( this.keywords );
    }
    
    public List getParameters() {
        return new ArrayList( this.parameters );
    }
    
    public void addKeyword( String keyword ) {
        keywords.add( keyword );
    }
    
    public int getLineNum() {
        return this.lineNum;
    }
    
    public void markDocumentation(int docStart, int length ) {
        this.documentationBounds= new int[] { docStart, length };
    }
    
    public void setSignature(String s) {
        this.signature= s;
    }
    
    public void addUsage(String name, int i) {
        String key= name.toUpperCase();
        int[] use= usages.get( key );
        if ( use==null ) {
            usages.put( key,new int[] {i} );
        } else {
            // TODO: slow!
            int[] newUse= new int[use.length+1];
            System.arraycopy(use,0,newUse,0,use.length);
            newUse[use.length]=i;
            usages.put( key, newUse );
        }
        
    }
    
    /**
     * return the file offset of each usage.
     * @param name uppercase name of keyword to search for
     */
    public int[] getUsages( String name ) {
        return  usages.get(name );
    }
    
    public boolean isFunction() {
        return this.isFunction;
    }
    
    public void addParameter(String lastTokString) {
        this.parameters.add(lastTokString);
    }
    
    public boolean isUserDefined() {
        return this.userDefined;
    }
    
    /**
     * returns map of all routines this routine uses.
     */
    public Map getAllUsages() {
        return usages;
    }
}
