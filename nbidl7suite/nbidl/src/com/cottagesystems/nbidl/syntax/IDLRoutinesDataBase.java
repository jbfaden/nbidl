/*
 * IDLKeywords.java
 *
 * Created on April 13, 2006, 1:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.syntax;

import com.cottagesystems.nbidl.completion.PvwaveCompletionItem;
import com.cottagesystems.nbidl.dataobject.Procedure;
import com.cottagesystems.nbidl.model.IdlClass;
import com.cottagesystems.nbidl.model.IdlProperty;
import com.cottagesystems.nbidl.model.IdlSession;
import com.cottagesystems.nbidl.options.PvwaveSettingUtil;
import com.cottagesystems.nbidl.session.SessionSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.editor.TokenID;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Jeremy
 */
public class IDLRoutinesDataBase {
    Set keywords;
    
    IdlSession idlSession;
    
    private static IDLRoutinesDataBase instance= null;
    private static Logger logger= Logger.getLogger("pvwave");
    
    /** Creates a new instance of IDLKeywords */
    public IDLRoutinesDataBase() {
        try {
            //readKeywords();
            idlSession= SessionSupport.getIdlSessionInstance();
            readIdlCatalog();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        } catch (SAXException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void rescan() {
        try {
            readIdlCatalog();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }
    }
    
    private void readIdlCatalog() throws ParserConfigurationException, IOException, SAXException {
        Logger logger= Logger.getLogger("");
        
        File pvwaveHome= new File( PvwaveSettingUtil.getDefault().retrieveSetting().getIDLHome() );
        File DOC_ROOT= new File( pvwaveHome, "help/online_help" );
        File idlCatalog= new File( DOC_ROOT, "idl_catalog.xml" );
        
        InputStream cat;
        
        if ( idlCatalog.exists() ) {
            cat= new FileInputStream( idlCatalog );
            logger.info("Reading file "+idlCatalog+"...");
        } else {
            URL keywordsListURL= getClass().getResource("idl_catalog.xml");
            logger.info("Reading file "+keywordsListURL+"...");
            cat= keywordsListURL.openStream();
        }
        
        ProgressHandle monitor= ProgressHandleFactory.createHandle( "IDL Routines Scan" );
        monitor.start();
        
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse( cat );
        
        doc.getDocumentElement().normalize();
        
        NodeList list = doc.getElementsByTagName("ROUTINE");
        
        procedures= new HashMap(list.getLength());
        for ( int i=0; i<list.getLength(); i++ ) {
            Element n= (Element)list.item(i);
            Element syntax= (Element)n.getElementsByTagName("SYNTAX").item(0);
            if ( syntax==null ) continue;
            String type=  syntax.getAttribute("type");
            boolean isFunc= "func".equals( type );
            if ( !isFunc && !"pro".equals( type ) ) continue;
            String signature= syntax.getAttribute("name");
            String name= n.getAttribute("name");
            if ( name.equals("PRO") ) continue;
            if ( name.equals("FUNCTION") ) continue;
            String[] names= n.getAttribute("name").split("/");
            for ( int j=0; j<names.length; j++ ) {
                Procedure p= Procedure.newInternalProcedure( names[j], isFunc );
                //System.err.println(p.getName());
                p.setSignature( signature );
                String link= n.getAttribute("link");
                p.setDocumentationLink(link);
                NodeList keywords= n.getElementsByTagName("KEYWORD");
                for ( int k=0; k<keywords.getLength(); k++ ) {
                    String kw= ((Element)keywords.item(k)).getAttribute("name").toLowerCase();
                    p.addKeyword(kw);
                }
                // TODO: grab parameters, they are right there...
                
                procedures.put( p.getName(), p );
            }
        }
        
        list = doc.getElementsByTagName("CLASS");
        
        for ( int i=0; i<list.getLength(); i++ ) {
            Element n= (Element)list.item(i);
            String name= n.getAttribute("name");
            
            IdlClass clas= idlSession.newClass( name );
            Element superclass= (Element)n.getElementsByTagName("SUPERCLASS").item(0);
            
            if ( superclass!=null ) {
                String sname= superclass.getAttribute("name" );
                if ( !sname.equals("None") ) {
                    IdlClass parentClass= idlSession.newClass( sname );
                    clas.setParentClass( parentClass );
                }
            }
            
            NodeList methods= n.getElementsByTagName("METHOD");
            for ( int jj=0; jj<methods.getLength(); jj++ ) {
                Element nn= (Element)methods.item(jj);
                Element syntax= (Element)nn.getElementsByTagName("SYNTAX").item(0);
                if ( syntax==null ) continue;
                String type=  syntax.getAttribute("type");
                boolean isFunc= "func".equals( type );
                if ( !isFunc && !"pro".equals( type ) ) continue;
                String signature= syntax.getAttribute("name");
                String pname= nn.getAttribute("name");
                if ( pname.equals("PRO") ) continue;
                if ( pname.equals("FUNCTION") ) continue;
                String[] names= pname.split("/");
                for ( int j=0; j<names.length; j++ ) {
                    Procedure p= Procedure.newInternalProcedure( names[j], isFunc );
                    //System.err.println(p.getName());
                    p.setSignature( signature );
                    String link= nn.getAttribute("link");
                    p.setDocumentationLink(link);
                    NodeList keywords= nn.getElementsByTagName("KEYWORD");
                    for ( int k=0; k<keywords.getLength(); k++ ) {
                        String kw= ((Element)keywords.item(k)).getAttribute("name").toLowerCase();
                        p.addKeyword(kw);
                    }
                    // TODO: grab parameters, they are right there...
                    
                    clas.addMethod(p);
                    
                }
            }
            
            NodeList nodes= n.getElementsByTagName("PROPERTY");
            for ( int jj=0; jj<nodes.getLength(); jj++ ) {
                Element nn= (Element)nodes.item(jj);
                String nname=  nn.getAttribute("name");
                String link= nn.getAttribute("link");
                IdlProperty prop= new IdlProperty(nname);
                prop.setLink(link);
                clas.addProperty( prop );
            }
            
        }
        
        // remove the statements.
        list = doc.getElementsByTagName("STATEMENT");
        
        for ( int i=0; i<list.getLength(); i++ ) {
            Element n= (Element)list.item(i);
            String name= n.getAttribute("name");
            if ( procedures.get(name)!=null ) {
                procedures.remove(name);
            }
        }
        
        logger.info("done.");
        monitor.finish();
        
    }
    
    
    public static IDLRoutinesDataBase getInstance() {
        if ( instance==null ) {
            instance= new IDLRoutinesDataBase();
        }
        return instance;
    }
    
    HashMap procedures;
    
    
    public Procedure getProcedure( String name ) {
        Procedure result= (Procedure) procedures.get(name.toUpperCase());
        //if ( result==null ) {
     /*   if ( true ) {
            result= Procedure.newInternalProcedure( name, true );
            try {
                File f= PvwaveDocumentationItem.getFileForName(name);
                ModelSupport.parseIDLDocumentationItem( f, result );
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            procedures.put( name, result );
        } */
        return result;
    }
    
    public IdlSession getSession() {
        return idlSession;
    }
    
    protected TokenID matchKeyword( char[] buffer, int offset, int len) {
        String target= new String( buffer, offset, len ).toUpperCase();
        if ( procedures.containsKey(target) ) return PvwaveTokenContext.PVFUNC;
        return null;
    }
    
    /**
     * returns a set of PvwaveCompletionItems
     */
    public List getMatching( String startsWith, boolean pros, boolean funcs ) {
        List result= new ArrayList();
        startsWith= startsWith.toUpperCase();
        int offset= startsWith.length();
        for ( Iterator i= procedures.keySet().iterator(); i.hasNext(); ) {
            String kw= (String)i.next();
            if ( kw.startsWith(startsWith) ) {
                Procedure p= (Procedure) procedures.get(kw);
                if ( p.isFunction() ) {
                    if ( funcs ) {
                        PvwaveCompletionItem item= new PvwaveCompletionItem( kw.toLowerCase(), offset );
                        result.add(item);
                    }
                } else {
                    if ( pros ) {
                        PvwaveCompletionItem item= new PvwaveCompletionItem( kw.toLowerCase(), offset );
                        result.add(item);
                    }
                }
            }
        }
        return result;
    }
}
