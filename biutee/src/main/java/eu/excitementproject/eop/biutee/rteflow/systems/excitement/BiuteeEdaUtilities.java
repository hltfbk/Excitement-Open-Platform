package eu.excitementproject.eop.biutee.rteflow.systems.excitement;

import java.io.File;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.rteflow.systems.excitement.ExcitementToBiuConfigurationFileConverter.ExcitementToBiuConfigurationFileConverterException;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairData;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairResult;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.lap.biu.uima.CasTreeConverterException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jan 23, 2013
 *
 */
public class BiuteeEdaUtilities
{
	/**
	 * Converts an Excitement configuration-file to BIU configuration file.
	 * 
	 * @param excitementConfigurationFile the Excitement configuration file. Should not be touched.
	 * @param biuConfigurationFile the BIU configuration-file. It is an empty, or not-exist, and should be written by this method.
	 * @throws ExcitementToBiuConfigurationFileConverterException 
	 */
	public static void convertExcitementConfigurationFileToBiuConfigurationFile(File excitementConfigurationFile, File biuConfigurationFile) throws ExcitementToBiuConfigurationFileConverterException
	{
		try
		{
			new ExcitementToBiuConfigurationFileConverter(excitementConfigurationFile, biuConfigurationFile).convert();
		}
		catch(RuntimeException e)
		{
			throw new ExcitementToBiuConfigurationFileConverterException("Failed to convert "+excitementConfigurationFile.getPath()+" to "+biuConfigurationFile.getPath(),e);
		}
	}
	
	public static PairData convertJCasToPairData(JCas aCas) throws TeEngineMlException
	{
		try {
			return CasPairDataConverter.convertCasToPairData(aCas);
		}
		catch (CASException | CasTreeConverterException | UnsupportedPosTagStringException | EDAException e) {
			throw new TeEngineMlException("Error while creating a PairData from a JCas.", e);
		}
	}
	
	public static String getPairIdFromJCas(JCas aCas) throws TeEngineMlException
	{
		return CasPairDataConverter.getPairIdFromCas(aCas);
	}
	
	/**
	 * Constructs a {@link TEDecision} from a given {@link PairResult}.
	 *   
	 * @param pairId
	 * @param pairResult
	 * @param classifierForPredictions
	 * @return The {@link TEDecision} constructed by the given {@link PairResult}.
	 * @throws ClassifierException
	 */
	public static TEDecision createDecisionFromPairResult(final String pairId, PairResult pairResult, Classifier classifierForPredictions) throws ClassifierException
	{
		final double classification = classifierForPredictions.classify(pairResult.getBestTree().getFeatureVector());
		final boolean entailment = ClassifierUtils.classifierResultToBoolean(classification);
		final DecisionLabel decisionLabel;
		if (entailment)
		{
			decisionLabel = DecisionLabel.Entailment;
		}
		else
		{
			decisionLabel = DecisionLabel.NonEntailment;
		}
		
		return new TEDecision()
		{
			@Override
			public String getPairID()
			{
				return pairId;
			}
			
			@Override
			public DecisionLabel getDecision()
			{
				return decisionLabel;
			}
			
			@Override
			public double getConfidence()
			{
				return classification;
			}
		};
	}
}
