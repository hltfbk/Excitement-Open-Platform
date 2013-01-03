/**
 * 
 */
package ac.biu.nlp.nlp.instruments.postagger;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.ngram.Dictionary;
import opennlp.tools.postag.DefaultPOSContextGenerator;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import ac.biu.nlp.nlp.representation.PennPartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;

/**
 * A {@link PosTagger} implemented with the {@link POSTaggerME} in {@code opennlp-tools-1.3.0.jar} from the {@code opennlp-tools-1.3.0} package.
 * {@link POSTagger} uses the {@link PennPartOfSpeech} pos tag set. {@link #cleanup()} does nothing here.
 * <p>
 * <b>Note</b> that this class requires {@code trove.jar} (form the same package) in the classpath
 * <p>
 * Note that setTokenizedSentence(List) is slightly faster than setTokenizedSentence(String)
 * 
 * @author Amnon Lotan
 * @since Jan 10, 2011
 * 
 */
public class OpenNlpPosTagger implements PosTagger 
{
	/**
	 * Slim constructor with only the mandatory params
	 * 
	 * @param posTaggerModelFile MANDATORY the model for training the POS tagger
	 * @param posTaggerTagDictionaryFile MANDATORY a dictionary of words and their POSes 
	 * @throws PosTaggerException 
	 * 
	 */
	public OpenNlpPosTagger(File posTaggerModelFile, String posTaggerTagDictionaryFile) throws PosTaggerException 
	{
		this(posTaggerModelFile, posTaggerTagDictionaryFile, false, null);
	}
	
	/**
	 * Constructor with all possible params
	 * 
	 * @param posTaggerModelFile MANDATORY the model for training the POS tagger
	 * @param posTaggerTagDictionaryFile MANDATORY a dictionary of words and their POSes
	 * @param posTaggerTagDictionaryCaseSensitive MANDATORY by default should be FALSE
	 * @param posTaggerDictionaryFile OPTIONAL
	 * @throws PosTaggerException 
	 * 
	 */
	public OpenNlpPosTagger(File posTaggerModelFile, String posTaggerTagDictionaryFile, boolean posTaggerTagDictionaryCaseSensitive, 
			String posTaggerDictionaryFile) throws PosTaggerException 
	{
		// load pos tagger
		if(posTaggerModelFile == null)
			throw new PosTaggerException("no pos tagger model file specified");
		if (!posTaggerModelFile.exists())
			throw new PosTaggerException(posTaggerModelFile + " doesn't exist");
		if(posTaggerTagDictionaryFile == null)
			throw new PosTaggerException("no pos tagger tag dictionary file specified");

		this.posTaggerModelFile = posTaggerModelFile;
		this.posTaggerTagDictionaryFile = posTaggerTagDictionaryFile;
		this.posTaggerTagDictionaryCaseSensitive = posTaggerTagDictionaryCaseSensitive;
		this.posTaggerDictionaryFile = posTaggerDictionaryFile;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.postagger.PosTagger#init()
	 */
	public void init() throws PosTaggerException 
	{
		MaxentModel posTaggerModel;
		Dictionary dict;
		POSDictionary tagDict;
		try {
			posTaggerModel = new SuffixSensitiveGISModelReader(posTaggerModelFile).getModel();
			tagDict = new POSDictionary(posTaggerTagDictionaryFile, posTaggerTagDictionaryCaseSensitive);
			dict = posTaggerDictionaryFile != null ? new Dictionary(posTaggerDictionaryFile) : null;
		} catch (IOException e) {
			throw new PosTaggerException("Error constructing a SuffixSensitiveGISModelReader with " + posTaggerModelFile + 
					", " + posTaggerTagDictionaryFile + " and " + posTaggerDictionaryFile + ". See nested.", e);
		}
		posTagger = new POSTaggerME(posTaggerModel, new DefaultPOSContextGenerator(dict), tagDict);
		
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
		tokens = new LinkedList<String>();
		for (String token : sentence.split(" "))
			tokens.add(token);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.postagger.PosTagger#setTokenizedSentence(java.util.List)
	 */
	public void setTokenizedSentence(List<String> sentenceTokens) throws PosTaggerException 
	{
		tokens = sentenceTokens;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.postagger.PosTagger#process()
	 */
	@SuppressWarnings("unchecked")
	public synchronized void process() throws PosTaggerException 
	{
		if (!initialized)
			throw new PosTaggerException("You must call init() before calling process()");
		
		List<String> posTags;
		try {
			posTags = posTagger.tag(tokens);
		} catch (Exception e){		// no exceptions are declared for tag(), but I'm suspicious
			throw new PosTaggerException("Error trown by the POSTagger. See nested.", e);			
		}
		
		if (posTags.size() != tokens.size())
			throw new PosTaggerException("Error with the opennlp posTagger. The returned set has " + posTags.size() + 
					" pos tags, while there are " +  tokens.size() + " input tokens.");

		// prepare the output
		posTaggedTokens = new LinkedList<PosTaggedToken>();
		for (int i = 0; i <  tokens.size(); i++)
			try {
				
				posTaggedTokens.add(new PosTaggedToken(tokens.get(i), new PennPartOfSpeech(posTags.get(i))));
				
			} catch (UnsupportedPosTagStringException e) {
				throw new PosTaggerException("Opennlp pos tag set doesn't comply with the Penn pos tag set. What is this supposed to be? " 
						+ posTags.get(i), e);
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
	
	private POSTagger posTagger;
	private File posTaggerModelFile;
	private String posTaggerTagDictionaryFile;
	private boolean posTaggerTagDictionaryCaseSensitive;
	private String posTaggerDictionaryFile;
	private List<String> tokens;
	private List<PosTaggedToken> posTaggedTokens;
	private boolean initialized = false;
}