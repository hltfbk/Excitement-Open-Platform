package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.utilities.StopFlag;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


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
		if (BiuteeConstants.PRINT_TIME_STATISTICS)
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
