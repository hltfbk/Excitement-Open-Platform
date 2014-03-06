package eu.excitementproject.eop.lexicalminer.wikipedia;
import java.io.FileNotFoundException;



import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiTitleParsingException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.InitException;
import eu.excitementproject.eop.lexicalminer.dataAccessLayer.InsertionTool;
import eu.excitementproject.eop.lexicalminer.definition.Common.StopWatch;
import eu.excitementproject.eop.lexicalminer.definition.Common.StopwordsDictionary;
import eu.excitementproject.eop.lexicalminer.definition.Common.UtilClass;
import eu.excitementproject.eop.lexicalminer.wikipedia.common.IExtractor;

/** First Update : 16.3.2012<br>
 * A language-independent lexical inference rules miner from Wikipedia. 
 * Each language should extend this class, implementing the language-dependent parts.   
 * 
 * Don't forget to import the the JWPL Wikipedia API and all it's dependencies<br>
 * <br>
 * In Properties of the project, under "Java Build Path"<br>
 * In Libraries tab add the following Jars:<br>
 * 1. de.tudarmstadt.ukp.wikipedia.api-0.9.1.jar<br>
 * 2. add the jars from the dependencies folder (log4j etc...)<br>
 * <br>
 * Follow the instructions in the attached log4j.properties file to config
 * log4j. don't forget to add the classpath of the log4j directory you created<br>
 * <br>
 * Created By:<br>
 * Dov Miron<br>
 * Alon Halfon<br>
 * Modified by: Eyal Shnarch
 */


public abstract class WikipediaLexicalInferencesMiner {
	protected static Logger m_logger;
	protected static ConfigurationParams processingToolsConf;
	public static String m_jarsFolder;
	
	private static Logger logger = Logger.getLogger(WikipediaLexicalInferencesMiner.class);

