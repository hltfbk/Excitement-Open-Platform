

/* First created by JCasGen Mon Jun 02 13:23:38 CEST 2014 */
package eu.excitement.type.nemex;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.cas.StringArray;


/** This type is the annotation type designed for the Nemex-A annotator(s). 
 * Updated by JCasGen Mon Jun 02 13:23:38 CEST 2014
 * XML source: /Users/tailblues/progs/Excitement-Open-Platform/common/src/main/resources/desc/type/NemexTypes.xml
 * @generated */
public class NemexType extends Annotation {
  /** @generated
   * @ordered 
   */
//  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NemexType.class);
  /** @generated
   * @ordered 
   */
// @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected NemexType() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public NemexType(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public NemexType(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public NemexType(JCas jcas, int begin, int end) {
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
  //* Feature: values

  /** getter for values - gets This feature is an array (uima StringArray) that holds values matched by the Nemex-A lookup. 
   * @generated */
  public StringArray getValues() {
    if (NemexType_Type.featOkTst && ((NemexType_Type)jcasType).casFeat_values == null)
      jcasType.jcas.throwFeatMissing("values", "eu.excitement.type.nemex.NemexType");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((NemexType_Type)jcasType).casFeatCode_values)));}
    
  /** setter for values - sets This feature is an array (uima StringArray) that holds values matched by the Nemex-A lookup.  
   * @generated */
  public void setValues(StringArray v) {
    if (NemexType_Type.featOkTst && ((NemexType_Type)jcasType).casFeat_values == null)
      jcasType.jcas.throwFeatMissing("values", "eu.excitement.type.nemex.NemexType");
    jcasType.ll_cas.ll_setRefValue(addr, ((NemexType_Type)jcasType).casFeatCode_values, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for values - gets an indexed value - This feature is an array (uima StringArray) that holds values matched by the Nemex-A lookup. 
   * @generated */
  public String getValues(int i) {
    if (NemexType_Type.featOkTst && ((NemexType_Type)jcasType).casFeat_values == null)
      jcasType.jcas.throwFeatMissing("values", "eu.excitement.type.nemex.NemexType");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((NemexType_Type)jcasType).casFeatCode_values), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((NemexType_Type)jcasType).casFeatCode_values), i);}

  /** indexed setter for values - sets an indexed value - This feature is an array (uima StringArray) that holds values matched by the Nemex-A lookup. 
   * @generated */
  public void setValues(int i, String v) { 
    if (NemexType_Type.featOkTst && ((NemexType_Type)jcasType).casFeat_values == null)
      jcasType.jcas.throwFeatMissing("values", "eu.excitement.type.nemex.NemexType");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((NemexType_Type)jcasType).casFeatCode_values), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((NemexType_Type)jcasType).casFeatCode_values), i, v);}
  }

    