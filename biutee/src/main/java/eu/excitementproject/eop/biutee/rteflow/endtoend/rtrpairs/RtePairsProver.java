package eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.DefaultProver;
import eu.excitementproject.eop.biutee.rteflow.macro.GlobalPairInformation;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative.LocalCreativeTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.ExtendedPairData;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairDataCollapseToSingleTree;
import eu.excitementproject.eop.biutee.script.HypothesisInformation;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.Provider;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Implementation of {@link Prover} for T-H pairs of RTE:1-5.
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 */
public class RtePairsProver extends DefaultProver<THPairInstance, THPairProof>
{
	public RtePairsProver(TESystemEnvironment teSystemEnvironment, Provider<Lemmatizer> lemmatizerProvider)
	{
		super(lemmatizerProvider);
		this.teSystemEnvironment = teSystemEnvironment;
	}
	
	@Override
	public THPairProof prove(THPairInstance instance,
			OperationsScript<Info, BasicNode> script,
			LinearClassifier classifierForSearch) throws BiuteeException
			{
		try
		{
			// Make preparations.
			if (logger.isInfoEnabled()){logger.info("Processing pair #"+instance.getPairData().getPair().getId());}
			HypothesisInformation hypothesisInformation = instance.getHypothesisInformation();
			if (!hypothesisInformation.equals(script.getHypothesisInformation()))
			{
				script.setHypothesisInformation(hypothesisInformation);
			}
			
			// Get the pair. 
			ExtendedPairData pairData = instance.getPairData();
			if (Constants.COLLAPSE_MULTIPLE_TREES_TO_SINGLE_TREE)
			{
				pairData = new PairDataCollapseToSingleTree(pairData).collapse();
			}
			
			// Process the pair (This is "macro" stage, Search algorithm).
			TextTreesProcessor processor = createProcessor(pairData,script,classifierForSearch);
			processor.process();
			THPairProof proof = new THPairProof(processor.getBestTree(),processor.getBestTreeSentence(),processor.getBestTreeHistory(), processor.getGapDescription());
			return proof;
		}
		catch (TeEngineMlException | OperationException | ClassifierException | AnnotatorException | ScriptException | RuleBaseException | TreeAndParentMapException e)
		{
			throw new BiuteeException("Prover failed. Please see nested exception.",e);
		}
	}
	
	
	protected TextTreesProcessor createProcessor(ExtendedPairData pairData,
			OperationsScript<Info, BasicNode> script,
			LinearClassifier classifierForSearch) throws BiuteeException, TeEngineMlException
	{
		LocalCreativeTextTreesProcessor processor = new LocalCreativeTextTreesProcessor(
				pairData.getPair().getText(), pairData.getPair().getHypothesis(),
				pairData.getTextTrees(), pairData.getHypothesisTree(),
				pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),
				classifierForSearch, getLemmatizer(), script, teSystemEnvironment
				);
		processor.setGlobalPairInformation(createGlobalPairInformation(pairData));
		return processor;
	}
	
	protected GlobalPairInformation createGlobalPairInformation(ExtendedPairData pairData) throws BiuteeException
	{
		String datasetName = pairData.getDatasetName();
		GlobalPairInformation globalPairInformation = null;
		if  (null==datasetName)
		{
			globalPairInformation = new GlobalPairInformation(pairData.getPair().getAdditionalInfo(),null);
		}
		else
		{
			globalPairInformation = new GlobalPairInformation(pairData.getPair().getAdditionalInfo(),datasetName);
		}
		return globalPairInformation;
	}


	private final TESystemEnvironment teSystemEnvironment;
	
	private static final Logger logger = Logger.getLogger(RtePairsProver.class);
}
