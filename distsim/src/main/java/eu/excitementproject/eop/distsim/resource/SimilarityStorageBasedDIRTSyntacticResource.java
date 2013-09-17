/**
 * 
 */
package eu.excitementproject.eop.distsim.resource;

import java.util.Collection;


import java.util.LinkedList;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import eu.excitementproject.eop.common.component.syntacticknowledge.RuleMatch;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResource;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResourceException;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.DependencyPathsFromTree;
import eu.excitementproject.eop.common.representation.parse.DependencyPathsFromTreeBinary;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNodeConstructor;
import eu.excitementproject.eop.common.representation.parse.tree.match.pathmatcher.PathAllEmbeddedMatcher;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.core.component.syntacticknowledge.BasicMatchCriteria;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.PARSER;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.TemplateToTree;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.TemplateToTreeException;
import eu.excitementproject.eop.distsim.domains.FilterType;
import eu.excitementproject.eop.distsim.domains.RuleDirection;
import eu.excitementproject.eop.distsim.items.StringBasedElement;
import eu.excitementproject.eop.distsim.scoring.ElementsSimilarityMeasure;
import eu.excitementproject.eop.distsim.storage.DefaultSimilarityStorage;
import eu.excitementproject.eop.distsim.storage.SimilarityStorage;
import eu.excitementproject.eop.distsim.util.Configuration;

/**
 * @author Meni Adler
 * @since Aug 28 2013
 *
 */
public class SimilarityStorageBasedDIRTSyntacticResource implements SyntacticResource<Info, BasicNode> {

	
	public SimilarityStorageBasedDIRTSyntacticResource(ConfigurationParams params) throws ConfigurationException {
		this(
				new DefaultSimilarityStorage(params),
				new DependencyPathsFromTreeBinary<Info, BasicNode>(new BasicNodeConstructor(), new DependencyPathsFromTree.VerbAdjectiveNounPredicate<Info>(), true, true),
				params.getInt(Configuration.TOP_N_RULES)
		);
	}
	
	public SimilarityStorageBasedDIRTSyntacticResource(SimilarityStorage similarityStorage, DependencyPathsFromTreeBinary<Info, BasicNode> extractor) {
		this(similarityStorage, extractor, null);
	}

	public SimilarityStorageBasedDIRTSyntacticResource(SimilarityStorage similarityStorage, DependencyPathsFromTreeBinary<Info, BasicNode> extractor, Integer maxNumOfRetrievedRules) {
		this.similarityStorage = similarityStorage;
		this.extractor = extractor ;
		this.maxNumOfRetrievedRules = maxNumOfRetrievedRules;
		this.matchCriteria = new BasicMatchCriteria<Info,Info,BasicNode,BasicNode>();
		//matchCriteria.setUseCanonicalPosTag(true);
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

	@Override
	@ParserSpecific({"easyfirst"})
	public List<RuleMatch<Info, BasicNode>> findMatches(BasicNode currentTree) throws SyntacticResourceException {
		
		try {
			
			//debug
			//System.out.println("root: " + currentTree.getInfo());

			List<RuleMatch<Info, BasicNode>> ret = new LinkedList<RuleMatch<Info, BasicNode>>();
			for (String dependencyPath : extractor.stringDependencyPaths(currentTree)) {
				
				int pos1 = dependencyPath.indexOf("<");
				int pos2 = dependencyPath.lastIndexOf(">");
				StringBasedElement element = new StringBasedElement(dependencyPath.substring(pos1-1,pos2+2));
				
				//debug
				//System.out.println("dp: " + element.toKey());
				
				for (ElementsSimilarityMeasure leftSimilarity : 
						similarityStorage.getSimilarityMeasure(element, RuleDirection.LEFT_TO_RIGHT, FilterType.TOP_N, maxNumOfRetrievedRules)) {
					
					//debug
					//System.out.println("left: " + leftSimilarity.getLeftElement().toKey());
					//System.out.println("right: " + leftSimilarity.getRightElement().toKey());

					TemplateToTree leftTemplateConverter=new TemplateToTree(leftSimilarity.getLeftElement().toKey(),PARSER.EASYFIRST);
					leftTemplateConverter.createTree();
					
					TemplateToTree rightTemplateConverter=new TemplateToTree(leftSimilarity.getRightElement().toKey(),PARSER.EASYFIRST);
					rightTemplateConverter.createTree();
					double score = leftSimilarity.getSimilarityMeasure();
					
					SyntacticRule<Info, BasicNode>  rule = ruleFromTemplates(leftTemplateConverter,rightTemplateConverter,score);
					PathAllEmbeddedMatcher<Info, BasicNode,Info,BasicNode> matcher = 
							new PathAllEmbeddedMatcher<Info, BasicNode,Info,BasicNode>(matchCriteria);
					matcher.setTrees(currentTree, leftTemplateConverter.getTree());
					matcher.findMatches();
					Collection<? extends BidirectionalMap<BasicNode, BasicNode>> matches = matcher.getMatches();
					
					for (BidirectionalMap<BasicNode, BasicNode> match : matches) {
						
						//debug
						//System.out.println("match!");

						ret.add(new RuleMatch<Info, BasicNode>(rule,match));
					}
				}
			}
			return ret;
		} catch (Exception e) {
			throw new SyntacticResourceException(ExceptionUtils.getStackTrace(e));
		}
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

	@Override
	public List<RuleMatch<Info, BasicNode>> findMatches(BasicNode textTree, BasicNode hypothesisTree) throws SyntacticResourceException {
		throw new UnsupportedOperationException();
	}
	
	SimilarityStorage similarityStorage;
	DependencyPathsFromTreeBinary<Info, BasicNode> extractor;
	Integer maxNumOfRetrievedRules;
	BasicMatchCriteria<Info,Info,BasicNode,BasicNode> matchCriteria;

}
