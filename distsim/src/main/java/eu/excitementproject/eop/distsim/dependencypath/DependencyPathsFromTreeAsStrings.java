package eu.excitementproject.eop.distsim.dependencypath;

import java.util.Set;


import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeConstructor;


//AS Just a question - why every main JavaDoc comment of a class starts with <p>?
/**
 * <p>Takes a parse-tree and returns all unary dependency paths from that tree, as strings.
 * 
 * @see DependencyPathsFromTree
 * 
 * @author Erel Segal
 * @since 2012-03-28
 * @param <T>
 * @param <S>
 */
public abstract class DependencyPathsFromTreeAsStrings<T extends Info, S extends AbstractNode<T,S>> {
	protected AbstractNodeConstructor<T, S> nodeConstructor;

	/**
	 * @param theNodeConstructor constructs new tree nodes. 
	 */
	public DependencyPathsFromTreeAsStrings(AbstractNodeConstructor<T, S> theNodeConstructor) {
		nodeConstructor = theNodeConstructor;
	}


	//AS Please explain exactly the format of the string.
	// "according to UNARY_BINC definition" tells nothing to a user that is not
	// familiar with UNARY_BINC.
	/**
	 * @param root the root of a parse-tree.
	 * @return a list of all dependency paths, in string form, according to UNARY_BINC definition.
	 */
	public abstract Set<String> stringDependencyPaths(S root);
}
