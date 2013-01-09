package ac.biu.nlp.nlp.engineml.plugin;

import java.util.List;

import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;

import ac.biu.nlp.nlp.engineml.operations.finders.Finder;
import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.specifications.Specification;
import ac.biu.nlp.nlp.engineml.operations.updater.FeatureVectorUpdater;
import ac.biu.nlp.nlp.engineml.rteflow.micro.perform.PerformFactory;

/**
 * Plug-in is a list of {@link PerformFactory}, which is a data-structure of:
 * <UL>
 * <LI>{@link Finder}</LI>
 * <LI>{@link Specification}</LI>
 * <LI>{@link GenerationOperation}</LI>
 * <LI>{@link FeatureVectorUpdater}</LI>
 * </UL>
 * <P>
 * A plug-in specifies a user-custom operation that can be performed like all
 * other operations.
 * A plug-in might be a rule base, but it can be also a new on-the-fly operation.
 * <P>
 * If the operation is defined for a particular text-hypothesis pair (for example,
 * a numerical entailment that is based on the particular text and hypothesis), then
 * {@link InstanceBasedPlugin} should be used.
 * 
 * 
 * @author Asher Stern
 * @since Jan 27, 2012
 *
 */
@NotThreadSafe
public abstract class Plugin
{
	protected Plugin() throws PluginException {}
	
	
	public final List<PerformFactory<? extends Specification>> getPerformFactories() throws PluginException
	{
		try
		{
			return getPerformFactoriesImplementation();
		}
		catch(RuntimeException e)
		{
			throw new PluginException("A runtime-exception was thrown by plugin",e);
		}
	}
	
	public abstract List<PerformFactory<? extends Specification>> getPerformFactoriesImplementation() throws PluginException;
}
