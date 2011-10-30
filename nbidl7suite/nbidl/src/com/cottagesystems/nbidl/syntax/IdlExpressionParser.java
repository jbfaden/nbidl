/*
 * IdlExpressionParser.java
 *
 * Created on May 21, 2007, 10:46:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.nbidl.syntax;

import com.cottagesystems.nbidl.model.IdlValue;
import com.cottagesystems.nbidl.model.PrimativeType;
import java.util.Stack;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;

/**
 *
 * @author jbf
 */
public class IdlExpressionParser {
    
    int nonWhiteSpaceCount;
    
    public IdlExpressionParser() {
        nonWhiteSpaceCount=0;
    }
    
    private class CompletionContext {
        TokenID contextTok;
        /**
         * the context of the string to complete.  This is the name of a function,
         * an expression, etc, to be interpretted based on contextTok.
         */
        String contextString;
        
        /**
         * the beginning of the keyword
         */
        String completable;
        
        /**
         * a keyword is to be completed.
         */
        boolean inKeywords;
        
        final static String COMPLETION_TYPE_NONE="none";
        final static String COMPLETION_TYPE_KEYWORD="kw";
        final static String COMPLETION_TYPE_STRUCT="tag";
        final static String COMPLETION_TYPE_OBJECT="obj";
        final static String COMPLETION_TYPE_PRO="pro";
        final static String COMPLETION_TYPE_FUNC="func";
        final static String COMPLETION_TYPE_KW_FUNC="kw_func";
        
        String completionType=COMPLETION_TYPE_PRO;
        
        public String toString() {
            return contextString + " "+completionType+" " + String.valueOf(completable);
        }
    }
    
    TokenID lastTok;
    
