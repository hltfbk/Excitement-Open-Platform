/**
 * 
 */
package eu.excitementproject.eop.core.component.syntacticknowledge;

import java.io.FileNotFoundException;

import java.util.Collection;



import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;

import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import eu.excitementproject.eop.common.component.syntacticknowledge.RuleMatch;
import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResourceCloseException;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResourceException;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.FlippedBidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNodeConstructor;
import eu.excitementproject.eop.common.representation.parse.tree.match.pathmatcher.PathAllEmbeddedMatcher;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.PARSER;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.TemplateToTree;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.TemplateToTreeException;
import eu.excitementproject.eop.distsim.dependencypath.DependencyPathsFromTree;
import eu.excitementproject.eop.distsim.dependencypath.DependencyPathsFromTreeBinary;
import eu.excitementproject.eop.distsim.domains.FilterType;
import eu.excitementproject.eop.distsim.domains.RuleDirection;
import eu.excitementproject.eop.distsim.items.StringBasedElement;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
import eu.excitementproject.eop.distsim.scoring.ElementsSimilarityMeasure;
import eu.excitementproject.eop.distsim.storage.DefaultSimilarityStorage;
import eu.excitementproject.eop.distsim.storage.ElementTypeException;
import eu.excitementproject.eop.distsim.storage.SimilarityNotFoundException;
import eu.excitementproject.eop.distsim.storage.SimilarityStorage;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.redis.RedisRunException;

/**
 * Implements the SyntacticResource for DIRT rules stored in a {@link eu.excitementproject.eop.redis.SimilarityStorage}
 * 
 * @author Meni Adler
 * @since Aug 28 2013
 *
 */
@ParserSpecific({"easyfirst"})
public class SimilarityStorageBasedDIRTSyntacticResource extends SyntacticResourceSupportDIRTTemplates<Info, BasicNode> {

	
	/**
	 * Constructs a syntactic resource from configuration params, by constructing a new similarity storage from these params.
	 * @see DefaultSimilarityStorage#DefaultSimilarityStorage(ConfigurationParams)
	 * <p>Additionally, uses the param "top-n-rules" to limit the number of retrieved rules.
	 * 
	 * @throws ElementTypeException 
	 * @throws RedisRunException 
	 * @throws FileNotFoundException 
	 */
	public SimilarityStorageBasedDIRTSyntacticResource(ConfigurationParams params) throws ConfigurationException, ElementTypeException, FileNotFoundException, RedisRunException {
		String hostLeft = null;
		int portLeft = -1;
		try {
			hostLeft = params.get(Configuration.L2R_REDIS_HOST);
			portLeft = params.getInt(Configuration.L2R_REDIS_PORT);
		} catch (ConfigurationException e) {
		}		
		this.maxNumOfRetrievedRules = params.getInt(Configuration.TOP_N_RULES);
		
		if (hostLeft == null || portLeft == -1)
			this.similarityStorage = new DefaultSimilarityStorage(params);			
		else {
			String instanceName = "";
			try {
				instanceName = params.get(Configuration.INSTANCE_NAME);
			} catch (ConfigurationException e) {
				instanceName = params.getConfigurationFile().toString();
			}
			this.similarityStorage = new DefaultSimilarityStorage(hostLeft,portLeft,params.get(Configuration.RESOURCE_NAME),instanceName);
		}
		this.extractor = new DependencyPathsFromTreeBinary<Info, BasicNode>(new BasicNodeConstructor(), new DependencyPathsFromTree.VerbAdjectiveNounPredicate<Info>(), true, true);
		this.matchCriteria = new BasicMatchCriteria<Info,Info,BasicNode,BasicNode>();
	}
	
	/**
	 * Constructs a syntactic resource from an existing, initialized similarity storage, with a rule-count limit.
	 * 
	 * @param similarityStorage The storage of element similarities, which stands at the base of the rule retrieval, with a rule-count limit
	 * @param extractor For extracting dependency paths from a given BasicNode
	 */
	public SimilarityStorageBasedDIRTSyntacticResource(SimilarityStorage similarityStorage, DependencyPathsFromTreeBinary<Info, BasicNode> extractor) {
		this(similarityStorage, extractor, null);
	}

