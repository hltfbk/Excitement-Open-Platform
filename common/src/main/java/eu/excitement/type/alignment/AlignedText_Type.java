
/* First created by JCasGen Fri Oct 05 20:17:43 CEST 2012 */
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

/** This type represent an aligned textual unit. Its span refers to the "source" linguistic entity. This can be a token (word alignment), a syntax node (phrase alignments), or a sentence
(sentence alignment).
 * Updated by JCasGen Fri Oct 05 20:17:43 CEST 2012
 * @generated */
public class AlignedText_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (AlignedText_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = AlignedText_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new AlignedText(addr, AlignedText_Type.this);
  			   AlignedText_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new AlignedText(addr, AlignedText_Type.this);
  	  }
    };
  /** @generated */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = AlignedText.typeIndexID;
  /** @generated 
     @modifiable */
  //@SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("eu.excitement.type.alignment.AlignedText");
 
  /** @generated */
  final Feature casFeat_alignedTo;
  /** @generated */
  final int     casFeatCode_alignedTo;
  /** @generated */ 
  public int getAlignedTo(int addr) {
        if (featOkTst && casFeat_alignedTo == null)
      jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedText");
    return ll_cas.ll_getRefValue(addr, casFeatCode_alignedTo);
  }
  /** @generated */    
  public void setAlignedTo(int addr, int v) {
        if (featOkTst && casFeat_alignedTo == null)
      jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedText");
    ll_cas.ll_setRefValue(addr, casFeatCode_alignedTo, v);}
    
   /** @generated */
  public int getAlignedTo(int addr, int i) {
        if (featOkTst && casFeat_alignedTo == null)
      jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedText");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_alignedTo), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_alignedTo), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_alignedTo), i);
  }
   
  /** @generated */ 
  public void setAlignedTo(int addr, int i, int v) {
        if (featOkTst && casFeat_alignedTo == null)
      jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedText");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_alignedTo), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_alignedTo), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_alignedTo), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_alignmentType;
  /** @generated */
  final int     casFeatCode_alignmentType;
  /** @generated */ 
  public String getAlignmentType(int addr) {
        if (featOkTst && casFeat_alignmentType == null)
      jcas.throwFeatMissing("alignmentType", "eu.excitement.type.alignment.AlignedText");
    return ll_cas.ll_getStringValue(addr, casFeatCode_alignmentType);
  }
  /** @generated */    
  public void setAlignmentType(int addr, String v) {
        if (featOkTst && casFeat_alignmentType == null)
      jcas.throwFeatMissing("alignmentType", "eu.excitement.type.alignment.AlignedText");
    ll_cas.ll_setStringValue(addr, casFeatCode_alignmentType, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public AlignedText_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_alignedTo = jcas.getRequiredFeatureDE(casType, "alignedTo", "uima.cas.FSArray", featOkTst);
    casFeatCode_alignedTo  = (null == casFeat_alignedTo) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_alignedTo).getCode();

 
    casFeat_alignmentType = jcas.getRequiredFeatureDE(casType, "alignmentType", "uima.cas.String", featOkTst);
    casFeatCode_alignmentType  = (null == casFeat_alignmentType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_alignmentType).getCode();

  }
}



    