package ac.biu.nlp.nlp.engineml.rteflow.systems.rtepairs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.TextHypothesisPair;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;

import ac.biu.nlp.nlp.engineml.rteflow.preprocess.Instruments;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.coreference.CoreferenceResolutionException;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationException;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizerException;
import ac.biu.nlp.nlp.instruments.parse.ParserRunException;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitterException;


/**
 * Gets a list of pairs ( {@link TextHypothesisPair} ) and returns list of {@link PairData}.
 * The pre-processing is sentence-splitting, parsing and co-reference resolution.
 * @author Asher Stern
 * @since Jan 6, 2011
 *
 */
public class PairsPreProcessor
{
	public PairsPreProcessor(List<TextHypothesisPair> pairs, Instruments<Info, BasicNode> instruments)
	{
		this.pairs = pairs;
		this.instruments = instruments;
	}
	
	
	public boolean isProcessingNamedEntities()
	{
		return processingNamedEntities;
	}

	public void setProcessingNamedEntities(boolean processingNamedEntities)
	{
		this.processingNamedEntities = processingNamedEntities;
	}
	
	
	public boolean isMakingTextNormalization()
	{
		return makingTextNormalization;
	}

	public void setMakingTextNormalization(boolean makingTextNormalization)
	{
		this.makingTextNormalization = makingTextNormalization;
	}

	public void process() throws TeEngineMlException, ParserRunException, SentenceSplitterException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException, NamedEntityRecognizerException, TreeStringGeneratorException
	{
		pairsData = new ArrayList<PairData>(pairs.size());
		if (!processingNamedEntities)
			logger.warn("Warning: does not perform Named-Entity recognition.");
			
		//this.coreferenceResolver = new DummyCoreferenceResolver();
		if (processingNamedEntities)
		{
			instruments.getNamedEntityRecognizer().init();
		}
		try
		{
			this.instruments.getParser().init();
			try
			{
				this.instruments.getCoreferenceResolver().init();
				try
				{
					Iterator<TextHypothesisPair> pairsIterator = pairs.iterator();
					while (pairsIterator.hasNext())
					{
						TextHypothesisPair pair = pairsIterator.next();
						logger.debug("Preprocessing pair #"+pair.getId());
						
						SinglePairPreProcessor singlePreProcessor = 
							new SinglePairPreProcessor(pair, makingTextNormalization, processingNamedEntities, instruments);

						singlePreProcessor.preprocess();
						
						pairsData.add(new PairData(pair, singlePreProcessor.getTextTrees(), singlePreProcessor.getHypothesisTree(), singlePreProcessor.getMapTreesToSentences(), singlePreProcessor.getCoreferenceInformation()));
						
						logger.info("pair #"+pair.getId()+" done.");
						logger.info("Current usage of memory: "+Utils.stringMemoryUsedInMB());
					}
				}
				finally
				{
					instruments.getCoreferenceResolver().cleanUp();
				}
			}
			finally
			{
				instruments.getParser().cleanUp();
			}
		}
		finally
		{
			if (processingNamedEntities)
				instruments.getNamedEntityRecognizer().cleanUp();
		}
	}

	
	
	public List<PairData> getPairsData()
	{
		return pairsData;
	}
	
	
	/////////////////////////// PROTECTED AND PRIVATE /////////////////////////////////////////


	private List<TextHypothesisPair> pairs;
	private boolean processingNamedEntities = true;
	private boolean makingTextNormalization = true;
	
	private List<PairData> pairsData;
	
	private Instruments<Info, BasicNode> instruments;
	
	private static Logger logger = Logger.getLogger(PairsPreProcessor.class);
}
