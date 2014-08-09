

/* First created by JCasGen Mon Jul 14 22:27:25 IDT 2014 */
package eu.excitement.type.predicatetruth;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Mon Jul 14 22:27:25 IDT 2014
 * XML source: C:/Users/user/fromHP/Shared/excitement workspace/eop/common/src/main/resources/desc/type/ClauseTruth.xml
 * @generated */
public class ClauseTruthUnknown extends ClauseTruth {
  /** @generated
   * @ordered 
   */
  // @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ClauseTruthUnknown.class);
  /** @generated
   * @ordered 
   */
  // @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ClauseTruthUnknown() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ClauseTruthUnknown(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ClauseTruthUnknown(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ClauseTruthUnknown(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
}

    