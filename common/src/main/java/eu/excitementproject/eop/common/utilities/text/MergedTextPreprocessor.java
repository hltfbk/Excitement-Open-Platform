package eu.excitementproject.eop.common.utilities.text;

import java.util.List;

public class MergedTextPreprocessor implements TextPreprocessor
{
	public MergedTextPreprocessor(List<TextPreprocessor> textPreprocessors) throws TextPreprocessorException
	{
		if (null==textPreprocessors) throw new TextPreprocessorException("null==textPreprocessors");
		this.textPreprocessors = textPreprocessors;
	}
	

	public void setText(String text) throws TextPreprocessorException
	{
		if (null==text) throw new TextPreprocessorException("The given text was null.");
		this.text = text;
		this.preprocessedText = null;
	}

	public void preprocess() throws TextPreprocessorException
	{
		preprocessedText = this.text;
		for (TextPreprocessor preprocessor : this.textPreprocessors)
		{
			preprocessor.setText(preprocessedText);
			preprocessor.preprocess();
			preprocessedText = preprocessor.getPreprocessedText();
		}
	}

	public String getPreprocessedText() throws TextPreprocessorException
	{
		if (null==this.preprocessedText) throw new TextPreprocessorException("preprocess() was not called.");
		return this.preprocessedText;
	}
	

	protected List<TextPreprocessor> textPreprocessors;
	protected String text;
	protected String preprocessedText = null;
}
