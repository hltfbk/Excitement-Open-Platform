

/* First created by JCasGen Fri Oct 05 20:17:38 CEST 2012 */
package eu.excitement.type.temporal;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** It is anchored to a textual region (a paragraph, or a document), and holds the "default time" that has been determined for this passage and can be useful to interpret relative time expressions ("now", "yesterday") in the text.
 * Updated by JCasGen Fri Oct 05 20:17:38 CEST 2012
 * XML source: /Users/tailblues/progs/github/Excitement-Open-Platform/common/src/main/resources/desc/type/TemporalExpression.xml
 * @generated */
public class DefaultTimeOfText extends Annotation {
  /** @generated
   * @ordered 
   */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DefaultTimeOfText.class);
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
  protected DefaultTimeOfText() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public DefaultTimeOfText(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public DefaultTimeOfText(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public DefaultTimeOfText(JCas jcas, int begin, int end) {
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
  //* Feature: time

  /** getter for time - gets This feature holds the default time for the textual unit which is annotated
by this annotation. The time string is expressed in the normalized ISO 8601 format. More specifically,
it is a concatenation of the ISO 8601 calendar date and extended time: "YYYY-MM-DD hh:mm:ss".
   * @generated */
  public String getTime() {
    if (DefaultTimeOfText_Type.featOkTst && ((DefaultTimeOfText_Type)jcasType).casFeat_time == null)
      jcasType.jcas.throwFeatMissing("time", "eu.excitement.type.temporal.DefaultTimeOfText");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DefaultTimeOfText_Type)jcasType).casFeatCode_time);}
    
  /** setter for time - sets This feature holds the default time for the textual unit which is annotated
by this annotation. The time string is expressed in the normalized ISO 8601 format. More specifically,
it is a concatenation of the ISO 8601 calendar date and extended time: "YYYY-MM-DD hh:mm:ss". 
   * @generated */
  public void setTime(String v) {
    if (DefaultTimeOfText_Type.featOkTst && ((DefaultTimeOfText_Type)jcasType).casFeat_time == null)
      jcasType.jcas.throwFeatMissing("time", "eu.excitement.type.temporal.DefaultTimeOfText");
    jcasType.ll_cas.ll_setStringValue(addr, ((DefaultTimeOfText_Type)jcasType).casFeatCode_time, v);}    
  }

    