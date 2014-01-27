package eu.excitementproject.eop.biutee.rteflow.systems;
import java.io.Serializable;
import java.util.LinkedHashSet;

import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.transformations.datastructures.BooleanAndString;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * A base class for {@link FeatureVectorStructureOrganizer}.
 * <P>
 * (Note. The preferred way to create an instance of this interface is via
 * {@link SystemInitialization}: call {@link SystemInitialization#init()} , create an {@link OperationsScript},
 * call {@link SystemInitialization#completeInitializationWithScript(eu.excitementproject.eop.biutee.script.RuleBasesAndPluginsContainer)}
 * with that script, and finally you get it in the field teSystemEnvironment via its method {@link TESystemEnvironment#getFeatureVectorStructureOrganizer()}).
 * 
 * @see FeatureVectorStructureOrganizer
 * 
 * @author Asher Stern
 * @since Dec 19, 2012
 *
 */
public interface FeatureVectorStructure extends Serializable
{
	public ImmutableMap<String,Integer> getRuleBasesFeatures() throws TeEngineMlException;

	public ImmutableMap<String,Integer> getPluginFeatures() throws TeEngineMlException;

	public ImmutableMap<String,Integer> getDynamicGlobalFeatures() throws TeEngineMlException;
	
	public LinkedHashSet<String> getPredefinedFeaturesNames() throws TeEngineMlException;;

	
	
	
	/**
	 * Compares two {@link FeatureVectorStructure}s.
	 * Returns a boolean value indicating whether they are compatible (i.e. identical)
	 * and a string which explains what the incompatibilities are. The latter string
	 * is returned only if <code>withString</code> parameter is <tt>true</tt>.
	 * 
	 * The boolean and the string are combined as a {@link BooleanAndString}.
	 * 
	 * @param other another {@link FeatureVectorStructure}
	 * @param withString - <tt>true</tt> if the caller wants to get a string
	 * that explains what are the incompatibilities, in the case that indeed they
	 * are incompatible
	 * @return
	 * @throws TeEngineMlException
	 */
	public BooleanAndString isCompatibleWithString(FeatureVectorStructure other, boolean withString) throws TeEngineMlException;
	
	


}
