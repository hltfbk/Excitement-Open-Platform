package ac.biu.nlp.nlp.engineml.plugin;
import java.util.List;

import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;

/**
 * An instance based plug-in is a plug-in which can be initialized for each
 * text-hypothesis pair.
 * <P>
 * By implementing {@link #initForInstanceImplementation(List, ExtendedNode, ImmutableMap, TreeCoreferenceInformation)},
 * the user can initialize the plug-in for the particular text-hypothesis pair.
 * 
 * @author Asher Stern
 * @since January, 2012
 *
 */
@NotThreadSafe
public abstract class InstanceBasedPlugin extends Plugin
{
	protected InstanceBasedPlugin() throws PluginException
	{
		super();
	}

	public final void initForInstance(
			String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees,
			ExtendedNode hypothesisTree,
			ImmutableMap<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation) throws PluginException
	{
		try
		{
			initForInstanceImplementation(textText, hypothesisText, originalTextTrees,
					hypothesisTree,
					originalMapTreesToSentences,
					coreferenceInformation);
		}
		catch(RuntimeException e)
		{
			throw new PluginException("A runtime exception was thrown by plugin",e);
		}
	}

	
	protected abstract void initForInstanceImplementation(
			String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees,
			ExtendedNode hypothesisTree,
			ImmutableMap<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation) throws PluginException;
}
