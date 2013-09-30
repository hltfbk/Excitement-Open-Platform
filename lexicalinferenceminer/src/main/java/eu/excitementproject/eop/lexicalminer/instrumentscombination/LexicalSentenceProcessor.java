package eu.excitementproject.eop.lexicalminer.instrumentscombination;


import java.util.*;
import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityWord;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggedToken;
import eu.excitementproject.eop.lap.biu.postagger.PosTagger;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggerException;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TokenizerException;

/**
 * This class combines several instruments to lexically process (i.e. no parsing) a sentence.
 * It saves the direct sequential operation of the following instruments and the need to combine their outputs.  
 * Applies the following instruments:
 * <ol>
 * <li>tokenizer</li>
 * <li>pos-tagger</li>
 * <li>named entity recognizer</li>
 * <li>lemmatizer</li>
 * </ol>
 * It is possible to avoid applying one or more instruments out of that list, simply by providing null instead of a pointer.
 * Processed sentence is given as a List of {@link TokenInfo}.
 * @author Eyal Shnarch
 * @since 05/07/2011
 */
public class LexicalSentenceProcessor {

	private Tokenizer m_tokenizer;
	private PosTagger m_postagger;
	private NamedEntityRecognizer m_ner;
	private Lemmatizer m_lemmatizer;
	
	/**
	 * Assign the input instruments. It is possible to avoid applying an instrument by providing null instead of a pointer.<br>
	 * <b>Important</b>: all provided instruments must be after initialization.
	 * @param tokenizer - if not provided, the sentence will be split by white spaces (using the regular expression '\\s+').
	 * @param postagger
	 * @param ner - note that if you are using StanfordNamedEntityRecognizer, its initialization may take a few seconds
	 * @param lemmatizer
	 * @throws InstrumentCombinationException 
	 */
	public LexicalSentenceProcessor(Tokenizer tokenizer, PosTagger postagger,	
									Lemmatizer lemmatizer, NamedEntityRecognizer ner){
		m_tokenizer = tokenizer;
		m_postagger = postagger;
		m_ner = ner;
		m_lemmatizer = lemmatizer;
	}
	
	
	/**
	 * Assign the input instruments using the given configuration params.
	 * @throws TokenizerException 
	 */
	public LexicalSentenceProcessor(ConfigurationParams params) throws InstrumentCombinationException{
		this(LexicalToolsFactory.createTokenizer(params), LexicalToolsFactory.createPosTagger(params), 
				LexicalToolsFactory.createLemmatizer(params), LexicalToolsFactory.createNamedEntityRecognizer(params));
		
	}
	


	
	/**
	 * Process the sentence by utilizing only the given instruments (in the constructor) and in the following order:
	 * <ol>
	 * <li>tokenizer - if a Tokenizer was not provided, the sentence is split by white spaces (using the regular expression '\\s+')</li>
	 * <li>pos-tagger</li>
	 * <li>named entity recognizer</li>
	 * <li>lemmatizer - if a PosTagger was not applied, all lemmas of all pos-tags are stored. 
	 * 					Words are being lower-cased before being sent to the Lemmatizer unless recognized as named entities.</li>
	 * </ol>
	 * @param sentence
	 * @return
	 * @throws InstrumentCombinationException
	 */
	public List<TokenInfo> process(String sentence) throws InstrumentCombinationException{
		List<TokenInfo> processedSent = new Vector<TokenInfo>();
		
		List<String> tokenizedSent = tokenize(sentence);
		for(String token : tokenizedSent){
			TokenInfo tInfo = new TokenInfo(token);
			processedSent.add(tInfo);
		}
		
		if(m_postagger != null) {
			addPosTagInfo(tokenizedSent, processedSent);
		}

		if(m_ner != null){
			addNerInfo(tokenizedSent, processedSent);
		}
		
		if(m_lemmatizer != null){
			addLemmaInfo(processedSent);
		}
		
		return processedSent;
	}
	
	
	
	


