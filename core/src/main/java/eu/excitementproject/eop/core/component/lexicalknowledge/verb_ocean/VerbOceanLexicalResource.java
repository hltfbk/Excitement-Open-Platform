package eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean;

import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.datastructures.Pair;
import eu.excitementproject.eop.common.datastructures.PairMap;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResourceNothingToClose;


/**
 * A {@link LexicalResource} wrapping VerbOcean. See article. Each retrieved {@link LexicalRule} comes with a {@link VerbOceanRuleInfo} containing a 
 * confidence score and a VerbOcean {@link RelationType}. The main part of the rule contains the String name of the relation and the default dummy score 
 * {@link LexicalRule#DEFAULT_CONFIDENCE}. 
 * Each pair of verbs may have up to one rule, even if it has several relation in VerbOcean. 
 * <p>
 * Almost all the work is performed in the constructor, when the entire VerbOcean file is loaded, filtered and mapped. 
 * <p>   
 * Verb Ocean only deals with verbs, so if the {@link PartOfSpeech} parameter is different that VERB or null, they will return an empty list.
 * <p>
 * <b>NOTE!</b> As usual, in all the retrieved rules, the left verb is always the entailing, and the right verb is the entailed, regardless of
 * the directionality of the relation. For instance, even though in the relation "accept [happens-before] abandon" <i>accept</i> come before <i>abandon</i>, 
 * you'll get <i>accept</i> on the right of the rule, 
 * and <i>abandon</i> on the left, because <code>happens-before</code> is a reversed order relation. <br>
 * To know if the relation is <i>conceptually</i> entailing left-to-right or entailed right-to-left, or bidirectional, check {@link RelationType#isEntailing()}.    
 * <p>
 * <b>NOTE!</b> Strictly speaking VerbOcean is not an entailment resource, and does not contain entailment rules per say. The user may interpret some of the verb 
 * relations in it as entailment rules (mainly {@link RelationType#STRONGER_THAN}), <b>at her own risk</b>. Consult the article for guidelines.
 * <p> 
 * <b>NOTE!</b> FYI The way VerbOcean relations are filtered into lexical rules here creates certain inconsistencies. For instance, let V1 relate to V2 with a leftToRight 
 * relation1,
 * and let V2 relate to V1 with a rightToLeft relation2, and V1-->V2 has a higher score. From the VO article's perspective, it makes sense to filter out V2-->V1, and 
 * that's what this class does. But, from a lexical resource's point of view, it makes sense to treat the two separately, because they have different 
 * directions. 
 * 
 * @author Amnon Lotan
 *
 * @since Dec 24, 2011
 * @see	http://www.patrickpantel.com/download/papers/2004/emnlp04.pdf
 */
public class VerbOceanLexicalResource extends LexicalResourceNothingToClose<VerbOceanRuleInfo> {

	public static final String PARAM_SCORE_THRESHOLD="threshold";
	public static final String PARAM_FILE="file";
	public static final String PARAM_ALLOWED_RELATIONS="allowedRelations";

	/**
	 * Rules created with these relations are always filtered out. 
	 */
	public static final RelationType[] FORBIDDEN_RELATION_TYPES = new RelationType[]{ RelationType.UNKNOWN, RelationType.LOW_VOL};

	private static final String RESOURCE_NAME = "VerbOcean";
	private static final Comparator<? super LexicalRule<? extends VerbOceanRuleInfo>> SCORE_COMPARATOR = 
			new Comparator<LexicalRule<? extends VerbOceanRuleInfo>>() {

				@Override
				public int compare(LexicalRule<? extends VerbOceanRuleInfo> rule1, LexicalRule<? extends VerbOceanRuleInfo> rule2) {
					return rule1.getConfidence() > rule2.getConfidence() ? 1 : -1;
				}
	}; 

	private final PartOfSpeech VERB; 

	// state fields
	private final Map<EntailmentPair, LexicalRule<? extends VerbOceanRuleInfo>> mapRulesByEntailmentPair = new LinkedHashMap<EntailmentPair, LexicalRule<? extends VerbOceanRuleInfo>>();
	private final Map<String, List<LexicalRule<? extends VerbOceanRuleInfo>>> mapRulesByEntailingVerb = new LinkedHashMap<String, List<LexicalRule<? extends VerbOceanRuleInfo>>>();
	private final Map<String, List<LexicalRule<? extends VerbOceanRuleInfo>>> mapRulesByEntailedVerb = new LinkedHashMap<String, List<LexicalRule<? extends VerbOceanRuleInfo>>>();

	double maxScore = 0;
	
