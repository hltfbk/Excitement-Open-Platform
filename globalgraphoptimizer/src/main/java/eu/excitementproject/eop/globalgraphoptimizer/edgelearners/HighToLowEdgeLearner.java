package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.util.HashMap;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;

/**
 * An implementation of the high-to-low algorithm (https://github.com/hltfbk/Excitement-Open-Platform/wiki/Global-graph-optimizer-user-guide#high-to-low)
 * 
 * @author Jonathan
 *
 */
public class HighToLowEdgeLearner implements EdgeLearner {

	private static Logger logger = Logger.getLogger(HighToLowEdgeLearner.class);
	
	public HighToLowEdgeLearner(NodeGraph ioGraph, MapLocalScorer iLocalModel,double edgeCost) throws Exception {
		m_edgeCost = edgeCost;
		init(ioGraph, iLocalModel);
	}
	
	public HighToLowEdgeLearner(double edgeCost) {
		m_edgeCost = edgeCost;
		m_nodeGraph = null;
		m_sortedEdgeCandidates = null;
		m_reachableNodes = null;
		m_reachingNodes = null;
		m_localModel = null;
	}
	
	public void init(NodeGraph ioGraph, MapLocalScorer iLocalModel) throws Exception {

		m_nodeGraph = ioGraph;
		m_localModel = iLocalModel;
		//if cast fails we should throw an exception
		//m_goldStandard = AbstractOntologyGraph.fromFile(iParams.getFile("gs-file"));

		m_reachableNodes = new HashMap<RelationNode, Set<RelationNode>>();
		m_reachingNodes = new HashMap<RelationNode, Set<RelationNode>>();
		m_sortedEdgeCandidates = new LinkedList<Pair<RelationNode,RelationNode>>();

		for(RelationNode node:m_nodeGraph.getGraph().getNodes()) {
			Set<RelationNode> reachableNodes = new HashSet<RelationNode>();
			Set<RelationNode> reachingNodes = new HashSet<RelationNode>();
			reachableNodes.add(node);
			reachingNodes.add(node);
			m_reachableNodes.put(node, reachableNodes);
			m_reachingNodes.put(node,reachingNodes);
		}		

		Map<String,RelationNode> desc2NodeMap = ioGraph.getDesc2NodesMap();

		//adding candidates from score file
		Map<Pair<Integer,Integer>,Double> ruleScoreMap = m_localModel.getRule2ScoreMap();
		for(Pair<Integer,Integer> rule: ruleScoreMap.keySet()) {

			double score = ruleScoreMap.get(rule);
			if(score>m_edgeCost) {
				Pair<String,String> ruleDesc = m_localModel.getRuleDesc(rule);
				m_sortedEdgeCandidates.add(new Pair<RelationNode,RelationNode>(desc2NodeMap.get(ruleDesc.getFirst()),desc2NodeMap.get(ruleDesc.getSecond())));
			}
			else
				break;
		}
	}

	@Override
	public void learn() throws Exception {

		m_nodeGraph.getGraph().clearEdges(); //we delete all original edges

		int i = 0;
		while(!m_sortedEdgeCandidates.isEmpty()) {
			Pair<RelationNode,RelationNode> nodePair = m_sortedEdgeCandidates.iterator().next();
			ImplicitSetProb implicitSetProb = calcImplicitSetProb(nodePair);

			if(implicitSetProb.getProb()>0) {
				//add all of the implicit set
				for(Pair<RelationNode,RelationNode> edgeToAdd: implicitSetProb.getImplicitSet()) {
					RelationNode fromNode = edgeToAdd.getFirst();
					RelationNode toNode = edgeToAdd.getSecond();
					double score = m_localModel.getEntailmentScore(fromNode, toNode);
					m_nodeGraph.getGraph().addEdge(new RuleEdge(fromNode,toNode,score));
				}
				//update reachable and reaching nodes
				for(RelationNode node: implicitSetProb.getReachingNodes())
					m_reachableNodes.get(node).addAll(implicitSetProb.getReachableNodes());
				for(RelationNode node: implicitSetProb.getReachableNodes())
					m_reachingNodes.get(node).addAll(implicitSetProb.getReachingNodes());
				//remove from the list of candidates and add to the list of handled nodes	
				m_sortedEdgeCandidates.removeAll(implicitSetProb.getImplicitSet());
			}
			else {
				m_sortedEdgeCandidates.remove(nodePair);
				logger.info("Edge candidate:\t"+nodePair.getFirst().description()+"\t"+nodePair.getSecond().description()+"\t"
						+m_localModel.getEntailmentScore(nodePair.getFirst(), nodePair.getSecond()));
				logger.info("Implicit set score:\t"+implicitSetProb.getProb());
				for(Pair<RelationNode,RelationNode> implicitEdge: implicitSetProb.getImplicitSet()) {
					logger.info("Implicit edge:\t"+implicitEdge.getFirst().description()+"\t"+implicitEdge.getSecond().description()+"\t"+
							m_localModel.getEntailmentScore(implicitEdge.getFirst(), implicitEdge.getSecond()));
				}
			}	
			i++;
			if(i % 1000000 == 0) {
				System.gc();
				Runtime runtime = Runtime.getRuntime();
				long memory = runtime.totalMemory() - runtime.freeMemory();
				logger.info("Used memory is megabytes: "
						+ (memory / (1024L * 1024L)));
			}
		}
		double currentObjValue = m_nodeGraph.getGraph().sumOfEdgeWeights()-m_edgeCost*m_nodeGraph.getGraph().getEdgeCount();
		logger.warn("OBJECTIVE-FUNCTION-VALUE: " + currentObjValue);	

		//clean all the reachable and reaching nodes to save memory
		m_reachableNodes.clear();
		m_reachingNodes.clear();

	}

