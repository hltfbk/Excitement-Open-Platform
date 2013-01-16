

/* First created by JCasGen Fri Oct 05 20:16:32 CEST 2012 */
package de.tudarmstadt.ukp.dkpro.core.api.coref.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Oct 05 20:16:32 CEST 2012
 * XML source: /Users/tailblues/progs/github/Excitement-Open-Platform/common/src/main/resources/desc/type/coref.xml
 * @generated */
public class CoreferenceLink extends Annotation {
  /** @generated
   * @ordered 
   */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CoreferenceLink.class);
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
  protected CoreferenceLink() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public CoreferenceLink(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public CoreferenceLink(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public CoreferenceLink(JCas jcas, int begin, int end) {
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
  //* Feature: next

  /** getter for next - gets 
   * @generated */
  public CoreferenceLink getNext() {
    if (CoreferenceLink_Type.featOkTst && ((CoreferenceLink_Type)jcasType).casFeat_next == null)
      jcasType.jcas.throwFeatMissing("next", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink");
    return (CoreferenceLink)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CoreferenceLink_Type)jcasType).casFeatCode_next)));}
    
  /** setter for next - sets  
   * @generated */
  public void setNext(CoreferenceLink v) {
    if (CoreferenceLink_Type.featOkTst && ((CoreferenceLink_Type)jcasType).casFeat_next == null)
      jcasType.jcas.throwFeatMissing("next", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink");
    jcasType.ll_cas.ll_setRefValue(addr, ((CoreferenceLink_Type)jcasType).casFeatCode_next, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: referenceType

  /** getter for referenceType - gets 
   * @generated */
  public String getReferenceType() {
    if (CoreferenceLink_Type.featOkTst && ((CoreferenceLink_Type)jcasType).casFeat_referenceType == null)
      jcasType.jcas.throwFeatMissing("referenceType", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CoreferenceLink_Type)jcasType).casFeatCode_referenceType);}
    
  /** setter for referenceType - sets  
   * @generated */
  public void setReferenceType(String v) {
    if (CoreferenceLink_Type.featOkTst && ((CoreferenceLink_Type)jcasType).casFeat_referenceType == null)
      jcasType.jcas.throwFeatMissing("referenceType", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink");
    jcasType.ll_cas.ll_setStringValue(addr, ((CoreferenceLink_Type)jcasType).casFeatCode_referenceType, v);}    
  }

    