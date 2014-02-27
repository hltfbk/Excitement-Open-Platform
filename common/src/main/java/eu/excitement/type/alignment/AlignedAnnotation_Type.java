
/* First created by JCasGen Sun Feb 16 17:14:26 CET 2014 */
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

/** 
 * Updated by JCasGen Sun Feb 16 17:14:26 CET 2014
 * @generated */
public class AlignedAnnotation_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (AlignedAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = AlignedAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new AlignedAnnotation(addr, AlignedAnnotation_Type.this);
  			   AlignedAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new AlignedAnnotation(addr, AlignedAnnotation_Type.this);
  	  }
    };
  /** @generated */
//  @SuppressWarnings ("hiding")
  public final static int typeIndexID = AlignedAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
//  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("eu.excitement.type.alignment.AlignedAnnotation");
 
  /** @generated */
  final Feature casFeat_alignedTo;
  /** @generated */
  final int     casFeatCode_alignedTo;
  /** @generated */ 
  public int getAlignedTo(int addr) {
        if (featOkTst && casFeat_alignedTo == null)
      jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedAnnotation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_alignedTo);
  }
  /** @generated */    
  public void setAlignedTo(int addr, int v) {
        if (featOkTst && casFeat_alignedTo == null)
      jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedAnnotation");
    ll_cas.ll_setRefValue(addr, casFeatCode_alignedTo, v);}
    
   /** @generated */
  public int getAlignedTo(int addr, int i) {
        if (featOkTst && casFeat_alignedTo == null)
      jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedAnnotation");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_alignedTo), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_alignedTo), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_alignedTo), i);
  }
   
  /** @generated */ 
  public void setAlignedTo(int addr, int i, int v) {
        if (featOkTst && casFeat_alignedTo == null)
      jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedAnnotation");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_alignedTo), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_alignedTo), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_alignedTo), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_label;
  /** @generated */
  final int     casFeatCode_label;
  /** @generated */ 
  public String getLabel(int addr) {
        if (featOkTst && casFeat_label == null)
      jcas.throwFeatMissing("label", "eu.excitement.type.alignment.AlignedAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_label);
  }
  /** @generated */    
  public void setLabel(int addr, String v) {
        if (featOkTst && casFeat_label == null)
      jcas.throwFeatMissing("label", "eu.excitement.type.alignment.AlignedAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_label, v);}
    
  
 
  /** @generated */
  final Feature casFeat_info;
  /** @generated */
  final int     casFeatCode_info;
  /** @generated */ 
  public int getInfo(int addr) {
        if (featOkTst && casFeat_info == null)
      jcas.throwFeatMissing("info", "eu.excitement.type.alignment.AlignedAnnotation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_info);
  }
  /** @generated */    
  public void setInfo(int addr, int v) {
        if (featOkTst && casFeat_info == null)
      jcas.throwFeatMissing("info", "eu.excitement.type.alignment.AlignedAnnotation");
    ll_cas.ll_setRefValue(addr, casFeatCode_info, v);}
    
  
 
  /** @generated */
  final Feature casFeat_alignedItems;
  /** @generated */
  final int     casFeatCode_alignedItems;
  /** @generated */ 
  public int getAlignedItems(int addr) {
        if (featOkTst && casFeat_alignedItems == null)
      jcas.throwFeatMissing("alignedItems", "eu.excitement.type.alignment.AlignedAnnotation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_alignedItems);
  }
  /** @generated */    
  public void setAlignedItems(int addr, int v) {
        if (featOkTst && casFeat_alignedItems == null)
      jcas.throwFeatMissing("alignedItems", "eu.excitement.type.alignment.AlignedAnnotation");
    ll_cas.ll_setRefValue(addr, casFeatCode_alignedItems, v);}
    
   /** @generated */
  public int getAlignedItems(int addr, int i) {
        if (featOkTst && casFeat_alignedItems == null)
      jcas.throwFeatMissing("alignedItems", "eu.excitement.type.alignment.AlignedAnnotation");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_alignedItems), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_alignedItems), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_alignedItems), i);
  }
   
  /** @generated */ 
  public void setAlignedItems(int addr, int i, int v) {
        if (featOkTst && casFeat_alignedItems == null)
      jcas.throwFeatMissing("alignedItems", "eu.excitement.type.alignment.AlignedAnnotation");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_alignedItems), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_alignedItems), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_alignedItems), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public AlignedAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_alignedTo = jcas.getRequiredFeatureDE(casType, "alignedTo", "uima.cas.FSArray", featOkTst);
    casFeatCode_alignedTo  = (null == casFeat_alignedTo) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_alignedTo).getCode();

 
    casFeat_label = jcas.getRequiredFeatureDE(casType, "label", "uima.cas.String", featOkTst);
    casFeatCode_label  = (null == casFeat_label) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_label).getCode();

 
    casFeat_info = jcas.getRequiredFeatureDE(casType, "info", "uima.cas.TOP", featOkTst);
    casFeatCode_info  = (null == casFeat_info) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_info).getCode();

 
    casFeat_alignedItems = jcas.getRequiredFeatureDE(casType, "alignedItems", "uima.cas.FSArray", featOkTst);
    casFeatCode_alignedItems  = (null == casFeat_alignedItems) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_alignedItems).getCode();

  }
}



    