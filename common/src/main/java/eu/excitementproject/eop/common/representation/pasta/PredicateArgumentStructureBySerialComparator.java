package eu.excitementproject.eop.common.representation.pasta;

import java.util.Comparator;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * Can be used for sorting a collection of {@link PredicateArgumentStructure} objects.
 * @author Asher Stern
 *
 */
public class PredicateArgumentStructureBySerialComparator<I extends Info, S extends AbstractNode<I, S>> implements Comparator<PredicateArgumentStructure<I, S>>
{

	@Override
	public int compare(PredicateArgumentStructure<I, S> o1,
			PredicateArgumentStructure<I, S> o2)
	{
		int serial1 = getSerial(o1);
		int serial2 = getSerial(o2);
		if (serial1<serial2)
			return -1;
		else if (serial1 == serial2)
			return 0;
		else
			return 1;
		
	}
	
	private static <I extends Info, S extends AbstractNode<I, S>> int getSerial(PredicateArgumentStructure<I, S> pas)
	{
		return InfoGetFields.getSerial(pas.getPredicate().getHead().getInfo());
	}

}
