

/* First created by JCasGen Tue Nov 04 18:05:45 CET 2014 */
package de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Nov 04 18:05:45 CET 2014
 * XML source: /Users/tailblues/progs/Excitement-Open-Platform/common/src/main/resources/desc/type/Chunks.xml
 * @generated */
public class Chunk extends Annotation {
  /** @generated
   * @ordered 
   */
//  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Chunk.class);
  /** @generated
   * @ordered 
   */
//  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Chunk() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Chunk(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Chunk(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Chunk(JCas jcas, int begin, int end) {
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
  //* Feature: chunkValue

  /** getter for chunkValue - gets 
   * @generated
   * @return value of the feature 
   */
  public String getChunkValue() {
    if (Chunk_Type.featOkTst && ((Chunk_Type)jcasType).casFeat_chunkValue == null)
      jcasType.jcas.throwFeatMissing("chunkValue", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Chunk_Type)jcasType).casFeatCode_chunkValue);}
    
  /** setter for chunkValue - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setChunkValue(String v) {
    if (Chunk_Type.featOkTst && ((Chunk_Type)jcasType).casFeat_chunkValue == null)
      jcasType.jcas.throwFeatMissing("chunkValue", "de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk");
    jcasType.ll_cas.ll_setStringValue(addr, ((Chunk_Type)jcasType).casFeatCode_chunkValue, v);}    
  }

    