package eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.GlobalMessages;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * @author Asher Stern
 * @since Oct 22, 2013
 *
 */
public class ExperimentalParametersLocalCreativeTextTreesProcessor extends LocalCreativeTextTreesProcessor
{
	public static final boolean NEXT_ITERATION_G_PLUS_H = false;
	public static final boolean NEXT_ITERATION_H_ONLY = false;

	/**
	 * Superclass constructor
	 */
	public ExperimentalParametersLocalCreativeTextTreesProcessor(
			String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees, ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			LinearClassifier classifier, Lemmatizer lemmatizer,
			OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment) throws TeEngineMlException
	{
		super(textText, hypothesisText, originalTextTrees, hypothesisTree,
				originalMapTreesToSentences, coreferenceInformation,
				classifier, lemmatizer, script, teSystemEnvironment);
		
		if (!hybridGapMode) throw new TeEngineMlException("This class is designed only for hybrid mode experiments.");
		verifyConstants();
		if (!warningLogged)
		{
			GlobalMessages.globalWarn("Using experimental parameters in search algorithm.", logger);
			warningLogged=true;
		}
	}
	
	@SuppressWarnings("unused")
	private void verifyConstants() throws TeEngineMlException
	{
		if (NEXT_ITERATION_G_PLUS_H&&NEXT_ITERATION_H_ONLY) throw new TeEngineMlException("Bug in constants of "+this.getClass().getSimpleName()); 
	}
	
	
	@Override
	protected LocalCreativeTreeElement findBest(Set<LocalCreativeTreeElement> elements, double originalCost, double originalGap) throws TeEngineMlException
	{
		if (elements.size()==0)
		{
			if (hybridGapMode) {return null;}
			else {throw new TeEngineMlException("An error occurred LLGS search. A \"global iteration\" ended with no new generated tree, in pure-transformation mode.");}
		}
		
		if (is(!NEXT_ITERATION_G_PLUS_H)&&is(!NEXT_ITERATION_H_ONLY))
		{
			return super.findBest(elements, originalCost, originalGap);
		}
		else
		{
			double stopValue;
			if (NEXT_ITERATION_G_PLUS_H)
			{
				stopValue=originalCost+originalGap;
			}
			else if (NEXT_ITERATION_H_ONLY)
			{
				stopValue=originalGap;
			}
			else
			{
				throw new TeEngineMlException("Bug");
			}
			
			LocalCreativeTreeElement bestElement = null;
			Double bestValue = null;
			for (LocalCreativeTreeElement element : elements)
			{
				double value;
				if (NEXT_ITERATION_G_PLUS_H)
				{
					value = element.getCost()+element.getGap();
				}
				else if (NEXT_ITERATION_H_ONLY)
				{
					value = element.getGap();
				}
				else
				{
					throw new TeEngineMlException("Bug");
				}
				
				if (null==bestElement)
				{
					bestElement=element;
					bestValue=value;
				}
				else
				{
					if (value<bestValue)
					{
						bestElement=element;
						bestValue=value;
					}
				}
			} // end of for
			
			if (bestValue!=null)
			{
				if (stopValue<=bestValue) // what we already have is not worse than what we return.
				{
					return null;
				}
			}
			return bestElement;
		}
	}
	
	
	/**
	 * Returns the input. Used to prevent compilation warnings when
	 * constants are involved.
	 * @param b
	 * @return
	 */
	private final boolean is(final boolean b)
	{
		return b;
	}
	
	

	private static boolean warningLogged=false;
	private static final Logger logger = Logger.getLogger(ExperimentalParametersLocalCreativeTextTreesProcessor.class);
}
