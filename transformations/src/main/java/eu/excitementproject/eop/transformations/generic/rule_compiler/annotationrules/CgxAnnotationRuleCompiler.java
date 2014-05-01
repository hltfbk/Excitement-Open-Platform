package eu.excitementproject.eop.transformations.generic.rule_compiler.annotationrules;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;
import eu.excitementproject.eop.transformations.generic.rule_compiler.Constants;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.CgxDomParser;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.CgxMultipleChoiceExpander;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.CgxReadingUtils;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.GenericAlignment;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.RuleBuildingUtils;
import eu.excitementproject.eop.transformations.generic.rule_compiler.utils.PairSet;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRule;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.RuleAnnotations;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.RuleType;


/**
 * This class helps {@link AnnotationRuleCompiler} in compiling rule files into {@link AnnotationRule}s, by performing some operations specific to
 * Charger(TM) CGX xml files.
 * <p>
 * 
 * a {@link AnnotationRule} is composed of:<br>
 * 	Left Hand Side == a tree of AnnotatedNodes, each containing:<br>
 * 		String ID,<br>
 * 		String word,<br>
 * 		String lemma,<br>
 * 		String PartOfSpeech<br>
 * 		DependencyRelation - for example "subj", "subject", "s".<br>
 *  A {@link RuleAnnotations} record for each LHS node whose annotations should be changed<br>
 * 	Map == a map from (part of) the left hand nodes to the right hand side annotations.<br> 
 * 
 * @author Amnon Lotan
 * @since 18/06/2011
 * 
 * @param <I>	node {@link Info} type
 * @param <N>	{@link AbstractNode} type 
 * @param <CN>	{@link AbstractConstructionNode} type. Should be like the N node type, but with a {@link AbstractConstructionNode}{@code .setInfo(Info)} method
 * @param <A>	A {@link RuleAnnotations} type representing the annotation values records on the right side of the rule 
 */
public class CgxAnnotationRuleCompiler<I extends Info, N extends AbstractNode<I, N>, CN extends AbstractConstructionNode<I, CN>, A extends RuleAnnotations> 
{
	private final AnnotationRuleCompileServices<I, N, CN, A> compilationServices;
	/**
	 * A map used for calling the expandMultipleChoiceParameters method
	 */
	private final Map<String, Set<String>> extraMultipleChoiceParameters = new LinkedHashMap<String, Set<String>>();

	private final  String REGULAR_MAPPING_LABEL;


	/**
	 * Ctor
	 * @param predicateList an array of all listed predicates
	 * @param compilationServices utility class used for fine details of instantiating {@link NodeInfo} and {@link EdgeInfo}
	 */
	public CgxAnnotationRuleCompiler(Set<String> predicateList, AnnotationRuleCompileServices<I, N, CN, A> compilationServices) 
	{
		this.compilationServices = compilationServices;
		
		extraMultipleChoiceParameters.put(Constants.PREDICATE_LIST_LABEL, predicateList);
		
		REGULAR_MAPPING_LABEL = compilationServices.getFullAlignmentTypeString();
	}
	
	/**
	 * make one rule out of one GCX XML file's text
	 * 
	 * @param ruleText
	 * @return
	 * @throws AnnotationCompilationException
	 */
	public AnnotationRule<N, A> makeRule(String ruleText) throws CompilationException
	{
		RuleType ruleType = CgxReadingUtils.readCgxRuleType(ruleText);
		if (!ruleType.isAnnotation())
			throw new AnnotationCompilationException(ruleType + " is not a valid ruleType value. Only "+RuleType.ANNOTATION+" and other annotation rule types are allowed");

		AnnotationRule<N, A> rule = makeAnnotationRule(ruleText, ruleType);
		
		if (rule == null)
			throw new AnnotationCompilationException("Could not complie this file into a rule, for some reason");
		
		return rule;
	}
	
	/**
	 * Find all multiple option attributes (if exist) in the file (like lemma="lemma1\lemma2\lemma3..."), 
	 * 	and return one doc per full attributes' assignment
	 * 
	 * 1. read the file's text
	 * 2. parse all the nodes out of the xml
	 * 3. after parsing, record all the multiple option parameters (like lemma="lemma1\lemma2\lemma3...")
	 * 4. create a new doc for each full selection of all the multiple option parameters
	 */
	public Set<String> expandMultipleChoiceParameters(String cgxText) throws CompilationException 
	{
		List<String> expandedTexts = CgxMultipleChoiceExpander.expandMultipleChoiceParameters(cgxText, extraMultipleChoiceParameters);
		
		// perform any other special textual expansions the  compilationServices may require
		return  compilationServices.doSpecialRuleTextExpantion(expandedTexts);  
	}
	
