/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.domains.FilterType;
import eu.excitementproject.eop.distsim.domains.RuleDirection;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.scoring.ElementSimilarityMeasure;
import eu.excitementproject.eop.distsim.scoring.ElementsSimilarityMeasure;
import eu.excitementproject.eop.distsim.scoring.FeatureScore;
import eu.excitementproject.eop.distsim.scoring.SimilarityMeasure;
import eu.excitementproject.eop.distsim.scoring.similarity.ElementSimilarityScoring;
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
			PersistentBasicMap<LinkedHashMap<Integer,Double>> leftElemntSimilarities,
			PersistentBasicMap<LinkedHashMap<Integer,Double>> rightElemntSimilarities,
			PatternBasedCountableIdentifiableStorage<Element> elementStorage,
			String resourceName) {
		this.leftElemntSimilarities = leftElemntSimilarities;
		this.rightElemntSimilarities = rightElemntSimilarities;
		this.elementStorage = elementStorage;
		this.resourceName = resourceName;
	}
	
	public DefaultSimilarityStorage(PatternBasedCountableIdentifiableStorage<Element> elementStorage, String resourceName, PersistenceDevice... devices) throws LoadingStateException {
		this.elementStorage = elementStorage;
		this.resourceName = resourceName;
		loadState(devices);
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.SimilarityStorage#getSimilarityMeasure(org.excitement.distsim.items.Element, org.excitement.distsim.items.Element)
	 */
	@Override
	public SimilarityMeasure getSimilarityMeasure(Element leftElement,
			Element rightElement) throws SimilarityNotFoundException {
		try {
			int leftElementId = elementStorage.getId(leftElement);
			LinkedHashMap<Integer, Double> rightElements = leftElemntSimilarities.get(leftElementId);
			if (rightElements == null)
				throw new SimilarityNotFoundException();
			int rightElementId = elementStorage.getId(rightElement);
			Double score = rightElements.get(rightElementId);
			if (score ==  null)
				throw new SimilarityNotFoundException();
			return new DefaultSimilarityMeasure(score,null);
		} catch (Exception e) {
			throw new SimilarityNotFoundException(e);
		}
	}

	
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.SimilarityStorage#getSimilarityMeasure(org.excitement.distsim.items.Element, org.excitement.distsim.items.Element, org.excitement.distsim.storage.ElementFeatureScoreStorage)
	 */
	@Override
	public SimilarityMeasure getSimilarityMeasure(Element leftElement,
			Element rightElement,
			ElementFeatureScoreStorage elementFeatureScores,
			ElementSimilarityScoring elementSimilarityScoring)
			throws SimilarityNotFoundException {
		try {
			int leftElementId = elementStorage.getId(leftElement);
			int rightElementId = elementStorage.getId(rightElement);
			LinkedHashMap<Integer, Double> rightElements = leftElemntSimilarities.get(leftElementId);
			if (rightElements == null)
				return measureSimilarity(leftElementId,rightElementId,elementFeatureScores,elementSimilarityScoring);
			Double score = rightElements.get(rightElementId);
			if (score ==  null)
				return measureSimilarity(leftElementId,rightElementId,elementFeatureScores,elementSimilarityScoring);
			return new DefaultSimilarityMeasure(score,null);
		} catch (Exception e) {
			throw new SimilarityNotFoundException(e);
		}
	}

	protected SimilarityMeasure measureSimilarity(int leftElementId,
			int rightElementId,
			ElementFeatureScoreStorage elementFeatureScores,
			ElementSimilarityScoring elementSimilarityScoring) 
				throws SimilarityNotFoundException {
		ImmutableIterator<FeatureScore> leftFeatures;
		ImmutableIterator<FeatureScore> rightFeatures;
		try {
			leftFeatures = elementFeatureScores.getElementFeatureScores(leftElementId);
			rightFeatures = elementFeatureScores.getElementFeatureScores(rightElementId);
		} catch (NoScoreFoundException e) {
			throw new SimilarityNotFoundException(e);
		}
		double score = elementSimilarityScoring.getSimilarity(leftFeatures, rightFeatures);
		return new DefaultSimilarityMeasure(score,null);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.SimilarityStorage#getSimilarityMeasure(org.excitement.distsim.items.Element, org.excitement.distsim.domains.RuleDirection)
	 */
	@Override
	public List<ElementSimilarityMeasure> getSimilarityMeasure(Element element,RuleDirection ruleDirection) throws SimilarityNotFoundException {
		return getSimilarityMeasure(element,ruleDirection,null, null,FilterType.ALL,0.0);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.SimilarityStorage#getSimilarityMeasure(org.excitement.distsim.items.Element, org.excitement.distsim.domains.RuleDirection, org.excitement.distsim.storage.ElementFeatureScoreStorage)
	 */
	@Override
	public List<ElementSimilarityMeasure> getSimilarityMeasure(Element element,
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
	public List<ElementSimilarityMeasure> getSimilarityMeasure(Element element,
			RuleDirection ruleDirection, FilterType filterType, double filterVal) throws SimilarityNotFoundException {
		return getSimilarityMeasure(element,ruleDirection,null, null,filterType,filterVal);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.SimilarityStorage#getSimilarityMeasure(org.excitement.distsim.items.Element, org.excitement.distsim.domains.RuleDirection, org.excitement.distsim.storage.ElementFeatureScoreStorage, org.excitement.distsim.domains.FilterType, double)
	 */
	@Override
	public List<ElementSimilarityMeasure> getSimilarityMeasure(Element element,
			RuleDirection ruleDirection,
			ElementFeatureScoreStorage elementFeatureScores,
			ElementSimilarityScoring elementSimilarityScoring,
			FilterType filterType, double filterVal) throws SimilarityNotFoundException {
		int elementId;
		try {
			elementId = elementStorage.getId(element);
		} catch (Exception e) {
			throw new SimilarityNotFoundException(e);
		}
		return getSimilarityMeasure(elementId,ruleDirection,
				elementFeatureScores,elementSimilarityScoring,
				filterType,filterVal);		
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.SimilarityStorage#getSimilarityMeasure(java.lang.String, org.excitement.distsim.domains.RuleDirection)
	 */
	@Override
	public List<ElementsSimilarityMeasure> getSimilarityMeasure(String elementRegExp, RuleDirection ruleDirection) throws SimilarityNotFoundException {
		return getSimilarityMeasure(elementRegExp, ruleDirection, FilterType.ALL, 0.0);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.SimilarityStorage#getSimilarityMeasure(java.lang.String, org.excitement.distsim.domains.RuleDirection, org.excitement.distsim.domains.FilterType, double)
	 */
	@Override
	public List<ElementsSimilarityMeasure> getSimilarityMeasure(
			String elementRegExp, RuleDirection ruleDirection,
			FilterType filterType, double filterVal)  throws SimilarityNotFoundException {
		try {
			List<ElementsSimilarityMeasure> tmp = new LinkedList<ElementsSimilarityMeasure>();
			
			//debug
			//System.out.println("regexp: " + elementRegExp);
			
			for (Integer elementId : elementStorage.getItemIds(elementRegExp)) {
				LinkedHashMap<Integer, Double> elementSimilarities = (ruleDirection == RuleDirection.LEFT_TO_RIGHT ?
					leftElemntSimilarities.get(elementId)
					:
					rightElemntSimilarities.get(elementId));
				Element element = elementStorage.getData(elementId);
				for (Entry<Integer, Double> entry : elementSimilarities.entrySet())
					tmp.add(new DefaultElementsSimilarityMeasure(element,elementStorage.getData(entry.getKey()),entry.getValue(),null));
			}
			SortUtil.sortSimilarityRules(tmp, true);
			int i=0;
			List<ElementsSimilarityMeasure> ret = new LinkedList<ElementsSimilarityMeasure>();
			for (ElementsSimilarityMeasure similarityRule :  tmp) {
				if (!filtered(filterType,filterVal,similarityRule.getSimilarityMeasure(),tmp.size(),i)) {
					ret.add(new DefaultElementsSimilarityMeasure(similarityRule.getLeftElement(),similarityRule.getRightElement(),similarityRule.getSimilarityMeasure(),similarityRule.getAdditionalInfo()));
					i++;
				} else
					break;
			}

			return ret;
		} catch (Exception e) {
			throw new SimilarityNotFoundException(e); 
		}			
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.storage.SimilarityStorage#getSimilarityMeasure(eu.excitementproject.eop.distsim.items.Element, eu.excitementproject.eop.distsim.items.Element)
	 */
	@Override
	public List<ElementsSimilarityMeasure> getSimilarityMeasure(String leftRegExp, String rightRegExp) throws SimilarityNotFoundException {
		return getSimilarityMeasure(leftRegExp, rightRegExp, FilterType.ALL, 0.0);
	} 

	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.storage.SimilarityStorage#getSimilarityMeasure(eu.excitementproject.eop.distsim.items.Element, eu.excitementproject.eop.distsim.items.Element)
	 */
	@Override
	public List<ElementsSimilarityMeasure> getSimilarityMeasure(String leftRegExp, String rightRegExp, FilterType filterType, double filterVal) throws SimilarityNotFoundException {
		try {
			List<ElementsSimilarityMeasure> tmp = new LinkedList<ElementsSimilarityMeasure>();
			Set<Integer> lefts = elementStorage.getItemIds(leftRegExp);
			Set<Integer> rights = elementStorage.getItemIds(rightRegExp);
			
			for (Integer  leftId : lefts) {
				Element left = elementStorage.getData(leftId);
				LinkedHashMap<Integer, Double> rightElements = leftElemntSimilarities.get(left.getID());
				if (rightElements != null) {
					for (Integer  rightId : rights) {
						Element right = elementStorage.getData(rightId);
						Double score = rightElements.get(rightId);
						if (score !=  null)
							tmp.add(new DefaultElementsSimilarityMeasure(left,right, score,null));
					}
				}
			}
			SortUtil.sortSimilarityRules(tmp, true);
			int i=0;
			List<ElementsSimilarityMeasure> ret = new LinkedList<ElementsSimilarityMeasure>();
			for (ElementsSimilarityMeasure similarityRule :  tmp) {
				if (!filtered(filterType,filterVal,similarityRule.getSimilarityMeasure(),tmp.size(),i)) {
					ret.add(new DefaultElementsSimilarityMeasure(similarityRule.getLeftElement(),similarityRule.getRightElement(),similarityRule.getSimilarityMeasure(),similarityRule.getAdditionalInfo()));
					i++;
				} else
					break;
			}
			return ret;

		} catch (Exception e) {
			throw new SimilarityNotFoundException(e);
		}
	} 

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.storage.SimilarityStorage#getResourceName()
	 */
	@Override
	public String getResourceName() {
		return resourceName;
	}


	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.Persistence#saveState(org.excitement.distsim.storage.PersistenceDevice[])
	 * 
	 * 	 Assumption: two persistence devices are provided:
	 *   1. left-right similarities
	 *   2. right-left similarities
	 */
	@Override
	public void saveState(PersistenceDevice... devices) throws SavingStateException {
		if (devices.length != 2)
			throw new SavingStateException(devices.length + " persistence devices was providied for saving, where two are expected");
		leftElemntSimilarities.saveState(devices[0]);
		rightElemntSimilarities.saveState(devices[1]);		
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.Persistence#loadState(org.excitement.distsim.storage.PersistenceDevice[])
	 * 
	 * 	 Assumption: two persistence devices are provided:
	 *   1. left-right similarities
	 *   2. right-left similarities
	 */
	@Override
	public void loadState(PersistenceDevice... devices) throws LoadingStateException {
		if (devices.length != 2)
			throw new LoadingStateException(devices.length + " persistence devices was providied for loading, where two are expected");		
		leftElemntSimilarities.loadState(devices[0]);
		rightElemntSimilarities.loadState(devices[1]);		
	}
	
	
	protected List<ElementSimilarityMeasure> getSimilarityMeasure(
			int elementId,
			RuleDirection ruleDirection,
			ElementFeatureScoreStorage elementFeatureScores,
			ElementSimilarityScoring elementSimilarityScoring,
			FilterType filterType, double filterVal) 
			throws SimilarityNotFoundException {
		List<ElementSimilarityMeasure> ret = new LinkedList<ElementSimilarityMeasure>();
		try {
			LinkedHashMap<Integer, Double> elementSimilarities;
			if (ruleDirection == RuleDirection.LEFT_TO_RIGHT)
				elementSimilarities = leftElemntSimilarities.get(elementId);
			else
				elementSimilarities = rightElemntSimilarities.get(elementId);
			int i=0;
			for (Entry<Integer,Double> entry : elementSimilarities.entrySet()) {
				try {
					if (!filtered(filterType,filterVal,entry.getValue(),elementSimilarities.size(),i)) {
						ret.add(new DefaultElementSimilarityMeasure(elementStorage.getData(entry.getKey()),entry.getValue(),null));
						i++;
					} else
						break;
				} catch (Exception e)  {
					logger.error(e.toString());
				}
			}			
		} catch (Exception e) {
			throw new SimilarityNotFoundException(e);
			//@TOCHECK:
			
			/*if (elementFeatureScores != null && elementSimilarityScoring != null) {
				if (ruleDirection == RuleDirection.LEFT_TO_RIGHT)
					throw new SimilarityNotFoundException(e);
				
				List<ElementIdSimilarityMeasure> elementSimilarities = elementSimilarityScoring.getSimilaritiesOfElement(elementId, elementFeatureScores, null, ruleDirection);
				int i=0;
				double count=0;
				for (ElementIdSimilarityMeasure elementIdSimilarityMeasure : elementSimilarities) {
					try {
						if (!filtered(filterType,filterVal,count,elementSimilarities.size(),i)) {
							ret.add(new DefaultElementSimilarityMeasure(elementStorage.getData(elementIdSimilarityMeasure.getElementID()),elementIdSimilarityMeasure.getSimilarityMeasure(),null));
							count += elementIdSimilarityMeasure.getSimilarityMeasure(); 
							i++;
						}
					} catch (Exception e1)  {
						logger.error(e1.toString());
					}
				}
			} else
				throw new SimilarityNotFoundException(e);	*/
		}
		return ret;
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
	
	// Assumption: The similar elements each left-element are ordered descendingly by their similarity measures
	protected PersistentBasicMap<LinkedHashMap<Integer,Double>> leftElemntSimilarities;
	
	// Assumption: The similar elements of each right-element are ordered descendingly by their similarity measures	
	protected PersistentBasicMap<LinkedHashMap<Integer,Double>> rightElemntSimilarities;
	
	protected final PatternBasedCountableIdentifiableStorage<Element> elementStorage;
	
	protected final String resourceName;


}