	/**
	 * Main function to execute all the extraction methods.<br>
	 * Make sure you start the process with sufficient amount of space (for example -Xmx4496M).
	 * The function will run the extractors according to the configuration files and save
	 * the results to the DB.<br>
	 * Follow the progress by reading the log file.
	 */
	public void MineWikipedia(ConfigurationFile conf) throws WikiInitializationException, WikiTitleParsingException, IOException
	{

		
		m_logger = Logger.getLogger(WikipediaLexicalInferencesMiner.class.getName());
		m_logger.info("Logger started properly");
		
		
		ConfigurationParams generalConf;
		try {
			generalConf = conf.getModuleConfiguration("General");
			UtilClass.init(generalConf);
		} catch (ConfigurationException e) {
			m_logger.fatal("No \"General\" module found in configuration file. please review your file. "+e.getMessage());
			return;
		}
		
		int maxNPSize;
		
		try {
			m_jarsFolder = generalConf.get("jars-dir");
			maxNPSize = generalConf.getInt("max_NP_words_count");		
		} catch (ConfigurationException e) {
			m_logger.fatal("Missing configuration. module \"General\" key max_NP_words_count which represents the maximum number of words in noun phrase must have a value. "+e.getMessage());
			return;
		}
		
		
		
		//m_jarsFolder = System.getenv("JARS"); // Deprecated - read from the configuration file
		logger.info(m_jarsFolder);
		
		// configure the database connection parameters for the jwpl
		final DatabaseConfiguration dbConfig = new DatabaseConfiguration();
		
		try {
			ConfigurationParams jwplConf = conf.getModuleConfiguration("jwpl");
			
			dbConfig.setHost(jwplConf.get("host"));
			dbConfig.setDatabase(jwplConf.get("database")); 
			dbConfig.setUser(jwplConf.get("user"));
			dbConfig.setPassword(jwplConf.get("password"));
			dbConfig.setLanguage(Language.valueOf(jwplConf.get("language")));
			
			logger.info("Password: " + jwplConf.get("password"));
			
		} catch (ConfigurationException e) {
			m_logger.fatal("Missing configuration. module \"jwpl\" must have the keys: host ,database ,user ,password and language. "+e.getMessage());
			return;
		}
		
		
		
		ConfigurationParams databaseConf;
		boolean useCategory,useRedirect,useLink,useParenthesis,useLexicalIDM,useSyntacticIDM;
		try {

			ConfigurationParams extractorsConf = conf.getModuleConfiguration("Extractors"); 
			useCategory=extractorsConf.getBoolean("useCategory");
			useRedirect=extractorsConf.getBoolean("useRedirect");
			useLink=extractorsConf.getBoolean("useLink");
			useParenthesis=extractorsConf.getBoolean("useParenthesis");
			useLexicalIDM=extractorsConf.getBoolean("useLexicalIDM");
			useSyntacticIDM=extractorsConf.getBoolean("useSyntacticIDM");

			if(useLexicalIDM && useSyntacticIDM){
				m_logger.fatal("Do not insert into the database rules originated from both the lexicalIDM and the syntacticIDM extractors. Using them both can result in wrong classifiers ranks.");
			}
			
		} catch (ConfigurationException e) {
			m_logger.fatal("Missing configuration. module \"Extractors\" must have the keys: useCategory ,useRedirect ,useLink ,useParenthesis, useLexicalIDM and useSyntacticIDM (boolean values). "+e.getMessage());
			return;
		}
		
		
		
		try {
			// where we save the inferences to
			databaseConf = conf.getModuleConfiguration("Database");
		} catch (ConfigurationException e) {
			m_logger.fatal("module \"Database\" in configuration file is missing. "+e.getMessage());
			return;
		}
		
		
		// Create the Wikipedia object
		Wikipedia wiki = new Wikipedia(dbConfig);


		logger.info("Wikipedia object was created");
	
		try {
			processingToolsConf = conf.getModuleConfiguration("processing_tools");
		} catch (ConfigurationException e) {
			m_logger.fatal("module \"processing_tools\" in configuration file is missing. "+e.getMessage());
			return;
		}
		String stopwordsFilePath;
		try {
			stopwordsFilePath = processingToolsConf.getString("stopwordsFilePath");
			StopwordsDictionary.init(stopwordsFilePath);
		} catch (ConfigurationException e) {
			m_logger.fatal("module \"processing_tools\" does not contain the stopwordsFilePath key. Exception: "+e.getMessage());
			return;
		}
		
		runExtractors(wiki,databaseConf, maxNPSize, useParenthesis, useLink, useLexicalIDM, useSyntacticIDM, useRedirect, useCategory);

	}
	
