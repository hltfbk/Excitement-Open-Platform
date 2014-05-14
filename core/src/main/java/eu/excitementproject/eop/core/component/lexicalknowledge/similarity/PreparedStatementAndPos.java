/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.similarity;
import java.sql.PreparedStatement;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;


/**
 * @author Amnon Lotan
 *
 * @since Nov 26, 2011
 */
public class PreparedStatementAndPos {
	
	private final PreparedStatement stmt;
	private final PartOfSpeech pos;
	/**
	 * Ctor
	 * @param stmt
	 * @param pos
	 */
	public PreparedStatementAndPos(PreparedStatement stmt, PartOfSpeech pos) {
		super();
		this.stmt = stmt;
		this.pos = pos;
	}
	
	/**
	 * @return the pos
	 */
	public PartOfSpeech getPos() {
		return pos;
	}

	/**
	 * @return the stmt
	 */
	public PreparedStatement getStmt() {
		return stmt;
	}
}

