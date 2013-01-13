package ac.biu.nlp.nlp.engineml.operations.finders;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.specifications.SubstituteNodeSpecification;
import ac.biu.nlp.nlp.engineml.operations.specifications.SubstituteNodeSpecificationMultiWord;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.InfoObservations;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNodeUtils;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.Utils;


/**
 * This finder finds all {@link SubstituteNodeSpecification} that specify substitution of
 * a lemma in the text by a lemma in the hypothesis, if the text lemma's words are contained
 * in the hypothesis lemma or vice versa.
 * <P>
 * The finder finds all lemmas in the text and in the hypothesis that are multi-words.
 * Then, if a text lemma is contained in a multi-word lemma of the hypothesis, or vice versa,
 * then the appropriate {@link SubstituteNodeSpecification} is created to replace the
 * text lemma by the hypothesis lemma.
 * <P>
 * 
 * All those specifications are found by the {@link #find(TreeAndParentMap<Info,EnglishNode>)} method, and
 * returned by the {@link #getSpecs()} method.
 * 
 * @author Asher Stern
 * @since Jan 30, 2011
 *
 */
public class SubstitutionMultiWordUnderlyingFinder
{
	public SubstitutionMultiWordUnderlyingFinder(TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesisTree)
	{
		this.hypothesisTree = hypothesisTree;
		hypothesisNodesToMultiWord = buildMapForTree(this.hypothesisTree);
	}
	
	public void find(TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree)
	{
		this.textTree = textTree;
		textNodesToMultiWord = buildMapForTree(this.textTree);
		findUsingMaps();
	}
	
	public Set<SubstituteNodeSpecificationMultiWord> getSpecs() throws OperationException
	{
		if (null==specs) throw new OperationException("You forgot to call find()");
		return specs;
	}

	private void findUsingMaps()
	{
		specs = new LinkedHashSet<SubstituteNodeSpecificationMultiWord>();
		for (ExtendedNode textNode : textNodesToMultiWord.keySet())
		{
			for (ExtendedNode hypothesisNode : hypothesisNodesToMultiWord.keySet())
			{
				if (
						InfoObservations.infoHasLemma(hypothesisNode.getInfo())
						&&
						(!(InfoGetFields.getLemma(textNode.getInfo()).equals(InfoGetFields.getLemma(hypothesisNode.getInfo()))))
						)
				{
					List<String> textNodeWords = textNodesToMultiWord.get(textNode);
					List<String> hypothesisNodeWords = hypothesisNodesToMultiWord.get(hypothesisNode);
					
					Collection<String> intersection = Utils.intersect(textNodeWords, hypothesisNodeWords, new LinkedList<String>());
					if (intersection.size()>0)
					{
						SubstituteNodeSpecificationMultiWord spec = new SubstituteNodeSpecificationMultiWord(textNode, hypothesisNode.getInfo().getNodeInfo(), textNode.getInfo().getAdditionalNodeInformation(),textNodeWords,hypothesisNodeWords);
						specs.add(spec);
					}
				}

			}
		}
	}
	
	private Map<ExtendedNode, List<String>> buildMapForTree(TreeAndParentMap<ExtendedInfo,ExtendedNode> tree)
	{
		Map<ExtendedNode, List<String>> ret = new LinkedHashMap<ExtendedNode, List<String>>();
		Set<ExtendedNode> nodes = AbstractNodeUtils.treeToSet(tree.getTree());
		for (ExtendedNode node : nodes)
		{
			String lemma = InfoGetFields.getLemma(node.getInfo());
			List<String> lemmaAsList = StringUtil.stringToWords(lemma);
			if (lemmaAsList.size()>0)
			{
				ret.put(node, lemmaAsList);
			}
		}
		return ret;
	}
	
	private TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree;
	private TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesisTree;
	
	private Map<ExtendedNode, List<String>> hypothesisNodesToMultiWord;
	private Map<ExtendedNode, List<String>> textNodesToMultiWord;
	
	
	
	private Set<SubstituteNodeSpecificationMultiWord> specs = null;



}
