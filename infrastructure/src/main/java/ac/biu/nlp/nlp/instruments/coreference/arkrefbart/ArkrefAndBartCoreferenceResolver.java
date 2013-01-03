package ac.biu.nlp.nlp.instruments.coreference.arkrefbart;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ac.biu.nlp.nlp.instruments.coreference.CoreferenceResolutionException;
import ac.biu.nlp.nlp.instruments.coreference.CoreferenceResolver;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationException;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationUtils;
import ac.biu.nlp.nlp.instruments.coreference.arkref.ArkrefClient;
import ac.biu.nlp.nlp.instruments.coreference.arkref.ArkrefClient.ArkrefClientException;
import ac.biu.nlp.nlp.instruments.coreference.arkref.ArkrefCoreferenceResolver;
import ac.biu.nlp.nlp.instruments.coreference.bart.BartClient;
import ac.biu.nlp.nlp.instruments.coreference.bart.BartCoreferenceResolver;
import ac.biu.nlp.nlp.instruments.coreference.merge.CorefMergeException;
import ac.biu.nlp.nlp.instruments.coreference.merge.WordWithCoreferenceTag;
import ac.biu.nlp.nlp.instruments.coreference.merge.english.EnglishCorefMerger;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

/**
 * A naive resolver, returning union of Arkref's ({@link ArkrefClient}) and BART's ({@link BartClient}) outputs. See their
 * main comments. 
 * 
 * @author Lili Kotlerman
 *
 * @since 1 Aug 2011
 */
public class ArkrefAndBartCoreferenceResolver extends CoreferenceResolver<BasicNode>
{

	private static BartCoreferenceResolver bart;
	private static ArkrefCoreferenceResolver arkref;	

	public ArkrefAndBartCoreferenceResolver() throws ArkrefClientException, IOException
	{
		bart = new BartCoreferenceResolver();
		arkref = new ArkrefCoreferenceResolver();
	}
		
	public ArkrefAndBartCoreferenceResolver(String bartServerName) throws ArkrefClientException, IOException
	{
		bart = new BartCoreferenceResolver(bartServerName);
		arkref = new ArkrefCoreferenceResolver();		
	}
	
	public ArkrefAndBartCoreferenceResolver(String bartServerName, String bartPort) throws ArkrefClientException, IOException
	{
		bart = new BartCoreferenceResolver(bartServerName,bartPort);
		arkref = new ArkrefCoreferenceResolver();
	}	
			
	public ArkrefAndBartCoreferenceResolver(File arkrefWorkDirectory, String bartServerName) throws ArkrefClientException, IOException
	{
		bart = new BartCoreferenceResolver(bartServerName);
		arkref = new ArkrefCoreferenceResolver(arkrefWorkDirectory);		
	}
	
	public ArkrefAndBartCoreferenceResolver(File arkrefWorkDirectory, String bartServerName, String bartPort) throws ArkrefClientException, IOException
	{
		bart = new BartCoreferenceResolver(bartServerName,bartPort);
		arkref = new ArkrefCoreferenceResolver(arkrefWorkDirectory);
	}	
		
	@Override
	public void init() throws CoreferenceResolutionException
	{
		bart.init();
		arkref.init();
	}

	@Override
	public void cleanUp()
	{
		bart.cleanUp();
		arkref.cleanUp();
	}

	

	@Override
	public void implementResolve() throws CoreferenceResolutionException
	{
		try
		{
			arkref.setInput(trees, originalText);
			List<WordWithCoreferenceTag> unitedOutput = arkref.implementResolveWithoutMerge();
			bart.setInput(trees, originalText);
			unitedOutput.addAll(bart.implementResolveWithoutMerge());
			
			EnglishCorefMerger corefMerger = new EnglishCorefMerger(trees, unitedOutput);
			corefMerger.merge();
			coreferenceInformation = corefMerger.getCoreferenceInformation();
			TreeCoreferenceInformationUtils.removeNestedTags(coreferenceInformation);

		}
		catch (CorefMergeException e)
		{
			throw new CoreferenceResolutionException("Merging united Arkref+Bart output with given trees failed. See nested exception.",e);
		}
		catch (TreeCoreferenceInformationException e)
		{
			throw new CoreferenceResolutionException("Could not create coreference Information. See nested exception.",e);
		}
	}

}
