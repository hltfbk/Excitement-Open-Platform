package ac.biu.nlp.nlp.instruments.coreference.bart;

import java.util.List;

import ac.biu.nlp.nlp.instruments.coreference.CoreferenceResolutionException;
import ac.biu.nlp.nlp.instruments.coreference.CoreferenceResolver;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationException;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationUtils;
import ac.biu.nlp.nlp.instruments.coreference.bart.BartClient.BartClientException;
import ac.biu.nlp.nlp.instruments.coreference.merge.CorefMergeException;
import ac.biu.nlp.nlp.instruments.coreference.merge.WordWithCoreferenceTag;
import ac.biu.nlp.nlp.instruments.coreference.merge.english.EnglishCorefMerger;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

/**
 * A CoreferenceResolver based on Bart.
 * 
 * <b>NOTE!</b> you must run the BART engine before running this code 
 * 
 * @author Asher Stern
 *
 * @since 1 Jan 2012
 * @see http://bart-anaphora.org/
 */
public class BartCoreferenceResolver extends CoreferenceResolver<BasicNode>
{
	public BartCoreferenceResolver(){}
	
	public BartCoreferenceResolver(String serverName)
	{
		this.serverName = serverName;
	}
	
	public BartCoreferenceResolver(String serverName, String port)
	{
		this.serverName = serverName;
		this.port = port;
	}
	
	
	
	
	
	@Override
	public void init() throws CoreferenceResolutionException
	{}

	@Override
	public void cleanUp()
	{}

	

	@Override
	public void implementResolve() throws CoreferenceResolutionException
	{
		try
		{
			BartClient bartClient = null;
			if (null==serverName)
			{
				bartClient = new BartClient(originalText);
			}
			else if (null==this.port)
			{
				bartClient = new BartClient(originalText, serverName);
			}
			else
			{
				bartClient = new BartClient(originalText, serverName, this.port, BartClient.DEFAULT_ULR_PATH);
			}
			
			bartClient.process();
			List<WordWithCoreferenceTag> bartOutput = bartClient.getBartOutput();
			
			EnglishCorefMerger corefMerger = new EnglishCorefMerger(trees, bartOutput);
			corefMerger.merge();
			coreferenceInformation = corefMerger.getCoreferenceInformation();
			TreeCoreferenceInformationUtils.removeNestedTags(coreferenceInformation);


		}
		catch (BartClientException e)
		{
			throw new CoreferenceResolutionException("Bart client failure. See nested exception.",e);
		}
		catch (CorefMergeException e)
		{
			throw new CoreferenceResolutionException("Mergin Bart output with given trees failed. See nested exception.",e);
		}
		catch (TreeCoreferenceInformationException e)
		{
			throw new CoreferenceResolutionException("Could not create coreference Information. See nested exception.",e);
		}
	}

	// 29.08.11 Lili
	public List<WordWithCoreferenceTag> implementResolveWithoutMerge() throws CoreferenceResolutionException
	{
		try
		{
			BartClient bartClient = null;
			if (null==serverName)
			{
				bartClient = new BartClient(originalText);
			}
			else if (null==this.port)
			{
				bartClient = new BartClient(originalText, serverName);
			}
			else
			{
				bartClient = new BartClient(originalText, serverName, this.port, BartClient.DEFAULT_ULR_PATH);
			}
			
			bartClient.process();
			return bartClient.getBartOutput();

		}
		catch (BartClientException e)
		{
			throw new CoreferenceResolutionException("Bart client failure. See nested exception.",e);
		}
	}	

	protected String serverName = null;
	protected String port = null;

}
