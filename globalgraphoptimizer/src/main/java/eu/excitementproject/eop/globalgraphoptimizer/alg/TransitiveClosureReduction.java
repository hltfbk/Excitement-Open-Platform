package eu.excitementproject.eop.globalgraphoptimizer.alg;

import java.util.HashMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.globalgraphoptimizer.graph.AbstractRuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.graph.DirectedOneMappingOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RuleEdge;

/**
 * Transitive Closure Reduction utilities
 * 
 * @author Jonathan Berant
 * 
 */
public class TransitiveClosureReduction {

	/**
	 * Takes a transitive graph and performs transitive reduction in cubic time. If the degree of the nodes
	 * is bounded by a constant then this is a linear time algorithm.
	 * 
	 */
	public static void performTransitiveReductionOnTransitiveGraph(DirectedOneMappingOntologyGraph graph) {

		for(RelationNode node: graph.getNodes()) {
			for(AbstractRuleEdge outEdge : node.outEdges()) {
				for(AbstractRuleEdge inEdge : node.inEdges()) {
					graph.removeEdge(new RuleEdge(inEdge.from(), outEdge.to(), 0));
				}
			}
		}
	}
	
	/**
	 * Takes an acyclic graph and adds all transitive closure edges
	 * @param graph 
	 */
	public static void addTransitiveEdgesToAcyclicGraph(DirectedOneMappingOntologyGraph graph) {

		HashMap<Integer,Set<Integer>> entailmentMap = new HashMap<Integer, Set<Integer>>();
		List<RelationNode> sortedNodeList = TopologicalSorter.sort(graph);

		for(int i = sortedNodeList.size()-1; i>=0; i--) {

			RelationNode currNode = sortedNodeList.get(i);
			entailmentMap.put(currNode.id(), new HashSet<Integer>());
			//now look at the target nodes in the graph and their lists
			for(AbstractRuleEdge outEdge: currNode.outEdges()) { 
				Integer toNodeId = outEdge.to().id();
				Set<Integer> sCurr = entailmentMap.get(currNode.id()); 
				sCurr.add(toNodeId); //add the node
				Set<Integer> sTo = entailmentMap.get(toNodeId);
				if (sTo != null)
					sCurr.addAll(sTo); // and those entailed by him
			}
		}

		for(Integer fromId: entailmentMap.keySet()) 
			for(Integer toId: entailmentMap.get(fromId))
				graph.addEdge(graph.getNode(fromId),graph.getNode(toId));
	}

}
