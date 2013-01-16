package eu.excitementproject.eop.transformations.utilities.parsetreeutils;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier;
import eu.excitementproject.eop.transformations.representation.AdditionalInformationServices;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;
import eu.excitementproject.eop.transformations.representation.OriginalInfoTrace;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Sets the field of {@link OriginalInfoTrace} of each node to be the
 * node-info itself.
 * <P>
 * <B>Note that the given parse tree must be an original parse tree.
 * I.e., a parse tree as parsed by the parser. Not an intermediate parse tree
 * that was generated during the proof construction.
 * 
 * @author Asher Stern
 * @since Oct 22, 2011
 *
 */
public class SelfTraceSetter
{
	public SelfTraceSetter(ExtendedNode tree)
	{
		super();
		this.tree = tree;
	}
	
	public void set() throws TeEngineMlException
	{
		TreeCopier<ExtendedInfo, ExtendedNode, ExtendedInfo, ExtendedNode> copier =
			new TreeCopier<ExtendedInfo, ExtendedNode, ExtendedInfo, ExtendedNode>(tree, new SelfTrace_InfoConverter(), new ExtendedNodeConstructor());
		copier.copy();
		
		if (this.exception!=null)
		{
			throw this.exception;
		}
		
		newTree = copier.getGeneratedTree();
		mapping = copier.getNodesMap();
	}
	
	
	
	public ExtendedNode getNewTree()
	{
		return newTree;
	}

	public BidirectionalMap<ExtendedNode, ExtendedNode> getMapping()
	{
		return mapping;
	}



	private class SelfTrace_InfoConverter implements TreeCopier.InfoConverter<ExtendedNode, ExtendedInfo>
	{
		public ExtendedInfo convert(ExtendedNode os)
		{
			try
			{
				if (os.getInfo()!=null)
				{
					AdditionalNodeInformation additionalNodeInformation = AdditionalInformationServices.setOriginalInfoTrace(os.getInfo().getAdditionalNodeInformation(), new OriginalInfoTrace(os.getInfo(), os));
					return new ExtendedInfo(os.getInfo(),additionalNodeInformation);
				}
				else
				{
					return null;
				}
			}
			catch (TeEngineMlException e)
			{
				SelfTraceSetter.this.exception = e;
				return null;
			}

		}
	}
	
	private ExtendedNode tree;
	
	private TeEngineMlException exception = null;
	
	private ExtendedNode newTree;
	private BidirectionalMap<ExtendedNode, ExtendedNode> mapping;
	
	
}