	protected ImplicitSetProb calcImplicitSetProb(Pair<RelationNode, RelationNode> edgeCandidate) throws Exception {

		Set<Pair<RelationNode,RelationNode>> implicitSet = calcImplicitSet(edgeCandidate);
		/* Jonathan - commenting this since we have the scores in the local classifier and don't need the entailing and non-entailing
		//check if it contains a non-entailing
		boolean isNonEntailing=false;
		boolean isEntailing=false;
		Set<Pair<String,String>> nonEntailing = m_localModel.getNonEntailing();
		Set<Pair<String,String>> entailing = m_localModel.getEntailing();

		for(Pair<RelationNode,RelationNode> currEdgeCandidate: implicitSet) {
			Pair<String,String> currCandidate = new Pair<String, String>(currEdgeCandidate.key().description(),currEdgeCandidate.value().description());
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
		else */
		double setProb = multipleRelationsMultiplicativeChange(implicitSet);
		return new ImplicitSetProb(implicitSet,setProb,m_reachingNodes.get(edgeCandidate.getFirst()),m_reachableNodes.get(edgeCandidate.getSecond()));
	}

	protected Set<Pair<RelationNode,RelationNode>> calcImplicitSet(Pair<RelationNode, RelationNode> edgeCandidate) {

		RelationNode fromNode = edgeCandidate.getFirst();
		RelationNode toNode = edgeCandidate.getSecond();
		Set<Pair<RelationNode,RelationNode>> implicitSet = new HashSet<Pair<RelationNode,RelationNode>>();

		//add implicit edges due to reachability from the toNode and reachability to the fromNode
		for(RelationNode implicitFromNode:m_reachingNodes.get(fromNode)) {
			for(RelationNode implicitToNode:m_reachableNodes.get(toNode)) {	

				if(implicitFromNode!=implicitToNode) {
					Pair<RelationNode,RelationNode> currImplicitEdgeCandidate =
						new Pair<RelationNode, RelationNode>(implicitFromNode,implicitToNode);
					if(!m_nodeGraph.getGraph().containsEdge(currImplicitEdgeCandidate.getFirst().id()+RuleEdge.SYMBOL+currImplicitEdgeCandidate.getSecond().id()))
						implicitSet.add(currImplicitEdgeCandidate);
				}
			}
		}	
		return implicitSet;	
	}

	protected double multipleRelationsMultiplicativeChange(Set<Pair<RelationNode, RelationNode>> edgeCandidates)
	throws Exception {

		double score=0;	
		for(Pair<RelationNode,RelationNode> edgeCandidate: edgeCandidates) 
			score+=singleRelationMultiplicativeChange(edgeCandidate);
		return score;
	}

	protected double singleRelationMultiplicativeChange(Pair<RelationNode,RelationNode> iEdgeCandidate) throws Exception {
		return m_localModel.getEntailmentScore(iEdgeCandidate.getFirst(),iEdgeCandidate.getSecond())-m_edgeCost;
	}

	public double getObjectiveFunctionValue() {
		return m_nodeGraph.getGraph().sumOfEdgeWeights()-m_edgeCost*m_nodeGraph.getGraph().getEdgeCount();
	}

	NodeGraph m_nodeGraph;
	List<Pair<RelationNode,RelationNode>> m_sortedEdgeCandidates;
	HashMap<RelationNode,Set<RelationNode>> m_reachableNodes;
	HashMap<RelationNode,Set<RelationNode>> m_reachingNodes;
	MapLocalScorer m_localModel;
	protected double m_edgeCost;
}
