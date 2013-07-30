package eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.nominals;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.StandardSpecific;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMapWrapper;

/**
 * Holds a map from Nomlex relations to Stanford-dependencies relations.
 * @author Asher Stern
 * @since Oct 15, 2012
 *
 */
@StandardSpecific({"stanford-dependencies","nomlex"})
public class PlaceToRelationMap
{
	public static ImmutableMap<String, Set<String>> getMapPlaceToRelation()
	{
		return IMMUTABLE_MAP_PLACE_TO_RELATIONS;
	}

	private static final Map<String, Set<String>> MAP_PLACE_TO_RELATIONS;
	private static final ImmutableMap<String, Set<String>> IMMUTABLE_MAP_PLACE_TO_RELATIONS;
	static
	{
		MAP_PLACE_TO_RELATIONS = new LinkedHashMap<String, Set<String>>();
		MAP_PLACE_TO_RELATIONS.put("DET-POSS", Collections.singleton("poss"));
		MAP_PLACE_TO_RELATIONS.put("N-N-MOD", Collections.singleton("nn"));
		
		IMMUTABLE_MAP_PLACE_TO_RELATIONS = new ImmutableMapWrapper<String, Set<String>>(MAP_PLACE_TO_RELATIONS);
	}
}
