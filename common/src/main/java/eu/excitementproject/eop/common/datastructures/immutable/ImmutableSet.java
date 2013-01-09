package eu.excitementproject.eop.common.datastructures.immutable;

import java.util.Set;

/**
 * Much like <code> java.util.Set </code> but only with
 * "read" operations.
 * <P>
 * <B>Note:</B> it is strongly recommended to use {@link #getImmutableSetCopy()} method, in
 * cases which an iterator runs on the and makes operations. I don't have the time
 * to explain it now, but if you get runtime-exception when using <code>for(X x : S)</code>
 * loops, or when using Iterator - try using a copy of the set instead of the original one.
 * This is correct to <code>java.util.Set</code> as well, in which another mechanism (e.g.
 * <code>addAll()</code> method) is used to create copies.
 * <P>
 * Note: ImmutableSet may be non-thread-safe (depending on underlying implementation
 * that may be non-thread-safe of read methods). (But usually ImmutableSets <B>are</B>
 * thread safe, since even LinkedHashSet has no option to be ordered according to access, but
 * according to insertion. See the JavaDoc for LinkedHashMap and LinkedHashSet in JDK documentation)
 * 
 * @author Asher Stern
 *
 * @param <T>
 */
public interface ImmutableSet<T> extends ImmutableCollection<T>
{
	public Set<T> getMutableSetCopy();
	
	public ImmutableSet<T> getImmutableSetCopy();

}
