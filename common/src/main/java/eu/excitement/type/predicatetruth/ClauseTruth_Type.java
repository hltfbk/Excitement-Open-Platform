
/* First created by JCasGen Fri Oct 05 20:17:26 CEST 2012 */
package eu.excitement.type.predicatetruth;

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

/** 
 * Updated by JCasGen Tue Jul 15 10:01:13 IDT 2014
 * @generated */
public class ClauseTruth_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ClauseTruth_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ClauseTruth_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ClauseTruth(addr, ClauseTruth_Type.this);
  			   ClauseTruth_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ClauseTruth(addr, ClauseTruth_Type.this);
  	  }
    };
  /** @generated */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = ClauseTruth.typeIndexID;
  /** @generated 
     @modifiable */
  //@SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("eu.excitement.type.predicatetruth.ClauseTruth");
 
  /** @generated */
  final Feature casFeat_clauseTokens;
  /** @generated */
  final int     casFeatCode_clauseTokens;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getClauseTokens(int addr) {
        if (featOkTst && casFeat_clauseTokens == null)
      jcas.throwFeatMissing("clauseTokens", "eu.excitement.type.predicatetruth.ClauseTruth");
    return ll_cas.ll_getRefValue(addr, casFeatCode_clauseTokens);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setClauseTokens(int addr, int v) {
        if (featOkTst && casFeat_clauseTokens == null)
      jcas.throwFeatMissing("clauseTokens", "eu.excitement.type.predicatetruth.ClauseTruth");
    ll_cas.ll_setRefValue(addr, casFeatCode_clauseTokens, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getClauseTokens(int addr, int i) {
        if (featOkTst && casFeat_clauseTokens == null)
      jcas.throwFeatMissing("clauseTokens", "eu.excitement.type.predicatetruth.ClauseTruth");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_clauseTokens), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_clauseTokens), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_clauseTokens), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setClauseTokens(int addr, int i, int v) {
        if (featOkTst && casFeat_clauseTokens == null)
      jcas.throwFeatMissing("clauseTokens", "eu.excitement.type.predicatetruth.ClauseTruth");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_clauseTokens), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_clauseTokens), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_clauseTokens), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public ClauseTruth_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_clauseTokens = jcas.getRequiredFeatureDE(casType, "clauseTokens", "uima.cas.FSArray", featOkTst);
    casFeatCode_clauseTokens  = (null == casFeat_clauseTokens) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_clauseTokens).getCode();

  }
}



    