package ac.biu.nlp.nlp.instruments.coreference.merge.english;

import ac.biu.nlp.nlp.instruments.coreference.merge.CorefMatchFinder;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

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
