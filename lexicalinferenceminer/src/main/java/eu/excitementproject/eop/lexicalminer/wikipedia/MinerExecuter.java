package eu.excitementproject.eop.lexicalminer.wikipedia;

import java.io.IOException;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;

import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiTitleParsingException;

/**
 * The entry point for running {@link WikipediaLexicalInferencesMinerForEnglish}.
 * @author Dov Miron and Alon Halfon. Modified by: Eyal Shnarch
 * @since 16.3.2012
 */
public class MinerExecuter {

	public static void main(String[] args) throws WikiInitializationException, WikiTitleParsingException, IOException
	{
		
		
		
		if (args.length==0)
		{
			System.out.println("Missing configuration file path on first argument");
			return;
		}
		
		System.out.println("Configurationm file: " + args[0]);
		
		ConfigurationFile conf;
		try {
			conf = new ConfigurationFile(args[0]);
		} catch (ConfigurationFileDuplicateKeyException e) {

			System.out.println("Exception when initializing the ConfigurationFile Class. error was:\n"+ e.getMessage());
			return;
		} catch (ConfigurationException e) {
			System.out.println("Exception when initializing the ConfigurationFile Class. error was:\n"+ e.getMessage());
			return;
		}
		
		WikipediaLexicalInferencesMiner miner = new WikipediaLexicalInferencesMinerForEnglish();
		miner.MineWikipedia(conf);
	}
}
