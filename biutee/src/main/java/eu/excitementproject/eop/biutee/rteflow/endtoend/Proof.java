package eu.excitementproject.eop.biutee.rteflow.endtoend;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * @author Asher Stern
 * @since Jul 14, 2013
 *
 */
public abstract class Proof implements Serializable
{
	private static final long serialVersionUID = -4536915487361835659L;
	
	@Override
	public abstract String toString();
	public abstract Map<Integer, Double> getFeatureVector();
}
