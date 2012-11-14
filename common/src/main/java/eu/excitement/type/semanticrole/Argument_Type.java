
/* First created by JCasGen Fri Oct 05 20:17:31 CEST 2012 */
package eu.excitement.type.semanticrole;

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

/** It represents an argument (of semantic role labeling). It has two features; the argument's semantic role (type), and a backward reference to the predicate that governs the argument.
 * Updated by JCasGen Fri Oct 05 20:17:31 CEST 2012
 * @generated */
public class Argument_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Argument_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Argument_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Argument(addr, Argument_Type.this);
  			   Argument_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Argument(addr, Argument_Type.this);
  	  }
    };
  /** @generated */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = Argument.typeIndexID;
  /** @generated 
     @modifiable */
  //@SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("eu.excitement.type.semanticrole.Argument");
 
  /** @generated */
  final Feature casFeat_argumentName;
  /** @generated */
  final int     casFeatCode_argumentName;
  /** @generated */ 
  public String getArgumentName(int addr) {
        if (featOkTst && casFeat_argumentName == null)
      jcas.throwFeatMissing("argumentName", "eu.excitement.type.semanticrole.Argument");
    return ll_cas.ll_getStringValue(addr, casFeatCode_argumentName);
  }
  /** @generated */    
  public void setArgumentName(int addr, String v) {
        if (featOkTst && casFeat_argumentName == null)
      jcas.throwFeatMissing("argumentName", "eu.excitement.type.semanticrole.Argument");
    ll_cas.ll_setStringValue(addr, casFeatCode_argumentName, v);}
    
  
 
  /** @generated */
  final Feature casFeat_predicates;
  /** @generated */
  final int     casFeatCode_predicates;
  /** @generated */ 
  public int getPredicates(int addr) {
        if (featOkTst && casFeat_predicates == null)
      jcas.throwFeatMissing("predicates", "eu.excitement.type.semanticrole.Argument");
    return ll_cas.ll_getRefValue(addr, casFeatCode_predicates);
  }
  /** @generated */    
  public void setPredicates(int addr, int v) {
        if (featOkTst && casFeat_predicates == null)
      jcas.throwFeatMissing("predicates", "eu.excitement.type.semanticrole.Argument");
    ll_cas.ll_setRefValue(addr, casFeatCode_predicates, v);}
    
   /** @generated */
  public int getPredicates(int addr, int i) {
        if (featOkTst && casFeat_predicates == null)
      jcas.throwFeatMissing("predicates", "eu.excitement.type.semanticrole.Argument");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_predicates), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_predicates), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_predicates), i);
  }
   
  /** @generated */ 
  public void setPredicates(int addr, int i, int v) {
        if (featOkTst && casFeat_predicates == null)
      jcas.throwFeatMissing("predicates", "eu.excitement.type.semanticrole.Argument");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_predicates), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_predicates), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_predicates), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Argument_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_argumentName = jcas.getRequiredFeatureDE(casType, "argumentName", "uima.cas.String", featOkTst);
    casFeatCode_argumentName  = (null == casFeat_argumentName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_argumentName).getCode();

 
    casFeat_predicates = jcas.getRequiredFeatureDE(casType, "predicates", "uima.cas.FSArray", featOkTst);
    casFeatCode_predicates  = (null == casFeat_predicates) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_predicates).getCode();

  }
}



    