package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.common.utilities.match.MatchFinder;
import eu.excitementproject.eop.common.utilities.match.Matcher;
import eu.excitementproject.eop.common.utilities.match.Operator;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;

/**
 * Aligns every word in ArkRef output to a parse-tree node.
 * More or less, every word should correspond to a parse-tree node.
 * This is a typical list-matching task, so it is performed using {@link Matcher}.
 * 
 * @author Asher Stern
 * @since Dec 9, 2013
 *
 */
public class ArkrefOutputAlignToTrees<I extends Info, S extends AbstractNode<I, S>>
{
	public ArkrefOutputAlignToTrees(List<S> trees,
			ArrayList<ArkrefOutputWord<I, S>> arkrefOutput)
	{
		super();
		this.trees = trees;
		this.arkrefOutput = arkrefOutput;
	}

	public void align() throws CoreferenceResolutionException
	{
		List<S> allNodesList = generateAllNodeList();
		Matcher<S, ArkrefOutputWord<I,S>> matcher = new Matcher<S, ArkrefOutputWord<I,S>>(
				allNodesList.iterator(),arkrefOutput.iterator(),
				new MatchFinder<S, ArkrefOutputWord<I,S>>()
				{
					@Override
					public void set(S lhs, ArkrefOutputWord<I,S> rhs)
					{
						this.lhs = lhs;
						this.rhs = rhs;
					}

					@Override
					public boolean areMatch()
					{
						String wordInTree = InfoGetFields.getWord(lhs.getInfo());
						String wordInArkref = rhs.getWord();
						return wordInTree.equals(wordInArkref);
					}
					private S lhs;
					private ArkrefOutputWord<I,S> rhs;
				},
				new Operator<S, ArkrefOutputWord<I,S>>()
				{
					@Override
					public void set(S lhs, ArkrefOutputWord<I, S> rhs)
					{
						this.lhs = lhs;
						this.rhs = rhs;
					}

					@Override
					public void makeOperation()
					{
						rhs.setAlignedNode(lhs);
					}
					private S lhs;
					private ArkrefOutputWord<I,S> rhs;
				}
				);
		
		matcher.makeMatchOperation();
	}
	
	private List<S> generateAllNodeList() throws CoreferenceResolutionException
	{
		List<S> allNodesList = new LinkedList<>();
		for (S tree : trees)
		{
			Map<Integer, S> mapBySerial = new LinkedHashMap<>();
			for (S node : TreeIterator.iterableTree(tree))
			{
				if (node.getAntecedent()==null)
				{
					int serial = InfoGetFields.getSerial(node.getInfo());
					if (mapBySerial.containsKey(serial)) {throw new CoreferenceResolutionException("Malformed input. The parse tree has more than one node with the serial "+serial);}
					mapBySerial.put(serial, node);
				}
			}
			List<Integer> serials = new ArrayList<>(mapBySerial.size());
			serials.addAll(mapBySerial.keySet());
			Collections.sort(serials);
			for (Integer serialKey : serials)
			{
				allNodesList.add(mapBySerial.get(serialKey));
			}
		}
		return allNodesList;
	}
	

	private final List<S> trees;
	private final ArrayList<ArkrefOutputWord<I,S>> arkrefOutput;
}
