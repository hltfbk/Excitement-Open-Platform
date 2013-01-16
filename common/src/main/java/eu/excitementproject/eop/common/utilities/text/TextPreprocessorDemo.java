package eu.excitementproject.eop.common.utilities.text;


/**
 * Demo for {@link TextPreprocessor}.
 * @author Asher Stern
 *
 */
public class TextPreprocessorDemo
{
	public static void main(String[] args)
	{
		try
		{
			String text = "I am gonna see What\'s going on here! Don't stop me! Nobody can stop me.";
			TextPreprocessor tp = new SimpleTextPreprocessor();
			tp.setText(text);
			tp.preprocess();
			System.out.println(tp.getPreprocessedText());
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}

	}

}
