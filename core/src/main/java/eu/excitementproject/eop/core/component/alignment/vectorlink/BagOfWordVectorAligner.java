package eu.excitementproject.eop.core.component.alignment.vectorlink;

import java.io.IOException;
import java.util.Set;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ConfigurationException;


/**
 * Alignment of words in T and H using word vectors trained on Google News
 * Corpus model trained using word2vec
 * 
 * @author Madhumita
 * @since July 2015
 *
 */
public class BagOfWordVectorAligner extends VectorAligner {

	public BagOfWordVectorAligner(CommonConfig config, boolean removeStopWords, Set<String> stopWords)
			throws ConfigurationException, IOException {
		super(config, removeStopWords, stopWords, "BagOfWordVectorScoring","tokenWord");
	}
	
	@Override
	public String getComponentName() {
		return ("BagOfWordVectorAligner");
	}
	
}
