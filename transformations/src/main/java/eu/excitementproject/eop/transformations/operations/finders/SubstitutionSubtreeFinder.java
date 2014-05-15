package eu.excitementproject.eop.transformations.operations.finders;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.specifications.SubstitutionSubtreeSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;



/**
 * Finds a set of {@link SubstitutionSubtreeSpecification}s based on a given predicate.
 * 
 * @author Erel Segal
 * @since 2011-10-27
 * @see ac.biu.nlp.nlp.engineml.tcpip.demos.FinderDemo
 */
public class SubstitutionSubtreeFinder implements Finder<SubstitutionSubtreeSpecification> {

	/**
	 * Most general initialization - use any predicate for the text nodes and any predicate for the hypothesis node. 
	 * @param textTree
	 * @param hypothesisTree
	 * @param textNodePredicate
	 * @param hypothesisNodePredicate
	 * @param description a string put in the specification for tracing purposes.
	 */
	public SubstitutionSubtreeFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			Predicate<ExtendedNode> textNodePredicate,
			Predicate<ExtendedNode> hypothesisNodePredicate,
			String description 
			) {
		super();
		this.textTree = textTree;
		this.hypothesisTree = hypothesisTree;
		this.textNodePredicate = textNodePredicate;
		this.hypothesisNodePredicate = hypothesisNodePredicate;
		this.description = description;
	}
	

	/**
	 * <p>More specific initialization - use any predicate for the text nodes, but search for hypothesis nodes using the "word" field only. 
	 * <p>Useful for variable substitution.
	 * <p>Also serves as a demo for using predicates as arguments. 
	 * @param textTree
	 * @param hypothesisTree
	 * @param textNodePredicate
	 * @param hypothesisWord look for nodes with this word only.
	 */
	public SubstitutionSubtreeFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			Predicate<ExtendedNode> textNodePredicate,
			final String hypothesisWord 
			) {
		this(textTree, hypothesisTree, 
			textNodePredicate, 
			new Predicate<ExtendedNode>() {
				@Override public boolean evaluate(ExtendedNode node) {
						return InfoGetFields.getWord(node.getInfo()).equals(hypothesisWord);
				}
			}, "Substitution to '"+hypothesisWord+"':");
	}

	@Override public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException
	{}

	public void find() {
			specs = new LinkedHashSet<SubstitutionSubtreeSpecification>();

			Set<ExtendedNode> textTreeNodes = AbstractNodeUtils.treeToLinkedHashSet(textTree.getTree());
			CollectionUtils.filter(textTreeNodes, textNodePredicate);
			if (textTreeNodes.isEmpty()) return;

			Set<ExtendedNode> hypothesisTreeNodes = AbstractNodeUtils.treeToLinkedHashSet(hypothesisTree.getTree());
			CollectionUtils.filter(hypothesisTreeNodes, hypothesisNodePredicate);
			if (hypothesisTreeNodes.isEmpty()) return;

			for (ExtendedNode textNode: textTreeNodes)	{
				for (ExtendedNode hypothesisNode: hypothesisTreeNodes)	{
					specs.add(new SubstitutionSubtreeSpecification(
						description, textNode, hypothesisNode));
				}
			}
	}

	public Set<SubstitutionSubtreeSpecification> getSpecs() throws OperationException {
		if (null==specs) throw new OperationException("find() was not called.");
		return specs;
	}

	private String description;
	private TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree, hypothesisTree;
	private Predicate<ExtendedNode> textNodePredicate, hypothesisNodePredicate;
	private Set<SubstitutionSubtreeSpecification> specs = null;
}
