package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
	
	public static String getStringRel(List<BasicNode> trees, boolean withContext) {
		return getString(trees, withContext, new NodeShortString.Rel());
	}

	public static String getStringRelPos(List<BasicNode> trees, boolean withContext) {
		return getString(trees, withContext, new NodeShortString.RelPos());
	}

	public static String getStringRelCanonicalPos(List<BasicNode> trees, boolean withContext) {
		return getString(trees, withContext, new NodeShortString.RelCanonicalPos());
	}

	public static String getStringWordRel(List<BasicNode> trees, boolean withContext) {
		return getString(trees, withContext, new NodeShortString.WordRel());
	}

	public static String getStringWordRelPos(List<BasicNode> trees, boolean withContext) {
		return getString(trees, withContext, new NodeShortString.WordRelPos());
	}

	public static String getStringWordRelCanonicalPos(List<BasicNode> trees, boolean withContext) {
		return getString(trees, withContext, new NodeShortString.WordRelCanonicalPos());
	}

	
	//// Generic Methods ///////////////////////////////////////////////////////

	public static String getString(BasicNode tree, NodeShortString nodeStr) {
		return getString(tree, "(", ")", nodeStr);
	}
	
	public static String getString(List<BasicNode> trees, boolean withContext, NodeShortString nodeStr) {
		if (trees.isEmpty()) {
			return "(empty-tree)";
		}
		String subrootDep = null;
		if (!withContext) {
			subrootDep = "<SUBROOT>";
		}
		return getString(trees, "(", ")", "#", subrootDep, nodeStr);
	}
	
	public static String getString(BasicNode root, String pre, String post, String dep, NodeShortString str) {
		return getStringSubtree(root, str, pre, post, dep).toString().trim();
	}
	
	public static String getString(BasicNode root, String pre, String post, NodeShortString str) {
		return getStringSubtree(root, str, pre, post, null).toString().trim();
	}
	
	public static String getString(Collection<BasicNode> trees, String pre, String post, String treeSeparator, String dep, NodeShortString str) {
		List<String> strings = new ArrayList<String>(trees.size());
		for (BasicNode root : trees) {
			strings.add(getString(root, pre, post, dep, str));
		}
		return StringUtil.join(strings, treeSeparator);
	}
	
	public static String getString(Collection<BasicNode> trees, String pre, String post, NodeShortString str) {
		return getString(trees, pre, post, null, "#", str);
	}
	
	protected static <I extends Info> StringBuffer getStringSubtree(BasicNode subtree, NodeShortString str, String pre, String post, String dep) {
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
				result.append(nodeDep);
			}
			
			if (subtree.getChildren() != null) {
				for (BasicNode child : subtree.getChildren()) {
					result.append(pre);
					result.append(getStringSubtree(child, str, pre, post, null));
					result.append(post);
				}
			}
		}
		
		return result;
	}

}
