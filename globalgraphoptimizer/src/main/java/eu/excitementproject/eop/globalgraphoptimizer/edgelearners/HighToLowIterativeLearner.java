package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.util.LinkedList;

import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;

public class HighToLowIterativeLearner extends HighToLowEdgeLearner {

	private Logger logger = Logger.getLogger(HighToLowIterativeLearner.class);
	
	public HighToLowIterativeLearner(NodeGraph ioGraph, MapLocalScorer iLocalModel, double edgeCost) throws Exception {
		super(ioGraph, iLocalModel,edgeCost);
	}
	
	public HighToLowIterativeLearner(double edgeCost) {
		super(edgeCost);
	}
	
	public void init(NodeGraph ioGraph, MapLocalScorer iLocalModel) throws Exception {
		super.init(ioGraph, iLocalModel);
	}
	
	@Override
	public void learn() throws Exception {

		m_nodeGraph.getGraph().clearEdges(); //we delete all original edges
		List<Pair<RelationNode,RelationNode>> nextIterationCandidates = new LinkedList<Pair<RelationNode,RelationNode>>();
		List<Pair<RelationNode,RelationNode>> currCandidates = m_sortedEdgeCandidates;
		
		boolean changed = true;
		int i = 0;
		while(changed) {
			logger.info("Iteration: " + ++i);
			changed = false;
			while(!currCandidates.isEmpty()) {
				int j = 0;
				Pair<RelationNode,RelationNode> nodePair = currCandidates.iterator().next();
				ImplicitSetProb implicitSetProb = calcImplicitSetProb(nodePair);
				
				if(implicitSetProb.getProb()>0) {
					//add all of the implicit set
					for(Pair<RelationNode,RelationNode> edgeToAdd: implicitSetProb.getImplicitSet()) {
						RelationNode fromNode = edgeToAdd.getFirst();
						RelationNode toNode = edgeToAdd.getSecond();
						double score = m_localModel.getEntailmentScore(fromNode, toNode);
						m_nodeGraph.getGraph().addEdge(new RuleEdge(fromNode,toNode,score));
						changed=true;
					}
					//update reachable and reaching nodes
					for(RelationNode node: implicitSetProb.getReachingNodes())
						m_reachableNodes.get(node).addAll(implicitSetProb.getReachableNodes());
					for(RelationNode node: implicitSetProb.getReachableNodes())
						m_reachingNodes.get(node).addAll(implicitSetProb.getReachingNodes());
					//remove from the list of candidates and add to the list of handled nodes	
					currCandidates.removeAll(implicitSetProb.getImplicitSet());
				}
				else {
					currCandidates.remove(nodePair);
					nextIterationCandidates.add(nodePair);
				}	
				j++;
				if(j % 1000000 == 0) {
					System.gc();
					Runtime runtime = Runtime.getRuntime();
					long memory = runtime.totalMemory() - runtime.freeMemory();
					logger.info("Used memory is megabytes: "
							+ (memory / (1024L * 1024L)));
					
				}
			}
			currCandidates = nextIterationCandidates;
			nextIterationCandidates = new LinkedList<Pair<RelationNode,RelationNode>>();
			double currentObjValue = m_nodeGraph.getGraph().sumOfEdgeWeights()-m_edgeCost*m_nodeGraph.getGraph().getEdgeCount();
			logger.warn("OBJECTIVE-FUNCTION-VALUE: " + currentObjValue);	
		}

		//clean all the reachable and reaching nodes to save memory
		m_reachableNodes.clear();
		m_reachingNodes.clear();

	}
}
