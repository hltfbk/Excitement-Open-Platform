package eu.excitementproject.eop.distsim.dependencypath;

import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;



import java.util.ArrayList;
import java.util.List;

import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;


//AS In general - I will need some explanations about the methods below.
// Isn't DIRT like paths extracted by other classes of "DependencyPathsFromTree"?

//ES 
//   The "DependencyPathsFromTree" classes are responsible for extracting specific paths from trees. But the paths are still made of AbstractNode's (a path is a chain of nodes).
//   This class is responsible for converting the paths (=chains of nodes) to strings.

//AS a general comment to all JavaDoc - a main comment should exist, even if
// its information is duplicated later in the @return and @param tags.
//ES OK.
//
//AS What is "roughly equivalent"? Need exact description. (the comment is relevant
// also to the method below, which is not commented out).
//ES OK, changed the wording.

/**
 * <p>Utility functions for converting parse trees that are chains of nodes, to string-representations of dependency paths.
 *
 * @author Erel Segal Halevi
 * @since 2012-12-19
 */
public class AbstractNodeDependencyPathsUtils {

	/**
	 * Used for selecting the direction of parent-to-child arrows in a dependency path:  
	 */
	public enum Direction {
		RIGHT_TO_LEFT,  // for example:   n:he:n<nsubj<v:say:v
		LEFT_TO_RIGHT   // for example:   v:say:v>nsubj>n:he:n 
	}
	
	
	/**
	 * Convert a dependency parse tree with a single chain of nodes, to a string in DIRT-like format.
	 * <p>Examples of unary paths:  n&lt;nsubj&lt;v:convict:v , n&lt;nsubj&lt;v:use:v&gt;dobj&gt;n:keyboard:n
	 * <p>Examples of binary paths: n&lt;dobj&lt;v:convict:v&gt;nsubj&gt;n , n&lt;pobj&lt;p:against:p&lt;prep&lt;n:charge:n&lt;dobj&lt;v:drop:v&gt;nsubj&gt;n
	 * 
	 * @param parseTree the root of the parse tree.
	 * @param maxChildCount the maximum number of children that this tree can have (1 for unary, 2 for binary).
	 * @param parentToChildDirection [INPUT] if RIGHT_TO_LEFT, the first child (child 0) will be inserted to the left of the parent. Otherwise, it will be inserted to the right of the parent. 
	 * @param writeRootLemma [INPUT] if true, the root node will be written with both POS and lemma (e.g. v:convict:v). Otherwise, only POS will be written (v). 
	 * @param writeLeftLeafLemma [INPUT] if true, the left leaf node will be written with both POS and lemma (e.g. a:wise:a). Otherwise, only POS will be written (a). 
	 * @param writeRightLeafLemma [INPUT] if true, the right leaf node will be written with both POS and lemma (e.g. a:wise:a). Otherwise, only POS will be written (a). 
	 * @return a DIRT-like dependency path that represents the given parse tree.
	 */
	public static <T extends Info,S extends AbstractNode<T,S>> String toDependencyPath(S parseTree, int maxChildCount, Direction parentToChildDirection, 
			boolean writeRootLemma, boolean writeLeftLeafLemma, boolean writeRightLeafLemma) {
		if (maxChildCount<1 || maxChildCount>2) 
			//AS Why do you throw a runtime exception?
			throw new IllegalArgumentException("maxChildCount can only be 1 (unary) or 2 (binary)");
		List<String> words = new ArrayList<String> ();
		AbstractNodeDependencyPathsUtils.addComponentsOfDependencyPath(parseTree, maxChildCount, parentToChildDirection, writeRootLemma, writeLeftLeafLemma, writeRightLeafLemma, words);
		return StringUtil.join(words, "");
	}

