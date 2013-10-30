package eu.excitementproject.eop.globalgraphoptimizer.graph;

public abstract class AbstractRuleEdge {
	
	public abstract String toString();
	public abstract String symbol();
	
	public RelationNode from() {
		return m_from;
	}
		
	public RelationNode to() {
		return m_to;
	}
	
	public void setFrom(RelationNode from) throws Exception {
		m_from=from;
	}
	
	public void setTo(RelationNode to) throws Exception {
		m_to=to;
	}
	
	public abstract boolean equals(Object other); 
	public abstract String toDotString();
		
	public int hashCode() {
		return toString().hashCode();
	}
	
	public double score() {
		return m_score;
	}
	
	public void setScore(double score) {
		m_score = score;
	}
	
	protected RelationNode m_from;
	protected RelationNode m_to;
	protected double m_score;


}
