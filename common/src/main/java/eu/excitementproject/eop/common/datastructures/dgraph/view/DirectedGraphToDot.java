package eu.excitementproject.eop.common.datastructures.dgraph.view;

import java.io.PrintStream;
import java.util.HashSet;

import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraph;
import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraphException;
import eu.excitementproject.eop.common.datastructures.dgraph.EdgeAndNode;
import eu.excitementproject.eop.common.datastructures.dgraph.HeadAndTail;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.StringUtilException;



/**
 * Used to create a ".dot" file that represents a {@link DirectedGraph}.
 * <P>
 * Dot is the file format used by GraphViz software package
 * (see: www.graphviz.org).
 * GraphViz is a software package to draw graphs (directed and
 * undirected).
 * 
 * @author Asher Stern
 *
 * @param <N>
 * @param <E>
 */
public class DirectedGraphToDot<N,E>
{
	public DirectedGraphToDot(DirectedGraph<N, E> graph, StringRepresentation<N, E> representation, PrintStream stream)
	{
		this.graph = graph;
		this.representation = representation;
		this.stream = stream;
	}
	
	public void setGraphLabel(String graphLabel)
	{
		this.graphLabel = graphLabel;
	}
	
	protected void printHeader()
	{
		stream.println("digraph G{");
		if (this.graphLabel!=null)
		{
			try {
				stream.println("graph [label=\""+StringUtil.convertStringToCString(this.graphLabel)+"\"];");
			} catch (StringUtilException e) {}
			
		}
	}
	protected void printFooter()
	{
		stream.println("}");
	}
	protected void printContent() throws DirectedGraphException
	{
		for (N node : graph.getAllNodes())
		{
			try {
				stream.println(representation.getNodeIdentifier(node)+" [label=\""+StringUtil.convertStringToCString(representation.getNodeRepresentation(node))+"\"];");
			} catch (StringUtilException e) {
				throw new DirectedGraphException("error in StringUtil.convertStringToCString", e );
			}
		}
		
		HashSet<HeadAndTail<N>> alreadyPrinted = new HashSet<HeadAndTail<N>>();
		for (N node : graph.getAllNodes())
		{
			ImmutableSet<EdgeAndNode<N, E>> edges = graph.getDirectSuccessorsOf(node);
			for (EdgeAndNode<N, E> edge : edges)
			{
				HeadAndTail<N> headAndTail = new HeadAndTail<N>(node, edge.getNode());
				if (alreadyPrinted.contains(headAndTail)) ;
				else
				{
					alreadyPrinted.add(headAndTail);
					try {
						stream.println(representation.getNodeIdentifier(node)+"->"+representation.getNodeIdentifier(edge.getNode())+"[label=\""+StringUtil.convertStringToCString(representation.getEdgeRepresentation(edge.getEdgeInfo()))+"\"];");
					} catch (StringUtilException e) {
						throw new DirectedGraphException("error in StringUtil.convertStringToCString", e );
					}
				}
			}
			
			
		}
		
	}
	
	public void printDot() throws DirectedGraphException
	{
		printHeader();
		printContent();
		printFooter();
	}
	
	
	
	
	
	private DirectedGraph<N, E> graph;
	private StringRepresentation<N, E> representation;
	private PrintStream stream;
	private String graphLabel = null;

}
