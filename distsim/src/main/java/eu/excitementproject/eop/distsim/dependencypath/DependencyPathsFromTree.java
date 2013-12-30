package eu.excitementproject.eop.distsim.dependencypath;

import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections15.Predicate;

import weka.core.Stopwords;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeConstructor;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;



//AS The JavaDoc comment is very short, and the user cannot understand how to
// use this class.
// You should also write how you represent a dependency path, what is the definition
// of a dependency path. How to use the class (call the constructor,... call the methods ...,
// retrieve the results by the methods ..., use the returned value by ...).
/**
 * <p>Takes a parse-tree, and returns all dependency paths from that tree, according to the given conditions.
 * <p>A dependency path is defined by starting conditions, ending conditions, and stopping conditions - see below.
 *
 * TODO: Stop at clause boundary (not only at verbs).
 * TODO: enable templates with control variables such as "X help preventing".
 * TODO: capture noun modifiers such as "the losing X".
 * <p>See {@link #main} for examples.
 * @see DependencyPathsFromTreeUnaryBinc
 * 
 * @author Erel Segal
 * @since 2011-12-27
 * @param <T>
 * @param <S>
 */
public class DependencyPathsFromTree<T extends Info, S extends AbstractNode<T,S>> {
	
	//AS A general comment - make the public together and the private&protected together.
	
	
	protected AbstractNodeConstructor<T, S> nodeConstructor;
	protected Predicate<T> startNodePredicate, endNodePredicate, stopNodePredicate;
	protected boolean createDependencyPathsWithTwoBranches;
	protected boolean filterPathsWithOnlyStopwords;
	
	/**
	 * Initialize a DPFT calculator with generic start-node and end-node predicate.
	 * @param theNodeConstructor constructs new tree nodes. 
	 * @param startNodePredicate detects nodes that are the start of a path.
	 * @param endNodePredicate detects nodes that are the end of a path.
	 * @param stopNodePredicate detects nodes that stop the search - clause boundaries.
	 * @param createDependencyPathsWithTwoBranches If false - creates dependency paths with in a single direction (top to bottom). If true - create ALSO dependency paths with two directions (bottom to top to bottom).
	 * @param filterPathsWithOnlyStopwords if true - don't return paths that contain only stopwords.  
	 */
	public DependencyPathsFromTree(AbstractNodeConstructor<T, S> theNodeConstructor, Predicate<T> theStartNodePredicate, Predicate<T> theEndNodePredicate, Predicate<T> theStopNodePredicate,
			boolean theCreateDependencyPathsWithTwoBranches, boolean theFilterPathsWithOnlyStopwords) {
		nodeConstructor = theNodeConstructor;
		startNodePredicate = theStartNodePredicate;
		endNodePredicate = theEndNodePredicate;
		stopNodePredicate = theStopNodePredicate;
		createDependencyPathsWithTwoBranches = theCreateDependencyPathsWithTwoBranches;
		filterPathsWithOnlyStopwords = theFilterPathsWithOnlyStopwords;
	}

	public static class NounPredicate<T extends Info> implements Predicate<T> {
		public boolean evaluate(T object) {
			PartOfSpeech pos = InfoGetFields.getPartOfSpeechObject(object);
			return (
					simplerPos(pos.getCanonicalPosTag())==SimplerCanonicalPosTag.NOUN || 
					simplerPos(pos.getCanonicalPosTag())==SimplerCanonicalPosTag.PRONOUN
			);
		}
	}

	public static class VerbRelativeClausePredicate<T extends Info> implements Predicate<T> {
		public boolean evaluate(T object) {
			PartOfSpeech pos = InfoGetFields.getPartOfSpeechObject(object);
			return (
					simplerPos(pos.getCanonicalPosTag())==SimplerCanonicalPosTag.VERB  
			);
		}
	}

