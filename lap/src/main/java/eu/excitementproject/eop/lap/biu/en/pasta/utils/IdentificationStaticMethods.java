package eu.excitementproject.eop.lap.biu.en.pasta.utils;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.StandardSpecific;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.RelationTypes;

/**
 * 
 * @author Asher Stern
 * @since Aug 6, 2013
 *
 */
@StandardSpecific("stanford-dependencies") // All the methods here are supposed to be restricted to Stanford-dependencies
public class IdentificationStaticMethods
{

	public static <I extends Info, S extends AbstractNode<I, S>> Set<S> getInternalNodes(S node)
	{
		Set<S> ret = new LinkedHashSet<S>();
		ret.add(node);
		if (node.hasChildren())
		{
			for (S child : node.getChildren())
			{
				String relation = InfoGetFields.getRelation(child.getInfo());
				if (RelationTypes.getSemanticInternalFacetRelations().contains(relation))
				{
					ret.addAll(getInternalNodes(child));
				}
			}
		}
		return ret;
	}

}
