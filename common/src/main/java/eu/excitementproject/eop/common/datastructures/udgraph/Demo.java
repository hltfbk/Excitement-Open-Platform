package eu.excitementproject.eop.common.datastructures.udgraph;

import eu.excitementproject.eop.common.datastructures.Pair;


/**
 * Demonstrates the undirected graph.
 * @author Asher Stern
 * @since Aug 17, 2010
 *
 */
public class Demo
{

	private static class DemoNode
	{
		public DemoNode(int i){this.i = i;}
		public int i;
	}
	
	public void f() throws UndirectedGraphException
	{
		UndirectedGraph<DemoNode, String> graph = new MapBasedGraph<DemoNode, String>();
		
		DemoNode node1 = new DemoNode(3);
		DemoNode node2 = new DemoNode(4);
		DemoNode node3 = new DemoNode(4);
		graph.addNode(node1);
		graph.addNode(node2);
		graph.addNode(node3);
		
		graph.addEdge(new Pair<DemoNode>(node1, node2), "hello world");
		
		String str = graph.getEdgeContents(new Pair<DemoNode>(node1, node2));
		
		System.out.println(str);
		
		
		graph.addEdge(new Pair<DemoNode>(node1, node1),"illogical");
		String str2 = graph.getEdgeContents(new Pair<DemoNode>(node1, node1));
		System.out.println(str2);
		
		for (DemoNode ne : graph.getNeighbors(node1))
		{
			System.out.println(ne.i);
		}
		
		System.out.println("graph2");
		
		UndirectedGraph<Integer, String> graph2 = new MapBasedGraph<Integer, String>();
		graph2.addNode(3);
		graph2.addNode(4);
		graph2.addNode(5);
		System.out.println(graph2.getNeighbors(3).size());
		graph2.addEdge(new Pair<Integer>(3, 5), "yes");
		System.out.println(graph2.getEdgeContents(new Pair<Integer>(3, 5)));
		graph2.removeEdge(new Pair<Integer>(5, 3));
		//System.out.println(graph2.getEdgeContents(new Pair<Integer>(3, 5)));
		
		
		
		
		
	}
	
	
	
	public static void main(String[] args)
	{
		try
		{
			new Demo().f();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
