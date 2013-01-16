package eu.excitementproject.eop.transformations.operations.rules;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity;


/**
 * A marker for rule bases that should be applied only on nodes that
 * are annotated by {@link NamedEntity}'ies that are in the set of
 * {@link NamedEntity}'ies returned by the method {@link #getNamedEntitiesOfRuleBase()}.
 * 
 * @author Asher Stern
 * @since Apr 8, 2012
 *
 */
public interface RuleBaseWithNamedEntities
{
	public Set<NamedEntity> getNamedEntitiesOfRuleBase() throws RuleBaseException;
}
