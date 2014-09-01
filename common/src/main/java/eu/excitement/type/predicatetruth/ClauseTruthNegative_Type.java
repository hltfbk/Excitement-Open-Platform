
/* First created by JCasGen Mon Jul 14 22:26:14 IDT 2014 */
package eu.excitement.type.predicatetruth;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** 
 * Updated by JCasGen Tue Jul 15 10:01:13 IDT 2014
 * @generated */
public class ClauseTruthNegative_Type extends ClauseTruth_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ClauseTruthNegative_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ClauseTruthNegative_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ClauseTruthNegative(addr, ClauseTruthNegative_Type.this);
  			   ClauseTruthNegative_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ClauseTruthNegative(addr, ClauseTruthNegative_Type.this);
  	  }
    };
  /** @generated */
  // @SuppressWarnings ("hiding")
  public final static int typeIndexID = ClauseTruthNegative.typeIndexID;
  /** @generated 
     @modifiable */
  // @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("eu.excitement.type.predicatetruth.ClauseTruthNegative");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ClauseTruthNegative_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    