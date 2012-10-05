

/* First created by JCasGen Fri Oct 05 09:56:44 CEST 2012 */
package de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** Adjective
 * Updated by JCasGen Fri Oct 05 20:17:19 CEST 2012
 * XML source: /Users/tailblues/progs/github/Excitement-Open-Platform/common/src/main/resources/desc/type/POS.xml
 * @generated */
public class ADJ extends POS {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ADJ.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ADJ() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ADJ(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ADJ(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ADJ(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
}

    