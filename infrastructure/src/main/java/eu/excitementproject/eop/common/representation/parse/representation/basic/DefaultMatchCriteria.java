package eu.excitementproject.eop.common.representation.parse.representation.basic;


import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.match.AllEmbeddedMatcher;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatchCriteria;
import eu.excitementproject.eop.common.representation.parse.tree.match.Matcher;

/**
 * A simple {@link MatchCriteria} for {@link BasicNode}.
 * Used by {@link AllEmbeddedMatcher} and {@link Matcher}.
 * 
 * @see MatchCriteria
 * 
 * @author Asher Stern
 * @since Jan 24, 2011
 *
 */
public class DefaultMatchCriteria implements MatchCriteria<Info, Info, BasicNode, BasicNode>
{
	public void setUseCanonicalPosTag(boolean useCanonicalPosTag)
	{
		this.useCanonicalPosTag = useCanonicalPosTag;
	}

	public boolean nodesMatch(BasicNode mainNode, BasicNode testNode)
	{
		Info mainInfo = mainNode.getInfo();
		Info testInfo = testNode.getInfo();
		return nodesInfoMatch(mainInfo, testInfo);
	}
	
	public boolean nodesInfoMatch(Info mainInfo, Info testInfo)
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
					(InfoGetFields.getLemma(mainInfo).equals(InfoGetFields.getLemma(testInfo)))
					&&
					partOfSpeechMatch(mainInfo,testInfo)
					)
					ret = true;
				else
					ret = false;
			}
			else
			{
				if  (partOfSpeechMatch(mainInfo,testInfo))
						ret = true;
					else
						ret = false;
			}
		}
		
		return ret;
	}
	
	

	public boolean edgesMatch(Info mainInfo, Info testInfo)
	{
		boolean ret = false;
		if ( (null==mainInfo) && (null==testInfo) )
			ret = true;
		else if ( (null==mainInfo) || (null==testInfo) )
			ret = false;
		else
		{
			if (InfoGetFields.getRelation(mainInfo).equals(InfoGetFields.getRelation(testInfo)))
				ret = true;
			else
				ret = false;
		}

		return ret;
	}
	
	private boolean partOfSpeechMatch(Info mainInfo, Info testInfo)
	{
		if (!useCanonicalPosTag)
		{
			return InfoGetFields.getPartOfSpeech(mainInfo).equals(InfoGetFields.getPartOfSpeech(testInfo));
		}
		else
		{
			return InfoGetFields.getPartOfSpeechObject(mainInfo).getCanonicalPosTag().equals(InfoGetFields.getPartOfSpeechObject(testInfo).getCanonicalPosTag());
		}
	}
	
	private boolean useCanonicalPosTag = false;
}
