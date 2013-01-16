package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwnl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetInitializationException;


/**
 * Handles the JWNL's initialization phase.
 * <P>
 * Use {@link #newDictionary()} method to get a new {@linkplain Dictionary},
 * which has JWNL as its underlying implementation.
 * <p>
 * <b>WARNING</b> This dictionary will crash in most multiprocess settings!
 * 
 * @author Asher Stern
 *
 */
public class JwnlDictionaryManager
{
	////////////////// NESTED ENUM TYPES //////////////////////////////


	public static enum JwnlDictionaryManagementType
	{
		DISK,
		MEMORY,
		DATABASE;
	}
	public static enum JwnlDictionarySupportedVersion
	{
		VER_20("2.0"),
		VER_21("2.1"),
		VER_30("3.0");
		
		private JwnlDictionarySupportedVersion(String version)
		{
			this.versionStringRepresentation = version;
		}
		public String getVersionStringRepresentation()
		{
			return this.versionStringRepresentation;
		}
		String versionStringRepresentation;
	}
	
	/////////////////////////////////////////////////////////////////
	
	///////////////////////// PUBLIC METHODS //////////////////////////////
	
	public JwnlDictionaryManager(JwnlDictionaryManagementType type ,JwnlDictionarySupportedVersion version ,File wordnetDictDir)
	{
		this.managementType = type;
		this.version = version;
		this.wordnetDictDir = wordnetDictDir;
	}
	
	public JwnlDictionary newDictionary() throws WordNetInitializationException
	{
		if (!jwnlInitialized)
		{
			synchronized(jwnlInitializationSynchronizer)
			{
				if (!jwnlInitialized)
				{
					initializeJwnl();
					jwnlInitialized = true;
				}
			}
		}
		return new JwnlDictionary();
	}
	
	
	///////////////////////// PROTECTED CONSTANTS /////////////////////////
	protected static String CONFIGURATION_FILE_NAME = "ac_biu_nlp_nlp_jwnl_properties_";
	protected static String CONFIGURATION_FILE_NAME_EXTENSION = ".xml";
	protected static String MARKER_VERSION = "MARKER_VERSION";
	protected static String MARKER_DICT_DIR = "MARKER_DICT_DIR";
	
	protected static String CONFIGURATION_FILE_CONTENTS =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<jwnl_properties language=\"en\">"+
		"\t<version publisher=\"Princeton\" number=\"MARKER_VERSION\" language=\"en\"/>"+
		"\t<dictionary class=\"net.didion.jwnl.dictionary.FileBackedDictionary\">"+
		"\t\t<param name=\"morphological_processor\" value=\"net.didion.jwnl.dictionary.morph.DefaultMorphologicalProcessor\">"+
		"\t\t\t<param name=\"operations\">"+
		"\t\t\t\t<param value=\"net.didion.jwnl.dictionary.morph.LookupExceptionsOperation\"/>"+
		"\t\t\t\t<param value=\"net.didion.jwnl.dictionary.morph.DetachSuffixesOperation\">"+
		"\t\t\t\t\t<param name=\"noun\" value=\"|s=|ses=s|xes=x|zes=z|ches=ch|shes=sh|men=man|ies=y|\"/>"+
		"\t\t\t\t\t<param name=\"verb\" value=\"|s=|ies=y|es=e|es=|ed=e|ed=|ing=e|ing=|\"/>"+
		"\t\t\t\t\t<param name=\"adjective\" value=\"|er=|est=|er=e|est=e|\"/>"+
		"                    <param name=\"operations\">"+
		"                        <param value=\"net.didion.jwnl.dictionary.morph.LookupIndexWordOperation\"/>"+
		"                        <param value=\"net.didion.jwnl.dictionary.morph.LookupExceptionsOperation\"/>"+
		"                    </param>"+
		"\t\t\t\t</param>"+
		"\t\t\t\t<param value=\"net.didion.jwnl.dictionary.morph.TokenizerOperation\">"+
		"\t\t\t\t\t<param name=\"delimiters\">"+
		"\t\t\t\t\t\t<param value=\" \"/>"+
		"\t\t\t\t\t\t<param value=\"-\"/>"+
		"\t\t\t\t\t</param>"+
		"\t\t\t\t\t<param name=\"token_operations\">"+
		"                        <param value=\"net.didion.jwnl.dictionary.morph.LookupIndexWordOperation\"/>"+
		"\t\t\t\t\t\t<param value=\"net.didion.jwnl.dictionary.morph.LookupExceptionsOperation\"/>"+
		"\t\t\t\t\t\t<param value=\"net.didion.jwnl.dictionary.morph.DetachSuffixesOperation\">"+
		"\t\t\t\t\t\t\t<param name=\"noun\" value=\"|s=|ses=s|xes=x|zes=z|ches=ch|shes=sh|men=man|ies=y|\"/>"+
		"\t\t\t\t\t\t\t<param name=\"verb\" value=\"|s=|ies=y|es=e|es=|ed=e|ed=|ing=e|ing=|\"/>"+
		"\t\t\t\t\t\t\t<param name=\"adjective\" value=\"|er=|est=|er=e|est=e|\"/>"+
		"                            <param name=\"operations\">"+
		"                                <param value=\"net.didion.jwnl.dictionary.morph.LookupIndexWordOperation\"/>"+
		"                                <param value=\"net.didion.jwnl.dictionary.morph.LookupExceptionsOperation\"/>"+
		"                            </param>"+
		"\t\t\t\t\t\t</param>"+
		"\t\t\t\t\t</param>"+
		"\t\t\t\t</param>"+
		"\t\t\t</param>"+
		"\t\t</param>"+
		"\t\t<param name=\"dictionary_element_factory\" value=\"net.didion.jwnl.princeton.data.PrincetonWN17FileDictionaryElementFactory\"/>"+
		"\t\t<param name=\"file_manager\" value=\"net.didion.jwnl.dictionary.file_manager.FileManagerImpl\">"+
		"\t\t\t<param name=\"file_type\" value=\"net.didion.jwnl.princeton.file.PrincetonRandomAccessDictionaryFile\"/>"+
		"\t\t\t<param name=\"dictionary_path\" value=\"MARKER_DICT_DIR\"/>"+
		"\t\t</param>"+
		"\t</dictionary>"+
		"\t<resource class=\"PrincetonResource\"/>"+
		"</jwnl_properties>";
	
