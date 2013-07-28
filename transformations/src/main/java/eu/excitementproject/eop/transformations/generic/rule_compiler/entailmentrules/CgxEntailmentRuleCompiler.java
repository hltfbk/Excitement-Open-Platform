package eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;
import eu.excitementproject.eop.transformations.generic.rule_compiler.Constants;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.CgxDomParser;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.CgxReadingUtils;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.GenericAlignment;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.RuleBuildingUtils;
import eu.excitementproject.eop.transformations.generic.rule_compiler.utils.PairSet;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.RuleType;

/**
 * This class helps {@link EntailmentRuleCompiler} in compiling rule files into {@link SyntacticRule}s, by performing some operations specific to
 * Charger(TM) CGX xml files.
 * <p>
 * 
 * a rule is composed of:<br>
 * 	Left Hand Side == a tree of AnnotatedNodes, each containing:<br>
 * 		String ID,<br>
 * 		String word,<br>
 * 		String lemma,<br>
 * 		int serial (location in the sentence)<br>
 * 		null namedEntity,<br>
 * 		String PartOfSpeech<br>
 * 		DependencyRelation - for example "subj", "subject", "s".<br>
 *  RightHandSide == like LHS<br>
 * 	BidirectionalMap == a 1to1 mapping from (part of) the left hand nodes to the right hand side nodes<br> 
 * 
 * @author Amnon Lotan
 * @since 18/06/2011
 * 
 * @param <I>	node {@link Info} type
 * @param <N>	{@link AbstractNode} type 
 * @param <CN>	{@link AbstractConstructionNode} type. Should be like the N node type, but with a {@link AbstractConstructionNode}{@code .setInfo(Info)} method
 */
public class CgxEntailmentRuleCompiler<I extends Info, N extends AbstractNode<I, N>, CN extends AbstractConstructionNode<I, CN>> 
{
	private final String REGULAR_ALIGNMENT;
	private final EntailmentRuleCompileServices<I, N, CN> compilationServices;

	/**
	 * Ctor
	 * @param predicateList an array of all listed predicates
	 * @param compilationServices utility class used for fine details of instantiating {@link NodeInfo} and {@link EdgeInfo}
	 */
	public CgxEntailmentRuleCompiler(EntailmentRuleCompileServices<I, N, CN> compilationServices) 
	{
		this.compilationServices = compilationServices;
		REGULAR_ALIGNMENT = compilationServices.getFullAlignmentTypeString();
	}
	
	/**
	 * make one rule out of one GCX XML file's text
	 * 
	 * @param ruleText
	 * @return
	 * @throws EntailmentCompilationException
	 */
	public SyntacticRule<I, N> makeRule(String ruleText) throws EntailmentCompilationException
	{
		SyntacticRule<I, N> rule = null;
		// determine the rule type
		RuleType ruleType;
		try {
			ruleType = CgxReadingUtils.readCgxRuleType(ruleText);
		} catch (CompilationException e) {
			throw new EntailmentCompilationException("see nested", e);
		}

		if (ruleType.equals(RuleType.EXTRACTION))
			rule = makeNormalRule(ruleText, true);
		else if (ruleType.equals(RuleType.SUBSTITUTION)	)
			rule = makeNormalRule(ruleText, false);
		else
			throw new EntailmentCompilationException(ruleType + " is not a valid ruleType value. The permitted values are "+RuleType.SUBSTITUTION +" and " + RuleType.EXTRACTION);
		
		EntailmentRuleBuildingUtils.sanityCheckRule(rule);
		return rule;
	}
	
	/////////////////////////////////////// PRIVATE ///////////////////////////////////////////////////////////////////////////

