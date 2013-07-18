package eu.excitementproject.eop.biutee.rteflow.macro;

import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.USE_MLE_FOR_INSERTION_COST;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.operations.updater.FeatureVectorUpdater;
import eu.excitementproject.eop.biutee.rteflow.micro.PathObservations;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructureOrganizer;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ConfidenceChainItem;
import eu.excitementproject.eop.transformations.operations.specifications.InsertNodeSpecification;
import eu.excitementproject.eop.transformations.operations.specifications.MoveNodeSpecification;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSpecification;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.operations.specifications.SubstituteNodeSpecificationMultiWord;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.InfoObservations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.UnigramProbabilityEstimation;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.AdvancedEqualities;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.Equalities;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.PathInTree;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;


/**
 * This class holds maps from feature-name to feature-index.
 * It has two maps: one for rule-bases features, and one for plug-in features.
 * <BR>
 * This class also contains a method that gets a feature-vector and a value to add
 * to one of its features, and creates a new feature-vector with the required
 * addition to the appropriate feature. The method is {@link #createAndUpdateFeatureVector(Map, int, double)}.
 * <P>
 * In addition, this class contains many methods that change a given feature-vector,
 * as follows:<BR>
 * Given a feature-vector of the original tree, and an operation that has been
 * performed on it to generate a new tree, this class contains methods to calculate
 * the new tree's feature vector.
 * <P>
 * Starting with version 2.4.0, this class's methods are called
 * by subclasses of {@link FeatureVectorUpdater}s.
 * 
 * @author Asher Stern
 * @since Jan 30, 2011
 *
 */
public class FeatureUpdate
{
	///////////////// PUBLIC /////////////////
	
	public FeatureUpdate(Set<String> pairLemmas, FeatureVectorStructureOrganizer featureVectorStructure, UnigramProbabilityEstimation unigramProbabilityEstimation) throws TeEngineMlException //  LinkedHashSet<String> ruleBasesNames, ImmutableList<String> customFeatures
	{
		this.pairLemmas = pairLemmas;
		
		// The map imMapRuleBaseNameToFeatureIndex will be as follows
		// (assuming the enum "Feature" has "n" elements):
		// 1 - the first feature in the enum "Feature".
		// 2 - the second feature in the enum "Feature".
		// ... etc.
		// n - the last feature in the enum "Feature".
		// n+1 - the first rule-base-name
		// n+2 - the second rule-base-name
		// ... etc.
		//
		// Then, the map imMapCustomFeatureToFeatureIndex is as follows:
		// Assuming that there are n built-in features, in the enum "Feature",
		// and k knowledge-resources (rule bases), then the features for
		// plug-ins start at n+k+1.
		// These features are specified by the plug-ins.
		
		this.imMapRuleBaseNameToFeatureIndex = featureVectorStructure.getRuleBasesFeatures();
		this.imMapCustomFeatureToFeatureIndex = featureVectorStructure.getPluginFeatures();
		
		this.unigramProbabilityEstimation = unigramProbabilityEstimation;
	}
	
	////////// Methods returning maps from feature-names to feature-indexes //////////

	
	/**
	 * Returns a map from feature-names to feature-indexes - for rule-base features.
	 * @return a map from feature-names to feature-indexes - for rule-base features.
	 */
	public ImmutableMap<String, Integer> getMapRuleBaseNameToFeatureIndex()
	{
		return imMapRuleBaseNameToFeatureIndex;
	}
	
	/**
	 * Returns a map from feature-names to feature-indexes - for plug-in features.
	 * @return a map from feature-names to feature-indexes - for plug-in features.
	 */
	public ImmutableMap<String, Integer> getMapCustomFeatureToFeatureIndex()
	{
		return imMapCustomFeatureToFeatureIndex;
	}
	
	/**
	 * A method that gets a feature-vector, and a value to add to one of the
	 * features, and creates a new feature-vector with the required addition to
	 * the appropriate feature.
	 * 
	 * @param originalFeatureVector
	 * @param featureIndex
	 * @param valueToAdd
	 * @return
	 * @throws TeEngineMlException
	 */
	public final Map<Integer,Double> createAndUpdateFeatureVector(Map<Integer,Double> originalFeatureVector, int featureIndex, double valueToAdd) throws TeEngineMlException
	{
		if (valueToAdd>=0) throw new TeEngineMlException("non-negative value added for feature: "+featureIndex+". valueToAdd is: "+String.format("%3.3f", valueToAdd));
		Map<Integer,Double> featureVector = new LinkedHashMap<Integer, Double>();
		featureVector.putAll(originalFeatureVector);
		double valueToSet = 0.0;
		if (null!=originalFeatureVector.get(featureIndex))
		{
			valueToSet = originalFeatureVector.get(featureIndex).doubleValue();
		}
		valueToSet += valueToAdd;
		featureVector.put(featureIndex, valueToSet);
		
		return featureVector;
	}

	

