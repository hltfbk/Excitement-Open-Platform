

/* First created by JCasGen Wed Sep 13 18:11:14 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Morphological categories that can be attached to tokens.
 * Updated by JCasGen Wed Sep 13 18:11:14 CEST 2017
 * XML source: /home/nira/informiz/Excitement-Open-Platform/common/target/jcasgen/typesystem.xml
 * @generated */
public class MorphologicalFeatures extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(MorphologicalFeatures.class);
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
  protected MorphologicalFeatures() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public MorphologicalFeatures(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public MorphologicalFeatures(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public MorphologicalFeatures(JCas jcas, int begin, int end) {
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
  //* Feature: gender

  /** getter for gender - gets 
   * @generated
   * @return value of the feature 
   */
  public String getGender() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_gender == null)
      jcasType.jcas.throwFeatMissing("gender", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_gender);}
    
  /** setter for gender - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setGender(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_gender == null)
      jcasType.jcas.throwFeatMissing("gender", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_gender, v);}    
   
    
  //*--------------*
  //* Feature: number

  /** getter for number - gets Singular/plural
   * @generated
   * @return value of the feature 
   */
  public String getNumber() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_number == null)
      jcasType.jcas.throwFeatMissing("number", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_number);}
    
  /** setter for number - sets Singular/plural 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNumber(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_number == null)
      jcasType.jcas.throwFeatMissing("number", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_number, v);}    
   
    
  //*--------------*
  //* Feature: case

  /** getter for case - gets Nouns: nominative, genetiv, dative, ...
   * @generated
   * @return value of the feature 
   */
  public String getCase() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_case == null)
      jcasType.jcas.throwFeatMissing("case", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_case);}
    
  /** setter for case - sets Nouns: nominative, genetiv, dative, ... 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCase(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_case == null)
      jcasType.jcas.throwFeatMissing("case", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_case, v);}    
   
    
  //*--------------*
  //* Feature: degree

  /** getter for degree - gets Adjectives: comparative/Superlative
   * @generated
   * @return value of the feature 
   */
  public String getDegree() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_degree == null)
      jcasType.jcas.throwFeatMissing("degree", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_degree);}
    
  /** setter for degree - sets Adjectives: comparative/Superlative 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDegree(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_degree == null)
      jcasType.jcas.throwFeatMissing("degree", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_degree, v);}    
   
    
  //*--------------*
  //* Feature: verbForm

  /** getter for verbForm - gets 
   * @generated
   * @return value of the feature 
   */
  public String getVerbForm() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_verbForm == null)
      jcasType.jcas.throwFeatMissing("verbForm", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_verbForm);}
    
  /** setter for verbForm - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setVerbForm(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_verbForm == null)
      jcasType.jcas.throwFeatMissing("verbForm", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_verbForm, v);}    
   
    
  //*--------------*
  //* Feature: tense

  /** getter for tense - gets Verbs: past tense, present tense, future tense, etc.
   * @generated
   * @return value of the feature 
   */
  public String getTense() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_tense == null)
      jcasType.jcas.throwFeatMissing("tense", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_tense);}
    
  /** setter for tense - sets Verbs: past tense, present tense, future tense, etc. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTense(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_tense == null)
      jcasType.jcas.throwFeatMissing("tense", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_tense, v);}    
   
    
  //*--------------*
  //* Feature: mood

  /** getter for mood - gets Verbs: indicative, imperative, subjunctive
   * @generated
   * @return value of the feature 
   */
  public String getMood() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_mood == null)
      jcasType.jcas.throwFeatMissing("mood", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_mood);}
    
  /** setter for mood - sets Verbs: indicative, imperative, subjunctive 
   * @generated
   * @param v value to set into the feature 
   */
  public void setMood(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_mood == null)
      jcasType.jcas.throwFeatMissing("mood", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_mood, v);}    
   
    
  //*--------------*
  //* Feature: voice

  /** getter for voice - gets Verbs: active/passive
   * @generated
   * @return value of the feature 
   */
  public String getVoice() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_voice == null)
      jcasType.jcas.throwFeatMissing("voice", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_voice);}
    
  /** setter for voice - sets Verbs: active/passive 
   * @generated
   * @param v value to set into the feature 
   */
  public void setVoice(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_voice == null)
      jcasType.jcas.throwFeatMissing("voice", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_voice, v);}    
   
    
  //*--------------*
  //* Feature: definiteness

  /** getter for definiteness - gets Definite or indefinite
   * @generated
   * @return value of the feature 
   */
  public String getDefiniteness() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_definiteness == null)
      jcasType.jcas.throwFeatMissing("definiteness", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_definiteness);}
    
  /** setter for definiteness - sets Definite or indefinite 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDefiniteness(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_definiteness == null)
      jcasType.jcas.throwFeatMissing("definiteness", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_definiteness, v);}    
   
    
  //*--------------*
  //* Feature: value

  /** getter for value - gets The original morphological analysis results as produced by a tool or as recorded in a corpus (if available). If the categories were originally encoded in such a string, the other features are filled by analyzing this string. If the categories were provided separately, e.g. by different attributed in an XML-encoded corpus, this field may remain empty.
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets The original morphological analysis results as produced by a tool or as recorded in a corpus (if available). If the categories were originally encoded in such a string, the other features are filled by analyzing this string. If the categories were provided separately, e.g. by different attributed in an XML-encoded corpus, this field may remain empty. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_value, v);}    
   
    
  //*--------------*
  //* Feature: person

  /** getter for person - gets Verbs: 1st, 2nd, 3rd person
   * @generated
   * @return value of the feature 
   */
  public String getPerson() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_person == null)
      jcasType.jcas.throwFeatMissing("person", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_person);}
    
  /** setter for person - sets Verbs: 1st, 2nd, 3rd person 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPerson(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_person == null)
      jcasType.jcas.throwFeatMissing("person", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_person, v);}    
   
    
  //*--------------*
  //* Feature: aspect

  /** getter for aspect - gets Verbs: perfective, imperfective
   * @generated
   * @return value of the feature 
   */
  public String getAspect() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_aspect == null)
      jcasType.jcas.throwFeatMissing("aspect", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_aspect);}
    
  /** setter for aspect - sets Verbs: perfective, imperfective 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAspect(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_aspect == null)
      jcasType.jcas.throwFeatMissing("aspect", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_aspect, v);}    
   
    
  //*--------------*
  //* Feature: animacy

  /** getter for animacy - gets 
   * @generated
   * @return value of the feature 
   */
  public String getAnimacy() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_animacy == null)
      jcasType.jcas.throwFeatMissing("animacy", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_animacy);}
    
  /** setter for animacy - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnimacy(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_animacy == null)
      jcasType.jcas.throwFeatMissing("animacy", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_animacy, v);}    
   
    
  //*--------------*
  //* Feature: negative

  /** getter for negative - gets 
   * @generated
   * @return value of the feature 
   */
  public String getNegative() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_negative == null)
      jcasType.jcas.throwFeatMissing("negative", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_negative);}
    
  /** setter for negative - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNegative(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_negative == null)
      jcasType.jcas.throwFeatMissing("negative", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_negative, v);}    
   
    
  //*--------------*
  //* Feature: numType

  /** getter for numType - gets 
   * @generated
   * @return value of the feature 
   */
  public String getNumType() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_numType == null)
      jcasType.jcas.throwFeatMissing("numType", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_numType);}
    
  /** setter for numType - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNumType(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_numType == null)
      jcasType.jcas.throwFeatMissing("numType", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_numType, v);}    
   
    
  //*--------------*
  //* Feature: possessive

  /** getter for possessive - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPossessive() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_possessive == null)
      jcasType.jcas.throwFeatMissing("possessive", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_possessive);}
    
  /** setter for possessive - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPossessive(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_possessive == null)
      jcasType.jcas.throwFeatMissing("possessive", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_possessive, v);}    
   
    
  //*--------------*
  //* Feature: pronType

  /** getter for pronType - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPronType() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_pronType == null)
      jcasType.jcas.throwFeatMissing("pronType", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_pronType);}
    
  /** setter for pronType - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPronType(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_pronType == null)
      jcasType.jcas.throwFeatMissing("pronType", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_pronType, v);}    
   
    
  //*--------------*
  //* Feature: reflex

  /** getter for reflex - gets 
   * @generated
   * @return value of the feature 
   */
  public String getReflex() {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_reflex == null)
      jcasType.jcas.throwFeatMissing("reflex", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_reflex);}
    
  /** setter for reflex - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setReflex(String v) {
    if (MorphologicalFeatures_Type.featOkTst && ((MorphologicalFeatures_Type)jcasType).casFeat_reflex == null)
      jcasType.jcas.throwFeatMissing("reflex", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    jcasType.ll_cas.ll_setStringValue(addr, ((MorphologicalFeatures_Type)jcasType).casFeatCode_reflex, v);}    
  }

    