	/////////////////////////////////////// PRIVATE ///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Make one normal {@link AnnotationRule} out of one GCX XML file's text
	 * @param cgxText
	 * @param ruleType 
	 * @return
	 * @throws AnnotationCompilationException 
	 */
	private AnnotationRule<N, A> makeAnnotationRule(String cgxText, RuleType ruleType) throws CompilationException
	{
		CgxDomParser cgxDomParser = new CgxDomParser(cgxText);
	  
	  	//	 parse nodes into a map from IDs to node labels
		Map<Long, String> nodeLabelsMap = cgxDomParser.parseNodes(  );	// map CGX element ID to node
		
	  	// 	parse the undirected edges, that stretch between the nodes
	  	PairSet<Long> undirectedEdges = cgxDomParser.parseEdges();
	  	
	  	// now that we know which nodes are in the trees, return the mappings (from some LHS nodes to RHS nodes)
	  	List<GenericAlignment> alignments = cgxDomParser.parseMappings();
	  	
	  	Map<Long, CN> nodesMap = new LinkedHashMap<Long, CN>();
	  	CN lhsRoot = RuleBuildingUtils.buildTreeUnderLabel(Constants.LHS, nodeLabelsMap, undirectedEdges, nodesMap, compilationServices);
	  	RuleBuildingUtils.sanityCheckRuleTree(lhsRoot);
	  	RuleBuildingUtils.sanityCheckEdges(undirectedEdges, nodesMap.keySet());

	  	Map<Long, A> mapIdtoAnnotations = new LinkedHashMap<Long, A>();
	  	Map<CN, A> annotationAlignments = buildRegularAlignmentsMap(alignments, nodeLabelsMap, nodesMap, mapIdtoAnnotations);
	  	
	  	new LinkedHashMap<PartialAlignment<CN, A>, String>();
	  	List<PartialAlignment<CN, A>> partialAlignments = buildPartialMappingsMap(alignments, nodesMap, mapIdtoAnnotations);
	  	
		// map all the partial mappings
		this.compilationServices.performPartialMappings(partialAlignments);

		AnnotationRule<N, A> rule = buildAnnotationRule(lhsRoot, annotationAlignments, compilationServices, ruleType);
		
		return rule;
	}



	/**
	 * Cross ref the given map from edge IDs to labels, with the map from IDs to nodes, with the alignment map from nodes to annotations  
	 * @param alignments
	 * @param nodesMap
	 * @param mapIdtoAnnotations 
	 * @return
	 * @throws AnnotationCompilationException 
	 */
	private List<PartialAlignment<CN, A>> buildPartialMappingsMap(List<GenericAlignment> alignments, Map<Long, CN> nodesMap, 
			Map<Long, A> mapIdtoAnnotations) throws AnnotationCompilationException {
		
		List<PartialAlignment<CN, A>> partialMappings = new ArrayList<PartialAlignment<CN, A>>();
		for (GenericAlignment alignment : alignments)
		{
			if (!nodesMap.containsKey(alignment.getLeftId()))
				throw new AnnotationCompilationException("there is a \""+ alignment.getType() +"\" arrow that does not start from an edge");
			if (!mapIdtoAnnotations.containsKey(alignment.getRightId()))
				throw new AnnotationCompilationException("there is a \""+ alignment.getType() +"\" arrow that does not terminate in an annotations Concept");
			CN node = nodesMap.get(alignment.getLeftId());
			A annotations = mapIdtoAnnotations.get(alignment.getRightId());
			partialMappings.add(new PartialAlignment<CN, A>(node, annotations, alignment.getType()));
		}
		return partialMappings;
	}

