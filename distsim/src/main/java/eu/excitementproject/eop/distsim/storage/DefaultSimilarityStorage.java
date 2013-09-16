/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.domains.FilterType;
import eu.excitementproject.eop.distsim.domains.RuleDirection;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.scoring.ElementsSimilarityMeasure;
import eu.excitementproject.eop.distsim.scoring.similarity.ElementSimilarityScoring;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.SortUtil;

/**
 * A general implementation of the {@link SimilarityStorage} interface, based on a given left and right similarity storages, and an element storage
 * 
 * @author Meni Adler
 * @since 09/09/2012
 *
 */
public class DefaultSimilarityStorage implements SimilarityStorage {
	
	private static final Logger logger = Logger.getLogger(DefaultSimilarityStorage.class);

	// Assumption: The similar elements for each element, of leftElemntSimilarities and rightElemntSimilarities parameters, 
    //             are ordered descendingly by their similarity measures
	public DefaultSimilarityStorage(
			RedisBasedStringListBasicMap leftElemntSimilarities,
			RedisBasedStringListBasicMap rightElemntSimilarities,
			String resourceName,
			String instanceName,
			String elementClassName) {
		this.leftElemntSimilarities = leftElemntSimilarities;
		this.rightElemntSimilarities = rightElemntSimilarities;
		this.resourceName = resourceName;
		this.instanceName = instanceName;
		this.elementClassName = elementClassName;
	}

