package eu.excitementproject.eop.globalgraphoptimizer.graph;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class RelationNode /*extends AttributeContainerImpl*/ implements Comparable<RelationNode>{
	
	public RelationNode(int id, String description) {
		
		m_description = description;
		m_id = id;
		m_inEdges=new HashSet<AbstractRuleEdge>();
		m_outEdges=new HashSet<AbstractRuleEdge>();
		m_attrs = null;
	}
	
	public RelationNode(RelationNode other) {
		m_id = other.m_id;
		m_description=other.m_description;
		m_inEdges=other.m_inEdges;
		m_outEdges=other.m_outEdges;
		m_attrs = other.m_attrs;
	}
	
	public void addOutEdge(AbstractRuleEdge e) {
		m_outEdges.add(e);
	}
	
	public void addInEdge(AbstractRuleEdge e) {
		m_inEdges.add(e);
	}
	
	public void removeOutEdge(AbstractRuleEdge e) {
		m_outEdges.remove(e);
	}
	
	public void removeInEdge(AbstractRuleEdge e) {
		m_inEdges.remove(e);
	}
	
	public int id() {
		return m_id;
	}
	
	public String description() {
		return m_description;
	}
	
	public void setDescription(String desc) {
		m_description=desc;
	}
	
	public Iterable<AbstractRuleEdge> outEdges() {
		return m_outEdges;
	}
	
	public int outEdgesCount() {
		return m_outEdges.size();
	}
	
	public Iterable<AbstractRuleEdge> inEdges() {
		return m_inEdges;
	}
	
	public int inEdgesCount() {
		return m_inEdges.size();
	}
	
	public String toString() {
		return m_id+";"+m_description;
	}
	public int getInstances() {
		return m_instances;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((m_description == null) ? 0 : m_description.hashCode());
		result = prime * result + m_id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelationNode other = (RelationNode) obj;
		if (m_description == null) {
			if (other.m_description != null)
				return false;
		} else if (!m_description.equals(other.m_description))
			return false;
		if (m_id != other.m_id)
			return false;
		return true;
	}

	public void setInstances(int instances) {
		m_instances = instances;
	}

	public double getMentions() {
		return m_mentions;
	}

	public void setMentions(double mentions) {
		m_mentions = mentions;
	}
	
	public void clearEdges() {
		m_inEdges.clear();
		m_outEdges.clear();
	}
	
	
	public int attributeCount()
	{
		if(m_attrs == null)
			return 0;
		
		return m_attrs.size();
	}
	
	
	
	//
	public final Object attr(String iName)
	{
		if(m_attrs == null || iName == null)
			return null;
		return m_attrs.get(iName);
	}

	

	// set an attribute
	public Iterable<String> attributeNames()
	{
		if(m_attrs == null)
			m_attrs = new Hashtable<String, Object>();

		return m_attrs.keySet();
	}

	

	// set an attribute
	public void setAttr(String iName, Object iValue)
	{
		if(iValue == null || iName == null)
			return;

		if(m_attrs == null)
			m_attrs = new Hashtable<String, Object>();

		m_attrs.put(iName, iValue);
	}

	// set an attribute
	public void deleteAttr(String iName)
	{
		if(iName == null)
			return;

		if(m_attrs != null)
			m_attrs.remove(iName);
	}

	public int compareTo(RelationNode otherNode) {
		if(m_id<otherNode.m_id)
			return -1;
		if(m_id>otherNode.m_id)
			return 1;
		return 0;
	}
	
	private int m_id;
	private String m_description;
	private int m_instances;
	private double m_mentions;
	protected Map<String, Object> m_attrs;
	protected Set<AbstractRuleEdge> m_inEdges;
	protected Set<AbstractRuleEdge> m_outEdges;	
}
