/**
 * 
 */
package ac.biu.nlp.nlp.engineml.generic.truthteller.services;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.WildcardPartOfSpeech;
import ac.biu.nlp.nlp.engineml.representation.AdditionalNodeInformation;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedMatchCriteria;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultMatchCriteria;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.match.MatchCriteria;

/**
 * Compare two nodes {@link ExtendedInfo}s, <b>ignore </b>their {@link AdditionalNodeInformation}s!
 * testInfo is the entailment rule's info.
 * 
 * 
 * @author Amnon Lotan
 * @since 13/06/2011
 * 
 */
public class IgonreAdditionalInfoMatchCriteria implements MatchCriteria<ExtendedInfo, ExtendedInfo, ExtendedNode, ExtendedNode> 
{

	public boolean nodesMatch(ExtendedNode mainNode, ExtendedNode testNode)
	{
		if ( (mainNode==null) && (testNode==null) )return true;
		else if ( (mainNode==null) || (testNode==null) )return false;
		else return this.nodesInfoMatch(mainNode.getInfo(), testNode.getInfo());
	}

	
	public boolean edgesMatch(ExtendedInfo mainInfo, ExtendedInfo testInfo)
	{
		if (InfoGetFields.getRelation(testInfo).equals(ExtendedMatchCriteria.WILDCARD_RELATION))
			return true;	// wildcard in the rule!
		
		return defaultMatchCriteria.edgesMatch(mainInfo, testInfo);
	}
	
	public boolean nodesInfoMatch(ExtendedInfo mainInfo, ExtendedInfo testInfo)
	{
		boolean ret = false;
		if ( (null==mainInfo) && (null==testInfo) )
			ret = true;
		else if ( (null==mainInfo) || (null==testInfo) )
			ret = false;
		else
		{
			boolean mainIsVariable = InfoGetFields.isVariable(mainInfo);
			boolean testIsVariable = InfoGetFields.isVariable(testInfo);
			if ( (!mainIsVariable) && (!testIsVariable) )
			{
				if (
					(InfoGetFields.getLemma(mainInfo).equalsIgnoreCase(InfoGetFields.getLemma(testInfo)))
					&&
					posMatch(InfoGetFields.getPartOfSpeechObject(mainInfo), InfoGetFields.getPartOfSpeechObject(testInfo))
					)
					ret = true;
				else
					ret = false;
			}
			else
			{
				if  (posMatch(InfoGetFields.getPartOfSpeechObject(mainInfo), InfoGetFields.getPartOfSpeechObject(testInfo))
					)
						ret = true;
					else
						ret = false;
			}
		}
		
		return ret;
	}

	/**
	 * @param partOfSpeechObject
	 * @param partOfSpeechObject2
	 * @return
	 */
	private boolean posMatch(PartOfSpeech textPartOfSpeech, PartOfSpeech rulePartOfSpeech) 
	{
		return (WildcardPartOfSpeech.isWildCardPOS(rulePartOfSpeech) 
				|| 
				textPartOfSpeech.getCanonicalPosTag()==rulePartOfSpeech.getCanonicalPosTag()
				);
	}

	private DefaultMatchCriteria defaultMatchCriteria = new DefaultMatchCriteria();
}
