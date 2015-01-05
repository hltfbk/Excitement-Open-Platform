package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.StringUtil;

/**
 * Convenient static methods for printing a tree in one line, using parentheses to determine nesting.
 * @author Ofer Bronstein
 * @since August 2014
 */
public class TreeToLineString {

	private  TreeToLineString() {}

	
	//// Specific Methods ///////////////////////////////////////////////////////
	
	/////// Single Node
	
	public static String getStringWordRel(BasicNode tree) {
		return getString(tree, new NodeShortString.WordRel());
	}
	
	public static String getStringWordRelPos(BasicNode tree) {
		return getString(tree, new NodeShortString.WordRelPos());
	}
	
	public static String getStringWordRelCanonicalPos(BasicNode tree) {
		return getString(tree, new NodeShortString.WordRelCanonicalPos());
	}
	
	
	/////// Multiple Nodes
	
	public static String getStringRel(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.Rel());
	}

	public static String getStringRelPrep(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPrep());
	}

	public static String getStringRelPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPos());
	}

	public static String getStringRelPrepPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPrepPos());
	}

	public static String getStringRelCanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelCanonicalPos());
	}

	public static String getStringRelPrepCanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.RelPrepCanonicalPos());
	}

	public static String getStringWordRel(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.WordRel());
	}

	public static String getStringWordRelPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.WordRelPos());
	}

	public static String getStringWordRelCanonicalPos(List<BasicNode> trees, boolean withContext, boolean withMagicNodes) {
		return getString(trees, withContext, withMagicNodes, new NodeShortString.WordRelCanonicalPos());
	}

	
	//// Generic Methods ///////////////////////////////////////////////////////

	public static String getString(BasicNode tree, NodeShortString nodeStr) {
		return getString(tree, "(", ")", nodeStr);
	}
	
	public static String getString(List<BasicNode> trees, boolean withContext, boolean withMagicNodes, NodeShortString nodeStr) {
		if (trees.isEmpty()) {
			return "(empty-tree)";
		}
		String subrootDep = null;
		if (!withContext) {
			subrootDep = "<SUBROOT>";
		}
		return getString(trees, "(", ")", "#", subrootDep, withMagicNodes, nodeStr);
	}
	
	public static String getString(BasicNode root, String pre, String post, String dep, boolean withMagicNodes, NodeShortString str) {
		return getStringSubtree(root, str, pre, post, dep, withMagicNodes).toString().trim();
	}
	
	public static String getString(BasicNode root, String pre, String post, NodeShortString str) {
		return getStringSubtree(root, str, pre, post, null, true).toString().trim();
	}
	
	public static String getString(BasicNode root, String pre, String post, boolean withMagicNodes, NodeShortString str) {
		return getStringSubtree(root, str, pre, post, null, withMagicNodes).toString().trim();
	}
	
	public static String getString(Collection<BasicNode> trees, String pre, String post, String treeSeparator, String dep, boolean withMagicNodes, NodeShortString str) {
		List<String> strings = new ArrayList<String>(trees.size());
		for (BasicNode root : trees) {
			strings.add(getString(root, pre, post, dep, withMagicNodes, str));
		}
		return StringUtil.join(strings, treeSeparator);
	}
	
	public static String getString(Collection<BasicNode> trees, String pre, String post, boolean withMagicNodes, NodeShortString str) {
		return getString(trees, pre, post, null, "#", withMagicNodes, str);
	}
	
	protected static <I extends Info> StringBuffer getStringSubtree(BasicNode subtree, NodeShortString str, String pre, String post, String dep, boolean withMagicNodes) {
		final String NULL_TREE_STR = "(null)";
		StringBuffer result = new StringBuffer();
		
		if (subtree == null) {
			result.append(NULL_TREE_STR);
		}
		else {
			if (subtree.getInfo().getNodeInfo().getWord() != null) {
				String nodeDep;
				if (dep != null) {
					nodeDep = dep;
				}
				else {
					nodeDep = str.toString(subtree);
				}
				
				// "Magic Node" data should just be added to nodeDep
				if (	withMagicNodes &&
						subtree.getInfo().getNodeInfo().getWordLemma()!=null &&
						MAGIC_NODES.contains(subtree.getInfo().getNodeInfo().getWordLemma())) {
					nodeDep += subtree.getInfo().getNodeInfo().getWordLemma();
				}
				
				result.append(nodeDep);
			}
			
			if (subtree.getChildren() != null) {
				for (BasicNode child : subtree.getChildren()) {
					result.append(pre);
					result.append(getStringSubtree(child, str, pre, post, null, withMagicNodes));
					result.append(post);
				}
			}
		}
		
		return result;
	}

	
	// "Magic Nodes" are one with specific importance for a tree/fragment, and should be printed accordingly
	public static final String MAGIC_NODE_PREDICATE = "[PRD]";
	public static final String MAGIC_NODE_ARGUMENT = "[ARG]";
	public static final Set<String> MAGIC_NODES = new HashSet<String>(Arrays.asList(new String[] {MAGIC_NODE_PREDICATE, MAGIC_NODE_ARGUMENT}));
	
}