	protected static String CONFIGURATION_MEMORY_FILE_CONTENTS =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<jwnl_properties language=\"en\">"+
		"\t<version publisher=\"Princeton\" number=\"MARKER_VERSION\" language=\"en\"/>"+
		"\t<dictionary class=\"net.didion.jwnl.dictionary.MapBackedDictionary\">"+
		"\t\t<param name=\"morphological_processor\" value=\"net.didion.jwnl.dictionary.morph.DefaultMorphologicalProcessor\">"+
		"\t\t\t<param name=\"operations\">"+
		"\t\t\t\t<param value=\"net.didion.jwnl.dictionary.morph.LookupExceptionsOperation\"/>"+
		"\t\t\t\t<param value=\"net.didion.jwnl.dictionary.morph.DetachSuffixesOperation\">"+
		"\t\t\t\t\t<param name=\"noun\" value=\"|s=|ses=s|xes=x|zes=z|ches=ch|shes=sh|men=man|ies=y|\"/>"+
		"\t\t\t\t\t<param name=\"verb\" value=\"|s=|ies=y|es=e|es=|ed=e|ed=|ing=e|ing=|\"/>"+
		"\t\t\t\t\t<param name=\"adjective\" value=\"|er=|est=|er=e|est=e|\"/>"+
		"                    <param name=\"operations\">"+
		"                        <param value=\"net.didion.jwnl.dictionary.morph.LookupIndexWordOperation\"/>"+
		"                        <param value=\"net.didion.jwnl.dictionary.morph.LookupExceptionsOperation\"/>"+
		"                    </param>"+
		"\t\t\t\t</param>"+
		"\t\t\t\t<param value=\"net.didion.jwnl.dictionary.morph.TokenizerOperation\">"+
		"\t\t\t\t\t<param name=\"delimiters\">"+
		"\t\t\t\t\t\t<param value=\" \"/>"+
		"\t\t\t\t\t\t<param value=\"-\"/>"+
		"\t\t\t\t\t</param>"+
		"\t\t\t\t\t<param name=\"token_operations\">"+
		"                        <param value=\"net.didion.jwnl.dictionary.morph.LookupIndexWordOperation\"/>"+
		"\t\t\t\t\t\t<param value=\"net.didion.jwnl.dictionary.morph.LookupExceptionsOperation\"/>"+
		"\t\t\t\t\t\t<param value=\"net.didion.jwnl.dictionary.morph.DetachSuffixesOperation\">"+
		"\t\t\t\t\t\t\t<param name=\"noun\" value=\"|s=|ses=s|xes=x|zes=z|ches=ch|shes=sh|men=man|ies=y|\"/>"+
		"\t\t\t\t\t\t\t<param name=\"verb\" value=\"|s=|ies=y|es=e|es=|ed=e|ed=|ing=e|ing=|\"/>"+
		"\t\t\t\t\t\t\t<param name=\"adjective\" value=\"|er=|est=|er=e|est=e|\"/>"+
		"                            <param name=\"operations\">"+
		"                                <param value=\"net.didion.jwnl.dictionary.morph.LookupIndexWordOperation\"/>"+
		"                                <param value=\"net.didion.jwnl.dictionary.morph.LookupExceptionsOperation\"/>"+
		"                            </param>"+
		"\t\t\t\t\t\t</param>"+
		"\t\t\t\t\t</param>"+
		"\t\t\t\t</param>"+
		"\t\t\t</param>"+
		"\t\t</param>"+
		"\t\t<param name=\"file_type\" value=\"net.didion.jwnl.princeton.file.PrincetonObjectDictionaryFile\"/>"+
		"\t\t<param name=\"dictionary_path\" value=\"MARKER_DICT_DIR\"/>"+
		"\t</dictionary>"+
		"\t<resource class=\"PrincetonResource\"/>"+
		"</jwnl_properties>";

		
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////

