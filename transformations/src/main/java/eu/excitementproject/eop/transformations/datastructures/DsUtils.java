package eu.excitementproject.eop.transformations.datastructures;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * A collection of utilities for data-structures.
 * 
 * @author Asher Stern
 * @since February 2011
 *
 */
public class DsUtils
{
	/**
	 * Takes two {@link ValueSetMap}s, and creates a new {@link ValueSetMap} composed by them.
	 * The new {@linkplain ValueSetMap} is constructed as follows:
	 * For each <k,u> in "left": for each <u,v> in "right" - the new {@linkplain ValueSetMap} will
	 * contain <k,v>
	 *   
	 * @param <K>
	 * @param <U>
	 * @param <V>
	 * @param left
	 * @param right
	 * @return
	 */
	public static <K, U, V> ValueSetMap<K, V> compose(ValueSetMap<K, U> left, ValueSetMap<U, V> right)
	{
		ValueSetMap<K, V> ret = new SimpleValueSetMap<K, V>();
		for (K key : left.keySet())
		{
			if (left.get(key)!=null)
			{
				for (U intermediate : left.get(key))
				{
					if (right.containsKey(intermediate))
					{
						if (right.get(intermediate)!=null)
						{
							for (V value : right.get(intermediate))
							{
								ret.put(key,value);
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * Adds all (L,R) keys-values from <code>mapToAdd</code> to <code>map</code>
	 * @param <L>
	 * @param <R>
	 * @param map
	 * @param mapToAdd
	 */
	public static <L,R> void BidiMapAddAll(BidirectionalMap<L, R> map, BidirectionalMap<L, R> mapToAdd)
	{
		for (L l : mapToAdd.leftSet())
		{
			if (!map.leftContains(l))
			{
				map.put(l, mapToAdd.leftGet(l));
			}
		}
	}

	/**
	 *
	 * Returns <tt>true</tt> if and only if the given <code>set</code> contains
	 * the given <code>lemmaAndPos</code>, ignoring the string representation
	 * of its part-of-speech, but depending solely on its {@link SimplerCanonicalPosTag}.
	 * @param set
	 * @param lemmaAndPos
	 * @return
	 * 
	 * @deprecated inefficient
	 */
	@Deprecated
	public static boolean containsCanonical(Set<LemmaAndPos> set, LemmaAndPos lemmaAndPos)
	{
		return containsCanonical((Iterable<LemmaAndPos>)set,lemmaAndPos);
	}
	
	/**
	 * Returns <tt>true</tt> if and only if the given <code>set</code> contains
	 * the given <code>lemmaAndPos</code>, ignoring the string representation
	 * of its part-of-speech, but depending solely on its {@link SimplerCanonicalPosTag}.
	 * @param set
	 * @param lemmaAndPos
	 * @return
	 * 
	 * @deprecated inefficient
	 */
	@Deprecated
	public static boolean containsCanonical(Iterable<LemmaAndPos> set, LemmaAndPos lemmaAndPos)
	{
		boolean found = false;
		for (LemmaAndPos lemmaAndPosItem : set)
		{
			if (lemmaAndPos.getLemma().equals(lemmaAndPosItem.getLemma()))
			{
				if (simplerPos(lemmaAndPos.getPartOfSpeech().getCanonicalPosTag())==simplerPos(lemmaAndPosItem.getPartOfSpeech().getCanonicalPosTag()))
				{
					found = true;
					break;
				}
			}
		}
		return found;
	}

	/**
	 * Returns true if the two maps are equal.
	 * Since the equal method of LinkedHashMap does not take the order into account,
	 * this function was created, which does take order into account.
	 * @param <K>
	 * @param <V>
	 * @param map1
	 * @param map2
	 * @return
	 */
	public static <K,V> boolean linkedHashMapEquals(LinkedHashMap<K, V> map1, LinkedHashMap<K, V> map2)
	{
		if (map1==map2)return true;
		else if ( (null==map1) || (null==map2) )return false;
		else
		{
			boolean ret = true;
			Iterator<Map.Entry<K, V>> iter1 = map1.entrySet().iterator();
			Iterator<Map.Entry<K, V>> iter2 = map2.entrySet().iterator();
			while (iter1.hasNext() && iter2.hasNext())
			{
				Map.Entry<K, V> entry1 = iter1.next();
				Map.Entry<K, V> entry2 = iter2.next();
				if ( 
						(entry1.getKey().equals(entry2.getKey()))
						&&
						(entry1.getValue().equals(entry2.getValue()))
					)
					;
				else
					ret = false;
			}
			if ( (iter1.hasNext()) || (iter2.hasNext()) )
				ret = false;
			
			return ret;
		}
	}
	
	public static <T> boolean linkedHashSetEquals(Set<T> set1, Set<T> set2)
	{
		if (set1==set2) return true;
		if ( (null==set1) || (null==set2) )return false;
		
		boolean ret = true;
		Iterator<T> iter1 = set1.iterator();
		Iterator<T> iter2 = set2.iterator();
		while (iter1.hasNext() && iter2.hasNext())
		{
			T t1 = iter1.next();
			T t2 = iter2.next();
			if (t1==t2) ;
			else if ( (null==t1) || (null==t2) ) ret = false;
			else if (!t1.equals(t2)) ret = false;
			
			if (false == ret) break;
		}
		if (iter1.hasNext() || iter2.hasNext())
		{
			return false;
		}
		else
		{
			return ret;
		}
	}
	
	public static <T> boolean intersectionNotEmpty(Set<T> set1, Set<T> set2)
	{
		if (set1==set2)return true;
		else if ( (set1==null) || (set2==null) )return false;
		else
		{
			boolean ret = false;
			for (T t1 : set1)
			{
				for (T t2 : set2)
				{
					if (t1.equals(t2))
					{
						ret=true;
						break;
					}
				}
				if (true==ret)break;
			}
			return ret;
		}
	}
	
	public static <E extends Comparable<E>> LinkedHashSet<E> createSorted(Iterable<E> iterable)
	{
		List<E> list = new ArrayList<E>();
		for (E e : iterable)
		{
			list.add(e);
		}
		Collections.sort(list);
		
		LinkedHashSet<E> ret = new LinkedHashSet<E>();
		for (E e : list)
		{
			ret.add(e);
		}
		return ret;
	}
	
	public static <E extends Comparable<E>> ImmutableList<E> createSortedImmutableList(Iterable<E> iterable)
	{
		List<E> list = new ArrayList<E>();
		for (E e : iterable)
		{
			list.add(e);
		}
		Collections.sort(list);

		return new ImmutableListWrapper<E>(list);
	}
	

	public static <E> LinkedHashSet<E> copySet(Collection<E> set)
	{
		LinkedHashSet<E> ret = new LinkedHashSet<E>();
		ret.addAll(set);
		return ret;
	}

	public static <K,V> BooleanAndString immutableMapEquals(
			ImmutableMap<K, V> map1,
			ImmutableMap<K, V> map2,
			boolean withString,
			String map1Name,
			String map2Name
			)
	{
		BooleanAndString ret = null;
		if (map1.equals(map2))
		{
			ret = new BooleanAndString(true, null);
		}
		else
		{
			if (!withString)
			{
				ret = new BooleanAndString(false, null);
			}
			else
			{
				if (map1.keySet().equals(map2.keySet()))
				{
					for (K k : map1.keySet())
					{
						if (!map1.get(k).equals(map2.get(k)))
						{
							ret = new BooleanAndString(false, "Value of "+k+" in "+map1Name+" is "+map1.get(k)+" and is differ from the value in "+map2Name+" which is "+map2.get(k));
							break;
						}
					}
					if (null==ret)
					{
						ret = new BooleanAndString(false, "Unknown incompatibility of values between "+map1Name+" and "+map2Name);
					}
				}
				else
				{
					ImmutableSet<K> map2KeySet = map2.keySet();
					for (K k : map1.keySet())
					{
						if (!map2KeySet.contains(k))
						{
							ret = new BooleanAndString(false, map2Name+" does not contain \""+k+"\"");
						}
					}
					if (null==ret)
					{
						ImmutableSet<K> map1KeySet = map1.keySet();
						for (K k : map2.keySet())
						{
							if (!map1KeySet.contains(k))
							{
								ret = new BooleanAndString(false, map1Name+" does not contain \""+k+"\"");
							}
						}
					}
					if (null==ret)
					{
						ret = new BooleanAndString(false, "unknown incompatibility of keys between "+map1Name+" and "+map2Name);
					}
				} // end of else (keys are not equal)
			} // end of else (withString is true)
		} // end of else (maps not equal)
		return ret;
	}
	
	public static String collectionToString(Collection<?> collection)
	{
		StringBuilder sb = new StringBuilder();
		boolean firstIteration = true;
		for (Object object : collection)
		{
			if (firstIteration) firstIteration=false;
			else sb.append(", ");
			sb.append(object.toString());
		}
		return sb.toString();
	}

	public static <L, R, M> BidirectionalMap<L, R> concatenateBidiMaps(BidirectionalMap<L, M> map1,BidirectionalMap<M, R> map2) throws TeEngineMlException
	{
		boolean failed = false;
		BidirectionalMap<L, R> ret = new SimpleBidirectionalMap<L, R>();
		for (L l : map1.leftSet())
		{
			M m = map1.leftGet(l);
			if (!map2.leftContains(m)) failed=true;
			ret.put(l, 
					map2.leftGet(m)
					);
		}
		if (failed) throw new TeEngineMlException("Concatenation failed.");
		else return ret;
	}
}
