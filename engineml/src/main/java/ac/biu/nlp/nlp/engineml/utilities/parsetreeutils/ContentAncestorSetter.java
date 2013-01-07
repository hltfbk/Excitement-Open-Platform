package ac.biu.nlp.nlp.engineml.utilities.parsetreeutils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.representation.AdditionalInformationServices;
import ac.biu.nlp.nlp.engineml.representation.AdditionalNodeInformation;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNodeConstructor;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.general.BidirectionalMap;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeCopier;
import ac.biu.nlp.nlp.representation.CanonicalPosTag;


/**
 * 
 * Sets the content ancestor of the nodes of a given tree.
 * For example: Let's say we have the tree:
 * <pre>
 *      go
 *      |
 *   /-----\
 *   |     to
 *   I     |
 *        school
 *        
 * </pre>
 * Then "go" is the content ancestor of the node "to".
 * 
 * <P>
 * "content ancestor" of a node <tt>n</tt> is a node <tt>m</tt> that:
 * <OL>
 * <LI>is an ancestor of <tt>n</tt></LI>
 * <LI>and is a content-word</LI>
 * <LI>and there is no other node <tt>m'</tt> that
 * <OL>
 * <LI>is ancestor of <tt>n</tt></LI>
 * <LI>and descendant of <tt>m</tt></LI>
 * <LI>and is a content word</LI>
 * </OL>
 * </LI>
 * </OL>
 * 
 * 
 * @author Asher Stern
 * @since May 23, 2011
 *
 */
public class ContentAncestorSetter
{
	//////////////////////// PUBLIC ////////////////////////////
	
	public static ExtendedNode generateWithAncestorInformation(ExtendedNode originalTree) throws TeEngineMlException
	{
		ContentAncestorSetter setter = new ContentAncestorSetter(originalTree);
		setter.generate();
		return setter.getGeneratedTree();
	}
	
	public static final Set<CanonicalPosTag> contentPoses;
	static
	{
		contentPoses=new HashSet<CanonicalPosTag>();
		contentPoses.add(CanonicalPosTag.VERB);
		contentPoses.add(CanonicalPosTag.NOUN);
		contentPoses.add(CanonicalPosTag.ADJECTIVE);
		contentPoses.add(CanonicalPosTag.ADVERB);
	}

	// Constructor
	public ContentAncestorSetter(ExtendedNode tree)
	{
		super();
		this.tree = tree;
	}

	public void generate()
	{
		mapNodeToItsContentAncestor = new LinkedHashMap<ExtendedNode, ExtendedNode>();
		fillMapContentAncestor(this.tree,null);
		ContentAncestorSetterInfoConverter infoConverter = new ContentAncestorSetterInfoConverter();
		
		treeCopier =
			new TreeCopier<ExtendedInfo, ExtendedNode, ExtendedInfo, ExtendedNode>(tree, infoConverter, new ExtendedNodeConstructor());
		
		treeCopier.copy();
		generatedTree = treeCopier.getGeneratedTree();
	}
	
	public ExtendedNode getGeneratedTree() throws TeEngineMlException
	{
		if (null==this.generatedTree) throw new TeEngineMlException("generatedTree is null");
		return generatedTree;
	}
	
	public BidirectionalMap<ExtendedNode, ExtendedNode> getNodesMap() throws TeEngineMlException
	{
		if (null==treeCopier) throw new TeEngineMlException("Not generated");
		return treeCopier.getNodesMap();
	}


	/////////////////////////// PRIVATE /////////////////////////////////////

	private class ContentAncestorSetterInfoConverter implements TreeCopier.InfoConverter<ExtendedNode, ExtendedInfo>
	{
		public ExtendedInfo convert(ExtendedNode oi)
		{
			ExtendedNode theContentAncestorNode = mapNodeToItsContentAncestor.get(oi);
			ExtendedInfo contentAncestor = (null==theContentAncestorNode)?null:theContentAncestorNode.getInfo();
			AdditionalNodeInformation additionalInfo = AdditionalInformationServices.setContentAncestor(oi.getInfo().getAdditionalNodeInformation(), contentAncestor);
			return new ExtendedInfo(oi.getInfo(),additionalInfo);
		}
	}

	
	private void fillMapContentAncestor(ExtendedNode subtree, ExtendedNode currentKnownAncestor)
	{
		mapNodeToItsContentAncestor.put(subtree,currentKnownAncestor);
		if (isContent(subtree))
		{
			currentKnownAncestor = subtree;
		}
		if (subtree.getChildren()!=null)
		{
			for (ExtendedNode child : subtree.getChildren())
			{
				fillMapContentAncestor(child, currentKnownAncestor);
			}
		}
	}
	
	private boolean isContent(ExtendedNode node)
	{
		return contentPoses.contains(
				InfoGetFields.getPartOfSpeechObject(node.getInfo()).getCanonicalPosTag());
	}
	
	
	
	private ExtendedNode tree;
	
	private Map<ExtendedNode, ExtendedNode> mapNodeToItsContentAncestor;

	private TreeCopier<ExtendedInfo, ExtendedNode, ExtendedInfo, ExtendedNode> treeCopier;
	private ExtendedNode generatedTree = null;
}