	/**
	 * Constructs a syntactic resource from an existing, initialized similarity storage, with a rule-count limit.
	 * 
	 * @param similarityStorage The storage of element similarities, which stands at the base of the rule retrieval, with a rule-count limit
	 * @param extractor For extracting dependency paths from a given BasicNode
	 * @param maxNumOfRetrievedRules The maximal number of retrieved rules, where the retrieved rules are those with the highest scores.
	 */
	public SimilarityStorageBasedDIRTSyntacticResource(SimilarityStorage similarityStorage, DependencyPathsFromTreeBinary<Info, BasicNode> extractor, Integer maxNumOfRetrievedRules) {
		this.similarityStorage = similarityStorage;
		this.extractor = extractor ;
		this.maxNumOfRetrievedRules = maxNumOfRetrievedRules;
		this.matchCriteria = new BasicMatchCriteria<Info,Info,BasicNode,BasicNode>();
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.component.Component#getComponentName()
	 */
	@Override
	public String getComponentName() {
		return similarityStorage.getComponentName();
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.component.Component#getInstanceName()
	 */
	@Override
	public String getInstanceName() {
		return similarityStorage.getInstanceName();
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResource#findMatches(eu.excitementproject.eop.common.representation.parse.tree.AbstractNode)
	 */
	@Override
	@ParserSpecific({"easyfirst"})
	public List<RuleMatch<Info, BasicNode>> findMatches(BasicNode currentTree) throws SyntacticResourceException {
		return findMatches1(currentTree,null);
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.core.component.syntacticknowledge.SyntacticResourceSupportDIRTTemplates#findMatches(eu.excitementproject.eop.common.representation.parse.tree.AbstractNode, java.util.Set)
	 */
	@Override
	public List<RuleMatch<Info, BasicNode>> findMatches(BasicNode textTree, Set<String> hypothesisTemplates) throws SyntacticResourceException
	{
		return findMatches1(textTree,hypothesisTemplates);
	}
	
	@Override
	public void close() throws SyntacticResourceCloseException {
		similarityStorage.close();
	}


	private List<RuleMatch<Info, BasicNode>> findMatches1(BasicNode textTree, Set<String> hypothesisTemplates) throws SyntacticResourceException
	{
		try {
			List<RuleMatch<Info, BasicNode>> ret = new LinkedList<RuleMatch<Info, BasicNode>>();
			for (String dependencyPath : createTemplatesForTree(textTree)) {
				
				StringBasedElement element = new StringBasedElement(dependencyPath);
				for (ElementsSimilarityMeasure leftSimilarity : getSimilarityMeasures(element,hypothesisTemplates)) {
					TemplateToTree leftTemplateConverter=new TemplateToTree(leftSimilarity.getLeftElement().toKey(),PARSER.EASYFIRST);
					leftTemplateConverter.createTree();
					
					TemplateToTree rightTemplateConverter=new TemplateToTree(leftSimilarity.getRightElement().toKey(),PARSER.EASYFIRST);
					rightTemplateConverter.createTree();
					double score = leftSimilarity.getSimilarityMeasure();
					
					SyntacticRule<Info, BasicNode>  rule = ruleFromTemplates(leftTemplateConverter,rightTemplateConverter,score);
					PathAllEmbeddedMatcher<Info, BasicNode,Info,BasicNode> matcher = 
							new PathAllEmbeddedMatcher<Info, BasicNode,Info,BasicNode>(matchCriteria);
					matcher.setTrees(textTree, leftTemplateConverter.getTree());
					matcher.findMatches();
					Collection<? extends BidirectionalMap<BasicNode, BasicNode>> matches = matcher.getMatches();
					
					for (BidirectionalMap<BasicNode, BasicNode> match : matches) {
						ret.add(new RuleMatch<Info, BasicNode>(
								new RuleWithConfidenceAndDescription<Info, BasicNode>(rule,score,leftSimilarity.getLeftElement().toKey() + "->" + leftSimilarity.getRightElement().toKey()),
								new FlippedBidirectionalMap<BasicNode, BasicNode>(match)));
					}
				}
			}
			return ret;
		} catch (Exception e) {
			throw new SyntacticResourceException(ExceptionUtils.getStackTrace(e));
		}
	}

	List<ElementsSimilarityMeasure> getSimilarityMeasures(StringBasedElement textElement, Set<String> hypothesisTemplates) throws SimilarityNotFoundException, UndefinedKeyException {
		
		if (maxNumOfRetrievedRules == 0)
			return new LinkedList<ElementsSimilarityMeasure>();
		
		if (hypothesisTemplates == null)
			return similarityStorage.getSimilarityMeasure(textElement, RuleDirection.LEFT_TO_RIGHT, FilterType.TOP_N, maxNumOfRetrievedRules);
		else {
			List<ElementsSimilarityMeasure> ret = new LinkedList<ElementsSimilarityMeasure>();

			//Option 1: One Redis access
			//Get all rules for leftElement, and filter topN rules with right element of given  hypothesisTemplates
			// Should be used for a case of reasonable number of total rules per left element
			// According to Reuters statistics:
			//     Average number of rules per element: 66.69478967988928
			//     Max number of rules per element: 733
			// this option was chosen
			for (ElementsSimilarityMeasure rule : similarityStorage.getSimilarityMeasure(textElement,RuleDirection.LEFT_TO_RIGHT)) {
				if (ret.size() == maxNumOfRetrievedRules)
					break;
				if (hypothesisTemplates.contains(rule.getRightElement().toKey()))
					ret.add(rule);
			}

			//maxNumOfRetrievedRules
			//Option 2: A Redis access per each given right template
			// Should be used for a case of huge number of total rules per left element
			// and/or small number of hypothesisTemplate
			/*for (String hypothesisTemplate : hypothesisTemplates) {
				StringBasedElement hypothesisElement = new StringBasedElement(hypothesisTemplate);
				for (ElementsSimilarityMeasure rule : similarityStorage.getSimilarityMeasure(textElement, hypothesisElement)) {
					...
				}
			}*/
			
			return ret;
		}		
	}


	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.core.component.syntacticknowledge.SyntacticResourceSupportDIRTTemplates#createTemplatesForTree(eu.excitementproject.eop.common.representation.parse.tree.AbstractNode)
	 */
	@Override
	protected Set<String> createTemplatesForTree(BasicNode tree) throws SyntacticResourceException
	{
		Set<String> ret = new HashSet<String>();
		for (String dependencyPath : extractor.stringDependencyPaths(tree)) {
			int pos1 = dependencyPath.indexOf("<");
			int pos2 = dependencyPath.lastIndexOf(">");
			ret.add(dependencyPath.substring(pos1-1,pos2+2));
		}
		return ret;
	}

	protected SyntacticRule<Info, BasicNode> ruleFromTemplates(TemplateToTree entailing, TemplateToTree entailed, double score) throws TemplateToTreeException
	{
		BidirectionalMap<BasicNode, BasicNode> mapLhsRhs = new SimpleBidirectionalMap<BasicNode, BasicNode>();
		mapLhsRhs.put(entailing.getTree(), entailed.getTree());
		if ( (entailing.getLeftVariableNode()!=null) && (entailed.getLeftVariableNode()!=null) )
		{
			mapLhsRhs.put(entailing.getLeftVariableNode(),entailed.getLeftVariableNode());
		}
		if ( (entailing.getRightVariableNode()!=null) && (entailed.getRightVariableNode()!=null) )
		{
			mapLhsRhs.put(entailing.getRightVariableNode(),entailed.getRightVariableNode());
		}
		
		
		return new SyntacticRule<Info, BasicNode>(entailing.getTree(), entailed.getTree(), mapLhsRhs);
	}
	
	SimilarityStorage similarityStorage;
	DependencyPathsFromTreeBinary<Info, BasicNode> extractor;
	Integer maxNumOfRetrievedRules;
	BasicMatchCriteria<Info,Info,BasicNode,BasicNode> matchCriteria;
}
