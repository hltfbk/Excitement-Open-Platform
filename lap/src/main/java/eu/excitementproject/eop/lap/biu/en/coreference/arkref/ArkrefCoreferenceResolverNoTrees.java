package eu.excitementproject.eop.lap.biu.en.coreference.arkref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.common.representation.coreference.DockedMention;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolverNoTrees;
import eu.excitementproject.eop.lap.biu.en.coreference.arkref.ArkrefClient.ArkrefClientException;
import eu.excitementproject.eop.lap.biu.en.coreference.arkref.ArkrefInputFileManager.ArkrefInputFileManagerException;

/**
 * A coreference resolver, running Arkref ({@link ArkrefClient}) for a given text.
 * <p>
 * About Arkref, see http://www.ark.cs.cmu.edu/ARKref/
 * <p>
 * <b>NOTE!</b> for arkref to work, you must have <code>JARS\arkref\lib</code> and <code>JARS\arkref\config</code> in the
 * work dir.
 * 
 * @author Ofer Bronstein
 * @since August 2013
 * @see ArkrefCoreferenceResolver
 *
 */
public class ArkrefCoreferenceResolverNoTrees extends CoreferenceResolverNoTrees {

	ArkrefInputFileManager fileManager;
	
	public ArkrefCoreferenceResolverNoTrees() throws ArkrefClientException, IOException {
		try {
			fileManager = new ArkrefInputFileManager();
		}
		catch (ArkrefInputFileManagerException e) {
			throw new ArkrefClientException("Problem with input file, see inner exception.", e);
		}
	}
	
	@Override
	public void init() throws CoreferenceResolutionException {
		// nothing to do
	}

	@Override
	public void cleanUp() {
		fileManager.cleanUp();
	}

	@Override
	protected void implementResolve() throws CoreferenceResolutionException {
		try
		{
			String filename = fileManager.createTempInputFile().getPath();
			ArkrefClient arkrefClient = new ArkrefClient(originalText, filename);
			arkrefClient.process();
			Map<String,List<DockedMention>> dockedMentionsMap = arkrefClient.getArkrefDockedOutput();
			dockedMentions = buildDockedMentionList(dockedMentionsMap);
			
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

	private List<List<DockedMention>> buildDockedMentionList(Map<String, List<DockedMention>> dockedMentionsMap) {
		return new ArrayList<List<DockedMention>>(dockedMentionsMap.values());
	}

}
