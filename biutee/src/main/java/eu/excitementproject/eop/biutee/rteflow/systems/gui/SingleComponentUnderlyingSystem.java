package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_PAIRS_GUI_CLASSIFIER_FOR_PREDICTIONS_IS_DUMMY;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_TEST_SAMPLES_FOR_SEARCH_CLASSIFIER;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_TEST_SEARCH_CLASSIFIER_REASONABLE_GUESS;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_TEST_SERIALIZED_SAMPLES_NAME;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierFactory;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.TrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.dummy.DummyAllTrueClassifier;
import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.biutee.rteflow.macro.GlobalPairInformation;
import eu.excitementproject.eop.biutee.rteflow.macro.OriginalTreesAfterInitialization;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistoryComponent;
import eu.excitementproject.eop.biutee.rteflow.systems.RTESystemsUtils;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.ExtendedPairData;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.RTEPairsBaseSystem;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.RTESumSurroundingSentencesUtility;
import eu.excitementproject.eop.biutee.script.HypothesisInformation;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.ScriptFactory;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.biutee.utilities.safemodel.SafeSamples;
import eu.excitementproject.eop.biutee.utilities.safemodel.SafeSamplesUtils;
import eu.excitementproject.eop.biutee.utilities.safemodel.classifiers_io.SafeClassifiersIO;
import eu.excitementproject.eop.common.datastructures.DummySet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.DatasetParameterValueParser;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
/**
 * 
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and
 * should be improved. Need to re-design, make it more modular,
 * adding documentation and improve code.
 * 
 * @author Asher Stern
 * @since May 25, 2011
 *
 */
