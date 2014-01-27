package eu.excitementproject.eop.globalgraphoptimizer.graph;

import java.io.File;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;



public abstract class AbstractOntologyGraph {
	
	private static Logger logger = Logger.getLogger(AbstractOntologyGraph.class);
	
	public AbstractOntologyGraph() {
		this("");
	}

	public AbstractOntologyGraph(String description) {
		this(description,new TreeMap<Integer, RelationNode>(),new HashMap<String, AbstractRuleEdge>());
	}

	public AbstractOntologyGraph(String description, Map<Integer,RelationNode> nodes, Map<String,AbstractRuleEdge> edges) {
		this(description,nodes,edges,false);
	}

	public AbstractOntologyGraph(String description, Map<Integer,RelationNode> nodes, Map<String,AbstractRuleEdge> edges, boolean contracted) {
		m_description = description;
		m_nodes = nodes;
		m_edges = edges;
		m_contracted = contracted;
	}
	
	public AbstractOntologyGraph(Set<String> iNodeDescs) {
		m_nodes = new TreeMap<Integer, RelationNode>();
		m_edges = new HashMap<String, AbstractRuleEdge>();
		int i = 1;
		for(String nodeDesc:iNodeDescs) { 
			m_nodes.put(i, new RelationNode(i,nodeDesc));
			i++;
		}
	}
	
	public AbstractRuleEdge createEdge(String description) throws Exception {
		
		AbstractRuleEdge newEdge;
		String[] parts;
		if(description.contains(RuleEdge.SYMBOL)) {
			parts = description.split(RuleEdge.SYMBOL);
			RelationNode from = m_nodes.get(Integer.parseInt(parts[0]));
			RelationNode to = m_nodes.get(Integer.parseInt(parts[1]));
			if(from==null || to==null)
				throw new Exception("edge description contains node ids that are not in the graph");
			newEdge = new RuleEdge(from,to,1);
		}
		else if(description.contains(ReversedRuleEdge.SYMBOL)){
			parts = description.split(ReversedRuleEdge.SYMBOL);
			RelationNode from = m_nodes.get(Integer.parseInt(parts[0]));
			RelationNode to = m_nodes.get(Integer.parseInt(parts[1]));
			if(from==null || to==null)
				throw new Exception("edge description contains node ids that are not in the graph");
			newEdge = new ReversedRuleEdge(from,to,1);
		}
		else if(description.contains(SynonymEdge.SYMBOL)) {
			parts = description.split(SynonymEdge.SYMBOL);
			RelationNode from = m_nodes.get(Integer.parseInt(parts[0]));
			RelationNode to = m_nodes.get(Integer.parseInt(parts[1]));
			if(from==null || to==null)
				throw new Exception("edge description contains node ids that are not in the graph");
			newEdge = new SynonymEdge(from,to,1);
		}
		else throw new Exception("ERROR: edge description is illegal");
		return newEdge;
	}
	
	public static String[] parseEdgeDesc(String edgeDesc) {
		
		String[] result = new String[3];
		String[] parts = null;
		if(edgeDesc.contains(RuleEdge.SYMBOL)) {
			result[1] = RuleEdge.SYMBOL;
			parts = edgeDesc.split(RuleEdge.SYMBOL);
		}
		else if (edgeDesc.contains(ReversedRuleEdge.SYMBOL)) {
			result[1] = ReversedRuleEdge.SYMBOL;
			parts = edgeDesc.split(ReversedRuleEdge.SYMBOL);
		}
		else if (edgeDesc.contains(SynonymEdge.SYMBOL)) {
			result[1] = SynonymEdge.SYMBOL;
			parts = edgeDesc.split(SynonymEdge.SYMBOL);
		}
		result[0] = parts[0];
		result[2] = parts[1];
		return result;
	}
	
	public AbstractOntologyGraph copy() throws Exception {
		
		AbstractOntologyGraph copy = getClass().newInstance();
		
		copy.m_description=m_description;
		copy.m_contracted=m_contracted;
			
		for(RelationNode node: m_nodes.values()) {
			RelationNode newNode = new RelationNode(node.id(),node.description());
			newNode.setMentions(node.getMentions());
			newNode.setInstances(node.getInstances());
			copy.addNode(newNode);
		}
		for(AbstractRuleEdge edge: m_edges.values()) {
			AbstractRuleEdge newEdge=null;
			if(edge instanceof RuleEdge)
				newEdge = new RuleEdge(copy.m_nodes.get(edge.from().id()),copy.m_nodes.get(edge.to().id()),edge.score());
			else if (edge instanceof ReversedRuleEdge)
				newEdge = new ReversedRuleEdge(copy.m_nodes.get(edge.from().id()),copy.m_nodes.get(edge.to().id()),edge.score());
			else if(edge instanceof SynonymEdge)
				newEdge = new SynonymEdge(copy.m_nodes.get(edge.from().id()),copy.m_nodes.get(edge.to().id()),edge.score());
			
			copy.addEdge(newEdge);
		}
		return copy;
	}
	
	public AbstractOntologyGraph union(AbstractOntologyGraph otherGraph) throws Exception {
		
		if(this.getClass() != otherGraph.getClass())
			throw new Exception("Union of graphs of differenct classes is not permitted");
		
		Set<Integer> thisSet = m_nodes.keySet();
		Set<Integer> otherSet = new HashSet<Integer>();
		
		for(RelationNode otherNode: otherGraph.getNodes()) {
			
			if(!thisSet.contains(otherNode.id())) 
				throw new Exception("trying to union graphs that are not over the same set of nodes");
			
			otherSet.add(otherNode.id());
		}
		
		for(Integer thisId: thisSet) {
			if(!otherSet.contains(thisId))
				throw new Exception("trying to union graphs that are not over the same set of nodes");
		}
		
		AbstractOntologyGraph copyThis = copy();
		
		for(AbstractRuleEdge otherEdge: otherGraph.getEdges())
			copyThis.addEdge(otherEdge);
		
		return copyThis;
	}
	
	
	
