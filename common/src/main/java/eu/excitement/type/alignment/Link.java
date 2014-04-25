

/* First created by JCasGen Thu Apr 24 15:21:08 CEST 2014 */
package eu.excitement.type.alignment;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** - CAS type that links two Target. 
- Multi-view type: a Link connects one target in T (TextView), the other target in H (HypothesisView). 
- Three features: "from" (alignment.Target), "to" (alignment.Target), and "strength" (double). 

The semantic of a "Link" is: The text (or structure) pointed by "from" has a relation of "type" to the text (or structure) pointed in "to". 

 We make no assumptions regarding what annotations are aligned by Link and Target types. One Target can be linked by an arbitrary number of Link, also a Target can group an arbitrary number of Annotations. Note that uima.tcas.Annotation is the super type of almost all CAS annotation data. Since a Target can group Annotation, it can group any type of annotations in CAS. 
 * Updated by JCasGen Thu Apr 24 15:21:08 CEST 2014
 * XML source: /home/tailblues/progs/Excitement-Open-Platform/common/src/main/resources/desc/type/AlignmentTypes.xml
 * @generated */
public class Link extends Annotation {
  /** @generated
   * @ordered 
   */
//  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Link.class);
  /** @generated
   * @ordered 
   */
//  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Link() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Link(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Link(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Link(JCas jcas, int begin, int end) {
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
  //* Feature: from

  /** getter for from - gets This feature points one Target. Mandatory value, and should not be null. The semantic of a "Link" is: The text (or structure) pointed by "from" has a relation of "type" to the text (or structure) pointed in "to". 
   * @generated */
  public Target getFrom() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_from == null)
      jcasType.jcas.throwFeatMissing("from", "eu.excitement.type.alignment.Link");
    return (Target)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Link_Type)jcasType).casFeatCode_from)));}
    
  /** setter for from - sets This feature points one Target. Mandatory value, and should not be null. The semantic of a "Link" is: The text (or structure) pointed by "from" has a relation of "type" to the text (or structure) pointed in "to".  
   * @generated */
  public void setFrom(Target v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_from == null)
      jcasType.jcas.throwFeatMissing("from", "eu.excitement.type.alignment.Link");
    jcasType.ll_cas.ll_setRefValue(addr, ((Link_Type)jcasType).casFeatCode_from, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: to

  /** getter for to - gets This feature points one Target. Mandatory value, and should not be null. The semantic of a "Link" is: The text (or structure) pointed by "from" has a relation of "type" to the text (or structure) pointed in "to". 
   * @generated */
  public Target getTo() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_to == null)
      jcasType.jcas.throwFeatMissing("to", "eu.excitement.type.alignment.Link");
    return (Target)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Link_Type)jcasType).casFeatCode_to)));}
    
  /** setter for to - sets This feature points one Target. Mandatory value, and should not be null. The semantic of a "Link" is: The text (or structure) pointed by "from" has a relation of "type" to the text (or structure) pointed in "to".  
   * @generated */
  public void setTo(Target v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_to == null)
      jcasType.jcas.throwFeatMissing("to", "eu.excitement.type.alignment.Link");
    jcasType.ll_cas.ll_setRefValue(addr, ((Link_Type)jcasType).casFeatCode_to, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: strength

  /** getter for strength - gets This feature keeps one double (numerical) value. Mandatory value, and should not be null. The value indicates the strength of the relation. 
   * @generated */
  public double getStrength() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_strength == null)
      jcasType.jcas.throwFeatMissing("strength", "eu.excitement.type.alignment.Link");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Link_Type)jcasType).casFeatCode_strength);}
    
  /** setter for strength - sets This feature keeps one double (numerical) value. Mandatory value, and should not be null. The value indicates the strength of the relation.  
   * @generated */
  public void setStrength(double v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_strength == null)
      jcasType.jcas.throwFeatMissing("strength", "eu.excitement.type.alignment.Link");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Link_Type)jcasType).casFeatCode_strength, v);}    
  }

    