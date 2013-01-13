package ac.biu.nlp.nlp.engineml.rteflow.systems;
import java.io.Serializable;
import java.util.LinkedHashSet;

import ac.biu.nlp.nlp.engineml.datastructures.BooleanAndString;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;

/**
 * A base class for {@link FeatureVectorStructureOrganizer}.
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
