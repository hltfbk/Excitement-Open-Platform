package ac.biu.nlp.nlp.engineml.representation;

import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultMatchCriteria;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import ac.biu.nlp.nlp.instruments.parse.tree.match.MatchCriteria;
import ac.biu.nlp.nlp.representation.PartOfSpeech;
import ac.biu.nlp.nlp.representation.WildcardPartOfSpeech;

/**
 * @author Asher Stern
 * @since Apr 6, 2011
 *
 */
public class ExtendedMatchCriteria implements MatchCriteria<ExtendedInfo, Info, ExtendedNode , BasicNode>
{
	public boolean nodesMatch(ExtendedNode mainNode, BasicNode testNode)
	{
		if ( (mainNode==null) && (testNode==null) )return true;
		else if ( (mainNode==null) || (testNode==null) )return false;
		else return this.nodesInfoMatch(mainNode.getInfo(), testNode.getInfo());
	}

	
	public boolean edgesMatch(ExtendedInfo mainInfo, Info testInfo)
	{
		if (InfoGetFields.getRelation(testInfo).equals(WILDCARD_RELATION))
			return true;	// wildcard in the rule!
		
		return defaultMatchCriteria.edgesMatch(mainInfo, testInfo);
	}
	
	public boolean nodesInfoMatch(ExtendedInfo mainInfo, Info testInfo)
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
				if  (posMatch(InfoGetFields.getPartOfSpeechObject(mainInfo), InfoGetFields.getPartOfSpeechObject(testInfo)))
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
	public static final String WILDCARD_RELATION = "*";
}
