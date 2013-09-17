package eu.excitementproject.eop.biutee.rteflow.macro.gap.baseline;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapEnvironment;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.alignment.AlignmentCriteria;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

/**
 * 
 * @author Asher Stern
 * @since Sep 9, 2013
 *
 */
public class GapBaselineV2Calculator
{
	public GapBaselineV2Calculator(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			GapEnvironment<ExtendedInfo, ExtendedNode> environment,
			AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria,
			Set<String> hypothesisLemmasLowerCase)
	{
		super();
		this.textTree = textTree;
		this.hypothesisTree = hypothesisTree;
		this.environment = environment;
		this.alignmentCriteria = alignmentCriteria;
		this.hypothesisLemmasLowerCase = hypothesisLemmasLowerCase;
	}


	public void calculate()
	{
		AlignmentCalculator alignmentCalculator = new AlignmentCalculator(alignmentCriteria,textTree,hypothesisTree);
		Set<String> coveredLemmas = TreeUtilities.findCoveredLemmasLowerCase(textTree,hypothesisLemmasLowerCase);
		missingLemmas = setminus(hypothesisLemmasLowerCase,coveredLemmas);
		
		missingNodes = alignmentCalculator.getMissingSimilarNodes();
		missingRelations = alignmentCalculator.getMissingTriples();
	}
	
	
	public Set<String> getMissingLemmas()
	{
		return missingLemmas;
	}


	public Set<ExtendedNode> getMissingNodes()
	{
		return missingNodes;
	}


	public Set<ExtendedNode> getMissingRelations()
	{
		return missingRelations;
	}


	/////////////// PRIVATE ///////////////

	private <T> Set<T> setminus(Set<T> set, Set<T> minus)
	{
		Set<T> ret = new LinkedHashSet<>();
		for (T t : set)
		{
			if (!minus.contains(t))
			{
				ret.add(t);
			}
		}
		return ret;
	}

	
	
	// input
	private final TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree;
	private final TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree;
	@SuppressWarnings("unused")
	private final GapEnvironment<ExtendedInfo, ExtendedNode> environment;
	private final AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria;
	private final Set<String> hypothesisLemmasLowerCase;
	
	// output
	private Set<String> missingLemmas;
	private Set<ExtendedNode> missingNodes;
	private Set<ExtendedNode> missingRelations;
}
