
/* First created by JCasGen Thu Apr 24 15:21:08 CEST 2014 */
package eu.excitement.type.alignment;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** This is a CAS annotation type that can point one or more annotations (tokens, parse nodes, NER nodes, or any annotations) 
It is a list that groups annotations in one View. The target type allows flexible alignment between any data, including structures made by multiple annotations. 
Note on usage: begin holds the minimum begin value among annotations in targetAnnotations. Likewise, end should point the maximum end among annotations. 
 * Updated by JCasGen Thu Apr 24 15:21:08 CEST 2014
 * @generated */
public class Target_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Target_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Target_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Target(addr, Target_Type.this);
  			   Target_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Target(addr, Target_Type.this);
  	  }
    };
  /** @generated */
//  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Target.typeIndexID;
  /** @generated 
     @modifiable */
//  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("eu.excitement.type.alignment.Target");
 
  /** @generated */
  final Feature casFeat_targetAnnotations;
  /** @generated */
  final int     casFeatCode_targetAnnotations;
  /** @generated */ 
  public int getTargetAnnotations(int addr) {
        if (featOkTst && casFeat_targetAnnotations == null)
      jcas.throwFeatMissing("targetAnnotations", "eu.excitement.type.alignment.Target");
    return ll_cas.ll_getRefValue(addr, casFeatCode_targetAnnotations);
  }
  /** @generated */    
  public void setTargetAnnotations(int addr, int v) {
        if (featOkTst && casFeat_targetAnnotations == null)
      jcas.throwFeatMissing("targetAnnotations", "eu.excitement.type.alignment.Target");
    ll_cas.ll_setRefValue(addr, casFeatCode_targetAnnotations, v);}
    
   /** @generated */
  public int getTargetAnnotations(int addr, int i) {
        if (featOkTst && casFeat_targetAnnotations == null)
      jcas.throwFeatMissing("targetAnnotations", "eu.excitement.type.alignment.Target");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_targetAnnotations), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_targetAnnotations), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_targetAnnotations), i);
  }
   
  /** @generated */ 
  public void setTargetAnnotations(int addr, int i, int v) {
        if (featOkTst && casFeat_targetAnnotations == null)
      jcas.throwFeatMissing("targetAnnotations", "eu.excitement.type.alignment.Target");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_targetAnnotations), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_targetAnnotations), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_targetAnnotations), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Target_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_targetAnnotations = jcas.getRequiredFeatureDE(casType, "targetAnnotations", "uima.cas.FSArray", featOkTst);
    casFeatCode_targetAnnotations  = (null == casFeat_targetAnnotations) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_targetAnnotations).getCode();

  }
}



    