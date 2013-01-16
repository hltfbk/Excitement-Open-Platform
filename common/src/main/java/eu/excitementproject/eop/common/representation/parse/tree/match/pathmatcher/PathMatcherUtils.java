package eu.excitementproject.eop.common.representation.parse.tree.match.pathmatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatcherException;

/**
 * 
 * @author Asher Stern
 * @since Feb 6, 2012
 *
 */
public class PathMatcherUtils
{
	public static <L, R> BidirectionalMap<L, R> combineTwoMaps(BidirectionalMap<L, R> map1, BidirectionalMap<L, R> map2) throws MatcherException
	{
		BidirectionalMap<L, R> ret = new SimpleBidirectionalMap<L, R>();
		for (L l1 : map1.leftSet())
		{
			ret.put(l1, map1.leftGet(l1));
		}
		for (L l2 : map2.leftSet())
		{
			ret.put(l2, map2.leftGet(l2));
		}
		return ret;
	}
	

	public static <L, R> List<BidirectionalMap<L, R>> combineLists(Collection<? extends BidirectionalMap<L, R>> col1,Collection<? extends BidirectionalMap<L, R>> col2) throws MatcherException
	{
		List<BidirectionalMap<L, R>> ret = new ArrayList<BidirectionalMap<L,R>>(col1.size()+col2.size());
		{
			for (BidirectionalMap<L, R> map1 : col1)
			{
				for (BidirectionalMap<L, R> map2 : col2)
				{
					ret.add(combineTwoMaps(map1,map2));
				}
			}
		}
		return ret;
	}
}
