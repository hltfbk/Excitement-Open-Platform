package eu.excitementproject.eop.biutee.utilities.preprocess;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessor;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;

/**
 * Adds a point at the end of sentence, if no ending punctuation exists.
 * 
 * @author Asher Stern
 * @since Apr 16, 2012
 *
 */
public class AddPunctuationTextPreprocessor implements TextPreprocessor
{

	@Override
	public void setText(String text) throws TextPreprocessorException
	{
		this.text = text;
		this.preprocessed = null;
	}

	@Override
	public void preprocess() throws TextPreprocessorException
	{
		if (null==this.text) throw new TextPreprocessorException("null");
		if (this.text.length()==0)
		{
			this.preprocessed=this.text;
		}
		else
		{
			char c = text.charAt(text.length()-1);
			if (Character.isLetterOrDigit(c))
			{
				this.preprocessed = text+".";
			}
			else
			{
				this.preprocessed=this.text;
			}
		}
		this.text = null;
	}

	@Override
	public String getPreprocessedText() throws TextPreprocessorException
	{
		if (null==this.preprocessed) throw new TextPreprocessorException("not processed.");
		return this.preprocessed;
	}
	
	private String text = null;
	private String preprocessed = null;
}
