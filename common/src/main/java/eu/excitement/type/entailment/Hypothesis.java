

/* First created by JCasGen Fri Oct 05 20:16:50 CEST 2012 */
package eu.excitement.type.entailment;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** This type represents a hypothesis part of a T-H pair. This type annotates a hypoth-
esis item within the HypothesisView. It can occur multiple times (for multi-hypothesis problems)
 * Updated by JCasGen Fri Oct 05 20:16:50 CEST 2012
 * XML source: /Users/tailblues/progs/github/Excitement-Open-Platform/common/src/main/resources/desc/type/EntailmentTypes.xml
 * @generated */
public class Hypothesis extends Annotation {
  /** @generated
   * @ordered 
   */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Hypothesis.class);
  /** @generated
   * @ordered 
   */
  //@SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Hypothesis() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Hypothesis(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Hypothesis(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Hypothesis(JCas jcas, int begin, int end) {
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

    