package eu.excitementproject.eop.biutee.utilities.preprocess;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.text.TextPreprocessor;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;
import eu.excitementproject.eop.transformations.codeannotations.Workaround;

/**
 * A temporary class that handles sentences that make pre-processing
 * utilities getting crazy.
 * The real solution is to repair the pre-processing utilities.
 * However, in the meantime, we can use this workaround to avoid crash
 * in the system, at least for those problematic sentences.
 * 
 * @author Asher Stern
 * @since Feb 26, 2012
 *
 */
@Workaround
public class WorkaroundTextPreprocessor implements TextPreprocessor
{
	/**
	 * This map contains mapping from a sentence to an alternative sentence.
	 * Note that "sentence" might not seem like a sentence. However, technically
	 * it is considered as a "sentence".
	 */
	protected static final Map<String, String> WORKAROUND_SENTENCES = new LinkedHashMap<String, String>();
	static
	{
		WORKAROUND_SENTENCES.put("\" ...","...");
	}
	
	

	public void setText(String text) throws TextPreprocessorException
	{
		this.sentence = text;
	}

	public void preprocess() throws TextPreprocessorException
	{
		if (null==this.sentence) throw new TextPreprocessorException("input not given");
		if (WORKAROUND_SENTENCES.containsKey(this.sentence))
		{
			String wordaround = WORKAROUND_SENTENCES.get(this.sentence);
			
			logger.warn("The given sentence exists in the work-around map.\n" +
					"Sentence is: \""+this.sentence+"\"\n" +
							"It will be converted to \""+wordaround+"\"");
			
			this.sentence = wordaround;
		}
		
		

	}

	public String getPreprocessedText() throws TextPreprocessorException
	{
		return this.sentence;
	}
	

	protected String sentence = null;
	
	private static final Logger logger = Logger.getLogger(WorkaroundTextPreprocessor.class);
}
