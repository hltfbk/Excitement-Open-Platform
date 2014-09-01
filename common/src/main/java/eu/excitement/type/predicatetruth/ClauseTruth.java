

/* First created by JCasGen Fri Oct 05 20:17:26 CEST 2012 */
package eu.excitement.type.predicatetruth;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Jul 15 10:01:13 IDT 2014
 * XML source: C:/Users/user/fromHP/Shared/excitement workspace/eop/common/src/main/resources/desc/type/PredicateTruth.xml
 * @generated */
public class ClauseTruth extends Annotation {
  /** @generated
   * @ordered 
   */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ClauseTruth.class);
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
  protected ClauseTruth() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ClauseTruth(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ClauseTruth(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ClauseTruth(JCas jcas, int begin, int end) {
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
     
 
    
  //*--------------*
  //* Feature: clauseTokens

  /** getter for clauseTokens - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getClauseTokens() {
    if (ClauseTruth_Type.featOkTst && ((ClauseTruth_Type)jcasType).casFeat_clauseTokens == null)
      jcasType.jcas.throwFeatMissing("clauseTokens", "eu.excitement.type.predicatetruth.ClauseTruth");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ClauseTruth_Type)jcasType).casFeatCode_clauseTokens)));}
    
  /** setter for clauseTokens - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setClauseTokens(FSArray v) {
    if (ClauseTruth_Type.featOkTst && ((ClauseTruth_Type)jcasType).casFeat_clauseTokens == null)
      jcasType.jcas.throwFeatMissing("clauseTokens", "eu.excitement.type.predicatetruth.ClauseTruth");
    jcasType.ll_cas.ll_setRefValue(addr, ((ClauseTruth_Type)jcasType).casFeatCode_clauseTokens, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for clauseTokens - gets an indexed value - This is an array that can hold one or
more tokens. Representing the tokens which comprise this clause.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public Annotation getClauseTokens(int i) {
    if (ClauseTruth_Type.featOkTst && ((ClauseTruth_Type)jcasType).casFeat_clauseTokens == null)
      jcasType.jcas.throwFeatMissing("clauseTokens", "eu.excitement.type.predicatetruth.ClauseTruth");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ClauseTruth_Type)jcasType).casFeatCode_clauseTokens), i);
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ClauseTruth_Type)jcasType).casFeatCode_clauseTokens), i)));}

  /** indexed setter for clauseTokens - sets an indexed value - This is an array that can hold one or
more tokens. Representing the tokens which comprise this clause.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setClauseTokens(int i, Annotation v) { 
    if (ClauseTruth_Type.featOkTst && ((ClauseTruth_Type)jcasType).casFeat_clauseTokens == null)
      jcasType.jcas.throwFeatMissing("clauseTokens", "eu.excitement.type.predicatetruth.ClauseTruth");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ClauseTruth_Type)jcasType).casFeatCode_clauseTokens), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ClauseTruth_Type)jcasType).casFeatCode_clauseTokens), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    