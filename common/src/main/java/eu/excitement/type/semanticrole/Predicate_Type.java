
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

/** Predicate is a uima.tcas.Annotation. It represents a predicate. It holds the predicate sense as string and links to its arguments (An array of Argument). It has the following features:
- predicateName (uima.cas.String): This feature represents the name of this predicate. It actually refers to the sense of the predicate in PropBank or FrameNet.
 -arguments (uima.cas.FSArray):This feature is an array of EXCITEMENT.semanticrole.Argument. It holds the predicate's arguments.
 * Updated by JCasGen Fri Oct 05 20:17:31 CEST 2012
 * @generated */
public class Predicate_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Predicate_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Predicate_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Predicate(addr, Predicate_Type.this);
  			   Predicate_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Predicate(addr, Predicate_Type.this);
  	  }
    };
  /** @generated */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = Predicate.typeIndexID;
  /** @generated 
     @modifiable */
  //@SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("eu.excitement.type.semanticrole.Predicate");
 
  /** @generated */
  final Feature casFeat_predicateName;
  /** @generated */
  final int     casFeatCode_predicateName;
  /** @generated */ 
  public String getPredicateName(int addr) {
        if (featOkTst && casFeat_predicateName == null)
      jcas.throwFeatMissing("predicateName", "eu.excitement.type.semanticrole.Predicate");
    return ll_cas.ll_getStringValue(addr, casFeatCode_predicateName);
  }
  /** @generated */    
  public void setPredicateName(int addr, String v) {
        if (featOkTst && casFeat_predicateName == null)
      jcas.throwFeatMissing("predicateName", "eu.excitement.type.semanticrole.Predicate");
    ll_cas.ll_setStringValue(addr, casFeatCode_predicateName, v);}
    
  
 
  /** @generated */
  final Feature casFeat_arguments;
  /** @generated */
  final int     casFeatCode_arguments;
  /** @generated */ 
  public int getArguments(int addr) {
        if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "eu.excitement.type.semanticrole.Predicate");
    return ll_cas.ll_getRefValue(addr, casFeatCode_arguments);
  }
  /** @generated */    
  public void setArguments(int addr, int v) {
        if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "eu.excitement.type.semanticrole.Predicate");
    ll_cas.ll_setRefValue(addr, casFeatCode_arguments, v);}
    
   /** @generated */
  public int getArguments(int addr, int i) {
        if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "eu.excitement.type.semanticrole.Predicate");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i);
  }
   
  /** @generated */ 
  public void setArguments(int addr, int i, int v) {
        if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "eu.excitement.type.semanticrole.Predicate");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Predicate_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_predicateName = jcas.getRequiredFeatureDE(casType, "predicateName", "uima.cas.String", featOkTst);
    casFeatCode_predicateName  = (null == casFeat_predicateName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_predicateName).getCode();

 
    casFeat_arguments = jcas.getRequiredFeatureDE(casType, "arguments", "uima.cas.FSArray", featOkTst);
    casFeatCode_arguments  = (null == casFeat_arguments) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_arguments).getCode();

  }
}



    