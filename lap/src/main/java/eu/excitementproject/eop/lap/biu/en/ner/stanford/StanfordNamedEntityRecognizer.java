package eu.excitementproject.eop.lap.biu.en.ner.stanford;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TokenizerException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityPhrase;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityWord;



/**
 * Implementation of {@link NamedEntityRecognizer} by using the Stanford
 * NER (I am using the 2009 version).
 * 
 * @author Asher Stern & Erel Segal
 *
 */
public class StanfordNamedEntityRecognizer implements NamedEntityRecognizer
{
	protected void cleanSentence()
	{
		sentence = null;
		listOfEntities = null;
		
		
		
	}
	
	
	
	/**
	 * Constructs this NamedEntityRecognizer.
	 * <P>
	 * 
	 * @param classifierPath a classifier for Stanford NER
	 * (I am using ner-eng-ie.crf-3-all2008-distsim.ser.gz usually, which
	 * is part of the downloadable Stanford NER package).
	 * 
	 */
	public StanfordNamedEntityRecognizer(File classifierPath) throws NamedEntityRecognizerException
	{
		if (classifierPath.exists())
			this.classifierPath = classifierPath;
		else 
			throw new NamedEntityRecognizerException("File "+classifierPath+" does not exist");
	}
	

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizer#init()
	 */
	public void init() throws NamedEntityRecognizerException
	{
		if (initialized)
			throw new NamedEntityRecognizerException(
					"init() was called though the StanfordNamedEntityRecognizer was already initialized.");
		try
		{
			this.crfClassifier = CRFClassifier.getClassifier(this.classifierPath.getPath());
			this.initialized = true;
		}
		catch (Exception e)
		{
			throw new NamedEntityRecognizerException("Classifier load failed.",e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizer#setSentence(java.util.List)
	 */
	public void setSentence(List<String> sentence)
	{
		this.sentence = sentence;
	}

	public void setSentence(String sentence, Tokenizer tokenizer) throws TokenizerException {
		tokenizer.setSentence(sentence);
		tokenizer.tokenize();
		setSentence(tokenizer.getTokenizedSentence());
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizer#recognize()
	 */
	public void recognize() throws NamedEntityRecognizerException
	{
		if (!initialized)
			throw new NamedEntityRecognizerException("StanfordNamedEntityRecognizer was not initialized.");
		if (this.sentence==null)
			throw new NamedEntityRecognizerException("Wrong input to StanfordNamedEntityRecognizer. A null sentence was supplied.");
		
		listOfEntities = new ArrayList<NamedEntityWord>(this.sentence.size());
		mapOfEntities = new LinkedHashMap<Integer, NamedEntityPhrase>();
		
		ArrayList<Word> sentenceAsWords = new ArrayList<Word>(this.sentence.size());
		for (String word : this.sentence)
		{
			sentenceAsWords.add(new Word(word));
		}
		
		List<CoreLabel> nerredList = null;
		try
		{
			nerredList = this.crfClassifier.testSentence(sentenceAsWords);
		}
		catch(Exception e)
		{
			throw new NamedEntityRecognizerException("classifying (i.e. retrieving the Named-Entity for the sentence words) sentence failed.",e);
		}
		
		if (nerredList==null)
			throw new NamedEntityRecognizerException("classification returned a null list");
		
		// Add a sentinel object at the end of the list, for building mapOfEntities
		// does not affect listOfEntities
		nerredList.add(null);
		
		StringBuffer currentNamedEntity = new StringBuffer();
		NamedEntity currentNamedEntityType = null;
		int startOfCurrentNamedEntity = 0;
		for (int iToken=0; iToken<nerredList.size(); ++iToken) {
			NamedEntity newNamedEntityType = null;
			String strWord = null;
			CoreLabel label = nerredList.get(iToken);
			boolean sentinelToken = (iToken == nerredList.size()-1);
			
			if (!sentinelToken) {
				strWord = label.get(edu.stanford.nlp.ling.CoreAnnotations.WordAnnotation.class);
				String strNamedEntity = label.get(edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation.class);
				newNamedEntityType = StanfordAnswerToNamedEntityMapper.convert(strNamedEntity);

				listOfEntities.add(new NamedEntityWord(strWord, newNamedEntityType));
			}

			// Calculate building a full phrase from all adjacent entities with the same type
			// This implementation supports entities adjacent to each other (with different types) and entities at the end of the sentence
			boolean endEntityPreviousToken =     (currentNamedEntityType!=null && newNamedEntityType!=currentNamedEntityType);
			boolean startEntityCurrentToken =    (newNamedEntityType!=null     && newNamedEntityType!=currentNamedEntityType);
			boolean continueEntityCurrentToken = (currentNamedEntityType!=null && newNamedEntityType==currentNamedEntityType);

			if (continueEntityCurrentToken) {
				currentNamedEntity.append(" ").append(strWord);
			}
			if (endEntityPreviousToken) {
				String canonicalNamedEntity = currentNamedEntity.toString();
				mapOfEntities.put(startOfCurrentNamedEntity, new NamedEntityPhrase(canonicalNamedEntity, currentNamedEntityType));
				currentNamedEntityType = null;
				currentNamedEntity.setLength(0);
			}
			if (startEntityCurrentToken) {
				currentNamedEntity.append(strWord);
				currentNamedEntityType = newNamedEntityType;
				startOfCurrentNamedEntity = iToken;
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizer#getAnnotatedSentence()
	 */
	public List<NamedEntityWord> getAnnotatedSentence()
	{
		return this.listOfEntities;
	}
	
	public Map<Integer, NamedEntityPhrase> getAnnotatedEntities() {
		return this.mapOfEntities;
	}	

	public void cleanUp()
	{
		cleanSentence();
		crfClassifier = null;
		classifierPath = null;
		initialized = false;
		
	}
	
	protected File classifierPath;
	protected CRFClassifier crfClassifier;
	protected List<String> sentence;
	protected ArrayList<NamedEntityWord> listOfEntities;
	protected Map<Integer, NamedEntityPhrase> mapOfEntities;
	
	protected boolean initialized = false;
}
