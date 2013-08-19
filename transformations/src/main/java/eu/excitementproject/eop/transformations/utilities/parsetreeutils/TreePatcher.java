package eu.excitementproject.eop.transformations.utilities.parsetreeutils;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeConstructor;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier;

/**
 * Gets a tree, and another tree to "patch" into it as a direct child, and
 * makes this patch by completely copying the original tree and adding the
 * "patch".
 * 
 * 
 * @author Asher Stern
 * @since Aug 19, 2013
 *
 * @param <I>
 * @param <S>
 */
public class TreePatcher<I, S extends AbstractNode<I, S>>
{
	public TreePatcher(S originalTree, S treeToPatch,
			AbstractNodeConstructor<I, S> nodeConstructor)
	{
		super();
		this.originalTree = originalTree;
		this.treeToPatch = treeToPatch;
		this.nodeConstructor = nodeConstructor;
	}
	
	public void generate()
	{
		TreeCopier<I, S, I, S> copier = new TreeCopier<I, S, I, S>(
				originalTree,
				new TreeCopier.InfoConverter<S, I>()
				{
					@Override
					public I convert(S os)
					{
						return os.getInfo();
					}
				},
				nodeConstructor
				);
		
		copier.copy();
		generatedTree = copier.getGeneratedTree();
		generatedTree.addChild(treeToPatch);
		
		BidirectionalMap<S, S> copierMap = copier.getNodesMap();
		mapOriginalToGenerated = new SimpleValueSetMap<>();
		for (S node : copierMap.leftSet())
		{
			mapOriginalToGenerated.put(node, copierMap.leftGet(node));
		}
	}
	
	
	
	public S getGeneratedTree()
	{
		return generatedTree;
	}

	public ValueSetMap<S, S> getMapOriginalToGenerated()
	{
		return mapOriginalToGenerated;
	}



	private final S originalTree;
	private final S treeToPatch;
	private final AbstractNodeConstructor<I, S> nodeConstructor;
	
	private S generatedTree;
	private ValueSetMap<S, S> mapOriginalToGenerated;
}