	public void RunExtractor(Wikipedia wikipedia,IExtractor extractor,ConfigurationParams dbConfig)
	{
		
		String extractorName = extractor.getRelationType().toString();
		m_logger.info("Start running for "+ extractorName);
		InsertionTool insertTool;
		try {
			insertTool = new InsertionTool(dbConfig);
		} catch (ConfigurationException e) {
			m_logger.error("error initializing  the insertion tool. one of the needed config on \"Database\" module is missing "+ e.getMessage());
			return;
		}
	
		StopWatch DBStopwatch=new StopWatch();
		StopWatch miningStopwatch = new StopWatch();
		StopWatch allStopwatch = new StopWatch();
		allStopwatch.start();
		Iterable<Page> pagesIterable =  wikipedia.getArticles();
		int doneCount=0;
		
		
		
		try {
			int extractionMethodID=insertTool.getExtractionMethodIDByName(extractorName);
			HashSet<Integer> doneBeforeCrash=new HashSet<Integer>();
			if (extractionMethodID!=-1)
			{
				m_logger.info(String.format("%s Extractor - Skipping all pages that already done by",extractorName));
				StopWatch getDonesSW=new StopWatch();
				getDonesSW.start();
				doneBeforeCrash=insertTool.getDoneBeforeCrashForType(extractionMethodID);
				getDonesSW.stop();
				m_logger.info("Get all done IDs for extractor "+extractorName+" in "+getDonesSW.getTotalTimeSeconds()+" seconds");
			}
			else
				m_logger.info(String.format("%s Extractor - DB didn't contain any existing rule - Working on the whole Wikipedia database",extractorName));
			

			for (Page page: pagesIterable)
			{
				doneCount++;
				
					List<LexicalRule<RuleInfo>> pagePotentialRules;
					
					if (!doneBeforeCrash.contains(page.getPageId()))
					{
						miningStopwatch.start();
						pagePotentialRules = extractor.ExtractDocument(page);
						miningStopwatch.stop();
			
				       
				       DBStopwatch.start();
						for (LexicalRule<RuleInfo> lexicalRule : pagePotentialRules) {
							insertTool.AddRule(lexicalRule);
						}
						DBStopwatch.stop();
					}
			 
			       
			       if (doneCount % 50000==0)
			       {
			    	   m_logger.info(String.format("%s Extractor --- %d Wikipedia Pages Done",extractorName,doneCount));
			    	   m_logger.info(String.format("%s Extractor: DB Insertions time so far:%d",extractorName.toString(),DBStopwatch.getTotalTimeSeconds()));
			    	   m_logger.info(String.format("%s Extractor:Mining the data time so far:%d",extractorName,miningStopwatch.getTotalTimeSeconds()));
			    	   allStopwatch.stop();
			    	   m_logger.info(String.format("%s Extractor:Total time so far:%d",extractorName,allStopwatch.getTotalTimeSeconds()));
			    	   allStopwatch.start();
			       	   
			       }
			
			}
			
		} catch (FileNotFoundException e) {
			m_logger.fatal("in "+extractor.getRelationType().toString()+" Extractor", e);
		} catch (SQLException e) {
			m_logger.fatal("in "+extractor.getRelationType().toString()+" Extractor", e);
		} catch (InitException e) {
			m_logger.fatal("in "+extractor.getRelationType().toString()+" Extractor", e);
		} catch (javax.naming.ConfigurationException e) {
			m_logger.fatal("in "+extractor.getRelationType().toString()+" Extractor", e);
		} 
		
		// to make sure all the rules of the last batch are inserted
		try {
			insertTool.manualFlushAndCloseConnection();
		} catch (SQLException e) {
			m_logger.fatal("in "+extractor.toString()+" Extractor", e);
		}
		
		m_logger.info("Done running for "+ extractor.getRelationType().toString());
	
	}


	protected void runExtractors(Wikipedia wiki, ConfigurationParams databaseConf, int maxNPSize, 
			boolean useParenthesis, boolean useLink, boolean useLexicalIDM, 
			boolean useSyntacticIDM, boolean useRedirect, boolean useCategory) {
		IExtractor extractor;
		if (useParenthesis)
		{
			extractor= getParenthesesExtractor();
			startExtractorInNewThread(wiki,extractor,databaseConf);
		}
		
		if (useLink)
		{
			extractor = getLinksExtractor();
			startExtractorInNewThread(wiki,extractor,databaseConf);
		}
		
		if (useLexicalIDM)
		{
			extractor = getLexicalIDMExtractor();
			startExtractorInNewThread(wiki,extractor,databaseConf);
		}
		
		if (useSyntacticIDM)
		{
			extractor = getSyntacticIDMExtractor(maxNPSize);
			startExtractorInNewThread(wiki,extractor,databaseConf);
		}
		
		if (useRedirect)
		{
			extractor =getRedirectExtractor();
			startExtractorInNewThread(wiki,extractor,databaseConf);
		}
		
		if (useCategory)
		{
			extractor = getCategoryExtractor();
			startExtractorInNewThread(wiki,extractor,databaseConf);
		}
		
	}


	protected void startExtractorInNewThread(final Wikipedia wiki, final IExtractor extractor,final ConfigurationParams databaseConf) {
		Thread thread = new Thread(){
		    public void run(){
		    	RunExtractor(wiki, extractor,databaseConf);
		        
		    }
		  };
		 
		  thread.start();
	}



	protected abstract IExtractor getSyntacticIDMExtractor(int m_maxNPSize);	
	protected abstract IExtractor getLexicalIDMExtractor();
	protected abstract IExtractor getParenthesesExtractor();
	protected abstract IExtractor getCategoryExtractor();
	protected abstract IExtractor getLinksExtractor();
	protected abstract IExtractor getRedirectExtractor();
	

	




}