	/**
	 * build the map of regular alignments, from (some of the ) nodes to annotations. In the process:<br>
	 * 1. instantiate the annotation records
	 * 2. build a map of IDs to all the new annotation records
	 * 3. remove the regular alignments from the given alignments map  
	 * 
	 * @param alignments
	 * @param nodeLabelsMap 
	 * @param nodesMap
	 * @param mapIdtoAnnotations 
	 * @return
	 * @throws AnnotationCompilationException 
	 */
	private Map<CN, A> buildRegularAlignmentsMap(List<GenericAlignment> alignments, Map<Long, String> nodeLabelsMap, Map<Long, CN> nodesMap, 
			Map<Long, A> mapIdtoAnnotations)	throws AnnotationCompilationException {
		if (alignments == null) throw new AnnotationCompilationException("Got null mappings");
		if (nodeLabelsMap == null) throw new AnnotationCompilationException("Got null nodeLabelsMap");
		if (nodesMap == null) throw new AnnotationCompilationException("Got null nodesMap");
		if (mapIdtoAnnotations == null) throw new AnnotationCompilationException("Got null mapIdtoAnnotations");
		
		
		Map<CN, A> annotationsMap = new LinkedHashMap<CN, A>();
		Set<GenericAlignment> regularAlignmentsToRemove = new LinkedHashSet<GenericAlignment>();
		for (GenericAlignment alignment : alignments)
		{
			Long nodeId = alignment.getLeftId();
			if (!nodesMap.containsKey(nodeId))
				throw new AnnotationCompilationException("There is an alignment edge with \""+alignment.getType()+"\" that comes out of something that isn't an LHS node");
			
			String mappingType = alignment.getType();
			if (REGULAR_MAPPING_LABEL.equalsIgnoreCase(mappingType))
			{
				if (annotationsMap.containsKey(nodeId))
					throw new AnnotationCompilationException("There is more than one regular alignment arrow originating from: " + nodesMap.get(nodeId));
				Long annotationsId = alignment.getRightId();
				if (mapIdtoAnnotations.containsKey(annotationsId))
					throw new AnnotationCompilationException("There is more than one regular alignment arrow terminating in: " + mapIdtoAnnotations.get(annotationsId));
				// this is a regular alignment from node to its new annotations
				if (nodesMap.containsKey(annotationsId))
					throw new AnnotationCompilationException("There is a \""+REGULAR_MAPPING_LABEL+"\" mappings arrow that enters an LHS node instead of an annotations record, from "+
								nodesMap.get(nodeId) +" to "+nodesMap.get(annotationsId));
				A annotations = compilationServices.labelToAnnotations(nodeLabelsMap.get(annotationsId));
				
				annotationsMap.put(nodesMap.get(nodeId), annotations);
				mapIdtoAnnotations.put(annotationsId, annotations);
				
				regularAlignmentsToRemove.add(alignment);
			}
		}
		alignments.removeAll(regularAlignmentsToRemove);		// we're done with regular alignments
		
		return annotationsMap;
	}
	
	/**
	 * Get an {@link AbstractConstructionNode} LHS tree, and mappings from it to {@link RuleAnnotations}, and construct an {@link AbstractNode}-based 
	 * {@link AnnotationRule}
	 * @param lhsRoot
	 * @param annotationAlignments
	 * @param ruleType 
	 * @param compilationServices2
	 * @return
	 * @throws AnnotationCompilationException 
	 */
	private static	<I extends Info, N extends AbstractNode<I, N>, CN extends AbstractConstructionNode<I, CN>, A extends RuleAnnotations>
		AnnotationRule<N, A> buildAnnotationRule(CN lhsRoot, Map<CN, A> annotationAlignments, AnnotationRuleCompileServices<I, N, CN, A> compilationServices, 
				RuleType ruleType) throws AnnotationCompilationException {
	
		Map<N, A> newAnnotationAlignments = new LinkedHashMap<N, A>();
		N newLhs = convertAnnotationTreeToRegularNodes(lhsRoot, annotationAlignments, newAnnotationAlignments, compilationServices);
		
		try {
			return new AnnotationRule<N, A>(newLhs, newAnnotationAlignments, ruleType);
		} catch (AnnotatorException e) {
			throw new AnnotationCompilationException("See nested", e);
		}
	}
	
	private static	<I extends Info, N extends AbstractNode<I, N>, CN extends AbstractConstructionNode<I, CN>, A extends RuleAnnotations>
		N convertAnnotationTreeToRegularNodes(CN oldLhs, Map<CN, A> oldAnnotationAlignments, Map<N, A> newAnnotationAlignments,
				AnnotationRuleCompileServices<I, N, CN, A> compilationServices) 
	{
		N newLhs = compilationServices.newNode(oldLhs.getInfo());
		newAnnotationAlignments.put(newLhs, oldAnnotationAlignments.get(oldLhs));
		if (oldLhs.hasChildren())
			for (CN child : oldLhs.getChildren())
			{
				N newChild = convertAnnotationTreeToRegularNodes(child, oldAnnotationAlignments, newAnnotationAlignments, compilationServices);
				newLhs.addChild(newChild);
				newChild.setAntecedent(newLhs);
			}
		return newLhs; 
	}
}
