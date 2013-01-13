package ac.biu.nlp.nlp.engineml.utilities.preprocess;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.PREPROCESS_EASYFIRST;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.PREPROCESS_MINIPAR;

import java.io.File;

import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import eu.excitementproject.eop.common.representation.parse.EnglishSingleTreeParser;
import eu.excitementproject.eop.common.representation.parse.ParserRunException;
import eu.excitementproject.eop.common.representation.parse.easyfirst.EasyFirstParser;
import eu.excitementproject.eop.common.representation.parse.minipar.MiniparClientParser;
import eu.excitementproject.eop.common.representation.parse.minipar.MiniparParser;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

/**
 * Factory to construct a {@link MiniparParser}
 * @author Asher Stern
 * 
 *
 */
public class ParserFactory
{
	public static EnglishSingleTreeParser getParser(ConfigurationParams params) throws TeEngineMlException, ParserRunException, ConfigurationException
	{
		EnglishSingleTreeParser ret = null;
		if ( (params.containsKey(PREPROCESS_EASYFIRST)) && (params.containsKey(PREPROCESS_MINIPAR)) )
			throw new TeEngineMlException("The configuration file specify both easy first and minipar parsers, but only one of them should be specified, not both!");
		
		if (params.containsKey(PREPROCESS_EASYFIRST))
		{
			logger.info("Parser is EasyFirst");
			String parameter = params.get(PREPROCESS_EASYFIRST);
			String host=null;
			Integer port=null;
			if (params.containsKey(ConfigurationParametersNames.PREPROCESS_EASYFIRST_HOST))
				host = params.get(ConfigurationParametersNames.PREPROCESS_EASYFIRST_HOST);
			if (params.containsKey(ConfigurationParametersNames.PREPROCESS_EASYFIRST_PORT))
				port = params.getInt(ConfigurationParametersNames.PREPROCESS_EASYFIRST_PORT);
				
			ret = getParserEasyFirst(host,port,parameter);
		}
		else if (params.containsKey(PREPROCESS_MINIPAR))
		{
			logger.info("Parser is Minipar");
			String parameter = params.get(PREPROCESS_EASYFIRST);
			ret = getParserMinipar(parameter);
			
		}
		else
			throw new TeEngineMlException("Neither minipar not easy first argument specified.");
		
		return ret;
	}
	
	@Deprecated
	public static EnglishSingleTreeParser getParser(String parameter) throws TeEngineMlException, ParserRunException
	{
		return getParserMinipar(parameter);
	}
	
	
	private static EnglishSingleTreeParser getParserEasyFirst(String host, Integer port, String stanfordPosTaggerModuleFileName) throws ParserRunException, TeEngineMlException
	{
		if (null==stanfordPosTaggerModuleFileName)throw new TeEngineMlException("Null stanfordPosTaggerModuleFileName");
		EnglishSingleTreeParser ret = null;
		if ( (null==host) && (null==port) )
			ret = new EasyFirstParser(stanfordPosTaggerModuleFileName);
		else
		{
			if (null==host)host="localhost";
			if (null==port)port=EasyFirstParser.DEFAULT_PORT;
			ret = new EasyFirstParser(host, port, stanfordPosTaggerModuleFileName);
		}
		return ret;
	}
	
	private static EnglishSingleTreeParser getParserMinipar(String miniparParameter) throws TeEngineMlException, ParserRunException
	{
		EnglishSingleTreeParser ret = null;
		if (null==miniparParameter) throw new TeEngineMlException("null parameter");
		File miniparDir = new File(miniparParameter);
		boolean isLocal = false;
		if (miniparDir.exists()&&miniparDir.isDirectory())
			isLocal=true;
		if (isLocal)
			ret = new MiniparParser(miniparParameter);
		else
			ret = new MiniparClientParser(miniparParameter);
		
		return ret;
	}

	private static final Logger logger = Logger.getLogger(ParserFactory.class);
}
