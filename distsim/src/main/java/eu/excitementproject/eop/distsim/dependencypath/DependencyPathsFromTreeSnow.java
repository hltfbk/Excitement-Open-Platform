package eu.excitementproject.eop.distsim.dependencypath;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections15.functors.TruePredicate;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeConstructor;
import eu.excitementproject.eop.distsim.dependencypath.AbstractNodeDependencyPathsUtils.Direction;
import eu.excitementproject.eop.distsim.dependencypath.DependencyPathsFromTree.NounPredicate;
import eu.excitementproject.eop.distsim.dependencypath.DependencyPathsFromTree.VerbRelativeClausePredicate;



/**
 * <p>Takes a parse-tree and returns all unary dependency paths from that tree, according to the [Snow 2005] experiment definition.
 * <p>Snow used paths from a noun node to a descendant noun node. These paths were then used as features for machine learning.
 * <p>NOTE: before using this class, make sure the tree went through the two extensions in [Snow 2005] (satellite links, conjunction links). 
 * 
 * <p>See {@link #main} for examples.
 * @see DependencyPathsFromTree
 * 
 * @author Eden Erez, Erel Segal
 * @since 2012-05-21
 * @param <T>
 * @param <S>
 */
public class DependencyPathsFromTreeSnow<T extends Info, S extends AbstractNode<T,S>> extends DependencyPathsFromTreeAsStrings<T,S> {
	protected DependencyPathsFromTree<T,S> oneBranchDPFT; // for paths where the parent is a noun and the leaf is a noun.
	protected DependencyPathsFromTree<T,S> twoBranchDPFT; // for paths where the parent is anything and there are two noun leafs.
	protected boolean USE_TWO_BRANCH_DPFT = false; 
	
	/**
	 * @param theNodeConstructor constructs new tree nodes.
	 */ 
	@SuppressWarnings("unchecked")
	public DependencyPathsFromTreeSnow(AbstractNodeConstructor<T, S> theNodeConstructor) {
		super(theNodeConstructor);
		oneBranchDPFT = new DependencyPathsFromTree<T,S>(theNodeConstructor, 
						/* start (root)  */   new NounPredicate<T>(), 
						/* end (leaf)    */   new NounPredicate<T>(), 
						/* stop */  new VerbRelativeClausePredicate<T>(), // TODO: limit by length (4)
						/* createDependencyPathsWithTwoBranches */ false,
						/* theFilterPathsWithOnlyStopwords */true);
		twoBranchDPFT = new DependencyPathsFromTree<T,S>(theNodeConstructor, 
				/* start (root)  */   TruePredicate.INSTANCE, 
				/* end (leaf)    */   new NounPredicate<T>(), 
				/* stop */  new VerbRelativeClausePredicate<T>(),  // TODO: limit by length (4)
				/* createDependencyPathsWithTwoBranches */ true,
				/* theFilterPathsWithOnlyStopwords */true);
	}


	/**
	 * @param root the root of a parse-tree.
	 * @return a list of all dependency paths, in string form, according to [Snow 2005] definition.
	 */
	@Override public Set<String> stringDependencyPaths(S root) {
		Set<String> result = new HashSet<String>();

		for (S dependencyPath: oneBranchDPFT.dependencyPaths(root))  
			result.add(AbstractNodeDependencyPathsUtils.toDependencyPath(dependencyPath, 1, Direction.RIGHT_TO_LEFT, 
					/*writeRootLemma=*/true, /*writeLeftLeafLemma=*/true, /*writeRightLeafLemma=*/true));
		
		if (USE_TWO_BRANCH_DPFT)
			for (S dependencyPath: twoBranchDPFT.dependencyPaths(root))
				if (dependencyPath.getChildren().size()==2) // limit to exactly 2 noun-leafs 
					result.add(AbstractNodeDependencyPathsUtils.toDependencyPath(dependencyPath, 2, Direction.RIGHT_TO_LEFT, 
						/*writeRootLemma=*/true, /*writeLeftLeafLemma=*/true, /*writeRightLeafLemma=*/true));
		return result;
	}
}
