package eu.excitementproject.eop.transformations.generic.truthteller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier.InfoConverter;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.transformations.generic.truthteller.application.AnnotationRuleApplier;
import eu.excitementproject.eop.transformations.generic.truthteller.application.AnnotationRuleApplierFactory;
import eu.excitementproject.eop.transformations.generic.truthteller.application.DefaultAnnotationRuleApplier;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRule;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRuleWithDescription;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRulesBatch;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.BasicRuleAnnotations;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.RuleType;
import eu.excitementproject.eop.transformations.generic.truthteller.services.TreeUtils;
import eu.excitementproject.eop.transformations.generic.truthteller.services.TwoTreesAndTheirBidirectionalMap;
import eu.excitementproject.eop.transformations.representation.ExtendedConstructionNode;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;
import eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames;
import eu.excitementproject.eop.transformations.utilities.view.ExtendedConstructionRulesViewer;
import eu.excitementproject.eop.transformations.utilities.view.ExtendedRulesViewer;

/**
 * Applies all the annotation rules on the tree under the given ExtendedNode, assumed to be one whole sentence.
 * It returns a list containing the given sentence, and some of its entailments, annotated with {@link PredTruth} 
 * values on the verb nodes. The entailments added to the original sentence have to be 
 * computed in this class as a preprocess stage, and not in the main flow of the entailment engine, cos their 
 * computation requires additional annotation data, unavailable outside this class.
 * 
 * @author Amnon Lotan
 * @since 31/05/2011
 * 
 */
public class DefaultSentenceAnnotator implements SentenceAnnotator {

	private static final Logger logger = Logger.getLogger(DefaultSentenceAnnotator.class);
	private static final ExtendedRulesViewer TREE_VIEWER = new ExtendedRulesViewer(null);
	private static final ExtendedConstructionRulesViewer CONSTRUCTION_TREE_VIEWER = new ExtendedConstructionRulesViewer(null);
	private final AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations> CALCULATE_PT_RULE_WITH_DESCRIPTION;
	
	private final List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> rulesWithDesc;
	private final AnnotationRuleApplierFactory applierFactory;
	
	private ExtendedNode annotatedTree;
	private ExtendedNode origTree;
	private BidirectionalMap<ExtendedNode, ExtendedNode> mapOriginalToAnnotated;
	/**
	 * Ctor
	 * @param annotationRulesFile the serialized file containing all the List of {@link AnnotationRule}s
	 * @param annotationParams 
	 * @throws AnnotatorException
	 */
	@SuppressWarnings("unchecked")
	public DefaultSentenceAnnotator(File annotationRulesFile) throws AnnotatorException 
	{
		if (annotationRulesFile == null)
			throw new AnnotatorException("got null annotation rules file");
		
		AnnotationRulesBatch<ExtendedNode, BasicRuleAnnotations> rulesBatch;
		try 
		{
			FileInputStream fis = new FileInputStream(annotationRulesFile);
			ObjectInputStream in = new ObjectInputStream(fis);
			rulesBatch = (AnnotationRulesBatch<ExtendedNode, BasicRuleAnnotations>) in.readObject();
			in.close();
		} catch (FileNotFoundException e) {
			throw new AnnotatorException("File at " + TransformationsConfigurationParametersNames.ANNOTATION_RULES_FILE + " was not found", e);
		} catch (Exception e) {
			throw new AnnotatorException("Error unserializing the annotation rules from " + TransformationsConfigurationParametersNames.ANNOTATION_RULES_FILE, e);
		}
		
		
		if (rulesBatch == null)
			throw new AnnotatorException("read null rules batch from " + annotationRulesFile);
		if (rulesBatch.getAnnotationRules() == null)
			throw new AnnotatorException("read null main-rules-list from " + annotationRulesFile);

		this.rulesWithDesc = rulesBatch.getAnnotationRules();
		
		// after all rules are done, run a special PT calculator that must run last
		CALCULATE_PT_RULE_WITH_DESCRIPTION = new AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>(
					new AnnotationRule<ExtendedNode, BasicRuleAnnotations>(null, null, RuleType.COMPUTE_PT), "(last rule:) compute PT as the product of NU and CT");
		this.rulesWithDesc.add(CALCULATE_PT_RULE_WITH_DESCRIPTION);
		
		applierFactory = new AnnotationRuleApplierFactory(rulesBatch.getRecursiveCtCalcAnnotationRules()); 
	}
	
