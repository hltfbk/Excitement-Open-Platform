package ac.biu.nlp.nlp.general.text;

public interface TextPreprocessor
{
	public void setText(String text) throws TextPreprocessorException;
	
	public void preprocess() throws TextPreprocessorException;
	
	public String getPreprocessedText() throws TextPreprocessorException;

}