	/**
	 * Make one normal rule (substitution or extraction) out of one GCX XML file's text
	 * @param cgxText
	 * @param isExtraction 
	 * @return
	 * @throws EntailmentCompilationException 
	 */
	private SyntacticRule<I, N> makeNormalRule(String cgxText, boolean isExtraction) throws EntailmentCompilationException
	{
		try {
			CgxDomParser cgxDomParser = new CgxDomParser(cgxText);
  
			//	 parse nodes into a map from IDs to node content labels
			Map<Long, String> nodeLabelMap = cgxDomParser.parseNodes();
			
			// 	parse the undirected edges Info from the text
			PairSet<Long> undirectedEdges = cgxDomParser.parseEdges( );
				
			// now that we know which nodes are in the trees and which are not, return the mappings (from some LHS nodes to RHS nodes)
			List<GenericAlignment> idAlignments = cgxDomParser.parseMappings();

			// build LHS and RHS trees
			Map<Long, CN> lhsNodesMap = new LinkedHashMap<Long, CN>();
			CN lhsRoot = RuleBuildingUtils.buildTreeUnderLabel(Constants.LHS, nodeLabelMap, undirectedEdges, lhsNodesMap, compilationServices);
			Map<Long, CN> rhsNodesMap = new LinkedHashMap<Long, CN>();
			CN rhsRoot = RuleBuildingUtils.buildTreeUnderLabel(Constants.RHS, nodeLabelMap, undirectedEdges, rhsNodesMap, compilationServices);
			
			// apply all alignments
			BidirectionalMap<CN, CN> nodeAlignments = applyAlignments(idAlignments, lhsNodesMap , rhsNodesMap );

			// Build a {@link Rule} out of an LHS RHS and alignments. Convert them from {@link AbstractConstructionNode} to {@link AbstractNode}, first.
			SyntacticRule<I, N> rule = buildRule(lhsRoot, rhsRoot, nodeAlignments, isExtraction);
			
			compilationServices.doRuleLastFixes(rule);
			
			return rule;
		} catch (CompilationException e) {
			throw new EntailmentCompilationException("see nested", e);
		}
	}

	/**
	 * Build a {@link SyntacticRule} out of an LHS RHS and alignments. Convert them from {@link AbstractConstructionNode} to {@link AbstractNode}, first.
	 * @param oldLhsRoot
	 * @param oldRhsRoot
	 * @param nodeAlignments
	 * @param isExtraction 
	 * @return
	 */
	private SyntacticRule<I, N> buildRule(CN oldLhsRoot, CN oldRhsRoot, BidirectionalMap<CN, CN> nodeAlignments, boolean isExtraction) {
		
		BidirectionalMap<N, N> newAlignments = new SimpleBidirectionalMap<N, N>();
		BidirectionalMap<N, CN> helpMap = new SimpleBidirectionalMap<N, CN>();
		N newLhsRoot = convertLhsToNNodes(oldLhsRoot, nodeAlignments, helpMap );
		N newRhsRoot = convertRhsToNNodes(oldRhsRoot, helpMap, newAlignments );
			
		return new SyntacticRule<I, N>(newLhsRoot, newRhsRoot, newAlignments, isExtraction);
	}

	/**
	 * for each full/regular alignment pair, replace the null fields of the Info of rhs with the Info of the lhs
	 *  and for each <i>partial </i>mapping pair, replace only its specified attribute.<br>
	 *  Partial mappings take precedence over full mappings, so they run second!
	 *  <p>
	 *  Return the resulting full alignments map 
	 * @param idAlignments
	 * @param rhsNodesMap 
	 * @param rhsNodesMap 
	 * @return
	 * @throws EntailmentCompilationException 
	 */
	private BidirectionalMap<CN, CN> applyAlignments(List<GenericAlignment> idAlignments, Map<Long, CN> lhsNodesMap, Map<Long, CN> rhsNodesMap) 
			throws EntailmentCompilationException
	{
		// map all the full mappings
		BidirectionalMap<CN, CN> alignmentsMap = applyFullAlignments(idAlignments, lhsNodesMap, rhsNodesMap);
		
		// map all the partial alignments. Partial alignments take precedence over full alignments, so they must run second
		applyPartialAlignments(idAlignments, lhsNodesMap, rhsNodesMap);
		return alignmentsMap;
	}

