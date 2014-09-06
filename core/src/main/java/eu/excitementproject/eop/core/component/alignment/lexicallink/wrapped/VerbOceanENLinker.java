package eu.excitementproject.eop.core.component.alignment.lexicallink.wrapped;

import java.io.File;
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
public class VerbOceanENLinker implements AlignmentComponent {
	/**
	 * WARN: Broken code; verbOceanPath doesn't work. --- update either common config 
	 * or LexicalAligner. Thus, it will rely on fixed XML file path --- which won't work
	 * within a Jar (used as a library) 
	 * 
	 * @param wordNetPath
	 * @throws AlignmentComponentException
	 */
	public VerbOceanENLinker(String verbOceanFilePath) throws AlignmentComponentException {
		
		this.verbOceanPath = verbOceanFilePath; 
		// temporary! 
		File configFile = new File("../core/src/main/resources/configuration-file/lexlinkers/VerbOceanENLinker.xml");
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

	
	// private variable for path of underlying resource 
	@SuppressWarnings("unused")
	private final String verbOceanPath; 
	
	// two common private variables 
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
