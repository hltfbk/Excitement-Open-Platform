package eu.excitementproject.eop.common.representation.parse.tree;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Deprecated
public class BottomUpComputer<T,S extends AbstractNode<T, S>>
{
	public BottomUpComputer(S root)
	{
		this.root = root;
		compute();
	}
	
	public List<Set<S>> getLeavesLevels()
	{
		return this.leavesLevels;
	}
	
	private void compute()
	{
		parentMap = AbstractNodeUtils.parentMap(root);
		Set<S> alreadyVisited = new LinkedHashSet<S>();
		Set<S> leaves = AbstractNodeUtils.getLeaves(root);
		boolean leavesEmpty = true;
		if (leaves != null) if (leaves.size()>0) leavesEmpty = false;
		while (!leavesEmpty)
		{
			leavesLevels.add(leaves);
			alreadyVisited.addAll(leaves);
			Set<S> parents = new LinkedHashSet<S>();
			for (S leaf : leaves)
			{
				S parent = parentMap.get(leaf);
				if (parent!=null)
					parents.add(parent);
			}
			leaves = new LinkedHashSet<S>();
			for (S parent : parents)
			{
				boolean parentIsLeaf = true;
				if (parent.getChildren()!=null)
				{
					for (S child : parent.getChildren())
					{
						if (!alreadyVisited.contains(child))
							parentIsLeaf = false;
					}
				}
				if (parentIsLeaf)
					leaves.add(parent);
			}
			leavesEmpty = true;
			if (leaves != null) if (leaves.size()>0) leavesEmpty = false;
		}
	}
	
	
	
	private List<Set<S>> leavesLevels = new LinkedList<Set<S>>();
	private S root;
	private Map<S,S> parentMap;
	

	/*
	public static void main(String[] args)
	{
		try
		{
			EnglishSingleTreeParser parser = new MiniparClientParser("192.168.56.101");
			parser.init();
			parser.setSentence("It also names 40 kings of Kish spread over four dynasties.");
			parser.parse();
			EnglishNode root = parser.getParseTree();
			parser.cleanUp();
			
			BottomUpComputer<Info, EnglishNode> computer = new BottomUpComputer<Info, EnglishNode>(root);
			List<Set<EnglishNode>> ll = computer.getLeavesLevels();
			
			for (Set<EnglishNode> level : ll)
			{
				for (EnglishNode node : level)
				{
					System.out.print(node.getInfo().getNodeInfo().getWordLemma()+", ");
				}
				System.out.println();
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	*/
}