	public static class VerbAdjectiveNounPredicate<T extends Info> implements Predicate<T> {
		public boolean evaluate(T object) {
			PartOfSpeech pos = InfoGetFields.getPartOfSpeechObject(object);
			return (
					simplerPos(pos.getCanonicalPosTag())==SimplerCanonicalPosTag.VERB || 
					simplerPos(pos.getCanonicalPosTag())==SimplerCanonicalPosTag.ADJECTIVE || 
					simplerPos(pos.getCanonicalPosTag())==SimplerCanonicalPosTag.NOUN || 
					simplerPos(pos.getCanonicalPosTag())==SimplerCanonicalPosTag.PRONOUN
			);
		}
	}

	public static class AdjectiveNounPredicate<T extends Info> implements Predicate<T> {
		public boolean evaluate(T object) {
			PartOfSpeech pos = InfoGetFields.getPartOfSpeechObject(object);
			return (
					simplerPos(pos.getCanonicalPosTag())==SimplerCanonicalPosTag.ADJECTIVE || 
					simplerPos(pos.getCanonicalPosTag())==SimplerCanonicalPosTag.ADVERB || 
					simplerPos(pos.getCanonicalPosTag())==SimplerCanonicalPosTag.NOUN || 
					simplerPos(pos.getCanonicalPosTag())==SimplerCanonicalPosTag.PRONOUN ||
					"VBN".equals(pos.getStringRepresentation())   // a passive noun is also considered an adjective!
			);
		}
	}

	
	//AS A general comment - please avoid using comments of style
	/* */
	// This old style of comments, borrowed from C, is not recommended,
	// and in C++ the new style (of // ) was introduced, aiming at replacing the old
	// style, which is dangerous. (except JavaDoc comments, of course).
	
	/**
	 * Initialize a DPFT calculator with the default predicates:
	 * * start-node - verb / adj / noun;
	 * * end-node - noun
	 * * stop-node - verb / punctuation
	 * @param theNodeConstructor constructs new tree nodes. 
	 */
	public DependencyPathsFromTree(AbstractNodeConstructor<T, S> theNodeConstructor) {
		this(theNodeConstructor, 
				/* start */ new VerbAdjectiveNounPredicate<T>(), 
				/* end */   new NounPredicate<T>(), 
				/* stop */  new VerbRelativeClausePredicate<T>(),
				/* createDependencyPathsWithTwoBranches */true,
				/* filterPathsWithOnlyStopwords */true);
	}

	/**
	 * @param root the root of a parse-tree.
	 * @return a list of all dependency paths from that root or a descendant of it, to a descendant node. 
	 * <p>A dependency path is defined as a path between two nodes in the tree.
	 * <p>The nodes at the start and end of a path are determined by the predicates used when constructing the class.
	 * <p>By default, a path starts with a verb, adjective or noun, and ends with a noun.
	 */
	public List<S> dependencyPaths(S root) {
		List<S> result = new ArrayList<S>();
		addDependencyPaths(root, result);
		return result; 
	}




	/*
	 * PROTECTED ZONE
	 */

	/**
	 * Adds a list of all dependency paths from the given node or a descendant of it, to a descendant node.
	 * @param root [INPUT] the common ancestor of all the dependency paths that will be created.
	 * @param currentDependencyPaths [OUTPUT] the dependency paths.
	 */
	protected void addDependencyPaths(S node, List<S> currentDependencyPaths) {
		// 1. Add all dependency paths from the given root:
		if (startNodePredicate.evaluate(node.getInfo())) 
			currentDependencyPaths.addAll(dependencyPathsFromNode(node));

		// 2. Recursively add all dependency paths from each child of root:
		if (!AbstractNodeUtils.isLeaf(node)) 
			for (S child: node.getChildren())
				addDependencyPaths(child, currentDependencyPaths);
	}

