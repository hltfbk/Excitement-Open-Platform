package eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped;

import java.io.File;
import java.net.URL;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.core.component.alignment.lexicallink.LexicalAligner;

/**
 * This is, useful, but a broken code. 
 * (e.g. won't work within a JAR) 
 * 
 * TODO 
 * Update ASAP, after updating LexicalAligner to accept 
 * List of lexical resource in its constructor. 
 * 
 * *sigh* ... Well, but it works as is, at least within Source Tree Trunk. 
 * 
 * @author Tae-Gil Noh
 *
 */
public class LinProxLinkerDE implements AlignmentComponent {

	/**
	 * WARN: Broken code; wordNetPath doesn't work. --- update either commonconfig 
	 * or LexicalAligner. Thus, it will rely on fixed XML file path --- which won't work
	 * within a Jar (used as a library) 
	 * 
	 * @param wordNetPath
	 * @throws AlignmentComponentException
	 */
	public LinProxLinkerDE() throws AlignmentComponentException {
		
		URL configFileURL = getClass().getResource("/configuration-file/lexlinkers/GermanLinProx.xml");
		File configFile = new File(configFileURL.getFile());
		try {
			config = new ImplCommonConfig(configFile);
		}
		catch (ConfigurationException ce)
		{
			throw new AlignmentComponentException("Reading base configuration file failed!", ce); 
		}
		//updateConfig(); 	
		worker = new LexicalAligner(config); 
		
	}
	
	public void annotate(JCas aJCas) throws AlignmentComponentException
	{
		worker.annotate(aJCas); 
	}
	
	
	// this doesn't work. :-( ... CommonConfig implementation is really weak for now. 
//	private void updateConfig() throws AlignmentComponentException
//	{
//		try 
//		{
//			NameValueTable wordNet = config.getSection("wordnet");
//			wordNet.setString("wordnet-dir", this.wordNetPath); 
//			
//		}
//		catch (ConfigurationException ce)
//		{
//			throw new AlignmentComponentException("failed at generating configuration object", ce); 
//		}
//		
//	}
	
//	private void updateConfig() throws AlignmentComponentException
//	{
//		// fill this.config for WordNet & LexicalAligner 
//		// Note that this is actually making up a configuration via code, instead of XML 
//		try 
//		{
//			// For LexicalAligner 
//			NameValueTable general = config.getSection(LexicalAligner.GENERAL_PARAMS_CONF_SECTION); 
//			general.setString(LexicalAligner.MAX_PHRASE_KEY, "5"); 
//			
//			NameValueTable lexicalResource = config.getSection(LexicalAligner.LEXICAL_RESOURCES_CONF_SECTION);
//			lexicalResource.setString("wordnet", "eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource");
//			
//			// For WordNetResource 
//			NameValueTable wordNet = config.getSection("wordnet");
//			wordNet.setString("useLemma", "false");
//			wordNet.setString("version", "3.0"); 
//			//wordNet.setString("wordnet-dir", "../data/WordNet/3.0/dict.wn.orig"); 
//			wordNet.setString("wordnet-dir", this.wordNetPath); 
//			wordNet.setString("useFirstSenseOnlyLeft", "true"); 
//			wordNet.setString("useFirstSenseOnlyRight", "true"); 
//			wordNet.setString("entailing-relations", "SYNONYM,DERIVATIONALLY_RELATED,HYPERNYM,INSTANCE_HYPERNYM,MEMBER_HOLONYM,PART_HOLONYM,ENTAILMENT,SUBSTANCE_MERONYM"); 			
//			wordNet.setString("wordnet-depth", "2"); 
//		}
//		catch (ConfigurationException ce)
//		{
//			throw new AlignmentComponentException("failed at generating configuration object", ce); 
//		}
//	}
	
	
	// two commmon private variables 
	private final LexicalAligner worker; 
	private final ImplCommonConfig config;  

	
	public String getComponentName()
	{
		return this.getClass().getName(); 
	}
	
	public String getInstanceName()
	{
		return null; 
	}
}
