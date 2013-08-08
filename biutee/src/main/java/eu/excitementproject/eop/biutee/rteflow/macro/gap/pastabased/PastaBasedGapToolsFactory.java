package eu.excitementproject.eop.biutee.rteflow.macro.gap.pastabased;

import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolInstances;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolsFactory;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

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
	@Override
	public GapToolInstances<I, S> createInstances(TreeAndParentMap<I, S> hypothesis, LinearClassifier classifierForSearch) throws GapException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
