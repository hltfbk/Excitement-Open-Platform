
/* First created by JCasGen Fri Oct 05 20:16:50 CEST 2012 */
package eu.excitement.type.entailment;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.cas.TOP_Type;

/** This type represents a text-hypothesis pair.
 * Updated by JCasGen Fri Oct 05 20:16:50 CEST 2012
 * @generated */
public class Pair_Type extends TOP_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Pair_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Pair_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Pair(addr, Pair_Type.this);
  			   Pair_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Pair(addr, Pair_Type.this);
  	  }
    };
  /** @generated */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = Pair.typeIndexID;
  /** @generated 
     @modifiable */
  //@SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("eu.excitement.type.entailment.Pair");
 
  /** @generated */
  final Feature casFeat_pairID;
  /** @generated */
  final int     casFeatCode_pairID;
  /** @generated */ 
  public String getPairID(int addr) {
        if (featOkTst && casFeat_pairID == null)
      jcas.throwFeatMissing("pairID", "eu.excitement.type.entailment.Pair");
    return ll_cas.ll_getStringValue(addr, casFeatCode_pairID);
  }
  /** @generated */    
  public void setPairID(int addr, String v) {
        if (featOkTst && casFeat_pairID == null)
      jcas.throwFeatMissing("pairID", "eu.excitement.type.entailment.Pair");
    ll_cas.ll_setStringValue(addr, casFeatCode_pairID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_text;
  /** @generated */
  final int     casFeatCode_text;
  /** @generated */ 
  public int getText(int addr) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "eu.excitement.type.entailment.Pair");
    return ll_cas.ll_getRefValue(addr, casFeatCode_text);
  }
  /** @generated */    
  public void setText(int addr, int v) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "eu.excitement.type.entailment.Pair");
    ll_cas.ll_setRefValue(addr, casFeatCode_text, v);}
    
  
 
  /** @generated */
  final Feature casFeat_hypothesis;
  /** @generated */
  final int     casFeatCode_hypothesis;
  /** @generated */ 
  public int getHypothesis(int addr) {
        if (featOkTst && casFeat_hypothesis == null)
      jcas.throwFeatMissing("hypothesis", "eu.excitement.type.entailment.Pair");
    return ll_cas.ll_getRefValue(addr, casFeatCode_hypothesis);
  }
  /** @generated */    
  public void setHypothesis(int addr, int v) {
        if (featOkTst && casFeat_hypothesis == null)
      jcas.throwFeatMissing("hypothesis", "eu.excitement.type.entailment.Pair");
    ll_cas.ll_setRefValue(addr, casFeatCode_hypothesis, v);}
    
  
 
  /** @generated */
  final Feature casFeat_goldAnswer;
  /** @generated */
  final int     casFeatCode_goldAnswer;
  /** @generated */ 
  public String getGoldAnswer(int addr) {
        if (featOkTst && casFeat_goldAnswer == null)
      jcas.throwFeatMissing("goldAnswer", "eu.excitement.type.entailment.Pair");
    return ll_cas.ll_getStringValue(addr, casFeatCode_goldAnswer);
  }
  /** @generated */    
  public void setGoldAnswer(int addr, String v) {
        if (featOkTst && casFeat_goldAnswer == null)
      jcas.throwFeatMissing("goldAnswer", "eu.excitement.type.entailment.Pair");
    ll_cas.ll_setStringValue(addr, casFeatCode_goldAnswer, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Pair_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_pairID = jcas.getRequiredFeatureDE(casType, "pairID", "uima.cas.String", featOkTst);
    casFeatCode_pairID  = (null == casFeat_pairID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pairID).getCode();

 
    casFeat_text = jcas.getRequiredFeatureDE(casType, "text", "eu.excitement.type.entailment.Text", featOkTst);
    casFeatCode_text  = (null == casFeat_text) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_text).getCode();

 
    casFeat_hypothesis = jcas.getRequiredFeatureDE(casType, "hypothesis", "eu.excitement.type.entailment.Hypothesis", featOkTst);
    casFeatCode_hypothesis  = (null == casFeat_hypothesis) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_hypothesis).getCode();

 
    casFeat_goldAnswer = jcas.getRequiredFeatureDE(casType, "goldAnswer", "eu.excitement.type.entailment.Decision", featOkTst);
    casFeatCode_goldAnswer  = (null == casFeat_goldAnswer) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_goldAnswer).getCode();

  }
}



    