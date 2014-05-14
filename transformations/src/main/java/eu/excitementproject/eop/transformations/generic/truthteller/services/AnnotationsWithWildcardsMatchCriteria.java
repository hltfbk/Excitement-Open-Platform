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
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedMatchCriteria;

/**
 * Compares {@link ExtendedInfo}s. To be used in rule-text matching, where null values in a rule node (text node) acts like a 
 * wildcard.
 * <P>
 * testInfo is the entailment rule's info
 * 
 * @author Amnon Lotan
 * @since 13/06/2011
 * 
 */
public class AnnotationsWithWildcardsMatchCriteria<TM extends ExtendedInfo,TT extends ExtendedInfo, SM extends AbstractNode<TM, SM>, ST extends AbstractNode<TT, ST>> 
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

	/**
	 * @param mainInfo
	 * @param testInfo
	 * @return
	 */
	private boolean additionalNodeInformationMatch(ExtendedInfo mainInfo, ExtendedInfo testInfo) 
	{
		boolean ret = false;
			
		ret = 
			// if the rule's annotation value is null, it matches like a wildcard to any node. else, both annotations are compared.
				
			(	ExtendedInfoGetFields.getPredicateSignature(testInfo).equals("") 
					||
				ExtendedInfoGetFields.getPredicateSignature(mainInfo).equals(ExtendedInfoGetFields.getPredicateSignature(testInfo))
			)
			&&
			(	ExtendedInfoGetFields.getNegationAndUncertainty(testInfo).equals("") 
					||
				ExtendedInfoGetFields.getNegationAndUncertainty(mainInfo).equals(ExtendedInfoGetFields.getNegationAndUncertainty(testInfo))
			)
			&&
			(	ExtendedInfoGetFields.getClauseTruth(testInfo).equals("") 
					||
				ExtendedInfoGetFields.getClauseTruth(mainInfo).equals(ExtendedInfoGetFields.getClauseTruth(testInfo))
			)
			&&
			(	ExtendedInfoGetFields.getPredTruth(testInfo).equals("") 
					||
				ExtendedInfoGetFields.getPredTruth(mainInfo).equals(ExtendedInfoGetFields.getPredTruth(testInfo))
			)
			&&
			(	ExtendedInfoGetFields.getMonotonicity(testInfo).equals("") 
					||
				ExtendedInfoGetFields.getMonotonicity(mainInfo).equals(ExtendedInfoGetFields.getMonotonicity(testInfo))
			)
			;			
		
		return ret;
	}


	/**
	 * Check for wildcard POS, then compare canonical POSs, then compare normal (Penn) POSs!<br>
	 * This method differs from other <code>posMatch()</code> methods by comparing the regular POSs as well.
	 * @param partOfSpeechObject
	 * @param partOfSpeechObject2
	 * @return
	 */
	private boolean posMatch(PartOfSpeech textPartOfSpeech, PartOfSpeech rulePartOfSpeech) 
	{
		if (WildcardPartOfSpeech.isWildCardPOS(rulePartOfSpeech))
			return true;
		if (textPartOfSpeech.equals(rulePartOfSpeech))			// this covers cases where the rule's and text's POSs are of the same subclass and match
			return true;
		// this covers all other cases where the rule uses a canonical POS (and the text may use any other POS type)
		if (isUnspecifiedPos(rulePartOfSpeech) && simplerPos(textPartOfSpeech.getCanonicalPosTag())==simplerPos(rulePartOfSpeech.getCanonicalPosTag()))
			return true;
		return false;
	}
	
	/**
	 * @param partOfSpeech
	 * @return
	 */
	private boolean isUnspecifiedPos(PartOfSpeech partOfSpeech) {
		return simplerPos(partOfSpeech.getCanonicalPosTag()).toString().equals(partOfSpeech.getStringRepresentation());
	}

	private DefaultMatchCriteria defaultMatchCriteria = new DefaultMatchCriteria();
}
