package eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.DefaultProver;
import eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative.LocalCreativeTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
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
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jul 21, 2013
 *
 */
public class RteSumProver extends DefaultProver<RteSumInstance, RteSumProof>
{
	public RteSumProver(TESystemEnvironment teSystemEnvironment, Provider<Lemmatizer> lemmatizerProvider)
	{
		super(lemmatizerProvider);
		this.teSystemEnvironment = teSystemEnvironment;
	}

	@Override
	public RteSumProof prove(RteSumInstance instance,
			OperationsScript<Info, BasicNode> script,
			LinearClassifier classifierForSearch) throws BiuteeException
	{
		try
		{
			if (logger.isInfoEnabled()){logger.info("Processing "+instance.getCandidateIdentifier().toString());}
			HypothesisInformation hypothesisInformation = instance.getHypothesisInformation();
			if (!hypothesisInformation.equals(script.getHypothesisInformation()))
			{
				script.setHypothesisInformation(hypothesisInformation);
			}

			Map<ExtendedNode, String> mapTreesToSentences = new LinkedHashMap<>();
			mapTreesToSentences.put(instance.getTextTree(), instance.getTextSentence());
			LocalCreativeTextTreesProcessor processor = new LocalCreativeTextTreesProcessor(
					instance.getTextSentence(), instance.getHypothesisSentence(),
					Collections.singletonList(instance.getTextTree()),
					instance.getHypothesisTree(), mapTreesToSentences,
					instance.getCoreferenceInformation(),
					classifierForSearch, getLemmatizer(), script, teSystemEnvironment);

			processor.setSurroundingsContext(instance.getSurroundingTextTrees());
			
			processor.process();
			RteSumProof proof = new RteSumProof(processor.getBestTree(),processor.getBestTreeSentence(),processor.getBestTreeHistory(), processor.getGapDescription());
			return proof;
		}
		catch (TeEngineMlException | OperationException | ClassifierException | AnnotatorException | ScriptException | RuleBaseException | TreeAndParentMapException e)
		{
			throw new BiuteeException("Failed to prove due to some technical problem. See nested exception.",e);
		}
	}

	private final TESystemEnvironment teSystemEnvironment;
	
	private static final Logger logger = Logger.getLogger(RteSumProver.class);
}
