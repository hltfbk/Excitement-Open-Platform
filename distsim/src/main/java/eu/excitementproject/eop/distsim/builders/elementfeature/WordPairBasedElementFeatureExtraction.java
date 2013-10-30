/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.elementfeature;

import java.io.IOException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.StringBasedElement;
import eu.excitementproject.eop.distsim.items.StringBasedFeature;
import eu.excitementproject.eop.distsim.items.StringBasedTextUnit;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Pair;
//import org.apache.log4j.Logger;


/**
 * Given a co-occurrence of two lexical items, each composed of a string word (and their dependency relation) 
 * extracts two element-feature pairs where the element is the one word and the feature is the other word <b>without the dependency relation</b>
 *
 * @author Meni Adler
 * @since 04/09/2012
 *
 */
public class WordPairBasedElementFeatureExtraction extends IrelevantListBasedElementFeatureExtraction {

	//private final static Logger logger = Logger.getLogger(LemmaPosBasedElementFeatureExtraction.class);
		
	public WordPairBasedElementFeatureExtraction(int minCount) {
		this(new HashSet<String>(),minCount);
	}

	public WordPairBasedElementFeatureExtraction(Set<String> stopWordsFeatures, int minCount) {
		super(stopWordsFeatures);
		this.minCount = minCount;
	}
	
	public WordPairBasedElementFeatureExtraction(ConfigurationParams params) throws ConfigurationException, IOException {
		super(params);
		try {
			this.minCount = params.getInt(Configuration.MIN_COUNT);
		} catch (ConfigurationException e) {
			this.minCount = 0;
		}
	}
		
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.elementfeature.ElementFeatureExtractor#extractElementsFeatures(org.excitement.distsim.items.Cooccurrence)
	 */
	@Override
	public List<Pair<Element, Feature>> extractElementsFeature(Cooccurrence<?> cooccurrence) throws ElementFeatureExtractionException {
		
		List<Pair<Element, Feature>> ret = new LinkedList<Pair<Element, Feature>>();		
		
		try {
			StringBasedTextUnit word1 = (StringBasedTextUnit)cooccurrence.getTextItem1();
			StringBasedTextUnit word2 = (StringBasedTextUnit)cooccurrence.getTextItem2();

			if (word1.getCount() >= minCount && !isStopWordFeature(cooccurrence.getTextItem2())) {
				ret.add(new Pair<Element, Feature>(					
					new StringBasedElement(word1.getData()),
					new StringBasedFeature(word2.getData())								
					));
				}
			
			if (word2.getCount() >= minCount && !isStopWordFeature(cooccurrence.getTextItem1())) {
				ret.add(new Pair<Element, Feature>(					
						new StringBasedElement(word2.getData()),
						new StringBasedFeature(word1.getData())
									
						));
			}
			return ret;
		} catch (Exception e) {
			throw new ElementFeatureExtractionException(e);
		}
	}
	
	protected boolean isStopWordFeature(TextUnit feature) {
		return stopWordsFeatures.contains(feature.getData().toString());
	}


	protected int minCount;
	
}