	//////////////////// PROTECTED STATIC FIELDS ////////////////////////////////////
	protected static Object jwnlInitializationSynchronizer = new Object();
	protected static boolean jwnlInitialized = false;
	

	////////////////////////// PROTECTED METHODS //////////////////////////
	
	protected void createXmlConfigurationFile(String configurationFileConstantContents) throws IOException
	{
		UUID uuid = UUID.randomUUID();
		String configurationFileName = CONFIGURATION_FILE_NAME+uuid.toString()+CONFIGURATION_FILE_NAME_EXTENSION;
		//String configurationFileContents = CONFIGURATION_FILE_CONTENTS.replaceAll(MARKER_VERSION, version.getVersionStringRepresentation()).replaceAll(MARKER_DICT_DIR, this.wordnetDictDir.getAbsolutePath());
		String wordNetDirAsString = FileUtils.filePathToString(this.wordnetDictDir);
		String configurationFileContents = configurationFileConstantContents.replaceAll(MARKER_VERSION, version.getVersionStringRepresentation()).replaceAll(MARKER_DICT_DIR, wordNetDirAsString);
		
		String tempDir = System.getProperty("java.io.tmpdir");
		if (tempDir!=null)
			this.configurationFile = new File(tempDir,configurationFileName);
		else
			this.configurationFile = new File(configurationFileName);
			
		PrintStream configurationFileStream = new PrintStream(this.configurationFile);
		configurationFileStream.println(configurationFileContents);
		configurationFileStream.close();
	}
	
	
	
	protected void initializeJwnl() throws WordNetInitializationException
	{
		if (null==this.managementType)
			throw new WordNetInitializationException("this.managementType is null!");
		if (null==this.version)
			throw new WordNetInitializationException("this.version is null!");
		if (null==this.wordnetDictDir)
			throw new WordNetInitializationException("this.wordnetDictDir is null!");
		
		if ( (this.managementType != JwnlDictionaryManagementType.DISK) && (this.managementType != JwnlDictionaryManagementType.MEMORY) )
			throw new WordNetInitializationException("management type not supported.");
		if (this.wordnetDictDir.exists())
		{
			if (this.wordnetDictDir.isDirectory()) ;
			else
				throw new WordNetInitializationException("the supplied path for wordnet dict dir is not a directory");
		}
		else
			throw new WordNetInitializationException("the supplied path for wordnet dict dir does not exist.");

		try
		{
			try
			{
				String configurationFileConstantContents = null;
				if (this.managementType.equals(JwnlDictionaryManagementType.MEMORY))
					configurationFileConstantContents = CONFIGURATION_MEMORY_FILE_CONTENTS;
				else if (this.managementType.equals(JwnlDictionaryManagementType.DISK))
					configurationFileConstantContents = CONFIGURATION_FILE_CONTENTS;
				else throw new WordNetInitializationException("Internal bug. management type not supported was not catched.");
				createXmlConfigurationFile(configurationFileConstantContents);
			}
			catch(IOException e)
			{
				throw new WordNetInitializationException("Could not create configuration file. See nested exception.",e);
			}
			try
			{
				JWNL.initialize(new FileInputStream(this.configurationFile));
			}
			catch(FileNotFoundException e)
			{
				// can't happen.
				throw new WordNetInitializationException("The created configuration file was not found due to an unknown reason.",e);
			}
			try{this.configurationFile.delete();}catch(Exception e){}
		}
		catch(JWNLException e)
		{
			throw new WordNetInitializationException("initialization of JWNL failed. See nested exception.",e);
		}
	}
	

	
	


	

	
	////////////////////////// PROTECTED FIELDS ///////////////////////////
	
	protected JwnlDictionaryManagementType managementType;
	protected JwnlDictionarySupportedVersion version;
	protected File wordnetDictDir;
	
	protected File configurationFile;
	
	
	



}
