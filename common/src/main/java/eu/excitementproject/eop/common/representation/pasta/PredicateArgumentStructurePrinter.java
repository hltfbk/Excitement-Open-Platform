package eu.excitementproject.eop.common.representation.pasta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.utilities.StringUtil;

/**
 * 
 * @author Asher Stern
 *
 */
public class PredicateArgumentStructurePrinter
{

	public static <I extends Info, S extends AbstractNode<I, S>> String getString(PredicateArgumentStructure<I,S> pas)
	{
		StringBuffer out = new StringBuffer();
		String predicateHead = InfoGetFields.getLemma(pas.getPredicate().getHead().getInfo());
		out.append(predicateHead);
		if (pas.getPredicate().getVerbsForNominal()!=null)
		{
			out.append(" ("+StringUtil.joinIterableToString(pas.getPredicate().getVerbsForNominal(),", ",true)+")");
		}
		out.append("\n");
		
		for (TypedArgument<I,S> typedArgument : pas.getArguments())
		{
			Argument<I,S> argument = typedArgument.getArgument();
			S semanticHead = argument.getSemanticHead();
			String lemma = InfoGetFields.getLemma(semanticHead.getInfo());
			
			out.append("\t"+typedArgument.getArgumentType().name()+" "+lemma+" ("+strSetNodes(argument.getNodes())+")\n");
		}
		
		for (ClausalArgument<I,S> clausalArgument : pas.getClausalArguments())
		{
			String lemma = InfoGetFields.getLemma(clausalArgument.getClause().getInfo());
			out.append("\t(c) "+clausalArgument.getArgumentType().name()+" "+lemma+"\n");
		}
		
		return out.toString();
	}
	
	private static <I extends Info, S extends AbstractNode<I, S>> String strSetNodes(Collection<S> nodes)
	{
		List<S> listNodes = getSortedBySerial(nodes);
		StringBuilder sb = new StringBuilder();
		boolean firstIteration = true;
		for (S node : listNodes)
		{
			if (firstIteration){firstIteration=false;}else{sb.append(" ");}
			
			sb.append(
					InfoGetFields.getWord(node.getInfo())
					);
		}
		return sb.toString();
	}

	
	
	// copied from ac.biu.nlp.nlp.predarg.utilities.PredArgsUtilities

	/**
	 * Returns the given parse-tree-nodes, sorted by the their "serial" field.
	 * @param nodes a collection of parse-tree-nodes.
	 * @return the given parse-tree-nodes, sorted by the their "serial" field.
	 */
	private static <I extends Info, S extends AbstractNode<I, S>> List<S> getSortedBySerial(Collection<S> nodes)
	{
		ArrayList<S> list = new ArrayList<S>(nodes.size());
		list.addAll(nodes);
		Collections.sort(list,new BySerialComparator<I, S>());
		return list;
	}

	
	
	// copied from ac.biu.nlp.nlp.predarg.utilities
	
	/**
	 * 
	 * @author Asher Stern
	 * @since Oct 9, 2012
	 *
	 */
	private static class BySerialComparator<I extends Info, S extends AbstractNode<I, S>> implements Comparator<S>
	{
		@Override
		public int compare(S o1, S o2)
		{
			int serial1 = -1;
			int serial2 = -1;
			if (o1!=null){if(o1.getInfo()!=null){if (o1.getInfo().getNodeInfo()!=null)
			{
				serial1 = o1.getInfo().getNodeInfo().getSerial();
			}}}
			if (o2!=null){if(o2.getInfo()!=null){if (o2.getInfo().getNodeInfo()!=null)
			{
				serial2 = o2.getInfo().getNodeInfo().getSerial();
			}}}
			
			if (serial1<serial2) return -1;
			else if (serial1==serial2) return 0;
			else return 1;
		}
	}

}
