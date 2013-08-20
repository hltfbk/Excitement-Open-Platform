package eu.excitementproject.eop.biutee.rteflow.macro.gap.pastabased;

import java.util.Set;

import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolInstances;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolsFactory;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.lap.biu.en.pasta.PredicateArgumentStructureBuilderFactory;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentStructureBuilder;

/**
 * 
 * @author Asher Stern
 * @since Aug 8, 2013
 *
 * @param <I>
 * @param <S>
 */
public class PastaBasedGapToolsFactory<I extends Info, S extends AbstractNode<I, S>> implements GapToolsFactory<I, S> 
{
	public PastaBasedGapToolsFactory(
			PredicateArgumentStructureBuilderFactory<I, S> builderFactory)
	{
		super();
		this.builderFactory = builderFactory;
	}

	@Override
	public GapToolInstances<I, S> createInstances(TreeAndParentMap<I, S> hypothesis, LinearClassifier classifierForSearch) throws GapException
	{
		try{
			PredicateArgumentStructureBuilder<I, S> builder = builderFactory.createBuilder(hypothesis);
			builder.build();
			Set<PredicateArgumentStructure<I, S>> hypothesisStructures = builder.getPredicateArgumentStructures();

			//PastaBasedV2GapTools<I,S> pastaBasedTools = new PastaBasedV2GapTools<I,S>(
			PastaBasedV3GapTools<I,S> pastaBasedTools = new PastaBasedV3GapTools<I,S>(
					builderFactory,hypothesisStructures,hypothesis,classifierForSearch);

			return new GapToolInstances<>(pastaBasedTools, pastaBasedTools, pastaBasedTools);
		}
		catch (PredicateArgumentIdentificationException e)
		{
			throw new GapException("Failed to build predicate argument structures for the hypothesis.",e);
		}

	}
	
	private final PredicateArgumentStructureBuilderFactory<I, S> builderFactory;

}
