package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.globalgraphoptimizer.alg.TopologicalSorter;
import eu.excitementproject.eop.globalgraphoptimizer.alg.TransitiveClosureReduction;
import eu.excitementproject.eop.globalgraphoptimizer.defs.Constants;
import eu.excitementproject.eop.globalgraphoptimizer.graph.AbstractOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.AbstractRuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.graph.DirectedOneMappingOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.score.ContractedGraphLocalScoreModel;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;

public class EfficientlyComponentCorrectHtlTreeLearner extends HighToLowTreeLearner{

	private Logger logger = Logger.getLogger(EfficientlyComponentCorrectHtlTreeLearner.class);
	
	public EfficientlyComponentCorrectHtlTreeLearner(NodeGraph ioGraph,	MapLocalScorer iLocalModel,double edgeCost) throws Exception {
		super(ioGraph, iLocalModel,edgeCost);
	}

	public EfficientlyComponentCorrectHtlTreeLearner(double edgeCost) {
		super(edgeCost);
	}
	
	public void init(NodeGraph ioGraph,	MapLocalScorer iLocalModel) throws Exception {
		super.init(ioGraph, iLocalModel);
	}
	
	public void learn() throws Exception {

		//init with htl-tree
		super.learn();
		learnAfterInit();
	}
	
	public void learnAfterInit() throws Exception {
		
		//1. compute reduced graph
		DirectedOneMappingOntologyGraph graphCopy = (DirectedOneMappingOntologyGraph) m_nodeGraph.getGraph().copy();
		Map<Integer,Set<Integer>> contractedNodes2NodesMap = graphCopy.contractGraph();

		//2. create new local model
		ContractedGraphLocalScoreModel contractedScoreModel = new ContractedGraphLocalScoreModel(m_localModel, contractedNodes2NodesMap, m_edgeCost);
		graphCopy.scoreEdges(contractedScoreModel);

		//3. perform iterative procedure on reduced graph
		performIterativeProcedure(graphCopy,contractedScoreModel);

		//4. correct graph according to out
		copyEdgesFromContractedGraph(graphCopy,contractedNodes2NodesMap,contractedScoreModel);
	}

	private void copyEdgesFromContractedGraph(DirectedOneMappingOntologyGraph graphCopy, Map<Integer, Set<Integer>> contractedNodes2NodesMap,
			ContractedGraphLocalScoreModel contractedScoreModel) throws Exception {

		AbstractOntologyGraph graph = m_nodeGraph.getGraph();
		graph.clearEdges();
		//add the complete graph edges
		for(Integer contractedNode: contractedNodes2NodesMap.keySet()) {

			Set<Integer> completeGraphNodeIds = contractedNodes2NodesMap.get(contractedNode);
			Iterator<Integer> iter1 = completeGraphNodeIds.iterator();

			while((iter1.hasNext())) {

				int node1Id = iter1.next();
				Iterator<Integer> iter2 = completeGraphNodeIds.iterator();

				while(iter2.hasNext()) {

					int node2Id = iter2.next();
					if(node1Id!=node2Id) {

						RelationNode fromNode = graph.getNode(node1Id);
						RelationNode toNode = graph.getNode(node2Id);
						double score = m_localModel.getEntailmentScore(fromNode, toNode);
						graph.addEdge(new RuleEdge(fromNode, toNode, score));
					}
				}
			}	
		}
		//add the contracted graph edges
		for(AbstractRuleEdge contractedGraphEdge: graphCopy.getEdges()) {

			Set<Integer> fromIds = contractedNodes2NodesMap.get(contractedGraphEdge.from().id());
			Set<Integer> toIds = contractedNodes2NodesMap.get(contractedGraphEdge.to().id());
			Iterator<Integer> iter1 = fromIds.iterator();

			while((iter1.hasNext())) {

				int node1Id = iter1.next();
				Iterator<Integer> iter2 = toIds.iterator();
				while(iter2.hasNext()) {

					int node2Id = iter2.next();

					RelationNode fromNode = graph.getNode(node1Id);
					RelationNode toNode = graph.getNode(node2Id);
					double score = m_localModel.getEntailmentScore(fromNode, toNode);
					graph.addEdge(new RuleEdge(fromNode, toNode, score));

				}
			}	
		}
	}