	/**
	 * For each <i>partial </i>mapping pair, replace only its specified attribute in the right node with the attribute value from the left node.<br>
	 *  Partial mappings take precedence over full mappings, so they run second!
	 * @param idAlignments
	 * @param lhsNodesMap
	 * @param rhsNodesMap
	 * @throws EntailmentCompilationException 
	 */
	private void applyPartialAlignments(List<GenericAlignment> idAlignments, Map<Long, CN> lhsNodesMap, Map<Long, CN> rhsNodesMap) throws EntailmentCompilationException {
		for (GenericAlignment alignment : idAlignments)
			if (!REGULAR_ALIGNMENT.equalsIgnoreCase(alignment.getType()))
			{
				// partially reset the info on the RHS side
				Long lhsNodeId = alignment.getLeftId();
				Long rhsNodeId = alignment.getRightId();
				CN lhsNode = lhsNodesMap.get(lhsNodeId);
				CN rhsNode = rhsNodesMap.get(rhsNodeId);
				I newInfo = compilationServices.copyLeftParamToRight(lhsNode.getInfo(), rhsNode.getInfo(), alignment.getType());			
				rhsNode.setInfo(newInfo);
			}
	}

	/**
	 * Perform the compilation operations for full alignments - reset the info on the RHS side of each alignment, and return the resulting full alignments map.<br> 
	 * Also sanity check that each alignment arrow comes out of the LHS and ends in the RHS
	 * @param idAlignments
	 * @param lhsNodesMap 
	 * @param rhsNodesMap 
	 * @return 
	 * @throws EntailmentCompilationException 
	 */
	private BidirectionalMap<CN, CN> applyFullAlignments(List<GenericAlignment> idAlignments, Map<Long, CN> lhsNodesMap, Map<Long, CN> rhsNodesMap)
			throws EntailmentCompilationException 
	{
		BidirectionalMap<CN, CN> alignmentsMap = new SimpleBidirectionalMap<CN, CN>();
		for (GenericAlignment alignment : idAlignments)
		{	
			// sanity check the arrow
			Long lhsNodeId = alignment.getLeftId();
			if (!lhsNodesMap.containsKey(lhsNodeId))
				throw new EntailmentCompilationException("There is a "+ alignment.getType() +"  alingment arow, that does not come out of an LHS node" );
			Long rhsNodeId = alignment.getRightId();
			if (!rhsNodesMap.containsKey(rhsNodeId))
				throw new EntailmentCompilationException("There is a "+ alignment.getType() +"  alingment arow, that does not come in to an RHS node" );

			if (REGULAR_ALIGNMENT.equalsIgnoreCase(alignment.getType()))
			{
				// reset the info on the RHS side
				CN lhsNode = lhsNodesMap.get(lhsNodeId);
				CN rhsNode = rhsNodesMap.get(rhsNodeId);
				I newInfo = compilationServices.supplementRightInfoWithLeftInfo(lhsNode.getInfo(), rhsNode.getInfo());			
				rhsNode.setInfo(newInfo);
				
				alignmentsMap.put(lhsNode, rhsNode);
			}
		}
		return alignmentsMap;
	}
	
	/**
	 * @param rhs
	 * @param helpMap
	 * @param newMapping
	 * @return
	 */
	private  N convertRhsToNNodes(
			CN rhs,
			BidirectionalMap<N, CN> helpMap,
			BidirectionalMap<N, N> newMapping)
	{
		N newRhs =  compilationServices.newNode(rhs.getInfo());
		if (rhs.getChildren() != null)
			for (CN child : rhs.getChildren())
			{
				N newChild = convertRhsToNNodes(child, helpMap, newMapping);
				newRhs.addChild(newChild);
				newChild.setAntecedent(newRhs);
			}
		// update the new mapping if necessary
		if (helpMap.rightContains(rhs))
			newMapping.put(helpMap.rightGet(rhs), newRhs);

		return newRhs;
	}

	/**
	 * @param lhs
	 * @param mappings
	 * @param helpMap
	 * @return
	 */
	private  N convertLhsToNNodes(
			CN lhs,
			BidirectionalMap<CN, CN> mappings,
			BidirectionalMap<N, CN> helpMap)
	{
		N newLhs = compilationServices.newNode(lhs.getInfo());
		if (lhs.getChildren() != null)
			for (CN child : lhs.getChildren())
			{
				N newChild = convertLhsToNNodes(child, mappings, helpMap);
				newLhs.addChild(newChild);
				newChild.setAntecedent(newLhs);
			}
		// update the new mapping if necessary
		if (mappings.leftContains(lhs))
			helpMap.put(newLhs, mappings.leftGet(lhs));

		return newLhs;
	}
}
