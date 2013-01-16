package eu.excitementproject.eop.transformations.utilities.parsetreeutils;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier;
import eu.excitementproject.eop.transformations.representation.AdditionalInformationServices;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;
import eu.excitementproject.eop.transformations.representation.OriginalInfoTrace;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Given a generated-tree, and a tree from which that generated-tree was generated,
 * this class copies the field of {@link OriginalInfoTrace} from the original
 * tree's nodes into the given generated tree's nodes.
 * 
 * @see OriginalInfoTrace
 * @see AdditionalNodeInformation
 * 
 * @author Asher Stern
 * @since Oct 22, 2011
 *
 */
public class OriginalInfoTraceSetter
{
	public OriginalInfoTraceSetter(ExtendedNode generatedTree,
			ValueSetMap<ExtendedNode, ExtendedNode> mapOriginalToGenerated) throws TeEngineMlException
	{
		super();
		this.inputGeneratedTree = generatedTree;
		this.mapOriginalToGenerated = mapOriginalToGenerated;
	}
	
	public void set() throws TeEngineMlException
	{
		TreeCopier<ExtendedInfo, ExtendedNode, ExtendedInfo, ExtendedNode> copier =
			new TreeCopier<ExtendedInfo, ExtendedNode, ExtendedInfo, ExtendedNode>(inputGeneratedTree, new OriginalInfoTrace_InfoConverter(), new ExtendedNodeConstructor());
		
		copier.copy();
		
		if (this.exception!=null)
			throw exception;
		
		newGeneratedTree = copier.getGeneratedTree();
		mapInputToNew = copier.getNodesMap();
	}
	
	public ExtendedNode getNewGeneratedTree() throws TeEngineMlException
	{
		if (null == newGeneratedTree) throw new TeEngineMlException("set() was not called");
		return newGeneratedTree;
	}

	public BidirectionalMap<ExtendedNode, ExtendedNode> getMapInputToNew() throws TeEngineMlException
	{
		if (null == mapInputToNew) throw new TeEngineMlException("set() was not called");
		return mapInputToNew;
	}

	
	
	private class OriginalInfoTrace_InfoConverter implements TreeCopier.InfoConverter<ExtendedNode, ExtendedInfo>
	{
		public ExtendedInfo convert(ExtendedNode oi)
		{
			// oi is a node in "generatedTree"
			
			ExtendedInfo ret = null;
			if (mapOriginalToGenerated.containsValue(oi))
			{
				ImmutableSet<ExtendedNode> fromWhich = mapOriginalToGenerated.getKeysOf(oi);
				if (fromWhich.size()!=1)
					exception = new TeEngineMlException("Wrong mapping. fromWhich.size() = "+fromWhich.size()+
							"\nIt seems that mapOriginalToGenerated, which is actually given from some subclass of GenerationOperation" +
							"was build incorrectly. It cannot happen that a generated node was build from two different nodes." +
							"Only one source (a node in the original tree) is permitted.");
				else
				{
					ExtendedNode nodeFromWhich = fromWhich.iterator().next();
					OriginalInfoTrace originalInfoTrace = ExtendedInfoGetFields.getOriginalInfoTrace(nodeFromWhich.getInfo());
					if (originalInfoTrace!=null)
					{
						AdditionalNodeInformation additionalInfo =
							AdditionalInformationServices.setOriginalInfoTrace(oi.getInfo().getAdditionalNodeInformation(), originalInfoTrace);

						ret = new ExtendedInfo(oi.getInfo(), additionalInfo);
					}
				}
			}
			if (null==ret)
			{
				try
				{
					OriginalInfoTrace newTrace = new OriginalInfoTrace(oi.getInfo(),null);
					AdditionalNodeInformation originalAdditionalInfo = null;
					if (oi.getInfo()!=null){originalAdditionalInfo=oi.getInfo().getAdditionalNodeInformation();}
					AdditionalNodeInformation additionalInfo = AdditionalInformationServices.setOriginalInfoTrace(originalAdditionalInfo, newTrace);

					ret = new ExtendedInfo(oi.getInfo(),additionalInfo);
				}
				catch (TeEngineMlException e)
				{
					exception = e;
				}

			}
			
			return ret;
		}
	}





	private ExtendedNode inputGeneratedTree;
	private ValueSetMap<ExtendedNode, ExtendedNode> mapOriginalToGenerated;
	
	private ExtendedNode newGeneratedTree = null;
	private BidirectionalMap<ExtendedNode, ExtendedNode> mapInputToNew = null;
	private TeEngineMlException exception = null;
	
}
