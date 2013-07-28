package eu.excitementproject.eop.biutee.rteflow.endtoend;

import java.io.Serializable;
import java.util.Map;

/**
 * A proof is a sequence of transformations that convert T to H.
 * The proof has an attached feature-vector, which represents the proof steps,
 * and can be classifier by a machine-learning classifier.
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