	////////// Methods that update a feature-vector //////////
	

	public Map<Integer,Double> forRuleWithConfidence(Map<Integer,Double> originalFeatureVector, String ruleBaseName, double confidence) throws TeEngineMlException
	{
		Map<Integer,Double> featureVector = new LinkedHashMap<Integer, Double>();
		featureVector.putAll(originalFeatureVector);
		if (!imMapRuleBaseNameToFeatureIndex.containsKey(ruleBaseName)) throw new TeEngineMlException("Rule base: "+ruleBaseName+" is unrecognized.");
		Integer featureIndex = imMapRuleBaseNameToFeatureIndex.get(ruleBaseName);
		if (null==featureIndex) throw new TeEngineMlException("Rule base: "+ruleBaseName+" has null index, which is an anomaly.");
		Double origValue = originalFeatureVector.get(featureIndex);
		if (null==origValue) origValue=0.0;
		double valueToAdd = Math.log(confidence);
		if (valueToAdd>=0)throw new TeEngineMlException("Non-negative value to feature value for feature: "+featureIndex+". Value is: "+String.format("%3.3f", valueToAdd) );
		Double newValue = origValue+valueToAdd;
		if (newValue>=0) throw new TeEngineMlException("Non-negative value to feature value for feature: "+featureIndex+". Value is: "+String.format("%3.3f", newValue) );
		featureVector.put(featureIndex,newValue);
		return featureVector;
	}

	public Map<Integer, Double> forChainOfRules(Map<Integer, Double> originalFeatureVector, ImmutableList<ConfidenceChainItem> chain) throws TeEngineMlException
	{
		Map<Integer, Double> featureVector = new LinkedHashMap<Integer, Double>();
		featureVector.putAll(originalFeatureVector);
		for (ConfidenceChainItem item : chain)
		{
			if (!imMapRuleBaseNameToFeatureIndex.containsKey(item.getRuleBaseName()))throw new TeEngineMlException("rule base: "+item.getRuleBaseName()+" does not exist.");
			Integer featureIndex = imMapRuleBaseNameToFeatureIndex.get(item.getRuleBaseName());
			if (null==featureIndex)throw new TeEngineMlException("rule base: "+item.getRuleBaseName()+" does not exist.");
			Double origValue = featureVector.get(featureIndex);
			if (null==origValue)origValue=0.0;
			double valueToAdd = Math.log(item.getConfidence());
			if (valueToAdd>=0)throw new TeEngineMlException("Non-negative value to feature value for feature: "+featureIndex+" of rule-base: "+item.getRuleBaseName()+". Value is: "+String.format("%3.3f", valueToAdd) );
			Double newValue = origValue+valueToAdd;
			if (newValue>=0) throw new TeEngineMlException("Non-negative value to feature value for feature: "+featureIndex+". Value is: "+String.format("%3.3f", newValue) );
			featureVector.put(featureIndex, newValue);
		}
		return featureVector;
	}
	
