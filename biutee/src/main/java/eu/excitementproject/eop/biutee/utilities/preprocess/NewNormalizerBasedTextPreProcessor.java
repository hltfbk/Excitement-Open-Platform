package eu.excitementproject.eop.biutee.utilities.preprocess;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.PREPROCESS_NEW_NORMALIZER_FILE;

import java.io.File;

import ac.biu.nlp.normalization.BiuNormalizer;
import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessor;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;


/**
 * 
 * This class wraps a number-normalizer created by Shachar Mirkin.
 * The normalized is a ".jar" file: biu-normalizer_v0.6.1.jar, in
 * $JARS/BiuNormalizer
 * 
 * This class wraps the normalizer as a {@link TextPreprocessor}.
 *  
 * 
 * @author Asher Stern
 * @since 2011
 *
 */
@LanguageDependent("English")
public class NewNormalizerBasedTextPreProcessor implements TextPreprocessor
{
	public NewNormalizerBasedTextPreProcessor(ConfigurationParams params) throws ConfigurationException, TextPreprocessorException
	{
		File normalizerFile = params.getFile(PREPROCESS_NEW_NORMALIZER_FILE);
		try
		{
			normalizer = new BiuNormalizer(normalizerFile);
		}
		catch(Exception e)
		{
			throw new TextPreprocessorException("Failed to initialize new BiuNormalizer.",e);
		}
	}

	public void setText(String text) throws TextPreprocessorException
	{
		this.text = text;
		preprocessedText = null;
	}

	public void preprocess() throws TextPreprocessorException
	{
		try
		{
			if (text==null)throw new TextPreprocessorException("text not set.");
			preprocessedText = normalizer.normalize(text);
			text = null;
		}
		catch (Exception e)
		{
			throw new TextPreprocessorException("BiuNormalizer failed.",e);
		}
	}

	public String getPreprocessedText() throws TextPreprocessorException
	{
		if (preprocessedText==null)throw new TextPreprocessorException("text not preprocessed.");
		return preprocessedText;
	}

	
	protected BiuNormalizer normalizer;
	protected String text = null;;
	protected String preprocessedText = null;
}
