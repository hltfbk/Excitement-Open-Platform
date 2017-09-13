
/* First created by JCasGen Wed Sep 13 18:11:14 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** A dependency relation between two tokens. The dependency annotation begin and end offsets correspond to those of the dependent.
 * Updated by JCasGen Wed Sep 13 18:11:14 CEST 2017
 * @generated */
public class Dependency_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Dependency.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency");
 
  /** @generated */
  final Feature casFeat_Governor;
  /** @generated */
  final int     casFeatCode_Governor;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getGovernor(int addr) {
        if (featOkTst && casFeat_Governor == null)
      jcas.throwFeatMissing("Governor", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency");
    return ll_cas.ll_getRefValue(addr, casFeatCode_Governor);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setGovernor(int addr, int v) {
        if (featOkTst && casFeat_Governor == null)
      jcas.throwFeatMissing("Governor", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency");
    ll_cas.ll_setRefValue(addr, casFeatCode_Governor, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Dependent;
  /** @generated */
  final int     casFeatCode_Dependent;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getDependent(int addr) {
        if (featOkTst && casFeat_Dependent == null)
      jcas.throwFeatMissing("Dependent", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency");
    return ll_cas.ll_getRefValue(addr, casFeatCode_Dependent);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDependent(int addr, int v) {
        if (featOkTst && casFeat_Dependent == null)
      jcas.throwFeatMissing("Dependent", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency");
    ll_cas.ll_setRefValue(addr, casFeatCode_Dependent, v);}
    
  
 
  /** @generated */
  final Feature casFeat_DependencyType;
  /** @generated */
  final int     casFeatCode_DependencyType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDependencyType(int addr) {
        if (featOkTst && casFeat_DependencyType == null)
      jcas.throwFeatMissing("DependencyType", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency");
    return ll_cas.ll_getStringValue(addr, casFeatCode_DependencyType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDependencyType(int addr, String v) {
        if (featOkTst && casFeat_DependencyType == null)
      jcas.throwFeatMissing("DependencyType", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency");
    ll_cas.ll_setStringValue(addr, casFeatCode_DependencyType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_flavor;
  /** @generated */
  final int     casFeatCode_flavor;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getFlavor(int addr) {
        if (featOkTst && casFeat_flavor == null)
      jcas.throwFeatMissing("flavor", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency");
    return ll_cas.ll_getStringValue(addr, casFeatCode_flavor);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFlavor(int addr, String v) {
        if (featOkTst && casFeat_flavor == null)
      jcas.throwFeatMissing("flavor", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency");
    ll_cas.ll_setStringValue(addr, casFeatCode_flavor, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Dependency_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Governor = jcas.getRequiredFeatureDE(casType, "Governor", "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token", featOkTst);
    casFeatCode_Governor  = (null == casFeat_Governor) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Governor).getCode();

 
    casFeat_Dependent = jcas.getRequiredFeatureDE(casType, "Dependent", "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token", featOkTst);
    casFeatCode_Dependent  = (null == casFeat_Dependent) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Dependent).getCode();

 
    casFeat_DependencyType = jcas.getRequiredFeatureDE(casType, "DependencyType", "uima.cas.String", featOkTst);
    casFeatCode_DependencyType  = (null == casFeat_DependencyType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_DependencyType).getCode();

 
    casFeat_flavor = jcas.getRequiredFeatureDE(casType, "flavor", "uima.cas.String", featOkTst);
    casFeatCode_flavor  = (null == casFeat_flavor) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_flavor).getCode();

  }
}



    