	public Map<Integer,Double> forInsert(InsertNodeSpecification insertSpec, Map<Integer,Double> originalFeatureVector) throws TeEngineMlException
	{
		Map<Integer,Double> featureVector = new LinkedHashMap<Integer, Double>();
		featureVector.putAll(originalFeatureVector);

		if (InfoObservations.infoHasLemma(insertSpec.getHypothesisNodeToInsert().getInfo()))
		{
			if (InfoObservations.insertOnlyLexModOfMultiWord(insertSpec.getHypothesisNodeToInsert().getInfo(), insertSpec.getTextNodeToBeParent().getInfo()))
			{
				// We don't have this in Easy-First parser. Minipar does have this
				// property of adding multiple nodes for the same multi-word expression,
				// one node for the multi-word-expression itself, and other nodes for
				// most of its words.
				insertSpec.addDescription("only lex-mode");
				// Do nothing. This operation costs 0.
			}
			else
			{
				String lemmaToInsert = InfoGetFields.getLemma(insertSpec.getHypothesisNodeToInsert().getInfo());
				double featureValue = -1.0;

				if (USE_MLE_FOR_INSERTION_COST)
				{
					List<String> wordsInLemma = StringUtil.stringToWords(lemmaToInsert);
					double logEstimation = 0.0;
					boolean foundEstimation = false;
					for (String word : wordsInLemma)
					{
						if (word.length()>0)
						{
							if (0==logEstimation)
							{
								logEstimation = Math.log(unigramProbabilityEstimation.getEstimationFor(word.toLowerCase()));
							}
							else
							{
								double currentWordLogEstimation = Math.log(unigramProbabilityEstimation.getEstimationFor(word.toLowerCase()));
								if (currentWordLogEstimation<logEstimation)
									logEstimation=currentWordLogEstimation;
							}
							foundEstimation = true;
						}
					}
					if (!foundEstimation)
						logEstimation = unigramProbabilityEstimation.getEstimationFor(lemmaToInsert.toLowerCase());

					featureValue = logEstimation;
				} // end of if(USE_MLE_FOR_INSERTION_COST)
				
				//double estimation = unigramProbabilityEstimation.getEstimationFor(lemmaToInsert);
				//double featureValue = Math.log(estimation);
				
				if (featureValue>=0)throw new TeEngineMlException(String.format("featureValue>=0! featureValue=%-4.4f",featureValue));
				insertSpec.addDescription("\""+lemmaToInsert+"\""+" costs "+String.format("%-3.4f", featureValue));
				
				
				boolean existInPair = false;
				if (StringUtil.setContainsIgnoreCase(pairLemmas, lemmaToInsert))
				{
					existInPair=true;
					insertSpec.setExistInPair(true);
				}
				else
				{
					existInPair=false;
				}
				
				

				boolean isNamedEntity = InfoObservations.infoIsNamedEntity(insertSpec.getHypothesisNodeToInsert().getInfo());
				if (isNamedEntity)
					insertSpec.addDescription("Named Entity");
				//boolean isNumber = InfoObservations.infoIsNumber(insertSpec.getHypothesisNodeToInsert().getInfo());

				boolean contentVerb = InfoObservations.infoIsContentVerb(insertSpec.getHypothesisNodeToInsert().getInfo());
				boolean contentWord = InfoObservations.infoIsContentWord(insertSpec.getHypothesisNodeToInsert().getInfo());

				if (existInPair)
				{
					if (isNamedEntity)
					{
						updateFeatureVector(featureVector, Feature.INSERT_NAMED_ENTITY_EXIST_IN_PAIR, featureValue);
					}
//					else if (isNumber)
//					{
//						updateFeatureVector(featureVector, Feature.INSERT_NUMBER_EXIST_IN_PAIR, featureValue);
//					}
					else if (contentVerb)
					{
						updateFeatureVector(featureVector, Feature.INSERT_CONTENT_VERB_EXIST_IN_PAIR, featureValue);
					}
					else if (contentWord)
					{
						updateFeatureVector(featureVector, Feature.INSERT_CONTENT_WORD_EXIST_IN_PAIR, featureValue);
					}
					else
					{
//						updateFeatureVector(featureVector, Feature.INSERT_NON_CONTENT_NON_EMPTY_WORD, featureValue);
						updateFeatureVector(featureVector, Feature.INSERT_NON_CONTENT_NON_EMPTY_WORD_EXIST_IN_PAIR, featureValue);
					}
				}
				else
				{
					if (isNamedEntity)
					{
						updateFeatureVector(featureVector, Feature.INSERT_NAMED_ENTITY, featureValue);
					}
//					else if (isNumber)
//					{
//						updateFeatureVector(featureVector, Feature.INSERT_NUMBER, featureValue);
//					}
					else if (contentVerb)
					{
						updateFeatureVector(featureVector, Feature.INSERT_CONTENT_VERB, featureValue);
					}
					else if (contentWord)
					{
						updateFeatureVector(featureVector, Feature.INSERT_CONTENT_WORD, featureValue);
					}
					else
					{
						updateFeatureVector(featureVector, Feature.INSERT_NON_CONTENT_NON_EMPTY_WORD, featureValue);
					}

				}

			}
		}
		else
		{
			updateFeatureVector(featureVector, Feature.INSERT_EMPTY_WORD, -1.0);
		}
		
		return featureVector;
		
		
	}
	
	
	public Map<Integer,Double> forMove(Map<Integer,Double> originalFeatureVector, PathInTree path, TreeAndParentMap<ExtendedInfo,ExtendedNode> textTreeAndParentMap, MoveNodeSpecification moveSpec, ExtendedNode theNodeInTheGenerated) throws TeEngineMlException
	{
		Map<Integer,Double> featureVector = new LinkedHashMap<Integer, Double>();
		featureVector.putAll(originalFeatureVector);

		double featureValue = -1.0;

		boolean updateForRootPTDone = false;
		if (BiuteeConstants.SPECIAL_FEATURE_FOR_MOVE_BASED_ON_PT && (TreeUtilities.isArtificialRoot(moveSpec.getTextNodeToBeParent())) && (ptPlusOrMinus(moveSpec.getTextNodeToMove().getInfo())) )
		{
			moveSpec.addDescription("predicate-truth justified");
			updateFeatureVector(featureVector, Feature.MOVE_TO_ROOT_IF_PT_PLUS_OR_MINUS, -1.0);
			updateForRootPTDone = true;
		}
		if (!updateForRootPTDone)
		{
			double length = lengthOfPathInTree(path);
			featureValue = 0.0-(2*length+1.0);
			if (featureValue>=0)throw new TeEngineMlException(String.format("featureValue>=0! featureValue=%-4.4f",featureValue));
			moveSpec.addDescription("costs "+String.format("%-3.2f", featureValue));
		}
		
		if (updateForRootPTDone)
		{
			// it is done
		}
		else if (PathObservations.moveChangeRelationStrong(moveSpec, path, textTreeAndParentMap))
		{
			updateFeatureVector(featureVector, Feature.MOVE_ONLY_CHANGE_RELATION_STRONG ,featureValue);
		}
		else if (PathObservations.introduceOnlySurfaceRelation(moveSpec))
		{
			updateFeatureVector(featureVector, Feature.MOVE_INTRODUCE_SURFACE_RELATION, featureValue);
		}
		else
		{
			boolean areAncestorsEqual = true;
			
			if (null==theNodeInTheGenerated)
			{
				areAncestorsEqual=true; // just default.
			}
			else
			{
				ExtendedInfo contentAncestorInGenerated = ExtendedInfoGetFields.getContentAncestor(theNodeInTheGenerated.getInfo());
				ExtendedInfo contentAncestorInOriginal = ExtendedInfoGetFields.getContentAncestor(moveSpec.getTextNodeToMove().getInfo());
				if ( (null==contentAncestorInGenerated) && (null==contentAncestorInOriginal) )
				{
					areAncestorsEqual=true;
				}
				else if ( (null==contentAncestorInGenerated) || (null==contentAncestorInOriginal) )
				{
					areAncestorsEqual=false;
				}
				else
				{
					if (Constants.USE_ADVANCED_EQUALITIES) // though, in this case, there is no difference between the advanced equalities to the "equalities", I follow the flag in Constants.
					{
						areAncestorsEqual = AdvancedEqualities.nodesSimilarContents(contentAncestorInGenerated, contentAncestorInOriginal);
					}
					else
					{
						areAncestorsEqual = Equalities.areEqualNodes(contentAncestorInGenerated, contentAncestorInOriginal);
					}
				}
			}
			if (areAncestorsEqual)
			{
				moveSpec.addDescription("same context");
				updateFeatureVector(featureVector, Feature.MOVE_NODE_SAME_CONTEXT, featureValue);
			}
			else
			{
				moveSpec.addDescription("change context");
				updateFeatureVector(featureVector, Feature.MOVE_NODE_CHANGE_CONTEXT, featureValue);
			}
		}
		
		return featureVector;
	}
	
