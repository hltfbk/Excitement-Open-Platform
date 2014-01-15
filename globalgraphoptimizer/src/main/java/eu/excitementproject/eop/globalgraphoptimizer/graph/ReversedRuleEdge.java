package eu.excitementproject.eop.globalgraphoptimizer.graph;

public class ReversedRuleEdge extends AbstractRuleEdge {

	public ReversedRuleEdge(RelationNode from, RelationNode to,double score) {
		m_from=from;
		m_to=to;
		m_score=score;
	}
	
	@Override
	public String toString() {
		return m_from.id()+SYMBOL+m_to.id();
	}

	@Override
	public boolean equals(Object other) {
		if(!(other instanceof ReversedRuleEdge))
			return false;
		ReversedRuleEdge otherEdge = (ReversedRuleEdge) other;
		return m_from.equals(otherEdge.m_from) && m_to.equals(otherEdge.m_to);
	}

	@Override
	public String toDotString() {
		return "\t"+m_from.id()+"->"+m_to.id()+"[fontsize=10.0,label=\"\",style=dotted];";
	}
	
	@Override
	public String symbol() {
		return SYMBOL;
	}
	
	public static final String SYMBOL = "-R>";
}