	public Iterable<RelationNode> getNodes() {
		return m_nodes.values();
	}
	
	/**
	 * This is a copy of the keys to allow changing the graph while iterating - 
	 * to avoid java.util.ConcurrentModificationException
	 * @return
	 */
	public Set<Integer> getNodeIds() {
		Set<Integer> result = new TreeSet<Integer>();
		for(Integer id: m_nodes.keySet())
			result.add(id.intValue());
		return result;
	}
	
	
	
	public int getNodeCount() {
		return m_nodes.keySet().size();
	}
	
	public Iterable<AbstractRuleEdge> getEdges() {
		return m_edges.values();
	}
	
	public int getEdgeCount() {
		return m_edges.keySet().size();
	}
	
	public abstract boolean addEdge(AbstractRuleEdge edge) throws Exception;
	public abstract boolean addEdge(RelationNode fromNode, RelationNode toNode) throws Exception;
	
	//we add a node only if it doesn't exist
	public boolean addNode(RelationNode node) {
		if(m_nodes.get(node.id())==null) {
			m_nodes.put(node.id(),node);
			return true;
		}
		return false;
	}
	
	public RelationNode getNode(int id) {
		return m_nodes.get(id);
	}
	
	public AbstractRuleEdge removeEdge(String edgeId) {
		AbstractRuleEdge e = m_edges.get(edgeId);
		if(e!=null) {
			e.from().removeOutEdge(e);
			e.to().removeInEdge(e);
		}
		return m_edges.remove(edgeId);
	}
	
	public AbstractRuleEdge removeEdge(AbstractRuleEdge edge) {
		AbstractRuleEdge e = m_edges.get(edge.toString());
		if(e!=null) {
			e.from().removeOutEdge(e);
			e.to().removeInEdge(e);
		}
		return m_edges.remove(edge.toString());
	}
	
	public RelationNode removeNode(int nodeId) {
		
		Iterator<AbstractRuleEdge> outIter = m_nodes.get(nodeId).outEdges().iterator();
		while(outIter.hasNext()) {		
			AbstractRuleEdge e = outIter.next();
			outIter.remove();
			e.to().removeInEdge(e);
			m_edges.remove(e.toString());
		}
		
		Iterator<AbstractRuleEdge> inIter = m_nodes.get(nodeId).inEdges().iterator();
		while(inIter.hasNext()) {
			AbstractRuleEdge e = inIter.next();
			inIter.remove();
			e.from().removeOutEdge(e);
			m_edges.remove(e.toString());
		}
		return m_nodes.remove(nodeId);
	}
	

	public boolean containsEdge(AbstractRuleEdge edge) {
		return m_edges.containsKey(edge.toString());
	}
	
	public boolean containsEdge(String edgeDesc) {
		return m_edges.containsKey(edgeDesc);
	}
	
	public boolean contains(RelationNode node) {
		return m_nodes.containsKey(node.id());
	}
	
	public void print(PrintStream writer) {
		
		writer.println("nodes:");
		for(RelationNode node: m_nodes.values())
			writer.println(node);
		writer.println();
				
		writer.println("edges:");
		for(AbstractRuleEdge edge:m_edges.values())
			writer.println(edge);
	}
	
	public void setDescription(String description) {
		m_description = description;
	}
	
	public String description(){
		return m_description;
	}
	
	public void save(File oFile) throws IOException {
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(oFile));
		pw.println(m_description);
		pw.println("Nodes:");
		
		List<Integer> ids = new LinkedList<Integer>(m_nodes.keySet());
		Collections.sort(ids);
		for(Integer id: ids)
			pw.println(id+"\t"+getNode(id).description()+
					"\t"+getNode(id).getInstances()+"\t"+getNode(id).getMentions());
		
		pw.println("Edges:");
		List<String> edges = new LinkedList<String>(m_edges.keySet());
		Collections.sort(edges);
		for(String edge: edges)
			pw.println(edge);
		pw.flush();
		pw.close();
	}
	
	public void clearEdges() {
		m_edges.clear();
		for(RelationNode node:m_nodes.values())
			node.clearEdges();
	}
	
	public Map<String,RelationNode> getDesc2NodesMap() {
		Map<String,RelationNode> desc2Nodes = new HashMap<String, RelationNode>();
		//prepare potential edges for classification
		for(RelationNode node: getNodes())
			desc2Nodes.put(node.description(), node);
		return desc2Nodes;
	}
	
	public double calcAverageNodeDegree() {
		
		int sum = 0;
		for(RelationNode node: m_nodes.values()) {
			sum+=node.inEdgesCount();
			sum+=node.outEdgesCount();
		}
		
		return (double) sum / m_nodes.size();
	}
	
	public void printEdgesToLog() {
		for(AbstractRuleEdge edge:getEdges()) 
			logger.info("EDGE: " +  edge.from().description()+edge.symbol()+edge.to().description()+"\t"+edge.score());
	}
	
	public double sumOfEdgeWeights() {
		double result=0.0;
		for(AbstractRuleEdge e:m_edges.values())
			result+=e.score();
		return result;
	}
	
	public abstract int potentialEdges();
	public abstract List<ViolatedTransitivityConstraint> findViolatedTransitivityConstraints();
	public abstract List<List<Integer>> findViolatedTreeConstraints() throws UnsupportedOperationException;

	protected String m_description;
	protected Map<Integer,RelationNode> m_nodes;
	protected Map<String,AbstractRuleEdge> m_edges;
	protected boolean m_contracted=false;
}
