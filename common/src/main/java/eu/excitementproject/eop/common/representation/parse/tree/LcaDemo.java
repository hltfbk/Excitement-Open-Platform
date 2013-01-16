package eu.excitementproject.eop.common.representation.parse.tree;

//
//import java.io.File;
//import java.util.Map;
//import java.util.Set;
//
//import eu.excitementproject.eop.common.representation.parse.EnglishSingleTreeParser;
//import eu.excitementproject.eop.common.representation.parse.minipar.MiniparClientParser;
//import eu.excitementproject.eop.common.representation.parse.minipar.MiniparParser;
//import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
//import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
//import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.SimpleNodeString;
//import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator;
//import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.WordOnlyNodeString;
//


public class LcaDemo
{
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args)
//	{
//		try
//		{
//			if (args.length<1) throw new Exception("args. should be minipar data dir");
//			
//			String miniparArgument = args[0];
//			File miniparDataDir = new File(miniparArgument);
//			boolean isLocal = false;
//			if (miniparDataDir.exists()) if (miniparDataDir.isDirectory()) isLocal = true;
//			
//			EnglishSingleTreeParser parser = null;
//			if (isLocal)
//			{
//				parser = new MiniparParser(miniparArgument);
//			}
//			else
//			{
//				parser = new MiniparClientParser(miniparArgument);
//			}
//			parser.init();
//			try
//			{
//				parser.setSentence("If you could say that we hope to hear him, it would be nice.");
//				parser.parse();
//				BasicNode root = parser.getParseTree();
//				
//				Map<BasicNode, Integer> depthMap = AbstractNodeUtils.depthMap(root);
//				for (BasicNode nodeInMap : depthMap.keySet())
//				{
//					try
//					{
//						System.out.println(nodeInMap.getInfo().getNodeInfo().getWordLemma()+": "+depthMap.get(nodeInMap));
//					}
//					catch(NullPointerException e)
//					{}
//				}
//				
//				
//				TreeStringGenerator<Info> tsg = new TreeStringGenerator<Info>(new SimpleNodeString(), root);
//				System.out.println(tsg.generateString());
//				tsg = new TreeStringGenerator<Info>(new WordOnlyNodeString(), root);
//				System.out.println(tsg.generateString());
//				Map<BasicNode,BasicNode> mapParent = AbstractNodeUtils.parentMap(root);
//				for (AbstractNode<Info,BasicNode> node : mapParent.keySet())
//				{
//					try
//					{
//						System.out.println("parent of "+node.getInfo().getNodeInfo().getWord()+" is "+mapParent.get(node).getInfo().getNodeInfo().getWord());
//					}
//					catch(NullPointerException e)
//					{
//						System.out.println(":-(");
//						
//					}
//				}
//				
//				LeastCommonAncestor<Info, BasicNode> lca = new LeastCommonAncestor<Info, BasicNode>(root);
//				lca.compute();
//				Set<BasicNode> setNodes = AbstractNodeUtils.treeToSet(root);
//				for (BasicNode node1 : setNodes)
//				{
//					for (BasicNode node2 : setNodes)
//					{
//						try
//						{
//							System.out.println("lca of: "+node1.getInfo().getNodeInfo().getWord()+" and "+node2.getInfo().getNodeInfo().getWord()+" is "+
//									lca.getLeastCommonAncestorOf(node1, node2).getInfo().getNodeInfo().getWord()
//							);
//						}
//						catch(NullPointerException e)
//						{
//							System.out.println("lca :-(");
//						}
//
//						try
//						{
//							System.out.println("lca of: "+node1.getInfo().getId()+" and "+node2.getInfo().getId()+" is "+
//									lca.getLeastCommonAncestorOf(node1, node2).getInfo().getId()
//							);
//						}
//						catch(NullPointerException e)
//						{
//							System.out.println("lca :-(");
//						}
//
//
//					}
//				}
//				
//				
//				
//				
//				
//				
//				
//				
//			}
//			finally
//			{
//				parser.cleanUp();
//			}
//			
//			
//			
//			
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//
//
//	}
//
}

