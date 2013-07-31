package eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl;

import eu.excitementproject.eop.biutee.rteflow.endtoend.Instance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Proof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.Provider;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 * @param <I>
 * @param <P>
 */
public abstract class DefaultProver<I extends Instance, P extends Proof> extends Prover<I, P>
{
	public DefaultProver(Provider<Lemmatizer> lemmatizerProvider)
	{
		this.lemmatizerProvider = lemmatizerProvider;
	}
	protected Lemmatizer getLemmatizer() throws BiuteeException
	{
		return lemmatizerProvider.get();
	}
	
	private final Provider<Lemmatizer> lemmatizerProvider;
}
