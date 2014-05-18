package eu.excitementproject.eop.common.utilities.text;


/**
 * Used to convert Unicode string into an ASCII string.
 * <BR>
 * What is the meaning of "conversion"? this is defined
 * by subclasses of this class.
 * @author Asher Stern
 *
 */
public abstract class UnicodeToAsciiConverter
{
	////////////////// PUBLIC PART //////////////////////////
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public void convert() throws UnicodeToAsciiConverterException
	{
		if (null==this.text)
			throw new UnicodeToAsciiConverterException("The text is null. Did you forget calling setText()?");
		doConvert();
		this.converted = true;
	}
	
	
	public String getConvertedText() throws UnicodeToAsciiConverterException
	{
		if (!converted)
			throw new UnicodeToAsciiConverterException("The text was not yet converted. Please call convert() before calling this method.");
		return text;
	}
	

	/////////////////// PROTECTED PART ///////////////////////////
	// protected methods
	protected abstract void doConvert() throws UnicodeToAsciiConverterException;
	
	// protected fields
	protected boolean converted = false;
	protected String text = null;

}
