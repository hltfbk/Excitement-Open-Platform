package ac.biu.nlp.nlp.engineml.rteflow.systems.rtepairs;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.LabeledSample;
import ac.biu.nlp.nlp.engineml.classifiers.LinearTrainableStorableClassifier;
import ac.biu.nlp.nlp.engineml.generic.truthteller.AnnotatorException;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TreeAndFeatureVector;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants;
import ac.biu.nlp.nlp.engineml.rteflow.systems.TESystemEnvironment;
import ac.biu.nlp.nlp.engineml.script.OperationsScript;
import ac.biu.nlp.nlp.engineml.script.ScriptException;
import ac.biu.nlp.nlp.engineml.utilities.StopFlag;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.lap.biu.en.lemmatizer.Lemmatizer;


/**
 * Processes a list of {@linkplain PairData}s.
 * The results are stored in a map from {@linkplain PairData} to
 * {@link PairProcessResult}.
 * 
 * @author Asher Stern
 * @since Apr 3, 2011
 *
 */
public class ListOfPairsProcessor
{
	public ListOfPairsProcessor(StopFlag stopFlag,
			List<ExtendedPairData> pairs,
			OperationsScript<Info, BasicNode> script,
			LinearTrainableStorableClassifier classifierForSearch,
			Lemmatizer lemmatizer,
			TESystemEnvironment teSystemEnvironment
			)
	{
		super();
		this.stopFlag = stopFlag;
		this.pairs = pairs;
		this.script = script;
		this.classifierForSearch = classifierForSearch;
		this.lemmatizer = lemmatizer;
		this.teSystemEnvironment = teSystemEnvironment;
	}

	public void processList() throws TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, AnnotatorException
	{
		Iterator<ExtendedPairData> pairsIterator = pairs.iterator();
		// for (ExtendedPairData pair : pairs)
		while ( (!stopFlag.isStop()) && (pairsIterator.hasNext()) )
		{
			ExtendedPairData pair = pairsIterator.next();
			logger.info("Processing pair: "+((pair.getDatasetName()!=null)?pair.getDatasetName()+": ":"") + pair.getPair().getId());
			// logger.info("Processing pair: "+pair.getPair().getId());
			boolean noProblem = false;
			try
			{
				processPair(pair);
				noProblem = true;
			}
			finally
			{
				if (!noProblem)this.stopFlag.stop();
			}
			
			
			if (logger.isInfoEnabled())
			{
				logger.info("Pair processing done. Memory used: "+Utils.stringMemoryUsedInMB());
			}
		}
	}
	
	
	
	public LinkedHashMap<ExtendedPairData, PairProcessResult> getResults()
	{
		return results;
	}

	private void processPair(ExtendedPairData pair) throws TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, AnnotatorException
	{
		if (null==classifierForSearch) throw new TeEngineMlException("Null classifierForSearch");
		PairProcessor processor = new PairProcessor(pair, classifierForSearch, lemmatizer, script, this.teSystemEnvironment);
		processor.process();
		TreeAndFeatureVector treeAndFeatureVector = processor.getBestTree();
		LabeledSample sample = new LabeledSample(treeAndFeatureVector.getFeatureVector(), pair.getPair().getBooleanClassificationType().booleanValue());
		PairProcessResult result;
		if (Constants.PRINT_TIME_STATISTICS)
		{
			result = new PairProcessResult(treeAndFeatureVector.getTree(), treeAndFeatureVector.getFeatureVector(), processor.getBestTreeSentence(), pair, processor.getBestTreeHistory(), sample, processor.getCpuTime(),processor.getWorldClockTime());
		}
		else
		{
			result = new PairProcessResult(treeAndFeatureVector.getTree(), treeAndFeatureVector.getFeatureVector(), processor.getBestTreeSentence(), pair, processor.getBestTreeHistory(), sample);
		}
		
		results.put(pair, result);
	}
	
	
	protected StopFlag stopFlag;
	private List<ExtendedPairData> pairs;
	private OperationsScript<Info, BasicNode> script; // assuming already initialized
	private LinearTrainableStorableClassifier classifierForSearch;
	private Lemmatizer lemmatizer; // assuming already initialized
	private TESystemEnvironment teSystemEnvironment;
	
	private LinkedHashMap<ExtendedPairData, PairProcessResult> results = new LinkedHashMap<ExtendedPairData, PairProcessResult>();
	
	private Logger logger = Logger.getLogger(ListOfPairsProcessor.class);
}
