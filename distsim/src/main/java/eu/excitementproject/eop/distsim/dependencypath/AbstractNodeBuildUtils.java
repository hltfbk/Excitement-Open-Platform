package eu.excitementproject.eop.distsim.dependencypath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.*;
import eu.excitementproject.eop.common.representation.parse.tree.*;

/**
 * <p>Several utils related to building general parse trees.
 * 
 * @author Erel Segal Halevi
 * @since 2012-01-03
 */
public class AbstractNodeBuildUtils {
	/**
	 * @param thePath an ordered list of node infos.
	 * @param theNodeConstructor for constructing nodes.
	 * @return a tree where the first item in the list is the only leaf, and the last item is the root.
	 * <br>null if the path is empty. 
	 */
	public static <T extends Info,S extends AbstractNode<T,S>> S buildTreeFromLeafToRoot(
			List<T> thePath,
			AbstractNodeConstructor<T, S> theNodeConstructor) {
		if (thePath.isEmpty())
			return null;
		S theTree = null;
		List<S> theChildren = Collections.emptyList();
		for (T theNextInfo: thePath) {
			theTree = theNodeConstructor.newNode(theNextInfo, theChildren);
			theChildren = new ArrayList<S>();
			theChildren.add(theTree);
		}
		return theTree;
	}
}
