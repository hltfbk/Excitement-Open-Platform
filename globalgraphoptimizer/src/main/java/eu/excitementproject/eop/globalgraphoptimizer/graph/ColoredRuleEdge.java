package eu.excitementproject.eop.globalgraphoptimizer.graph;

public class ColoredRuleEdge extends RuleEdge {

	private EdgeColor m_color;
	public final String SYMBOL;

	public ColoredRuleEdge(RelationNode from, RelationNode to, double score, EdgeColor color) {
		super(from, to, score);
		m_color = color;
		SYMBOL = "-"+m_color+">";
	}

	@Override
	public String toDotString() {
		return "\t"+m_from.id()+"->"+m_to.id()+"[fontsize=10.0,label=\"\",color="+m_color.toString()+"];";
	}
	
	public String toString() {
		return m_from.id()+SYMBOL+m_to.id();
	}

	public enum EdgeColor {

		RED,BLUE,BLACK;

		public String toString() {

			String result;
			switch (this) {
			case RED:
				result = "red";
				break;
			case BLUE:
				result = "blue";
				break;  
			default:
				result = "black";
			}
			return result;
		}
	}

}
