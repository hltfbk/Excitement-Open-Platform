
/* First created by JCasGen Wed Sep 13 18:11:14 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.api.coref.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.cas.AnnotationBase_Type;

/** Marks the beginning of a chain.
 * Updated by JCasGen Wed Sep 13 18:11:14 CEST 2017
 * @generated */
public class CoreferenceChain_Type extends AnnotationBase_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = CoreferenceChain.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain");
 
  /** @generated */
  final Feature casFeat_first;
  /** @generated */
  final int     casFeatCode_first;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getFirst(int addr) {
        if (featOkTst && casFeat_first == null)
      jcas.throwFeatMissing("first", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain");
    return ll_cas.ll_getRefValue(addr, casFeatCode_first);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFirst(int addr, int v) {
        if (featOkTst && casFeat_first == null)
      jcas.throwFeatMissing("first", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain");
    ll_cas.ll_setRefValue(addr, casFeatCode_first, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public CoreferenceChain_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_first = jcas.getRequiredFeatureDE(casType, "first", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink", featOkTst);
    casFeatCode_first  = (null == casFeat_first) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_first).getCode();

  }
}



    