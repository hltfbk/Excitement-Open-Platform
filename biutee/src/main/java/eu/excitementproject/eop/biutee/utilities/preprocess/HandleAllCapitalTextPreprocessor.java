package eu.excitementproject.eop.biutee.utilities.preprocess;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessor;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;

/**
 * A {@link TextPreprocessor} which returns the given sentence "as is" - changes
 * nothing, unless the whole sentence is given in capital (upper-case) letters.
 * In the latter case - this {@link TextPreprocessor} changes all the letters
 * to lower-case except the first one (which is the first letter in the sentences).
 * <BR>
 * This is required for parsers that are confused for inputs of all-capital letters.
 *  
 * @author Asher Stern
 * @since 4-January-2012
 *
 */
public class HandleAllCapitalTextPreprocessor implements TextPreprocessor
{
	public void setText(String text) throws TextPreprocessorException
	{
		this.originalText = text;
	}
	public void preprocess() throws TextPreprocessorException
	{
		if (null==this.originalText) throw new TextPreprocessorException("Text not set, or tried to preprocess twice.");
		char[] charArray = originalText.toCharArray();
		boolean foundLetter=false;
		boolean foundLowerCase = false;
		for (int index=0;(!foundLowerCase) && (index<charArray.length);index++)
		{
			char ch = charArray[index];
			if (Character.isLetter(ch))
			{
				foundLetter=true;
				if (Character.isLowerCase(ch))
				{
					foundLowerCase=true;
				}
			}
		}
		if ((foundLetter)&&(!foundLowerCase))
		{
			int index=0;
			if (Character.isLetter(charArray[0]))
				index=1;
			for (;index<charArray.length;++index)
			{
				char ch = charArray[index];
				if (Character.isLetter(ch))
				{
					ch = Character.toLowerCase(ch);
					charArray[index]=ch;
				}
			}
			this.newText = new String(charArray);
		}
		else
		{
			this.newText = this.originalText;
		}
		this.originalText = null;
	}
	
	public String getPreprocessedText() throws TextPreprocessorException
	{
		if (null==this.newText) throw new TextPreprocessorException("Not preprocessed.");
		return this.newText;
	}

	private String originalText = null;
	private String newText = null;
}
