package eu.excitementproject.eop.biutee.rteflow.macro.search.old_beam_search;
import java.util.Vector;

import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * Used by {@link BeamSearchTextTreesProcessor}
 * 
 * @author Asher Stern
 * @since Jan 6, 2011
 *
 */
public class EvaluationFunction
{
	public static final double DECAY_ITERATION = 10.0;
	
	public static final int ANCESTOR_TO_OBSERVE_IMPROVEMENT = 3;
	public static final int TOO_OLD = 6;
	
//	public double evaluateTree(double missingNodesPortion, double missingRelationsPortion, double missingLemmasPortion, double classifierResult, int iterationNumber)
//	{
//		//if (iterationNumber>=30)classifierResult=0;
//		double doubleIterNumber = (double)iterationNumber;
//		double decayFactorClassifierResult = 1/(1+((doubleIterNumber*doubleIterNumber)/DECAY_ITERATION));
//		//double decayFactorClassifierResult = (1/(1+(((double)iterationNumber)/DECAY_ITERATION)));
//		
//		//classifierResult = ClassifierUtils.relief(classifierResult, Constants.RELIEF_CLASSIFIER_EXPONENT_IN_SEARCH);
//		
//		return
//		0.05*(1-missingNodesPortion)+
//		0.05*(1-missingRelationsPortion)+
//		0.05*(1-missingLemmasPortion)+
//		0.85*decayFactorClassifierResult*classifierResult
//		;
//
//
//	}
	
	public double evaluateTree(TreeEvaluationsHistory evaluationHistory, double classifierResult, int iterationNumber) throws TeEngineMlException
	{
		double ret = 0;
		boolean improved = false;
		Vector<SingleTreeEvaluations> historyVector = evaluationHistory.getEvaluations();
		SingleTreeEvaluations currentEvaluation = historyVector.get(historyVector.size()-1);
		int numberOfIterationsCurrentSurvivedFor = iterationNumber+1-(historyVector.size()-1);
		if (numberOfIterationsCurrentSurvivedFor<0)throw new TeEngineMlException("BUG: numberOfIterationsCurrentSurvivedFor = "+numberOfIterationsCurrentSurvivedFor+". iterationNumber = "+iterationNumber+". historyVector.size() = "+historyVector.size());
		if (numberOfIterationsCurrentSurvivedFor>TOO_OLD)
			improved=false;
		else
		{
			
			if (historyVector.size()<ANCESTOR_TO_OBSERVE_IMPROVEMENT+1)
			{
				improved = true;
			}
			else
			{
				int ancestorIndex = historyVector.size()-1-ANCESTOR_TO_OBSERVE_IMPROVEMENT;
				SingleTreeEvaluations ancestorEvaluation = historyVector.get(ancestorIndex);
				if (currentEvaluation.getMissingLemmas()<ancestorEvaluation.getMissingLemmas())
					improved=true;
				if (currentEvaluation.getMissingNodes()<ancestorEvaluation.getMissingNodes())
					improved=true;
				if (currentEvaluation.getMissingRelations()<ancestorEvaluation.getMissingRelations())
					improved=true;
			}
		}
		if (improved)
		{
			double missingLemmasPortion = currentEvaluation.getMissingLemmasPortion();
			double missingNodesPortion = currentEvaluation.getMissingNodesPortion();
			double missingRelationsPortion = currentEvaluation.getMissingRelationsPortion();
			double doubleIterNumber = (double)iterationNumber;
			double decayFactorClassifierResult = 1/(1+((doubleIterNumber*doubleIterNumber)/DECAY_ITERATION));
			ret = 
			0.05*(1-missingNodesPortion)+
			0.05*(1-missingRelationsPortion)+
			0.05*(1-missingLemmasPortion)+
			0.85*decayFactorClassifierResult*classifierResult
			;

			//ret = classifierResult;
		}
		else
		{
			ret=0;
		}
		return ret;
	}
}