	public Map<Integer,Double> forSubstitutionMultiWord(Map<Integer,Double> originalFeatureVector, SubstituteNodeSpecificationMultiWord spec) throws TeEngineMlException
	{
		Map<Integer,Double> featureVector = new LinkedHashMap<Integer, Double>();
		featureVector.putAll(originalFeatureVector);
		
		spec.addDescription("Multi-Word");

		Set<String> addedHypothesisWord = new HashSet<String>();
		for (String hypothesisWord : spec.getHypothesisWords())
		{
			if (!StringUtil.setContainsIgnoreCase(spec.getTextWords(), hypothesisWord))
			{
				addedHypothesisWord.add(hypothesisWord);
			}
		}
		
		return updateFeatureVectorForSubstitutionMultiWord(featureVector,spec,spec.getNewNodeInfo().getNamedEntityAnnotation()!=null,addedHypothesisWord);
	}
	
	

	/**
	 * Handling multi-word for cases in which the multi-word expression is
	 * spread over several nodes is done by representing the multi-word
	 * expression as a subtree being the right-hand-side of a rule.
	 * This rule does not represent a knowledge resource, but only the 
	 * substitution of a word by a multi-word expression.
	 * 
	 * @see InitializationTextTreesProcessor#createMultiWordNamedEntityRuleBase()
	 * 
	 * @param originalFeatureVector
	 * @param spec
	 * @return
	 * @throws TeEngineMlException
	 */
	public Map<Integer,Double> forSubstitutionMultiWordAsRule(Map<Integer,Double> originalFeatureVector, RuleSpecification spec) throws TeEngineMlException
	{
		Map<Integer,Double> featureVector = new LinkedHashMap<Integer, Double>();
		featureVector.putAll(originalFeatureVector);

		boolean namedEntity = false;
		Set<String> addedHypothesisWord = new HashSet<String>();
		String lhsLemma = InfoGetFields.getLemma(spec.getRule().getRule().getLeftHandSide().getInfo());
		for (AbstractNode<? extends Info, ?> rhsNode : TreeIterator.iterableTree(spec.getRule().getRule().getRightHandSide()))
		{
			String rhsLemma = InfoGetFields.getLemma(rhsNode.getInfo());
			if (!rhsLemma.equalsIgnoreCase(lhsLemma))
			{
				try{if (rhsNode.getInfo().getNodeInfo().getNamedEntityAnnotation()!=null)
					namedEntity=true;}
				catch(NullPointerException e){}
				addedHypothesisWord.add(rhsLemma);
			}
		}
		
		return updateFeatureVectorForSubstitutionMultiWord(featureVector,spec,namedEntity,addedHypothesisWord);
	}

