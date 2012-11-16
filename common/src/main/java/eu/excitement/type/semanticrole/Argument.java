

/* First created by JCasGen Fri Oct 05 20:17:31 CEST 2012 */
package eu.excitement.type.semanticrole;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** It represents an argument (of semantic role labeling). It has two features; the argument's semantic role (type), and a backward reference to the predicate that governs the argument.
 * Updated by JCasGen Fri Oct 05 20:17:31 CEST 2012
 * XML source: /Users/tailblues/progs/github/Excitement-Open-Platform/common/src/main/resources/desc/type/SemanticRole.xml
 * @generated */
public class Argument extends Annotation {
  /** @generated
   * @ordered 
   */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Argument.class);
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
  protected Argument() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Argument(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Argument(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Argument(JCas jcas, int begin, int end) {
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
  //* Feature: argumentName

  /** getter for argumentName - gets This feature represents the semantic role (type) of this argument. The fact that this feature is a String means that arbitrary role labels can be adopted, such as the PropBank argument set (A0, A1, AM-LOC, etc.), the FrameNet role set (Communication.Communicator, Communication.Message, etc.), or any other.
   * @generated */
  public String getArgumentName() {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_argumentName == null)
      jcasType.jcas.throwFeatMissing("argumentName", "eu.excitement.type.semanticrole.Argument");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Argument_Type)jcasType).casFeatCode_argumentName);}
    
  /** setter for argumentName - sets This feature represents the semantic role (type) of this argument. The fact that this feature is a String means that arbitrary role labels can be adopted, such as the PropBank argument set (A0, A1, AM-LOC, etc.), the FrameNet role set (Communication.Communicator, Communication.Message, etc.), or any other. 
   * @generated */
  public void setArgumentName(String v) {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_argumentName == null)
      jcasType.jcas.throwFeatMissing("argumentName", "eu.excitement.type.semanticrole.Argument");
    jcasType.ll_cas.ll_setStringValue(addr, ((Argument_Type)jcasType).casFeatCode_argumentName, v);}    
   
    
  //*--------------*
  //* Feature: predicates

  /** getter for predicates - gets This feature is an array of semanticrole.Predicate. (Backward) references to the predicates which governs it.
   * @generated */
  public FSArray getPredicates() {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_predicates == null)
      jcasType.jcas.throwFeatMissing("predicates", "eu.excitement.type.semanticrole.Argument");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_predicates)));}
    
  /** setter for predicates - sets This feature is an array of semanticrole.Predicate. (Backward) references to the predicates which governs it. 
   * @generated */
  public void setPredicates(FSArray v) {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_predicates == null)
      jcasType.jcas.throwFeatMissing("predicates", "eu.excitement.type.semanticrole.Argument");
    jcasType.ll_cas.ll_setRefValue(addr, ((Argument_Type)jcasType).casFeatCode_predicates, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for predicates - gets an indexed value - This feature is an array of semanticrole.Predicate. (Backward) references to the predicates which governs it.
   * @generated */
  public Predicate getPredicates(int i) {
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_predicates == null)
      jcasType.jcas.throwFeatMissing("predicates", "eu.excitement.type.semanticrole.Argument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_predicates), i);
    return (Predicate)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_predicates), i)));}

  /** indexed setter for predicates - sets an indexed value - This feature is an array of semanticrole.Predicate. (Backward) references to the predicates which governs it.
   * @generated */
  public void setPredicates(int i, Predicate v) { 
    if (Argument_Type.featOkTst && ((Argument_Type)jcasType).casFeat_predicates == null)
      jcasType.jcas.throwFeatMissing("predicates", "eu.excitement.type.semanticrole.Argument");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_predicates), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Argument_Type)jcasType).casFeatCode_predicates), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    