public class SingleComponentUnderlyingSystem extends RTEPairsBaseSystem
{
	public SingleComponentUnderlyingSystem(GuiRteSumUtilities sumUtilities,
			String configurationFileName,
			Boolean useF1Classifier)
	{
		super(configurationFileName,ConfigurationParametersNames.RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
		this.pairData = null;
		this.sumUtilitis = sumUtilities;
		this.useF1ClassifierFalseForAccuracyNullForXMLModelFile = useF1Classifier;
	}

	public void init() throws ConfigurationFileDuplicateKeyException, ConfigurationException, MalformedURLException, LemmatizerException, TeEngineMlException, IOException, PluginAdministrationException
	{
		super.init();
		initialized=false;
		
		try
		{
			DatasetParameterValueParser parameterParser = constructDatasetParameterParser();
			updateFeatureVectorStructure(parameterParser);

			script = new ScriptFactory(configurationFile,this.teSystemEnvironment.getPluginRegistry()).getDefaultScript();
			script.init();
			scriptInitialized = true;
			completeInitializationWithScript(script);
			
			// TODO (comment by Asher Stern): Ugly Ugly code
			if (useF1ClassifierFalseForAccuracyNullForXMLModelFile!=null)
			{
				initClassifierByLabeledSamples();
			}
			else
			{
				boolean useReasonableGuess = false;
				if (configurationParams.containsKey(RTE_TEST_SEARCH_CLASSIFIER_REASONABLE_GUESS))
				{
					useReasonableGuess = configurationParams.getBoolean(RTE_TEST_SEARCH_CLASSIFIER_REASONABLE_GUESS);
				}

				if (useReasonableGuess)
				{
					this.classifier = RTESystemsUtils.reasonableGuessClassifier(teSystemEnvironment.getFeatureVectorStructureOrganizer());
				}
				else if (configurationParams.containsKey(ConfigurationParametersNames.RTE_TEST_SEARCH_MODEL))
				{
					File modelFile = configurationParams.getFile(ConfigurationParametersNames.RTE_TEST_SEARCH_MODEL);
					logger.info("Loading classifier for search for test from file: "+modelFile.getPath());
					LinearClassifier ret =  SafeClassifiersIO.loadLinearClassifier(teSystemEnvironment.getFeatureVectorStructureOrganizer(), modelFile);
					ret.setFeaturesNames(teSystemEnvironment.getFeatureVectorStructureOrganizer().createMapOfFeatureNames());
					this.classifier = ret;
				}
				else
				{
					throw new TeEngineMlException("Cannot locate the model file parameter in the configuration file, for classifier-for-search.");
				}
			}
			
			
			logger.info("Classifier for search description:\n"+classifier.descriptionOfTraining());

			boolean dummyClassifierForPredictions = false;
			if (configurationParams.containsKey(RTE_PAIRS_GUI_CLASSIFIER_FOR_PREDICTIONS_IS_DUMMY))
			{
				dummyClassifierForPredictions = configurationParams.getBoolean(RTE_PAIRS_GUI_CLASSIFIER_FOR_PREDICTIONS_IS_DUMMY);
			}
			if (dummyClassifierForPredictions)
			{
				this.classifierForPredictions = new DummyAllTrueClassifier();
			}
			else
			{
				// TODO (comment by Asher Stern): Ugly Ugly code
				if (null==useF1ClassifierFalseForAccuracyNullForXMLModelFile)
				{
					if (configurationParams.containsKey(ConfigurationParametersNames.RTE_TEST_PREDICTIONS_MODEL))
					{
						File modelFile = configurationParams.getFile(ConfigurationParametersNames.RTE_TEST_PREDICTIONS_MODEL);
						logger.info("Loading classifier for predictions for test from file: "+modelFile.getPath());
						Classifier ret =  SafeClassifiersIO.load(teSystemEnvironment.getFeatureVectorStructureOrganizer(), modelFile);
						ret.setFeaturesNames(teSystemEnvironment.getFeatureVectorStructureOrganizer().createMapOfFeatureNames());
						this.classifierForPredictions = ret;
					}
					else
					{
						throw new TeEngineMlException("Could not locate the model file parameter for classifier-for-prediction, in the configuration file.");
					}
				}
				else
				{
					TrainableStorableClassifier tscClassifierForPredictions;
					// TODO (Comment by Asher Stern:) Ugly code. Should arrange the code here and in RTESYstemUtils.
					if (useF1ClassifierFalseForAccuracyNullForXMLModelFile)
					{
						tscClassifierForPredictions = new ClassifierFactory().getF1Classifier();
					}
					else
					{
						tscClassifierForPredictions = new ClassifierFactory().getDefaultClassifier();
					}
					File samplesSerFile = configurationParams.getFile(RTE_TEST_SERIALIZED_SAMPLES_NAME);
					SafeSamples safeSamples = SafeSamplesUtils.load(samplesSerFile, teSystemEnvironment.getFeatureVectorStructureOrganizer());
					tscClassifierForPredictions.train(safeSamples.getSamples());
					tscClassifierForPredictions.setFeaturesNames(teSystemEnvironment.getFeatureVectorStructureOrganizer().createMapOfFeatureNames());
					this.classifierForPredictions = tscClassifierForPredictions;
				}
			}
			logger.info("classifierForPredictions:\n"+this.classifierForPredictions.descriptionOfTraining());

			// We need only one instance of lemmatizer.
			this.lemmatizer = this.getLemmatizer();

			// setPair(pairData,taskName);
		}
		catch (OperationException e)
		{
			throw new TeEngineMlException("init failed",e);
		}
		catch (ClassifierException e)
		{
			throw new TeEngineMlException("init failed",e);
		}
		catch (ClassNotFoundException e)
		{
			throw new TeEngineMlException("init failed",e);
		}

		logger.info("Initialization complete.");
		initialized=true;
	}
	
	public ImmutableSet<String> getAllowedDatasetNames() throws TeEngineMlException
	{
		if (null==this.teSystemEnvironment.getFeatureVectorStructureOrganizer().getDynamicGlobalFeatures())
		{
			return new ImmutableSetWrapper<String>(new DummySet<String>());
		}
		else
		{
			return this.teSystemEnvironment.getFeatureVectorStructureOrganizer().getDynamicGlobalFeatures().keySet();
		}
	}
	
	public void setPair(ExtendedPairData pairData, String taskName) throws TeEngineMlException, OperationException, TreeAndParentMapException, ClassifierException, VisualTracingToolException, AnnotatorException, TreeStringGeneratorException, TreeCoreferenceInformationException, PluginAdministrationException
	{
		this.pairData = pairData;
		this.taskName = taskName;
		this.script.setHypothesisInformation(new HypothesisInformation(this.pairData.getPair().getHypothesis(), this.pairData.getHypothesisTree()));
		SingleTreeComponent.resetIds();
		initSurroundingUtility();
		generator = new TreesGeneratorOneIterationSingleTree(this.pairData.getPair().getText(), this.pairData.getPair().getHypothesis(), this.pairData.getTextTrees(), this.pairData.getHypothesisTree(), this.pairData.getMapTreesToSentences(), this.pairData.getCoreferenceInformation(), classifier, lemmatizer, script, classifierForPredictions, this.teSystemEnvironment);
		if (surroundingUtility!=null)
		{
			List<ExtendedNode> surroundingsContext =surroundingUtility.getSurroundingSentences(sumUtilitis.getSentenceID(), pairData.getTextTrees().iterator().next());
			generator.setSurroundingsContext(surroundingsContext);
		}
		generator.setGlobalPairInformation(new GlobalPairInformation(this.taskName));
		logger.info("Visual Tracing Tool: Initializing Tree-Generator of the underlying system...");
		generator.init();
		logger.info("Visual Tracing Tool: Initialization of Tree-Generator of the underlying system - Done.");
		
		originalTextTrees = generator.getOriginalTextTrees();
		hypothesisTree = generator.getHypothesisTree();
		originalTreesAfterInitialization = generator.getOriginalTreesAfterInitialization();
		
		initTreesComponents();
	}
	
	
	public void cleanUp()
	{
		lemmatizer.cleanUp();
		if (scriptInitialized)
		{
			script.cleanUp();
		}
	}
	
	
	public List<ExtendedNode> getOriginalTextTrees()
	{
		return originalTextTrees;
	}

	public ExtendedNode getHypothesisTree()
	{
		return hypothesisTree;
	}
	
	

	public OriginalTreesAfterInitialization getOriginalTreesAfterInitialization()
	{
		return originalTreesAfterInitialization;
	}
	
	public TESystemEnvironment getTeSystemEnvironment()
	{
		return this.teSystemEnvironment;
	}
	

	public void generateTreesFor(int index) throws TeEngineMlException, OperationException, ScriptException, RuleBaseException, TreeAndParentMapException, ClassifierException, VisualTracingToolException
	{
		if (!initialized)throw new VisualTracingToolException("Not initialized");
		if (index>=allTrees.size()) throw new VisualTracingToolException("Index not found: "+index);
		SingleTreeComponent requestedComponent = allTrees.get(index);
		this.lastRequestGeneratedTrees = generator.generate(requestedComponent,allTrees.size());
		this.allTrees.addAll(lastRequestGeneratedTrees);
	}
	
	

	public Vector<SingleTreeComponent> getAllTrees() throws VisualTracingToolException
	{
		if (!initialized)throw new VisualTracingToolException("Not initialized");
		return allTrees;
	}

	public List<SingleTreeComponent> getLastRequestGeneratedTrees() throws VisualTracingToolException
	{
		if (!initialized)throw new VisualTracingToolException("Not initialized");
		return lastRequestGeneratedTrees;
	}
	
	public Map<ExtendedNode, String> getMapTreeToSentence() throws VisualTracingToolException
	{
		if (!initialized)throw new VisualTracingToolException("Not initialized");
		return generator.getMapTreeToSentence();
	}
	
	public PairProcessor getRegularPairProcessor() throws VisualTracingToolException, TeEngineMlException
	{
		return getRegularPairProcessor(false);
	}
	public PairProcessor getRegularPairProcessor(boolean useOldBeamSearch) throws VisualTracingToolException, TeEngineMlException
	{
		if (!initialized)throw new VisualTracingToolException("Not initialized");
		PairProcessor ret = new PairProcessor(pairData, classifier, lemmatizer, script, this.teSystemEnvironment);
		if (useOldBeamSearch) ret.setUseOldBeam(true);
		if (surroundingUtility!=null)
		{
			List<ExtendedNode> surroundingsContext =surroundingUtility.getSurroundingSentences(sumUtilitis.getSentenceID(), pairData.getTextTrees().iterator().next());
			ret.overrideSurroundingsContext(surroundingsContext);
		}
		ret.setRichInformationInTreeHistory(true);
		if (taskName.equals(VisualTracingTool.IGNORE_TASK_NAME_STRING))
		{
			ret.setIgnoreTaskName(true);
		}
		return ret;
	}
	
	public List<ExtendedNode> getSurroundingTrees() throws TeEngineMlException
	{
		List<ExtendedNode> ret = null;
		if (null==this.surroundingUtility)
		{
			ret = null;
		}
		else
		{
			ret = surroundingUtility.getSurroundingSentences(sumUtilitis.getSentenceID(), pairData.getTextTrees().iterator().next());
		}
		return ret;
	}
	

	public LinearClassifier getClassifier() throws VisualTracingToolException
	{
		if (!initialized)throw new VisualTracingToolException("Not initialized");
		return classifier;
	}

	public Classifier getClassifierForPredictions() throws VisualTracingToolException
	{
		if (!initialized)throw new VisualTracingToolException("Not initialized");
		return classifierForPredictions;
	}
	
	 

	private void initClassifierByLabeledSamples() throws OperationException, ClassifierException, TeEngineMlException, ConfigurationException, IOException, ClassNotFoundException
	{
		if (useF1ClassifierFalseForAccuracyNullForXMLModelFile)
		{
			// TODO (comment by Asher Stern): Ugly code. Should somehow make things clear in RTESystemUtils and here.
			boolean useReasonableGuess = false;
			if (configurationParams.containsKey(RTE_TEST_SEARCH_CLASSIFIER_REASONABLE_GUESS))
			{
				useReasonableGuess = configurationParams.getBoolean(RTE_TEST_SEARCH_CLASSIFIER_REASONABLE_GUESS);
			}

			if (useReasonableGuess)
			{
				this.classifier = RTESystemsUtils.reasonableGuessClassifier(teSystemEnvironment.getFeatureVectorStructureOrganizer());
			}
			else
			{
				LinearTrainableStorableClassifier ltscClassifier;
				ltscClassifier = new ClassifierFactory().getF1Classifier();
				File fileSerializedSamples = configurationParams.getFile(RTE_TEST_SAMPLES_FOR_SEARCH_CLASSIFIER);
				SafeSamples safeSamples = SafeSamplesUtils.load(fileSerializedSamples, this.teSystemEnvironment.getFeatureVectorStructureOrganizer());
				ltscClassifier.train(safeSamples.getSamples());
				RTESystemsUtils.normalizeClassifierForSearch(ltscClassifier);
				this.classifier = ltscClassifier;
			}
			this.classifier.setFeaturesNames( ClassifierUtils.extendFeatureNames(Feature.toMapOfNames(), script.getRuleBasesNames()));
		}
		else
		{
			this.classifier = RTESystemsUtils.searchClassifierForTest(configurationParams, teSystemEnvironment.getFeatureVectorStructureOrganizer());	
		}
	}
	
	
	private void initTreesComponents() throws ClassifierException, TreeAndParentMapException, VisualTracingToolException, TeEngineMlException, PluginAdministrationException
	{
		allTrees = new Vector<SingleTreeComponent>();
		lastRequestGeneratedTrees = allTrees;
		int nextId = allTrees.size();
		int originalTextTreeNo = 1;
		for (ExtendedNode tree : originalTextTrees)
		{
			TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
				new TreeAndParentMap<ExtendedInfo, ExtendedNode>(tree);
//			InitialFeatureVectorUtility utility = new InitialFeatureVectorUtility(teSystemEnvironment, script, tree, generator.getHypothesisNumberOfNodes()); -- HEY That's wrong. The tree should be hypothesis tree! not text tree!
//			utility.setGlobalPairInformation(new GlobalPairInformation(this.taskName));
//			Map<Integer, Double> featureVector = utility.initialFeatureVector();
			Map<Integer, Double> featureVector = generator.getInitialFeatureVector();
			double classificationScore = classifier.classify(featureVector);
			double classificationScoreForPredictions = classifierForPredictions.classify(featureVector);
			double evaluation = this.generator.calculateEvaluation(treeAndParentMap, classificationScore, 0);
			double cost = -classifier.getProduct(featureVector);
			
			Set<ExtendedNode> missingNodes =
					new AlignmentCalculator(teSystemEnvironment.getAlignmentCriteria(), treeAndParentMap, generator.getHypothesis()).getMissingTriples();
//			if (AdvancedEqualities.USE_ADVANCED_EQUALITIES)
//			{
//				missingNodes = AdvancedEqualities.findMissingRelations(treeAndParentMap, generator.getHypothesis());
//			}
//			else
//			{
//				missingNodes = TreeUtilities.findRelationsNoMatch(treeAndParentMap, generator.getHypothesis());
//			}
			SingleTreeComponent component = new SingleTreeComponent(tree, new TreeHistory(TreeHistoryComponent.onlyFeatureVector(featureVector)), null, null, 
					featureVector , classificationScore, evaluation, null, 0, nextId, missingNodes, classificationScoreForPredictions, cost, originalTextTreeNo);
			++nextId;
			++originalTextTreeNo;
			allTrees.add(component);
		}
	}
	
	private void initSurroundingUtility() throws TreeStringGeneratorException, TreeCoreferenceInformationException, TeEngineMlException, VisualTracingToolException, AnnotatorException
	{
		surroundingUtility=null;
		if (sumUtilitis.isSumDatasetSentenceSelected())
		{
			surroundingUtility = new RTESumSurroundingSentencesUtility(sumUtilitis.getExtendedPreprocessedTopicDataSet(teSystemEnvironment));
		}
	}
	




	// input
	protected Boolean useF1ClassifierFalseForAccuracyNullForXMLModelFile = null;
	protected ExtendedPairData pairData;
	protected GuiRteSumUtilities sumUtilitis;
	protected String taskName = null;
	
	

	// internals
	protected LinearClassifier classifier;
	protected List<ExtendedNode> originalTextTrees;
	protected ExtendedNode hypothesisTree;
	protected OriginalTreesAfterInitialization originalTreesAfterInitialization;
	protected OperationsScript<Info, BasicNode> script;
	protected RTESumSurroundingSentencesUtility surroundingUtility = null;

	protected TreesGeneratorOneIterationSingleTree generator;
	protected Classifier classifierForPredictions;
	private boolean scriptInitialized = false;
	protected boolean initialized = false;
	

	// output
	protected Vector<SingleTreeComponent> allTrees;
	protected List<SingleTreeComponent> lastRequestGeneratedTrees;
	
	private static final Logger logger = Logger.getLogger(SingleComponentUnderlyingSystem.class);
}
