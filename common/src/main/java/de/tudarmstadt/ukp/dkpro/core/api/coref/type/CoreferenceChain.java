

/* First created by JCasGen Wed Sep 13 18:11:14 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.api.coref.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.AnnotationBase;


/** Marks the beginning of a chain.
 * Updated by JCasGen Wed Sep 13 18:11:14 CEST 2017
 * XML source: /home/nira/informiz/Excitement-Open-Platform/common/target/jcasgen/typesystem.xml
 * @generated */
public class CoreferenceChain extends AnnotationBase {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CoreferenceChain.class);
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
  protected CoreferenceChain() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public CoreferenceChain(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CoreferenceChain(JCas jcas) {
    super(jcas);
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
  //* Feature: first

  /** getter for first - gets This is the first corefernce link in coreference chain
   * @generated
   * @return value of the feature 
   */
  public CoreferenceLink getFirst() {
    if (CoreferenceChain_Type.featOkTst && ((CoreferenceChain_Type)jcasType).casFeat_first == null)
      jcasType.jcas.throwFeatMissing("first", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain");
    return (CoreferenceLink)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CoreferenceChain_Type)jcasType).casFeatCode_first)));}
    
  /** setter for first - sets This is the first corefernce link in coreference chain 
   * @generated
   * @param v value to set into the feature 
   */
  public void setFirst(CoreferenceLink v) {
    if (CoreferenceChain_Type.featOkTst && ((CoreferenceChain_Type)jcasType).casFeat_first == null)
      jcasType.jcas.throwFeatMissing("first", "de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain");
    jcasType.ll_cas.ll_setRefValue(addr, ((CoreferenceChain_Type)jcasType).casFeatCode_first, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    