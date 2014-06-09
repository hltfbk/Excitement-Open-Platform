
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

/** - CAS type that links two Target. 
- Multi-view type: a Link connects one target in T (TextView), the other target in H (HypothesisView). 

The semantic of a "Link" is: The texts (or structures) pointed by "TSideTarget" and "HSideTarget" have a relation of "type", with the direction of "direction",  on a strength of "strength". 

We make no assumptions regarding what annotations are aligned by Link and Target types. One Target can be linked by an arbitrary number of Link, also a Target can group an arbitrary number of Annotations. Note that uima.tcas.Annotation is the super type of almost all CAS annotation data. Since a Target can group Annotation, it can group any type of annotations in CAS.

Some notes on Link type usage. (Indexing and setting begin - end) 
- A Link instance should be indexed on the Hypothesis View. So one iteration over the Hypothesis view can get all alignment links.
- begin and end : both span value should hold the same value to that of HSide Target
 * Updated by JCasGen Wed May 14 14:20:19 CEST 2014
 * @generated */
public class Link_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Link_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Link_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Link(addr, Link_Type.this);
  			   Link_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Link(addr, Link_Type.this);
  	  }
    };
  /** @generated */
//  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Link.typeIndexID;
  /** @generated 
     @modifiable */
//  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("eu.excitement.type.alignment.Link");
 
  /** @generated */
  final Feature casFeat_TSideTarget;
  /** @generated */
  final int     casFeatCode_TSideTarget;
  /** @generated */ 
  public int getTSideTarget(int addr) {
        if (featOkTst && casFeat_TSideTarget == null)
      jcas.throwFeatMissing("TSideTarget", "eu.excitement.type.alignment.Link");
    return ll_cas.ll_getRefValue(addr, casFeatCode_TSideTarget);
  }
  /** @generated */    
  public void setTSideTarget(int addr, int v) {
        if (featOkTst && casFeat_TSideTarget == null)
      jcas.throwFeatMissing("TSideTarget", "eu.excitement.type.alignment.Link");
    ll_cas.ll_setRefValue(addr, casFeatCode_TSideTarget, v);}
    
  
 
  /** @generated */
  final Feature casFeat_HSideTarget;
  /** @generated */
  final int     casFeatCode_HSideTarget;
  /** @generated */ 
  public int getHSideTarget(int addr) {
        if (featOkTst && casFeat_HSideTarget == null)
      jcas.throwFeatMissing("HSideTarget", "eu.excitement.type.alignment.Link");
    return ll_cas.ll_getRefValue(addr, casFeatCode_HSideTarget);
  }
  /** @generated */    
  public void setHSideTarget(int addr, int v) {
        if (featOkTst && casFeat_HSideTarget == null)
      jcas.throwFeatMissing("HSideTarget", "eu.excitement.type.alignment.Link");
    ll_cas.ll_setRefValue(addr, casFeatCode_HSideTarget, v);}
    
  
 
  /** @generated */
  final Feature casFeat_strength;
  /** @generated */
  final int     casFeatCode_strength;
  /** @generated */ 
  public double getStrength(int addr) {
        if (featOkTst && casFeat_strength == null)
      jcas.throwFeatMissing("strength", "eu.excitement.type.alignment.Link");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_strength);
  }
  /** @generated */    
  public void setStrength(int addr, double v) {
        if (featOkTst && casFeat_strength == null)
      jcas.throwFeatMissing("strength", "eu.excitement.type.alignment.Link");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_strength, v);}
    
  
 
  /** @generated */
  final Feature casFeat_directionString;
  /** @generated */
  final int     casFeatCode_directionString;
  /** @generated */ 
  public String getDirectionString(int addr) {
        if (featOkTst && casFeat_directionString == null)
      jcas.throwFeatMissing("directionString", "eu.excitement.type.alignment.Link");
    return ll_cas.ll_getStringValue(addr, casFeatCode_directionString);
  }
  /** @generated */    
  public void setDirectionString(int addr, String v) {
        if (featOkTst && casFeat_directionString == null)
      jcas.throwFeatMissing("directionString", "eu.excitement.type.alignment.Link");
    ll_cas.ll_setStringValue(addr, casFeatCode_directionString, v);}
    
  
 
  /** @generated */
  final Feature casFeat_alignerID;
  /** @generated */
  final int     casFeatCode_alignerID;
  /** @generated */ 
  public String getAlignerID(int addr) {
        if (featOkTst && casFeat_alignerID == null)
      jcas.throwFeatMissing("alignerID", "eu.excitement.type.alignment.Link");
    return ll_cas.ll_getStringValue(addr, casFeatCode_alignerID);
  }
  /** @generated */    
  public void setAlignerID(int addr, String v) {
        if (featOkTst && casFeat_alignerID == null)
      jcas.throwFeatMissing("alignerID", "eu.excitement.type.alignment.Link");
    ll_cas.ll_setStringValue(addr, casFeatCode_alignerID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_alignerVersion;
  /** @generated */
  final int     casFeatCode_alignerVersion;
  /** @generated */ 
  public String getAlignerVersion(int addr) {
        if (featOkTst && casFeat_alignerVersion == null)
      jcas.throwFeatMissing("alignerVersion", "eu.excitement.type.alignment.Link");
    return ll_cas.ll_getStringValue(addr, casFeatCode_alignerVersion);
  }
  /** @generated */    
  public void setAlignerVersion(int addr, String v) {
        if (featOkTst && casFeat_alignerVersion == null)
      jcas.throwFeatMissing("alignerVersion", "eu.excitement.type.alignment.Link");
    ll_cas.ll_setStringValue(addr, casFeatCode_alignerVersion, v);}
    
  
 
  /** @generated */
  final Feature casFeat_linkInfo;
  /** @generated */
  final int     casFeatCode_linkInfo;
  /** @generated */ 
  public String getLinkInfo(int addr) {
        if (featOkTst && casFeat_linkInfo == null)
      jcas.throwFeatMissing("linkInfo", "eu.excitement.type.alignment.Link");
    return ll_cas.ll_getStringValue(addr, casFeatCode_linkInfo);
  }
  /** @generated */    
  public void setLinkInfo(int addr, String v) {
        if (featOkTst && casFeat_linkInfo == null)
      jcas.throwFeatMissing("linkInfo", "eu.excitement.type.alignment.Link");
    ll_cas.ll_setStringValue(addr, casFeatCode_linkInfo, v);}
    
  
 
  /** @generated */
  final Feature casFeat_groupLabel;
  /** @generated */
  final int     casFeatCode_groupLabel;
  /** @generated */ 
  public int getGroupLabel(int addr) {
        if (featOkTst && casFeat_groupLabel == null)
      jcas.throwFeatMissing("groupLabel", "eu.excitement.type.alignment.Link");
    return ll_cas.ll_getRefValue(addr, casFeatCode_groupLabel);
  }
  /** @generated */    
  public void setGroupLabel(int addr, int v) {
        if (featOkTst && casFeat_groupLabel == null)
      jcas.throwFeatMissing("groupLabel", "eu.excitement.type.alignment.Link");
    ll_cas.ll_setRefValue(addr, casFeatCode_groupLabel, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Link_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_TSideTarget = jcas.getRequiredFeatureDE(casType, "TSideTarget", "eu.excitement.type.alignment.Target", featOkTst);
    casFeatCode_TSideTarget  = (null == casFeat_TSideTarget) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TSideTarget).getCode();

 
    casFeat_HSideTarget = jcas.getRequiredFeatureDE(casType, "HSideTarget", "eu.excitement.type.alignment.Target", featOkTst);
    casFeatCode_HSideTarget  = (null == casFeat_HSideTarget) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_HSideTarget).getCode();

 
    casFeat_strength = jcas.getRequiredFeatureDE(casType, "strength", "uima.cas.Double", featOkTst);
    casFeatCode_strength  = (null == casFeat_strength) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_strength).getCode();

 
    casFeat_directionString = jcas.getRequiredFeatureDE(casType, "directionString", "eu.excitement.type.alignment.Direction", featOkTst);
    casFeatCode_directionString  = (null == casFeat_directionString) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_directionString).getCode();

 
    casFeat_alignerID = jcas.getRequiredFeatureDE(casType, "alignerID", "uima.cas.String", featOkTst);
    casFeatCode_alignerID  = (null == casFeat_alignerID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_alignerID).getCode();

 
    casFeat_alignerVersion = jcas.getRequiredFeatureDE(casType, "alignerVersion", "uima.cas.String", featOkTst);
    casFeatCode_alignerVersion  = (null == casFeat_alignerVersion) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_alignerVersion).getCode();

 
    casFeat_linkInfo = jcas.getRequiredFeatureDE(casType, "linkInfo", "uima.cas.String", featOkTst);
    casFeatCode_linkInfo  = (null == casFeat_linkInfo) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_linkInfo).getCode();

 
    casFeat_groupLabel = jcas.getRequiredFeatureDE(casType, "groupLabel", "uima.cas.StringList", featOkTst);
    casFeatCode_groupLabel  = (null == casFeat_groupLabel) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_groupLabel).getCode();

  }
}



    