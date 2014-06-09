package eu.excitementproject.eop.transformations.operations.rules.lexicalchain.graphbased;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.Vector;
//
//import org.BIU.utils.Serializer;
//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;
//
//import ac.biu.nlp.inference.lexical.graph.LexicalSensedNode;
//import ac.biu.nlp.inference.lexical.integration.LemmaCanonicalPosSense;
//import ac.biu.nlp.lexical.inference.general.LexicalSensedRule;
//import ac.biu.nlp.lexical.inference.general.THPwithEntailmentProb;
//import ac.biu.nlp.lexical.inference.general.lexicalchains.InferenceChain;
//import ac.biu.nlp.models.LexicalModelException;
//import ac.biu.nlp.models.PLM;
//import ac.biu.nlp.models.PLMFactory;
//import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
//import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.LexicalRuleWithName;
//import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
//import ac.biu.nlp.nlp.general.configuration.ConfigurationException;
//import ac.biu.nlp.nlp.general.configuration.ConfigurationFile;
//import ac.biu.nlp.nlp.general.configuration.ConfigurationFileDuplicateKeyException;
//import ac.biu.nlp.nlp.general.file.FileUtils;
//import ac.biu.nlp.nlp.general.immutable.ImmutableList;
//import ac.biu.nlp.nlp.general.immutable.ImmutableListWrapper;
//import ac.biu.nlp.nlp.general.immutable.ImmutableSetWrapper;
//import ac.biu.nlp.nlp.instrumentscombination.TokenInfo;
import eu.excitementproject.eop.transformations.datastructures.LemmaAndPos;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfLexicalRules;

/**
 * A wrapper for {@link PLM}, a probabilistic lexical model, which provides 
 * transitive {@link ChainOfLexicalRules} with inference probabilities.   
 * 
 * @author Eyal Shnarch
 * @since 20/12/2012
 */

public class PlisRuleBase extends ByLemmaPosLexicalRuleBase<ChainOfLexicalRules> {
	
