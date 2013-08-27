package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.macro.InitializationTextTreesProcessor;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

/**
 * Given a parse tree, derived from the text parse tree, the method {@link #updateForGap(TreeAndParentMap, Map)}
 * updates its feature-vector to resemble the gap between it and the hypothesis parse tree.
 * <P>
 * An instance of this interface has information about the hypothesis in its member fields.
 * For each hypothesis (and actually for each pair) a new instance of
 * {@link GapFeaturesUpdate} implementation should be constructed. This is done be constructing
 * a new instance of {@link GapToolInstances} in the <code>init()</code> method of
 * {@link InitializationTextTreesProcessor}.
 * 
 * @see GapToolInstances
 * 
 * @author Asher Stern
 * @since Aug 1, 2013
 *
 * @param <I>
 * @param <S>
 */
@NotThreadSafe
public interface GapFeaturesUpdate<I, S extends AbstractNode<I, S>>
{
	/**
	 * Assigns feature-values to gap-features in the given feature-vector.
	 * The gap-features are based on the gap between the given tree (the first
	 * parameter) and the hypothesis (assumed to be known as member field(s)).
	 * 
	 * @param tree a parse-tree that might be the original text tree or an
	 * intermediate tree derived from the text parse tree as part of the proof
	 * construction.
	 * @param featureVector the feature-vector which represents the proof steps
	 * that have been performed so far.
	 * @param environment additional objects which might be required to calculate
	 * the gap.
	 * @return The feature vector, which is identical to the given feature vector
	 * but with new values for the gap features.
	 * @throws GapException
	 */
	public Map<Integer, Double> updateForGap(TreeAndParentMap<I, S> tree, Map<Integer,Double> featureVector, GapEnvironment<I, S> environment) throws GapException;
	
	/**
	 * Assigns feature-values to gap-features in the given feature-vector, including
	 * gap estimations for final proof.
	 * This method is similar to {@link #updateForFinalGap(TreeAndParentMap, Map, GapEnvironment)},
	 * but it also updates additional gap-features which should not be used in the
	 * intermediate steps of proof constructions, but only for the complete proof.
	 * <BR>
	 * Such features are, for example (and, I guess, the only example) Truth-Value
	 * matching, under some configuration.
	 * 
	 * @param tree
	 * @param featureVector
	 * @param environment
	 * @return
	 * @throws GapException
	 */
	public Map<Integer, Double> updateForFinalGap(TreeAndParentMap<I, S> tree, Map<Integer,Double> featureVector, GapEnvironment<I, S> environment) throws GapException;
}
