package ac.biu.nlp.nlp.instruments.ner;

import ac.biu.nlp.nlp.general.match.MatchFinder;
import ac.biu.nlp.nlp.general.match.Operator;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultNodeInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.NamedEntity;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.NodeInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.SyntacticInfo;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicConstructionNode;


/**
 * 
 * @author Asher Stern
 *
 */
public class NamedEntityMergeServices
{
	public static MatchFinder<NamedEntityWord, BasicConstructionNode> getMatchFinder()
	{
		return new MatchFinder<NamedEntityWord, BasicConstructionNode>()
		{

			public boolean areMatch()
			{
				boolean ret = false;
				try{ret = node.getInfo().getNodeInfo().getWord().equalsIgnoreCase(neWord.getWord());}catch(Exception e){}
				return ret;
			}

			public void set(NamedEntityWord lhs, BasicConstructionNode rhs)
			{
				this.neWord = lhs;
				this.node = rhs;
			}

			private NamedEntityWord neWord;
			private BasicConstructionNode node;

		};
		
	}
	
	
	public static Operator<NamedEntityWord, BasicConstructionNode> getOperator()
	{
		return new Operator<NamedEntityWord, BasicConstructionNode>()
		{
			public void makeOperation()
			{
				NodeInfo nodeInfo = null;
				try{nodeInfo = node.getInfo().getNodeInfo();}catch(Exception e){}
				String word = null;
				String lemma = null;
				int serial = 0;
				NamedEntity namedEntity = null;
				SyntacticInfo syntacticInfo = null;
				if (nodeInfo != null)
				{
					word = nodeInfo.getWord();
					lemma = nodeInfo.getWordLemma();
					serial = nodeInfo.getSerial();
					syntacticInfo = nodeInfo.getSyntacticInfo();
				}
				namedEntity = neWord.getNamedEntity();
				
				Info oldInfo = node.getInfo();
				
				node.setInfo(new DefaultInfo(oldInfo.getId(),new DefaultNodeInfo(word,lemma,serial,namedEntity,syntacticInfo),oldInfo.getEdgeInfo()));
			}
			

			public void set(NamedEntityWord lhs, BasicConstructionNode rhs)
			{
				this.neWord = lhs;
				this.node = rhs;
			}
			
			private NamedEntityWord neWord;
			private BasicConstructionNode node;
		};
	}

}
