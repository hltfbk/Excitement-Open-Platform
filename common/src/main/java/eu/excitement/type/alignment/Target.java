

/* First created by JCasGen Thu Apr 24 15:21:08 CEST 2014 */
package eu.excitement.type.alignment;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** This is a CAS annotation type that can point one or more annotations (tokens, parse nodes, NER nodes, or any annotations) 
It is a list that groups annotations in one View. The target type allows flexible alignment between any data, including structures made by multiple annotations. 
Note on usage: begin holds the minimum begin value among annotations in targetAnnotations. Likewise, end should point the maximum end among annotations.
 * Updated by JCasGen Wed May 14 14:20:19 CEST 2014
 * XML source: /home/tailblues/progs/Excitement-Open-Platform/common/src/main/resources/desc/type/AlignmentTypes.xml
 * @generated */
public class Target extends Annotation {
  /** @generated
   * @ordered 
   */
//  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Target.class);
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
  protected Target() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Target(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Target(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Target(JCas jcas, int begin, int end) {
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
  //* Feature: targetAnnotations

  /** getter for targetAnnotations - gets This is a FSArray that can hold one or more annotations. A target should mark one or more annotations 
   * @generated */
  public FSArray getTargetAnnotations() {
    if (Target_Type.featOkTst && ((Target_Type)jcasType).casFeat_targetAnnotations == null)
      jcasType.jcas.throwFeatMissing("targetAnnotations", "eu.excitement.type.alignment.Target");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Target_Type)jcasType).casFeatCode_targetAnnotations)));}
    
  /** setter for targetAnnotations - sets This is a FSArray that can hold one or more annotations. A target should mark one or more annotations  
   * @generated */
  public void setTargetAnnotations(FSArray v) {
    if (Target_Type.featOkTst && ((Target_Type)jcasType).casFeat_targetAnnotations == null)
      jcasType.jcas.throwFeatMissing("targetAnnotations", "eu.excitement.type.alignment.Target");
    jcasType.ll_cas.ll_setRefValue(addr, ((Target_Type)jcasType).casFeatCode_targetAnnotations, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for targetAnnotations - gets an indexed value - This is a FSArray that can hold one or more annotations. A target should mark one or more annotations 
   * @generated */
  public Annotation getTargetAnnotations(int i) {
    if (Target_Type.featOkTst && ((Target_Type)jcasType).casFeat_targetAnnotations == null)
      jcasType.jcas.throwFeatMissing("targetAnnotations", "eu.excitement.type.alignment.Target");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Target_Type)jcasType).casFeatCode_targetAnnotations), i);
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Target_Type)jcasType).casFeatCode_targetAnnotations), i)));}

  /** indexed setter for targetAnnotations - sets an indexed value - This is a FSArray that can hold one or more annotations. A target should mark one or more annotations 
   * @generated */
  public void setTargetAnnotations(int i, Annotation v) { 
    if (Target_Type.featOkTst && ((Target_Type)jcasType).casFeat_targetAnnotations == null)
      jcasType.jcas.throwFeatMissing("targetAnnotations", "eu.excitement.type.alignment.Target");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Target_Type)jcasType).casFeatCode_targetAnnotations), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Target_Type)jcasType).casFeatCode_targetAnnotations), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    