package eu.excitementproject.eop.biutee.rteflow.systems.excitement;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;

/**
 * 
 * @author Asher Stern
 * @since Jan 23, 2013
 *
 */
public class BiuteeEDA implements EDABasic<TEDecision>
{

	@Override
	public void initialize(CommonConfig config) throws ConfigurationException,
			EDAException, ComponentException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public TEDecision process(JCas aCas) throws EDAException,
			ComponentException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startTraining(CommonConfig c) throws ConfigurationException,
			EDAException, ComponentException
	{
		// TODO Auto-generated method stub
		
	}

}
