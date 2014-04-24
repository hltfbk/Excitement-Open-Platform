
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
- Three features: "from" (alignment.Target), "to" (alignment.Target), and "strength" (double). 

The semantic of a "Link" is: The text (or structure) pointed by "from" has a relation of "type" to the text (or structure) pointed in "to". 

 We make no assumptions regarding what annotations are aligned by Link and Target types. One Target can be linked by an arbitrary number of Link, also a Target can group an arbitrary number of Annotations. Note that uima.tcas.Annotation is the super type of almost all CAS annotation data. Since a Target can group Annotation, it can group any type of annotations in CAS. 
 * Updated by JCasGen Thu Apr 24 15:21:08 CEST 2014
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
  final Feature casFeat_from;
  /** @generated */
  final int     casFeatCode_from;
  /** @generated */ 
  public int getFrom(int addr) {
        if (featOkTst && casFeat_from == null)
      jcas.throwFeatMissing("from", "eu.excitement.type.alignment.Link");
    return ll_cas.ll_getRefValue(addr, casFeatCode_from);
  }
  /** @generated */    
  public void setFrom(int addr, int v) {
        if (featOkTst && casFeat_from == null)
      jcas.throwFeatMissing("from", "eu.excitement.type.alignment.Link");
    ll_cas.ll_setRefValue(addr, casFeatCode_from, v);}
    
  
 
  /** @generated */
  final Feature casFeat_to;
  /** @generated */
  final int     casFeatCode_to;
  /** @generated */ 
  public int getTo(int addr) {
        if (featOkTst && casFeat_to == null)
      jcas.throwFeatMissing("to", "eu.excitement.type.alignment.Link");
    return ll_cas.ll_getRefValue(addr, casFeatCode_to);
  }
  /** @generated */    
  public void setTo(int addr, int v) {
        if (featOkTst && casFeat_to == null)
      jcas.throwFeatMissing("to", "eu.excitement.type.alignment.Link");
    ll_cas.ll_setRefValue(addr, casFeatCode_to, v);}
    
  
 
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
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Link_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_from = jcas.getRequiredFeatureDE(casType, "from", "eu.excitement.type.alignment.Target", featOkTst);
    casFeatCode_from  = (null == casFeat_from) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_from).getCode();

 
    casFeat_to = jcas.getRequiredFeatureDE(casType, "to", "eu.excitement.type.alignment.Target", featOkTst);
    casFeatCode_to  = (null == casFeat_to) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_to).getCode();

 
    casFeat_strength = jcas.getRequiredFeatureDE(casType, "strength", "uima.cas.Double", featOkTst);
    casFeatCode_strength  = (null == casFeat_strength) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_strength).getCode();

  }
}



    