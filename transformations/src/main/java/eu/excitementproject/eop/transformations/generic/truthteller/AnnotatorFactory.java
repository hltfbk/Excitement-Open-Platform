package eu.excitementproject.eop.transformations.generic.truthteller;
import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames;

/**
 * 
 * @author Asher Stern and Amnon Lotan
 * @since Oct 4, 2011
 *
 */
public class AnnotatorFactory
{
	public AnnotatorFactory(ConfigurationFile configurationFile) throws TeEngineMlException
	{
		super();
		this.configurationFile = configurationFile;
	}
	
	public SentenceAnnotator getSentenceAnnotator() throws TeEngineMlException
	{
		try
		{
			SentenceAnnotator ret;
			ConfigurationParams params = getTruthTellerParams();
			if (userRequiresAnnotations(params))
			{
				ret = getDefaultSentenceAnnotator(params);
			}
			else
			{
				// dummy implementation
				ret = new DummyTreeAnnotator();
				logger.warn("Using a dummy annotator - NO ANNOTATIONS WILL BE PERFORMED!");
			}

			logger.info("Using SentenceAnnotator: "+ret.getClass().getName());

			return ret;
		}
		catch (ConfigurationException e)
		{
			throw new TeEngineMlException("Failed to construct SentenceAnnotator. See nested exception",e);
		}
		catch (AnnotatorException e)
		{
			throw new TeEngineMlException("Failed to construct SentenceAnnotator. See nested exception",e);
		}
	}
	
	
	private SentenceAnnotator getDefaultSentenceAnnotator(ConfigurationParams params) throws TeEngineMlException, ConfigurationException, AnnotatorException
	{
		return new DefaultSentenceAnnotator(params);
	}
	
	protected ConfigurationParams getTruthTellerParams() throws ConfigurationException
	{
		ConfigurationParams params = configurationFile.getModuleConfiguration(TransformationsConfigurationParametersNames.TRUTH_TELLER_MODULE_NAME);
		return params;
	}
	
	protected boolean userRequiresAnnotations(ConfigurationParams params) throws ConfigurationException
	{
		boolean ret = true;
		if (params.containsKey(TransformationsConfigurationParametersNames.USER_REQUIRES_ANNOTATIONS))
		{
			ret = params.getBoolean(TransformationsConfigurationParametersNames.USER_REQUIRES_ANNOTATIONS);
		}
		return ret;
	}
	
	protected ConfigurationFile configurationFile;
	
	private static final Logger logger = Logger.getLogger(AnnotatorFactory.class);
}
