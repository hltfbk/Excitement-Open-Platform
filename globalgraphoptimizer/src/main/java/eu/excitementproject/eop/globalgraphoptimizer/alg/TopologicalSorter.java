package eu.excitementproject.eop.globalgraphoptimizer.alg;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import eu.excitementproject.eop.globalgraphoptimizer.graph.AbstractRuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.graph.DirectedOneMappingOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;


//http://en.wikipedia.org/wiki/Topological_sorting
public class TopologicalSorter {
	
	/**
	 * 
	 * @param OntoGraph - we assume it's a DAG
	 * @return a sorted list 
	 */
	public static List<RelationNode> sort(DirectedOneMappingOntologyGraph iOntoGraph) {
	
		List<RelationNode> sortedList = new ArrayList<RelationNode>();
		Queue<RelationNode> queue = new LinkedList<RelationNode>();
		//initialize the set
		for(RelationNode node: iOntoGraph.getNodes()) {
			if(node.inEdgesCount()==0)
				queue.offer(node);
		}
		
		while(!queue.isEmpty()) {
			
			RelationNode currNode = queue.poll();
			sortedList.add(currNode);
			
			for(AbstractRuleEdge edge: currNode.outEdges()) {
				
				RelationNode toNode = edge.to();
				Integer deletedInEdges = (Integer)toNode.attr("delEdges"); 
				if(deletedInEdges==null)
					toNode.setAttr("delEdges", new Integer(1));
				else
					toNode.setAttr("delEdges", new Integer(deletedInEdges+1));
				if(((Integer) toNode.attr("delEdges")).intValue()==toNode.inEdgesCount())
					queue.offer(toNode);
			}
			
		}
		
		//clear the labels I used for the sorting
		for(RelationNode node: iOntoGraph.getNodes())
			node.deleteAttr("delEdges");
		
		return sortedList;
	}

}