	/**
	 * @param node a node in a parse tree.
	 * @return a list of all dependency paths from that node to a descendant node. 
	 */
	protected List<S> dependencyPathsFromNode(S node) {
		List<S> allChildrenDependencyPaths = new ArrayList<S>(); 
		if (!AbstractNodeUtils.isLeaf(node)) { 
			for (S child: node.getChildren()) {
				List<S> currentChildDependencyPaths = new ArrayList<S>();
				List<T> pathFromCurrentNodeToRoot = new ArrayList<T>();
				pathFromCurrentNodeToRoot.add(0, node.getInfo());
				addDependencyPathsFromNodeThroughDescendant(node, child, pathFromCurrentNodeToRoot, currentChildDependencyPaths);
				
				int previousDependencyPathsCount = allChildrenDependencyPaths.size();
				allChildrenDependencyPaths.addAll(currentChildDependencyPaths);

				// If there are other paths from the same root but different first child - add all combinations:
				if (createDependencyPathsWithTwoBranches && previousDependencyPathsCount>0 && currentChildDependencyPaths.size()>0) {
					for (int i=0; i<previousDependencyPathsCount; ++i) {
						S otherDependencyPath = allChildrenDependencyPaths.get(i);
						if (otherDependencyPath.getChildren().size()==1) {
							for (S newDependencyPath: currentChildDependencyPaths) {
								
								// Create a new tree with an additional child:
								List<S> noChildren = Collections.emptyList();
								List<S> theNewChildrenList = new ArrayList<S>(otherDependencyPath.getChildren()==null? noChildren: otherDependencyPath.getChildren());
								theNewChildrenList.add(newDependencyPath.getChildren().get(0));
								allChildrenDependencyPaths.add(
									nodeConstructor.newNode(
										otherDependencyPath.getInfo(), 
										theNewChildrenList
									)
								);
							}
						}
					}
				}

			}
		}
		return allChildrenDependencyPaths; 
	}

	/**
	 * Adds a list of all dependency paths from the given root node to a descendant node, that pass through the given current node.
	 * @param root [INPUT] the common ancestor of all the dependency paths that will be created.
	 * @param currentNode [INPUT] one of the descendants of root.
	 * @param pathFromCurrentNodeToRoot [INPUT] list of info's on the dependency path from the currentNode to the root.
	 * @param currentDependencyPaths [OUTPUT] list of node's - dependency paths from the root to a descendant node.
	 */
	protected void addDependencyPathsFromNodeThroughDescendant(S root, S currentNode, List<T> pathFromCurrentNodeToRoot, List<S> currentDependencyPaths) {
		// 1. Prepend the current node to the current path:
		pathFromCurrentNodeToRoot.add(0, currentNode.getInfo());

		// 2. If the current path is complete - add it to the list of all paths:
		if (endNodePredicate.evaluate(currentNode.getInfo()) && pathFromCurrentNodeToRoot.size()>1) {
			//System.out.println("\tfound a path - add it to the list");
			if (!filterPathsWithOnlyStopwords || !isListContainsOnlyStopwords(pathFromCurrentNodeToRoot)) {
				S newDependencyPath = AbstractNodeBuildUtils.buildTreeFromLeafToRoot(pathFromCurrentNodeToRoot, this.nodeConstructor);
				currentDependencyPaths.add(newDependencyPath);
			}
		}

		// 3. Whether the current path is complete or not - recursively add the paths through each of the children of the current node:
		if (!AbstractNodeUtils.isLeaf(currentNode) && (pathFromCurrentNodeToRoot.size()==1 || !stopNodePredicate.evaluate(currentNode.getInfo()))) { 
			for (S child: currentNode.getChildren()) {
				addDependencyPathsFromNodeThroughDescendant(root, child, pathFromCurrentNodeToRoot, currentDependencyPaths);
			}
		}

		// 4. Cleanup - after adding all possible paths through the current node, remove the current node from the current path. 
		pathFromCurrentNodeToRoot.remove(0);
	}
	
	
	
	protected boolean isStopword(T info) {
		return Stopwords.isStopword(InfoGetFields.getLemma(info));
	}
	
	protected boolean isListContainsOnlyStopwords(List<T> infos) {
		for (T info: infos)
			if (!isStopword(info))
				return false;
		return true;
	}
}
