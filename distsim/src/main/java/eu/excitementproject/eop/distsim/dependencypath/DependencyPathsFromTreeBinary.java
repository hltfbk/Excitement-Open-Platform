package eu.excitementproject.eop.distsim.dependencypath;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.Set;


import java.util.List;

import org.apache.commons.collections15.Predicate;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeConstructor;
import eu.excitementproject.eop.distsim.dependencypath.AbstractNodeDependencyPathsUtils.Direction;
import eu.excitementproject.eop.distsim.dependencypath.DependencyPathsFromTree.NounPredicate;
import eu.excitementproject.eop.distsim.dependencypath.DependencyPathsFromTree.VerbAdjectiveNounPredicate;
import eu.excitementproject.eop.distsim.dependencypath.DependencyPathsFromTree.VerbRelativeClausePredicate;



/**
 * <p>Takes a parse-tree and returns all binary dependency paths from that tree.
 * <p>They define a path from a {verb|adjective|noun) node to two argument noun nodes.
 * 
 * <p>See {@link #main} for examples.
 * @see DependencyPathsFromTree
 * 
 * @author Erel Segal
 * @since 2012-09-24
 * @param <T>
 * @param <S>
 */
public class DependencyPathsFromTreeBinary<T extends Info, S extends AbstractNode<T,S>>extends DependencyPathsFromTreeAsStrings<T,S> {
	
	/**
	 * @param theNodeConstructor constructs new tree nodes. For BasicNode, just use "new BasicNodeConstructor()".
	 * @param writeLeftLemma if true, the returned strings will include the lemmas of the left leaf (e.g. "n:light:n"). If false, they will include only the POS (e.g. "n").
	 * @param writeRightLemma if true, the returned strings will include the lemmas of the left leaf (e.g. "n:light:n"). If false, they will include only the POS (e.g. "n").
	 */
	public DependencyPathsFromTreeBinary(AbstractNodeConstructor<T, S> theNodeConstructor, boolean writeLeftLemma, boolean writeRightLemma) {
		super(theNodeConstructor);
		DPFT = new DependencyPathsFromTree<T,S>(theNodeConstructor, 
						/* start (root)  */ new VerbAdjectiveNounPredicate<T>(), 
						/* end (leaf)    */   new NounPredicate<T>(), 
						/* stop */  new VerbRelativeClausePredicate<T>(),
						/* createDependencyPathsWithTwoBranches */ true,
						/* theFilterPathsWithOnlyStopwords */true);
		this.writeLeftLemma = writeLeftLemma;
		this.writeRightLemma = writeRightLemma;
	}

	public DependencyPathsFromTreeBinary(AbstractNodeConstructor<T, S> theNodeConstructor, Predicate<T> theStartNodePredicate, boolean writeLeftLemma, boolean writeRightLemma) {
		super(theNodeConstructor);
		DPFT = new DependencyPathsFromTree<T,S>(theNodeConstructor, 
						/* start (root)  */ theStartNodePredicate, 
						/* end (leaf)    */   new NounPredicate<T>(), 
						/* stop */  new VerbRelativeClausePredicate<T>(),
						/* createDependencyPathsWithTwoBranches */ true,
						/* theFilterPathsWithOnlyStopwords */true);
		this.writeLeftLemma = writeLeftLemma;
		this.writeRightLemma = writeRightLemma;
	}
	
	/**
	 * @param theNodeConstructor constructs new tree nodes. For BasicNode, just use "new BasicNodeConstructor()".
	 * By default, the class returns unary templates, so the left lemma is not written, and the right lemma is written.
	 */
	public DependencyPathsFromTreeBinary(AbstractNodeConstructor<T, S> theNodeConstructor) {
		this(theNodeConstructor, /*writeLeftLemma*/false, /*writeRightLemma*/true);
	}

	/**
	 * @param root the root of a parse-tree.
	 * @return a list of all dependency paths, in string form, according to UNARY_BINC definition.
	 */
	@Override public Set<String> stringDependencyPaths(S root) {
		Set<String> result = new HashSet<String>();
		
		// Right-to-left paths are dependency paths where the unknown is the left leaf (usually a noun). 
		// The root of the tree (usually a verb) is at the right. 
		// Optionally, to the right of the root, there is another leaf, with full lemma.
		List<S> paths = DPFT.dependencyPaths(root);
		for (S dependencyPath: paths) {  
			if (dependencyPath.getChildren().size()==2) { // If there are two branches - add them twice in both directions:
				result.add(AbstractNodeDependencyPathsUtils.toDependencyPath(dependencyPath, 2, Direction.RIGHT_TO_LEFT, 
						/*writeRootLemma=*/true, /*writeLeftLeafLemma=*/writeLeftLemma, /*writeRightLeafLemma=*/writeRightLemma));
				
				// create a new children list in reverse order:
				List<S> theNewChildrenList = new ArrayList<S>(2);
				theNewChildrenList.add(dependencyPath.getChildren().get(1));
				theNewChildrenList.add(dependencyPath.getChildren().get(0));
				result.add(AbstractNodeDependencyPathsUtils.toDependencyPath(nodeConstructor.newNode(dependencyPath.getInfo(), theNewChildrenList), 2, Direction.RIGHT_TO_LEFT, 
						/*writeRootLemma=*/true, /*writeLeftLeafLemma=*/writeLeftLemma, /*writeRightLeafLemma=*/writeRightLemma));
			}
		}
		return result;
	}
	
	
	/*
	 * PROTECTED ZONE
	 */
	protected DependencyPathsFromTree<T,S> DPFT;  // for paths such as "n<nsubj<v:condemn:v"
	protected boolean writeLeftLemma;
	protected boolean writeRightLemma;
}
