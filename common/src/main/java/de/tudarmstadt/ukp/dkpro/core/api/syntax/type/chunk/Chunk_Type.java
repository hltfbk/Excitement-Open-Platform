
/* First created by JCasGen Wed Sep 13 18:11:14 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Wed Sep 13 18:11:14 CEST 2017
 * @generated */
public class Chunk_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Chunk.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk");
 
  /** @generated */
  final Feature casFeat_chunkValue;
  /** @generated */
  final int     casFeatCode_chunkValue;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getChunkValue(int addr) {
        if (featOkTst && casFeat_chunkValue == null)
      jcas.throwFeatMissing("chunkValue", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk");
    return ll_cas.ll_getStringValue(addr, casFeatCode_chunkValue);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setChunkValue(int addr, String v) {
        if (featOkTst && casFeat_chunkValue == null)
      jcas.throwFeatMissing("chunkValue", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk");
    ll_cas.ll_setStringValue(addr, casFeatCode_chunkValue, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Chunk_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_chunkValue = jcas.getRequiredFeatureDE(casType, "chunkValue", "uima.cas.String", featOkTst);
    casFeatCode_chunkValue  = (null == casFeat_chunkValue) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_chunkValue).getCode();

  }
}



    