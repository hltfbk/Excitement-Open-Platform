/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.wordnet;
import java.io.File;

import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetInitializationException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordnetDictionaryImplementationType;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.ext_jwnl.ExtJwnlDictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwi.JwiDictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwnl.JwnlDictionaryManager;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwnl.JwnlDictionaryManager.JwnlDictionaryManagementType;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwnl.JwnlDictionaryManager.JwnlDictionarySupportedVersion;


/**
 * @author Amnon Lotan
 *
 * @since 7 Dec 2011
 */
public class WordNetDictionaryFactory {

	/**
	 * @param wnDictionaryDir
	 * @param wordnetDictionaryImplementation 
	 * @return
	 * @throws WordNetInitializationException 
	 */
	public static Dictionary newDictionary(File wnDictionaryDir, WordnetDictionaryImplementationType wordnetDictionaryImplementation) throws WordNetInitializationException
	{
		if (wordnetDictionaryImplementation == null)
			// default option
			return new ExtJwnlDictionary(wnDictionaryDir);
		else
			switch (wordnetDictionaryImplementation)
			{
			case JWI:
				return new JwiDictionary(wnDictionaryDir);
			case JWNL:
				return new JwnlDictionaryManager(JwnlDictionaryManagementType.DISK, JwnlDictionarySupportedVersion.VER_30, wnDictionaryDir).newDictionary();
			case EXT_JWNL:
				return new ExtJwnlDictionary(wnDictionaryDir);
			default:
				throw new WordNetInitializationException("Unsupported WordnetDictionaryImplementationType: " + wordnetDictionaryImplementation + 
						". Add code here to supprt it.");
			
			}
	}

}

