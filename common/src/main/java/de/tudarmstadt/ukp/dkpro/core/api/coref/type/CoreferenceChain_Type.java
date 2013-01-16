
/* First created by JCasGen Fri Oct 05 20:16:32 CEST 2012 */
package de.tudarmstadt.ukp.dkpro.core.api.coref.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.cas.AnnotationBase_Type;

/** 
 * Updated by JCasGen Fri Oct 05 20:16:32 CEST 2012
 * @generated */
public class CoreferenceChain_Type extends AnnotationBase_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (CoreferenceChain_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = CoreferenceChain_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new CoreferenceChain(addr, CoreferenceChain_Type.this);
  			   CoreferenceChain_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new CoreferenceChain(addr, CoreferenceChain_Type.this);
  	  }
    };
  /** @generated */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = CoreferenceChain.typeIndexID;
  /** @generated 
     @modifiable */
  //@SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain");
 
  /** @generated */
  final Feature casFeat_first;
  /** @generated */
  final int     casFeatCode_first;
  /** @generated */ 
  public int getFirst(int addr) {
        if (featOkTst && casFeat_first == null)
      jcas.throwFeatMissing("first", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain");
    return ll_cas.ll_getRefValue(addr, casFeatCode_first);
  }
  /** @generated */    
  public void setFirst(int addr, int v) {
        if (featOkTst && casFeat_first == null)
      jcas.throwFeatMissing("first", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain");
    ll_cas.ll_setRefValue(addr, casFeatCode_first, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public CoreferenceChain_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_first = jcas.getRequiredFeatureDE(casType, "first", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink", featOkTst);
    casFeatCode_first  = (null == casFeat_first) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_first).getCode();

  }
}



    