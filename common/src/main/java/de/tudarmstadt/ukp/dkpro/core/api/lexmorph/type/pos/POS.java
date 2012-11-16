

/* First created by JCasGen Fri Oct 05 09:56:45 CEST 2012 */
package de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** The part of speech of a word or a phrase.
 * Updated by JCasGen Fri Oct 05 20:17:19 CEST 2012
 * XML source: /Users/tailblues/progs/github/Excitement-Open-Platform/common/src/main/resources/desc/type/POS.xml
 * @generated */
public class POS extends Annotation {
  /** @generated
   * @ordered 
   */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(POS.class);
  /** @generated
   * @ordered 
   */
  //@SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected POS() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public POS(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public POS(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public POS(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: PosValue

  /** getter for PosValue - gets The string representation of POS. Mostly it is the POS
                        representation of the wrapped POS tagger.
   * @generated */
  public String getPosValue() {
    if (POS_Type.featOkTst && ((POS_Type)jcasType).casFeat_PosValue == null)
      jcasType.jcas.throwFeatMissing("PosValue", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS");
    return jcasType.ll_cas.ll_getStringValue(addr, ((POS_Type)jcasType).casFeatCode_PosValue);}
    
  /** setter for PosValue - sets The string representation of POS. Mostly it is the POS
                        representation of the wrapped POS tagger. 
   * @generated */
  public void setPosValue(String v) {
    if (POS_Type.featOkTst && ((POS_Type)jcasType).casFeat_PosValue == null)
      jcasType.jcas.throwFeatMissing("PosValue", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS");
    jcasType.ll_cas.ll_setStringValue(addr, ((POS_Type)jcasType).casFeatCode_PosValue, v);}    
  }

    