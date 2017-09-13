
/* First created by JCasGen Wed Sep 13 18:11:14 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.api.coref.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** A link in the coreference chain.
 * Updated by JCasGen Wed Sep 13 18:11:14 CEST 2017
 * @generated */
public class CoreferenceLink_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = CoreferenceLink.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink");
 
  /** @generated */
  final Feature casFeat_next;
  /** @generated */
  final int     casFeatCode_next;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getNext(int addr) {
        if (featOkTst && casFeat_next == null)
      jcas.throwFeatMissing("next", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink");
    return ll_cas.ll_getRefValue(addr, casFeatCode_next);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNext(int addr, int v) {
        if (featOkTst && casFeat_next == null)
      jcas.throwFeatMissing("next", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink");
    ll_cas.ll_setRefValue(addr, casFeatCode_next, v);}
    
  
 
  /** @generated */
  final Feature casFeat_referenceType;
  /** @generated */
  final int     casFeatCode_referenceType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getReferenceType(int addr) {
        if (featOkTst && casFeat_referenceType == null)
      jcas.throwFeatMissing("referenceType", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink");
    return ll_cas.ll_getStringValue(addr, casFeatCode_referenceType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setReferenceType(int addr, String v) {
        if (featOkTst && casFeat_referenceType == null)
      jcas.throwFeatMissing("referenceType", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink");
    ll_cas.ll_setStringValue(addr, casFeatCode_referenceType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_referenceRelation;
  /** @generated */
  final int     casFeatCode_referenceRelation;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getReferenceRelation(int addr) {
        if (featOkTst && casFeat_referenceRelation == null)
      jcas.throwFeatMissing("referenceRelation", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink");
    return ll_cas.ll_getStringValue(addr, casFeatCode_referenceRelation);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setReferenceRelation(int addr, String v) {
        if (featOkTst && casFeat_referenceRelation == null)
      jcas.throwFeatMissing("referenceRelation", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink");
    ll_cas.ll_setStringValue(addr, casFeatCode_referenceRelation, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public CoreferenceLink_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_next = jcas.getRequiredFeatureDE(casType, "next", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink", featOkTst);
    casFeatCode_next  = (null == casFeat_next) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_next).getCode();

 
    casFeat_referenceType = jcas.getRequiredFeatureDE(casType, "referenceType", "uima.cas.String", featOkTst);
    casFeatCode_referenceType  = (null == casFeat_referenceType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_referenceType).getCode();

 
    casFeat_referenceRelation = jcas.getRequiredFeatureDE(casType, "referenceRelation", "uima.cas.String", featOkTst);
    casFeatCode_referenceRelation  = (null == casFeat_referenceRelation) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_referenceRelation).getCode();

  }
}



    