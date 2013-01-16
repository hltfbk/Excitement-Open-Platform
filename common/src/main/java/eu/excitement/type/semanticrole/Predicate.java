

/* First created by JCasGen Fri Oct 05 20:17:31 CEST 2012 */
package eu.excitement.type.semanticrole;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** Predicate is a uima.tcas.Annotation. It represents a predicate. It holds the predicate sense as string and links to its arguments (An array of Argument). It has the following features:
- predicateName (uima.cas.String): This feature represents the name of this predicate. It actually refers to the sense of the predicate in PropBank or FrameNet.
 -arguments (uima.cas.FSArray):This feature is an array of EXCITEMENT.semanticrole.Argument. It holds the predicate's arguments.
 * Updated by JCasGen Fri Oct 05 20:17:31 CEST 2012
 * XML source: /Users/tailblues/progs/github/Excitement-Open-Platform/common/src/main/resources/desc/type/SemanticRole.xml
 * @generated */
public class Predicate extends Annotation {
  /** @generated
   * @ordered 
   */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Predicate.class);
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
  protected Predicate() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Predicate(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Predicate(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Predicate(JCas jcas, int begin, int end) {
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
  //* Feature: predicateName

  /** getter for predicateName - gets This feature represents the name of this predicate. It actually refers to the sense of the predicate in PropBank or FrameNet.
   * @generated */
  public String getPredicateName() {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_predicateName == null)
      jcasType.jcas.throwFeatMissing("predicateName", "eu.excitement.type.semanticrole.Predicate");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Predicate_Type)jcasType).casFeatCode_predicateName);}
    
  /** setter for predicateName - sets This feature represents the name of this predicate. It actually refers to the sense of the predicate in PropBank or FrameNet. 
   * @generated */
  public void setPredicateName(String v) {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_predicateName == null)
      jcasType.jcas.throwFeatMissing("predicateName", "eu.excitement.type.semanticrole.Predicate");
    jcasType.ll_cas.ll_setStringValue(addr, ((Predicate_Type)jcasType).casFeatCode_predicateName, v);}    
   
    
  //*--------------*
  //* Feature: arguments

  /** getter for arguments - gets This feature is an array of semanticrole.Argument. It holds the predicate's arguments.
   * @generated */
  public FSArray getArguments() {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "eu.excitement.type.semanticrole.Predicate");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_arguments)));}
    
  /** setter for arguments - sets This feature is an array of semanticrole.Argument. It holds the predicate's arguments. 
   * @generated */
  public void setArguments(FSArray v) {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "eu.excitement.type.semanticrole.Predicate");
    jcasType.ll_cas.ll_setRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_arguments, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for arguments - gets an indexed value - This feature is an array of semanticrole.Argument. It holds the predicate's arguments.
   * @generated */
  public Argument getArguments(int i) {
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "eu.excitement.type.semanticrole.Predicate");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_arguments), i);
    return (Argument)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_arguments), i)));}

  /** indexed setter for arguments - sets an indexed value - This feature is an array of semanticrole.Argument. It holds the predicate's arguments.
   * @generated */
  public void setArguments(int i, Argument v) { 
    if (Predicate_Type.featOkTst && ((Predicate_Type)jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "eu.excitement.type.semanticrole.Predicate");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_arguments), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Predicate_Type)jcasType).casFeatCode_arguments), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    