    public IdlValue nextExpression( char[] buffer, Syntax syntax ) {
        boolean notDone= true;
        
        TokenID tok= null;
        CompletionContext lastCC=null;
        CompletionContext cc= null;
        
        Stack contextStack= new Stack();
        
        int bufferShift= 0;
        
        // offset of start of parsing.
        int start= syntax.getTokenOffset();
        
        while ( notDone ) {
            tok= syntax.nextToken();
            
            int tpos= syntax.getTokenOffset();
            int len= syntax.getTokenLength();
            
            if ( tok.getNumericID()!=PvwaveTokenContext.WHITESPACE_ID ) nonWhiteSpaceCount++;
            
            if ( tok.getNumericID()==PvwaveTokenContext.USERFUNC_ID
                    || tok.getNumericID()==PvwaveTokenContext.PVFUNC_ID ) {
                lastCC= new CompletionContext();
                lastCC.contextString= new String( buffer, tpos+bufferShift, len );
                lastCC.contextTok= tok;
                if ( nonWhiteSpaceCount>1 ) {
                    lastCC.completionType= lastCC.COMPLETION_TYPE_KW_FUNC;
                }
                
            } else if ( tok.getNumericID()==PvwaveTokenContext.IDENTIFIER_ID ) {
                if ( cc.completionType==cc.COMPLETION_TYPE_STRUCT ) {
                    cc.completable= new String( buffer, tpos+bufferShift, len );
                    lastCC= new CompletionContext();
                    lastCC.contextTok= cc.contextTok;
                    lastCC.contextString= cc.contextString + "." + new String( buffer, tpos+bufferShift, len );
                    lastCC.completionType= cc.completionType;
                } else {
                    lastCC= new CompletionContext();
                    lastCC.contextString= new String( buffer, tpos+bufferShift, len );
                    lastCC.contextTok= tok;
                    lastCC.completionType= nonWhiteSpaceCount==1 ? lastCC.COMPLETION_TYPE_PRO : lastCC.COMPLETION_TYPE_KW_FUNC;
                    cc.completable= new String( buffer, tpos+bufferShift, len );
                }
                
            } else if ( tok.getNumericID()==PvwaveTokenContext.LPAREN_ID ) {
                if (lastCC!=null) {
                    contextStack.push(cc);
                    lastCC.completionType= cc.COMPLETION_TYPE_KW_FUNC;
                    cc= lastCC;
                } else { // expression
                    contextStack.push(cc);
                    cc= new CompletionContext();
                    cc.completionType= cc.COMPLETION_TYPE_FUNC;
                }
                lastCC= null;
                
            } else if ( tok.getNumericID()==PvwaveTokenContext.RPAREN_ID ) {
                if ( contextStack.size()>0 ) {
                    cc= (CompletionContext) contextStack.pop();
                }
                lastCC= null;
                
            } else if ( tok.getNumericID()==PvwaveTokenContext.LBRACKET_ID ) {
                contextStack.push(cc);
                cc= new CompletionContext();
                cc.completionType= cc.COMPLETION_TYPE_FUNC;
                lastCC= null;
                
            } else if ( tok.getNumericID()==PvwaveTokenContext.RBRACKET_ID ) {
                if ( contextStack.size()>0 ) cc= (CompletionContext) contextStack.pop();
                lastCC= null;
                
            } else if ( tok.getNumericID()==PvwaveTokenContext.COMMA_ID ) {
                if ( lastCC!=null && cc.completionType==cc.COMPLETION_TYPE_PRO ) {
                    lastCC.completionType= lastCC.COMPLETION_TYPE_KW_FUNC;
                    cc= lastCC;
                } else if ( cc.completionType==cc.COMPLETION_TYPE_STRUCT ) {
                    if ( contextStack.size()>0 ) cc= (CompletionContext) contextStack.pop();
                }
                
            } else if ( tok.getNumericID()==PvwaveTokenContext.DOT_ID ) {
                if ( lastCC!=null ) {
                    if ( cc.completionType!=cc.COMPLETION_TYPE_STRUCT ) contextStack.push(cc);
                    lastCC.completionType= lastCC.COMPLETION_TYPE_STRUCT;
                    cc= lastCC;
                }
                
            } else if ( tok.getNumericID()==PvwaveTokenContext.EQ_ID ) {
                lastCC= null;
                if ( cc.completionType==cc.COMPLETION_TYPE_PRO ) cc.completionType= cc.COMPLETION_TYPE_KW_FUNC;
                
            } else {
                lastCC= null;
            }
            
            System.err.println( " "+(tpos+bufferShift)+" "+tok.getName()+" cc:"+cc+"\t\t  pendingCC:"+lastCC );
            
           /* if ( tpos+bufferShift < pos &&  pos <= tpos+bufferShift + len ) {
                System.err.println(cc);
                functionToken= cc;
                if ( functionToken!=null ) {
                    if ( tok.getNumericID()==PvwaveTokenContext.DOT_ID ) {
                        functionToken.completable= "";
                    } else if ( tok.getNumericID()==PvwaveTokenContext.WHITESPACE_ID ) {
                        functionToken.completable= "";
                    } else if ( tok.getNumericID()==PvwaveTokenContext.DIV_ID ) {
                        functionToken.completable= "";
                    } else if ( tok.getNumericID()==PvwaveTokenContext.LPAREN_ID ) {
                        functionToken.completable= "";
                    } else if ( tok.getNumericID()==PvwaveTokenContext.USERFUNC_ID ) {
                        functionToken= lastCC;
                        functionToken.completable= doc.getText( tpos+bufferShift, pos-(tpos+bufferShift) ).toLowerCase();
                    } else if ( tok.getNumericID()==PvwaveTokenContext.USERFUNC_ID || tok.getNumericID()==PvwaveTokenContext.PVFUNC_ID ) {
                        functionToken= lastCC;
                        functionToken.completable= doc.getText( tpos+bufferShift, pos-(tpos+bufferShift) ).toLowerCase();
                    } else {
                        functionToken.completable= doc.getText( tpos+bufferShift, pos-(tpos+bufferShift) ).toLowerCase();
                    }
            
                    if ( functionToken.completable==null ) functionToken.completable="";
            
                    if ( ( functionToken.completionType==functionToken.COMPLETION_TYPE_KW_FUNC
                            || functionToken.completionType==functionToken.COMPLETION_TYPE_FUNC ) && functionToken.completable.equals("") ) {
                        functionToken.completionType= functionToken.COMPLETION_TYPE_KEYWORD;
                    }
            
                }
            
            } else {
                // a procedure can only be the first token on the line, so don't complete on procedures after here.
                if ( tok.getNumericID()!=PvwaveTokenContext.WHITESPACE_ID && cc.completionType==cc.COMPLETION_TYPE_PRO ) {
                    //cc.completionType= cc.COMPLETION_TYPE_FUNC;
                }
            }
            */
        }
        
        int end= syntax.getTokenOffset();
        
        return new IdlValue( PrimativeType.UNDEFINED, new String( buffer, start, end-start ) );
        
    }
    
    public TokenID getLastToken() {
        return lastTok;
    }
}