	public PlisRuleBase(ConfigurationParams plisParams) throws RuleBaseException
	{
		throw new RuntimeException("Disabled temporarily during migration");
	}
	public void setHypothesis(List<LemmaAndPos> iHypoWords) throws RuleBaseException
	{
		throw new RuntimeException("Disabled temporarily during migration");
	}
	public ImmutableSet<ChainOfLexicalRules> getRules(String lhsLemma, PartOfSpeech lhsPos) throws RuleBaseException
	{
		throw new RuntimeException("Disabled temporarily during migration");
	}
	public Set<String> getRuleBasesNames()
	{
		throw new RuntimeException("Disabled temporarily during migration");
	}
	
//	
//	public PlisRuleBase(ConfigurationParams plisParams) throws RuleBaseException{
//		try {
//			//load model's parameters
//			modelParams = loadModelParam(plisParams.getFile("learnt model"));
//			m_resourcesInUse = plisParams.getStringList("labels of resources and relations in use");
//			//chose inference model
//			String modelType = plisParams.get("model type");
//			inferModel = PLMFactory.create(modelType, plisParams, m_resourcesInUse);
//			m_serDirPath = buildSerPath(plisParams);
//			m_lhsChainsMap = new LinkedHashMap<LemmaAndPos, ImmutableSet<ChainOfLexicalRules>>();
//		} catch (Exception e) {
//			throw new RuleBaseException("Error initizlizing LexicalChainRuleBase", e);
//		} 
//	}
//
//
//	/**
//	 * Sets the current hypothesis to work with.<br> 
//	 * All {@link ChainOfLexicalRules} that entail the words 
//	 * 	or multi-words expressions of this hypothesis are constructed and stored for a fast retrieval by 
//	 * 	the {@link #getRules(String, PartOfSpeech)} function.<br><br>
//	 * Note: The chains construction process takes a lot of time. Therefore, once done for a specific 
//	 * 	hypothesis and a specific configuration of lexical resources the chains are serialized and saved
//	 * 	(to the path defined in the {@link ConfigurationParams} given to the constructor. 
//	 *   If no path is given, the function will not do serialization).<br> 
//	 * The next time this function would be called with the same hypothesis and resources configuration, the 
//	 * 	chains would be immediately retrieved from the serialized file.<br>
//	 * In order to force rebuilt of the chains for this hypothesis, delete its serialization file.<br>
//	 * 
//	 * @param iHypoWords - the terms of the current hypothesis. Stop words will be filtered out.
//	 * @return - a filtered list of the input hypothesis words (removing punctuation and stop words)
//	 * @throws RuleBaseException
//	 * @throws FileNotFoundException
//	 * @throws IOException
//	 * @throws ClassNotFoundException
//	 */
//	public void setHypothesis(List<LemmaAndPos> iHypoWords) throws RuleBaseException{
//		String hypoKey = extractKey(iHypoWords);
//		logger.info("setHypothesis("+hypoKey+")");
//		try {	
//			m_hypoIdMap = loadSerializedMap();
//			if(m_hypoIdMap.keySet().contains(hypoKey)){
//				m_lhsChainsMap = loadFromSerialization(m_hypoIdMap.get(hypoKey)); 
//			}else{
//				logger.info("a serializaed file was not found for hypothesis "+hypoKey);
//				m_lhsChainsMap.clear();
//				List<LemmaCanonicalPosSense> hypothesisTerms = convertTerms(iHypoWords);
////				logger.info("[PlisRuleBase]\t setHypothesis-1 before buildTowardsHypothesis");
//				inferModel.buildTowardsHypothesis(hypothesisTerms);
////				logger.info("[PlisRuleBase]\t setHypothesis-2 before extractRules");
//				THPwithEntailmentProb<LexicalSensedNode, InferenceChain> hypoRules = inferModel.extractRules(null, hypothesisTerms);
//				//apply the model specific policy regarding uncovered hypothesis terms 
//				inferModel.addUncoveredRules(hypoRules);	//TODO: is it needed?
//				//use the parameter set of a trained probabilistic model
//				inferModel.addProbabilities(modelParams, hypoRules);
//				//record in the serialization file
////				logger.info("[PlisRuleBase]\t setHypothesis-3 before recordRulesForHypoTerms");
//				recordRulesForHypoTerms(hypoRules);
//				int hypoId;
////				logger.info("[PlisRuleBase]\t setHypothesis-4 before sync m_nextId");
//				synchronized (m_nextId) {
//					int nextId = findMaxId(m_hypoIdMap);
//					if (m_nextId.getValue() < nextId){
//						m_nextId.setValue(nextId);
//					}
//					m_nextId.setValue(m_nextId.getValue()+1);
//					hypoId = m_nextId.getValue();
//				}
////				logger.info("[PlisRuleBase]\t setHypothesis-5 after  sync m_nextId");
//				serializeHypo(hypoId);
//				m_hypoIdMap.put(hypoKey, hypoId);
//				serializeHypoIdMap();
//			}
//		} catch (FileNotFoundException e) {
//			throw new RuleBaseException("Nested exception while seting hypothesis "+iHypoWords, e);
//		} catch (IOException e) {
//			throw new RuleBaseException("Nested exception while seting hypothesis "+iHypoWords, e);
//		} catch (ClassNotFoundException e) {
//			throw new RuleBaseException("Nested exception while seting hypothesis "+iHypoWords, e);
//		} catch (LexicalModelException e) {
//			throw new RuleBaseException("Nested exception while seting hypothesis "+iHypoWords, e);
//		} catch (TeEngineMlException e) {
//			throw new RuleBaseException("Nested exception while seting hypothesis "+iHypoWords, e);
//		}
//	}
//
//	
//	/* (non-Javadoc)
//	 * @see ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBase#getRules(String, PartOfSpeech)
//	 * must not return NULL! 
//	 */
//	public ImmutableSet<ChainOfLexicalRules> getRules(String lhsLemma, PartOfSpeech lhsPos) throws RuleBaseException {
//		ImmutableSet<ChainOfLexicalRules> rules;
//		try {
//			LemmaAndPos lhs = new LemmaAndPos(lhsLemma, new UnspecifiedPartOfSpeech(simplerPos(lhsPos.getCanonicalPosTag())));
//			if (m_lhsChainsMap.containsKey(lhs))
//				rules = m_lhsChainsMap.get(lhs);
//			else
//				rules = new ImmutableSetWrapper<ChainOfLexicalRules>(null);
//		} catch (TeEngineMlException e) {
//			throw new RuleBaseException("Nested exception while creating LemmaAndPos out of the input "+lhsLemma+" "+lhsPos,e);
//		} catch (UnsupportedPosTagStringException e) {
//			throw new RuleBaseException("Nested exception while creating UnspecifiedPartOfSpeech out of "+lhsPos,e);
//		}
////		logger.info("[PlisRuleBase] for t: "+lhsLemma+":"+lhsPos+" found the following rules: "+rules);
//		return rules;
//	}
//
//	
//	public Set<String> getRuleBasesNames(){
//		return new HashSet<String>(m_resourcesInUse);
//	}
//	
//	
//	///////////////////////////// private methods ///////////////////////////////////
//	
//	private Map<String, Double> loadModelParam(File paramInit) throws IOException, ConfigurationException {
//		final String COMMENT_MARK = "#";
//		final String EOF = "======";
//		Map<String, Double> parameters = new LinkedHashMap<String, Double>();
//		List<String> lines = FileUtils.loadFileToList(paramInit);
//		for(String line : lines){
//			if(line.startsWith(EOF)){
//				break;
//			}
//			if(line.startsWith(COMMENT_MARK) ||	line.isEmpty()){
//				continue;
//			}
//			String[] splittedLine = line.split("\t");
//			parameters.put(splittedLine[0], Double.parseDouble(splittedLine[1]));
//		}
//		return parameters;
//	}
//	
//	private String extractKey(List<LemmaAndPos> hypo) {
//		List<String> key = new Vector<String>();
//		for(LemmaAndPos h : hypo){
//			key.add(h.getLemma()+":"+h.getPartOfSpeech()+" ");
//		}
//		Collections.sort(key);
//		return key.toString();
//	}
//	
//	private int findMaxId(HashMap<String, Integer> m_hypoIdMap) {
//		int maxId = 0;
//		for(String hypo : m_hypoIdMap.keySet()){
//			if(m_hypoIdMap.get(hypo) > maxId){
//				maxId = m_hypoIdMap.get(hypo);
//			}
//		}
//		return maxId;
//	}
//	
//	private List<LemmaCanonicalPosSense> convertTerms(List<LemmaAndPos> hypoWords) throws LexicalModelException {
//		List<TokenInfo> tokens = new ArrayList<TokenInfo>(hypoWords.size());
//		for(LemmaAndPos hypoWord : hypoWords){
//			TokenInfo ti = new TokenInfo(hypoWord.getLemma());
//			ti.setPosTag(hypoWord.getPartOfSpeech());
//			tokens.add(ti);
//		}
//		return inferModel.convert(tokens);
//	}	
//	
//	/*
//	 * The single steps in a chain will not have confidence (i.e. 0.5). The PLIS probability is given only to the complete chain.
//	 * This supports the dual operation mode:
//	 * - LEXICAL_CHAIN_BY_GRAPH: in which BiuTee estimates resources reliability
//	 * - PLIS_GRAPH: in which BiuTee estimates only one resource, PLIS, and utilizes the probability it reports 
//	 *   for its inference chains (i.e. rules).
//	 * That can be changed in function convertInferenceChainToLexicalChain.
//	 */
//	private void recordRulesForHypoTerms(THPwithEntailmentProb<LexicalSensedNode, InferenceChain> hypoRules) throws TeEngineMlException, LexicalModelException {
//		for(LexicalSensedNode hypoTerm : hypoRules.getHypothesis()){
//			for(InferenceChain inferChain : hypoRules.getAlignments(hypoTerm)){
//				LemmaAndPos lhs = new LemmaAndPos(inferChain.getLhs().getLemma(), inferChain.getLhs().getPartOfSpeech());
//				ImmutableList<LexicalRuleWithName> lexChain = convertInferenceChainToLexicalChain(inferChain);
//				ChainOfLexicalRules ruleChain = new ChainOfLexicalRules(lhs.getLemma(), lhs.getPartOfSpeech(), 
//						inferChain.getRhs().getLemma(), inferChain.getRhs().getPartOfSpeech(), inferChain.getEntailmentProb(), lexChain);
//				Set<ChainOfLexicalRules> set;
//				if(m_lhsChainsMap.containsKey(lhs)){
//					set = m_lhsChainsMap.get(lhs).getMutableSetCopy();
//				}else{
//					set = new HashSet<ChainOfLexicalRules>();
//				}
//				set.add(ruleChain);
////				logger.info("[PlisRuleBase] for h: "+hypoTerm+" found the following rules: "+set);
//				m_lhsChainsMap.put(lhs, new ImmutableSetWrapper<ChainOfLexicalRules>(set));
//			}
//		}
//	}
//	
//	private ImmutableList<LexicalRuleWithName> convertInferenceChainToLexicalChain(InferenceChain inferChain) throws LexicalModelException {
//		final double DEFAULT_CONFIDENCE = 0.5;
//		ArrayList<LexicalRuleWithName> ruleList = new ArrayList<LexicalRuleWithName>();
//		for(LexicalSensedRule r : inferChain.getChain()){
//			if(!modelParams.containsKey(r.getRelation().toUpperCase())){
//				throw new LexicalModelException("label "+r.getRelation().toUpperCase()+" was not found in "+modelParams);
//			}
//			LexicalRule rule = new LexicalRule(r.getLhs().getLemma(), r.getLhs().getPartOfSpeech(),
//											   r.getRhs().getLemma(), r.getRhs().getPartOfSpeech(),
////											   modelParams.get(r.getRelation().toUpperCase())
//											   DEFAULT_CONFIDENCE
//											   );
////			LexicalRuleWithName rwn = new LexicalRuleWithName(rule, r.getResource().toUpperCase());
//			LexicalRuleWithName rwn = new LexicalRuleWithName(rule, r.getRelation().toUpperCase());
//			ruleList.add(rwn);
//		}
//		return new ImmutableListWrapper<LexicalRuleWithName>(ruleList);		
//	}
//	
//	
//	//--------serialization functions -----------------------------//
//	
//	private String buildSerPath(ConfigurationParams params) throws ConfigurationException {
//		String rootDir = params.get("serialize dir");
//		List<String> resourcesNames = params.getStringList("lexical resources");
//		Collections.sort(resourcesNames);
//		String subDir = resourcesNames.toString() + "-" +
//						params.get("transitivity length")  + "-" +
//						params.get("default sense for seeds");
//		String serDir = rootDir + File.separator+ subDir; 
//		new File(serDir).mkdir();
//		
//		return serDir;
//	}
//	
//	private  HashMap<String, Integer> loadSerializedMap() throws FileNotFoundException, IOException, ClassNotFoundException {
//		Serializer<HashMap<String, Integer>> hypoMapSerializer = new Serializer<HashMap<String, Integer>>();
//		HashMap<String, Integer> hypoMap;
//		String serFilePath = m_serDirPath+File.separator+HYPO_ID_MAP;
//		logger.info("loadSerializedMap("+serFilePath+")");
//		File serFile = new File(serFilePath);
//		if(serFile.exists()) {
////			logger.info("[PlisRuleBase]\t loadSerializedMap before sync");
//			synchronized (PlisRuleBase.class) {
//				hypoMap = hypoMapSerializer.load(serFilePath);
//			}
////			logger.info("[PlisRuleBase]\t loadSerializedMap after sync");
//		}else{
//			hypoMap = new HashMap<String, Integer>();
//		}
//		return hypoMap;
//	}
//	
//	private HashMap<LemmaAndPos, ImmutableSet<ChainOfLexicalRules>> loadFromSerialization(Integer hypoId) throws FileNotFoundException, IOException, ClassNotFoundException {
//		Serializer<HashMap<LemmaAndPos, ImmutableSet<ChainOfLexicalRules>>> hypoRulesSerializer = 
//				new Serializer<HashMap<LemmaAndPos, ImmutableSet<ChainOfLexicalRules>>>();
//		String serFileName = m_serDirPath+File.separator+hypoId+".bin";
//		logger.info("loadFromSerialization("+serFileName+")");
//		HashMap<LemmaAndPos, ImmutableSet<ChainOfLexicalRules>> hypoRuleApplications = hypoRulesSerializer.load(serFileName);
//		logger.info("loadFromSerialization("+serFileName+") DONE");
//		return hypoRuleApplications;
//	}
//	
//	private void serializeHypo(int hypoId) throws FileNotFoundException, IOException {
//		String outputFileName = m_serDirPath+File.separator+hypoId+".bin";
//		logger.info("serializeHypo ("+outputFileName+")");
//		Serializer<HashMap<LemmaAndPos, ImmutableSet<ChainOfLexicalRules>>> hypoRulesSerializer = 
//				new Serializer<HashMap<LemmaAndPos, ImmutableSet<ChainOfLexicalRules>>>();
//		hypoRulesSerializer.serialize(m_lhsChainsMap, outputFileName);
//	}
//	
//	private void serializeHypoIdMap() throws FileNotFoundException, IOException, ClassNotFoundException, LexicalModelException {
//		synchronized(PlisRuleBase.class){
//			//merge the hypoMap of the thread into the current serialized map
////			HashMap<String, Integer> mergedMap = mergeInToSerializedMap(m_hypoIdMap);
//			//load the serialized map, if exist
////			logger.info("[PlisRuleBase]\tserializeHypoIdMap before loadSerializedMap");
//			HashMap<String, Integer> serializedMap = loadSerializedMap();
////			logger.info("[PlisRuleBase]\tserializeHypoIdMap after  loadSerializedMap");
//			//if a map was found, merge the one you hold into the one you have just loaded
//			if(!serializedMap.isEmpty()){
//				for(String hypo : m_hypoIdMap.keySet()){
//					if(serializedMap.containsKey(hypo)){ //if this hypothesis was already serialized
//						int hypoIdInAddFrom = m_hypoIdMap.get(hypo);
//						int storedHypoId = serializedMap.get(hypo);
////						logger.info("[PlisRuleBase] hypoIdInAddFrom "+hypoIdInAddFrom+" storedHypoId "+storedHypoId);
//						//if a new serialization file was created for this hypothesis - remove it
//						if(hypoIdInAddFrom != storedHypoId){
//							String duplicateFileName = m_serDirPath+File.separator+m_hypoIdMap.get(hypo)+".bin";
//							File duplicateFile = new File(duplicateFileName);
//							boolean wasDeleted = duplicateFile.delete();
//							if(!wasDeleted){
//								throw new LexicalModelException("failed to delete the file "+duplicateFileName+
//										" which is a duplication of "+serializedMap.get(hypo)+
//										" for hypothesis "+hypo);
//							}
//						}else{
//							serializedMap.put(hypo, hypoIdInAddFrom);
////							logger.info("[PlisRuleBase] re-recording hypo: id "+hypoIdInAddFrom+" "+hypo);
//						}
//					}else{
//						//add a record for the new hypothesis to the loaded map 
//						serializedMap.put(hypo, m_hypoIdMap.get(hypo));
////						logger.info("[PlisRuleBase] new hypo: id "+m_hypoIdMap.get(hypo)+" "+hypo);
//					}
//				}
//			}else{
//				serializedMap = m_hypoIdMap;
//			}
//			//write the merged map to file
//			String outputFileName = m_serDirPath+File.separator+HYPO_ID_MAP;
//			logger.info("serializeHypoIdMap("+outputFileName+")");
//			Serializer<HashMap<String, Integer>> hypoIdSerializer = new Serializer<HashMap<String, Integer>>();
//			hypoIdSerializer.serialize(serializedMap, outputFileName);
//		}
//	}
//	
//	/*
//	private HashMap<String, Integer> mergeInToSerializedMap(HashMap<String, Integer> addFrom) 
//			throws FileNotFoundException, IOException, ClassNotFoundException, LexicalModelException{
//		//load the serialized map, if exist
//		HashMap<String, Integer> serializedMap = loadSerializedMap();
//		//if a map was found, merge the one you hold into the one you have just loaded
//		if(!serializedMap.isEmpty()){
//			for(String hypo : addFrom.keySet()){
//				if(serializedMap.containsKey(hypo)){ //if this hypothesis was already serialized
//					int hypoIdInAddFrom = addFrom.get(hypo);
//					int storedHypoId = serializedMap.get(hypo);
//					logger.info("[PlisRuleBase] hypoIdInAddFrom "+hypoIdInAddFrom+" storedHypoId "+storedHypoId);
//					//if a new serialization file was created for this hypothesis - remove it
//					if(hypoIdInAddFrom != storedHypoId){
//						String duplicateFileName = m_serDirPath+File.separator+addFrom.get(hypo)+".bin";
//						File duplicateFile = new File(duplicateFileName);
//						boolean wasDeleted = duplicateFile.delete();
//						if(!wasDeleted){
//							throw new LexicalModelException("failed to delete the file "+duplicateFileName+
//									" which is a duplication of "+serializedMap.get(hypo)+
//									" for hypothesis "+hypo);
//						}
//					}else{
//						serializedMap.put(hypo, hypoIdInAddFrom);
//						logger.info("[PlisRuleBase] re-recording hypo: id "+hypoIdInAddFrom+" "+hypo);
//					}
//				}else{
//					//add a record for the new hypothesis to the loaded map 
//					serializedMap.put(hypo, addFrom.get(hypo));
//					logger.info("[PlisRuleBase] new hypo: id "+addFrom.get(hypo)+" "+hypo);
//				}
//			}
//		}else{
//			serializedMap = addFrom;
//		}
//		return serializedMap;
//	}
//	*/
//	
//	//-------- end of serialization functions -----------------------------//
//	
//	
//	
//	
//	
//	///////////////////////////// members ///////////////////////////////////
//	
//	
//	private PLM inferModel;
//	private Map<String, Double> modelParams;
//	private String m_serDirPath;
//	private HashMap<LemmaAndPos, ImmutableSet<ChainOfLexicalRules>> m_lhsChainsMap;
//	private HashMap<String, Integer> m_hypoIdMap;
//	private List<String> m_resourcesInUse;
//
//	private static final MutableInteger m_nextId = new MutableInteger(0);
//	private static String HYPO_ID_MAP = "hypoIdMap.bin";
//	
//	
//	
//	
//	
//	///////////////////////////// Main for testing ///////////////////////////////////
//	
//	public static void main(String[] args){ 
//		BasicConfigurator.configure();
//		PlisRuleBase plmRB = null;
//		ConfigurationFile conf = null;
//		try {	
//			if(args.length == 0){
//				String defaultConfFilePath = System.getProperty("user.dir")+File.separator+
//					    "src"+File.separator+"ac"+File.separator+
//					    "biu"+File.separator+"nlp"+File.separator+"nlp"+File.separator+
//					    "engineml"+File.separator+"operations"+File.separator+"rules"+File.separator+ 
//					    "lexicalchain"+File.separator+"graphbased"+File.separator+
//					    "plisRuleBase_config.xml";
//				conf = new ConfigurationFile(new File(defaultConfFilePath));
//			}else{
//				conf = new ConfigurationFile(new File(args[0]));
//			}
//		} catch (ConfigurationFileDuplicateKeyException e) {
//			e.printStackTrace();
//		} catch (ConfigurationException e) {
//			e.printStackTrace();
//		}
//
//				
//		try {
//			
//			plmRB = new PlisRuleBase(conf.getModuleConfiguration("lexical inference"));			
//		} catch (ConfigurationFileDuplicateKeyException e) {
//			e.printStackTrace();
//		} catch (ConfigurationException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		logger.info("RuleBasesNames: "+plmRB.getRuleBasesNames());
//		
//		List<String> hypoList = new Vector<String>();
////		String[] hypoLemmaPosArray = {"people:NOUN","are:VERB","force:NOUN","to:PREPOSITION","leave:VERB","their:PRONOUN","cat:NOUN","behind:ADVERB","when:OTHER","they:PRONOUN","evacuate:NOUN","New:ADJECTIVE","Orleans:NOUN"};
//		String[] hypoLemmaPosArray = {"fluid:NOUN","water:NOUN", "cat:NOUN"};
//		
//		Collections.addAll(hypoList, hypoLemmaPosArray);
//		
//		List<LemmaAndPos> hypothesis;
//		try {
//			hypothesis = convertToLemmaAndPos(hypoList);
//			plmRB.setHypothesis(hypothesis);
//		} catch (TeEngineMlException e) {
//			e.printStackTrace();
//		} catch (RuleBaseException e) {
//			e.printStackTrace();
//		} 
//		
//		ImmutableSet<ChainOfLexicalRules> chains = null;
//		try {
//			chains = plmRB.getRules("liquid", new UnspecifiedPartOfSpeech("NOUN"));
////			chains = plmRB.getRules("humorous", new UnspecifiedPartOfSpeech("ADJECTIVE"));
//		} catch (RuleBaseException e) {
//			e.printStackTrace();
//		} catch (UnsupportedPosTagStringException e) {
//			e.printStackTrace();
//		}
//		
//		if(chains != null){
//			logger.info(LexChainToString(chains.getMutableSetCopy()));
//		}
//		logger.info("Done - LexicalChainRuleBase test");
//	}
//
//	private static String LexChainToString(Set<ChainOfLexicalRules> chains) {
//		StringBuffer sb = new StringBuffer();
//		String SEP = "\t";
//		for(ChainOfLexicalRules chain : chains){
//			sb.append("{" + chain.getConfidence() + SEP);
//			for(LexicalRuleWithName lrwn : chain.getChain().getMutableListCopy()){
//				LexicalRule rule = lrwn.getRule();
//				sb.append("[" + rule.getLhsLemma()+":"+rule.getLhsPos() + SEP);
//				sb.append(lrwn.getRuleBaseName() + SEP);
//				sb.append(rule.getRhsLemma()+":"+rule.getRhsPos() + SEP);
//				sb.append(rule.getConfidence() + "]" + SEP);
//			}
//			sb.append("}" + SEP);
//		}
//		return sb.toString();
//	}
//	
//	//input is list of lemmas possibly each one is followed by a colon and its part-of-speech, otherwise NOUN is asigned 
//	private static List<LemmaAndPos> convertToLemmaAndPos(List<String> iWords) throws TeEngineMlException, RuleBaseException {
//		List<LemmaAndPos> toReturn = new ArrayList<LemmaAndPos>();
//		for(String word : iWords){
//			UnspecifiedPartOfSpeech posTag = null;
//			String lemma = null;
//			try {
//				int colonIndex = word.indexOf(":"); 
//				if(colonIndex > -1){
//					posTag = new UnspecifiedPartOfSpeech(word.substring(colonIndex+1));
//					lemma = word.substring(0, colonIndex);
//				}else{
//					posTag = new UnspecifiedPartOfSpeech("NOUN");
//					lemma = word;
//				}
//			} catch (UnsupportedPosTagStringException e) {
//				throw new RuleBaseException(word+" does not have a valid POS tag", e);
//			}
//			toReturn.add(new LemmaAndPos(lemma, posTag));
//		}
//		return toReturn;
//	}
//
//	private static final Logger logger = Logger.getLogger(PlisRuleBase.class);
}