	/**
	 * Ctor read and map all rules from the given verb ocean file, but, keep only rules with allowed relation types, 
	 * and, for each verb pair, keep only the highest scoring rule. The rules are then mapped.
	 * 
	 * @param scoreThreshold	rules with thresholds not higher than this will be screened
	 * @param verbOceanRelationsFile	e.g. Data\RESOURCES\VerbOcean\verbocean.unrefined.2004-05-20.txt
	 * @param allowedRelationTypes	only rules with these relations will be returned. others will be screened. If they contain any of the 
	 * {@link #FORBIDDEN_RELATION_TYPES}, a LexicalResourceException is thrown. Cannot be null, can be empty. 
	 * @throws LexicalResourceException
	 */
	public VerbOceanLexicalResource(double scoreThreshold, File verbOceanRelationsFile, Set<RelationType> allowedRelationTypes) throws LexicalResourceException {
		if (scoreThreshold <= 0)
			throw new LexicalResourceException("the score threshold must be positive. I got " + scoreThreshold);
		if (verbOceanRelationsFile == null)
			throw new LexicalResourceException("got null relations file");
		if (!verbOceanRelationsFile.exists())
			throw new LexicalResourceException(verbOceanRelationsFile + " doesn't exist");
		if (allowedRelationTypes == null)
			throw new LexicalResourceException("allowedRelationTypes  is null");
		for (RelationType forbiddenRelationType : FORBIDDEN_RELATION_TYPES)
			if (allowedRelationTypes.contains(forbiddenRelationType))
				throw new LexicalResourceException("The given allowed relation types set "+allowedRelationTypes+" contains a forbidden relation type " + forbiddenRelationType);
				
		try {		VERB = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.VERB);	}
		catch (UnsupportedPosTagStringException e) {	throw new LexicalResourceException("Internal error", e);	}
		
		PairMap<String, LexicalRule<? extends VerbOceanRuleInfo>> mapRulesByUnorderedPair = new PairMap<String, LexicalRule<? extends VerbOceanRuleInfo>>();
		Set<Pair<String>> verbPairs = new LinkedHashSet<Pair<String>>();
		
