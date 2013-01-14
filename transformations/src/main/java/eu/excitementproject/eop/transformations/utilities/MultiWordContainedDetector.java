package eu.excitementproject.eop.transformations.utilities;
import java.util.List;

/**
 * This class is deprecated and not used.
 * 
 * @deprecated
 * 
 * @author Asher Stern
 * @since 2011
 *
 */

@Deprecated
public class MultiWordContainedDetector
{
	public MultiWordContainedDetector(List<String> list1, List<String> list2)
	{
		if (list1.size()<list2.size())
		{
			this.smallList = list1;
			this.largeList = list2;
		}
		else
		{
			this.smallList = list2;
			this.largeList = list1;
		}
	}
	
	public boolean contained()
	{
		boolean ret = false;
		for (int largeIndex=0;largeIndex<largeList.size();++largeIndex)
		{
			boolean containedHere = true;
			for (int smallIndex=0;smallIndex<smallList.size();++smallIndex)
			{
				if (!largeList.get(largeIndex).equalsIgnoreCase(smallList.get(smallIndex)))
				{
					containedHere = false;
					break;
				}
			}
			if (containedHere)
			{
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	private List<String> smallList;
	private List<String> largeList;

}
