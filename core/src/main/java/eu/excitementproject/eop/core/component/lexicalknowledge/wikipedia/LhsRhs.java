/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia;

/**
 * Two ordered Strings
 * @author Amnon Lotan
 *
 * @since Jan 12, 2012
 */
public class LhsRhs {
	
	private final String lhs;
	private final String rhs;
	/**
	 * Ctor
	 * @param lhs
	 * @param rhs
	 */
	public LhsRhs(String lhs, String rhs) {
		super();
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	/**
	 * @return the lhs
	 */
	public String getLhs() {
		return lhs;
	}
	/**
	 * @return the rhs
	 */
	public String getRhs() {
		return rhs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
		result = prime * result + ((rhs == null) ? 0 : rhs.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LhsRhs other = (LhsRhs) obj;
		if (lhs == null) {
			if (other.lhs != null)
				return false;
		} else if (!lhs.equals(other.lhs))
			return false;
		if (rhs == null) {
			if (other.rhs != null)
				return false;
		} else if (!rhs.equals(other.rhs))
			return false;
		return true;
	}
	
	

}