	public void close() {
		if(m_tokenizer != null) m_tokenizer.cleanUp();
		m_tokenizer = null;
		if(m_postagger != null) m_postagger.cleanUp();
		m_postagger = null;
		if(m_ner != null) m_ner.cleanUp();
		m_ner = null;
		if(m_lemmatizer != null) m_lemmatizer.cleanUp();
		m_lemmatizer = null;
	}
	
	
	//////////////// private functions ////////////////////////////////////////

	private void addLemmaInfo(List<TokenInfo> processedSent) throws InstrumentCombinationException {
		for(TokenInfo tInfo : processedSent){
			ImmutableList<String> lemmas;
			
			//Words are lower-cased, unless recognized as named entities
			String origTerm = tInfo.getOrigStr();
//			if(tInfo.getNamedEntity() == null){
//				origTerm = origTerm.toLowerCase();
//			}
			
			//if a PosTagger was not applied, all lemmas of all pos-tags are stored
			try {
				if(tInfo.getPosTag() != null){	
					m_lemmatizer.set(origTerm, tInfo.getPosTag());
					m_lemmatizer.process();
					List<String> reallist = new Vector<String>();
					reallist.add(m_lemmatizer.getLemma());
					lemmas = new ImmutableListWrapper<String>(reallist);
				}else{
					m_lemmatizer.set(origTerm);
					m_lemmatizer.process();
					lemmas = m_lemmatizer.getLemmas();
				}
			} catch (LemmatizerException e) {
				throw new InstrumentCombinationException("see nested exception from Lemmatizer:", e);
			}
			tInfo.setLemmas(lemmas);
		}
	}
	
	private void addNerInfo(List<String> tokenizedSent,	List<TokenInfo> processedSent) throws InstrumentCombinationException {
		List<NamedEntityWord> nerSent = null;
		try {
			m_ner.setSentence(tokenizedSent);
			m_ner.recognize();
			nerSent = m_ner.getAnnotatedSentence();
		} catch (NamedEntityRecognizerException e) {
			throw new InstrumentCombinationException("see nested exception from NamedEntityRecognizer:", e);
		}
		if(nerSent.size() != processedSent.size()){
			throw new InstrumentCombinationException("the number of ner-tagged tokens is not equal" +
					" to the number of sentence-tokens in input sentence " + tokenizedSent);
		}
		Iterator<TokenInfo> procSentIter = processedSent.iterator();
		Iterator<NamedEntityWord> nerSentIter = nerSent.iterator();
		while(procSentIter.hasNext()){
			TokenInfo tInfo = procSentIter.next();
			NamedEntityWord nerTerm = nerSentIter.next();
			if(tInfo.getOrigStr().equals(nerTerm.getWord())){
				tInfo.setNamedEntity(nerTerm.getNamedEntity());
			}else{
				throw new InstrumentCombinationException("a mismatch was found between a ner-tagged word" +
						" and the sentence word " + tInfo.getOrigStr() + " of input sentence " + tokenizedSent);
			}
		}
	}

	private void addPosTagInfo(List<String> tokenizedSent, 	List<TokenInfo> processedSent) throws InstrumentCombinationException {
		List<PosTaggedToken> postagedSent = null;
		try {
			m_postagger.setTokenizedSentence(tokenizedSent);
			m_postagger.process();
			postagedSent = m_postagger.getPosTaggedTokens();
		} catch (PosTaggerException e) {
			throw new InstrumentCombinationException("see nested exception from PosTagger:", e);
		}
		if(postagedSent.size() != processedSent.size()){
			throw new InstrumentCombinationException("the number of pos-tagged tokens is not equal" +
					" to the number of sentence-tokens in input sentence " + tokenizedSent);
		}
		Iterator<TokenInfo> procSentIter = processedSent.iterator();
		Iterator<PosTaggedToken> posSentIter = postagedSent.iterator();
		while(procSentIter.hasNext()){
			TokenInfo tInfo = procSentIter.next();
			PosTaggedToken posTerm = posSentIter.next();
			if(tInfo.getOrigStr().equals(posTerm.getToken())){
				tInfo.setPosTag(posTerm.getPartOfSpeech());
			}else{
				throw new InstrumentCombinationException("a mismatch was found between a ner-tagged word" +
						" and the sentence word " + tInfo.getOrigStr() + " of input sentence " + tokenizedSent);
			}
		}
	}

