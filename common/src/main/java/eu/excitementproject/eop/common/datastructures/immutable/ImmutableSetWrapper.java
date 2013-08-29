package eu.excitementproject.eop.common.datastructures.immutable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * A simple implementation of {@link ImmutableSet}.
 * <P>
 * The real set is given in the constructor, and only read operations
 * are exposed.
 * Note that one that holds a reference original set, <B> can </B>
 * change it.
 * <BR>
 * This implementation only insures that one who gets a reference
 * to the {@link ImmutableSet} cannot change it unless it also gets
 * a reference to the original set.
 * @author Asher Stern
 *
 * @param <T>
 */
public class ImmutableSetWrapper<T> extends AbstractImmutableCollectionWrapper<T>
implements ImmutableSet<T>
{
	private static final long serialVersionUID = -1624773454521001947L;


	public ImmutableSetWrapper(Set<T> realSet)
	{
		super(realSet);
		this.realSet = realSet;
		// both realCollection and realSet point to the same object.
	}

	@Override
	public ImmutableCollection<T> getImmutableCollectionCopy()
	{
		return getImmutableSetCopy();
	}

	@Override
	public Collection<T> getMutableCollectionCopy()
	{
		return getMutableSetCopy();
	}

	public ImmutableSet<T> getImmutableSetCopy()
	{
		if (this.realSet==null) return new ImmutableSetWrapper<T>(null);
		else return new ImmutableSetWrapper<T>(new LinkedHashSet<T>(this.realSet));
	}

	public LinkedHashSet<T> getMutableSetCopy()
	{
		if (null==this.realSet) return new LinkedHashSet<T>();
		else return new LinkedHashSet<T>(this.realSet);
	}
	
	
	final protected Set<T> realSet; // must point the same object as "wrappedCollection"


	@Override
	public int hashCode()
	{
		return super.hashCode();
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj==null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		return super.equals(obj);
	}
	
	public String toString() {
		if (realSet!=null)
			return realSet.toString();
		else
			return null;
	}
}