	/**
	 * Construct from configuration params.
	 * @param params contain at least the following: <ul>
	 * <li>elementStorageHost, elementStoragePort
	 * <li>l2rRulesStorageHost, l2rRulesStoragePort
	 * <li>r2lRulesStorageHost, r2lRulesStoragePort
	 * </ul>
	 * @throws ConfigurationException 
	 * 
	 */
	public DefaultSimilarityStorage(ConfigurationParams params) throws ConfigurationException {
		this.leftElemntSimilarities = new RedisBasedStringListBasicMap(params.getString(Configuration.L2R_REDIS_HOST), params.getInt(Configuration.L2R_REDIS_PORT));
		try {
			this.rightElemntSimilarities = new RedisBasedStringListBasicMap(params.getString(Configuration.R2L_REDIS_HOST), params.getInt(Configuration.R2L_REDIS_PORT));
		} catch (ConfigurationException e) {
			this.rightElemntSimilarities = null;
		}
		this.resourceName = params.getString(Configuration.RESOURCE_NAME);
		this.instanceName = params.getConfigurationFile().toString();
		this.elementClassName = params.get(Configuration.ELEMENT_CLASS);
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.SimilarityStorage#getSimilarityMeasure(org.excitement.distsim.items.Element, org.excitement.distsim.items.Element)
	 */
	@Override
	public List<ElementsSimilarityMeasure> getSimilarityMeasure(Element leftElement, Element rightElement) throws SimilarityNotFoundException {
		List<ElementsSimilarityMeasure> ret = new LinkedList<ElementsSimilarityMeasure>();
		try {
			Set<String> leftElementKeys = leftElement.toKeys();
			Set<String> rightElementKeys = rightElement.toKeys();
			
			for (String leftElementKey : leftElementKeys) {
				Element element1 = (Element) Class.forName(elementClassName).newInstance(); 
				element1.fromKey(leftElementKey);
				for (String rightElementKey : rightElementKeys) {
					Element element2 = (Element) Class.forName(elementClassName).newInstance(); 
					element2.fromKey(rightElementKey);
	
					String score = leftElemntSimilarities.get(leftElementKey,rightElementKey);
					if (score !=  null)
						ret.add(new DefaultElementsSimilarityMeasure(element1, element2,Double.parseDouble(score),null));
				}
			}
			
			if (leftElementKeys.size() > 1 && rightElementKeys.size() > 1)
				SortUtil.sortSimilarityRules(ret, true);
			return ret;
		} catch (Exception e) {
			throw new SimilarityNotFoundException(e);
		}
		
	}

	
	

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.SimilarityStorage#getSimilarityMeasure(org.excitement.distsim.items.Element, org.excitement.distsim.domains.RuleDirection)
	 */
	@Override
	public List<ElementsSimilarityMeasure> getSimilarityMeasure(Element element,RuleDirection ruleDirection) throws SimilarityNotFoundException {
		return getSimilarityMeasure(element,ruleDirection,null, null,FilterType.ALL,0.0);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.SimilarityStorage#getSimilarityMeasure(org.excitement.distsim.items.Element, org.excitement.distsim.domains.RuleDirection, org.excitement.distsim.storage.ElementFeatureScoreStorage)
	 */
	@Override
	public List<ElementsSimilarityMeasure> getSimilarityMeasure(Element element,
			RuleDirection ruleDirection,
			ElementFeatureScoreStorage elementFeatureScores,
			ElementSimilarityScoring elementSimilarityScoring) 
			throws SimilarityNotFoundException {
		return getSimilarityMeasure(element,ruleDirection,elementFeatureScores, elementSimilarityScoring,FilterType.ALL,0.0);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.SimilarityStorage#getSimilarityMeasure(org.excitement.distsim.items.Element, org.excitement.distsim.domains.RuleDirection, org.excitement.distsim.domains.FilterType, double)
	 */
	@Override
	public List<ElementsSimilarityMeasure> getSimilarityMeasure(Element element,
			RuleDirection ruleDirection, FilterType filterType, double filterVal) throws SimilarityNotFoundException {
		return getSimilarityMeasure(element,ruleDirection,null, null,filterType,filterVal);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.SimilarityStorage#getSimilarityMeasure(org.excitement.distsim.items.Element, org.excitement.distsim.domains.RuleDirection, org.excitement.distsim.storage.ElementFeatureScoreStorage, org.excitement.distsim.domains.FilterType, double)
	 */
	@Override
	public List<ElementsSimilarityMeasure> getSimilarityMeasure(Element element,
			RuleDirection ruleDirection,
			ElementFeatureScoreStorage elementFeatureScores,
			ElementSimilarityScoring elementSimilarityScoring,
			FilterType filterType, double filterVal) throws SimilarityNotFoundException {
		
		List<ElementsSimilarityMeasure> ret = new LinkedList<ElementsSimilarityMeasure>();	
		try {	
			List<ElementsSimilarityMeasure> tmp = new LinkedList<ElementsSimilarityMeasure>();
			       
			Set<String> element1Keys = element.toKeys();
			
			for (String element1Key : element1Keys) {
				
				Element element1 = (Element) Class.forName(elementClassName).newInstance(); 
				element1.fromKey(element1Key);
				
				List<String> elementSimilarities;
				if (ruleDirection == RuleDirection.LEFT_TO_RIGHT)
					elementSimilarities = leftElemntSimilarities.getTopN(element1Key,(filterType == FilterType.TOP_N ? (int)filterVal : -1));
				else
					elementSimilarities = rightElemntSimilarities.getTopN(element1Key,(filterType == FilterType.TOP_N ? (int)filterVal : -1));

				if (elementSimilarities == null) {
					logger.warn("No entry was found for key " + element1Key);
				} else {
					int i=0;
					for (String elementSimilarity : elementSimilarities) {
						
						//tmp
						//System.out.println(elementSimilarity);
						
						String[] toks = elementSimilarity.split(RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER);
						String element2Key = toks[0];
						Element element2 = (Element) Class.forName(elementClassName).newInstance(); 
						element2.fromKey(element2Key);
						double score = Double.parseDouble(toks[1]);

						tmp.add(ruleDirection == RuleDirection.LEFT_TO_RIGHT ? 
								new DefaultElementsSimilarityMeasure(element1, element2,score,null)
								: 
								new DefaultElementsSimilarityMeasure(element2, element1,score,null)
							);
						if (filterType != FilterType.TOP_N && filtered(filterType,filterVal,score,elementSimilarities.size(),i))
							break;
						i++;
					}							
				}					
			}
			
			if (element1Keys.size() > 1) {
				SortUtil.sortSimilarityRules(tmp, true); 			
				int i=0;
				for (ElementsSimilarityMeasure similarityRule :  tmp) {
					if (!filtered(filterType,filterVal,similarityRule.getSimilarityMeasure(),tmp.size(),i)) 
						ret.add(new DefaultElementsSimilarityMeasure(similarityRule.getLeftElement(),similarityRule.getRightElement(),similarityRule.getSimilarityMeasure(),similarityRule.getAdditionalInfo()));
					else
						break;
					i++;
				}
			} else
				ret = tmp;
			return ret;
		} catch (Exception e) {
			throw new SimilarityNotFoundException(e); 
		}			
	}



	protected boolean filtered(final FilterType type, final double filterVal, final double val, final int size, final int i) {
		switch (type) {
			case MIN_VAL:
				return val < filterVal;
			case TOP_N:
				return i > filterVal;
			case TOP_PRECENT:
				return i > size * filterVal;
			default:
				return false;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.component.Component#getComponentName()
	 */
	@Override
	public String getComponentName() {		
		return resourceName;
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.component.Component#getInstanceName()
	 */
	@Override
	public String getInstanceName() {
		return instanceName;
	}

	
	// Assumption: The similar elements each left-element are ordered descendingly by their similarity measures
	protected RedisBasedStringListBasicMap leftElemntSimilarities;	
	// Assumption: The similar elements of each right-element are ordered descendingly by their similarity measures	
	protected RedisBasedStringListBasicMap rightElemntSimilarities;
	protected String elementClassName;
	protected final String resourceName;
	protected final String instanceName;
}
