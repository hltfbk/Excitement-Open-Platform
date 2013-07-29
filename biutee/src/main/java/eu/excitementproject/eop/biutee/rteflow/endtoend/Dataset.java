package eu.excitementproject.eop.biutee.rteflow.endtoend;

import java.io.Serializable;
import java.util.List;

import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * Holds a dataset of T-H pairs (represented as {@link Instance}s).
 * 
 * @author Asher Stern
 * @since Jul 14, 2013
 *
 * @param <I>
 */
public abstract class Dataset<I extends Instance> implements Serializable
{
	private static final long serialVersionUID = -2274637862418032250L;

	public abstract List<I> getListOfInstances() throws BiuteeException;
}