	//if a Tokenizer was not provided, the sentence is split by white spaces (using the regular expression '\\s+')
	private List<String> tokenize(String sentence) throws InstrumentCombinationException {
		List<String> tokenizedSent = null;
		if(m_tokenizer != null){
			try {
				m_tokenizer.setSentence(sentence);
				m_tokenizer.tokenize();
				tokenizedSent = m_tokenizer.getTokenizedSentence();
			} catch (TokenizerException e) {
				throw new InstrumentCombinationException("see nested exception from Tokenizer:", e);
			}
		}else{
			tokenizedSent = new Vector<String>();
			Utils.arrayToCollection(sentence.split("//s+"), tokenizedSent);
		}
		return tokenizedSent;
	}
	
	
	private static String thisClassName = Thread.currentThread().getStackTrace()[1].getClassName();
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(thisClassName);


	
	//////////////////  A main for testing ////////////////////////////////////
	
	public static void main(String[] args) {
		
	// Ofer Bronstein 29/1/13: OpenNLPPosTagger for version 1.3.0 is no longer supported, so entire "main" is disabled

//		String jars = "//qa-srv/D/jars/";
//		try {
//			Tokenizer tokenizer = new MaxentTokenizer();
//			tokenizer.init();
//			
////			String posTaggerModelFile = jars + "stanford-postagger-2008-09-28/bidirectional-wsj-0-18.tagger";
////			PosTagger postagger = new MaxentPosTagger(posTaggerModelFile);		//very slow!
//			File posTaggerModelFile = new File(jars + "opennlp-tools-1.3.0/models/english/parser/tag.bin.gz");
//			String posTaggerDictPath = jars + "opennlp-tools-1.3.0/models/english/parser/tagdict";
//			PosTagger postagger = new OpenNlpPosTagger(posTaggerModelFile, posTaggerDictPath);
//			postagger.init();
//			
//			String nerClassifierPath = jars + "stanford-ner-2009-01-16/classifiers/ner-eng-ie.crf-3-all2008-distsim.ser.gz";
//			NamedEntityRecognizer ner = new StanfordNamedEntityRecognizer(new File(nerClassifierPath));
//			ner.init();
//			
//			String lemmatizerRuleFile = jars + "GATE-3.1/plugins/Tools/resources/morph/default.rul";
//			Lemmatizer lemmatizer = new GateLemmatizer(new File(lemmatizerRuleFile).toURI().toURL());
//			lemmatizer.init();
//			
//			LexicalSentenceProcessor sentProc = new LexicalSentenceProcessor(tokenizer, postagger, lemmatizer, ner);
//			
//			
//			String sentence = "Julia George: More than 150 former officers of the overthrown South " +
//					"Vietnamese government have been released from a re-education camp " +
//					"after 13 years of detention, the official Vietnam News Agency " +
//					"reported Saturday.";
//			
//			List<TokenInfo> procSent = sentProc.process(sentence);
//			
//			System.out.println("orig\tlemmas\tPOS\tNE");
//			for(TokenInfo tInfo : procSent){
//				System.out.println(tInfo);
//			}
//			tokenizer.cleanUp();
//			postagger.cleanUp();
//			ner.cleanUp();
//			lemmatizer.cleanUp();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println("Done: test LexicalSentenceProcessor");
	}

}
