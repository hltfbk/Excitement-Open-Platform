package eu.excitementproject.eop.distsim.dependencypath;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.Set;

import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeConstructor;
import eu.excitementproject.eop.distsim.dependencypath.AbstractNodeDependencyPathsUtils.Direction;
import eu.excitementproject.eop.distsim.dependencypath.DependencyPathsFromTree.AdjectiveNounPredicate;
import eu.excitementproject.eop.distsim.dependencypath.DependencyPathsFromTree.NounPredicate;
import eu.excitementproject.eop.distsim.dependencypath.DependencyPathsFromTree.VerbAdjectiveNounPredicate;
import eu.excitementproject.eop.distsim.dependencypath.DependencyPathsFromTree.VerbRelativeClausePredicate;



/**
 * <p>Takes a parse-tree and returns all unary dependency paths from that tree, according to the UNARY_BINC definition (Szpektor et al, COLING 2008).
 * <p>They define a path from a {verb|adjective|noun) node to a noun node, OR from a noun node to an {adjective|noun} modifier, but NOT through another verb or clause boundary.
 * 
 * <p>See {@link #main} for examples.
 * @see DependencyPathsFromTree
 * 
 * @author Erel Segal
 * @since 2012-03-28
 * @param <T>
 * @param <S>
 */
public class DependencyPathsFromTreeUnaryBinc<T extends Info, S extends AbstractNode<T,S>>extends DependencyPathsFromTreeAsStrings<T,S> {
	
	/**
	 * @param theNodeConstructor constructs new tree nodes. For BasicNode, just use "new BasicNodeConstructor()".
	 * @param writeLeftLemma if true, the returned strings will include the lemmas of the left leaf (e.g. "n:light:n"). If false, they will include only the POS (e.g. "n").
	 * @param writeRightLemma if true, the returned strings will include the lemmas of the left leaf (e.g. "n:light:n"). If false, they will include only the POS (e.g. "n").
	 */
	public DependencyPathsFromTreeUnaryBinc(AbstractNodeConstructor<T, S> theNodeConstructor, boolean writeLeftLemma, boolean writeRightLemma) {
		super(theNodeConstructor);
		rightToLeftDPFT = new DependencyPathsFromTree<T,S>(theNodeConstructor, 
						/* start (root)  */ new VerbAdjectiveNounPredicate<T>(), 
						/* end (leaf)    */   new NounPredicate<T>(), 
						/* stop */  new VerbRelativeClausePredicate<T>(),
						/* createDependencyPathsWithTwoBranches */ true,
						/* theFilterPathsWithOnlyStopwords */true);
		leftToRightDPFT = new DependencyPathsFromTree<T,S>(theNodeConstructor, 
				/* start (root) */ new NounPredicate<T>(), 
				/* end (leaf)   */ new AdjectiveNounPredicate<T>(), 
				/* stop */  new VerbRelativeClausePredicate<T>(),
				/* createDependencyPathsWithTwoBranches */ false,
				/* theFilterPathsWithOnlyStopwords */true);
		this.writeLeftLemma = writeLeftLemma;
		this.writeRightLemma = writeRightLemma;
	}

	/**
	 * @param theNodeConstructor constructs new tree nodes. For BasicNode, just use "new BasicNodeConstructor()".
	 * By default, the class returns unary templates, so the left lemma is not written, and the right lemma is written.
	 */
	public DependencyPathsFromTreeUnaryBinc(AbstractNodeConstructor<T, S> theNodeConstructor) {
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
		List<S> rightToLeftPaths = rightToLeftDPFT.dependencyPaths(root);
		for (S dependencyPath: rightToLeftPaths) {  
			result.add(AbstractNodeDependencyPathsUtils.toDependencyPath(dependencyPath, 2, Direction.RIGHT_TO_LEFT, 
					/*writeRootLemma=*/true, /*writeLeftLeafLemma=*/writeLeftLemma, /*writeRightLeafLemma=*/writeRightLemma));
			if (dependencyPath.getChildren().size()==2) { // If there are two branches - add them again in reverse order:
				// create a new children list in reverse order:
				List<S> theNewChildrenList = new ArrayList<S>(2);
				theNewChildrenList.add(dependencyPath.getChildren().get(1));
				theNewChildrenList.add(dependencyPath.getChildren().get(0));
				result.add(AbstractNodeDependencyPathsUtils.toDependencyPath(nodeConstructor.newNode(dependencyPath.getInfo(), theNewChildrenList), 2, Direction.RIGHT_TO_LEFT, 
						/*writeRootLemma=*/true, /*writeLeftLeafLemma=*/writeLeftLemma, /*writeRightLeafLemma=*/writeRightLemma));
			}
		}

		// Left-to-right paths are dependency paths where the unknown is the root (usually a noun). 
		// Usually there is a modifier on the left. 
		List<S> leftToRightPaths = leftToRightDPFT.dependencyPaths(root);
		for (S dependencyPath: leftToRightPaths) { 
			result.add(AbstractNodeDependencyPathsUtils.toDependencyPath(dependencyPath, 2, Direction.LEFT_TO_RIGHT, 
					/*writeRootLemma=*/writeLeftLemma, /*writeLeftLeafLemma=*/true, /*writeRightLeafLemma=*/writeRightLemma));
		}
		return result;
	}
	
	
	/*
	 * PROTECTED ZONE
	 */
	protected DependencyPathsFromTree<T,S> rightToLeftDPFT;  // for paths such as "n<nsubj<v:condemn:v"
	protected DependencyPathsFromTree<T,S> leftToRightDPFT;  // for paths such as "n>amod>a:convicted:a"
	protected boolean writeLeftLemma;
	protected boolean writeRightLemma;
}
