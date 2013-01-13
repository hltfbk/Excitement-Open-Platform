package ac.biu.nlp.nlp.engineml.rteflow.preprocess;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.TreeUtilities;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityMergeServices;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizer;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizerException;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityWord;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.BasicParser;
import eu.excitementproject.eop.common.representation.parse.ParserRunException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.match.Matcher;

/**
 * 
 * @author Asher Stern
 * @since Jun 5, 2011
 *
 */
public class PreprocessUtilities
{
	public static BasicNode generateParseTree(String sentence, BasicParser parser, NamedEntityRecognizer neRecognizer, boolean recognizeNameEntities) throws ParserRunException, NamedEntityRecognizerException
	{
		logger.debug("Parsing...");
		parser.setSentence(sentence);
		parser.parse();
		logger.debug("Parsing done by parser.");
		if (recognizeNameEntities)
		{
			logger.debug("Resolving Named Entities...");
			addNeToNodes(parser.getNodesOrderedByWords(),neRecognizer);
			logger.debug("Resolving Named Entities done.");
			BasicConstructionNode mutableTree = parser.getMutableParseTree();
			addNeToAntecedents(mutableTree);
		}
		BasicNode tree = parser.getParseTree();
		
		if (!TreeUtilities.isArtificialRoot(tree))
		{
			tree = TreeUtilities.addArtificialRoot(tree);
		}
		
		return tree;
	}
	
	public static <T, S extends AbstractNode<T, S>> void integrateParserAntecedentToCoreference(List<S> textTrees, TreeCoreferenceInformation<S> coreferenceInformation) throws TreeCoreferenceInformationException
	{
		for (S tree : textTrees)
		{
			for (S node : AbstractNodeUtils.treeToSet(tree))
			{
				if (!coreferenceInformation.isNodeExist(node))
				{
					if (node.getAntecedent()!=null)
					{
						S deepAntecedent = AbstractNodeUtils.getDeepAntecedentOf(node);
						if (coreferenceInformation.isNodeExist(deepAntecedent))
						{
							Integer deepAntecedentGroupId = coreferenceInformation.getIdOf(deepAntecedent);
							coreferenceInformation.addNodeToGroup(deepAntecedentGroupId, node);
						}
					}
				}
			}
		}
	}
	
	
	private static void addNeToNodes(ArrayList<BasicConstructionNode> nodes, NamedEntityRecognizer neRecognizer) throws NamedEntityRecognizerException
	{
		List<String> words = new ArrayList<String>(nodes.size());
		for (BasicConstructionNode node : nodes)
		{
			String word = null;
			try{word = node.getInfo().getNodeInfo().getWord();}catch(NullPointerException e){}
			if (word!=null)
				words.add(word);
		}
		neRecognizer.setSentence(words);
		neRecognizer.recognize();
		List<NamedEntityWord> neWords = neRecognizer.getAnnotatedSentence();
		if (logger.isDebugEnabled())
		{
			StringBuffer sb = new StringBuffer();
			for (NamedEntityWord neWord : neWords)
			{
				if (neWord.getNamedEntity()!=null)
				{
					sb.append(neWord.getNamedEntity().name());
					sb.append("{");
				}
				sb.append(neWord.getWord());
				if (neWord.getNamedEntity()!=null)
				{
					sb.append("}");
				}
				sb.append(" ");
			}
			logger.debug("Sentence with Named Entities: "+sb.toString());
		}
		Matcher<NamedEntityWord, BasicConstructionNode> matcher = new Matcher<NamedEntityWord, BasicConstructionNode>(neWords.iterator(), nodes.iterator(),NamedEntityMergeServices.getMatchFinder(),NamedEntityMergeServices.getOperator());
		matcher.makeMatchOperation();
	}
	
	
	private static void addNeToAntecedents(BasicConstructionNode mutableParseTree)
	{
		Set<BasicConstructionNode> mutableNodes = AbstractNodeUtils.treeToSet(mutableParseTree);
		for (BasicConstructionNode mutableNode : mutableNodes)
		{
			if (mutableNode.getAntecedent()!=null)
			{
				BasicConstructionNode antecedent = AbstractNodeUtils.getDeepAntecedentOf(mutableNode);
				if (antecedent.getInfo().getNodeInfo().getNamedEntityAnnotation()!=null)
				{
					NamedEntity ne = antecedent.getInfo().getNodeInfo().getNamedEntityAnnotation();
					Info newInfo = new DefaultInfo(mutableNode.getInfo().getId(), new DefaultNodeInfo(mutableNode.getInfo().getNodeInfo().getWord(), mutableNode.getInfo().getNodeInfo().getWordLemma(), mutableNode.getInfo().getNodeInfo().getSerial(), ne, mutableNode.getInfo().getNodeInfo().getSyntacticInfo()), mutableNode.getInfo().getEdgeInfo());
					mutableNode.setInfo(newInfo);
				}
			}
		}
		
	}

	private static final Logger logger = Logger.getLogger(PreprocessUtilities.class);
}
