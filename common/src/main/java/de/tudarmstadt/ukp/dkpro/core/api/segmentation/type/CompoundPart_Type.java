
/* First created by JCasGen Wed Sep 13 18:11:14 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.api.segmentation.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** <p>A CompoundPart represents one fragment from the compounding word. Besides that, it can store other CompoundParts if it can be split again. The way it stores a decompounding word represents a decompounding tree.</p>
 * Updated by JCasGen Wed Sep 13 18:11:14 CEST 2017
 * @generated */
public class CompoundPart_Type extends Split_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = CompoundPart.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.CompoundPart");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public CompoundPart_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    