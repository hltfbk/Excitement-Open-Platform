package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.excitementproject.eop.common.utilities.text.TextPreprocessor;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;

/**
 * 
 * @author Asher Stern
 * @since Dec 11, 2013
 *
 */
public class ArkrefFilesWorkaroundTextPreprocessor implements TextPreprocessor 
{
	
	protected static final Map<String, ExpressionAndReplacement> WORKAROUND_SEQUENCES = new LinkedHashMap<String, ExpressionAndReplacement>();
	static
	{
		WORKAROUND_SEQUENCES.put("%", new ExpressionAndReplacement("%"," percent "));
		WORKAROUND_SEQUENCES.put("(", new ExpressionAndReplacement("\\(","["));
		WORKAROUND_SEQUENCES.put(")", new ExpressionAndReplacement("\\)","]"));
		WORKAROUND_SEQUENCES.put("/", new ExpressionAndReplacement("/","-"));
		
	}


	@Override
	public void setText(String text) throws TextPreprocessorException
	{
		this.sentence = text;		
	}

	@Override
	public void preprocess() throws TextPreprocessorException
	{
		
		for (String problematicSequence : WORKAROUND_SEQUENCES.keySet())
		{
			if (this.sentence.contains(problematicSequence))
			{
				String expression = WORKAROUND_SEQUENCES.get(problematicSequence).getExpression();
				String replacement = WORKAROUND_SEQUENCES.get(problematicSequence).getReplacement();
				String newSentence = this.sentence.replaceAll(expression, replacement);
//				logger.warn("A problematic sequence is replaced.\n" +
//						"The problematic sequence is: \""+problematicSequence+"\" and will be replaced with \""+replacement+"\".\n" +
//						"The sentence is:\n"+
//						this.sentence+"\nIt will be changed to:\n"+newSentence);
				
				this.sentence = newSentence;
			}
		}
	}

	@Override
	public String getPreprocessedText() throws TextPreprocessorException
	{
		return this.sentence;
	}
	
	
	protected String sentence = null;
	
	
}
