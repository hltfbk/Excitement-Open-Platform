package eu.excitementproject.eop.lap.biu.ae.parser;

import org.uimafit.descriptor.ConfigurationParameter;

import eu.excitementproject.eop.common.datastructures.Envelope;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;

public class EasyFirstParserAE extends StanfordDependenciesParserAE<EasyFirstParser> {

	/**
	 * hostname of the machine that runs the external easyfirst server.
	 */
	public static final String PARAM_HOST = "host";
	@ConfigurationParameter(name = PARAM_HOST, mandatory = false, defaultValue = EasyFirstParser.DEFAULT_HOST)
	private String host;
	
	/**
	 * port that external easyfirst server listens to.
	 */
	public static final String PARAM_PORT = "port";
	@ConfigurationParameter(name = PARAM_PORT, mandatory = false, defaultValue = EasyFirstParser.DEFAULT_PORT_STR)
	private int port;
	
	private static Envelope<EasyFirstParser> envelope = new Envelope<EasyFirstParser>();
	
	@Override
	protected final Envelope<EasyFirstParser> getEnvelope(){return envelope;}
	
	@Override
	protected EasyFirstParser buildInnerTool() throws Exception {
		
		EasyFirstParser parser = new EasyFirstParser(host, port);
		parser.init();
		return parser;
	}	
}
