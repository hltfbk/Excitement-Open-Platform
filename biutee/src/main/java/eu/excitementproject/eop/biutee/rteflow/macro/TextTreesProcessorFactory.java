package eu.excitementproject.eop.biutee.rteflow.macro;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.search.WithStatisticsTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative.LocalCreativeTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.utilities.HiddenConfigurationProvider;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.GlobalMessages;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * A factory of {@link TextTreesProcessor} which creates and returns {@link LocalCreativeTextTreesProcessor}.
 * <P>
 * (Some experiments in BIU might change the behavior of this class. However, for all other users - this class just
 * returns {@link LocalCreativeTextTreesProcessor}).
 *  
 * @author Asher Stern
 * @since Jan 8, 2014
 *
 */
public class TextTreesProcessorFactory
{
	public static final String HIDDEN_PARAMETER_REFLECTION_PROCESSOR = "processor-class";
	
	/**
	 * A method that returns {@link LocalCreativeTextTreesProcessor}.
	 * @return {@link LocalCreativeTextTreesProcessor}
	 */
	public static WithStatisticsTextTreesProcessor createProcessor(
			String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees,
			ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			LinearClassifier classifier,
			Lemmatizer lemmatizer, OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment
			) throws TeEngineMlException
	{
		
		// Ignore the following line. ret will be null.
		WithStatisticsTextTreesProcessor ret = tryLoadFromHiddenParameters(textText,hypothesisText,originalTextTrees,hypothesisTree,originalMapTreesToSentences,coreferenceInformation,classifier,lemmatizer,script,teSystemEnvironment);
		
		// ret must be and will be null! The returned object will be LocalCreativeTextTreesProcessor, as you can see below.
		if (null==ret)
		{
			// This is what really returned.
			ret = new LocalCreativeTextTreesProcessor(textText,hypothesisText,
					originalTextTrees,hypothesisTree,originalMapTreesToSentences,
					coreferenceInformation,classifier,lemmatizer, script,
					teSystemEnvironment);

		}
		return ret;
	}
	
	
	
	/**
	 * A method that returns null and throws no exception. Ignore it.
	 * <P>
	 * (Some experiments in BIU might change the behavior of this method. However, for all other users
	 * this method will return null).
	 */
	private static WithStatisticsTextTreesProcessor tryLoadFromHiddenParameters(
			String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees,
			ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			LinearClassifier classifier,
			Lemmatizer lemmatizer, OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment
			) throws TeEngineMlException
	{
		try
		{
			ConfigurationParams hiddenParams = HiddenConfigurationProvider.getHiddenParams();
			if (hiddenParams!=null) // Should be null
			{
				GlobalMessages.globalWarn("Loading TextTreesProcessor from hidden parameters.", logger);

				String processorClassName = hiddenParams.get(HIDDEN_PARAMETER_REFLECTION_PROCESSOR);
				logger.info("TextTreesProcessor class is "+processorClassName);
				Class<?> clsProcessor = Class.forName(processorClassName);
				Constructor<?> constructor = clsProcessor.getConstructor(String.class,String.class,List.class,ExtendedNode.class,Map.class,TreeCoreferenceInformation.class,LinearClassifier.class,Lemmatizer.class,OperationsScript.class,TESystemEnvironment.class);
				return (WithStatisticsTextTreesProcessor) constructor.newInstance(textText,hypothesisText,
					originalTextTrees,hypothesisTree,originalMapTreesToSentences,
					coreferenceInformation,classifier,lemmatizer, script,
					teSystemEnvironment);

			}
			else
			{
				return null;
			}
		}
		catch (ConfigurationException | RuntimeException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
		{
			throw new TeEngineMlException("A failure when trying to load from hidden parameters. Please see nested exception.",e);
		}
	}
	
	private static final Logger logger = Logger.getLogger(TextTreesProcessorFactory.class);
}
