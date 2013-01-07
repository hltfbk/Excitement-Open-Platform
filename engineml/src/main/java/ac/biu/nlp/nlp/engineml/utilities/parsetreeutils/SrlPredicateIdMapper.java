package ac.biu.nlp.nlp.engineml.utilities.parsetreeutils;

import java.util.LinkedHashMap;
import java.util.Map;

import ac.biu.nlp.nlp.engineml.representation.ExtendedInfoGetFields;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.representation.srl.SrlPredicateId;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNodeUtils;

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
		for (ExtendedNode node : AbstractNodeUtils.treeToSet(tree))
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
