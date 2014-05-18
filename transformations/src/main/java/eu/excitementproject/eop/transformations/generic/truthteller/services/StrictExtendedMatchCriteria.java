/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.services;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultMatchCriteria;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatchCriteria;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.WildcardPartOfSpeech;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedMatchCriteria;

/**
 * Compares {@link ExtendedInfo}s. To be used for comparing trees and other exact match scenarios, where any difference between 
 * the values of any field counts, inc. nulls. This is in opposed to {@link AnnotationsWithWildcardsMatchCriteria}.
 * <p> 
 * rule node (text node) acts like a 
 * wildcard.
 * testInfo is the entailment rule's info
 * 
 * @author Amnon Lotan
 * @since 13/06/2011
 * 
 */
public class StrictExtendedMatchCriteria<TM extends ExtendedInfo,TT extends ExtendedInfo, SM extends AbstractNode<TM, SM>, ST extends AbstractNode<TT, ST>> 
	implements MatchCriteria<TM,TT, SM, ST>	 
{

	public boolean nodesMatch(SM mainNode, ST testNode)
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
					&&
					additionalNodeInformationMatch(mainInfo, testInfo)
					)
					ret = true;
				else
					ret = false;
			}
			else
			{
				if  (posMatch(InfoGetFields.getPartOfSpeechObject(mainInfo), InfoGetFields.getPartOfSpeechObject(testInfo))
					&&
					additionalNodeInformationMatch(mainInfo, testInfo)
					)
						ret = true;
					else
						ret = false;
			}
		}
		
		return ret;
	}


	private boolean additionalNodeInformationMatch(ExtendedInfo mainInfo, ExtendedInfo testInfo)
	{
		return additionalNodeInformationMatch(mainInfo.getAdditionalNodeInformation(), testInfo.getAdditionalNodeInformation());
	}
	
	/**
	 * @param mainInfo
	 * @param testInfo
	 * @return
	 */
	public static boolean additionalNodeInformationMatch(AdditionalNodeInformation mainInfo, AdditionalNodeInformation testInfo) 
	{
		boolean ret = false;
			
		ret = 
			// if the rule's annotation value is null, it matches like a wildcard to any node. else, both annotations are compared.
				
				ExtendedInfoGetFields.getPredicateSignature(mainInfo).equals(ExtendedInfoGetFields.getPredicateSignature(testInfo))
			&&
				ExtendedInfoGetFields.getNegationAndUncertainty(mainInfo).equals(ExtendedInfoGetFields.getNegationAndUncertainty(testInfo))
			&&
				ExtendedInfoGetFields.getClauseTruth(mainInfo).equals(ExtendedInfoGetFields.getClauseTruth(testInfo))
			&&
				ExtendedInfoGetFields.getPredTruth(mainInfo).equals(ExtendedInfoGetFields.getPredTruth(testInfo))
			&&
				ExtendedInfoGetFields.getMonotonicity(mainInfo).equals(ExtendedInfoGetFields.getMonotonicity(testInfo))
			;			
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
				simplerPos(textPartOfSpeech.getCanonicalPosTag())==simplerPos(rulePartOfSpeech.getCanonicalPosTag())
				);
	}

	private DefaultMatchCriteria defaultMatchCriteria = new DefaultMatchCriteria();
}