	public Map<Integer,Double> byName(Map<Integer,Double> originalFeatureVector, String customFeatureName) throws TeEngineMlException
	{
		return byName(originalFeatureVector, customFeatureName, -1.0);
	}
	
	public Map<Integer,Double> byName(Map<Integer,Double> originalFeatureVector, String customFeatureName, double valueToAdd) throws TeEngineMlException
	{
		Map<Integer,Double> featureVector = new LinkedHashMap<Integer, Double>();
		featureVector.putAll(originalFeatureVector);
		Integer customfeatureIndex = imMapCustomFeatureToFeatureIndex.get(customFeatureName);
		updateFeatureVector(featureVector, customfeatureIndex, valueToAdd);
		return featureVector;
	}
	

	public Map<Integer,Double> forSubstitutionFlipPos(Map<Integer,Double> originalFeatureVector) throws TeEngineMlException
	{
		Map<Integer,Double> featureVector = new LinkedHashMap<Integer, Double>();
		featureVector.putAll(originalFeatureVector);
		updateFeatureVector(featureVector, Feature.SUBSTITUTION_FLIP_POS, -1.0);
		return featureVector;
	}
	

	public Map<Integer,Double> forSubstitutionParserAntecedent(Map<Integer,Double> originalFeatureVector) throws TeEngineMlException
	{
		Map<Integer,Double> featureVector = new LinkedHashMap<Integer, Double>();
		featureVector.putAll(originalFeatureVector);
		updateFeatureVector(featureVector, Feature.SUBSTITUTION_PARSER_ANTECEDENT, -1.0);
		return featureVector;
	}
	
	public Map<Integer, Double> forSubstitutionCoreference(Map<Integer,Double> originalFeatureVector) throws TeEngineMlException
	{
		Map<Integer,Double> featureVector = new LinkedHashMap<Integer, Double>();
		featureVector.putAll(originalFeatureVector);
		updateFeatureVector(featureVector, Feature.SUBSTITUTION_COREFERENCE, -1.0);
		return featureVector;
	}
	
	public Map<Integer,Double> forChangePredicateTruth(Map<Integer,Double> originalFeatureVector) throws TeEngineMlException
	{
		Map<Integer,Double> featureVector = new LinkedHashMap<Integer, Double>();
		featureVector.putAll(originalFeatureVector);
		updateFeatureVector(featureVector, Feature.CHANGE_PREDICATE_TRUTH, -1.0);
		return featureVector;
	}