		// read and map all rules, but, keep only rules with allowed relation types, and, for each verb pair, keep only the highest scoring rule 
		try {
			BufferedReader reader = new BufferedReader(new FileReader(verbOceanRelationsFile));
			
			String line;
			while((line = reader.readLine()) != null)
			{
				if(line.length() != 0 && line.charAt(0) != '#')	// skip empty and commented lines
				{
					String[] parts = line.split(" ");
					RelationType relationType = RelationType.parse(parts[1]);
					double score = Double.parseDouble(parts[4]);
					if (allowedRelationTypes.contains(relationType) &&  score > scoreThreshold)	// screen out unallowed relation types and low scores
					{
						String leftVerb = parts[0];
						String rightVerb = parts[2];
						Pair<String> verbPair = new Pair<String>(leftVerb, rightVerb);
						
						LexicalRule<? extends VerbOceanRuleInfo> comparedRule = mapRulesByUnorderedPair.getValueOf(verbPair);
						if (comparedRule == null || score > comparedRule.getConfidence())	// if there is a better rule for the same verb pair, skip this rule
							mapRulesByUnorderedPair.put(verbPair, makeRule(leftVerb, rightVerb, score, relationType));
						
						if (comparedRule == null)
							verbPairs.add(verbPair);
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			throw new LexicalResourceException("file not found: " + verbOceanRelationsFile, e);
		} catch (IOException e) {
			throw new LexicalResourceException("IO error reading: " + verbOceanRelationsFile, e);
		}

		// fill up the one sided rule maps
		fillTheRuleMaps(mapRulesByUnorderedPair, verbPairs);
	}

	/**
	 * Copy Ctor - quicker!
	 */
	public VerbOceanLexicalResource( VerbOceanLexicalResource otherVerbOceanLexicalResource ) {
		VERB = otherVerbOceanLexicalResource.VERB;
		mapRulesByEntailedVerb.putAll(otherVerbOceanLexicalResource.mapRulesByEntailedVerb);
		mapRulesByEntailingVerb.putAll(otherVerbOceanLexicalResource.mapRulesByEntailingVerb);
		mapRulesByEntailmentPair.putAll(otherVerbOceanLexicalResource.mapRulesByEntailmentPair);
	}
	
	public VerbOceanLexicalResource(ConfigurationParams params) throws LexicalResourceException, ConfigurationException
	{
		this(
				params.getDouble(PARAM_SCORE_THRESHOLD),
				params.getFile(PARAM_FILE),
				params.getEnumSet(RelationType.class, PARAM_ALLOWED_RELATIONS));
	}
	/////////////////////////////////////////////////////////////// PUBLIC	////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexicalResource#getRulesForRight(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends VerbOceanRuleInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		if (lemma == null)
			throw new LexicalResourceException("lemma is null");
		List<LexicalRule<? extends VerbOceanRuleInfo>> rules = new Vector<LexicalRule<? extends VerbOceanRuleInfo>>(); 
		if((pos==null || simplerPos(pos.getCanonicalPosTag()) == SimplerCanonicalPosTag.VERB) &&
				mapRulesByEntailedVerb.containsKey(lemma)){
			rules.addAll(mapRulesByEntailedVerb.get(lemma));
		}
		return new ArrayList<LexicalRule<? extends VerbOceanRuleInfo>>(rules);
	}
	
//	/**
//	 * Return all rules with <code>lemma</code> on the right, that also have one of the given <code>relationTypes</code>
//	 * @param lemma
//	 * @param relationTypes
//	 * @return
//	 * @throws LexicalResourceException
//	 */
//	public List<LexicalRule<? extends VerbOceanRuleInfo>> getRulesForRight( String lemma, Set<RelationType> relationTypes) throws LexicalResourceException {
//		return filterByRelationTypes(getRulesForRight(lemma, VERB), relationTypes);
//	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexicalResource#getRulesForLeft(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends VerbOceanRuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		if (lemma == null)
			throw new LexicalResourceException("lemma is null");
		List<LexicalRule<? extends VerbOceanRuleInfo>> rules = new Vector<LexicalRule<? extends VerbOceanRuleInfo>>(); 
		if((pos==null || simplerPos(pos.getCanonicalPosTag()) == SimplerCanonicalPosTag.VERB) &&
				mapRulesByEntailingVerb.containsKey(lemma)){
			rules.addAll(mapRulesByEntailingVerb.get(lemma));
		}
		return new ArrayList<LexicalRule<? extends VerbOceanRuleInfo>>(rules);
	}
	
//	/**
//	 * Return all rules with <code>lemma</code> on the left, that also have one of the given <code>relationTypes</code>
//	 * @param lemma
//	 * @param relationTypes
//	 * @return
//	 * @throws LexicalResourceException
//	 */
//	public List<LexicalRule<? extends VerbOceanRuleInfo>> getRulesForLeft( String lemma, Set<RelationType> relationTypes) throws LexicalResourceException {
//		return filterByRelationTypes(getRulesForLeft(lemma, VERB), relationTypes);
//	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexicalResource#getRules(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech, java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends VerbOceanRuleInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos) 
			throws LexicalResourceException {

		if (leftLemma == null)
			throw new LexicalResourceException("left lemma is null");
		if (rightLemma == null)
			throw new LexicalResourceException("right lemma is null");
		List<LexicalRule<? extends VerbOceanRuleInfo>> rules = new ArrayList<LexicalRule<? extends VerbOceanRuleInfo>>();
		if((leftPos==null || simplerPos(leftPos.getCanonicalPosTag()) == SimplerCanonicalPosTag.VERB) &&
				(rightPos==null || simplerPos(rightPos.getCanonicalPosTag()) == SimplerCanonicalPosTag.VERB)){
			LexicalRule<? extends VerbOceanRuleInfo> rule = mapRulesByEntailmentPair.get(new EntailmentPair(leftLemma, rightLemma));
			if(rule != null){
				rules.add(rule);
			}
		}
		return rules; 
	}
	
//	/**
//	 * Return all rules with <code>lemma</code> on the left, that also have one of the given <code>relationTypes</code>
//	 * @param lemma
//	 * @param relationTypes
//	 * @return
//	 * @throws LexicalResourceException
//	 */
//	public List<LexicalRule<? extends VerbOceanRuleInfo>> getRules( String leftLemma, String rightLemma, Set<RelationType> relationTypes) 
//			throws LexicalResourceException {
//		return filterByRelationTypes(getRules(leftLemma, VERB, rightLemma, VERB), relationTypes);
//	}

	/////////////////////////////////////////////////////////////// PARIVATE	////////////////////////////////////////////////////////////
	
//	/**
//	 * @param rulesToFilter
//	 * @param relationTypes
//	 * @return
//	 * @throws LexicalResourceException 
//	 */
//	private List<LexicalRule<? extends VerbOceanRuleInfo>> filterByRelationTypes(List<LexicalRule<? extends VerbOceanRuleInfo>> rulesToFilter, 
//			Set<RelationType> relationTypes) throws LexicalResourceException {
//		if (relationTypes == null )
//			throw new LexicalResourceException("got null relationTypes");
//		if (relationTypes.isEmpty())
//			throw new LexicalResourceException("relationTypes is empty");
//		
//		List<LexicalRule<? extends VerbOceanRuleInfo>> ret = new ArrayList<LexicalRule<? extends VerbOceanRuleInfo>>();
//		for (LexicalRule<? extends VerbOceanRuleInfo> rule : rulesToFilter)
//			if (relationTypes.contains(rule.getInfo().getRelationType()))
//				ret.add(rule);
//		return ret;
//	}

