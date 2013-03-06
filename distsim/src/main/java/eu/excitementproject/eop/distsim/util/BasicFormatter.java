package eu.excitementproject.eop.distsim.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Simple logger formator
 *        
 * @author Meni Adler
 * @since 21/07/2011
 * 
 */

public class BasicFormatter extends Formatter {
	public String format(LogRecord rec) {
		StringBuilder buf = new StringBuilder();
		buf.append("[");
		buf.append(rec.getLevel());
		buf.append("] ");
		buf.append(rec.getMessage());
		buf.append("\n");
		return buf.toString();
	}
}
