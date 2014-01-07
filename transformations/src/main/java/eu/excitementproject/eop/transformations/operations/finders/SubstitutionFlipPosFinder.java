package eu.excitementproject.eop.transformations.operations.finders;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.common.utilities.Cache;
import eu.excitementproject.eop.common.utilities.CacheFactory;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.specifications.SubstituteNodeSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.Equalities;



/**
 * This {@linkplain Finder} finds nodes that their lemma does exist in the hypothesis tree,
 * but differ in the part-of-speech from the corresponding node in the hypothesis tree.
 * The finder returns set of specification, each specification contains the old node, and
 * a new node-ExtendedInformation for that node, such that the new node-ExtendedInformation equals to the
 * original one except the part-of-speech, which is taken from the hypothesis.
 *   
 * @author Asher Stern
 * @since February, 2011
 *
 */
public class SubstitutionFlipPosFinder implements Finder<SubstituteNodeSpecification>
{
	
	
	public SubstitutionFlipPosFinder(TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree, TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesisTree, Lemmatizer lemmatizer)
	{
		this.textTree = textTree;
		this.hypothesisTree = hypothesisTree;
		this.lemmatizer = lemmatizer;
	}
	
	@Override public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException
	{}

	@Override
	public void find() throws OperationException
	{
		try
		{
			fillMapTextNodesToHypothesisNodes();
			this.specs = new LinkedHashSet<SubstituteNodeSpecification>();
			for (ExtendedNode textNode : mapTextNodesToHypothesisNodes.keySet())
			{
				for (ExtendedNode hypothesisNode : mapTextNodesToHypothesisNodes.get(textNode))
				{
					SubstituteNodeSpecification spec = new SubstituteNodeSpecification(textNode, hypothesisNode.getInfo().getNodeInfo(),textNode.getInfo().getAdditionalNodeInformation());
					specs.add(spec);
				}
			}
		}
		catch(LemmatizerException e)
		{
			throw new OperationException("Lemmatizer failed. See nested Exception",e);
		}
	}
	
	@Override
	public Set<SubstituteNodeSpecification> getSpecs() throws OperationException
	{
		if (null==this.specs) throw new OperationException("find() was not called.");
		return this.specs;
	}
	
	
	
	private void fillMapTextNodesToHypothesisNodes() throws LemmatizerException
	{
		fillMapHypothesisNodesToLemmas();
		this.mapTextNodesToHypothesisNodes = new SimpleValueSetMap<ExtendedNode, ExtendedNode>();
		Set<ExtendedNode> textNodes = AbstractNodeUtils.treeToLinkedHashSet(textTree.getTree());
		Set<ExtendedNode> hypothesisNodes = AbstractNodeUtils.treeToLinkedHashSet(hypothesisTree.getTree());

		for (ExtendedNode textNode : textNodes)
		{
			if (textNode!=textTree.getTree()) // do nothing with the "ROOT"
			{
				String lemma = InfoGetFields.getLemma(textNode.getInfo());
				ImmutableList<String> textNodeLemmas = getLemmas(lemma);

				for (ExtendedNode hypothesisNode : hypothesisNodes)
				{
					if (hypothesisNode!=hypothesisTree.getTree()) // do nothing with the "ROOT"
					{
						if (!Equalities.areEqualNodes(textNode.getInfo(), hypothesisNode.getInfo()))
						{
							for (String textNodeLemma : textNodeLemmas)
							{
								if (StringUtil.setContainsIgnoreCase(mapHypothesisNodesToLemmas.get(hypothesisNode), textNodeLemma))
								{
									mapTextNodesToHypothesisNodes.put(textNode, hypothesisNode);
									break;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void fillMapHypothesisNodesToLemmas() throws LemmatizerException
	{
		this.mapHypothesisNodesToLemmas = new LinkedHashMap<ExtendedNode, Set<String>>();
		
		for (ExtendedNode hypothesisNode : TreeIterator.iterableTree(hypothesisTree.getTree()))
		{
			Set<String> lemmas = new LinkedHashSet<String>();
			String hypothesisLemma = InfoGetFields.getLemma(hypothesisNode.getInfo());
			ImmutableList<String> fromLemmatizer = getLemmas(hypothesisLemma);
			for (String lemmaFromLemmatizer : fromLemmatizer)
			{
				lemmas.add(lemmaFromLemmatizer);
			}
			mapHypothesisNodesToLemmas.put(hypothesisNode, lemmas);
		}
	}
	
	private ImmutableList<String> getLemmas(String word) throws LemmatizerException
	{
		ImmutableList<String> ret = null;
		boolean retrievedFromCache=false;
		if (lemmatizerCache.containsKey(word))
		{
			synchronized(lemmatizerCache)
			{
				if (lemmatizerCache.containsKey(word))
				{
					ret = lemmatizerCache.get(word);
					retrievedFromCache=true;
				}
			}
		}
		if (!retrievedFromCache)
		{
			synchronized(lemmatizer)
			{
				lemmatizer.set(word);
				lemmatizer.process();
				ret = lemmatizer.getLemmas();
			}
			lemmatizerCache.put(word,ret);
		}
		return ret;
	}
	
	
	private static Cache<String, ImmutableList<String>> lemmatizerCache =
		new CacheFactory<String, ImmutableList<String>>().getThreadSafeCache(Constants.LEMMATIZER_CACHE_CAPACITY);

	private Lemmatizer lemmatizer; // assuming it is already initiated
	private TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree;
	private TreeAndParentMap<ExtendedInfo,ExtendedNode> hypothesisTree;
	
	private ValueSetMap<ExtendedNode, ExtendedNode> mapTextNodesToHypothesisNodes = null;
	private Map<ExtendedNode, Set<String>> mapHypothesisNodesToLemmas = null;
	
	private Set<SubstituteNodeSpecification> specs = null;
	
	
	
}
