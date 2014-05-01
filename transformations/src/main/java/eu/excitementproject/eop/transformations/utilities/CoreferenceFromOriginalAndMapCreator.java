package eu.excitementproject.eop.transformations.utilities;
import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;


/**
 * Not Used
 * 
 * @deprecated
 * 
 * @author Asher Stern
 * @since Aug 9, 2012
 *
 * @param <T>
 */
@Deprecated
public class CoreferenceFromOriginalAndMapCreator<T>
{
	public CoreferenceFromOriginalAndMapCreator(
			TreeCoreferenceInformation<T> originalCorefInformation,
			BidirectionalMap<T, T> map)
	{
		super();
		this.originalCorefInformation = originalCorefInformation;
		this.map = map;
	}



	public void create() throws TreeCoreferenceInformationException, TeEngineMlException
	{
		boolean warn = false;
		coref = new TreeCoreferenceInformation<T>();
		int max = 0;
		for (Integer groupId : originalCorefInformation.getAllExistingGroupIds())
		{
			if (groupId.intValue()>max)
				max=groupId.intValue();
		}
		int cur = 0;
		do
		{
			cur = coref.createNewGroup().intValue();
		} while (cur < max);
		
		for (Integer groupId : originalCorefInformation.getAllExistingGroupIds())
		{
			for (T node : originalCorefInformation.getGroup(groupId))
			{
				if (!map.leftContains(node))
				{
					// throw new TeEngineMlException("map does not contain node.");
					warn = true;
					coref.addNodeToGroup(groupId, node);
				}
				else
				{
					coref.addNodeToGroup(groupId, map.leftGet(node));
				}
			}
		}
		if (warn)
		{
			logger.warn("Some nodes are not contained in the map-of-nodes.\n" +
					"This is a known limitation on RTE 6 7.\n" +
					"However, if you see this warnings when using other data-sets, it means a bug.");
		}
	}
	
	

	public TreeCoreferenceInformation<T> getCoref() throws TeEngineMlException
	{
		if (null==coref) throw new TeEngineMlException("You did not call create()");
		return coref;
	}



	private final TreeCoreferenceInformation<T> originalCorefInformation;
	private final BidirectionalMap<T, T> map;
	private TreeCoreferenceInformation<T> coref = null;
	
	private static final Logger logger = Logger.getLogger(CoreferenceFromOriginalAndMapCreator.class);
}
