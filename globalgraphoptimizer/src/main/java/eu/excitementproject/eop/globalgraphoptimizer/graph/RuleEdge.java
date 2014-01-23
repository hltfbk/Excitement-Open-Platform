package eu.excitementproject.eop.globalgraphoptimizer.graph;

public class RuleEdge extends AbstractRuleEdge{
	
	public RuleEdge(RelationNode from, RelationNode to,double score) {
		m_from=from;
		m_to=to;
		m_score=score;
	}
	
	public String toString() {
		return m_from.id()+SYMBOL+m_to.id();
	}
		
	public boolean equals(Object other) {
		if(!(other instanceof RuleEdge))
			return false;
		RuleEdge otherEdge = (RuleEdge) other;
		return m_from.equals(otherEdge.m_from) && m_to.equals(otherEdge.m_to);
	}

	@Override
	public String toDotString() {
		return "\t"+m_from.id()+"->"+m_to.id()+"[fontsize=10.0,label=\"\"];";
	}
	
	@Override
	public String symbol() {
		return SYMBOL;
	}
	
	public static final String SYMBOL = "->"; 
}
