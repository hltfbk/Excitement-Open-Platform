package eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.AbstractFilterEnabledTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessingResult;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndIndex;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistoryComponent;
import eu.excitementproject.eop.biutee.rteflow.macro.search.WithStatisticsTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.micro.TreesGeneratorByOperations;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.SingleOperationItem;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.datastructures.SingleItemSet;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

/**
 * Implementation of LLGS algorithm
 * 
 * @author Asher Stern
 * @since Aug 4, 2013
 *
 */
public abstract class LLGSTextTreesProcessor extends AbstractFilterEnabledTextTreesProcessor implements WithStatisticsTextTreesProcessor
{
	/////////////// PUBLIC ///////////////
	
	public static final int WARNING_IF_EXCEEDS_NUMBER_OF_GLOBAL_ITERATIONS = 20;

	
	public LLGSTextTreesProcessor(String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees, ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			LinearClassifier classifier, Lemmatizer lemmatizer,
			OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment) throws TeEngineMlException
	{
		super(textText, hypothesisText, originalTextTrees, hypothesisTree,
				originalMapTreesToSentences, coreferenceInformation, classifier,
				lemmatizer, script, teSystemEnvironment);
	}
	
	
	public void setNumberOfLocalIterations(int numberOfLocalIterations)
	{
		this.numberOfLocalIterations = numberOfLocalIterations;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.rteflow.macro.search.WithStatisticsTextTreesProcessor#getNumberOfExpandedElements()
	 */
	@Override
	public long getNumberOfExpandedElements()
	{
		return numberOfExpandedElements;
	}


	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.rteflow.macro.search.WithStatisticsTextTreesProcessor#getNumberOfGeneratedElements()
	 */
	@Override
	public long getNumberOfGeneratedElements()
	{
		return numberOfGeneratedElements;
	}
	

	////////////// PRIVATE AND PROTECTED //////////////

	// 1. protected and private methods
	// 2. private nested class
	// 3. fields
	
	
	@Override
	protected void init() throws TeEngineMlException, OperationException, TreeAndParentMapException, AnnotatorException
	{
		super.init();
		hypothesisLemmasLowerCase = TreeUtilities.constructSetLemmasLowerCase(operationsEnvironment.getHypothesis());
		
		numberOfExpandedElements = 0;
		numberOfGeneratedElements = 0;
	}

	
	/*
	 * (non-Javadoc)
	 * @see eu.excitementproject.eop.biutee.rteflow.macro.AbstractFilterEnabledTextTreesProcessor#processSingleTree(eu.excitementproject.eop.biutee.rteflow.macro.TreeAndIndex)
	 */
	@Override
	protected TextTreesProcessingResult processSingleTree(TreeAndIndex tree) throws ClassifierException, TreeAndParentMapException, TeEngineMlException, OperationException, ScriptException, RuleBaseException
	{
		results = new Vector<TextTreesProcessingResult>();
		processTree(tree.getTree(),originalMapTreesToSentences.get(tree.getTree()));
		return findResultWithHighestConfidence(results);
	}
	
	
	@Override
	protected void prepareComputation() throws TeEngineMlException
	{
		// used for GUI
		progressSingleTree = 1.0/((double)numberOfTreesToBeProcessed);
	}
	
	
	//protected abstract double getHeuristicGap(TreeAndParentMap<ExtendedInfo, ExtendedNode> tree, Map<Integer, Double> featureVector) throws GapException;
	protected abstract int calculateNumberOfLocalIterations(int globalIteraion, int actualNumberOfLocalIterations) throws TeEngineMlException;
	protected abstract void prepareGlobalLoop(TreeInProcess currentInProcess) throws TeEngineMlException , TreeAndParentMapException;
	protected abstract void postGlobalLoop(LocalCreativeTreeElement bestElement) throws TeEngineMlException, TreeAndParentMapException;
	protected abstract boolean continueGlobalIteration(TreeInProcess currentInProcess, TreeInProcess previousIterationTree) throws TeEngineMlException, TreeAndParentMapException;
	protected abstract void conditionallyInsertInitialElementToResults(TreeInProcess currentInProcess) throws TeEngineMlException , ClassifierException, TreeAndParentMapException;
	protected abstract void addSingleGoalToResutls(LocalCreativeTreeElement element, String origianlSentence) throws TeEngineMlException, TreeAndParentMapException;
	protected abstract void addResultsFromElements(final String sentence) throws TeEngineMlException, ClassifierException, TreeAndParentMapException;

	/**
	 * Processes a single sentence of the text.
	 * This method adds at least one result to {@link #results}.
	 * @param tree
	 * @param sentence
	 * @throws TreeAndParentMapException
	 * @throws TeEngineMlException
	 * @throws OperationException
	 * @throws ScriptException
	 * @throws RuleBaseException
	 * @throws ClassifierException
	 */
	protected void processTree(ExtendedNode tree, final String sentence) throws TreeAndParentMapException, TeEngineMlException, OperationException, ScriptException, RuleBaseException, ClassifierException
	{
		if (results.size()!=0) throw new TeEngineMlException("Internal bug in "+LLGSTextTreesProcessor.class.getSimpleName()+". results must not contain any element at the beginning of tree processing.");
		int debug_resultsSize = results.size();
		
		TreeInProcess currentInProcess = new TreeInProcess(
				new TreeAndFeatureVector(tree, initialFeatureVector()),
				AbstractNodeUtils.parentMap(tree),sentence,
				new TreeHistory(TreeHistoryComponent.onlyFeatureVector(initialFeatureVector()))
				);
		
		TreeInProcess previousIterationTree = null;
		
		conditionallyInsertInitialElementToResults(currentInProcess);
		
		prepareGlobalLoop(currentInProcess);
		
		int currentIteration = 0;

		// How many local iterations will be performed
		int actualNumberOfLocalIterations = calculateNumberOfLocalIterations(currentIteration,this.numberOfLocalIterations);
		while (continueGlobalIteration(currentInProcess,previousIterationTree))
		{
			if (null==currentInProcess) throw new TeEngineMlException("Internal bug. Starting a global iteration with no element to process.");
			++numberOfExpandedElements;

			// element = all the trees that will be generated in this global iteration
			elements = new LinkedHashSet<LocalCreativeTreeElement>();
			
			// Run TreesGeneratorByOperations. First create the input parameters, then run it.
			Set<TreeAndFeatureVector> treeAsSet = new SingleItemSet<TreeAndFeatureVector>(currentInProcess.getTree());
			ImmutableList<SingleOperationItem> operations = script.getItemListForLocalCreativeIteration(currentIteration, 0, treeAsSet);
			TreesGeneratorByOperations generator =
				new TreesGeneratorByOperations(
						currentInProcess.getTree(),
						operations,
						script,
						currentInProcess.getHistory(),
						operationsEnvironment
						);
			
			generator.generateTrees();
			numberOfGeneratedElements += generator.getGeneratedTrees().size();
			Map<TreeAndFeatureVector,TreeHistory> historyMap = generator.getHistoryMap();
			Map<ExtendedNode, Set<ExtendedNode>> mapAffectedNodes = generator.getMapAffectedNodes();
			for (TreeAndFeatureVector generatedTree : generator.getGeneratedTrees())
			{
				double cost = getCost(generatedTree.getFeatureVector());
				double gap = getHeuristicGap(new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree()),generatedTree.getFeatureVector());
				LocalCreativeTreeElement generatedElement =
					new LocalCreativeTreeElement(generatedTree.getTree(), historyMap.get(generatedTree), generatedTree.getFeatureVector(), 1, currentIteration, mapAffectedNodes.get(generatedTree.getTree()),cost,gap);
				elements.add(generatedElement);
				processElement(generatedElement,currentIteration,actualNumberOfLocalIterations);
			}

			// cost and gap of the tree at the beginning of the global iteration.
			double currentTreeCost = getCost(currentInProcess.getTree().getFeatureVector());
			double currentTreeGap = getHeuristicGap(new TreeAndParentMap<ExtendedInfo, ExtendedNode>(currentInProcess.getTree().getTree(),currentInProcess.getParentMap()),currentInProcess.getTree().getFeatureVector());
			
			previousIterationTree = currentInProcess;
			// Pick the best tree at the end of the iteration - the element that will survive for the next global iteration.
			LocalCreativeTreeElement bestElement = findBest(elements, currentTreeCost, currentTreeGap);
			if (bestElement!=null)
			{
				currentInProcess = new TreeInProcess(new TreeAndFeatureVector(bestElement.getTree(), bestElement.getFeatureVector()),
						AbstractNodeUtils.parentMap(bestElement.getTree()), sentence, bestElement.getHistory());
			}
			else
			{
				if (!hybridGapMode) throw new TeEngineMlException("Internal bug in "+LLGSTextTreesProcessor.class.getSimpleName()+". No element has been generated in a global-loop iteraion, though in pure-transformation mode there must be at least one generated in each iteraion.");
				currentInProcess=null;
			}
			
			currentIteration++;
			
			// warning if works too hard on this pair
			if (0 == (currentIteration%WARNING_IF_EXCEEDS_NUMBER_OF_GLOBAL_ITERATIONS) )
			{
				logger.warn("Too many global iterations, though proof has not been found! Number of global iterations so far is "+currentIteration
						+" The current gap is: "+String.format("%-4.4f", currentTreeGap)+
						" The current cost is: "+String.format("%-4.4f", currentTreeCost)
						);
			}
			
			postGlobalLoop(bestElement);
			
			// How many local iterations will be performed in the next
			// local-lookahead loop.
			// See description in the JavaDoc of the method updateActualNumberOfLocalIterations()
			actualNumberOfLocalIterations = calculateNumberOfLocalIterations(currentIteration,actualNumberOfLocalIterations);
			
			addResultsFromElements(sentence);
		} // end of while loop.
		
		// for GUI
		progressSoFar += progressSingleTree;
		
		if (results.size()<=debug_resultsSize) throw new TeEngineMlException("BUG - Seems that no results were added for the given sentence.");
	}
	
	
	
