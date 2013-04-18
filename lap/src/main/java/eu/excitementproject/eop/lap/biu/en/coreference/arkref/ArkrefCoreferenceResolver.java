package eu.excitementproject.eop.lap.biu.en.coreference.arkref;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.UUID;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolver;
import eu.excitementproject.eop.lap.biu.coreference.TreeCoreferenceInformationUtils;
import eu.excitementproject.eop.lap.biu.coreference.merge.CorefMergeException;
import eu.excitementproject.eop.lap.biu.coreference.merge.WordWithCoreferenceTag;
import eu.excitementproject.eop.lap.biu.en.coreference.arkref.ArkrefClient.ArkrefClientException;
import eu.excitementproject.eop.lap.biu.en.coreference.merge.english.EnglishCorefMerger;

/**
 * A coreference resolver, running Arkref ({@link ArkrefClient}) for a given text and further merging this output with parse trees, representing the text.
 * <p>
 * About Arkref, see http://www.ark.cs.cmu.edu/ARKref/
 * <p>
 * <b>NOTE!</b> for arkref to work, you must have <code>JARS\arkref\lib</code> and <code>JARS\arkref\config</code> in the
 * work dir.
 * 
 * @author Lili Kotlerman
 *
 */
public class ArkrefCoreferenceResolver extends CoreferenceResolver<BasicNode>
{
	////////////////////////////////// PRIVATE /////////////////////////////////////
	
	// constants
	private static final String TXT = ".txt";
	private static final String ARKREF_TEMP = "arkrefTemp";
	private static final String ARKREF_TEMP_FILE_PREFIX = "arkref_temp_file_";
	private static final String PROCESS_ID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	
	// member fields
	 
	protected File workDirectory;
	
	
	/////////////////////////////////// PUBLIC //////////////////////////////////////
	
	public ArkrefCoreferenceResolver() throws ArkrefClientException, IOException{
		this(new File(System.getProperty("java.io.tmpdir") + File.separator + ARKREF_TEMP + PROCESS_ID + "__" + UUID.randomUUID().toString()));   
	}
	
	public ArkrefCoreferenceResolver(File workDirectory) throws ArkrefClientException, IOException{
		this.workDirectory = workDirectory;
		if(workDirectory.exists())
			if (!FileUtils.deleteDirectory(workDirectory)) {
				throw new ArkrefClientException("Could not delete directory " + workDirectory);
			}
		if (workDirectory.mkdir()==false) 
			throw new ArkrefClientException("Could not make new directory " + workDirectory); 
		
		
		// filename = workDirectory+File.separator+String.valueOf(RANDOM_GENERATOR.nextInt(1000000))+TXT;
	}
	
	@Override
	public void init() throws CoreferenceResolutionException
	{}

	@Override
	public void cleanUp()
	{
		FileUtils.deleteDirectory(workDirectory);
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
			String filename = File.createTempFile(ARKREF_TEMP_FILE_PREFIX, TXT, workDirectory).getPath();
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
