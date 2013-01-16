package eu.excitementproject.eop.common.datastructures;

import java.util.HashMap;

/**
 * 
 * @author Asher Stern
 * @since Aug 30, 2010
 *
 * @param <K>
 */
public class IntValueMap<K> extends HashMap<K,Integer>
{
	private static final long serialVersionUID = -848303382262465247L;

	public Integer get(Object key)
	{
		Integer ret = 0;
		Integer realValue = super.get(key);
		if (null==realValue)
			ret = 0;
		else
			ret = realValue;
		
		return ret;
	}
	

}
