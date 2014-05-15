/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean;
import java.io.Serializable;

/**
 * A pair of words, one entailing the other. 
 * @author Amnon Lotan
 *
 * @since Dec 25, 2011
 */
public class EntailmentPair implements Serializable {

	private static final long serialVersionUID = -6134580562608153313L;
	private final String entailing;
	private final String entailed;
	/**
	 * Ctor
	 * @param entailing
	 * @param entailed
	 */
	public EntailmentPair(String entailing, String entailed) {
		super();
		this.entailing = entailing;
		this.entailed = entailed;
	}
	
	/**
	 * @return the entailed
	 */
	public String getEntailed() {
		return entailed;
	}
	/**
	 * @return the entailing
	 */
	public String getEntailing() {
		return entailing;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((entailed == null) ? 0 : entailed.hashCode());
		result = prime * result
				+ ((entailing == null) ? 0 : entailing.hashCode());
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
		EntailmentPair other = (EntailmentPair) obj;
		if (entailed == null) {
			if (other.entailed != null)
				return false;
		} else if (!entailed.equals(other.entailed))
			return false;
		if (entailing == null) {
			if (other.entailing != null)
				return false;
		} else if (!entailing.equals(other.entailing))
			return false;
		return true;
	}
	
	
}

