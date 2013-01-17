package eu.excitementproject.eop.transformations.representation;
import java.io.Serializable;

import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.AdvancedEqualities;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.OriginalInfoTraceSetter;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.SelfTraceSetter;


/**
 * 
 * The info of the original node in the text tree from which this node was derived.
 * To use this: Make sure that {@link Constants#TRACE_ORIGINAL_NODES} is <tt>true</tt>,
 * and run the engine. Then, use {@link AdvancedEqualities#findMatchingRelations(ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap, ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap)}
 * to get for each hypothesis node the corresponding node in the generated tree. Then,
 * take the {@link OriginalInfoTraceSetter} of that node (from its {@link AdditionalNodeInformation}).
 * 
 * 
 * @see AdditionalNodeInformation#getOriginalInfoTrace()
 * @see AdvancedEqualities#findMatchingRelations(ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap, ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap)
 * @see Constants#TRACE_ORIGINAL_NODES
 * @see OriginalInfoTraceSetter
 * @see SelfTraceSetter
 * 
 * 
 * @author Asher Stern
 * @since Oct 22, 2011
 *
 */
public class OriginalInfoTrace implements Serializable
{
	private static final long serialVersionUID = 3063726362889353840L;

	public OriginalInfoTrace(ExtendedInfo originalInfo, ExtendedNode nodeInOriginalTree) throws TeEngineMlException
	{
		super();
		this.originalInfo = originalInfo;
		this.nodeInOriginalTree = nodeInOriginalTree;
		if (this.nodeInOriginalTree!=null)
		{
			if (this.nodeInOriginalTree.getInfo()!=originalInfo)
			{
				throw new TeEngineMlException("BUG: Error when assigning the OriginalInfoTrace");
			}
		}
	}
	
	
	
	public ExtendedInfo getOriginalInfo()
	{
		return originalInfo;
	}
	public boolean isInOriginalTree()
	{
		return (nodeInOriginalTree!=null);
	}
	public ExtendedNode getNodeInOriginalTree()
	{
		return nodeInOriginalTree;
	}



	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((nodeInOriginalTree == null) ? 0 : nodeInOriginalTree
						.hashCode());
		result = prime * result
				+ ((originalInfo == null) ? 0 : originalInfo.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OriginalInfoTrace other = (OriginalInfoTrace) obj;
		if (nodeInOriginalTree == null)
		{
			if (other.nodeInOriginalTree != null)
				return false;
		} else if (!nodeInOriginalTree.equals(other.nodeInOriginalTree))
			return false;
		if (originalInfo == null)
		{
			if (other.originalInfo != null)
				return false;
		} else if (!originalInfo.equals(other.originalInfo))
			return false;
		return true;
	}






	/**
	 * If the "original info" is from a node that exists in the original text-tree,
	 * as given by the parser, then this field is assigned this node.
	 * Otherwise - it must be null.
	 */
	private final ExtendedNode nodeInOriginalTree;
	
	private final ExtendedInfo originalInfo;
}
