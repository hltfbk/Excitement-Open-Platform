package eu.excitementproject.eop.transformations.operations.finders;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.specifications.SubstituteNodeSpecification;
import eu.excitementproject.eop.transformations.operations.specifications.SubstituteNodeSpecificationMultiWord;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.InfoObservations;


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
 * All those specifications are found by the {@link #find(TreeAndParentMap)} method, and
 * returned by the {@link #getSpecs()} method.
 * <P>
 * An instance of this class is created for each text-hypothesis pair in
 * biutee project class <code>InitializationTextTreesProcessor</code>
 * 
 * @see SubstitutionMultiWordFinder
 * 
 * @author Asher Stern
 * @since Jan 30, 2011
 *
 */
@NotThreadSafe
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
		for (ExtendedNode node : TreeIterator.iterableTree(tree.getTree()))
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
