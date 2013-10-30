package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.util.Iterator;

import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.globalgraphoptimizer.alg.StrongConnectivityComponents;
import eu.excitementproject.eop.globalgraphoptimizer.alg.TransitiveClosureReduction;
import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.AbstractRuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.graph.DirectedOneMappingOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;
import eu.excitementproject.eop.globalgraphoptimizer.score.ScoreModel;

public class GreedyScoreTreeLearner extends GreedyScoreEdgeLearner {

	private Logger logger = Logger.getLogger(GreedyScoreTreeLearner.class);
	
	public GreedyScoreTreeLearner(NodeGraph ioGraph, ScoreModel iLocalModel, double edgeCost) throws Exception {
		super(ioGraph, iLocalModel,edgeCost);
	}
	
	public GreedyScoreTreeLearner(double edgeCost) {
		super(edgeCost);
	}
	
	public void init(NodeGraph ioGraph, MapLocalScorer iLocalModel) throws Exception {
		super.init(ioGraph, iLocalModel);
	}
	
	public void learn() throws Exception {
		super.learn();
		
		//The caller needs to know this algorithm is for Directed one mapping ontology graphs
		DirectedOneMappingOntologyGraph graph = 
			(DirectedOneMappingOntologyGraph) m_nodeGraph.getGraph();
		
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
				logger.info("REMOVING EDGE: " + graphEdge.from()+"-->"+graphEdge.to());
				edgeIter.remove();
				graphEdge.from().removeOutEdge(graphEdge);
				graphEdge.to().removeInEdge(graphEdge);
			}
		}
	}

}