	private void performIterativeProcedure(DirectedOneMappingOntologyGraph graph, ContractedGraphLocalScoreModel contractedScoreModel) throws Exception {

		//keep going while did there are changes to the graph 
		boolean converge = false;
		double currentObjValue = graph.sumOfEdgeWeights();
		
		/*for(RelationNode node: graph.getNodes())
			EL.warn(node);
		for(AbstractRuleEdge edge: graph.getEdges())
			EL.warn(edge+"\t"+edge.score());
		*/
		
		logger.warn("OBJECTIVE-FUNCTION-VALUE-CONTRACTED: " + currentObjValue);	
		while(!converge) {
			for(Integer currNodeId: graph.getNodeIds()) {
				RelationNode currNode = graph.getNode(currNodeId);
				logger.info("Current node: " + currNode.id());
				reattachNode(currNode,graph,contractedScoreModel);
			}
			
		/*	for(RelationNode node: graph.getNodes())
				EL.warn(node);
			for(AbstractRuleEdge edge: graph.getEdges())
				EL.warn(edge+"\t"+edge.score());*/
			
			double objectiveValue = graph.sumOfEdgeWeights();
			if(objectiveValue+0.00001<currentObjValue)
				throw new OntologyException("objective function value can not decrease. Current value: " + currentObjValue + " new value: " + objectiveValue);
			else if(objectiveValue-currentObjValue < Constants.CONVERGENCE)
				converge = true;
			currentObjValue = objectiveValue;
			logger.warn("OBJECTIVE-FUNCTION-VALUE-CONTRACTED: " + currentObjValue);	
		}
	}

	private void reattachNode(RelationNode removedNode, DirectedOneMappingOntologyGraph graph, ContractedGraphLocalScoreModel contractedScoreModel) throws Exception {

		graph.removeNode(removedNode.id());

		//compute transitive reduction
		DirectedOneMappingOntologyGraph graphCopy = (DirectedOneMappingOntologyGraph) graph.copy();
		TransitiveClosureReduction.performTransitiveReductionOnTransitiveGraph(graphCopy);
	

		//get the nodes of the rtf topologically sorted
		List<RelationNode> sortedList = TopologicalSorter.sort(graphCopy);
		//compute S_in and S_out with dynamic programming
		Map<RelationNode,Double> sIn = computeSIn(sortedList,graphCopy,removedNode,contractedScoreModel);
		Map<RelationNode,Double> sOut = computeSOut(sortedList,graphCopy,removedNode,contractedScoreModel);
		//go over all options and find the sin and sout
		OptimalReattachment optReattachment = findOptimalReattachment(graphCopy, sIn, sOut, removedNode, sortedList);
		//reattach node
		graph.addNode(removedNode);
		if(optReattachment.getScore()>0) {

			Set<AbstractRuleEdge> edgesToAdd = new HashSet<AbstractRuleEdge>();
			//add in edges
			if(optReattachment.getOut()!=null) {
				int outId = optReattachment.getOut().id();
				RelationNode representOutNode = graph.getNode(outId);
				edgesToAdd.add(new RuleEdge(removedNode,representOutNode,contractedScoreModel.getEntailmentScore(removedNode, representOutNode)));
				for(AbstractRuleEdge outEdge: representOutNode.outEdges())
					edgesToAdd.add(new RuleEdge(removedNode,outEdge.to(),contractedScoreModel.getEntailmentScore(removedNode, outEdge.to())));
			}

			for(RelationNode inNode: optReattachment.getIns()) {

				int inId = inNode.id();
				RelationNode representInNode = graph.getNode(inId);
				edgesToAdd.add(new RuleEdge(representInNode,removedNode,contractedScoreModel.getEntailmentScore(representInNode, removedNode)));
				for(AbstractRuleEdge inEdge: representInNode.inEdges())
					edgesToAdd.add(new RuleEdge(inEdge.from(),removedNode,contractedScoreModel.getEntailmentScore(inEdge.from(), removedNode)));
			}
			for(AbstractRuleEdge edgeToAdd: edgesToAdd)
				graph.addEdge(edgeToAdd);
		}

		if(graph.findViolatedTransitivityConstraints().size() > 0 ||
				graph.findViolatedTreeConstraints().size() > 0)
			throw new OntologyException("Re-attaching the node resulted in a non-transitive or non-rtf graph");
	}

