package eu.excitementproject.eop.globalgraphoptimizer.graph;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.StringTokenizer;

import eu.excitementproject.eop.globalgraphoptimizer.alg.StrongConnectivityComponents;
import eu.excitementproject.eop.globalgraphoptimizer.alg.TopologicalSorter;
import eu.excitementproject.eop.globalgraphoptimizer.alg.TransitiveClosureReduction;
import eu.excitementproject.eop.globalgraphoptimizer.defs.EdgeType;
import eu.excitementproject.eop.globalgraphoptimizer.score.ScoreModel;


public class DirectedOneMappingOntologyGraph extends DirectedOntologyGraph {

	public DirectedOneMappingOntologyGraph() {
		super();
	}

	public DirectedOneMappingOntologyGraph(String description) {
		super(description);
	}
	
	public DirectedOneMappingOntologyGraph(Set<String> iNodeDescs) {
		super(iNodeDescs);
	}

	public DirectedOneMappingOntologyGraph(String description, Map<Integer,RelationNode> nodes, Map<String,AbstractRuleEdge> edges) {
		super(description,nodes,edges);
	}

	public DirectedOneMappingOntologyGraph(String description, Map<Integer,RelationNode> nodes, Map<String,AbstractRuleEdge> edges, boolean contracted) {
		super(description, nodes, edges, contracted);
	}
	
	/**
	 * Constructs a graph that is a unification of the nodes of the two graph and has all edges in two colors
	 * @param otherGraph
	 * @throws Exception 
	 */
	public DirectedOneMappingOntologyGraph unifyGraph(DirectedOneMappingOntologyGraph otherGraph) throws Exception {
		
		DirectedOneMappingOntologyGraph graph = new DirectedOneMappingOntologyGraph();
		
		Map<String,RelationNode> desc2NodeMap = this.getDesc2NodesMap();
		
		int nodeId = 1;
		//add all nodes in this graph
		for(RelationNode thisNode: getNodes()) {
			graph.addNode(new RelationNode(nodeId++, thisNode.description()));
		}
		//add all nodes in other graph that are not already in this graph
		for(RelationNode otherNode: otherGraph.getNodes()) {
			if(!desc2NodeMap.containsKey(otherNode.description()))
				graph.addNode(new RelationNode(nodeId++,otherNode.description()));
		}
		
		Map<String,RelationNode> unifiedDesc2NodeMap = graph.getDesc2NodesMap();
		
		//add all edges in this graph without color
		for(AbstractRuleEdge thisEdge: getEdges()) {
			RelationNode fromNode = unifiedDesc2NodeMap.get(thisEdge.from().description());
			RelationNode toNode = unifiedDesc2NodeMap.get(thisEdge.to().description());
			graph.addEdge(new RuleEdge(fromNode, toNode, thisEdge.score()));
		}
		//add all edges in other graph in red color
		for(AbstractRuleEdge otherEdge: otherGraph.getEdges()) {
			RelationNode fromNode = unifiedDesc2NodeMap.get(otherEdge.from().description());
			RelationNode toNode = unifiedDesc2NodeMap.get(otherEdge.to().description());
			graph.addEdge(new ColoredRuleEdge(fromNode, toNode, otherEdge.score(),ColoredRuleEdge.EdgeColor.RED));
		}
		return graph;
	}
	
