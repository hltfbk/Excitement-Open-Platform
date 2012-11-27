

/* First created by JCasGen Fri Oct 05 20:16:50 CEST 2012 */
package eu.excitement.type.entailment;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** This type represents a text-hypothesis pair.
 * Updated by JCasGen Fri Oct 05 20:16:50 CEST 2012
 * XML source: /Users/tailblues/progs/github/Excitement-Open-Platform/common/src/main/resources/desc/type/EntailmentTypes.xml
 * @generated */
public class Pair extends TOP {
  /** @generated
   * @ordered 
   */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Pair.class);
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
  protected Pair() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Pair(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Pair(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: pairID

  /** getter for pairID - gets ID of this pair. The main purpose of this value is to distinguish
a certain pair among multiple pairs.
   * @generated */
  public String getPairID() {
    if (Pair_Type.featOkTst && ((Pair_Type)jcasType).casFeat_pairID == null)
      jcasType.jcas.throwFeatMissing("pairID", "eu.excitement.type.entailment.Pair");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Pair_Type)jcasType).casFeatCode_pairID);}
    
  /** setter for pairID - sets ID of this pair. The main purpose of this value is to distinguish
a certain pair among multiple pairs. 
   * @generated */
  public void setPairID(String v) {
    if (Pair_Type.featOkTst && ((Pair_Type)jcasType).casFeat_pairID == null)
      jcasType.jcas.throwFeatMissing("pairID", "eu.excitement.type.entailment.Pair");
    jcasType.ll_cas.ll_setStringValue(addr, ((Pair_Type)jcasType).casFeatCode_pairID, v);}    
   
    
  //*--------------*
  //* Feature: text

  /** getter for text - gets This feature points a Text instance, which rep-
resents the text part of this pair.
   * @generated */
  public Text getText() {
    if (Pair_Type.featOkTst && ((Pair_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "eu.excitement.type.entailment.Pair");
    return (Text)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Pair_Type)jcasType).casFeatCode_text)));}
    
  /** setter for text - sets This feature points a Text instance, which rep-
resents the text part of this pair. 
   * @generated */
  public void setText(Text v) {
    if (Pair_Type.featOkTst && ((Pair_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "eu.excitement.type.entailment.Pair");
    jcasType.ll_cas.ll_setRefValue(addr, ((Pair_Type)jcasType).casFeatCode_text, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: hypothesis

  /** getter for hypothesis - gets This feature points a Hypothesis instance, which represents the hypothesis part of this pair.
   * @generated */
  public Hypothesis getHypothesis() {
    if (Pair_Type.featOkTst && ((Pair_Type)jcasType).casFeat_hypothesis == null)
      jcasType.jcas.throwFeatMissing("hypothesis", "eu.excitement.type.entailment.Pair");
    return (Hypothesis)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Pair_Type)jcasType).casFeatCode_hypothesis)));}
    
  /** setter for hypothesis - sets This feature points a Hypothesis instance, which represents the hypothesis part of this pair. 
   * @generated */
  public void setHypothesis(Hypothesis v) {
    if (Pair_Type.featOkTst && ((Pair_Type)jcasType).casFeat_hypothesis == null)
      jcasType.jcas.throwFeatMissing("hypothesis", "eu.excitement.type.entailment.Pair");
    jcasType.ll_cas.ll_setRefValue(addr, ((Pair_Type)jcasType).casFeatCode_hypothesis, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: goldAnswer

  /** getter for goldAnswer - gets This features records the gold standard answer for this pair. If the pair (and CAS) represents a training data, this value will be filled in with the gold standard answer. If it is a null value, the pair represents a entailment problem that is yet
to be answered.
   * @generated */
  public String getGoldAnswer() {
    if (Pair_Type.featOkTst && ((Pair_Type)jcasType).casFeat_goldAnswer == null)
      jcasType.jcas.throwFeatMissing("goldAnswer", "eu.excitement.type.entailment.Pair");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Pair_Type)jcasType).casFeatCode_goldAnswer);}
    
  /** setter for goldAnswer - sets This features records the gold standard answer for this pair. If the pair (and CAS) represents a training data, this value will be filled in with the gold standard answer. If it is a null value, the pair represents a entailment problem that is yet
to be answered. 
   * @generated */
  public void setGoldAnswer(String v) {
    if (Pair_Type.featOkTst && ((Pair_Type)jcasType).casFeat_goldAnswer == null)
      jcasType.jcas.throwFeatMissing("goldAnswer", "eu.excitement.type.entailment.Pair");
    jcasType.ll_cas.ll_setStringValue(addr, ((Pair_Type)jcasType).casFeatCode_goldAnswer, v);}    
  }

    