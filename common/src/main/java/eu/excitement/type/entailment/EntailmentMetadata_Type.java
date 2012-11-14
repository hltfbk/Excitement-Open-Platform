
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

/** This type provides metadata for entailment problem.
 * Updated by JCasGen Fri Oct 05 20:16:50 CEST 2012
 * @generated */
public class EntailmentMetadata_Type extends TOP_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (EntailmentMetadata_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = EntailmentMetadata_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new EntailmentMetadata(addr, EntailmentMetadata_Type.this);
  			   EntailmentMetadata_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new EntailmentMetadata(addr, EntailmentMetadata_Type.this);
  	  }
    };
  /** @generated */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = EntailmentMetadata.typeIndexID;
  /** @generated 
     @modifiable */
  //@SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("eu.excitement.type.entailment.EntailmentMetadata");
 
  /** @generated */
  final Feature casFeat_task;
  /** @generated */
  final int     casFeatCode_task;
  /** @generated */ 
  public String getTask(int addr) {
        if (featOkTst && casFeat_task == null)
      jcas.throwFeatMissing("task", "eu.excitement.type.entailment.EntailmentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_task);
  }
  /** @generated */    
  public void setTask(int addr, String v) {
        if (featOkTst && casFeat_task == null)
      jcas.throwFeatMissing("task", "eu.excitement.type.entailment.EntailmentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_task, v);}
    
  
 
  /** @generated */
  final Feature casFeat_channel;
  /** @generated */
  final int     casFeatCode_channel;
  /** @generated */ 
  public String getChannel(int addr) {
        if (featOkTst && casFeat_channel == null)
      jcas.throwFeatMissing("channel", "eu.excitement.type.entailment.EntailmentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_channel);
  }
  /** @generated */    
  public void setChannel(int addr, String v) {
        if (featOkTst && casFeat_channel == null)
      jcas.throwFeatMissing("channel", "eu.excitement.type.entailment.EntailmentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_channel, v);}
    
  
 
  /** @generated */
  final Feature casFeat_origin;
  /** @generated */
  final int     casFeatCode_origin;
  /** @generated */ 
  public String getOrigin(int addr) {
        if (featOkTst && casFeat_origin == null)
      jcas.throwFeatMissing("origin", "eu.excitement.type.entailment.EntailmentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_origin);
  }
  /** @generated */    
  public void setOrigin(int addr, String v) {
        if (featOkTst && casFeat_origin == null)
      jcas.throwFeatMissing("origin", "eu.excitement.type.entailment.EntailmentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_origin, v);}
    
  
 
  /** @generated */
  final Feature casFeat_TextDocumentID;
  /** @generated */
  final int     casFeatCode_TextDocumentID;
  /** @generated */ 
  public String getTextDocumentID(int addr) {
        if (featOkTst && casFeat_TextDocumentID == null)
      jcas.throwFeatMissing("TextDocumentID", "eu.excitement.type.entailment.EntailmentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TextDocumentID);
  }
  /** @generated */    
  public void setTextDocumentID(int addr, String v) {
        if (featOkTst && casFeat_TextDocumentID == null)
      jcas.throwFeatMissing("TextDocumentID", "eu.excitement.type.entailment.EntailmentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_TextDocumentID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_TextCollectionID;
  /** @generated */
  final int     casFeatCode_TextCollectionID;
  /** @generated */ 
  public String getTextCollectionID(int addr) {
        if (featOkTst && casFeat_TextCollectionID == null)
      jcas.throwFeatMissing("TextCollectionID", "eu.excitement.type.entailment.EntailmentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TextCollectionID);
  }
  /** @generated */    
  public void setTextCollectionID(int addr, String v) {
        if (featOkTst && casFeat_TextCollectionID == null)
      jcas.throwFeatMissing("TextCollectionID", "eu.excitement.type.entailment.EntailmentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_TextCollectionID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_HypothesisDocumentID;
  /** @generated */
  final int     casFeatCode_HypothesisDocumentID;
  /** @generated */ 
  public String getHypothesisDocumentID(int addr) {
        if (featOkTst && casFeat_HypothesisDocumentID == null)
      jcas.throwFeatMissing("HypothesisDocumentID", "eu.excitement.type.entailment.EntailmentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_HypothesisDocumentID);
  }
  /** @generated */    
  public void setHypothesisDocumentID(int addr, String v) {
        if (featOkTst && casFeat_HypothesisDocumentID == null)
      jcas.throwFeatMissing("HypothesisDocumentID", "eu.excitement.type.entailment.EntailmentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_HypothesisDocumentID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_HypothesisCollectionID;
  /** @generated */
  final int     casFeatCode_HypothesisCollectionID;
  /** @generated */ 
  public String getHypothesisCollectionID(int addr) {
        if (featOkTst && casFeat_HypothesisCollectionID == null)
      jcas.throwFeatMissing("HypothesisCollectionID", "eu.excitement.type.entailment.EntailmentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_HypothesisCollectionID);
  }
  /** @generated */    
  public void setHypothesisCollectionID(int addr, String v) {
        if (featOkTst && casFeat_HypothesisCollectionID == null)
      jcas.throwFeatMissing("HypothesisCollectionID", "eu.excitement.type.entailment.EntailmentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_HypothesisCollectionID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_language;
  /** @generated */
  final int     casFeatCode_language;
  /** @generated */ 
  public String getLanguage(int addr) {
        if (featOkTst && casFeat_language == null)
      jcas.throwFeatMissing("language", "eu.excitement.type.entailment.EntailmentMetadata");
    return ll_cas.ll_getStringValue(addr, casFeatCode_language);
  }
  /** @generated */    
  public void setLanguage(int addr, String v) {
        if (featOkTst && casFeat_language == null)
      jcas.throwFeatMissing("language", "eu.excitement.type.entailment.EntailmentMetadata");
    ll_cas.ll_setStringValue(addr, casFeatCode_language, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public EntailmentMetadata_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_task = jcas.getRequiredFeatureDE(casType, "task", "uima.cas.String", featOkTst);
    casFeatCode_task  = (null == casFeat_task) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_task).getCode();

 
    casFeat_channel = jcas.getRequiredFeatureDE(casType, "channel", "uima.cas.String", featOkTst);
    casFeatCode_channel  = (null == casFeat_channel) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_channel).getCode();

 
    casFeat_origin = jcas.getRequiredFeatureDE(casType, "origin", "uima.cas.String", featOkTst);
    casFeatCode_origin  = (null == casFeat_origin) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_origin).getCode();

 
    casFeat_TextDocumentID = jcas.getRequiredFeatureDE(casType, "TextDocumentID", "uima.cas.String", featOkTst);
    casFeatCode_TextDocumentID  = (null == casFeat_TextDocumentID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TextDocumentID).getCode();

 
    casFeat_TextCollectionID = jcas.getRequiredFeatureDE(casType, "TextCollectionID", "uima.cas.String", featOkTst);
    casFeatCode_TextCollectionID  = (null == casFeat_TextCollectionID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TextCollectionID).getCode();

 
    casFeat_HypothesisDocumentID = jcas.getRequiredFeatureDE(casType, "HypothesisDocumentID", "uima.cas.String", featOkTst);
    casFeatCode_HypothesisDocumentID  = (null == casFeat_HypothesisDocumentID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_HypothesisDocumentID).getCode();

 
    casFeat_HypothesisCollectionID = jcas.getRequiredFeatureDE(casType, "HypothesisCollectionID", "uima.cas.String", featOkTst);
    casFeatCode_HypothesisCollectionID  = (null == casFeat_HypothesisCollectionID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_HypothesisCollectionID).getCode();

 
    casFeat_language = jcas.getRequiredFeatureDE(casType, "language", "uima.cas.String", featOkTst);
    casFeatCode_language  = (null == casFeat_language) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_language).getCode();

  }
}



    