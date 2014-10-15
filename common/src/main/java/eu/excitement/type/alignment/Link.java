package eu.excitement.type.alignment;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.NonEmptyStringList;
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
 * Updated by JCasGen Wed May 14 14:20:19 CEST 2014
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
  //* Feature: directionString

  /** getter for directionString - gets This value denotes the "direction" of the alignment.Link. Enum-like value that holds one of "TtoH", "HtoT", or "Symmetric".
   * @generated */
  public String getDirectionString() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_directionString == null)
      jcasType.jcas.throwFeatMissing("directionString", "eu.excitement.type.alignment.Link");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Link_Type)jcasType).casFeatCode_directionString);}
    
  /** setter for directionString - sets This value denotes the "direction" of the alignment.Link. Enum-like value that holds one of "TtoH", "HtoT", or "Symmetric". 
   * @generated */
  public void setDirectionString(String v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_directionString == null)
      jcasType.jcas.throwFeatMissing("directionString", "eu.excitement.type.alignment.Link");
    jcasType.ll_cas.ll_setStringValue(addr, ((Link_Type)jcasType).casFeatCode_directionString, v);}    
   
    
  //*--------------*
  //* Feature: alignerID

  /** getter for alignerID - gets This is the first part of 3 ID strings for the alignment.Link instance. The string denotes the idetification of the aligner (or underlying resource). 

It is the convention to use getID() method of alignment.Link to get the concatenated, unique string for the instance. getID() returns such a string by concatenating 3 ID strings: 
alignerID + alignerVersion + linkInfo
   * @generated */
  public String getAlignerID() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_alignerID == null)
      jcasType.jcas.throwFeatMissing("alignerID", "eu.excitement.type.alignment.Link");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Link_Type)jcasType).casFeatCode_alignerID);}
    
  /** setter for alignerID - sets This is the first part of 3 ID strings for the alignment.Link instance. The string denotes the idetification of the aligner (or underlying resource). 

It is the convention to use getID() method of alignment.Link to get the concatenated, unique string for the instance. getID() returns such a string by concatenating 3 ID strings: 
alignerID + alignerVersion + linkInfo 
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

  /** 
   *  Getter for groupLabel - Please do not use this method directly. 
   *  
   *  Instead, Please use getGroupLabel methods that returns a Enum Set. 
   *  (getGroupLabelInferenceLevel() and getGroupLabelDomainLevel()).
   *  
   *  This method retrieves the underlying StringList object, which is a linkedList. 
   *  The wrapper methods getGroupLabel() for levels are better presented with Enums. 
   *  
   * @generated */
  public StringList getGroupLabel() {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_groupLabel == null)
      jcasType.jcas.throwFeatMissing("groupLabel", "eu.excitement.type.alignment.Link");
    return (StringList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Link_Type)jcasType).casFeatCode_groupLabel)));}
    
  /** 
   * setter for groupLabel - Please do not use this method directly.
   * 
   * Instead, please use addGroupLabel method that accepts two Enum types. 
   *  --- domain level group label and inference lavel group label. 
   * 
   *  This method sets StringList object, which is a linkedList. 
   *  The wrapper methods addGroupLabel() methods are better presented 
   *  with Enums. Use those methods. 
   *  
   * @generated */
  public void setGroupLabel(StringList v) {
    if (Link_Type.featOkTst && ((Link_Type)jcasType).casFeat_groupLabel == null)
      jcasType.jcas.throwFeatMissing("groupLabel", "eu.excitement.type.alignment.Link");
    jcasType.ll_cas.ll_setRefValue(addr, ((Link_Type)jcasType).casFeatCode_groupLabel, jcasType.ll_cas.ll_getFSRef(v));}    
  
  
  //
  // Start of the manually extended code part. 
  // WARNING: JCasGen automated code generation often overwrites all of those, if you change
  // the type definition and regenerates. It is advisable to copy and keep the section, do 
  // auto-generate, and then update the code part ... 
  //
  
  /** 
   * This is a convenience method that returns "Type string" that can identify the 
   * type of the Link uniquely within the consumer of the CAS Links (e.g. EDAs, as a feature name, etc) 
   * The method returns a concatenated string of "alignerID" + "alignerVersion" + "linkInfo"
   * */
  
   public String getID() {
	   return (getAlignerID() + "__" + getAlignerVersion() + "__" + getLinkInfo()); 
   }
  
   /**
    * Enum wrapper for type.alignment.Direction string sub-type. Used with 
    * set/getDirection()  
    */
   public enum Direction
   {
	   HtoT, // correspond to eu.excitement.type.alignment.Direction -- "HtoT" (string subtype) 
	   TtoH, // correspond to eu.excitement.type.alignment.Direction -- "TtoH" (string subtype)
	   Bidirection, // correspond to Direction string -- "Bidirection" 
   }
   
   /**
    * Use this method to set the direction of the Link instance: one of HtoT, TtoH, or Bidirection 
    * (A wrapper method for setDirectionString. On the actual CAS, directionString will be set to indicate the direction. However, this wrapper provides enum-access instead of string access.) 
    * 
    * @param dir Direction enum (type of Link.Direction) 
    */
   public void setDirection(Link.Direction dir)
   {
	   setDirectionString(dir.name()); 
   }
   
   /**
    * Use this method to check the direction of the Link instance: one of HtoT, TtoH, or Bidirection. 
    * (A wrapper method for getDirectionString. On the actual CAS, directionString will be set to indicate the direction. However, this wrapper provides enum-access instead of string access.) 
    * 
    * @return Direction enum (type of Link.Direction), one of HtoT, TtoH, or Bidirection 
    */
   public Direction getDirection()
   {
	   Direction dir = Direction.valueOf(getDirectionString()); 
	   return dir; 
   }
   
   	/**
   	 * One of the two getter method for Group Labels. 
   	 * 
   	 * This method returns the set of "Inference level" group labels that are added for this 
   	 * alignment.Link instance. See SemanticLabelInferenceLevel enum class, for checking what 
   	 * type of labels are there currently.
   	 * 
   	 * @return set of inference level group labels. 
   	 */
   	public Set<GroupLabelInferenceLevel> getGroupLabelsInferenceLevel() 
   	{
   		Set<GroupLabelInferenceLevel> result = new HashSet<GroupLabelInferenceLevel>(); 
   		
   		// iterate each of string, check, and add if it is. 
   		NonEmptyStringList i = (NonEmptyStringList) this.getGroupLabel(); 
   		
   		while(i != null)
   		{
   			String s = i.getHead(); 
   			i = (NonEmptyStringList) i.getTail(); 
   			
   			GroupLabelInferenceLevel label = null; 
   			try {
   	   			label = GroupLabelInferenceLevel.valueOf(s);    				
   			}
   			catch(IllegalArgumentException e)
   			{
   				continue; // this string is not one of this enum. pass. 
   			}
   			result.add(label);    			
   		}
   		return result; 
   	}
   	
   	/**
   	 * One of the two getter method for Group Labels. 
   	 * 
   	 * This method returns the set of "Domain level" group labels that are added for this 
   	 * alignment.Link instance. See SemanticLabelDomainLevel enum class, for checking what 
   	 * type of labels are there currently. 
   	 * 
   	 * @return set of domain level group labels. 
   	 */
   	public Set<GroupLabelDomainLevel> getGroupLabelsDomainLevel()
   	{
   		Set<GroupLabelDomainLevel> result = new HashSet<GroupLabelDomainLevel>(); 
   		
   		// iterate each of string, check, and add if it is. 
   		NonEmptyStringList i = (NonEmptyStringList) this.getGroupLabel(); 
   		
   		while(i != null)
   		{
   			String s = i.getHead(); 
   			i = (NonEmptyStringList) i.getTail(); 

   			GroupLabelDomainLevel label = null; 
   			try {
   	   			label = GroupLabelDomainLevel.valueOf(s);    				
   			}
   			catch(IllegalArgumentException e)
   			{
   				continue; // this string is not one of this enum. pass. 
   			}
   			result.add(label);    			
   		}
   		return result; 
   	}
   	
   	/**
   	 * Use this method to add one semantic group label (domain level). 
   	 * To add multiple labels, call this method multiple times with different labels. 
   	 * 
   	 * Adding semantic group label is optional, but can be helpful for the grouping of the 
   	 * Links for the consumer of the JCas. Thus, it is highly recommended that an aligner 
   	 * should add minimally the inference level group label, if applicable. 
   	 *  
   	 * This method adds one domain level group label, to this alignment.Link instance. 
   	 * See SemanticLabelDomainLevel enum class, for checking what type of labels are there currently.
   	 * 
   	 * @param label
   	 */
   	public void addGroupLabel(GroupLabelDomainLevel aDomainLabel) throws CASException 
   	{
   		addOneStringInGroupLabelList(aDomainLabel.toString()); 
   	}
   	
   	/**
   	 * Use this method to add one semantic group label (inference level). 
   	 * To add multiple labels, call this method multiple times with different labels. 
   	 * 
 	 * Adding semantic group label is optional, but can be helpful for the grouping of the 
   	 * Links for the consumer of the JCas. Thus, it is highly recommended that an aligner 
   	 * should add minimally the inference level group label, if applicable. 

   	 * This method adds one inference level group label, to this alignment.Link instance. 
   	 * See SemanticLabelInferenceLevel enum class, for checking what type of labels are there currently.
   	 * 
   	 * @param label
   	 */
   	public void addGroupLabel(GroupLabelInferenceLevel aInferenceLabel) throws CASException
   	{
   		addOneStringInGroupLabelList(aInferenceLabel.toString());    		
   	}

   	/**
   	 * Worker method for addGroupLabel. 
   	 * 
   	 * @param stringToAdd
   	 * @throws CASException
   	 */
   	private void addOneStringInGroupLabelList(String stringToAdd) throws CASException
   	{
   		NonEmptyStringList sList = (NonEmptyStringList) this.getGroupLabel(); 
   		
   		// if the underlying StringList is null, make a new head. (underlying String list is a linked list)  
   		if (sList == null)
   		{
   	   		NonEmptyStringList head = new NonEmptyStringList(this.getCAS().getJCas()); 
   	   		head.setHead(stringToAdd); 
   	   		this.setGroupLabel(head); 
   	   		
   		}
   		else
   		{
   			// get to the last part, and add label as a string 
   			NonEmptyStringList i = sList;    		
   			
   			// find the last node ... 
   			while(i.getTail() != null)
   			{
   				i = (NonEmptyStringList) i.getTail(); 
   			}
   			
   			// add new node at the end. 
   	   		NonEmptyStringList newNode = new NonEmptyStringList(this.getCAS().getJCas()); 
   	   		newNode.setHead(stringToAdd); 
   	   		i.setTail(newNode); 
   		}  		
   	}
   	
   	
  
  }