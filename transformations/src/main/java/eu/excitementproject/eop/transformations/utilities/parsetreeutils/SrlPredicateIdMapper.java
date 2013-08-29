package eu.excitementproject.eop.transformations.utilities.parsetreeutils;
import java.util.LinkedHashMap;
import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.srl_informations.SrlPredicateId;


/**
 * 
 * @author Asher Stern
 * @since Dec 27, 2011
 *
 */
public class SrlPredicateIdMapper
{
	public SrlPredicateIdMapper(ExtendedNode tree)
	{
		super();
		this.tree = tree;
	}
	
	public void map()
	{
		mapIdToLemma = new LinkedHashMap<SrlPredicateId, String>();
		for (ExtendedNode node : TreeIterator.iterableTree(tree))
		{
			SrlPredicateId id = ExtendedInfoGetFields.getSrlPredicateId(node.getInfo());
			if (id != null)
			{
				mapIdToLemma.put(id,InfoGetFields.getLemma(node.getInfo()));
			}
		}
	}
	
	public Map<SrlPredicateId, String> getMapIdToLemma()
	{
		return mapIdToLemma;
	}



	private ExtendedNode tree;
	private Map<SrlPredicateId, String> mapIdToLemma;
}
