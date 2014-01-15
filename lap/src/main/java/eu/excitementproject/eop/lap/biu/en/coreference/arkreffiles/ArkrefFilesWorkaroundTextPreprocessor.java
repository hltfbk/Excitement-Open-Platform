package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.excitementproject.eop.common.utilities.text.TextPreprocessor;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;

/**
 * Modifies the input text for ArkRef, such that it would bypass ArkRef bugs.
 * Since ArkRef tool is somewhat buggy, and crashes on some texts, this workaround
 * processor modifies the text to avoid ArkRef's bugs.
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
		replaceProblematicSequences();
		handleDanglingNonLetterDigitSequence();
	}

	@Override
	public String getPreprocessedText() throws TextPreprocessorException
	{
		return this.sentence;
	}
	
	
	private void replaceProblematicSequences()
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
	
	private void handleDanglingNonLetterDigitSequence()
	{
		String current = this.sentence;
		String removed = removeDanglingNonLetterDigitSequence(current);
		while (!(removed.equals(current)))
		{
			current = removed;
			removed = removeDanglingNonLetterDigitSequence(current);
		}
		this.sentence = removed;
	}
	
	private static String removeDanglingNonLetterDigitSequence(final String givenSentence)
	{
		final String str = " "+givenSentence+" ";
		final Pattern pattern = Pattern.compile("\\s+([^0-9a-zA-Z]+)\\s+");
		Matcher matcher = pattern.matcher(str);
		
		int index=0;
		StringBuilder sb = new StringBuilder();
		while (matcher.find())
		{
			sb.append( str.substring(index, matcher.start()) );
			index = matcher.start(1);
		}
		sb.append(str.substring(index, str.length()));

		return sb.toString().trim();
	}
	
	
	protected String sentence = null;
	
	
}
