package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.GlobalPairInformation;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.WithStatisticsTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.astar.AStarTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.HypothesisInformation;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.common.datastructures.Table;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * <B>No longer used</B>
 * <P>
 * This class was used during some test when writing the paper about
 * the search algorithm.
 * <P>
 * TODO: This is not a very nice code.
 * 
 * 
 * @author Asher Stern
 * @since Jun 19, 2011
 *
 */
public class PairProcessorForStatistics extends PairProcessor
{
	public PairProcessorForStatistics(ExtendedPairData pairData,
			LinearClassifier classifier, Lemmatizer lemmatizer,
			OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment,
			Table<String, Double> costTable, Table<String,Long> timeTable,
			Table<String, Long> expandedTable,
			Table<String, Long> generatedeTable)
	{
		super(pairData, classifier, lemmatizer, script, teSystemEnvironment);
		this.costTable = costTable;
		this.timeTable = timeTable;
		this.expandedTable = expandedTable;
		this.generatedeTable = generatedeTable;
	}
	
	public void process() throws TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, AnnotatorException
	{
		try
		{
			String id = this.pairData.getPair().getId().toString();
			id = "(pair #"+id+") ";
			threadMXBean = ManagementFactory.getThreadMXBean();
			
			this.script.setHypothesisInformation(new HypothesisInformation(pairData.getPair().getHypothesis(), pairData.getHypothesisTree()));
			
			String algorithmName;
			AStarTextTreesProcessor astarTextTreesProcessor = null;
			
			algorithmName = "Caches filler (A* Future = 5 Cost = 1)";
			logger.info(id+"Staring "+algorithmName);
			astarTextTreesProcessor = new AStarTextTreesProcessor(pairData.getPair().getText(), pairData.getPair().getHypothesis(), pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment);
			astarTextTreesProcessor.setWhenEqualTakeAll(false);
			astarTextTreesProcessor.setWeightOfCost(1);
			astarTextTreesProcessor.setWeightOfFuture(5);
			process(astarTextTreesProcessor,algorithmName);
			
			this.bestTree = astarTextTreesProcessor.getBestTree();
			this.bestTreeHistory = astarTextTreesProcessor.getBestTreeHistory();
			this.bestTreeSentence = astarTextTreesProcessor.getBestTreeSentence();

			astarTextTreesProcessor = null;
			logger.info("Done.");
			

			// parameters for Weighted A*
			
//			algorithmName = "Weighted A* Future = 4 Cost = 1";
//			logger.info(id+"Staring "+algorithmName);
//			astarTextTreesProcessor = new AStarTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment);
//			astarTextTreesProcessor.setWhenEqualTakeAll(false);
//			astarTextTreesProcessor.setWeightOfCost(1);
//			astarTextTreesProcessor.setWeightOfFuture(4);
//			process(astarTextTreesProcessor,algorithmName);
//			astarTextTreesProcessor = null;
//			logger.info("Done.");
//
//
//			algorithmName = "Weighted A* Future = 3 Cost = 1";
//			logger.info(id+"Staring "+algorithmName);
//			astarTextTreesProcessor = new AStarTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment);
//			astarTextTreesProcessor.setWhenEqualTakeAll(false);
//			astarTextTreesProcessor.setWeightOfCost(1);
//			astarTextTreesProcessor.setWeightOfFuture(3);
//			process(astarTextTreesProcessor,algorithmName);
//			astarTextTreesProcessor = null;
//			logger.info("Done.");


//			algorithmName = "Weighted A* Future = 2 Cost = 1";
//			logger.info(id+"Staring "+algorithmName);
//			astarTextTreesProcessor = new AStarTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment);
//			astarTextTreesProcessor.setWhenEqualTakeAll(false);
//			astarTextTreesProcessor.setWeightOfCost(1);
//			astarTextTreesProcessor.setWeightOfFuture(2);
//			process(astarTextTreesProcessor,algorithmName);
//			astarTextTreesProcessor = null;
//			logger.info("Done.");
			

			// parameters for K Weighted A*
			



			algorithmName = "K Weighted A* Future = 5 Cost = 1 K = 2";
			logger.info(id+"Staring "+algorithmName);
			astarTextTreesProcessor = new AStarTextTreesProcessor(pairData.getPair().getText(), pairData.getPair().getHypothesis(), pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment);
			astarTextTreesProcessor.setWhenEqualTakeAll(false);
			astarTextTreesProcessor.setWeightOfCost(1);
			astarTextTreesProcessor.setWeightOfFuture(5);
			astarTextTreesProcessor.setK_expandInEachIteration(2);
			process(astarTextTreesProcessor,algorithmName);
			astarTextTreesProcessor = null;
			logger.info("Done.");

			algorithmName = "K Weighted A* Future = 5 Cost = 1 K = 5";
			logger.info(id+"Staring "+algorithmName);
			astarTextTreesProcessor = new AStarTextTreesProcessor(pairData.getPair().getText(), pairData.getPair().getHypothesis(), pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment);
			astarTextTreesProcessor.setWhenEqualTakeAll(false);
			astarTextTreesProcessor.setWeightOfCost(1);
			astarTextTreesProcessor.setWeightOfFuture(5);
			astarTextTreesProcessor.setK_expandInEachIteration(5);
			process(astarTextTreesProcessor,algorithmName);
			astarTextTreesProcessor = null;
			logger.info("Done.");

			
			algorithmName = "K Weighted A* Future = 5 Cost = 1 K = 10";
			logger.info(id+"Staring "+algorithmName);
			astarTextTreesProcessor = new AStarTextTreesProcessor(pairData.getPair().getText(), pairData.getPair().getHypothesis(), pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment);
			astarTextTreesProcessor.setWhenEqualTakeAll(false);
			astarTextTreesProcessor.setWeightOfCost(1);
			astarTextTreesProcessor.setWeightOfFuture(5);
			astarTextTreesProcessor.setK_expandInEachIteration(10);
			process(astarTextTreesProcessor,algorithmName);
			astarTextTreesProcessor = null;
			logger.info("Done.");


			algorithmName = "K Weighted A* Future = 5 Cost = 1 K = 20";
			logger.info(id+"Staring "+algorithmName);
			astarTextTreesProcessor = new AStarTextTreesProcessor(pairData.getPair().getText(), pairData.getPair().getHypothesis(), pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment);
			astarTextTreesProcessor.setWhenEqualTakeAll(false);
			astarTextTreesProcessor.setWeightOfCost(1);
			astarTextTreesProcessor.setWeightOfFuture(5);
			astarTextTreesProcessor.setK_expandInEachIteration(20);
			process(astarTextTreesProcessor,algorithmName);
			astarTextTreesProcessor = null;
			logger.info("Done.");

			
			algorithmName = "K Weighted A* Future = 5 Cost = 1 K = 50";
			logger.info(id+"Staring "+algorithmName);
			astarTextTreesProcessor = new AStarTextTreesProcessor(pairData.getPair().getText(), pairData.getPair().getHypothesis(), pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment);
			astarTextTreesProcessor.setWhenEqualTakeAll(false);
			astarTextTreesProcessor.setWeightOfCost(1);
			astarTextTreesProcessor.setWeightOfFuture(5);
			astarTextTreesProcessor.setK_expandInEachIteration(50);
			process(astarTextTreesProcessor,algorithmName);
			astarTextTreesProcessor = null;
			logger.info("Done.");

			
			algorithmName = "K Weighted A* Future = 5 Cost = 1 K = 100";
			logger.info(id+"Staring "+algorithmName);
			astarTextTreesProcessor = new AStarTextTreesProcessor(pairData.getPair().getText(), pairData.getPair().getHypothesis(), pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment);
			astarTextTreesProcessor.setWhenEqualTakeAll(false);
			astarTextTreesProcessor.setWeightOfCost(1);
			astarTextTreesProcessor.setWeightOfFuture(5);
			astarTextTreesProcessor.setK_expandInEachIteration(100);
			process(astarTextTreesProcessor,algorithmName);
			astarTextTreesProcessor = null;
			logger.info("Done.");
			


			
			

//			// parameters for K Staged Weighted A*
//			
//			for (int bigK=40;bigK<=280;bigK+=40)
//			{
//				for (int smallK=40;smallK<=bigK;smallK+=40)
//				{
//					algorithmName = "K Staged Weighted A* Future = 3 Cost = 1 bigK = "+bigK+" smallK = "+smallK;
//					logger.info(id+"Staring "+algorithmName);
//					KStagedTextTreesProcessor kStagedTextTreesProcessor =
//						new KStagedTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment,smallK,bigK,5,1.0,3.0);
//					process(kStagedTextTreesProcessor,algorithmName);
//					kStagedTextTreesProcessor = null;
//					logger.info("Done.");
//				}
//			}
//
			

//			// parameters for local creative
//
//			for (int lcDepth=5;lcDepth>=1;lcDepth--)
//			{
//				algorithmName = "Local Creative depth = "+lcDepth;
//				logger.info(id+"Staring "+algorithmName);
//				LocalCreativeTextTreesProcessor localCreativeTextTreesProcessor =
//					new LocalCreativeTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment);
//				localCreativeTextTreesProcessor.setNumberOfLocalIterations(lcDepth);
//				process(localCreativeTextTreesProcessor,algorithmName);
//				localCreativeTextTreesProcessor = null;
//				logger.info("Done.");
//			}

			
			


			
			
			
//			// mixed
//			// change expanding method
//			
//			algorithmName = "Weighted A* with local creative";
//			logger.info(id+"Staring "+algorithmName);
//			AStarLocalCreativeTextTreesProcessor aStarLocalCreativeTextTreesProcessor =
//				new AStarLocalCreativeTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment);
//			aStarLocalCreativeTextTreesProcessor.setWeightOfCost(1);
//			aStarLocalCreativeTextTreesProcessor.setWeightOfFuture(5);
//			aStarLocalCreativeTextTreesProcessor.setCompareByCostPlusFuture(true);
//			process(aStarLocalCreativeTextTreesProcessor,algorithmName);
//			aStarLocalCreativeTextTreesProcessor = null;
//			logger.info("Done.");
//			
//
//			algorithmName = "K Weighted A* with local creative K = 10";
//			logger.info(id+"Staring "+algorithmName);
//			aStarLocalCreativeTextTreesProcessor =
//				new AStarLocalCreativeTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment);
//			aStarLocalCreativeTextTreesProcessor.setWeightOfCost(1);
//			aStarLocalCreativeTextTreesProcessor.setWeightOfFuture(5);
//			aStarLocalCreativeTextTreesProcessor.setCompareByCostPlusFuture(true);
//			aStarLocalCreativeTextTreesProcessor.setK_expandInEachIteration(10);
//			process(aStarLocalCreativeTextTreesProcessor,algorithmName);
//			aStarLocalCreativeTextTreesProcessor = null;
//			logger.info("Done.");
//
//			
//			algorithmName = "K Staged Weighted A* with local creative K = 50 k = 50";
//			logger.info(id+"Staring "+algorithmName);
//			KStagedTextTreesProcessor kStagedTextTreesProcessor =
//				new KStagedTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,teSystemEnvironment, 50, 50, 5, 1, 5);
//			kStagedTextTreesProcessor.setLocalCreativeMode(true);
//			process(kStagedTextTreesProcessor,algorithmName);
//			kStagedTextTreesProcessor = null;
//			logger.info("Done.");
			
			
		}
		catch(TreeAndParentMapException e)
		{
			throw new TeEngineMlException("Processing failed. See nested exception",e);
		}
	}
	
