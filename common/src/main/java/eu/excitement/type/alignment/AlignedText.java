

/* First created by JCasGen Fri Oct 05 20:17:43 CEST 2012 */
package eu.excitement.type.alignment;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** This type represent an aligned textual unit. Its span refers to the "source" linguistic entity. This can be a token (word alignment), a syntax node (phrase alignments), or a sentence
(sentence alignment).
 * Updated by JCasGen Fri Oct 05 20:17:43 CEST 2012
 * XML source: /Users/tailblues/progs/github/Excitement-Open-Platform/common/src/main/resources/desc/type/TextAlignment.xml
 * @generated */
public class AlignedText extends Annotation {
  /** @generated
   * @ordered 
   */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AlignedText.class);
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
  protected AlignedText() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public AlignedText(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public AlignedText(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public AlignedText(JCas jcas, int begin, int end) {
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
  //* Feature: alignedTo

  /** getter for alignedTo - gets This feature holds references to other AlignedText instances. The array can have multiple references, which means that it is one-to-many alignment. Likewise, a null array can also be a valid value for this feature, if the underlying alignment method is an asymmetric one; empty array means that this AlignedText instance is a recipient, but it does not align itself to other text.
   * @generated */
  public FSArray getAlignedTo() {
    if (AlignedText_Type.featOkTst && ((AlignedText_Type)jcasType).casFeat_alignedTo == null)
      jcasType.jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedText");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedText_Type)jcasType).casFeatCode_alignedTo)));}
    
  /** setter for alignedTo - sets This feature holds references to other AlignedText instances. The array can have multiple references, which means that it is one-to-many alignment. Likewise, a null array can also be a valid value for this feature, if the underlying alignment method is an asymmetric one; empty array means that this AlignedText instance is a recipient, but it does not align itself to other text. 
   * @generated */
  public void setAlignedTo(FSArray v) {
    if (AlignedText_Type.featOkTst && ((AlignedText_Type)jcasType).casFeat_alignedTo == null)
      jcasType.jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedText");
    jcasType.ll_cas.ll_setRefValue(addr, ((AlignedText_Type)jcasType).casFeatCode_alignedTo, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for alignedTo - gets an indexed value - This feature holds references to other AlignedText instances. The array can have multiple references, which means that it is one-to-many alignment. Likewise, a null array can also be a valid value for this feature, if the underlying alignment method is an asymmetric one; empty array means that this AlignedText instance is a recipient, but it does not align itself to other text.
   * @generated */
  public AlignedText getAlignedTo(int i) {
    if (AlignedText_Type.featOkTst && ((AlignedText_Type)jcasType).casFeat_alignedTo == null)
      jcasType.jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedText");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedText_Type)jcasType).casFeatCode_alignedTo), i);
    return (AlignedText)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedText_Type)jcasType).casFeatCode_alignedTo), i)));}

  /** indexed setter for alignedTo - sets an indexed value - This feature holds references to other AlignedText instances. The array can have multiple references, which means that it is one-to-many alignment. Likewise, a null array can also be a valid value for this feature, if the underlying alignment method is an asymmetric one; empty array means that this AlignedText instance is a recipient, but it does not align itself to other text.
   * @generated */
  public void setAlignedTo(int i, AlignedText v) { 
    if (AlignedText_Type.featOkTst && ((AlignedText_Type)jcasType).casFeat_alignedTo == null)
      jcasType.jcas.throwFeatMissing("alignedTo", "eu.excitement.type.alignment.AlignedText");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedText_Type)jcasType).casFeatCode_alignedTo), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AlignedText_Type)jcasType).casFeatCode_alignedTo), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: alignmentType

  /** getter for alignmentType - gets This feature holds additional information for the alignment as a string.
   * @generated */
  public String getAlignmentType() {
    if (AlignedText_Type.featOkTst && ((AlignedText_Type)jcasType).casFeat_alignmentType == null)
      jcasType.jcas.throwFeatMissing("alignmentType", "eu.excitement.type.alignment.AlignedText");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AlignedText_Type)jcasType).casFeatCode_alignmentType);}
    
  /** setter for alignmentType - sets This feature holds additional information for the alignment as a string. 
   * @generated */
  public void setAlignmentType(String v) {
    if (AlignedText_Type.featOkTst && ((AlignedText_Type)jcasType).casFeat_alignmentType == null)
      jcasType.jcas.throwFeatMissing("alignmentType", "eu.excitement.type.alignment.AlignedText");
    jcasType.ll_cas.ll_setStringValue(addr, ((AlignedText_Type)jcasType).casFeatCode_alignmentType, v);}    
  }

    