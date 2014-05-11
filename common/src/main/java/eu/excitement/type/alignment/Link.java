

/* First created by JCasGen Thu Apr 24 15:21:08 CEST 2014 */
package eu.excitement.type.alignment;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringList;
import org.apache.uima.jcas.tcas.Annotation;


/** - CAS type that links two Target. 
- Multi-view type: a Link connects one target in T (TextView), the other target in H (HypothesisView). 

The semantic of a "Link" is: The texts (or structures) pointed by "TSideTarget" and "HSideTarget" have a relation of "type", with the direction of "direction",  on a strength of "strength". 

We make no assumptions regarding what annotations are aligned by Link and Target types. One Target can be linked by an arbitrary number of Link, also a Target can group an arbitrary number of Annotations. Note that uima.tcas.Annotation is the super type of almost all CAS annotation data. Since a Target can group Annotation, it can group any type of annotations in CAS.

Some notes on Link type usage. (Indexing and setting begin - end) 
- A Link instance should be indexed on the Hypothesis View. So one iteration over the Hypothesis view can get all alignment links.
- begin and end : both span value should hold the same value to that of HSide Target
 * Updated by JCasGen Sat May 10 23:34:30 CEST 2014
 * XML source: /Users/tailblues/progs/Excitement-Open-Platform/common/src/main/resources/desc/type/AlignmentTypes.xml
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
  //* Feature: TSideTarget

  /** getter for TSideTarget - gets This feature points one Target in TEXTVIEW side. A mandatory value, and should not be null. The semantic of a "Link" is: The text (or structure) targets have the relation of "type" between them, with the direction of "direction".
   * @generated */
  public Target getTSideTarget() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_TSideTarget == null)
      jcasType.jcas.throwFeatMissing("TSideTarget", "eu.excitement.type.alignment.Link");
    return (Target)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Link_Type)jcasType).casFeatCode_TSideTarget)));}
    
  /** setter for TSideTarget - sets This feature points one Target in TEXTVIEW side. A mandatory value, and should not be null. The semantic of a "Link" is: The text (or structure) targets have the relation of "type" between them, with the direction of "direction". 
   * @generated */
  public void setTSideTarget(Target v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_TSideTarget == null)
      jcasType.jcas.throwFeatMissing("TSideTarget", "eu.excitement.type.alignment.Link");
    jcasType.ll_cas.ll_setRefValue(addr, ((Link_Type)jcasType).casFeatCode_TSideTarget, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: HSideTarget

  /** getter for HSideTarget - gets This feature points one Target in HYPOTHESISVIEW side. A mandatory value, and should not be null. The semantic of a "Link" is: The text (or structure) targets have the relation of "type" between them, with the direction of "direction".
   * @generated */
  public Target getHSideTarget() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_HSideTarget == null)
      jcasType.jcas.throwFeatMissing("HSideTarget", "eu.excitement.type.alignment.Link");
    return (Target)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Link_Type)jcasType).casFeatCode_HSideTarget)));}
    
  /** setter for HSideTarget - sets This feature points one Target in HYPOTHESISVIEW side. A mandatory value, and should not be null. The semantic of a "Link" is: The text (or structure) targets have the relation of "type" between them, with the direction of "direction". 
   * @generated */
  public void setHSideTarget(Target v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_HSideTarget == null)
      jcasType.jcas.throwFeatMissing("HSideTarget", "eu.excitement.type.alignment.Link");
    jcasType.ll_cas.ll_setRefValue(addr, ((Link_Type)jcasType).casFeatCode_HSideTarget, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
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
   
    
  //*--------------*
  //* Feature: direction

  /** getter for direction - gets This value denotes the "direction" of the alignment.Link. Enum-like value that holds one of "TtoH", "HtoT", or "Symmetric". 

   * @generated */
  public String getDirection() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_direction == null)
      jcasType.jcas.throwFeatMissing("direction", "eu.excitement.type.alignment.Link");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Link_Type)jcasType).casFeatCode_direction);}
    
  /** setter for direction - sets This value denotes the "direction" of the alignment.Link. Enum-like value that holds one of "TtoH", "HtoT", or "Symmetric". 
 
   * @generated */
  public void setDirection(String v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_direction == null)
      jcasType.jcas.throwFeatMissing("direction", "eu.excitement.type.alignment.Link");
    jcasType.ll_cas.ll_setStringValue(addr, ((Link_Type)jcasType).casFeatCode_direction, v);}    
   
    
  //*--------------*
  //* Feature: alignerID

  /** getter for alignerID - gets This is the first part of 3 ID strings for the alignment.Link instance. The string denotes the idetification of the aligner (or underlying resource). 

It is the convention to use getID() method of alignment.Link to get the concatenated, unique string for the instance. getID() returns such a string by concatenating 3 ID strings: 
alignerID + version + info 
   * @generated */
  public String getAlignerID() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_alignerID == null)
      jcasType.jcas.throwFeatMissing("alignerID", "eu.excitement.type.alignment.Link");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Link_Type)jcasType).casFeatCode_alignerID);}
    
  /** setter for alignerID - sets This is the first part of 3 ID strings for the alignment.Link instance. The string denotes the idetification of the aligner (or underlying resource). 

It is the convention to use getID() method of alignment.Link to get the concatenated, unique string for the instance. getID() returns such a string by concatenating 3 ID strings: 
alignerID + version + info  
   * @generated */
  public void setAlignerID(String v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_alignerID == null)
      jcasType.jcas.throwFeatMissing("alignerID", "eu.excitement.type.alignment.Link");
    jcasType.ll_cas.ll_setStringValue(addr, ((Link_Type)jcasType).casFeatCode_alignerID, v);}    
   
    
  //*--------------*
  //* Feature: alignerVersion

  /** getter for alignerVersion - gets This is the second part of 3 ID strings for the alignment.Link instance. The string denotes the sub-identification of the aligner (or underlying resource) --- which are generally the version (or date).  

It is the convention to use getID() method of alignment.Link to get the concatenated, unique string for the instance. getID() returns such a string by concatenating 3 ID strings: 
alignerID + alignerVersion + linkInfo
   * @generated */
  public String getAlignerVersion() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_alignerVersion == null)
      jcasType.jcas.throwFeatMissing("alignerVersion", "eu.excitement.type.alignment.Link");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Link_Type)jcasType).casFeatCode_alignerVersion);}
    
  /** setter for alignerVersion - sets This is the second part of 3 ID strings for the alignment.Link instance. The string denotes the sub-identification of the aligner (or underlying resource) --- which are generally the version (or date).  

It is the convention to use getID() method of alignment.Link to get the concatenated, unique string for the instance. getID() returns such a string by concatenating 3 ID strings: 
alignerID + alignerVersion + linkInfo 
   * @generated */
  public void setAlignerVersion(String v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_alignerVersion == null)
      jcasType.jcas.throwFeatMissing("alignerVersion", "eu.excitement.type.alignment.Link");
    jcasType.ll_cas.ll_setStringValue(addr, ((Link_Type)jcasType).casFeatCode_alignerVersion, v);}    
   
    
  //*--------------*
  //* Feature: linkInfo

  /** getter for linkInfo - gets This is the thrid part of 3 ID strings for the alignment.Link instance. The string denotes the internal information about the added link; for example "synonym" (in WordNet based aligner), "stronger-than" (in VerbOcean based lexical aligner), or "local-entailment" (some aligner that aligned multiple structures). Thus, the value is defined by the aligner, and enable alginer to denote more than one relations that is identifiable by getID().

It is the convention to use getID() method of alignment.Link to get the concatenated, unique string for the instance. getID() returns such a string by concatenating 3 ID strings: 
alignerID + alignerVersion + linkInfo
   * @generated */
  public String getLinkInfo() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_linkInfo == null)
      jcasType.jcas.throwFeatMissing("linkInfo", "eu.excitement.type.alignment.Link");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Link_Type)jcasType).casFeatCode_linkInfo);}
    
  /** setter for linkInfo - sets This is the thrid part of 3 ID strings for the alignment.Link instance. The string denotes the internal information about the added link; for example "synonym" (in WordNet based aligner), "stronger-than" (in VerbOcean based lexical aligner), or "local-entailment" (some aligner that aligned multiple structures). Thus, the value is defined by the aligner, and enable alginer to denote more than one relations that is identifiable by getID().

It is the convention to use getID() method of alignment.Link to get the concatenated, unique string for the instance. getID() returns such a string by concatenating 3 ID strings: 
alignerID + alignerVersion + linkInfo 
   * @generated */
  public void setLinkInfo(String v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_linkInfo == null)
      jcasType.jcas.throwFeatMissing("linkInfo", "eu.excitement.type.alignment.Link");
    jcasType.ll_cas.ll_setStringValue(addr, ((Link_Type)jcasType).casFeatCode_linkInfo, v);}    
   
    
  //*--------------*
  //* Feature: groupLabel

  /** getter for groupLabel - gets TBDTBDTBDTBD

TO BE DETERMINED. 

We will adopt "common semantic groups", such as "LOCAL-ENTAILMENT" links, or "LOCAL-CONTRADICTION" links, and so on. This field is for those "labels". Such labels are provided as "Convenience" tools --- to help the consumer modules of alignment.Link can classify various Links without hard-coding aliner Id or link's getIDs. 

Actual values for the labels will be updated. TBDTBDTBDTBD 
   * @generated */
  public StringList getGroupLabel() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_groupLabel == null)
      jcasType.jcas.throwFeatMissing("groupLabel", "eu.excitement.type.alignment.Link");
    return (StringList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Link_Type)jcasType).casFeatCode_groupLabel)));}
    
  /** setter for groupLabel - sets TBDTBDTBDTBD

TO BE DETERMINED. 

We will adopt "common semantic groups", such as "LOCAL-ENTAILMENT" links, or "LOCAL-CONTRADICTION" links, and so on. This field is for those "labels". Such labels are provided as "Convenience" tools --- to help the consumer modules of alignment.Link can classify various Links without hard-coding aliner Id or link's getIDs. 

Actual values for the labels will be updated. TBDTBDTBDTBD  
   * @generated */
  public void setGroupLabel(StringList v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_groupLabel == null)
      jcasType.jcas.throwFeatMissing("groupLabel", "eu.excitement.type.alignment.Link");
    jcasType.ll_cas.ll_setRefValue(addr, ((Link_Type)jcasType).casFeatCode_groupLabel, jcasType.ll_cas.ll_getFSRef(v));}    
    /** A convenience method to get long (full) ID of the Link instance */
  public String getID() {
	  // TODO: better method that ignores null? 
	  return getAlignerID() + getAlignerVersion() + getLinkInfo(); 
  }
  
  }