package eu.excitementproject.eop.core.utilities.dictionary.wordnet.ext_jwnl;
import java.io.File;
import java.io.IOException;

import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetInitializationException;



/**
 * Assists the ExtJWNL's initialization phase.
 * <P>
 * Gets a String path to the WN directory and prepares an xml properties file for initializing a new {@link WordnetDictionary}.
 * ExtJWNL is the underlying implementation.
 * 
 * @author Asher Stern
 *
 */
public class ExtJwnlDictionaryInitializer
{
	/**
	 * @param wnDictionaryDir
	 * @return
	 * @throws WordNetInitializationException 
	 */
	public static File makePropsFile(File wnDictionaryDir) throws WordNetInitializationException {
		if (wnDictionaryDir == null)
			throw new WordNetInitializationException("got null WordNet directory");
		String wnDirPath = FileUtils.filePathToString(wnDictionaryDir);
		String propsFileText = CONFIGURATION_FILE_CONTENTS.replaceAll(MARKER_DICT_DIR, wnDirPath);
		File propsFile = null;
		try {
			propsFile = File.createTempFile(CONFIGURATION_FILE_NAME, CONFIGURATION_FILE_NAME_EXTENSION);
			propsFile.deleteOnExit();
			FileUtils.writeFile(propsFile, propsFileText);
		} catch (IOException e) {
			throw new WordNetInitializationException("couldn't create a temp props file", e);
		}
		
		return propsFile;
	}
	
	///////////////////////// PROTECTED CONSTANTS /////////////////////////
	protected static String CONFIGURATION_FILE_NAME = "ac_biu_nlp_nlp_jwnl_properties_";
	protected static String CONFIGURATION_FILE_NAME_EXTENSION = ".xml";
	protected static String MARKER_DICT_DIR = "MARKER_DICT_DIR";
	
	protected static String CONFIGURATION_FILE_CONTENTS =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<jwnl_properties language=\"en\">" +
			"    <version publisher=\"Princeton\" number=\"3.0\" language=\"en\"/>" +
			"    <dictionary class=\"net.sf.extjwnl.dictionary.FileBackedDictionary\">" +
			"        <param name=\"morphological_processor\" value=\"net.sf.extjwnl.dictionary.morph.DefaultMorphologicalProcessor\">" +
			"            <param name=\"operations\">" +
			"                <param value=\"net.sf.extjwnl.dictionary.morph.LookupExceptionsOperation\"/>" +
			"                <param value=\"net.sf.extjwnl.dictionary.morph.DetachSuffixesOperation\">" +
			"                    <param name=\"noun\" value=\"|s=|ses=s|xes=x|zes=z|ches=ch|shes=sh|men=man|ies=y|\"/>" +
			"                    <param name=\"verb\" value=\"|s=|ies=y|es=e|es=|ed=e|ed=|ing=e|ing=|\"/>" +
			"                    <param name=\"adjective\" value=\"|er=|est=|er=e|est=e|\"/>" +
			"                    <param name=\"operations\">" +
			"                        <param value=\"net.sf.extjwnl.dictionary.morph.LookupIndexWordOperation\"/>" +
			"                        <param value=\"net.sf.extjwnl.dictionary.morph.LookupExceptionsOperation\"/>" +
			"                    </param>" +
			"                </param>" +
			"                <param value=\"net.sf.extjwnl.dictionary.morph.TokenizerOperation\">" +
			"                    <param name=\"delimiters\">" +
			"                        <param value=\" \"/>" +
			"                        <param value=\"-\"/>" +
			"                    </param>" +
			"                    <param name=\"token_operations\">" +
			"                        <param value=\"net.sf.extjwnl.dictionary.morph.LookupIndexWordOperation\"/>" +
			"                        <param value=\"net.sf.extjwnl.dictionary.morph.LookupExceptionsOperation\"/>" +
			"                        <param value=\"net.sf.extjwnl.dictionary.morph.DetachSuffixesOperation\">" +
			"                            <param name=\"noun\" value=\"|s=|ses=s|xes=x|zes=z|ches=ch|shes=sh|men=man|ies=y|\"/>" +
			"                            <param name=\"verb\" value=\"|s=|ies=y|es=e|es=|ed=e|ed=|ing=e|ing=|\"/>" +
			"                            <param name=\"adjective\" value=\"|er=|est=|er=e|est=e|\"/>" +
			"                            <param name=\"operations\">" +
			"                                <param value=\"net.sf.extjwnl.dictionary.morph.LookupIndexWordOperation\"/>" +
			"                                <param value=\"net.sf.extjwnl.dictionary.morph.LookupExceptionsOperation\"/>" +
			"                            </param>" +
			"                        </param>" +
			"                    </param>" +
			"                </param>" +
			"            </param>" +
			"        </param>" +
			"        <param name=\"dictionary_element_factory\"" +
			"               value=\"net.sf.extjwnl.princeton.data.PrincetonWN17FileDictionaryElementFactory\"/>" +
			"        <param name=\"file_manager\" value=\"net.sf.extjwnl.dictionary.file_manager.FileManagerImpl\">" +
			"            <param name=\"file_type\" value=\"net.sf.extjwnl.princeton.file.PrincetonRandomAccessDictionaryFile\">" +
			"                <!--<param name=\"write_princeton_header\" value=\"true\"/>-->" +
			"                <!--<param name=\"encoding\" value=\"UTF-8\"/>-->" +
			"            </param>" +
			"            <!--<param name=\"cache_use_count\" value=\"true\"/>-->" +
			"            <param name=\"dictionary_path\" value=\"MARKER_DICT_DIR\"/>" +
			"        </param>" +
			"    </dictionary>" +
			"    <resource class=\"PrincetonResource\"/>" +
			"</jwnl_properties>";

}
