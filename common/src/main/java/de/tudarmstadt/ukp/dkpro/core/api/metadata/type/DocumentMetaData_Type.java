
/* First created by JCasGen Wed Sep 13 18:11:14 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.api.metadata.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.DocumentAnnotation_Type;

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
 * @generated */
public class DocumentMetaData_Type extends DocumentAnnotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = DocumentMetaData.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
 
  /** @generated */
  final Feature casFeat_documentTitle;
  /** @generated */
  final int     casFeatCode_documentTitle;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDocumentTitle(int addr) {
        if (featOkTst && casFeat_documentTitle == null)
      jcas.throwFeatMissing("documentTitle", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return ll_cas.ll_getStringValue(addr, casFeatCode_documentTitle);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDocumentTitle(int addr, String v) {
        if (featOkTst && casFeat_documentTitle == null)
      jcas.throwFeatMissing("documentTitle", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    ll_cas.ll_setStringValue(addr, casFeatCode_documentTitle, v);}
    
  
 
  /** @generated */
  final Feature casFeat_documentId;
  /** @generated */
  final int     casFeatCode_documentId;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDocumentId(int addr) {
        if (featOkTst && casFeat_documentId == null)
      jcas.throwFeatMissing("documentId", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return ll_cas.ll_getStringValue(addr, casFeatCode_documentId);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDocumentId(int addr, String v) {
        if (featOkTst && casFeat_documentId == null)
      jcas.throwFeatMissing("documentId", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    ll_cas.ll_setStringValue(addr, casFeatCode_documentId, v);}
    
  
 
  /** @generated */
  final Feature casFeat_documentUri;
  /** @generated */
  final int     casFeatCode_documentUri;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDocumentUri(int addr) {
        if (featOkTst && casFeat_documentUri == null)
      jcas.throwFeatMissing("documentUri", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return ll_cas.ll_getStringValue(addr, casFeatCode_documentUri);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDocumentUri(int addr, String v) {
        if (featOkTst && casFeat_documentUri == null)
      jcas.throwFeatMissing("documentUri", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    ll_cas.ll_setStringValue(addr, casFeatCode_documentUri, v);}
    
  
 
  /** @generated */
  final Feature casFeat_collectionId;
  /** @generated */
  final int     casFeatCode_collectionId;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCollectionId(int addr) {
        if (featOkTst && casFeat_collectionId == null)
      jcas.throwFeatMissing("collectionId", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return ll_cas.ll_getStringValue(addr, casFeatCode_collectionId);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCollectionId(int addr, String v) {
        if (featOkTst && casFeat_collectionId == null)
      jcas.throwFeatMissing("collectionId", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    ll_cas.ll_setStringValue(addr, casFeatCode_collectionId, v);}
    
  
 
  /** @generated */
  final Feature casFeat_documentBaseUri;
  /** @generated */
  final int     casFeatCode_documentBaseUri;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDocumentBaseUri(int addr) {
        if (featOkTst && casFeat_documentBaseUri == null)
      jcas.throwFeatMissing("documentBaseUri", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return ll_cas.ll_getStringValue(addr, casFeatCode_documentBaseUri);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDocumentBaseUri(int addr, String v) {
        if (featOkTst && casFeat_documentBaseUri == null)
      jcas.throwFeatMissing("documentBaseUri", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    ll_cas.ll_setStringValue(addr, casFeatCode_documentBaseUri, v);}
    
  
 
  /** @generated */
  final Feature casFeat_isLastSegment;
  /** @generated */
  final int     casFeatCode_isLastSegment;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIsLastSegment(int addr) {
        if (featOkTst && casFeat_isLastSegment == null)
      jcas.throwFeatMissing("isLastSegment", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_isLastSegment);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsLastSegment(int addr, boolean v) {
        if (featOkTst && casFeat_isLastSegment == null)
      jcas.throwFeatMissing("isLastSegment", "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_isLastSegment, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public DocumentMetaData_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_documentTitle = jcas.getRequiredFeatureDE(casType, "documentTitle", "uima.cas.String", featOkTst);
    casFeatCode_documentTitle  = (null == casFeat_documentTitle) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_documentTitle).getCode();

 
    casFeat_documentId = jcas.getRequiredFeatureDE(casType, "documentId", "uima.cas.String", featOkTst);
    casFeatCode_documentId  = (null == casFeat_documentId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_documentId).getCode();

 
    casFeat_documentUri = jcas.getRequiredFeatureDE(casType, "documentUri", "uima.cas.String", featOkTst);
    casFeatCode_documentUri  = (null == casFeat_documentUri) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_documentUri).getCode();

 
    casFeat_collectionId = jcas.getRequiredFeatureDE(casType, "collectionId", "uima.cas.String", featOkTst);
    casFeatCode_collectionId  = (null == casFeat_collectionId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_collectionId).getCode();

 
    casFeat_documentBaseUri = jcas.getRequiredFeatureDE(casType, "documentBaseUri", "uima.cas.String", featOkTst);
    casFeatCode_documentBaseUri  = (null == casFeat_documentBaseUri) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_documentBaseUri).getCode();

 
    casFeat_isLastSegment = jcas.getRequiredFeatureDE(casType, "isLastSegment", "uima.cas.Boolean", featOkTst);
    casFeatCode_isLastSegment  = (null == casFeat_isLastSegment) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_isLastSegment).getCode();

  }
}



    