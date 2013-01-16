

/* First created by JCasGen Fri Oct 05 20:17:26 CEST 2012 */
package eu.excitement.type.predicatetruth;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** This type represents a predicate truth value annotation.
 * Updated by JCasGen Fri Oct 05 20:17:26 CEST 2012
 * XML source: /Users/tailblues/progs/github/Excitement-Open-Platform/common/src/main/resources/desc/type/PredicateTruth.xml
 * @generated */
public class PredicateTruth extends Annotation {
  /** @generated
   * @ordered 
   */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(PredicateTruth.class);
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
  protected PredicateTruth() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public PredicateTruth(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public PredicateTruth(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public PredicateTruth(JCas jcas, int begin, int end) {
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
     
 
    
  //*--------------*
  //* Feature: value

  /** getter for value - gets This represents the value of the annotation.
   * @generated */
  public String getValue() {
    if (PredicateTruth_Type.featOkTst && ((PredicateTruth_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "eu.excitement.type.predicatetruth.PredicateTruth");
    return jcasType.ll_cas.ll_getStringValue(addr, ((PredicateTruth_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets This represents the value of the annotation. 
   * @generated */
  public void setValue(String v) {
    if (PredicateTruth_Type.featOkTst && ((PredicateTruth_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "eu.excitement.type.predicatetruth.PredicateTruth");
    jcasType.ll_cas.ll_setStringValue(addr, ((PredicateTruth_Type)jcasType).casFeatCode_value, v);}    
  }

    