package eu.excitementproject.eop.biutee.utilities.preprocess;
import java.util.List;

import eu.excitementproject.eop.common.utilities.text.TextPreprocessor;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;


/**
 * A {@link TextPreprocessor} that runs a list of (given) {@link TextPreprocessor} for
 * the input sentences. The output of the first {@link TextPreprocessor} is the input
 * to the second, the output of the second is the input to the third, etc.
 * 
 * @author Asher Stern
 * @since 4-January-2012
 *
 */
public class ListOfTextPreprocessors implements TextPreprocessor
{
	public ListOfTextPreprocessors(List<? extends TextPreprocessor> processors)
	{
		super();
		this.processors = processors;
	}

	public void setText(String text) throws TextPreprocessorException
	{
		this.originalText = text;
	}
	
	public void preprocess() throws TextPreprocessorException
	{
		if (null== originalText) throw new TextPreprocessorException("Text not set, or already processed.");
		String text = originalText;
		for (TextPreprocessor processor : processors)
		{
			processor.setText(text);
			processor.preprocess();
			text = processor.getPreprocessedText();
		}
		this.newText = text;
		this.originalText = null;
		
	}

	public String getPreprocessedText() throws TextPreprocessorException
	{
		if (null==newText) throw new TextPreprocessorException("Not preprocessed.");
		return this.newText;
	}
	
	private String originalText;
	private String newText;
	
	private List<? extends TextPreprocessor> processors;
}
