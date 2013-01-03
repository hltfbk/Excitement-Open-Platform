package ac.biu.nlp.nlp.instruments.coreference.merge.english;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import ac.biu.nlp.nlp.instruments.coreference.CoreferenceResolver;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationException;
import ac.biu.nlp.nlp.instruments.coreference.merge.CorefMatchFinder;
import ac.biu.nlp.nlp.instruments.coreference.merge.CorefMergeException;
import ac.biu.nlp.nlp.instruments.coreference.merge.CorefMerger;
import ac.biu.nlp.nlp.instruments.coreference.merge.CorefOperator;
import ac.biu.nlp.nlp.instruments.coreference.merge.GenericCorefMerger;
import ac.biu.nlp.nlp.instruments.coreference.merge.WordWithCoreferenceTag;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNodeUtils;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

/**
 * Merges the output of a parser, with the output of a co-reference system.
 * <P>
 * <B>Note: Don't use this class directly. Use {@link CoreferenceResolver}. Use this class
 * only for creating subclasses of {@link CoreferenceResolver}</B>
 * <P>
 * The merging is done by creating a {@link TreeCoreferenceInformation} object
 * that represents the co-reference relations between the trees' nodes.
 * <BR>
 * The parse trees (and their nodes) are not changed.
 * <P>
 * The assumption is that the coreference system output is given as a list
 * of {@linkplain WordWithCoreferenceTag}. More general class is {@link CoreferenceResolver} that
 * has no assumptions on the coreference system output.
 * <BR>
 * 
 * 
 * 
 * @see CoreferenceResolver
 * 
 * @author Asher Stern
 *
 */
public class EnglishCorefMerger implements CorefMerger<BasicNode>
{
	public EnglishCorefMerger(List<BasicNode> treesRootsList,List<WordWithCoreferenceTag> corefSystemOutput) throws CorefMergeException
	{
		if (null==treesRootsList) throw new CorefMergeException("null==treesRootsList");
		if (null==corefSystemOutput) throw new CorefMergeException("null==corefSystemOutput");
		
		CorefOperator<BasicNode> englishCorefOperator = new CorefOperator<BasicNode>();
		CorefMatchFinder<BasicNode> englishCorefMatchFinder = new EnglishCorefMatchFinder();
		List<List<BasicNode>> listNodeWords = new ArrayList<List<BasicNode>>(treesRootsList.size());
		for (BasicNode treeRoot : treesRootsList)
		{
			listNodeWords.add(wordsListFromTree(treeRoot));
		}
		
		merger = new GenericCorefMerger<BasicNode>(corefSystemOutput, listNodeWords, englishCorefMatchFinder, englishCorefOperator); 
	}

	public void merge() throws TreeCoreferenceInformationException, CorefMergeException
	{
		merger.merge();
	}

	public TreeCoreferenceInformation<BasicNode> getCoreferenceInformation() throws CorefMergeException
	{
		return merger.getCoreferenceInformation();
	}


	
	protected static List<BasicNode> wordsListFromTree(BasicNode treeRoot)
	{
		List<BasicNode> ret = new LinkedList<BasicNode>();
		TreeMap<Integer, BasicNode> mapSerialToNode = new TreeMap<Integer, BasicNode>();
		Set<BasicNode> setNodes =  AbstractNodeUtils.treeToSet(treeRoot);
		if (setNodes!=null)
		{
			setNodes = getWithNoAntecedent(setNodes);
			for (BasicNode node : setNodes)
			{
				try
				{
					int serial = node.getInfo().getNodeInfo().getSerial();
					String word = node.getInfo().getNodeInfo().getWord();
					if (word != null)
					{
						mapSerialToNode.put(serial,node);
					}
				}
				catch(NullPointerException e)
				{}
			}

			for (Integer serial : mapSerialToNode.keySet())
			{
				ret.add(mapSerialToNode.get(serial));
			}
		}
		
		
		return ret;
		
	}
	
	protected static Set<BasicNode> getWithNoAntecedent(Set<BasicNode> setNodes)
	{
		Set<BasicNode> nodesWithNoAntecedent = new LinkedHashSet<BasicNode>();
		for (BasicNode node : setNodes)
		{
			if (null==node.getAntecedent())
			{
				nodesWithNoAntecedent.add(node);
			}
		}
		return nodesWithNoAntecedent;
	}
	


	
	protected GenericCorefMerger<BasicNode> merger;

}
