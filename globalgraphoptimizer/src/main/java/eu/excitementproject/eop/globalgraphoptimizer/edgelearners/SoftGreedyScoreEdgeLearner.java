package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.util.HashMap;

import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.score.LocalScoreModel;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;

/**
 * 
 * @author Jonathan
 * @since 12/4/11
 * Same as {@link GreedyScoreEdgeLearner} only for hard positive and negative constraints very high and low 
 * probabilities respectively are given, but the constraint is not completely hard.
 * @see GreedyScoreEdgeLearner
 *
 */
public class SoftGreedyScoreEdgeLearner implements EdgeLearner {

	protected class ImplicitSetProb {

		public ImplicitSetProb(Set<Pair<RelationNode, RelationNode>> implicitSet,
				double prob,Set<RelationNode> reaching, Set<RelationNode> reachable) {
			m_implicitSet = implicitSet;
			m_prob = prob;
			m_reaching=reaching;
			m_reachable=reachable;
		}


		public Set<Pair<RelationNode, RelationNode>> getImplicitSet() {
			return m_implicitSet;
		}

		public double getProb() {
			return m_prob;
		}

		public Set<RelationNode> getReachableNodes() {
			return m_reachable;
		}

		public Set<RelationNode> getReachingNodes() {
			return m_reaching;
		}
		
		private Set<Pair<RelationNode,RelationNode>> m_implicitSet;
		private double m_prob;
		private Set<RelationNode> m_reaching;
		private Set<RelationNode> m_reachable;
	}

	public SoftGreedyScoreEdgeLearner(NodeGraph ioGraph, LocalScoreModel iLocalModel, double edgeCost) throws Exception {
		m_nodeGraph = ioGraph;
		m_localModel = iLocalModel;
		m_edgeCost = edgeCost;
		init1();
	}
	
	public SoftGreedyScoreEdgeLearner(double edgeCost) {		
		m_edgeCost = edgeCost;
		m_nodeGraph = null;
		m_edgeCandidates = null;
		m_reachableNodes = null;
		m_reachingNodes = null;
		m_localModel = null;
	}
	
	public void init(NodeGraph ioGraph, MapLocalScorer iLocalModel) throws Exception {
		m_nodeGraph = ioGraph;
		m_localModel = iLocalModel;
		init1();
	}
	
	public void init1() throws Exception {
		m_reachableNodes = new HashMap<RelationNode, Set<RelationNode>>();
		m_reachingNodes = new HashMap<RelationNode, Set<RelationNode>>();
		m_edgeCandidates = new HashSet<Pair<RelationNode,RelationNode>>();

		for(RelationNode node:m_nodeGraph.getGraph().getNodes()) {
			Set<RelationNode> reachableNodes = new HashSet<RelationNode>();
			Set<RelationNode> reachingNodes = new HashSet<RelationNode>();
			reachableNodes.add(node);
			reachingNodes.add(node);
			m_reachableNodes.put(node, reachableNodes);
			m_reachingNodes.put(node,reachingNodes);
		}
	}

	protected double multipleRelationsMultiplicativeChange(Set<Pair<RelationNode, RelationNode>> edgeCandidates)
			throws Exception {
		
		double score=0;	
		for(Pair<RelationNode,RelationNode> edgeCandidate: edgeCandidates) 
			score+=singleRelationMultiplicativeChange(edgeCandidate);
		return score;
	}

	public boolean toStop(double score) {
		//EL.info("OBJECTIVE FUNCTION VALUE: " + (m_nodeGraph.getGraph().sumOfEdgeWeights()-m_edgeCost*m_nodeGraph.getGraph().getEdgeCount()));
		if(score>0)
			return false;
		return true;
	}
	
	private double singleRelationMultiplicativeChange(Pair<RelationNode,RelationNode> iEdgeCandidate) throws Exception {
		return m_localModel.getEntailmentScore(iEdgeCandidate.getFirst(),iEdgeCandidate.getSecond())-m_edgeCost;
	}
	