	/**
	 * Constructs the projected graph - i.e., add to this graph all edges from the other graph that are between
	 * nodes that are in this graph (in a differenct color) 
	 * @param otherGraph
	 * @throws Exception 
	 */
	public DirectedOneMappingOntologyGraph projectGraph(DirectedOneMappingOntologyGraph otherGraph) throws Exception {
		
		DirectedOneMappingOntologyGraph graph = new DirectedOneMappingOntologyGraph();
			
		int nodeId = 1;
		//add all nodes in this graph
		for(RelationNode thisNode: getNodes()) {
			graph.addNode(new RelationNode(nodeId++, thisNode.description()));
		}
		
		Map<String,RelationNode> unifiedDesc2NodeMap = graph.getDesc2NodesMap();
		
		//add all edges in this graph without color
		for(AbstractRuleEdge thisEdge: getEdges()) {
			RelationNode fromNode = unifiedDesc2NodeMap.get(thisEdge.from().description());
			RelationNode toNode = unifiedDesc2NodeMap.get(thisEdge.to().description());
			graph.addEdge(new RuleEdge(fromNode, toNode, thisEdge.score()));
		}
		//add all edges in other graph in red color
		for(AbstractRuleEdge otherEdge: otherGraph.getEdges()) {
			RelationNode fromNode = unifiedDesc2NodeMap.get(otherEdge.from().description());
			RelationNode toNode = unifiedDesc2NodeMap.get(otherEdge.to().description());
			if(fromNode!=null && toNode !=null) 
				graph.addEdge(new ColoredRuleEdge(fromNode, toNode, otherEdge.score(),ColoredRuleEdge.EdgeColor.RED));
		}
		return graph;
	}
	
	
	public boolean addEdge(AbstractRuleEdge edge) throws Exception {
		if(!(edge instanceof RuleEdge))
			throw new Exception("Adding reversed edges or synonym edges is not possible in a directed one-mapping graph");
		
		if(m_edges.containsKey(edge.toString()))
			return false;
		
		RelationNode from = edge.from();
		RelationNode to = edge.to();
		
		addNode(from);
		addNode(to);
		from.addOutEdge(edge);
		to.addInEdge(edge);
		
		m_edges.put(edge.toString(), edge);
		return true;
	}
	
	public Map<Integer,Set<Integer>> contractGraph() throws Exception {
		
	//	if(m_contracted) {
		//	throw new OntologyException("Trying to contract an already contracted graph");
		//}
			
		StrongConnectivityComponents scc = new StrongConnectivityComponents();
		List<List<Integer>> components = scc.DFSandSCC(this);
		Map<Integer,Set<Integer>> reducedNodes2NodesMap = contractGraph(components);
		m_contracted=true;
		return reducedNodes2NodesMap;
	}
		
	/**
	 * 
	 * @param components
	 * @return A mapping from the id of a node in the contracted graph to the ids in the original graph
	 * @throws Exception
	 */
	private Map<Integer,Set<Integer>> contractGraph(List<List<Integer>> components) throws Exception {
		
		Map<Integer,Set<Integer>> reducedNodes2NodesMap = new HashMap<Integer, Set<Integer>>();
		
		for (List<Integer> component : components) {
			int firstId = component.get(0);
			if (firstId != -1) {
				
				RelationNode firstNode = m_nodes.get(firstId);
				Set<Integer> origNodes = new HashSet<Integer>();
				origNodes.add(firstId);
				reducedNodes2NodesMap.put(firstId, origNodes);
				
				firstNode.setDescription(formatDescription(component));				
				for (int i=1;i<component.size(); i++) {
					int otherNodeId = component.get(i);
					mergeNodes(firstNode,m_nodes.get(otherNodeId));
					removeNode(otherNodeId);
					origNodes.add(otherNodeId);
				}
			}
		}
		
		return reducedNodes2NodesMap;
	}
	
