

/* First created by JCasGen Wed Sep 13 18:11:14 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.api.metadata.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.DocumentAnnotation;


/** <p>The DocumentMetaData annotation stores information about a single processed
        document. There can only be one of these annotations per CAS. The annotation is
        created by readers and contains information to uniquely identify the document from
        which a CAS was created. Writer components use this information when determining
        under which filename a CAS is stored.<p>

        <p>There are two principle ways of identifying a document:<p>

        <ul>
        <li><b>collection id / document id:</b> this simple system identifies a document
        within a collection. The ID of the collection and the document are each
        simple strings without any further semantics such as e.g. a hierarchy. For
        this reason, this identification scheme is not well suited to preserve
        information about directory structures.</li>

        <li><b>document base URI / document URI:</b> this system identifies a document using
        a URI. The base URI is used to derive the relative path of the document with
        respect to the base location from where it has been read. E.g. if the base
        URI is <code>file:/texts</code> and the document URI is <code>file:/texts/english/text1.txt</code>, then the relativ
        path of the document is <code>english/text1.txt</code>. This
        information is used by writers to recreate the directory structure found
        under the base location in the target location.</li>
        </ul>

        <p>It is possible and indeed common for a writer to initialize both systems of
        identification. If both systems are present, most writers default to using the
        URI-based systems. However, most writers also allow forcing the use of the ID-based
        systems.</p>

        <p>In addition to the features given here, there is a <i>language</i> feature inherited from UIMA's DocumentAnnotation. DKPro Core components expect a two letter ISO
        639-1 language code there.</p>
 * Updated by JCasGen Wed Sep 13 18:11:14 CEST 2017
 * XML source: /home/nira/informiz/Excitement-Open-Platform/common/target/jcasgen/typesystem.xml
 * @generated */
public class DocumentMetaData extends DocumentAnnotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DocumentMetaData.class);
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
  protected DocumentMetaData() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public DocumentMetaData(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public DocumentMetaData(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public DocumentMetaData(JCas jcas, int begin, int end) {
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
  //* Feature: documentTitle

  /** getter for documentTitle - gets The human readable title of the document.
   * @generated
   * @return value of the feature 
   */
  public String getDocumentTitle() {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentTitle == null)
      jcasType.jcas.throwFeatMissing("documentTitle", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentTitle);}
    
  /** setter for documentTitle - sets The human readable title of the document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocumentTitle(String v) {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentTitle == null)
      jcasType.jcas.throwFeatMissing("documentTitle", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentTitle, v);}    
   
    
  //*--------------*
  //* Feature: documentId

  /** getter for documentId - gets The id of the document.
   * @generated
   * @return value of the feature 
   */
  public String getDocumentId() {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentId == null)
      jcasType.jcas.throwFeatMissing("documentId", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentId);}
    
  /** setter for documentId - sets The id of the document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocumentId(String v) {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentId == null)
      jcasType.jcas.throwFeatMissing("documentId", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentId, v);}    
   
    
  //*--------------*
  //* Feature: documentUri

  /** getter for documentUri - gets The URI of the document.
   * @generated
   * @return value of the feature 
   */
  public String getDocumentUri() {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentUri == null)
      jcasType.jcas.throwFeatMissing("documentUri", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentUri);}
    
  /** setter for documentUri - sets The URI of the document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocumentUri(String v) {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentUri == null)
      jcasType.jcas.throwFeatMissing("documentUri", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentUri, v);}    
   
    
  //*--------------*
  //* Feature: collectionId

  /** getter for collectionId - gets The ID of the whole document collection.
   * @generated
   * @return value of the feature 
   */
  public String getCollectionId() {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_collectionId == null)
      jcasType.jcas.throwFeatMissing("collectionId", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_collectionId);}
    
  /** setter for collectionId - sets The ID of the whole document collection. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCollectionId(String v) {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_collectionId == null)
      jcasType.jcas.throwFeatMissing("collectionId", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_collectionId, v);}    
   
    
  //*--------------*
  //* Feature: documentBaseUri

  /** getter for documentBaseUri - gets Base URI of the document.
   * @generated
   * @return value of the feature 
   */
  public String getDocumentBaseUri() {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentBaseUri == null)
      jcasType.jcas.throwFeatMissing("documentBaseUri", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentBaseUri);}
    
  /** setter for documentBaseUri - sets Base URI of the document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocumentBaseUri(String v) {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_documentBaseUri == null)
      jcasType.jcas.throwFeatMissing("documentBaseUri", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_documentBaseUri, v);}    
   
    
  //*--------------*
  //* Feature: isLastSegment

  /** getter for isLastSegment - gets CAS de-multipliers need to know whether a CAS is the
            last multiplied segment.
            Thus CAS multipliers should set this field to true for the last CAS
            they produce.
   * @generated
   * @return value of the feature 
   */
  public boolean getIsLastSegment() {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_isLastSegment == null)
      jcasType.jcas.throwFeatMissing("isLastSegment", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_isLastSegment);}
    
  /** setter for isLastSegment - sets CAS de-multipliers need to know whether a CAS is the
            last multiplied segment.
            Thus CAS multipliers should set this field to true for the last CAS
            they produce. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsLastSegment(boolean v) {
    if (DocumentMetaData_Type.featOkTst && ((DocumentMetaData_Type)jcasType).casFeat_isLastSegment == null)
      jcasType.jcas.throwFeatMissing("isLastSegment", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((DocumentMetaData_Type)jcasType).casFeatCode_isLastSegment, v);}    
  }

    