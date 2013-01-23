package eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged.localcreative;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.AbstractTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistoryComponent;
import eu.excitementproject.eop.biutee.rteflow.macro.search.WithStatisticsTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged.KStagedAlgorithm;
import eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged.KStagedAlgorithmException;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.datastructures.SingleItemList;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * @author Asher Stern
 * @since Oct 31, 2011
 *
 */
public class KStagedLocalCreativeTextTreesProcessor extends AbstractTextTreesProcessor implements WithStatisticsTextTreesProcessor
{
	public KStagedLocalCreativeTextTreesProcessor(
			String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees, ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			LinearClassifier classifier, Lemmatizer lemmatizer,
			OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment,
			int numberToExpand, // small k
			int numberToRetain, // big K
			int numberOfIterationsAfterFound
			) throws TeEngineMlException
	{
		super(textText, hypothesisText, originalTextTrees, hypothesisTree, originalMapTreesToSentences,
				coreferenceInformation, classifier, lemmatizer, script,
				teSystemEnvironment);
		this.numberToExpand=numberToExpand;
		this.numberToRetain=numberToRetain;
		this.numberOfIterationsAfterFound=numberOfIterationsAfterFound;
	}

	@Override
	public TreeAndFeatureVector getBestTree() throws TeEngineMlException
	{
		if (null==this.bestElement)throw new TeEngineMlException("Not processed");
		return new TreeAndFeatureVector(this.bestElement.getTree(), this.bestElement.getFeatureVector());
	}

	@Override
	public String getBestTreeSentence() throws TeEngineMlException
	{
		if (null==this.bestElement)throw new TeEngineMlException("Not processed");
		return this.bestElement.getOriginalSentence();
	}

	@Override
	public TreeHistory getBestTreeHistory() throws TeEngineMlException
	{
		if (null==this.bestElement)throw new TeEngineMlException("Not processed");
		return this.bestElement.getHistory();
	}
	
	public long getNumberOfExpandedElements() throws TeEngineMlException
	{
		return numberOfExpandedElements;
	}

	public long getNumberOfGeneratedElements() throws TeEngineMlException
	{
		return numberOfGeneratedElements;
	}


	@Override
	protected void processPair() throws ClassifierException,
	TreeAndParentMapException, TeEngineMlException, OperationException,
	ScriptException, RuleBaseException
	{
		try
		{
			this.numberOfExpandedElements=0;
			this.numberOfGeneratedElements=0;
			Set<KStagedLocalCreativeElement> initialStates = buildInitialStates();
			List<KStagedLocalCreativeElement> bestElements = new ArrayList<KStagedLocalCreativeElement>(initialStates.size());
			for (KStagedLocalCreativeElement initialState : initialStates)
			{

				KStagedAlgorithm<KStagedLocalCreativeElement> algorithm =
					new KStagedAlgorithm<KStagedLocalCreativeElement>(
							new SingleItemList<KStagedLocalCreativeElement>(initialState),
							new KStagedLocalCreativeByIterationComparator(),
							new KStagedLocalCreativeByIterationComparator(),
							new KStagedLocalCreativeStateCalculator(script, operationsEnvironment, classifier),
							this.numberToExpand,
							this.numberToRetain,
							false,
							numberOfIterationsAfterFound,
							new CostOnlyComparator(),
							true
					);

				algorithm.find();
				this.numberOfExpandedElements+=algorithm.getNumberOfExpansions();
				this.numberOfGeneratedElements+=algorithm.getNumberOfGenerations();
				bestElements.add(algorithm.getBestGoal());
			}
			this.bestElement = Collections.min(bestElements,new CostOnlyComparator());
		}
		catch (KStagedAlgorithmException e)
		{
			throw new TeEngineMlException("KStaged algorithm failed.",e);
		}



	}
	
	protected Set<KStagedLocalCreativeElement> buildInitialStates() throws TeEngineMlException, ClassifierException, TreeAndParentMapException
	{
		Set<KStagedLocalCreativeElement> ret = new LinkedHashSet<KStagedLocalCreativeElement>();
		for (ExtendedNode textTree : this.originalTextTrees)
		{
			KStagedLocalCreativeElement element = 
				new KStagedLocalCreativeElement(
						textTree,
						new TreeHistory(TreeHistoryComponent.onlyFeatureVector(initialFeatureVector())),
						this.initialFeatureVector(),
						0,
						0,
						null,
						costOf(this.initialFeatureVector()),
						gapOf(textTree),
						null,
						this.originalMapTreesToSentences.get(textTree)
						);
			
			ret.add(element);
		}
		return ret;
	}
	
	private double costOf(Map<Integer,Double> featureVector) throws ClassifierException
	{
		return -this.classifier.getProduct(featureVector);
	}
	
	private double gapOf(ExtendedNode textTree) throws TreeAndParentMapException
	{
		TreeAndParentMap<ExtendedInfo, ExtendedNode> tapm = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(textTree);
		// SingleTreeEvaluations evaluations = SingleTreeEvaluations.create(tapm, this.operationsEnvironment.getHypothesis(), this.operationsEnvironment.getHypothesisLemmasLowerCase(),this.operationsEnvironment.getHypothesisNumberOfNodes());
		SingleTreeEvaluations evaluations = new AlignmentCalculator(operationsEnvironment.getAlignmentCriteria(), tapm, operationsEnvironment.getHypothesis()).getEvaluations(operationsEnvironment.getHypothesisLemmasLowerCase(), operationsEnvironment.getHypothesisNumberOfNodes());
		return (double) evaluations.getMissingLemmas()+evaluations.getMissingNodes()+evaluations.getMissingRelations();
	}
	
	private static class CostOnlyComparator implements Comparator<KStagedLocalCreativeElement>
	{
		public int compare(KStagedLocalCreativeElement o1, KStagedLocalCreativeElement o2)
		{
			return Double.compare(o1.getCost(), o2.getCost());
		}
	}

	private int numberToExpand; // small k
	private int numberToRetain; // big K
	private int numberOfIterationsAfterFound;
	
	private long numberOfExpandedElements=0;
	private long numberOfGeneratedElements=0;
	
	private KStagedLocalCreativeElement bestElement;
}