	public double getObjectiveFunctionValue() {
		return m_nodeGraph.getGraph().sumOfEdgeWeights()-m_edgeCost*m_nodeGraph.getGraph().getEdgeCount();
	}
	public void learn() throws Exception {

		m_nodeGraph.getGraph().clearEdges(); //we delete all original edges

		initEdgeCandidates();

		while(!m_edgeCandidates.isEmpty()) {

			ImplicitSetProb bestImplicitSetProb=null;
			//find the max and argmax
			for(Pair<RelationNode,RelationNode> edgeCandidate:m_edgeCandidates) {
				ImplicitSetProb currImplicitSetProb = calcImplicitSetProb(edgeCandidate);
				if(bestImplicitSetProb==null || currImplicitSetProb.getProb()>bestImplicitSetProb.getProb()) 
					bestImplicitSetProb=currImplicitSetProb;
			}

			if(toStop(bestImplicitSetProb.getProb()))
				break;
			else {
				//add all of the implicit set
				for(Pair<RelationNode,RelationNode> edgeToAdd: bestImplicitSetProb.getImplicitSet()) {
					RelationNode fromNode = edgeToAdd.getFirst();
					RelationNode toNode = edgeToAdd.getSecond();
					m_nodeGraph.getGraph().addEdge(new RuleEdge(fromNode,toNode,m_localModel.getEntailmentScore(fromNode, toNode)));
				}
				//update reachable and reaching nodes
				for(RelationNode node: bestImplicitSetProb.getReachingNodes())
					m_reachableNodes.get(node).addAll(bestImplicitSetProb.getReachableNodes());
				for(RelationNode node: bestImplicitSetProb.getReachableNodes())
					m_reachingNodes.get(node).addAll(bestImplicitSetProb.getReachingNodes());
				//remove from the list of candidates					
				m_edgeCandidates.removeAll(bestImplicitSetProb.getImplicitSet());
			}
		}
	}
	
	protected void initEdgeCandidates() {

		for(RelationNode node1: m_nodeGraph.getGraph().getNodes()) {
			for(RelationNode node2:m_nodeGraph.getGraph().getNodes()) {
				if(node1!=node2)
					m_edgeCandidates.add(new Pair<RelationNode,RelationNode>(node1,node2));
			}
		}
	}

	protected ImplicitSetProb calcImplicitSetProb(Pair<RelationNode, RelationNode> edgeCandidate) throws Exception {

		Set<Pair<RelationNode,RelationNode>> implicitSet = calcImplicitSet(edgeCandidate);
		double setProb;
		
		setProb = multipleRelationsMultiplicativeChange(implicitSet);
		return new ImplicitSetProb(implicitSet,setProb,m_reachingNodes.get(edgeCandidate.getFirst()),m_reachableNodes.get(edgeCandidate.getSecond()));
	}

	protected Set<Pair<RelationNode,RelationNode>> calcImplicitSet(Pair<RelationNode, RelationNode> edgeCandidate) {

		RelationNode fromNode = edgeCandidate.getFirst();
		RelationNode toNode = edgeCandidate.getSecond();
		Set<Pair<RelationNode,RelationNode>> implicitSet = new HashSet<Pair<RelationNode,RelationNode>>();

		//add implicit edges due to reachability from the toNode and reachability to the fromNode
		for(RelationNode implicitFromNode:m_reachingNodes.get(fromNode)) {
			for(RelationNode implicitToNode:m_reachableNodes.get(toNode)) {		
				Pair<RelationNode,RelationNode> currImplicitEdgeCandidate =
					new Pair<RelationNode, RelationNode>(implicitFromNode,implicitToNode);
				if(m_edgeCandidates.contains(currImplicitEdgeCandidate))
					implicitSet.add(currImplicitEdgeCandidate);
			}
		}	
		return implicitSet;	
	}
	
	NodeGraph m_nodeGraph;
	Set<Pair<RelationNode,RelationNode>> m_edgeCandidates;
	HashMap<RelationNode,Set<RelationNode>> m_reachableNodes;
	HashMap<RelationNode,Set<RelationNode>> m_reachingNodes;
	LocalScoreModel m_localModel;
	protected double m_edgeCost;
}
