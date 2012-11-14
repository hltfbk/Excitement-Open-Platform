

/* First created by JCasGen Fri Oct 05 20:16:50 CEST 2012 */
package eu.excitement.type.entailment;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** This type provides metadata for entailment problem.
 * Updated by JCasGen Fri Oct 05 20:16:50 CEST 2012
 * XML source: /Users/tailblues/progs/github/Excitement-Open-Platform/common/src/main/resources/desc/type/EntailmentTypes.xml
 * @generated */
public class EntailmentMetadata extends TOP {
  /** @generated
   * @ordered 
   */
  //@SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(EntailmentMetadata.class);
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
  protected EntailmentMetadata() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public EntailmentMetadata(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public EntailmentMetadata(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: task

  /** getter for task - gets This string holds the task description which can be found in the
RTE challenge data.
   * @generated */
  public String getTask() {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_task == null)
      jcasType.jcas.throwFeatMissing("task", "eu.excitement.type.entailment.EntailmentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_task);}
    
  /** setter for task - sets This string holds the task description which can be found in the
RTE challenge data. 
   * @generated */
  public void setTask(String v) {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_task == null)
      jcasType.jcas.throwFeatMissing("task", "eu.excitement.type.entailment.EntailmentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_task, v);}    
   
    
  //*--------------*
  //* Feature: channel

  /** getter for channel - gets his feature can holds a string that shows the channel where
this problem was originated. For example, "customer e-mail", "online forum", or " customer
transcription", etc.
   * @generated */
  public String getChannel() {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_channel == null)
      jcasType.jcas.throwFeatMissing("channel", "eu.excitement.type.entailment.EntailmentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_channel);}
    
  /** setter for channel - sets his feature can holds a string that shows the channel where
this problem was originated. For example, "customer e-mail", "online forum", or " customer
transcription", etc. 
   * @generated */
  public void setChannel(String v) {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_channel == null)
      jcasType.jcas.throwFeatMissing("channel", "eu.excitement.type.entailment.EntailmentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_channel, v);}    
   
    
  //*--------------*
  //* Feature: origin

  /** getter for origin - gets This metadata field can hold a string that shows the origin of
this text and hypothesis. A company name, or a product name.
   * @generated */
  public String getOrigin() {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_origin == null)
      jcasType.jcas.throwFeatMissing("origin", "eu.excitement.type.entailment.EntailmentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_origin);}
    
  /** setter for origin - sets This metadata field can hold a string that shows the origin of
this text and hypothesis. A company name, or a product name. 
   * @generated */
  public void setOrigin(String v) {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_origin == null)
      jcasType.jcas.throwFeatMissing("origin", "eu.excitement.type.entailment.EntailmentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_origin, v);}    
   
    
  //*--------------*
  //* Feature: TextDocumentID

  /** getter for TextDocumentID - gets This field can hold a string that identifies the doc-
ument of the TextView. This feature must have a value, if TextCollectionID is not null.
   * @generated */
  public String getTextDocumentID() {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_TextDocumentID == null)
      jcasType.jcas.throwFeatMissing("TextDocumentID", "eu.excitement.type.entailment.EntailmentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_TextDocumentID);}
    
  /** setter for TextDocumentID - sets This field can hold a string that identifies the doc-
ument of the TextView. This feature must have a value, if TextCollectionID is not null. 
   * @generated */
  public void setTextDocumentID(String v) {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_TextDocumentID == null)
      jcasType.jcas.throwFeatMissing("TextDocumentID", "eu.excitement.type.entailment.EntailmentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_TextDocumentID, v);}    
   
    
  //*--------------*
  //* Feature: TextCollectionID

  /** getter for TextCollectionID - gets This field can hold a string that identifies the
collection name where the document of the TextView belongs to.
   * @generated */
  public String getTextCollectionID() {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_TextCollectionID == null)
      jcasType.jcas.throwFeatMissing("TextCollectionID", "eu.excitement.type.entailment.EntailmentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_TextCollectionID);}
    
  /** setter for TextCollectionID - sets This field can hold a string that identifies the
collection name where the document of the TextView belongs to. 
   * @generated */
  public void setTextCollectionID(String v) {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_TextCollectionID == null)
      jcasType.jcas.throwFeatMissing("TextCollectionID", "eu.excitement.type.entailment.EntailmentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_TextCollectionID, v);}    
   
    
  //*--------------*
  //* Feature: HypothesisDocumentID

  /** getter for HypothesisDocumentID - gets This field can hold a string that identifies
the document of the HypothesisView. This feature must have a value, if HypothesisCollec-
tionID is not null.
   * @generated */
  public String getHypothesisDocumentID() {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_HypothesisDocumentID == null)
      jcasType.jcas.throwFeatMissing("HypothesisDocumentID", "eu.excitement.type.entailment.EntailmentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_HypothesisDocumentID);}
    
  /** setter for HypothesisDocumentID - sets This field can hold a string that identifies
the document of the HypothesisView. This feature must have a value, if HypothesisCollec-
tionID is not null. 
   * @generated */
  public void setHypothesisDocumentID(String v) {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_HypothesisDocumentID == null)
      jcasType.jcas.throwFeatMissing("HypothesisDocumentID", "eu.excitement.type.entailment.EntailmentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_HypothesisDocumentID, v);}    
   
    
  //*--------------*
  //* Feature: HypothesisCollectionID

  /** getter for HypothesisCollectionID - gets This field can hold a string that identi-
fies the collection name where the document of the HypothesisView belongs to.
   * @generated */
  public String getHypothesisCollectionID() {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_HypothesisCollectionID == null)
      jcasType.jcas.throwFeatMissing("HypothesisCollectionID", "eu.excitement.type.entailment.EntailmentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_HypothesisCollectionID);}
    
  /** setter for HypothesisCollectionID - sets This field can hold a string that identi-
fies the collection name where the document of the HypothesisView belongs to. 
   * @generated */
  public void setHypothesisCollectionID(String v) {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_HypothesisCollectionID == null)
      jcasType.jcas.throwFeatMissing("HypothesisCollectionID", "eu.excitement.type.entailment.EntailmentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_HypothesisCollectionID, v);}    
   
    
  //*--------------*
  //* Feature: language

  /** getter for language - gets Language of this CAS.
   * @generated */
  public String getLanguage() {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_language == null)
      jcasType.jcas.throwFeatMissing("language", "eu.excitement.type.entailment.EntailmentMetadata");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_language);}
    
  /** setter for language - sets Language of this CAS. 
   * @generated */
  public void setLanguage(String v) {
    if (EntailmentMetadata_Type.featOkTst && ((EntailmentMetadata_Type)jcasType).casFeat_language == null)
      jcasType.jcas.throwFeatMissing("language", "eu.excitement.type.entailment.EntailmentMetadata");
    jcasType.ll_cas.ll_setStringValue(addr, ((EntailmentMetadata_Type)jcasType).casFeatCode_language, v);}    
  }

    