package eu.excitementproject.eop.biutee.rteflow.endtoend;

import java.io.Serializable;

import eu.excitementproject.eop.biutee.script.HypothesisInformation;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * 
 * @author Asher Stern
 * @since Jul 14, 2013
 *
 */
public abstract class Instance implements Serializable
{
	private static final long serialVersionUID = 2182601872503864048L;
	
	public abstract HypothesisInformation getHypothesisInformation() throws BiuteeException;
	@Override
	public abstract String toString();
	public abstract Boolean getBinaryLabel() throws BiuteeException;
}