	/**
	 * Ctor
	 * @throws ConfigurationException 
	 * @throws AnnotatorException 
	 */
	public DefaultSentenceAnnotator(ConfigurationParams annotationParams) throws AnnotatorException, ConfigurationException {
		this( annotationParams.getFile(TransformationsConfigurationParametersNames.ANNOTATION_RULES_FILE));
	}
	
	@Override
	public void setTree(ExtendedNode tree) throws AnnotatorException {
		if (tree == null)
			throw new AnnotatorException("got null tree");
		origTree = tree;
		mapOriginalToAnnotated = null;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.annotation.TreeAnnotator#annotate()
	 */
	@Override
	public void annotate() throws AnnotatorException {
		
		if (logger.isDebugEnabled())
			try {	TREE_VIEWER.printTree(origTree, true);	}
			catch (TreeStringGeneratorException e) { throw new AnnotatorException("Error printing the tree. See nested",e );	}
		
		// copy orig tree
		TreeCopier<ExtendedInfo, ExtendedNode, ExtendedInfo, ExtendedNode> treeCopier = 
				new TreeCopier<ExtendedInfo, ExtendedNode, ExtendedInfo, ExtendedNode>(origTree, DUMMY_INFO_CONVERTER, new ExtendedNodeConstructor());  
		treeCopier.copy();
		annotatedTree = treeCopier.getGeneratedTree();
		mapOriginalToAnnotated = treeCopier.getNodesMap();
		
		TwoTreesAndTheirBidirectionalMap<ExtendedNode, ExtendedConstructionNode> nodeToConstructionNodeData = TreeUtils.dupTreeToConstructionTree(origTree);
		ExtendedConstructionNode constructionTree = nodeToConstructionNodeData.getGeneratedTree();
		
		/* 
		 * all annotation rules are matched and applied to the text like substitution rules that delete the matched LHSs.
		 */
		String lastDescription = "";
		for (AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>  ruleWithDesc : rulesWithDesc)
		{
			if (!lastDescription.equals(ruleWithDesc.getDescription()))
			{
				lastDescription = ruleWithDesc.getDescription();
				logger.debug("Applying rule: " + lastDescription);
			}
			
			AnnotationRule<ExtendedNode, BasicRuleAnnotations> rule = ruleWithDesc.getRule();
			AnnotationRuleApplier<ExtendedConstructionNode> applier = applierFactory.getAnnotationRuleApplier(rule);
			
			if (logger.isDebugEnabled() && !(applier instanceof DefaultAnnotationRuleApplier))
			{
				try {	CONSTRUCTION_TREE_VIEWER.printTree(constructionTree, true);		}	
				catch (TreeStringGeneratorException e) { throw new AnnotatorException("See nested",e );	}
			}
			
			// apply normal annotation rule
			try {
				applier.annotateTreeWithOneRule(constructionTree);
			}
			catch (AnnotatorException e) { throw new AnnotatorException("Error applying rule: " +ruleWithDesc.getDescription(), e); }
		}
		
		TwoTreesAndTheirBidirectionalMap<ExtendedConstructionNode, ExtendedNode> constructionNodeToNodeData = TreeUtils.dupConstructionTreeToTree(constructionTree);
		annotatedTree = constructionNodeToNodeData.getGeneratedTree();
		mapOriginalToAnnotated = TreeUtils.crossRefMaps(nodeToConstructionNodeData.getBidiMap(), constructionNodeToNodeData.getBidiMap());
	}

	@Override
	public ExtendedNode getAnnotatedTree() throws AnnotatorException {
		if (annotatedTree == null)
				throw new AnnotatorException("You must call annotate() before calling this method");
		return annotatedTree;
	}

	@Override
	public BidirectionalMap<ExtendedNode, ExtendedNode> getMapOriginalToAnnotated()	throws AnnotatorException {
		if (mapOriginalToAnnotated == null)
			throw new AnnotatorException("No map available. You must  call annotaate() before calling this method.");
		return mapOriginalToAnnotated;
	}

	////////////////////////////////// PRIVATE	//////////////////////////////////////////////////////
	
	/**
	 a private anonymous class that naively  copies node infos 
	 * 
	 */
	private static InfoConverter<ExtendedNode, ExtendedInfo> DUMMY_INFO_CONVERTER = new InfoConverter<ExtendedNode, ExtendedInfo>() {
		@Override
		public ExtendedInfo convert(ExtendedNode node) {
			return new ExtendedInfo(node.getInfo(), node.getInfo().getAdditionalNodeInformation());
		}
	};
}