	public final static void updateFeatureVector(Map<Integer,Double> featureVector, Feature feature, double valueToAdd) throws TeEngineMlException
	{
		if (valueToAdd>=0) throw new TeEngineMlException("non-negative value added for feature: "+feature.name()+". valueToAdd is: "+String.format("%3.3f", valueToAdd));
		updateFeatureVector(featureVector, feature.getFeatureIndex(), valueToAdd);
	}

	public final static void updateFeatureVector(Map<Integer,Double> featureVector, Integer featureIndex, double valueToAdd) throws TeEngineMlException
	{
		if (valueToAdd>=0) throw new TeEngineMlException("non-negative value added for feature with index: "+featureIndex+". valueToAdd is: "+String.format("%3.3f", valueToAdd));
		featureVector.put(featureIndex, featureVector.get(featureIndex)+valueToAdd);
	}

	
	
	///////////////// PRIVATE /////////////////

	
	private  Map<Integer,Double> updateFeatureVectorForSubstitutionMultiWord(Map<Integer,Double> copiedOriginalFeatureVector, Specification spec, boolean namedEntity, Set<String> addedHypothesisWord) throws TeEngineMlException
	{
		if (addedHypothesisWord.size()==0)
		{
			spec.addDescription("remove words");
			updateFeatureVector(copiedOriginalFeatureVector, Feature.SUBSTITUTION_MULTI_WORD_REMOVE_WORDS, -1.0);
		}
		else
		{
			double featureValue = -1.0;
			if (USE_MLE_FOR_INSERTION_COST)
			{
				double logEstimationAllWords = 0;
				for (String hypothesisWord : addedHypothesisWord)
				{
					double currentWordLogEstimation = Math.log(unigramProbabilityEstimation.getEstimationFor(hypothesisWord));
					if (currentWordLogEstimation<logEstimationAllWords)
						logEstimationAllWords = currentWordLogEstimation;
				}
				featureValue=logEstimationAllWords;
			}
			
			if (featureValue>=0)throw new TeEngineMlException(String.format("featureValue>=0! featureValue=%-4.4f",featureValue));
			
			// spec.addDescription("feature value = "+String.format("%-2.3f", featureValue));
			
			if (namedEntity)
			{
				spec.addDescription("add named entity, with cost "+String.format("%-3.4f",featureValue));
				updateFeatureVector(copiedOriginalFeatureVector, Feature.SUBSTITUTION_MULTI_WORD_ADD_WORDS_NAMED_ENTITY, featureValue);
			}
			else
			{
				spec.addDescription("add words with cost "+String.format("%-3.4f",featureValue));
				updateFeatureVector(copiedOriginalFeatureVector, Feature.SUBSTITUTION_MULTI_WORD_ADD_WORDS, featureValue);
			}
		}
		
		return copiedOriginalFeatureVector;
		
	}

	
	
	
	private static double lengthOfPathInTree(PathInTree pathInTree)
	{
		int ret = 0;
		ret += pathInTree.getDownNodes().size();
		ret += pathInTree.getUpNodes().size();
		if ( (pathInTree.getLeastCommonAncestor()!=pathInTree.getFrom())
			&&
			(pathInTree.getLeastCommonAncestor()!=pathInTree.getTo())
			)
		{
			ret += 1;
		}
		return (double)ret;
	}
	
	

	/**
	 * Returns true if predicate-truth is specified, and is + or -.
	 * @param info
	 * @return true if predicate-truth is specified, and is + or -.
	 */
	private static boolean ptPlusOrMinus(ExtendedInfo info)
	{
		boolean ret = false;
		PredTruth predicateTruth = ExtendedInfoGetFields.getPredTruthObj(info);
		if (predicateTruth!=null)
		{
			if (predicateTruth.equals(PredTruth.P) || predicateTruth.equals(PredTruth.P))
			{
				ret = true;
			}
		}
		return ret;
	}


	private Set<String> pairLemmas;
	private ImmutableMap<String, Integer> imMapRuleBaseNameToFeatureIndex = null;
	private ImmutableMap<String, Integer> imMapCustomFeatureToFeatureIndex;
	private UnigramProbabilityEstimation unigramProbabilityEstimation = null;
	
}
