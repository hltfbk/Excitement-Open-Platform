
/* First created by JCasGen Wed Sep 13 18:11:14 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Morphological categories that can be attached to tokens.
 * Updated by JCasGen Wed Sep 13 18:11:14 CEST 2017
 * @generated */
public class MorphologicalFeatures_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = MorphologicalFeatures.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
 
  /** @generated */
  final Feature casFeat_gender;
  /** @generated */
  final int     casFeatCode_gender;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getGender(int addr) {
        if (featOkTst && casFeat_gender == null)
      jcas.throwFeatMissing("gender", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_gender);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setGender(int addr, String v) {
        if (featOkTst && casFeat_gender == null)
      jcas.throwFeatMissing("gender", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_gender, v);}
    
  
 
  /** @generated */
  final Feature casFeat_number;
  /** @generated */
  final int     casFeatCode_number;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNumber(int addr) {
        if (featOkTst && casFeat_number == null)
      jcas.throwFeatMissing("number", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_number);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNumber(int addr, String v) {
        if (featOkTst && casFeat_number == null)
      jcas.throwFeatMissing("number", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_number, v);}
    
  
 
  /** @generated */
  final Feature casFeat_case;
  /** @generated */
  final int     casFeatCode_case;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCase(int addr) {
        if (featOkTst && casFeat_case == null)
      jcas.throwFeatMissing("case", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_case);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCase(int addr, String v) {
        if (featOkTst && casFeat_case == null)
      jcas.throwFeatMissing("case", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_case, v);}
    
  
 
  /** @generated */
  final Feature casFeat_degree;
  /** @generated */
  final int     casFeatCode_degree;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDegree(int addr) {
        if (featOkTst && casFeat_degree == null)
      jcas.throwFeatMissing("degree", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_degree);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDegree(int addr, String v) {
        if (featOkTst && casFeat_degree == null)
      jcas.throwFeatMissing("degree", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_degree, v);}
    
  
 
  /** @generated */
  final Feature casFeat_verbForm;
  /** @generated */
  final int     casFeatCode_verbForm;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getVerbForm(int addr) {
        if (featOkTst && casFeat_verbForm == null)
      jcas.throwFeatMissing("verbForm", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_verbForm);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setVerbForm(int addr, String v) {
        if (featOkTst && casFeat_verbForm == null)
      jcas.throwFeatMissing("verbForm", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_verbForm, v);}
    
  
 
  /** @generated */
  final Feature casFeat_tense;
  /** @generated */
  final int     casFeatCode_tense;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTense(int addr) {
        if (featOkTst && casFeat_tense == null)
      jcas.throwFeatMissing("tense", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_tense);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTense(int addr, String v) {
        if (featOkTst && casFeat_tense == null)
      jcas.throwFeatMissing("tense", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_tense, v);}
    
  
 
  /** @generated */
  final Feature casFeat_mood;
  /** @generated */
  final int     casFeatCode_mood;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getMood(int addr) {
        if (featOkTst && casFeat_mood == null)
      jcas.throwFeatMissing("mood", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_mood);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMood(int addr, String v) {
        if (featOkTst && casFeat_mood == null)
      jcas.throwFeatMissing("mood", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_mood, v);}
    
  
 
  /** @generated */
  final Feature casFeat_voice;
  /** @generated */
  final int     casFeatCode_voice;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getVoice(int addr) {
        if (featOkTst && casFeat_voice == null)
      jcas.throwFeatMissing("voice", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_voice);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setVoice(int addr, String v) {
        if (featOkTst && casFeat_voice == null)
      jcas.throwFeatMissing("voice", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_voice, v);}
    
  
 
  /** @generated */
  final Feature casFeat_definiteness;
  /** @generated */
  final int     casFeatCode_definiteness;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDefiniteness(int addr) {
        if (featOkTst && casFeat_definiteness == null)
      jcas.throwFeatMissing("definiteness", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_definiteness);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDefiniteness(int addr, String v) {
        if (featOkTst && casFeat_definiteness == null)
      jcas.throwFeatMissing("definiteness", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_definiteness, v);}
    
  
 
  /** @generated */
  final Feature casFeat_value;
  /** @generated */
  final int     casFeatCode_value;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getValue(int addr) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_value);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setValue(int addr, String v) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_value, v);}
    
  
 
  /** @generated */
  final Feature casFeat_person;
  /** @generated */
  final int     casFeatCode_person;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPerson(int addr) {
        if (featOkTst && casFeat_person == null)
      jcas.throwFeatMissing("person", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_person);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPerson(int addr, String v) {
        if (featOkTst && casFeat_person == null)
      jcas.throwFeatMissing("person", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_person, v);}
    
  
 
  /** @generated */
  final Feature casFeat_aspect;
  /** @generated */
  final int     casFeatCode_aspect;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAspect(int addr) {
        if (featOkTst && casFeat_aspect == null)
      jcas.throwFeatMissing("aspect", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_aspect);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAspect(int addr, String v) {
        if (featOkTst && casFeat_aspect == null)
      jcas.throwFeatMissing("aspect", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_aspect, v);}
    
  
 
  /** @generated */
  final Feature casFeat_animacy;
  /** @generated */
  final int     casFeatCode_animacy;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAnimacy(int addr) {
        if (featOkTst && casFeat_animacy == null)
      jcas.throwFeatMissing("animacy", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_animacy);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAnimacy(int addr, String v) {
        if (featOkTst && casFeat_animacy == null)
      jcas.throwFeatMissing("animacy", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_animacy, v);}
    
  
 
  /** @generated */
  final Feature casFeat_negative;
  /** @generated */
  final int     casFeatCode_negative;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNegative(int addr) {
        if (featOkTst && casFeat_negative == null)
      jcas.throwFeatMissing("negative", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_negative);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNegative(int addr, String v) {
        if (featOkTst && casFeat_negative == null)
      jcas.throwFeatMissing("negative", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_negative, v);}
    
  
 
  /** @generated */
  final Feature casFeat_numType;
  /** @generated */
  final int     casFeatCode_numType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNumType(int addr) {
        if (featOkTst && casFeat_numType == null)
      jcas.throwFeatMissing("numType", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_numType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNumType(int addr, String v) {
        if (featOkTst && casFeat_numType == null)
      jcas.throwFeatMissing("numType", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_numType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_possessive;
  /** @generated */
  final int     casFeatCode_possessive;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPossessive(int addr) {
        if (featOkTst && casFeat_possessive == null)
      jcas.throwFeatMissing("possessive", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_possessive);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPossessive(int addr, String v) {
        if (featOkTst && casFeat_possessive == null)
      jcas.throwFeatMissing("possessive", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_possessive, v);}
    
  
 
  /** @generated */
  final Feature casFeat_pronType;
  /** @generated */
  final int     casFeatCode_pronType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPronType(int addr) {
        if (featOkTst && casFeat_pronType == null)
      jcas.throwFeatMissing("pronType", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_pronType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPronType(int addr, String v) {
        if (featOkTst && casFeat_pronType == null)
      jcas.throwFeatMissing("pronType", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_pronType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_reflex;
  /** @generated */
  final int     casFeatCode_reflex;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getReflex(int addr) {
        if (featOkTst && casFeat_reflex == null)
      jcas.throwFeatMissing("reflex", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    return ll_cas.ll_getStringValue(addr, casFeatCode_reflex);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setReflex(int addr, String v) {
        if (featOkTst && casFeat_reflex == null)
      jcas.throwFeatMissing("reflex", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures");
    ll_cas.ll_setStringValue(addr, casFeatCode_reflex, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public MorphologicalFeatures_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_gender = jcas.getRequiredFeatureDE(casType, "gender", "uima.cas.String", featOkTst);
    casFeatCode_gender  = (null == casFeat_gender) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_gender).getCode();

 
    casFeat_number = jcas.getRequiredFeatureDE(casType, "number", "uima.cas.String", featOkTst);
    casFeatCode_number  = (null == casFeat_number) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_number).getCode();

 
    casFeat_case = jcas.getRequiredFeatureDE(casType, "case", "uima.cas.String", featOkTst);
    casFeatCode_case  = (null == casFeat_case) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_case).getCode();

 
    casFeat_degree = jcas.getRequiredFeatureDE(casType, "degree", "uima.cas.String", featOkTst);
    casFeatCode_degree  = (null == casFeat_degree) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_degree).getCode();

 
    casFeat_verbForm = jcas.getRequiredFeatureDE(casType, "verbForm", "uima.cas.String", featOkTst);
    casFeatCode_verbForm  = (null == casFeat_verbForm) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_verbForm).getCode();

 
    casFeat_tense = jcas.getRequiredFeatureDE(casType, "tense", "uima.cas.String", featOkTst);
    casFeatCode_tense  = (null == casFeat_tense) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_tense).getCode();

 
    casFeat_mood = jcas.getRequiredFeatureDE(casType, "mood", "uima.cas.String", featOkTst);
    casFeatCode_mood  = (null == casFeat_mood) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_mood).getCode();

 
    casFeat_voice = jcas.getRequiredFeatureDE(casType, "voice", "uima.cas.String", featOkTst);
    casFeatCode_voice  = (null == casFeat_voice) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_voice).getCode();

 
    casFeat_definiteness = jcas.getRequiredFeatureDE(casType, "definiteness", "uima.cas.String", featOkTst);
    casFeatCode_definiteness  = (null == casFeat_definiteness) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_definiteness).getCode();

 
    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.String", featOkTst);
    casFeatCode_value  = (null == casFeat_value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_value).getCode();

 
    casFeat_person = jcas.getRequiredFeatureDE(casType, "person", "uima.cas.String", featOkTst);
    casFeatCode_person  = (null == casFeat_person) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_person).getCode();

 
    casFeat_aspect = jcas.getRequiredFeatureDE(casType, "aspect", "uima.cas.String", featOkTst);
    casFeatCode_aspect  = (null == casFeat_aspect) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_aspect).getCode();

 
    casFeat_animacy = jcas.getRequiredFeatureDE(casType, "animacy", "uima.cas.String", featOkTst);
    casFeatCode_animacy  = (null == casFeat_animacy) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_animacy).getCode();

 
    casFeat_negative = jcas.getRequiredFeatureDE(casType, "negative", "uima.cas.String", featOkTst);
    casFeatCode_negative  = (null == casFeat_negative) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_negative).getCode();

 
    casFeat_numType = jcas.getRequiredFeatureDE(casType, "numType", "uima.cas.String", featOkTst);
    casFeatCode_numType  = (null == casFeat_numType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_numType).getCode();

 
    casFeat_possessive = jcas.getRequiredFeatureDE(casType, "possessive", "uima.cas.String", featOkTst);
    casFeatCode_possessive  = (null == casFeat_possessive) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_possessive).getCode();

 
    casFeat_pronType = jcas.getRequiredFeatureDE(casType, "pronType", "uima.cas.String", featOkTst);
    casFeatCode_pronType  = (null == casFeat_pronType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pronType).getCode();

 
    casFeat_reflex = jcas.getRequiredFeatureDE(casType, "reflex", "uima.cas.String", featOkTst);
    casFeatCode_reflex  = (null == casFeat_reflex) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_reflex).getCode();

  }
}



    