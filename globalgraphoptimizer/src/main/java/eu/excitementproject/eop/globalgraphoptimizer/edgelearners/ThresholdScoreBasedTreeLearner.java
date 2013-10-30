package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.util.HashMap;


import java.util.Iterator;
import java.util.Set;

import eu.excitementproject.eop.globalgraphoptimizer.alg.StrongConnectivityComponents;
import eu.excitementproject.eop.globalgraphoptimizer.alg.TransitiveClosureReduction;
import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.AbstractRuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.graph.DirectedOneMappingOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;
import eu.excitementproject.eop.globalgraphoptimizer.score.ScoreModel;

/**
 * @author Meni Adler
 * @since 24/08/2011
 *
 * Learn a simple entailment tree, composed of the edges which are assigned to a score which is higher than the given threshold
 */
public class ThresholdScoreBasedTreeLearner extends EfficientlyCorrectGreedyTreeLearner {

	public ThresholdScoreBasedTreeLearner(NodeGraph ioGraph, ScoreModel iLocalModel, double edgeCost) throws Exception {
		super(ioGraph, iLocalModel, edgeCost);
	}
	
	public ThresholdScoreBasedTreeLearner(double edgeCost) {
		super(edgeCost);
	}
	
	public void init(NodeGraph ioGraph, MapLocalScorer iLocalModel) throws Exception {
		super.init(ioGraph, iLocalModel);
	}
	
	public void learn() throws Exception {
		performIterativeProcedure();
	}
		
	
	public static DirectedOneMappingOntologyGraph createInitGraph(ScoreModel scoreModel, double graphEdgeScoreThreshold) throws Exception {
		HashMap<String, RelationNode> relationNodes = new HashMap<String,RelationNode>();
		DirectedOneMappingOntologyGraph graph = new DirectedOneMappingOntologyGraph();
		int id=0;
		for (Pair<String,String> pair : scoreModel.getEntailing()) {
			String template1 = pair.getFirst();
			String template2 = pair.getSecond();
			double score = scoreModel.getEntailmentScore(template1,template2);
			RelationNode node1 = relationNodes.get(template1);
			if (node1 == null) {
				node1 = new RelationNode(id,template1);
				relationNodes.put(template1,node1);
				id++;
			}
			RelationNode node2 = relationNodes.get(template2);
			if (node2 == null) {
				node2 = new RelationNode(id,template2);
				relationNodes.put(template2,node2);
				id++;
			}
			if (score >= graphEdgeScoreThreshold) {
				graph.addEdge(new RuleEdge(node1,node2,score));
			} else {
				graph.addNode(node1);
				graph.addNode(node2);
			}
		}
		
		System.out.println("Basic number of nodes: " + graph.getNodeCount());
		System.out.println("Basic number of edges: " + graph.getEdgeCount());
		
		graph.addTransitiveEdgesNotEfficiently();
		
		
		//get the skeleton scc graph
		DirectedOneMappingOntologyGraph  sccSkeletonTree = graph.generateSkeletonSccTreeFromTransitiveGraph();
		
		//add the transitive edges to the tree
		TransitiveClosureReduction.addTransitiveEdgesToAcyclicGraph(sccSkeletonTree);
		//get the new edges as pairs of ids
		Set<Pair<Integer,Integer>> newEdgeIds = StrongConnectivityComponents.generateEdgesFromSccGraph(sccSkeletonTree);
		
		Iterator<AbstractRuleEdge> edgeIter = graph.getEdges().iterator();
		while(edgeIter.hasNext()) {
			AbstractRuleEdge graphEdge = edgeIter.next();
			Pair<Integer,Integer> edgeIds = new Pair<Integer,Integer>(graphEdge.from().id(),graphEdge.to().id());
			if(!newEdgeIds.contains(edgeIds)) {
				System.out.println("REMOVING EDGE: " + graphEdge.from()+"-->"+graphEdge.to());
				edgeIter.remove();
				graphEdge.from().removeOutEdge(graphEdge);
				graphEdge.to().removeInEdge(graphEdge);
			}
		}
		
		DirectedOneMappingOntologyGraph rtfGraph = graph.reduceGraph();
		for(RelationNode node: rtfGraph.getNodes())
			if(node.outEdgesCount()>1)
				throw new OntologyException("node has more than one parent: " + node);
				

		System.out.println("Initial number of nodes: " + graph.getNodeCount());
		System.out.println("Initial number of edges: " + graph.getEdgeCount());

		return graph;
	}
	
}
