package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.util.LinkedList;

import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.AbstractOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.AbstractRuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;

/**
 * Learns edges by iteratively going through the edges from high to low according to edge-weight and inserting
 * and edge under the constraint that it creates a graph that is a Forest-Reducible-Graph
 * @author User
 *
 */

public class HighToLowTreeLearner extends HighToLowEdgeLearner{

	private static Logger logger = Logger.getLogger(HighToLowEdgeLearner.class);

	public HighToLowTreeLearner(NodeGraph ioGraph, MapLocalScorer iLocalModel,double edgeCost) throws Exception {
		super(ioGraph, iLocalModel,edgeCost);
	}

	public HighToLowTreeLearner(double edgeCost) {
		super(edgeCost);
	}
	
	public void init(NodeGraph ioGraph, MapLocalScorer iLocalModel) throws Exception {
		super.init(ioGraph, iLocalModel);
	}
	
	@Override
	/**
	 * Note that this is exactly the same as {@link HighToLowIterativeLearner}, except that before
	 * working on an edge we check if adding it will result in an FRG or not
	 */
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

				Pair<RelationNode,RelationNode> nodePair = currCandidates.iterator().next();
				//check if inserting the edge is legal, i.e. does not violate the FRG condition
				if(frgPermissibleEdge(nodePair)) {

					ImplicitSetProb implicitSetProb = calcImplicitSetProb(nodePair);

					if(frgPermissibleEdge(nodePair) && implicitSetProb.getProb()>0) {
						//add all of the implicit set
						for(Pair<RelationNode,RelationNode> edgeToAdd: implicitSetProb.getImplicitSet()) {			
							double score = m_localModel.getEntailmentScore(edgeToAdd.getFirst(), edgeToAdd.getSecond());
							m_nodeGraph.getGraph().addEdge(new RuleEdge(edgeToAdd.getFirst(),edgeToAdd.getSecond(),score));
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
				}
				else {
					currCandidates.remove(nodePair);
					nextIterationCandidates.add(nodePair);
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

	/**
	 * checking if a candidate edge maintains the constraint that the graph is an FRG
	 * @param nodePair
	 * @return
	 */
	private boolean frgPermissibleEdge(Pair<RelationNode, RelationNode> nodePair) {

		AbstractOntologyGraph graph = m_nodeGraph.getGraph();
		RelationNode fromNode = nodePair.getFirst();
		for(AbstractRuleEdge edge: fromNode.outEdges()) {
			
			RelationNode fromNodeParent = edge.to();
			//if no edge from current parent to the candidate "to" and vice versa - not permissible
			//ADDED: and no edge from parent to current node
			if(!(graph.containsEdge(fromNodeParent.id()+RuleEdge.SYMBOL+nodePair.getSecond().id()) ||
					graph.containsEdge(nodePair.getSecond().id()+RuleEdge.SYMBOL+fromNodeParent.id()) ||
					graph.containsEdge(fromNodeParent.id()+RuleEdge.SYMBOL+nodePair.getFirst().id()) )) {
				return false;
			}
		}
		return true;
	}
}
