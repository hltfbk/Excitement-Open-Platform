package eu.excitementproject.eop.transformations.utilities;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
//import eu.excitementproject.eop.transformations.rteflow.macro.search.astar.GeneratedTreeStateCalculations;
//import eu.excitementproject.eop.transformations.rteflow.macro.search.local_creative.LocalCreativeTextTreesProcessor;
//import eu.excitementproject.eop.transformations.rteflow.macro.search.old_beam_search.BeamSearchTextTreesProcessor;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.AdvancedEqualities;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;


/**
 * Information about the "gap" between the given text-tree
 * to the hypothesis tree.
 * That gap is estimated as missing nodes, missing relations, etc.
 * 
 * (Note that more sophisticated methods to estimate that gap
 * could be used. For example, Heilman & Smith used a kernel
 * function for exactly that purpose).
 * 
 * Used by {@link BeamSearchTextTreesProcessor}, by {@link GeneratedTreeStateCalculations},
 * by {@link LocalCreativeTextTreesProcessor},  and by other classes that are used in
 * various search algorithms.
 * 
 * <P>
 * The following terminology is used:
 * <UL>
 * <LI>"Missing lemma" = a lemma that exist in the hypothesis-parse-tree by does not
 * exist in the given tree.</LI>
 * <LI>"Missing node" = a node in the hypothesis-parse-tree that has no
 * corresponding node in the given parse-tree.</LI>
 * <LI>"Missing relation" = a triple of parent-child-label (label is the
 * edge label of the edge connecting the parent and the child) that does
 * not exist in the given tree.</LI>
 * <LI></LI>
 * </UL>
 * 
 * @author Asher Stern
 * @since Jun 10, 2011
 *
 */
public class SingleTreeEvaluations
{
	@Deprecated
	public static SingleTreeEvaluations create(TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis,
			Set<String> hypothesisLemmasLowerCase,
			int numberOfHypothesisNodes)
	{
		int missingNodes;
		int missingRelations;
		int missingLemmas;
		if (AdvancedEqualities.USE_ADVANCED_EQUALITIES)
		{
			missingNodes = AdvancedEqualities.findMissingNodes(textTree, hypothesis).size();
			missingRelations = AdvancedEqualities.findMissingRelations(textTree, hypothesis).size();			
		}
		else
		{
			missingNodes = TreeUtilities.findNodesNoMatch(textTree, hypothesis).size();
			missingRelations = TreeUtilities.findRelationsNoMatch(textTree, hypothesis).size();
		}
		int coveredLemmas = TreeUtilities.findCoveredLemmasLowerCase(textTree,hypothesisLemmasLowerCase).size();
		missingLemmas = hypothesisLemmasLowerCase.size()-coveredLemmas;
		
		
		
		double missingNodesPortion = ((double)missingNodes) / ((double)numberOfHypothesisNodes);
		double missingRelationsPortion = ((double)missingRelations) / ((double)(numberOfHypothesisNodes-1));
		double missingLemmasPortion = ((double)missingLemmas) / ((double)(hypothesisLemmasLowerCase.size()));
		
		return new SingleTreeEvaluations(missingNodes, missingRelations, missingLemmas, missingNodesPortion, missingRelationsPortion, missingLemmasPortion);
	}
	
	public SingleTreeEvaluations(int missingNodes, int missingRelations,
			int missingLemmas, double missingNodesPortion,
			double missingRelationsPortion, double missingLemmasPortion)
	{
		super();
		this.missingNodes = missingNodes;
		this.missingRelations = missingRelations;
		this.missingLemmas = missingLemmas;
		this.missingNodesPortion = missingNodesPortion;
		this.missingRelationsPortion = missingRelationsPortion;
		this.missingLemmasPortion = missingLemmasPortion;
	}
	
	
	
	public int getMissingNodes()
	{
		return missingNodes;
	}
	public int getMissingRelations()
	{
		return missingRelations;
	}
	public int getMissingLemmas()
	{
		return missingLemmas;
	}
	public double getMissingNodesPortion()
	{
		return missingNodesPortion;
	}
	public double getMissingRelationsPortion()
	{
		return missingRelationsPortion;
	}
	public double getMissingLemmasPortion()
	{
		return missingLemmasPortion;
	}



	private final int missingNodes;
	private final int missingRelations;
	private final int missingLemmas;

	private final double missingNodesPortion;
	private final double missingRelationsPortion;
	private final double missingLemmasPortion;
}
