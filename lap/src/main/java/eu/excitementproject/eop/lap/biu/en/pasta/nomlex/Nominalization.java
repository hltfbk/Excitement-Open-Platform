package eu.excitementproject.eop.lap.biu.en.pasta.nomlex;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMapWrapper;
import eu.excitementproject.eop.common.representation.pasta.ArgumentType;

/**
 * Represents a nominal, and information about this nominal, as
 * extracted from Nomlex-corpus.
 * (see also JavaDoc on the private fields of this class).
 * 
 * @author Asher Stern
 * @since Oct 14, 2012
 *
 */
public class Nominalization
{
	public Nominalization(String nominal, ImmutableList<String> verbs,
			ArgumentType itsOwnRole,
			ImmutableMap<NomlexArgument, ArgumentType> mapArgumentToType)
	{
		super();
		this.nominal = nominal;
		this.verbs = verbs;
		this.itsOwnRole = itsOwnRole;
		this.mapArgumentToType = mapArgumentToType;
		createMapTypeToArgument();
	}
	
	public String getNominal()
	{
		return nominal;
	}
	public ImmutableList<String> getVerbs()
	{
		return verbs;
	}
	public ArgumentType getItsOwnRole()
	{
		return itsOwnRole;
	}
	public ImmutableMap<NomlexArgument, ArgumentType> getMapArgumentToType()
	{
		return mapArgumentToType;
	}

	public ImmutableMap<ArgumentType, Set<NomlexArgument>> getMapTypeToArgument()
	{
		return mapTypeToArgument;
	}
	
	public String toString() {
		return String.format("%s(%s, %s)", getClass().getSimpleName(), nominal, verbs.mutableListToString());
	}

	private void createMapTypeToArgument()
	{
		Map<ArgumentType, Set<NomlexArgument>> map = new LinkedHashMap<ArgumentType, Set<NomlexArgument>>(); 
		for (NomlexArgument argument : mapArgumentToType.keySet())
		{
			ArgumentType type = mapArgumentToType.get(argument);
			if (null==map.get(type))
			{
				map.put(type, new LinkedHashSet<NomlexArgument>());
			}
			map.get(type).add(argument);
		}
		mapTypeToArgument = new ImmutableMapWrapper<ArgumentType, Set<NomlexArgument>>(map);
	}


	/**
	 * The nominal itself. For example "abandonment"
	 */
	private final String nominal;
	
	/**
	 * List of verbal forms of this nominal. For example "abandon"
	 */
	private final ImmutableList<String> verbs;
	
	/**
	 * Optional: the nominal might be not only the predicate, but also
	 * an argument of the predicate. For example: the nominal "adventurer"
	 * indicates not only the predicate "adventure", but also the subject of
	 * this predicate.
	 * <BR>
	 * This field is null if the nominal has no role.
	 */
	private final ArgumentType itsOwnRole;

	/**
	 * Map from all the arguments in nomlex corpus of this predicate, to
	 * their type as {@link ArgumentType}.
	 * <BR>
	 * If you just want the set of arguments that are annotated in Nomlex-corpus
	 * for this predicate, take the keySet() of this map.
	 */
	private final ImmutableMap<NomlexArgument, ArgumentType> mapArgumentToType;
	
	/**
	 * The reverse direction of {@link #mapArgumentToType}.
	 */
	private ImmutableMap<ArgumentType, Set<NomlexArgument>> mapTypeToArgument;
	
}