	/**
	 * The goal of this method is to add more elements to {@link #elements}.
	 * Remember that {@link #elements} is deleted in each iteration in the "while"
	 * loop of {@link #processTree(ExtendedNode, String)}.
	 * @param element
	 * @param globalBaseIteration
	 * @param maxLocalIteration
	 * @throws TeEngineMlException
	 * @throws OperationException
	 * @throws ScriptException
	 * @throws RuleBaseException
	 * @throws TreeAndParentMapException
	 * @throws ClassifierException
	 */
	private void processElement(final LocalCreativeTreeElement element, final int globalBaseIteration, final int maxLocalIteration) throws TeEngineMlException, OperationException, ScriptException, RuleBaseException, TreeAndParentMapException, ClassifierException
	{
		if (element.getLocalIteration()<maxLocalIteration)
		{
			++numberOfExpandedElements;
			TreeAndFeatureVector treeAndFeatureVector = new TreeAndFeatureVector(element.getTree(), element.getFeatureVector());
			Set<TreeAndFeatureVector> treeAsSet = new SingleItemSet<TreeAndFeatureVector>(treeAndFeatureVector);
			ImmutableList<SingleOperationItem> operations = script.getItemListForLocalCreativeIteration(globalBaseIteration, element.getLocalIteration(), treeAsSet);
			TreesGeneratorByOperations generator =
				new TreesGeneratorByOperations(
						treeAndFeatureVector,
						operations,
						script,
						element.getHistory(),
						operationsEnvironment
						);
			
			generator.setAffectedNodes(element.getAffectedNodes()); // this is the most important line here.
			generator.generateTrees();
			numberOfGeneratedElements+=generator.getGeneratedTrees().size();
			Map<TreeAndFeatureVector,TreeHistory> historyMap = generator.getHistoryMap();
			Map<ExtendedNode, Set<ExtendedNode>> mapAffectedNodes = generator.getMapAffectedNodes();
			for (TreeAndFeatureVector generatedTree : generator.getGeneratedTrees())
			{
				double cost = getCost(generatedTree.getFeatureVector());
				double gap = getHeuristicGap(new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree()),generatedTree.getFeatureVector());

				LocalCreativeTreeElement generatedElement = 
					new LocalCreativeTreeElement(generatedTree.getTree(), historyMap.get(generatedTree), generatedTree.getFeatureVector(), element.getLocalIteration()+1, globalBaseIteration, mapAffectedNodes.get(generatedTree.getTree()),cost,gap);

				elements.add(generatedElement);
				
				processElement(generatedElement,globalBaseIteration,maxLocalIteration);
			}
		}

	}
	
	
	/**
	 * Returns the element for which \delta(g)/\delta(h) is the smallest.
	 * @param elements
	 * @param originalCost
	 * @param originalGap
	 * @return
	 * @throws TeEngineMlException
	 */
	protected LocalCreativeTreeElement findBest(Set<LocalCreativeTreeElement> elements, double originalCost, double originalGap) throws TeEngineMlException
	{
		if (elements.size()==0)
		{
			if (hybridGapMode) {return null;}
			else {throw new TeEngineMlException("An error occurred LLGS search. A \"global iteration\" ended with no new generated tree, in pure-transformation mode.");}
		}
		Double bestProportion = null; // null is interpreted as infinity
		LocalCreativeTreeElement bestElement = null;
		for (LocalCreativeTreeElement element : elements)
		{
			Double proportion = null;
			if ((originalGap-element.getGap())>0)
				proportion = (element.getCost()-originalCost)/(originalGap-element.getGap());
			if (null==bestProportion) // current best is infinity, so everything is better that the current best.
			{
				bestProportion=proportion;
				bestElement = element;
			}
			else
			{
				if (proportion!=null)
				{
					if (proportion<bestProportion)
					{
						bestProportion=proportion;
						bestElement=element;
					}
				}
			}
		}
		
		return bestElement;
	}
	
	
	
	protected double getCost(Map<Integer,Double> featureVector) throws ClassifierException
	{
		return -this.classifier.getProduct(featureVector);
	}
	
	
	
	protected static class TreeInProcess extends TextTreesProcessingResult
	{
		public TreeInProcess(TreeAndFeatureVector tree, Map<ExtendedNode,ExtendedNode> parentMap,
				String sentence, TreeHistory history)
		{
			super(tree, sentence, history);
			this.parentMap = parentMap;
		}
		
		public Map<ExtendedNode, ExtendedNode> getParentMap()
		{
			return parentMap;
		}

		private final Map<ExtendedNode,ExtendedNode> parentMap;
	}

	

	
	// input
	
	// For example: 3 means son, grandson, great-grandson
	protected int numberOfLocalIterations = BiuteeConstants.LOCAL_CREATIVE_NUMBER_OF_LOCAL_ITERATIONS;

	// internals
	protected Vector<TextTreesProcessingResult> results; // initializes for each (tree) sentence.
	protected Set<String> hypothesisLemmasLowerCase; //= TreeUtilities.constructSetLemmasLowerCase(hypothesis);
	protected Set<LocalCreativeTreeElement> elements; // initializes for each global iteration.
	
	// used for GUI
	protected double progressSoFar = 0.0;
	protected double progressSingleTree = 1.0;

	
	// output
	protected long numberOfExpandedElements = 0;
	protected long numberOfGeneratedElements = 0;

	


	private static final Logger logger = Logger.getLogger(LLGSTextTreesProcessor.class);
}
