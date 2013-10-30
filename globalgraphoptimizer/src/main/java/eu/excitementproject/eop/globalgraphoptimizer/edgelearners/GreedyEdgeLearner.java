package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.util.HashMap;

import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;
import eu.excitementproject.eop.globalgraphoptimizer.score.ScoreModel;

public abstract class GreedyEdgeLearner implements EdgeLearner {

	public enum EdgeType {
		DIRECT,
		REVERSED;
	}
	
	private class ImplicitSetProb {

		public ImplicitSetProb(Set<Pair<RelationNode, RelationNode>> implicitSet,
				double prob,Set<RelationNode> reaching, Set<RelationNode> reachable, boolean containsEntailing, boolean containsNonEntailing) {
			m_implicitSet = implicitSet;
			m_prob = prob;
			m_reaching=reaching;
			m_reachable=reachable;
			m_containsEntailing = containsEntailing;
			m_containsNonEntailing = containsNonEntailing;
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
		
		public boolean containsEntailing() {
			return m_containsEntailing;
		}
		
		@SuppressWarnings("unused")
		public boolean containsNonEntailing() {
			return m_containsNonEntailing;
		}

		private Set<Pair<RelationNode,RelationNode>> m_implicitSet;
		private double m_prob;
		private Set<RelationNode> m_reaching;
		private Set<RelationNode> m_reachable;
		private boolean m_containsEntailing;
		private boolean m_containsNonEntailing;
	}

	public GreedyEdgeLearner() {
		m_nodeGraph = null;
		m_edgeCandidates = null;
		m_reachableNodes = null;
		m_reachingNodes = null;
		m_localModel = null;
	}

	public GreedyEdgeLearner(NodeGraph ioGraph, ScoreModel iLocalModel) throws Exception {
		m_nodeGraph = ioGraph;
		m_localModel = iLocalModel;
		init1();
	}
	
	public void init(NodeGraph ioGraph, MapLocalScorer iLocalModel) throws Exception {
		m_nodeGraph = ioGraph;
		m_localModel = iLocalModel;
		init1();
	}
	
	public void init1() throws Exception {

		//if cast fails we should throw an exception
		//m_goldStandard = AbstractOntologyGraph.fromFile(iParams.getFile("gs-file"));

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

	protected abstract double multipleRelationsMultiplicativeChange(Set<Pair<RelationNode,RelationNode>> iImplicitSet) throws Exception;
	protected abstract boolean toStop(double score);
	public abstract double getObjectiveFunctionValue();

	public void learn() throws Exception {

		m_nodeGraph.getGraph().clearEdges(); //we delete all original edges

		initEdgeCandidates();

		while(!m_edgeCandidates.isEmpty()) {

			ImplicitSetProb bestImplicitSetProb=null;
			//find the max and argmax
			for(Pair<RelationNode,RelationNode> edgeCandidate:m_edgeCandidates) {
				ImplicitSetProb currImplicitSetProb = calcImplicitSetProb(edgeCandidate);
				if(currImplicitSetProb.containsEntailing()) {
					bestImplicitSetProb=currImplicitSetProb;
					break;
				}
				else if(bestImplicitSetProb==null || currImplicitSetProb.getProb()>bestImplicitSetProb.getProb()) 
					bestImplicitSetProb=currImplicitSetProb;
			}

			if(toStop(bestImplicitSetProb.getProb()) && !bestImplicitSetProb.containsEntailing())
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
		//free memory
		m_reachableNodes.clear();
		m_reachingNodes.clear();
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
		
		//check if it contains a non-entailing
		boolean isNonEntailing=false;
		boolean isEntailing=false;
		Set<Pair<String,String>> nonEntailing = m_localModel.getNonEntailing();
		Set<Pair<String,String>> entailing = m_localModel.getEntailing();
		
		for(Pair<RelationNode,RelationNode> currEdgeCandidate: implicitSet) {
			Pair<String,String> currCandidate = new Pair<String, String>(currEdgeCandidate.getFirst().description(),currEdgeCandidate.getSecond().description());
			if(nonEntailing.contains(currCandidate)) {
				isNonEntailing = true;
			}
			else if(entailing.contains(currCandidate)){
				isEntailing=true;
			}
			if(isNonEntailing && isEntailing)
				throw new OntologyException("implicit set contains both entailing and non-entailing hard constraint for edge candidate" + edgeCandidate);				
		}
		
		//to speed things up - no need to calc the setProb is entailing or non-entailing
		double setProb;
		if(isNonEntailing)
			setProb=0;
		else 
			setProb = multipleRelationsMultiplicativeChange(implicitSet);
		return new ImplicitSetProb(implicitSet,setProb,m_reachingNodes.get(edgeCandidate.getFirst()),m_reachableNodes.get(edgeCandidate.getSecond()),isEntailing,isNonEntailing);
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
	ScoreModel m_localModel;
	//AbstractOntologyGraph m_goldStandard;
}
