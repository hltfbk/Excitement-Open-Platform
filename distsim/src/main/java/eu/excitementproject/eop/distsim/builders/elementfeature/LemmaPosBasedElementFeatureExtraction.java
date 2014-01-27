/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.elementfeature;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.LemmaPosBasedElement;
import eu.excitementproject.eop.distsim.items.LemmaPosFeature;
import eu.excitementproject.eop.distsim.items.LemmaPosTextUnit;
import eu.excitementproject.eop.distsim.items.RelationBasedLemmaPosFeature;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Filter;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SetBasedPOSFilter;
//import org.apache.log4j.Logger;


/**
 * Given a co-occurrence of two lexical items, each composed of lemma and pos, and their dependency relation 
 * extracts two element-feature pairs where the element is the one word and the feature is the other word, with or without the dependency relation
 * and the same for opposite order 
 *
 * @author Meni Adler
 * @since 04/09/2012
 *
 */
public class LemmaPosBasedElementFeatureExtraction extends IrelevantListBasedElementFeatureExtraction {

	//private final static Logger logger = Logger.getLogger(LemmaPosBasedElementFeatureExtraction.class);
	protected static final String OPPOSITE_SIGN ="@R@";
		
	public LemmaPosBasedElementFeatureExtraction(boolean bIncludeDependencyRelation, int minCount) {
		this(bIncludeDependencyRelation, new HashSet<String>(),minCount);
	}

	public LemmaPosBasedElementFeatureExtraction(boolean bIncludeDependencyRelation, Set<String> stopWordsFeatures, int minCount) {
		super(stopWordsFeatures);
		this.bIncludeDependencyRelation = bIncludeDependencyRelation;
		this.posFilter = new SetBasedPOSFilter();
		this.minCount = minCount;
	}

	public LemmaPosBasedElementFeatureExtraction(boolean bIncludeDependencyRelation, Set<String> stopWordsFeatures,int minCount, CanonicalPosTag... relevantPOSs) {
		super(stopWordsFeatures);
		this.bIncludeDependencyRelation = bIncludeDependencyRelation;
		this.posFilter = new SetBasedPOSFilter(relevantPOSs);
		this.minCount = minCount;
	}
		
	public LemmaPosBasedElementFeatureExtraction(ConfigurationParams params) throws ConfigurationException, IOException {
		super(params);
		this.bIncludeDependencyRelation = params.getBoolean(Configuration.INCLUDE_DEPENDENCY_RELATION);
		try {
			this.minCount = params.getInt(Configuration.MIN_COUNT);
		} catch (ConfigurationException e) {
			this.minCount = 0;
		}
		this.posFilter = new SetBasedPOSFilter(params);
	}
		
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.elementfeature.ElementFeatureExtractor#extractElementsFeatures(org.excitement.distsim.items.Cooccurrence)
	 */
	@Override
	public List<Pair<Element, Feature>> extractElementsFeature(Cooccurrence<?> cooccurrence) throws ElementFeatureExtractionException {
		
		List<Pair<Element, Feature>> ret = new LinkedList<Pair<Element, Feature>>();		
		
		try {
			LemmaPosTextUnit word1 = (LemmaPosTextUnit)cooccurrence.getTextItem1(); //.copy();
			LemmaPosTextUnit word2 = (LemmaPosTextUnit)cooccurrence.getTextItem2(); //.copy();
			String rel = (String)cooccurrence.getRelation().getValue();

			if (posFilter.isRelevant(word1.getData().getPOS()) && word1.getCount() >= minCount && !isStopWordFeature(cooccurrence.getTextItem2())) {
				ret.add(new Pair<Element, Feature>(					
					new LemmaPosBasedElement(word1.getData()),
					(bIncludeDependencyRelation ?
							new RelationBasedLemmaPosFeature(rel,word2.getData()) :
							new LemmaPosFeature(word2.getData()))
								
					));
				}
			
			if (posFilter.isRelevant(word2.getData().getPOS()) && word2.getCount() >= minCount && !isStopWordFeature(cooccurrence.getTextItem1())) {
				//opposite - to check
				ret.add(new Pair<Element, Feature>(					
						new LemmaPosBasedElement(word2.getData()),
						(bIncludeDependencyRelation ?
								new RelationBasedLemmaPosFeature(rel+ OPPOSITE_SIGN,word1.getData()) :
								new LemmaPosFeature(word1.getData()))
									
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


	protected boolean bIncludeDependencyRelation;
	protected Filter<CanonicalPosTag> posFilter;
	protected int minCount;
	
}