	protected void process(WithStatisticsTextTreesProcessor processor, String processorDescription) throws TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, TreeAndParentMapException, AnnotatorException
	{
		processImpl(processor, processorDescription);
		String id = this.pairData.getPair().getId().toString();
		expandedTable.put(id, processorDescription, processor.getNumberOfExpandedElements());
		generatedeTable.put(id, processorDescription, processor.getNumberOfGeneratedElements());
	}
	
	protected void process(TextTreesProcessor processor, String processorDescription) throws TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, TreeAndParentMapException, AnnotatorException
	{
		processImpl(processor, processorDescription);
	}
	
	protected void processImpl(TextTreesProcessor processor, String processorDescription) throws TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, TreeAndParentMapException, AnnotatorException
	{
		setTaskNameInTextTreesProcessor(processor);
		long startNano = getCurrentCpuTime();
		processor.process();
		long endNano = getCurrentCpuTime();
		
		long elapsed = endNano-startNano;
		String id = this.pairData.getPair().getId().toString();
		timeTable.put(id, processorDescription, elapsed);
		Map<Integer, Double> featureVector =  processor.getBestTree().getFeatureVector();
		double cost = -this.classifier.getProduct(featureVector);
		costTable.put(id, processorDescription,cost);
		
		logSummary(processor, processorDescription, cost);
	}
	