	/**
	 * Fill up the one sided rule maps
	 * @param mapRulesByUnorderedPair
	 * @param verbPairs
	 * @throws LexicalResourceException 
	 */
	private void fillTheRuleMaps(PairMap<String, LexicalRule<? extends VerbOceanRuleInfo>> mapRulesByUnorderedPair,	Set<Pair<String>> verbPairs)
			throws LexicalResourceException 
	{
		for (Pair<String> verbPair :  verbPairs)
		{
			LexicalRule<? extends VerbOceanRuleInfo> rule = mapRulesByUnorderedPair.getValueOf(verbPair);
			addToMappedList(mapRulesByEntailingVerb, rule.getLLemma(), rule);
			addToMappedList(mapRulesByEntailedVerb, rule.getRLemma(), rule);
			
			mapRulesByEntailmentPair.put(new EntailmentPair(rule.getLLemma(), rule.getRLemma()), rule);
			
			if (rule.getInfo().getRelationType().isBidirectional())			// bidirectional rules are symmetrically duplicated
			{
				LexicalRule<VerbOceanRuleInfo> inverseRule = invertRule(rule);
				addToMappedList(mapRulesByEntailedVerb, inverseRule.getRLemma(), inverseRule);
				mapRulesByEntailmentPair.put(new EntailmentPair(inverseRule.getLLemma(), inverseRule.getRLemma()), inverseRule);
			}
		}
		
		// sort each little list according to score
		sortMappedLists(mapRulesByEntailingVerb);
		sortMappedLists(mapRulesByEntailedVerb);
	}

	/**
	 * @param rule
	 * @return
	 * @throws LexicalResourceException 
	 */
	private LexicalRule<VerbOceanRuleInfo> invertRule(LexicalRule<? extends VerbOceanRuleInfo> rule) throws LexicalResourceException 
	{
		return new LexicalRule<VerbOceanRuleInfo>(rule.getRLemma(), VERB, rule.getLLemma(), VERB, rule.getConfidence(), rule.getRelation(), 
				RESOURCE_NAME, rule.getInfo());
	}

	/**
	 * Make a rule out of the params. Note that if the relation is bidirectional, it represents two symmetrical rules, but only one is returned 
	 * @param leftVerb
	 * @param rightVerb
	 * @param score
	 * @param relationType
	 * @return
	 * @throws LexicalResourceException 
	 */
	private LexicalRule<? extends VerbOceanRuleInfo> makeRule(String leftVerb, String rightVerb, double score, RelationType relationType)
			throws LexicalResourceException {
		if (score > maxScore)
			maxScore  = score;
		return relationType.isEntailing() ? 
				new LexicalRule<VerbOceanRuleInfo>(leftVerb, VERB, rightVerb, VERB, relationType.name(), RESOURCE_NAME, 
						new VerbOceanRuleInfo(relationType, score))
				:
				new LexicalRule<VerbOceanRuleInfo>(rightVerb, VERB, leftVerb, VERB, relationType.name(), RESOURCE_NAME, 
						new VerbOceanRuleInfo(relationType, score));
	}
	
	/**
	 * @param mapRulesByVerb
	 */
	private void sortMappedLists(Map<String, List<LexicalRule<? extends VerbOceanRuleInfo>>> mapRulesByVerb) {

		for ( List<LexicalRule<? extends VerbOceanRuleInfo>> rules : mapRulesByVerb.values())
			Collections.sort(rules, SCORE_COMPARATOR);
	}

	/**
	 * @param mapRulesByVerb
	 * @param verb
	 * @param rule
	 */
	private void addToMappedList(Map<String, List<LexicalRule<? extends VerbOceanRuleInfo>>> mapRulesByVerb, String verb,
			LexicalRule<? extends VerbOceanRuleInfo> rule) {
		List<LexicalRule<? extends VerbOceanRuleInfo>> rulesList = mapRulesByVerb.get(verb);
		if (rulesList == null)
		{
			rulesList = new ArrayList<LexicalRule<? extends VerbOceanRuleInfo>>();
			mapRulesByVerb.put(verb, rulesList);
		}
		rulesList.add(rule);
	}
}

