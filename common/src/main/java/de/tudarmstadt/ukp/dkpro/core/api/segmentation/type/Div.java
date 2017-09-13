

/* First created by JCasGen Wed Sep 13 18:11:14 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.api.segmentation.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Document structure element.
 * Updated by JCasGen Wed Sep 13 18:11:14 CEST 2017
 * XML source: /home/nira/informiz/Excitement-Open-Platform/common/target/jcasgen/typesystem.xml
 * @generated */
public class Div extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Div.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Div() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Div(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Div(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Div(JCas jcas, int begin, int end) {
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
  //* Feature: divType

  /** getter for divType - gets 
   * @generated
   * @return value of the feature 
   */
  public String getDivType() {
    if (Div_Type.featOkTst && ((Div_Type)jcasType).casFeat_divType == null)
      jcasType.jcas.throwFeatMissing("divType", "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Div");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Div_Type)jcasType).casFeatCode_divType);}
    
  /** setter for divType - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDivType(String v) {
    if (Div_Type.featOkTst && ((Div_Type)jcasType).casFeat_divType == null)
      jcasType.jcas.throwFeatMissing("divType", "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Div");
    jcasType.ll_cas.ll_setStringValue(addr, ((Div_Type)jcasType).casFeatCode_divType, v);}    
   
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets If this unit had an ID in the source format from which it was imported, it may be stored here. IDs are typically not assiged by DKPro Core components. If an ID is present, it should be respected by writers.
   * @generated
   * @return value of the feature 
   */
  public String getId() {
    if (Div_Type.featOkTst && ((Div_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Div");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Div_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets If this unit had an ID in the source format from which it was imported, it may be stored here. IDs are typically not assiged by DKPro Core components. If an ID is present, it should be respected by writers. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(String v) {
    if (Div_Type.featOkTst && ((Div_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Div");
    jcasType.ll_cas.ll_setStringValue(addr, ((Div_Type)jcasType).casFeatCode_id, v);}    
  }

    