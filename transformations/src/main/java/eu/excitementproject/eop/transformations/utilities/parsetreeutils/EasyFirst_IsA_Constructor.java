package eu.excitementproject.eop.transformations.utilities.parsetreeutils;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelationType;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech.PennPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.transformations.datastructures.DsUtils;
import eu.excitementproject.eop.transformations.representation.AdditionalInformationServices;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;

/**
 * 
 * @author Asher Stern
 * @since Sep 9, 2012
 *
 */
@LanguageDependent("english")
@ParserSpecific("easyfirst")
public class EasyFirst_IsA_Constructor
{
	public EasyFirst_IsA_Constructor(ExtendedNode entity1, ExtendedNode entity2)
	{
		super();
		this.entity1 = entity1;
		this.entity2 = entity2;
	}

	public void construct() throws UnsupportedPosTagStringException
	{
		bidiMapOriginalToGenerated = new SimpleBidirectionalMap<ExtendedNode, ExtendedNode>();
		affectedNodes = new LinkedHashSet<ExtendedNode>();
		
		copyEntities();
		
		entity2.addChild(entity1);

		ExtendedNode beNode =
				new ExtendedNode(new ExtendedInfo(
						new DefaultInfo("IS_A_be", new DefaultNodeInfo("be", "be", 0, null, new DefaultSyntacticInfo(new PennPartOfSpeech(PennPosTag.VBZ))), new DefaultEdgeInfo(new DependencyRelation("cop", null))),
						AdditionalInformationServices.emptyInformation()));
		entity2.addChild(beNode);

		ExtendedNode punctNode =
				new ExtendedNode(new ExtendedInfo(
						new DefaultInfo("IS_A_punct", new DefaultNodeInfo(".", ".", 0, null, new DefaultSyntacticInfo(new PennPartOfSpeech("."))), new DefaultEdgeInfo(new DependencyRelation("punct", null))),
						AdditionalInformationServices.emptyInformation()));
		entity2.addChild(punctNode);
		
		generatedTree = entity2;
		
		affectedNodes.add(beNode);
		affectedNodes.add(punctNode);
		affectedNodes.add(entity1);
		affectedNodes.add(entity2);
	}

	public ExtendedNode getGeneratedTree()
	{
		return generatedTree;
	}
	
	public BidirectionalMap<ExtendedNode, ExtendedNode> getBidiMapOriginalToGenerated()
	{
		return bidiMapOriginalToGenerated;
	}

	public Set<ExtendedNode> getAffectedNodes()
	{
		return affectedNodes;
	}




	private void copyEntities()
	{
		CopyDepthLimitedTree copier1 =
				new CopyDepthLimitedTree(entity1, new DefaultEdgeInfo(new DependencyRelation("nsubj", DependencyRelationType.SUBJECT)));
		copier1.copy(Constants.DEFAULT_COPY_SUBTREE_DEPTH);
		entity1 = copier1.getGeneratedTree();
		DsUtils.BidiMapAddAll(bidiMapOriginalToGenerated, copier1.getMapOriginalToGenerated());


		CopyDepthLimitedTree copier2 =
				new CopyDepthLimitedTree(entity2, new DefaultEdgeInfo(new DependencyRelation("", null)));
		copier2.copy(Constants.DEFAULT_COPY_SUBTREE_DEPTH);
		entity2 = copier2.getGeneratedTree();
		DsUtils.BidiMapAddAll(bidiMapOriginalToGenerated, copier2.getMapOriginalToGenerated());
	}


	private ExtendedNode entity1;
	private ExtendedNode entity2;
	
	private Set<ExtendedNode> affectedNodes = null;
	private BidirectionalMap<ExtendedNode, ExtendedNode> bidiMapOriginalToGenerated;
	
	private ExtendedNode generatedTree;
}
