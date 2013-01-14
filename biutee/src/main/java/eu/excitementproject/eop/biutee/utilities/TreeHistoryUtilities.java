package eu.excitementproject.eop.biutee.utilities;
import java.util.Map;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierFactory;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.TrainableClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptFactory;
import eu.excitementproject.eop.biutee.utilities.safemodel.SafeSamples;
import eu.excitementproject.eop.biutee.utilities.safemodel.SafeSamplesUtils;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * Converts {@link TreeHistory} into a string.
 * 
 * @author Asher Stern
 * @since Jul 24, 2011
 *
 */
public class TreeHistoryUtilities
{
	public static String historyToString(TreeHistory treeHistory)
	{
		StringBuffer sb = new StringBuffer();
		ImmutableList<Specification> specs = treeHistory.getSpecifications();
		for (Specification spec : specs)
		{
			sb.append(spec.toString());
			sb.append('\n');
		}
		return sb.toString();
	}
	
	/**
	 * Prints the history, and for each operation - the cost of the sequence until now.
	 * I.e. After each operation it prints a cost. This cost is the cost of the sequence
	 * of all operations from the beginning until the current operation.
	 * 
	 * A typical usage (for a stand alone application that is given a {@link TreeHistory}) is:<BR>
	 * Use {@link ClassifierFactory#getDefaultClassifierForSearch()}.<BR>
	 * Use {@link ScriptFactory#getDefaultScript()}<BR>
	 * {@link OperationsScript#init()}<BR>
	 * {@link OperationsScript#getRuleBasesNames()}<BR>
	 * With the names: {@link SafeSamplesUtils#load(java.io.File, java.util.LinkedHashSet)}.<BR>
	 * Now you have classifier and samples, so call {@link TrainableClassifier#train(java.util.Vector)}
	 * (use {@link SafeSamples#getSamples()} for that).
	 * Now, you have a trained classifier, so you can call this method.
	 * 
	 * 
	 * @param treeHistory
	 * @param classifier
	 * @return
	 * @throws TeEngineMlException
	 * @throws ClassifierException
	 */
	public static String historyToString(TreeHistory treeHistory, LinearTrainableStorableClassifier classifier) throws TeEngineMlException, ClassifierException
	{
		StringBuffer sb = new StringBuffer();
		ImmutableList<Specification> specs = treeHistory.getSpecifications();
		ImmutableList<Map<Integer, Double>> featureVectors = treeHistory.getFeaturesVectors();
		boolean withFeatureVectors = false;
		if (featureVectors.size()>0)
		{
			withFeatureVectors=true;
			if (featureVectors.size()!=specs.size())throw new TeEngineMlException("Malformed TreeHistory.");
		}
		ImmutableIterator<Map<Integer, Double>> featureVectorIterator = featureVectors.iterator();
		for (Specification spec : specs)
		{
			sb.append(spec.toString());
			sb.append('\n');
			if (withFeatureVectors)
			{
				double cost = -classifier.getProduct(featureVectorIterator.next());
				sb.append(String.format("cost = %-4.4f\n", cost));
			}
		}
		return sb.toString();
	}

}
