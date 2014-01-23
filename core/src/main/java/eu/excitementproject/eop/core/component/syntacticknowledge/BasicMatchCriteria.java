package eu.excitementproject.eop.core.component.syntacticknowledge;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultMatchCriteria;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.match.AllEmbeddedMatcher;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatchCriteria;
import eu.excitementproject.eop.common.representation.parse.tree.match.Matcher;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.WildcardPartOfSpeech;

/**
 * 
 * An implementation of {@link MatchCriteria} based on syntactic criteria.
 * Used by {@link AllEmbeddedMatcher} and {@link Matcher}.
 * 
 * 
 *
 * @param <TM> The information type of the "main node"s (TM: T = type, M = main) (e.g. {@link Info}) 
 * @param <TT> The information type of the "tested node"s (TT: T = type, T = tested) (e.g. {@link Info})
 * @param <SM> The "main node"s type (SM: S = self, M = main) (e.g. {@link BasicNode})
 * @param <ST> The "tested node"s type (SM: S = self, T = tested) (e.g. {@link BasicNode})
 * 
 * @author Meni Adler
 * @since Sep 16, 2013
 *
 */
public class BasicMatchCriteria<TM extends Info,TT extends Info,SM extends AbstractNode<TM, SM>, ST extends AbstractNode<TT, ST>>  implements MatchCriteria<TM,TT,SM,ST> 
{
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.representation.parse.tree.match.MatchCriteria#nodesMatch(eu.excitementproject.eop.common.representation.parse.tree.AbstractNode, eu.excitementproject.eop.common.representation.parse.tree.AbstractNode)
	 */
	@Override
	public boolean nodesMatch(SM mainNode, ST testNode)
	{
		if ( (mainNode==null) && (testNode==null) )return true;
		else if ( (mainNode==null) || (testNode==null) )return false;
		else return this.nodesInfoMatch(mainNode.getInfo(), testNode.getInfo());
	}

	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.representation.parse.tree.match.MatchCriteria#edgesMatch(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean edgesMatch(TM mainInfo, TT testInfo)
	{
		if (InfoGetFields.getRelation(testInfo).equals(WILDCARD_RELATION))
			return true;	// wildcard in the rule!
		
		return defaultMatchCriteria.edgesMatch(mainInfo, testInfo);
	}
	
	public boolean nodesInfoMatch(TM mainInfo, TT testInfo)
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
				simplerPos(textPartOfSpeech.getCanonicalPosTag())==simplerPos(rulePartOfSpeech.getCanonicalPosTag())
				);
	}

	private DefaultMatchCriteria defaultMatchCriteria = new DefaultMatchCriteria();
	public static final String WILDCARD_RELATION = "*";
}
