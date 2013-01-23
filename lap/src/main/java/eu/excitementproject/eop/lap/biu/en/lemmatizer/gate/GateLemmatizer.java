package eu.excitementproject.eop.lap.biu.en.lemmatizer.gate;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import gate.creole.ResourceInstantiationException;
import gate.creole.morph.Interpret;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;




/**
 * Implementation of {@link Lemmatizer} based on Gate.
 * Using Gate version 3.1
 * @author Asher Stern
 * @since Jan 18, 2011
 *
 */
public class GateLemmatizer implements Lemmatizer
{
	/////////////////////////////////////// PUBLIC /////////////////////////////////////
	
	// constants
	public static final String GATE_LEMMATIZER_VERB_CATEGORY_STRING = "VB";
	public static final String GATE_LEMMATIZER_NOUN_CATEGORY_STRING = "NN";
	public static final String GATE_LEMMATIZER_ALL_CATEGORIES_STRING = "*";
	
	/**
	 * Constructor with the gate rules file (as URL). Currently it is
	 * $JARS/GATE-3.1/plugins/Tools/resources/morph/default.rul
	 * @param gateLemmatizerFileUrl gate rules file (as URL)
	 * @throws LemmatizerException
	 */
	public GateLemmatizer(URL gateLemmatizerFileUrl) throws LemmatizerException
	{
		if (null==gateLemmatizerFileUrl) throw new LemmatizerException("null==gateLemmatizerFileUrl");
		this.gateLemmatizerFileUrl = gateLemmatizerFileUrl;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.lemmatizer.Lemmatizer#init()
	 */
	public void init() throws LemmatizerException
	{
		try
		{
			gateLemmatizerInterpretObject = new Interpret();
			gateLemmatizerInterpretObject.init(this.gateLemmatizerFileUrl);
			initialized = true;
		}
		catch (ResourceInstantiationException e)
		{
			throw new LemmatizerException("Gate lemmatizer initialization failed. See nested exception.",e);
		}
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.lemmatizer.Lemmatizer#set(java.lang.String)
	 */
	public void set(String word) throws LemmatizerException
	{
		if (null==word) throw new LemmatizerException("null==word");
		this.word = word;
		this.partOfSpeech = null;
		this.lemmas = null;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.lemmatizer.Lemmatizer#set(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	public void set(String word, PartOfSpeech partOfSpeech) throws LemmatizerException
	{
		if (null==word) throw new LemmatizerException("null==word");
		if (null==partOfSpeech) throw new LemmatizerException("null==word");

		this.word = word;
		this.partOfSpeech = partOfSpeech;
		this.lemmas = null;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.lemmatizer.Lemmatizer#process()
	 */
	public void process() throws LemmatizerException
	{
		if (null==word) throw new LemmatizerException("word not set!");
		lemmas = new ArrayList<String>(1);
		
		String category = null;
		if (partOfSpeech!=null)
		{
			if (simplerPos(partOfSpeech.getCanonicalPosTag()).equals(SimplerCanonicalPosTag.VERB))
				category = GATE_LEMMATIZER_VERB_CATEGORY_STRING;
			else if (simplerPos(partOfSpeech.getCanonicalPosTag()).equals(SimplerCanonicalPosTag.NOUN))
				category = GATE_LEMMATIZER_NOUN_CATEGORY_STRING;
			else if (simplerPos(partOfSpeech.getCanonicalPosTag()).equals(SimplerCanonicalPosTag.PRONOUN))
				category = GATE_LEMMATIZER_NOUN_CATEGORY_STRING;
			else
				category = null;
		}
		else
		{
			category = GATE_LEMMATIZER_ALL_CATEGORIES_STRING;
		}
		
		if (null==category) // partOfSpeech was set, but it is neither verb nor noun
			lemmas.add(word);
		else
		{
			try
			{
				lemmas.add(gateLemmatizerInterpretObject.runMorpher(word, category));
			}
			catch(Exception e)
			{
				throw new LemmatizerException("Unknown problem in Gate lemmatizer. See nested exception",e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.lemmatizer.Lemmatizer#getLemma()
	 */
	public String getLemma() throws LemmatizerException
	{
		if (null==lemmas) throw new LemmatizerException("process() was not called.");
		if (lemmas.size()<1) throw new LemmatizerException("BUG: Internal bug in GateLemmatizer.");
		return lemmas.get(0);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.lemmatizer.Lemmatizer#getLemmas()
	 */
	public ImmutableList<String> getLemmas() throws LemmatizerException
	{
		if (null==lemmas) throw new LemmatizerException("process() was not called.");
		return new ImmutableListWrapper<String>(lemmas);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.lemmatizer.Lemmatizer#cleanUp()
	 */
	public void cleanUp()
	{
	}

	

	//////////////////////////// PROTECTED AND PRIVATE //////////////////////////////////
	
	protected URL gateLemmatizerFileUrl;
	protected Interpret gateLemmatizerInterpretObject = null;
	protected boolean initialized = false;
	
	protected String word = null;
	protected PartOfSpeech partOfSpeech = null;
	
	protected List<String> lemmas = null;

}
