package ac.biu.nlp.nlp.engineml.utilities.parsetreeutils;

import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;

import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.InfoObservations;
import ac.biu.nlp.nlp.general.StringUtil;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.PartOfSpeech;


/**
 * Given a tree, this class finds the top-most verb, which is the main verb.
 * It looks for only verbs that are not "be" or "have". Thus, it is easy to find out
 * whether the given tree is merely "X is Y" or "X has Y", or it is a tree, in which the
 * main predicate is a verb, for example "I go.", "I am waiting.", "I have decided to write it."
 * <P>
 * <B>Warning: This class currently works only for Minipar</B>
 * <P>
 * // TODO HARD CODED STRING<BR>
 * // TODO The hard coded strings should be removed from this class.<BR>
 * 
 * @author Asher Stern
 * @since Feb 28, 2011
 *
 */
@LanguageDependent("English")
public class FindMainVerbHeuristic
{
	public static final Set<String> NON_CONTENT_VERBS;
	static
	{
		NON_CONTENT_VERBS = new HashSet<String>();
		NON_CONTENT_VERBS.add("be");
		NON_CONTENT_VERBS.add("have");
	}
	
	
	

	public Set<ExtendedNode> topContentVerbs(ExtendedNode tree)
	{
		Set<ExtendedNode> set = new HashSet<ExtendedNode>();
		if (isContentVerb(tree.getInfo()))
			set.add(tree);
		else
		{
			if (tree.getChildren()!=null)
			{
				for (ExtendedNode child : tree.getChildren())
				{
					set.addAll(topContentVerbs(child));
				}
			}
		}
		return set;
	}
	
	

	private static boolean isVerb(Info info)
	{
		boolean ret = false;
		if (InfoObservations.infoHasLemma(info))
		{
			PartOfSpeech pos = InfoGetFields.getPartOfSpeechObject(info);
			if (pos.getCanonicalPosTag()==CanonicalPosTag.VERB)
				ret = true;
		}
		return ret;
	}
	
	private static boolean isContentVerb(Info info)
	{
		boolean ret = false;
		if (isVerb(info))
		{
			String lemma = InfoGetFields.getLemma(info);
			if (!StringUtil.setContainsIgnoreCase(NON_CONTENT_VERBS, lemma))
				ret = true;
		}
		
		return ret;
	}

}
