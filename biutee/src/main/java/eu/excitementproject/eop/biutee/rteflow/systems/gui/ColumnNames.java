/**
 * 
 */
package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import java.util.Vector;


/**
 * @author Amnon Lotan
 *
 * @since Mar 14, 2012
 */
public enum ColumnNames {

	OPERATION_ID( "Operation\nID"),
	ORIGINAL_SENTENCE ( "Original\nSentence"), 
	LAST_OPERATION("Last\nOperation"),
	ITERATION (   "Iteration"),	
	MISSING_RELATIONS ( "Missing\nElements"),
	PROOF_COST ( "Proof\nCost"), 
	OPERATION_COST ( "Operation\nCost"), 
	PREDICTIONS_SCORE (   "Predictions\nScore"),
	CLASSIFICATION_SCORE (   "Classification\nScore")
	;
	
	private String toString;
	/**
	 * Ctor
	 */
	private ColumnNames(String toString) {
		this.toString = toString;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return toString;
	}

	
	/**
	 * @return
	 */
	public static Vector<ColumnNames> valuesVector() {
		Vector<ColumnNames> valuesVector = new Vector<ColumnNames>();
		for (ColumnNames column : ColumnNames.values())
			valuesVector.add(column);
		return valuesVector;
	}

	/**
	 * @return
	 */
	public static Vector<String> printableColumnNames() {
		Vector<String> valuesVector = new Vector<String>();
		for (ColumnNames column : ColumnNames.values())
			valuesVector.add(column.toString());
		return valuesVector;
	}
}
