package eu.excitementproject.eop.lap.biu.en.coreference.merge.english;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.coreference.merge.CorefMatchFinder;

public class EnglishCorefMatchFinder extends CorefMatchFinder<BasicNode>
{
	@Override
	protected String getWordOf(BasicNode node)
	{
		String ret = null;
		try
		{
			ret = node.getInfo().getNodeInfo().getWord();
		}
		catch(NullPointerException e)
		{}
		return ret;
	}
}
