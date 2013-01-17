
package eu.excitementproject.eop.core.component.lexicalknowledge.germanet;

import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;

public class GermaNetInfo implements RuleInfo
{

	private static final long serialVersionUID = 6632656153521827038L;
	private int leftSynsetID;
	private int rightSynsetID;

	public GermaNetInfo(int leftSynsetID, int rightSynsetID) {
		this.leftSynsetID = leftSynsetID;
		this.rightSynsetID = rightSynsetID;
	}

	public int getLeftSynsetID() {
		return leftSynsetID;
	}

	public int getRightSynsetID() {
		return rightSynsetID;
	}

	public int hashCode() {
		return leftSynsetID + rightSynsetID;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GermaNetInfo other = (GermaNetInfo) obj;
		return (this.leftSynsetID == other.getLeftSynsetID() && this.rightSynsetID == other.getRightSynsetID());
	}
}



