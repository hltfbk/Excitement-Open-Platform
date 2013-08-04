package eu.excitementproject.eop.lap.biu.en.coreference.arkref;

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
import eu.excitementproject.eop.lap.biu.en.coreference.arkref.ArkrefClient.ArkrefClientException;
import eu.excitementproject.eop.lap.biu.en.coreference.arkref.ArkrefInputFileManager.ArkrefInputFileManagerException;
import eu.excitementproject.eop.lap.biu.en.coreference.merge.english.EnglishCorefMerger;

/**
 * A coreference resolver, running Arkref ({@link ArkrefClient}) for a given text and further merging this output with parse trees, representing the text.
 * <p>
 * About Arkref, see http://www.ark.cs.cmu.edu/ARKref/
 * <p>
 * <b>NOTE!</b> for arkref to work, you must have <code>JARS\arkref\lib</code> and <code>JARS\arkref\config</code> in the
 * work dir.
 * 
 * @author Lili Kotlerman, Ofer Bronstein
 *
 */
public class ArkrefCoreferenceResolver extends CoreferenceResolver<BasicNode>
{
	////////////////////////////////// PRIVATE /////////////////////////////////////
	ArkrefInputFileManager fileManager;
	
	/////////////////////////////////// PUBLIC //////////////////////////////////////
	
	public ArkrefCoreferenceResolver() throws ArkrefClientException, IOException {
		try {
			fileManager = new ArkrefInputFileManager();
		}
		catch (ArkrefInputFileManagerException e) {
			throw new ArkrefClientException("Problem with input file, see inner exception.", e);
		}
	}
	
	public ArkrefCoreferenceResolver(File workDirectory) throws ArkrefClientException, IOException{
		try {
			fileManager = new ArkrefInputFileManager(workDirectory);
		}
		catch (ArkrefInputFileManagerException e) {
			throw new ArkrefClientException("Problem with input file, see inner exception.", e);
		}
	}
	
	@Override
	public void init() throws CoreferenceResolutionException
	{}

	@Override
	public void cleanUp()
	{
		fileManager.cleanUp();
	}

	

	@Override
	public void implementResolve() throws CoreferenceResolutionException
	{
		try
		{
			List<WordWithCoreferenceTag> arkrefOutput = implementResolveWithoutMerge();
			
			EnglishCorefMerger corefMerger = new EnglishCorefMerger(trees, arkrefOutput);
			corefMerger.merge();
			coreferenceInformation = corefMerger.getCoreferenceInformation();
			TreeCoreferenceInformationUtils.removeNestedTags(coreferenceInformation);

		}
		catch (CorefMergeException e)
		{
			throw new CoreferenceResolutionException("Merging Arkref output with given trees failed. See nested exception.",e);
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
			String filename = fileManager.createTempInputFile().getPath();
			ArkrefClient arkrefClient = null;
			arkrefClient = new ArkrefClient(originalText, filename);
		
			arkrefClient.process();
			return arkrefClient.getArkrefOutput();
			
		}
		catch (ArkrefClientException e)
		{
			throw new CoreferenceResolutionException("Arkref client failure. See nested exception.",e);
		}
		catch (IOException e)
		{
			throw new CoreferenceResolutionException("Arkref client failure. See nested exception.",e);
		}

	}
	

}