	protected void setTaskNameInTextTreesProcessor(TextTreesProcessor ttProcessor)
	{
		if (ignoreTaskName)
			logger.debug("Ignoring task name");
		else
		{
			String taskName = pairData.getPair().getAdditionalInfo();
			if (logger.isDebugEnabled()){logger.debug("Setting task name = "+taskName);}
			ttProcessor.setGlobalPairInformation(new GlobalPairInformation(taskName));
		}

	}
	
	protected void logSummary(TextTreesProcessor processor, String processorDescription, double cost) throws TeEngineMlException
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Summary for ");
		sb.append(processorDescription);
		sb.append(":\n");
		sb.append("Text sentence: ");
		sb.append(processor.getBestTreeSentence());
		sb.append("\nHypothesis: ");
		sb.append(this.pairData.getPair().getHypothesis());
		sb.append("\nCost = ");
		sb.append(String.format("%4.5f", cost));
		sb.append("\n");
		
		
		ImmutableList<Specification> specs =  processor.getBestTreeHistory().getSpecifications();
		for (Specification spec : specs)
		{
			sb.append(spec.toString());
			sb.append("\n");
		}
		
		logger.info(sb.toString());
	}
	
	protected long getCurrentCpuTime()
	{
		return this.threadMXBean.isCurrentThreadCpuTimeSupported() ?
				this.threadMXBean.getCurrentThreadCpuTime( ) : 0L;
	}


	

	
	protected Table<String, Double> costTable;
	protected Table<String,Long> timeTable;
	protected Table<String, Long> expandedTable;
	protected Table<String, Long> generatedeTable;
	

	protected ThreadMXBean threadMXBean;
	
	private static final Logger logger = Logger.getLogger(PairProcessorForStatistics.class);
}
