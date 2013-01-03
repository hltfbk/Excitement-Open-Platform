package ac.biu.nlp.nlp.instruments.ner.stanford;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


import ac.biu.nlp.nlp.instruments.ner.NamedEntityPhrase;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizer;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizerException;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityWord;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.NamedEntity;
import ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer;
import ac.biu.nlp.nlp.instruments.tokenizer.TokenizerException;

import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.ling.CoreLabel;



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
		mapOfEntities = new HashMap<Integer, NamedEntityPhrase>();
		
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
		
		StringBuffer currentNamedEntity = new StringBuffer();
		NamedEntity currentNamedEntityType = null;
		int startOfCurrentNamedEntity = 0;
		for (int iToken=0; iToken<nerredList.size(); ++iToken) {
			CoreLabel label = nerredList.get(iToken);
			String strWord = label.get(edu.stanford.nlp.ling.CoreAnnotations.WordAnnotation.class);
			String strNamedEntity = label.get(edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation.class);
			NamedEntity newNamedEntityType = StanfordAnswerToNamedEntityMapper.convert(strNamedEntity);

			listOfEntities.add(new NamedEntityWord(strWord, newNamedEntityType));

			boolean endNamedEntity = (currentNamedEntityType!=null && newNamedEntityType!=currentNamedEntityType);
			boolean startNewNamedEntity = (newNamedEntityType!=null && newNamedEntityType!=currentNamedEntityType);
			boolean continueNamedEntity = (currentNamedEntityType!=null && newNamedEntityType==currentNamedEntityType);

			if (startNewNamedEntity) {
				currentNamedEntity.append(strWord);
				currentNamedEntityType = newNamedEntityType;
				startOfCurrentNamedEntity = iToken;
			}
			if (continueNamedEntity) {
				currentNamedEntity.append(" ").append(strWord);
			}
			if (endNamedEntity) {
				String canonicalNamedEntity = currentNamedEntity.toString();
				mapOfEntities.put(startOfCurrentNamedEntity, new NamedEntityPhrase(canonicalNamedEntity, currentNamedEntityType));
				currentNamedEntityType = null;
				currentNamedEntity.setLength(0);
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
