

/* First created by JCasGen Sun Feb 16 17:14:26 CET 2014 */
package eu.excitement.type.alignment;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Sun Feb 16 17:14:26 CET 2014
 * XML source: /Users/tailblues/progs/Excitement-Open-Platform/common/src/main/resources/desc/type/TextAlignment.xml
 * @generated */
public class AlignedAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
//  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AlignedAnnotation.class);
  /** @generated
   * @ordered 
   */
//  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected AlignedAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public AlignedAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public AlignedAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public AlignedAnnotation(JCas jcas, int begin, int end) {
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
  //* Feature: alignedTo

  /** getter for alignedTo - gets 
   * @generated */
  public FSArray getAlignedTo() {
    if (AlignedAnnotation_Type.featOkTst && ((AlignedAnnotation_Type)jcasType).casFeat_alignedTo == null)
      jcasType.jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedAnnotation");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_alignedTo)));}
    
  /** setter for alignedTo - sets  
   * @generated */
  public void setAlignedTo(FSArray v) {
    if (AlignedAnnotation_Type.featOkTst && ((AlignedAnnotation_Type)jcasType).casFeat_alignedTo == null)
      jcasType.jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedAnnotation");
    jcasType.ll_cas.ll_setRefValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_alignedTo, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for alignedTo - gets an indexed value - 
   * @generated */
  public AlignedAnnotation getAlignedTo(int i) {
    if (AlignedAnnotation_Type.featOkTst && ((AlignedAnnotation_Type)jcasType).casFeat_alignedTo == null)
      jcasType.jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedAnnotation");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_alignedTo), i);
    return (AlignedAnnotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_alignedTo), i)));}

  /** indexed setter for alignedTo - sets an indexed value - 
   * @generated */
  public void setAlignedTo(int i, AlignedAnnotation v) { 
    if (AlignedAnnotation_Type.featOkTst && ((AlignedAnnotation_Type)jcasType).casFeat_alignedTo == null)
      jcasType.jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedAnnotation");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_alignedTo), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_alignedTo), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: label

  /** getter for label - gets 
   * @generated */
  public String getLabel() {
    if (AlignedAnnotation_Type.featOkTst && ((AlignedAnnotation_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "eu.excitement.type.alignment.AlignedAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_label);}
    
  /** setter for label - sets  
   * @generated */
  public void setLabel(String v) {
    if (AlignedAnnotation_Type.featOkTst && ((AlignedAnnotation_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "eu.excitement.type.alignment.AlignedAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_label, v);}    
   
    
  //*--------------*
  //* Feature: info

  /** getter for info - gets 
   * @generated */
  public TOP getInfo() {
    if (AlignedAnnotation_Type.featOkTst && ((AlignedAnnotation_Type)jcasType).casFeat_info == null)
      jcasType.jcas.throwFeatMissing("info", "eu.excitement.type.alignment.AlignedAnnotation");
    return (TOP)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_info)));}
    
  /** setter for info - sets  
   * @generated */
  public void setInfo(TOP v) {
    if (AlignedAnnotation_Type.featOkTst && ((AlignedAnnotation_Type)jcasType).casFeat_info == null)
      jcasType.jcas.throwFeatMissing("info", "eu.excitement.type.alignment.AlignedAnnotation");
    jcasType.ll_cas.ll_setRefValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_info, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: alignedItems

  /** getter for alignedItems - gets 
   * @generated */
  public FSArray getAlignedItems() {
    if (AlignedAnnotation_Type.featOkTst && ((AlignedAnnotation_Type)jcasType).casFeat_alignedItems == null)
      jcasType.jcas.throwFeatMissing("alignedItems", "eu.excitement.type.alignment.AlignedAnnotation");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_alignedItems)));}
    
  /** setter for alignedItems - sets  
   * @generated */
  public void setAlignedItems(FSArray v) {
    if (AlignedAnnotation_Type.featOkTst && ((AlignedAnnotation_Type)jcasType).casFeat_alignedItems == null)
      jcasType.jcas.throwFeatMissing("alignedItems", "eu.excitement.type.alignment.AlignedAnnotation");
    jcasType.ll_cas.ll_setRefValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_alignedItems, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for alignedItems - gets an indexed value - 
   * @generated */
  public Annotation getAlignedItems(int i) {
    if (AlignedAnnotation_Type.featOkTst && ((AlignedAnnotation_Type)jcasType).casFeat_alignedItems == null)
      jcasType.jcas.throwFeatMissing("alignedItems", "eu.excitement.type.alignment.AlignedAnnotation");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_alignedItems), i);
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_alignedItems), i)));}

  /** indexed setter for alignedItems - sets an indexed value - 
   * @generated */
  public void setAlignedItems(int i, Annotation v) { 
    if (AlignedAnnotation_Type.featOkTst && ((AlignedAnnotation_Type)jcasType).casFeat_alignedItems == null)
      jcasType.jcas.throwFeatMissing("alignedItems", "eu.excitement.type.alignment.AlignedAnnotation");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_alignedItems), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedAnnotation_Type)jcasType).casFeatCode_alignedItems), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    