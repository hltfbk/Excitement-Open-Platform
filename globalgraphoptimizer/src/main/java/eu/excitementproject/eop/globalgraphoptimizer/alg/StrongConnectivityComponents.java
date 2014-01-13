package eu.excitementproject.eop.globalgraphoptimizer.alg;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.AbstractRuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.graph.DirectedOneMappingOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;
import eu.excitementproject.eop.globalgraphoptimizer.graph.ReversedRuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RuleEdge;


//http://www.ics.uci.edu/~eppstein/161/960220.html
public class StrongConnectivityComponents {
	
	public StrongConnectivityComponents() throws IOException {
		m_components = new LinkedList<List<Integer>>();
		m_currComponent = new LinkedList<Integer>();
	}

	public List<List<Integer>> DFSandSCC(DirectedOneMappingOntologyGraph g) throws Exception {
		
		try {
			RelationNode pseudoNode = addPseudoNode(g);
			
			m_counter = 0;
			m_nodeList = new LinkedList<RelationNode>();
			m_ontoTree = new DirectedOneMappingOntologyGraph();
			m_ontoTree.addNode(pseudoNode);
	
			visit(pseudoNode);
			g.removeNode(PSEUDO_NODE_ID);
		}
		finally {
			clearAttr(g);
		}
		return m_components;
	}
	
	private void clearAttr(DirectedOneMappingOntologyGraph g) {
		for(RelationNode node: g.getNodes()) {
			node.deleteAttr("dfs");
			node.deleteAttr("low");
			node.deleteAttr("deleted");
		}
	}

	private RelationNode addPseudoNode(DirectedOneMappingOntologyGraph g) throws Exception {

		RelationNode pseudoNode = new RelationNode(PSEUDO_NODE_ID,"pseudo");
		g.addNode(pseudoNode);
		for(RelationNode node: g.getNodes()) {
			if(node.equals(pseudoNode))
				continue;
			RuleEdge edge=new RuleEdge(pseudoNode,node,0);
			g.addEdge(edge);
		}
		return pseudoNode;
	}
	
/*	private boolean isPseudoNode(RelationNode node) {
		return node.id()==PSEUDO_NODE_ID;
	}
	*/
	
	private void visit(RelationNode fromNode) throws Exception {

		m_nodeList.add(fromNode);
		fromNode.setAttr("dfs", new Integer(m_counter));
		m_counter++;
		fromNode.setAttr("low", fromNode.attr("dfs"));
				
		for(AbstractRuleEdge edge: fromNode.outEdges()) {
			
			if(edge instanceof ReversedRuleEdge)
				throw new Exception("The method does not work for graphs with reversed edges");
						
			RelationNode toNode = edge.to();
			if(toNode.attr("deleted")!=null)
				continue;
			if(m_ontoTree.getNode(toNode.id())==null) {
				m_ontoTree.addEdge(edge);
				visit(toNode);
				fromNode.setAttr("low",Math.min((Integer)fromNode.attr("low"), (Integer)toNode.attr("low")));
			}
			else fromNode.setAttr("low",Math.min((Integer)fromNode.attr("low"), (Integer)toNode.attr("dfs"))); 			
		}
		if(fromNode.attr("dfs").equals(fromNode.attr("low"))) {
			while(true) {
				RelationNode lastNode=m_nodeList.remove(m_nodeList.size()-1);
				lastNode.setAttr("deleted", new Boolean(true));				
				m_currComponent.add(lastNode.id());
				
				//TODO: remove the last node from the graph? is that necessary? 
				if(lastNode.equals(fromNode))
					break;
			} 
			m_components.add(m_currComponent);
			m_currComponent = new LinkedList<Integer>();
		}
	}
	
	public static Set<Pair<Integer,Integer>> generateEdgesFromSccGraph(DirectedOneMappingOntologyGraph graph) {
	
		Set<Pair<Integer,Integer>> result = new HashSet<Pair<Integer,Integer>>();
		
		//for every node add all the clique edges
		for(RelationNode currNode: graph.getNodes()) {
			
			//create a list with the ids in the current node
			List<Integer> currNodeIds = new LinkedList<Integer>();
			String[] currNodeIdDescs = currNode.description().split(",");
			
			for(String currNodeIdDesc : currNodeIdDescs) {
				currNodeIds.add(Integer.parseInt(currNodeIdDesc));
			}
			
			//for each one add an entry in the table entailing all of the others
			for(Integer fromId: currNodeIds) {
				for(Integer toId: currNodeIds) {
					if(!fromId.equals(toId))
						result.add(new Pair<Integer, Integer>(fromId,toId));
				}
			}
		}
		//for every edge add the cartesian product
		for(AbstractRuleEdge currEdge: graph.getEdges()) {
			
			//the cast is safe since the input is a directed one mapping ontology graph
			RuleEdge currRuleEdge = (RuleEdge) currEdge;
			List<Integer> fromNodeIds = new LinkedList<Integer>();
			List<Integer> toNodeIds = new LinkedList<Integer>();
			
			String[] fromNodeIdDescs = currRuleEdge.from().description().split(",");
			String[] toNodeIdDescs = currRuleEdge.to().description().split(",");
			
			for(String fromNodeIdDesc : fromNodeIdDescs)
				fromNodeIds.add(Integer.parseInt(fromNodeIdDesc));
			
			for(String toNodeidDesc : toNodeIdDescs)
				toNodeIds.add(Integer.parseInt(toNodeidDesc));
			
			for(Integer fromNodeId : fromNodeIds) {
				for(Integer toNodeId : toNodeIds) {
					result.add(new Pair<Integer, Integer>(fromNodeId,toNodeId));
				}
			}
		}
		return result;
	}

	private int m_counter;
	private List<RelationNode> m_nodeList; 
	private DirectedOneMappingOntologyGraph m_ontoTree;
	private List<List<Integer>> m_components;
	private List<Integer> m_currComponent;
	private static final int PSEUDO_NODE_ID = -1;
}
