package eu.excitementproject.eop.core.component.lexicalknowledge.transDm;

/**
 * An ENUM type to hold the different similarity measures which are 
 * accepted.
 *  
 * 
 * @author Britta Zeller <zeller@cl.uni-heidelberg.de>
 *
 */
public enum SimMeasure {
	COSINE,
	BALAPINC;
	

	@Override
	public String toString() {
		switch (this) {
			case COSINE: return "cosine";
			case BALAPINC: return "balapinc";
		}
		return null;
	}

}
