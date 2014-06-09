package eu.excitementproject.eop.biutee.operations.updater;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.macro.FeatureUpdate;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * A {@link FeatureVectorUpdater} that adds a value to a custom feature.
 * The class is abstract, and should usually be subclassed when developing
 * {@link eu.excitementproject.eop.biutee.plugin.Plugin Plugin}s.
 * <UL>
 * <LI>You have to implement {@link #getCustomFeatureName()} and return the desire feature's name.</LI>
 * <LI>You may optionally implement {@link #getValueToAdd(Map, FeatureUpdate, TreeAndParentMap, TreeAndParentMap, GenerationOperation, Specification) getValueToAdd()} and return
 * the <code>double</code> amount that should be added to the feature. If not overridden, by default returns <code>-1.0</code>.</LI>
 * </UL>
 * @author Ofer Bronstein
 * @since Sept 02, 2012
 *
 * @param <T> type of specification
 */
public abstract class UpdaterByFeatureName<T extends Specification> extends FeatureVectorUpdater<T> {

	@Override
	public Map<Integer, Double> updateFeatureVector(
			Map<Integer, Double> originalFeatureVector,
			FeatureUpdate featureUpdate,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			GenerationOperation<ExtendedInfo, ExtendedNode> operation,
			T specification) throws TeEngineMlException {
		
		double valueToAdd = getValueToAdd(originalFeatureVector, featureUpdate, textTree, hypothesisTree, operation, specification);
		return featureUpdate.byName(originalFeatureVector, getCustomFeatureName(), valueToAdd);
	}

	/**
	 * Returns the <tt>double</tt> amount that should be added to the desired custom feature in the feature vector.
	 * The method returns <code>-1.0</code> (hard-coded), yet may be overridden in the subclass, for any other
	 * calculation and returned value.
	 * <P>
	 * The method gets various parameters to allow overriders to use all the available information for their
	 * calculations. Of course not all or any of the information must be used - you may also return just a constant value!
	 *  
	 * @param originalFeatureVector
	 * @param featureUpdate
	 * @param textTree
	 * @param hypothesisTree
	 * @param operation
	 * @param specification
	 * @return The amount to be added to the custom feature 
	 * @throws TeEngineMlException
	 * 
	 * @author Ofer Bronstein
	 * @since Sept 02, 2012
	 */
	protected double getValueToAdd(
			Map<Integer, Double> originalFeatureVector,
			FeatureUpdate featureUpdate,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			GenerationOperation<ExtendedInfo, ExtendedNode> operation,
			T specification) throws TeEngineMlException {
		
		return -1.0;
	}
	
	/**
	 * @return the custom feature's name
	 */
	protected abstract String getCustomFeatureName();
}
