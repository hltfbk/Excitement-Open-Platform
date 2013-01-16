package eu.excitementproject.eop.common.datastructures.udgraph.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.Pair;
import eu.excitementproject.eop.common.datastructures.udgraph.UndirectedGraph;
import eu.excitementproject.eop.common.datastructures.udgraph.UndirectedGraphException;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.StringUtilException;


public class UndirectedGraphDotGenerator<N, E>
{
	
	// exception class
	@SuppressWarnings("serial")
	public static class UndirectedGraphDotGeneratorException extends Exception
	{public UndirectedGraphDotGeneratorException(String str){super(str);}}

	
	public UndirectedGraphDotGenerator(UndirectedGraph<N, E> graph,
			GraphNodeAndEdgeString<N, E> nodeAndEdgeString) throws UndirectedGraphDotGeneratorException
	{
		super();
		if (null==graph) throw new UndirectedGraphDotGeneratorException("null==graph");
		if (null==nodeAndEdgeString) throw new UndirectedGraphDotGeneratorException("null==nodeAndEdgeString");
		this.graph = graph;
		this.nodeAndEdgeString = nodeAndEdgeString;
	}

	public void generate() throws UndirectedGraphException
	{
		buffer.setLength(0);
		buffer.append("graph\n");
		buffer.append("{\n");
		addContents();
		buffer.append("}\n");
		
		done = true;
	}
	
	public String getGeneratedDot() throws UndirectedGraphDotGeneratorException
	{
		if (!done) throw new UndirectedGraphDotGeneratorException("Not done yet. You did not call generate()!");
		return buffer.toString();
	}
	
	protected void addContents() throws UndirectedGraphException
	{
		Map<N, String> mapNodesIds = new HashMap<N, String>();
		int index=1;
		for (N node : graph.getNodes())
		{
			String nodeId = "n"+String.valueOf(index);
			mapNodesIds.put(node,nodeId);
			String label;
			try {
				label = StringUtil.convertStringToCString(nodeAndEdgeString.getNodeString(node));
			} catch (StringUtilException e) {
				throw new UndirectedGraphException("error in StringUtil.convertStringToCString() with " + nodeAndEdgeString.getNodeString(node), e);
			}
			buffer.append(nodeId);
			buffer.append("[label=\"");
			buffer.append(label);
			buffer.append("\"];");
			buffer.append("\n");
			
			index++;
		}
		
		Set<Pair<N>> already = new HashSet<Pair<N>>();
		for (N node : graph.getNodes())
		{
			for (N neighbor : graph.getNeighbors(node))
			{
				Pair<N> pair = new Pair<N>(node, neighbor);
				if (!already.contains(pair))
				{
					String edgeLabel;
					try {
						edgeLabel = StringUtil.convertStringToCString(nodeAndEdgeString.getEdgeString(graph.getEdgeContents(pair)));
					} catch (StringUtilException e) {
						throw new UndirectedGraphException("error in StringUtil.convertStringToCString() with " + graph.getEdgeContents(pair), e);
					}
					 
					buffer.append(mapNodesIds.get(node));
					buffer.append(" -- ");
					buffer.append(mapNodesIds.get(neighbor));
					buffer.append(" [label=\"");
					buffer.append(edgeLabel);
					buffer.append("\"];\n");
					already.add(pair);
				}
			}
		}
	}
	
	protected UndirectedGraph<N, E> graph;
	protected GraphNodeAndEdgeString<N, E> nodeAndEdgeString;
	protected StringBuffer buffer = new StringBuffer();
	private boolean done = false;
}