	private Map<RelationNode, Double> computeSIn(List<RelationNode> sortedList,
			DirectedOneMappingOntologyGraph graphCopy, RelationNode removedNode, ContractedGraphLocalScoreModel contractedScoreModel) throws Exception {

		Map<RelationNode,Double> result = new HashMap<RelationNode, Double>();

		for(RelationNode currNode: sortedList) {

			double nodeScore = 0;
			//recursive call
			for(AbstractRuleEdge inEdge: currNode.inEdges())
				nodeScore+=result.get(inEdge.from());
			//edges in Node
			nodeScore+=contractedScoreModel.getEntailmentScore(currNode,removedNode);
			result.put(currNode, nodeScore);
		}
		return result;
	}

	private Map<RelationNode, Double> computeSOut(List<RelationNode> sortedList, 
			DirectedOneMappingOntologyGraph graphCopy, RelationNode removedNode, ContractedGraphLocalScoreModel contractedScoreModel) throws Exception {

		Map<RelationNode,Double> result = new HashMap<RelationNode, Double>();
		for(int i = sortedList.size()-1; i>=0; --i) {

			RelationNode currNode = sortedList.get(i);
			double nodeScore = 0;

			if(currNode.outEdgesCount()>1)
				throw new OntologyException("Node: " + currNode + "has more than one parent in the rtf");
			//recursive call
			for(AbstractRuleEdge outEdge: currNode.outEdges())
				nodeScore+=result.get(outEdge.to());
			//edges in Node
			nodeScore+=contractedScoreModel.getEntailmentScore(removedNode,currNode);
			result.put(currNode, nodeScore);
		}
		return result;
	}

	private OptimalReattachment findOptimalReattachment(DirectedOneMappingOntologyGraph graphCopy, Map<RelationNode,Double> sIn,
			Map<RelationNode,Double> sOut, RelationNode removedNode,
			List<RelationNode> sortedList) {

		double bestScore=0;
		RelationNode bestOut=null;
		List<RelationNode> bestIns =  new ArrayList<RelationNode>();
		//1. inside a connectivity component
	/*	for(RelationNode currRtfNode: graphCopy.getNodes()) {
			double currScore = sIn.get(currRtfNode)+sOut.get(currRtfNode);
			if(currScore > bestScore) {
				bestScore = currScore;
				bestOut=currRtfNode;
				bestIns.clear();
				bestIns.add(currRtfNode);
			}
		}*/
		//as a child of one of the connectivity components
		for(RelationNode currRtfNode: graphCopy.getNodes()) {
			double currScore = sOut.get(currRtfNode);
			List<RelationNode> currIns =  new ArrayList<RelationNode>();
			for(AbstractRuleEdge edge: currRtfNode.inEdges()) {
				if(sIn.get(edge.from())>0) {
					currScore+=sIn.get(edge.from());
					currIns.add(edge.from());
				}
			}
			if(currScore>bestScore) {
				bestScore = currScore;
				bestOut = currRtfNode;
				bestIns = currIns;
			}	
		}
		//as a new root
		double currScore = 0;
		List<RelationNode> currIns =  new ArrayList<RelationNode>();
		for(int i = sortedList.size()-1; i>=0; --i) {

			RelationNode currRootNode = sortedList.get(i);
			if(currRootNode.outEdgesCount()==0) { 
				if(sIn.get(currRootNode)>0) {
					currScore+=sIn.get(currRootNode);
					currIns.add(currRootNode);
				}
			}
		}
		if(currScore>bestScore) {
			bestScore = currScore;
			bestOut = null;
			bestIns = currIns;
		}
		return new OptimalReattachment(bestScore, bestOut, bestIns);
	}
	private class OptimalReattachment {

		public OptimalReattachment(double score, RelationNode out, List<RelationNode> ins) {
			m_score = score;
			m_out = out;
			m_ins=ins;
		}

		public double getScore() {
			return m_score;
		}

		public RelationNode getOut() {
			return m_out;
		}

		public List<RelationNode> getIns() {
			return m_ins;
		}
		private double m_score;
		private RelationNode m_out;
		private List<RelationNode> m_ins;
	}


}
