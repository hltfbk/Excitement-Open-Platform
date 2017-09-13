
/* First created by JCasGen Wed Sep 13 18:11:14 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.api.metadata.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Information about a tagset (controlled vocabulary).
 * Updated by JCasGen Wed Sep 13 18:11:14 CEST 2017
 * @generated */
public class TagsetDescription_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TagsetDescription.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.api.metadata.type.TagsetDescription");
 
  /** @generated */
  final Feature casFeat_layer;
  /** @generated */
  final int     casFeatCode_layer;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getLayer(int addr) {
        if (featOkTst && casFeat_layer == null)
      jcas.throwFeatMissing("layer", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.TagsetDescription");
    return ll_cas.ll_getStringValue(addr, casFeatCode_layer);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLayer(int addr, String v) {
        if (featOkTst && casFeat_layer == null)
      jcas.throwFeatMissing("layer", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.TagsetDescription");
    ll_cas.ll_setStringValue(addr, casFeatCode_layer, v);}
    
  
 
  /** @generated */
  final Feature casFeat_name;
  /** @generated */
  final int     casFeatCode_name;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getName(int addr) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.TagsetDescription");
    return ll_cas.ll_getStringValue(addr, casFeatCode_name);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setName(int addr, String v) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.TagsetDescription");
    ll_cas.ll_setStringValue(addr, casFeatCode_name, v);}
    
  
 
  /** @generated */
  final Feature casFeat_tags;
  /** @generated */
  final int     casFeatCode_tags;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getTags(int addr) {
        if (featOkTst && casFeat_tags == null)
      jcas.throwFeatMissing("tags", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.TagsetDescription");
    return ll_cas.ll_getRefValue(addr, casFeatCode_tags);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTags(int addr, int v) {
        if (featOkTst && casFeat_tags == null)
      jcas.throwFeatMissing("tags", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.TagsetDescription");
    ll_cas.ll_setRefValue(addr, casFeatCode_tags, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getTags(int addr, int i) {
        if (featOkTst && casFeat_tags == null)
      jcas.throwFeatMissing("tags", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.TagsetDescription");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setTags(int addr, int i, int v) {
        if (featOkTst && casFeat_tags == null)
      jcas.throwFeatMissing("tags", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.TagsetDescription");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tags), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TagsetDescription_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_layer = jcas.getRequiredFeatureDE(casType, "layer", "uima.cas.String", featOkTst);
    casFeatCode_layer  = (null == casFeat_layer) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_layer).getCode();

 
    casFeat_name = jcas.getRequiredFeatureDE(casType, "name", "uima.cas.String", featOkTst);
    casFeatCode_name  = (null == casFeat_name) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_name).getCode();

 
    casFeat_tags = jcas.getRequiredFeatureDE(casType, "tags", "uima.cas.FSArray", featOkTst);
    casFeatCode_tags  = (null == casFeat_tags) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_tags).getCode();

  }
}



    