/**
 * 
 */
package eu.excitementproject.eop.lap.biu.en.postagger.stanford;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggedToken;
import eu.excitementproject.eop.lap.biu.postagger.PosTagger;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggerException;

/**
 * A {@link PosTagger} implemented with  {@link edu.stanford.nlp.tagger.maxent.MaxentTagger} in {@code stanford-postagger-2008-09-28.jar}. 
 * It uses the {@link PennPartOfSpeech} pos tag set. {@link #cleanup()} does nothing here.
 * 
 * @author Amnon Lotan
 * @since Jan 11, 2011
 * 
 */
public class MaxentPosTagger implements PosTagger 
{
	/**
	 * @param posTaggerModelFile the model for training the POS tagger, e.g. jars\stanford-postagger-2008-09-28\bidirectional-wsj-0-18.tagger
	 * @throws PosTaggerException 
	 * 
	 */
	public MaxentPosTagger(String posTaggerModelFile) throws PosTaggerException 
	{
		if (posTaggerModelFile == null)
			throw new PosTaggerException("No pos tagger model file specified for the MaxentTagger");

		this.posTaggerModelFile = posTaggerModelFile;

	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.postagger.PosTagger#init()
	 */
	public void init() throws PosTaggerException 
	{
		try {
			MaxentTagger.init(posTaggerModelFile);
		} catch (Exception e) {
			throw new PosTaggerException("Error initializing the MaxentTagger. See nested.", e);
		}
		
		initialized = true;
	}
	
	public boolean isInitialized() {
		return initialized;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.postagger.PosTagger#setTokenizedSentence(java.lang.String)
	 */
	public void setTokenizedSentence(String sentence) throws PosTaggerException 
	{
		if (sentence == null)
			throw new PosTaggerException("Got a null sentence");
		tokenizedTextAsString = sentence;
		tokenizedTextAsList = null;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.postagger.PosTagger#setTokenizedSentence(java.util.List)
	 */
	public void setTokenizedSentence(List<String> sentenceTokens) throws PosTaggerException 
	{
		if (sentenceTokens == null)
			throw new PosTaggerException("Got a null sentence");
		tokenizedTextAsList = sentenceTokens;
		tokenizedTextAsString = null;
//		tokenizedTextString = StringUtil.joinIterableToString(sentenceTokens, SPACE);

	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.postagger.PosTagger#process()
	 */
	public void process() throws PosTaggerException 
	{
		if ( !initialized )
			throw new PosTaggerException("You must call init() before calling process()");
		
		// tag
		Sentence<TaggedWord> taggedWords;
		try {
			if (tokenizedTextAsString != null)
				taggedWords = MaxentTagger.tagStringTokenized(tokenizedTextAsString);
			else
				taggedWords = MaxentTagger.tagSentence(listToSentence(tokenizedTextAsList));
		} catch (Exception e) {
			throw new PosTaggerException("Error tagging this tokenized text: " + 
					((tokenizedTextAsString != null) ? tokenizedTextAsString : tokenizedTextAsList)  , e); 
		}		
		
		// now prepare the output
//		String[] tokens = tokenizedTextAsString.split(SPACE);
//		
//		if (tokens.length != taggedWords.size())
//			throw new PosTaggerException("Number of input tokens " + tokens.length + " doesn't match number of returned tags " + 
//					taggedWords.size());
		
		posTaggedTokens = new LinkedList<PosTaggedToken>();

//		for (int i = 0; i < tokens.length; i++)
		for (TaggedWord taggedWord : taggedWords)
			try {
				//Eyal 10.7.11: a workaround for a tagging bug: 
				//(the term 'EOS' gets the non-existing tag 'EOS')
				if(taggedWord.word().equals("EOS") && 
						taggedWord.tag().equals("EOS")){	
					taggedWord.setTag("NNP");	
				}
				posTaggedTokens.add(new PosTaggedToken(taggedWord.word(), new PennPartOfSpeech(taggedWord.tag())));
			} catch (UnsupportedPosTagStringException e) {
				throw new PosTaggerException("MaxentTagger pos tag set don't comply with the Penn pos tag set. What is this supposed to be? " 
						+ taggedWord.word()+"\\"+taggedWord.tag(), e);
			}
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.postagger.PosTagger#getPosTaggedTokens()
	 */
	public List<PosTaggedToken> getPosTaggedTokens() throws PosTaggerException 
	{
		return posTaggedTokens;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.postagger.PosTagger#cleanUp()
	 */
	public void cleanUp() {}
	
	

	/**
	 * @param strings
	 * @return
	 */
	private Sentence<? extends HasWord> listToSentence(	List<String> strings) {
		Sentence<HasWord> sentence = new Sentence<HasWord>();
		for (String str : strings)
			sentence.add(new Word(str));
		return sentence;
	}

//	private static final String SPACE = " ";
	private String posTaggerModelFile;
	/**
	 * At any point of time, at most  one of {@link #tokenizedTextAsList} and {@link #tokenizedTextAsString} is not null. The non-null one (if any)
	 * is the input for the next call to {@link #process()} 
	 */	
	private String tokenizedTextAsString;
	/**
	 * At any point of time, at most  one of {@link #tokenizedTextAsList} and {@link #tokenizedTextAsString} is not null. The non-null one (if any)
	 * is the input for the next call to {@link #process()} 
	 */
	private List<String> tokenizedTextAsList;
	private List<PosTaggedToken> posTaggedTokens;
	private boolean initialized = false;
}