
/* First created by JCasGen Wed Sep 13 18:11:14 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.api.segmentation.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** This type represents a linking morpheme between two CompoundParts.
 * Updated by JCasGen Wed Sep 13 18:11:14 CEST 2017
 * @generated */
public class LinkingMorpheme_Type extends Split_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = LinkingMorpheme.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.LinkingMorpheme");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public LinkingMorpheme_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    