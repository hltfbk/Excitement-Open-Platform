package eu.excitementproject.eop.common.utilities.configuration;

/**
 * @author erelsgl
 * @date 06/01/2011
 */
public class Demo {

	/**
	 * @param args
	 * @throws ConfigurationException 
	 * @throws ConfigurationFileDuplicateKeyException 
	 */
	public static void main(String[] args) throws ConfigurationFileDuplicateKeyException, ConfigurationException {
		String configurationFilePath = "src/ac/biu/nlp/nlp/general/configuration/Demo.xml";
		ConfigurationFile conf = new ConfigurationFile(configurationFilePath);
		ConfigurationParams loggingParams = conf.getModuleConfiguration("logging");
		String experiment_name = loggingParams.getString("experiment-name");
		System.out.println("logging experiment-name: "+experiment_name);
		System.out.println("logging log-file-max-size: "+loggingParams.getString("log-file-max-size"));
	}

}
