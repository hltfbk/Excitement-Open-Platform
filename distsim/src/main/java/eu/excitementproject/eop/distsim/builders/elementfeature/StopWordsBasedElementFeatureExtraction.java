/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.elementfeature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Configuration;

/**
 * Defines a list of stop words which should excluded from the feature list
 *
 * @author Meni Adler
 * @since 04/09/2012
 *
 */
public abstract class StopWordsBasedElementFeatureExtraction implements ElementFeatureExtraction {

	private final static Logger logger = Logger.getLogger(StopWordsBasedElementFeatureExtraction.class);
	
	public StopWordsBasedElementFeatureExtraction() {
		this(new HashSet<String>());
	}

	public StopWordsBasedElementFeatureExtraction(Set<String> stopWordsFeatures) {
		this.stopWordsFeatures = stopWordsFeatures;
	}
		
	public StopWordsBasedElementFeatureExtraction(ConfigurationParams params) throws IOException {
		this.stopWordsFeatures = new HashSet<String>();
		try {
			initStopWords(new File(params.get(Configuration.STOP_WORDS_FILE)));			
		} catch (ConfigurationException e) {
			logger.info("no stop-words file is defined");
		}
	}
	
	protected void initStopWords(File file) throws IOException {
		logger.info("reading stop words file: " + file.getAbsolutePath());
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String line = null;
		while ((line = reader.readLine())!=null)
			//stopWordsFeatures.add(line.split("\t")[0]);
			stopWordsFeatures.add(line.trim());
		logger.info(stopWordsFeatures.size() +" stop words were read");
		reader.close();
			
		
	}

	Set<String> stopWordsFeatures;
	
}
