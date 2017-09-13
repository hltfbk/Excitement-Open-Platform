

/* First created by JCasGen Wed Sep 13 18:11:14 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Sep 13 18:11:14 CEST 2017
 * XML source: /home/nira/informiz/Excitement-Open-Platform/common/target/jcasgen/typesystem.xml
 * @generated */
public class Morpheme extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Morpheme.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Morpheme() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Morpheme(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Morpheme(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Morpheme(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: morphTag

  /** getter for morphTag - gets 
   * @generated
   * @return value of the feature 
   */
  public String getMorphTag() {
    if (Morpheme_Type.featOkTst && ((Morpheme_Type)jcasType).casFeat_morphTag == null)
      jcasType.jcas.throwFeatMissing("morphTag", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morpheme");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Morpheme_Type)jcasType).casFeatCode_morphTag);}
    
  /** setter for morphTag - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setMorphTag(String v) {
    if (Morpheme_Type.featOkTst && ((Morpheme_Type)jcasType).casFeat_morphTag == null)
      jcasType.jcas.throwFeatMissing("morphTag", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morpheme");
    jcasType.ll_cas.ll_setStringValue(addr, ((Morpheme_Type)jcasType).casFeatCode_morphTag, v);}    
  }

    