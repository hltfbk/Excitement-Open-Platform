package eu.excitementproject.eop.common.datastructures.dgraph;

import eu.excitementproject.eop.common.datastructures.dgraph.view.DirectedGraphToDot;
import eu.excitementproject.eop.common.datastructures.dgraph.view.StringRepresentation;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;

public class DirectedGraphDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			DirectedGraph<Integer, String> graph = new DefaultDirectedGraph<Integer, String>();
			graph.addNode(1);
			graph.addNode(2);
			graph.addEdge(1, 2, "a");
			System.out.println(graph.getEdge(1, 2));
			
			graph.addNode(3);
			graph.addEdge(1, 3, "b");
			ImmutableSet<EdgeAndNode<Integer, String>> setSuc =  graph.getDirectSuccessorsOf(1);
			for (EdgeAndNode<Integer, String> edgeAndNode : setSuc)
			{
				System.out.println("1--\""+edgeAndNode.getEdgeInfo()+"\"-->"+edgeAndNode.getNode());
			}
			

			ImmutableSet<EdgeAndNode<Integer, String>> setSuc2 =  graph.getDirectSuccessorsOf(2);
			for (EdgeAndNode<Integer, String> edgeAndNode : setSuc2)
			{
				System.out.println("2--\""+edgeAndNode.getEdgeInfo()+"\"-->"+edgeAndNode.getNode());
			}
			
			System.out.println("Now adding 2,3,\"c\"------------------");
			
			graph.addEdge(2, 3, "c");
			
			DirectedGraphToDot<Integer, String> toDot = new DirectedGraphToDot<Integer, String>(
					graph,
					new StringRepresentation<Integer, String>() {

						public String getEdgeRepresentation(String e) {
							return e;
						}

						public String getNodeIdentifier(Integer n) {
							return n.toString();
						}

						public String getNodeRepresentation(Integer n) {
							return n.toString();
						}
						
					}
					, System.out);
			
			toDot.printDot();

			setSuc2 =  graph.getDirectSuccessorsOf(2);
			for (EdgeAndNode<Integer, String> edgeAndNode : setSuc2)
			{
				System.out.println("2--\""+edgeAndNode.getEdgeInfo()+"\"-->"+edgeAndNode.getNode());
			}
			
			System.out.println("----- now pred of 1, pred of 2, pred of 3");
			ImmutableSet<EdgeAndNode<Integer, String>> pred1 = graph.getDirectPredecessorsOf(1);
			for (EdgeAndNode<Integer, String> edgeAndNode : pred1)
			{
				System.out.println("1<---\""+edgeAndNode.getEdgeInfo()+"\"---"+edgeAndNode.getNode());
			}

			ImmutableSet<EdgeAndNode<Integer, String>> pred2 = graph.getDirectPredecessorsOf(2);
			for (EdgeAndNode<Integer, String> edgeAndNode : pred2)
			{
				System.out.println("2<---\""+edgeAndNode.getEdgeInfo()+"\"---"+edgeAndNode.getNode());
			}
			ImmutableSet<EdgeAndNode<Integer, String>> pred3 = graph.getDirectPredecessorsOf(3);
			for (EdgeAndNode<Integer, String> edgeAndNode : pred3)
			{
				System.out.println("3<---\""+edgeAndNode.getEdgeInfo()+"\"---"+edgeAndNode.getNode());
			}

			
			graph.removeEdge(1,3);
			System.out.println(" afer removing 1,3 - lets see successors of 2------------------");
			setSuc2 =  graph.getDirectSuccessorsOf(2);
			for (EdgeAndNode<Integer, String> edgeAndNode : setSuc2)
			{
				System.out.println("2--\""+edgeAndNode.getEdgeInfo()+"\"-->"+edgeAndNode.getNode());
			}
			System.out.println(" and successors of 1------------------");
			setSuc =  graph.getDirectSuccessorsOf(1);
			for (EdgeAndNode<Integer, String> edgeAndNode : setSuc)
			{
				System.out.println("1--\""+edgeAndNode.getEdgeInfo()+"\"-->"+edgeAndNode.getNode());
			}

			
			

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