	/**
	 * Recursively add the dependency-path components of the given parseTree to the given words list, in the correct grammatical order. 
	 * Subroutine of {@link toDependencyPath}.
	 * 
	 * <p>Examples of unary paths:  n&lt;nsubj&lt;v:convict:v , n&lt;nsubj&lt;v:use:v&gt;dobj&gt;n:keyboard:n
	 * <p>Examples of binary paths: n&lt;dobj&lt;v:convict:v&gt;nsubj&gt;n , n&lt;pobj&lt;p:against:p&lt;prep&lt;n:charge:n&lt;dobj&lt;v:drop:v&gt;nsubj&gt;n
	 *  
	 * @param parseTree [INPUT] the root of the parse tree.
	 * @param maxChildCount [INPUT] the maximum number of children that this tree can have (1 for unary, 2 for binary).
	 * @param parentToChildDirection [INPUT] if RIGHT_TO_LEFT, the first child (child 0) will be inserted to the left of the parent. Otherwise, it will be inserted to the right of the parent.
	 * @param writeRootLemma [INPUT] if true, the root node will be written with both POS and lemma (e.g. v:convict:v). Otherwise, only POS will be written (v). 
	 * @param writeLeftLeafLemma [INPUT] if true, the left leaf node will be written with both POS and lemma (e.g. a:wise:a). Otherwise, only POS will be written (a). 
	 * @param writeRightLeafLemma [INPUT] if true, the right leaf node will be written with both POS and lemma (e.g. a:wise:a). Otherwise, only POS will be written (a). 
	 * @param components [OUTPUT]
	 */
	protected static <T extends Info,S extends AbstractNode<T,S>> void addComponentsOfDependencyPath(S parseTree, int maxChildCount, Direction parentToChildDirection, 
			boolean writeRootLemma, boolean writeLeftLeafLemma, boolean writeRightLeafLemma, List<String> components) {
		if (parseTree.getChildren()==null) { // leaf node - the component is its POS only (e.g. "n", "v"):
			boolean writeLeafLemma = (parentToChildDirection==Direction.LEFT_TO_RIGHT? writeRightLeafLemma: writeLeftLeafLemma);
			components.add(AbstractNodeDependencyPathsUtils.toDependencyPathComponent(parseTree.getInfo().getNodeInfo(), writeLeafLemma));
		} else {  // non-leaf node - add his first (and possible second) child:
			int childCount = parseTree.getChildren().size();
			S child0 = parseTree.getChildren().get(0);
			S child1 = maxChildCount>1 && childCount>1? parseTree.getChildren().get(1): null;
	
			if (parentToChildDirection==Direction.RIGHT_TO_LEFT) {
				// add the first child at the left, with right-to-left arrows:
				addComponentsOfDependencyPath (child0, 1, Direction.RIGHT_TO_LEFT, /*writeRootLemma=*/true, writeLeftLeafLemma, writeRightLeafLemma, components);  
				// add the dependency from the first child to the parent, with a right-to-left arrow:
				components.add(AbstractNodeDependencyPathsUtils.toDependencyPathComponent(child0.getInfo().getEdgeInfo(), Direction.RIGHT_TO_LEFT));
	
				// add the parent node:
				components.add(AbstractNodeDependencyPathsUtils.toDependencyPathComponent(parseTree.getInfo().getNodeInfo(), writeRootLemma));
	
				if (child1!=null) {
					// add the dependency from the parent to the second child, with a left-to-right arrow:
					components.add(AbstractNodeDependencyPathsUtils.toDependencyPathComponent(child1.getInfo().getEdgeInfo(), Direction.LEFT_TO_RIGHT));
					// add the second child at the right, with left-to-right arrows:
					addComponentsOfDependencyPath (child1, 1, Direction.LEFT_TO_RIGHT, /*writeRootLemma=*/true, writeLeftLeafLemma, writeRightLeafLemma, components);
				}
			} else {  // Direction.LEFT_TO_RIGHT
	
				if (child1!=null) {
					// add the second child at the right, with right-to-left arrows:
					addComponentsOfDependencyPath (child1, 1, Direction.RIGHT_TO_LEFT, /*writeRootLemma=*/true, writeLeftLeafLemma, writeRightLeafLemma, components);
					// add the dependency from the parent to the second child, with a right-to-left arrow:
					components.add(AbstractNodeDependencyPathsUtils.toDependencyPathComponent(child1.getInfo().getEdgeInfo(), Direction.RIGHT_TO_LEFT));
				}
	
				// add the parent node:
				components.add(AbstractNodeDependencyPathsUtils.toDependencyPathComponent(parseTree.getInfo().getNodeInfo(), writeRootLemma));
	
				// add the dependency from the first child to the parent, with a left-to-right arrow:
				components.add(AbstractNodeDependencyPathsUtils.toDependencyPathComponent(child0.getInfo().getEdgeInfo(), Direction.LEFT_TO_RIGHT));
				// add the first child at the right, with left-to-right arrow:
				addComponentsOfDependencyPath (child0, 1, Direction.LEFT_TO_RIGHT, /*writeRootLemma=*/true, writeLeftLeafLemma, writeRightLeafLemma, components);
	
			}
	
		}
	}
	
	
	protected static char toChar(PartOfSpeech pos) {
		switch (simplerPos(pos.getCanonicalPosTag())) {
		case NOUN: return 'n';
		case VERB: return 'v';
		case ADJECTIVE: return 'a';
		//case ADVERB: return 'r';
		case PREPOSITION: return 'p';
		//case DETERMINER: return 'd';
		case PRONOUN: return 'n';       // NOTE: in UNARY_BINC database, a pronoun is considered a 'n' - not 'p'!
		//case PUNCTUATION: return ',';
		default: return '0'; // no pos tag
		}
	}

	/**
	 * @param info a node information (including lemma and pos) 
	 * @param withLemma If true, the component is made of the lemma and the POS, for example: v:use:v, n:keyboard:n, p:against:p
	 * If false, the component is made of POS only, for example: v, n, a
	 * @return a DIRT-like dependency path component for the given parse tree node.
	 */
	//AS I don't understand your explanations in the JavaDoc.
	protected static String toDependencyPathComponent(NodeInfo info, boolean withLemma) {
		PartOfSpeech pos = InfoGetFields.getPartOfSpeechObject(info);
		char posChar = toChar(pos);
		if (withLemma) {
			String lemma = InfoGetFields.getLemma(info);
			return posChar+":"+lemma+":"+posChar;
		} else {
			return String.valueOf(posChar);
		}
	}

	/**
	 * @param info an edge information (including dependency type)
	 * @return a DIRT-like dependency path component for the given parse tree edge.
	 * The component is made of the relation, for example: "&gt;pobj&gt;" or "&lt;prep&lt;".
	 */
	protected static String toDependencyPathComponent(EdgeInfo info, Direction arrowDirection) {
		String relation = InfoGetFields.getRelation(info);
		if (arrowDirection==Direction.RIGHT_TO_LEFT)
			return "<"+relation+"<";
		else
			return ">"+relation+">";
	}
}
