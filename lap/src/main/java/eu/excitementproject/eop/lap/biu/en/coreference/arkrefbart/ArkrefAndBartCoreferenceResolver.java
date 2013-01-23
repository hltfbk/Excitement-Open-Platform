package eu.excitementproject.eop.lap.biu.en.coreference.arkrefbart;

import java.io.File;
import java.io.IOException;
import java.util.List;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolver;
import eu.excitementproject.eop.lap.biu.coreference.TreeCoreferenceInformationUtils;
import eu.excitementproject.eop.lap.biu.coreference.merge.CorefMergeException;
import eu.excitementproject.eop.lap.biu.coreference.merge.WordWithCoreferenceTag;
import eu.excitementproject.eop.lap.biu.en.coreference.arkref.ArkrefClient;
import eu.excitementproject.eop.lap.biu.en.coreference.arkref.ArkrefCoreferenceResolver;
import eu.excitementproject.eop.lap.biu.en.coreference.arkref.ArkrefClient.ArkrefClientException;
import eu.excitementproject.eop.lap.biu.en.coreference.bart.BartClient;
import eu.excitementproject.eop.lap.biu.en.coreference.bart.BartCoreferenceResolver;
import eu.excitementproject.eop.lap.biu.en.coreference.merge.english.EnglishCorefMerger;


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