	private static String formatDescription(List<Integer> component) {
		StringBuilder sb = new StringBuilder();
		boolean bFirst = true;
		for (Integer id : component) {
			if (!bFirst)
				sb.append(",");
			bFirst = false;
			sb.append(id);			
		}
		return sb.toString();
	}
	private void mergeNodes(RelationNode node1, RelationNode node2) throws Exception {
		
		Iterator<AbstractRuleEdge> edgeIter = node2.outEdges().iterator();
		while(edgeIter.hasNext()) {
			
			AbstractRuleEdge currOutEdge = edgeIter.next();
			//remove the edge
			m_edges.remove(currOutEdge.toString());
			edgeIter.remove();
			currOutEdge.to().removeInEdge(currOutEdge);
			//change the edge
			currOutEdge.setFrom(node1);
			//decide if to put it back
			if(!m_edges.containsKey(currOutEdge.toString()) &&
					!currOutEdge.from().equals(currOutEdge.to()))
				addEdge(currOutEdge);
		}
		
		edgeIter = node2.inEdges().iterator();
		while(edgeIter.hasNext()) {
			
			AbstractRuleEdge currInEdge = edgeIter.next();
			//remove the edge
			m_edges.remove(currInEdge.toString());
			edgeIter.remove();
			currInEdge.from().removeOutEdge(currInEdge);
			//change the edge
			currInEdge.setTo(node1);
			
			if(!m_edges.containsKey(currInEdge.toString()) && 
					!currInEdge.from().equals(currInEdge.to())) 
				addEdge(currInEdge);
		}
	}
	
	private static List<Integer> getCurrNodeIds(RelationNode node) {
		
		List<Integer> res = new LinkedList<Integer>();
		StringTokenizer st = new StringTokenizer(node.description(),",");
		while(st.hasMoreTokens()) {
			int id = Integer.parseInt(st.nextToken());
			st.nextToken(); //skip the string
			res.add(id);
		}
		return res;
	}
	
	public boolean containsEdge(RelationNode from, RelationNode to) {
		return m_edges.containsKey(from.id()+RuleEdge.SYMBOL+to.id());
	}
	
