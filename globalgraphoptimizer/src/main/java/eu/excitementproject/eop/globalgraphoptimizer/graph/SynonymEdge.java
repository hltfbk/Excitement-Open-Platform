package eu.excitementproject.eop.globalgraphoptimizer.graph;

public class SynonymEdge extends AbstractRuleEdge{
	
	public SynonymEdge(RelationNode node1, RelationNode node2, double score) throws Exception {
		
		if(node1.id()<node2.id()) {
			m_from = node1;
			m_to = node2;
		}
		else if(node2.id()<node1.id()) {
			m_from = node2;
			m_to = node1;
		}
		else
			throw new Exception("Loops are not allowed in the graph");

		m_score=score;
	}
	
	public String toString() {
		
		return m_from.id()+SYMBOL+m_to.id();
	}
	
	public RelationNode from() {
		return m_from;
	}
		
	public RelationNode to() {
		return m_to;
	}
	
	/**
	 * The setter needs to make sure that the setting is legal
	 * @param from
	 * @throws Exception 
	 */
	public void setFrom(RelationNode from) throws Exception {
		if(from.id()>=m_to.id())
			throw new Exception("The id of the from must be smaller than the id of the to");
		m_from=from;
	}
	
	public void setTo(RelationNode to) throws Exception {
		if(to.id()<=m_from.id())
			throw new Exception("The id of the to must be larger than the id of the from");
		m_to=to;
	}
	
	public boolean equals(Object other) {
		if(!(other instanceof SynonymEdge))
			return false;
		SynonymEdge otherEdge = (SynonymEdge) other;
		return m_from.equals(otherEdge.m_from) && m_to.equals(otherEdge.m_to);
	}
	
	public int hashCode() {
		return toString().hashCode();
	}
	
	public double score() {
		return m_score;
	}
	
	public String toDotString() {
		return "\t"+m_from.id()+"--"+m_to.id()+"[fontsize=10.0,label=\"\"];";
	}
	
	@Override
	public String symbol() {
		return SYMBOL;
	}
	
	public static final String SYMBOL = "--";

}