	/**
	 * Goes over all nodes, for each node v looks at the predecessors U and successors W and sees if they are connected with the appropriate edge:
	 * so RuleEdge(u,v) and RuleEdge(v,w) --> RuleEdge(u,w).
	 * If the degree of a node is bounded then this should take linear time. Otherwise, worst case is cubic
	 * @return
	 */
	public List<ViolatedTransitivityConstraint> findViolatedTransitivityConstraints() {
		
		List<ViolatedTransitivityConstraint> result = new LinkedList<ViolatedTransitivityConstraint>();
		
		for(RelationNode node: m_nodes.values()) {
			for(AbstractRuleEdge inEdge: node.inEdges()) {
				for(AbstractRuleEdge outEdge: node.outEdges()) {
					if(inEdge.from().id()!=outEdge.to().id() && !containsEdge(inEdge.from(), outEdge.to())) {
						result.add(new ViolatedTransitivityConstraint(inEdge.from().id(), node.id(), outEdge.to().id(), EdgeType.DIRECT, EdgeType.DIRECT));
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * finds triples of nodes in the graph that violate the reduced tree property
	 * that is: u-->v, u-->w, but v-/->w and w-/->v
	 */
	@Override
	public List<List<Integer>> findViolatedTreeConstraints() {
		
		List<List<Integer>> result = new LinkedList<List<Integer>>();
		for(RelationNode node: m_nodes.values()) {
			for(AbstractRuleEdge outEdge1: node.outEdges()) {
				for(AbstractRuleEdge outEdge2: node.outEdges()) {
					if(outEdge1.to().id()<outEdge2.to().id()) {
						if(!containsEdge(outEdge1.to(),outEdge2.to()) && 
								!containsEdge(outEdge2.to(),outEdge1.to())) {
							List<Integer> violation = new ArrayList<Integer>();
							violation.add(node.id());
							violation.add(outEdge1.to().id());
							violation.add(outEdge2.to().id());
							result.add(violation);
						}
					}
				}
			}			
		}
		return result;
	}
	
	public boolean isDirectedForest() throws Exception {
		
		DirectedOneMappingOntologyGraph reduced = this.reduceGraph();
		for(RelationNode node: reduced.getNodes())
			if(node.outEdgesCount()>1)
				return true;
		return false;

	}
	

	public void addTransitiveEdgesNotEfficiently() throws Exception {

		boolean add=true;
		while(add) {
			add = false;
			List<AbstractRuleEdge> edgesToAddList = new LinkedList<AbstractRuleEdge>();
			for(RelationNode currNode: getNodes()) {
				for(AbstractRuleEdge inEdge: currNode.inEdges()) {
					for(AbstractRuleEdge outEdge:currNode.outEdges()) {
						if(inEdge.from()!=outEdge.to())
							edgesToAddList.add(new RuleEdge(inEdge.from(),outEdge.to(),-1));
					}
				}
			}
			for(AbstractRuleEdge edge:edgesToAddList)
				add |= addEdge(edge);
			
			//debug			
			System.out.println("nodes: " + getNodeCount() + ", edges: " + getEdgeCount());
		}
	}

	@Override
	public int potentialEdges() {
		return m_nodes.size()*(m_nodes.size()-1);
	}
	

	/**
	 * Takes a transitive graph and checks if the SCC Skeleton graph is a tree
	 * @return
	 */
	public boolean isSkeletonSccTree() {
		
		for(RelationNode u: m_nodes.values()) {
			for(AbstractRuleEdge out1: u.outEdges()) {
				for(AbstractRuleEdge out2: u.outEdges()) {
					if(out1 != out2) {
						RelationNode v = out1.to();
						RelationNode w = out2.to();
						if(!containsEdge(v,w) && !containsEdge(w,v))
							return false;
					}
				}
			}
		}
		return true;
	}
	
public void addTransitiveEdges() throws Exception {
	
		// Assumption: 'this' is a DAG (as assumed by TopologicalSorter.sort method which are are called bellow)
	
		HashMap<Integer,Set<Integer>> entailmentMap = new HashMap<Integer, Set<Integer>>();
		//this cast is completely safe
		DirectedOneMappingOntologyGraph graphCopy = (DirectedOneMappingOntologyGraph) this.copy();
		
		graphCopy.contractGraph();
		List<RelationNode> sortedNodeList = TopologicalSorter.sort(graphCopy);
		
		for(int i = sortedNodeList.size()-1; i>=0; i--) {
			
			RelationNode currNode = sortedNodeList.get(i);
			//create a list with the ids in the current node
			List<Integer> currNodeIds = getCurrNodeIds(currNode);
			
			//for each one add an entry in the table entailing all of the others
			for(Integer id: currNodeIds) {
				Set<Integer> entailedIds = new HashSet<Integer>();
				for(Integer otherId: currNodeIds) {
					if(!id.equals(otherId))
						entailedIds.add(otherId);
				}
				entailmentMap.put(id, entailedIds);
			}
			//now look at the target nodes in the graph and their lists
			for(AbstractRuleEdge outEdge: currNode.outEdges()) { 
				
				Integer toNodeId = outEdge.to().id();
				for(Integer id: currNodeIds) {
					entailmentMap.get(id).add(toNodeId); //add the node
					entailmentMap.get(id).addAll(entailmentMap.get(toNodeId)); // and those entailed by him
				}
			}
		}
		
		for(Integer fromId: entailmentMap.keySet()) 
			for(Integer toId: entailmentMap.get(fromId))
				addEdge(getNode(fromId),getNode(toId));
	}
	
	/**
	 * Takes a graph that contains all edges in its transitive closure and return a tree that approximates
	 * the skeleton SCC graph of this graph
	 * @return  a new graph that is the SCC graph of this after and performs transitive reduction
	 * on the SCC graph.
	 * @throws Exception 
	 */
	public DirectedOneMappingOntologyGraph generateSkeletonSccTreeFromTransitiveGraph() throws Exception {
		
		//this cast is completely safe
		DirectedOneMappingOntologyGraph graphCopy = (DirectedOneMappingOntologyGraph) this.copy();
		//generate SCC graph
		graphCopy.contractGraph();
		
		//compute transitive reduction
		TransitiveClosureReduction.performTransitiveReductionOnTransitiveGraph(graphCopy);
		
		graphCopy.convertSkeletonGraphToTree();
		return graphCopy;		
	}
	
	public DirectedOneMappingOntologyGraph reduceGraph() throws Exception {
		//@TODO: check efficiency
		//this cast is completely safe
		DirectedOneMappingOntologyGraph graphCopy = (DirectedOneMappingOntologyGraph) this.copy();
		//generate SCC graph
		graphCopy.contractGraph();
		//compute transitive reduction
		TransitiveClosureReduction.performTransitiveReductionOnTransitiveGraph(graphCopy);
		return graphCopy;		
	}
	
	
	
	
	/**
	 * Takes a transitive graph and performs transitive reduction in cubic time. If the degree of the nodes
	 * is bounded by a constant then this is a linear time algorithm.
	 */
	public void performTransitiveReductionOnTransitiveGraph() {
		
		for(RelationNode node:getNodes()) {
			for(AbstractRuleEdge outEdge : node.outEdges()) {
				for(AbstractRuleEdge inEdge : node.inEdges()) {
					removeEdge(new RuleEdge(inEdge.from(), outEdge.to(), 0));
				}
			}
		}
	}
	
	
	private void convertSkeletonGraphToTree() {
		
		Map<RelationNode,Set<Integer>> node2NumOfEntailed = calcNode2NumOfEntailedMap();
		
		for(RelationNode currNode: getNodes()) {
			List<RelationNode> outNodes = new ArrayList<RelationNode>();
			for(AbstractRuleEdge outEdge : currNode.outEdges()) 
				outNodes.add(outEdge.to());
		
			//if there is more than one parent we need to choose a single parent
			if(outNodes.size()>1) {
				
				int maxNumberOfNodes = 0;
				int maxIndex = -1;
				for(int i = 0; i < outNodes.size();++i) {
					
					int numOfNodesEntailedByScc = node2NumOfEntailed.get(outNodes.get(i)).size();
					int numOfNodesInScc = outNodes.get(i).description().split(",").length;
					if(numOfNodesInScc+numOfNodesEntailedByScc > maxNumberOfNodes) {
						maxNumberOfNodes=numOfNodesInScc+numOfNodesEntailedByScc;
						maxIndex = i;
					}
				}
				//now delete all edges except for the max one
				for(int i = 0; i < outNodes.size();++i) {
					if(i != maxIndex)
						removeEdge(new RuleEdge(currNode,outNodes.get(i),0));
				}
			}
		}
	}
	
	private Map<RelationNode,Set<Integer>> calcNode2NumOfEntailedMap() {
		
		Map<RelationNode,Set<Integer>> result = new HashMap<RelationNode, Set<Integer>>();
		List<RelationNode> sortedNodes = TopologicalSorter.sort(this);

		for(int i = sortedNodes.size()-1; i>=0; i--) {
			
			RelationNode currNode = sortedNodes.get(i);
			Set<Integer> entailedNodes = new HashSet<Integer>();
			
			for(AbstractRuleEdge currEdge: currNode.outEdges()) {
				
				RelationNode toNode = currEdge.to();
				entailedNodes.addAll(result.get(toNode)); //add the nodes entailed by toNode
				String[] sccNodes = toNode.description().split(",");
				//add the nodes of toNode itself
				for(int j = 0; j < sccNodes.length;++j) {
					entailedNodes.add(Integer.parseInt(sccNodes[j]));
				}
				
			}
			result.put(currNode, entailedNodes);	
		}
		return result;
	}
	
	public void scoreEdges(ScoreModel scorer) throws Exception {

		for(AbstractRuleEdge edge: m_edges.values()) {
			edge.setScore(scorer.getEntailmentScore(edge.from(), edge.to()));
		}
	}
	
	public List<Set<String>> findWeaklyConnectedComponents() throws Exception {
		
		UndirectedOntologyGraph graph = new UndirectedOntologyGraph(this);
		return graph.findConnectivityComponents();
		
	}
}
