package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolver;

/**
 * Runs ArkRef as a separate process to resolve co-references in text, and
 * integrate them with the parse-trees of the text.
 * 
 * @author Asher Stern
 * @since Dec 10, 2013
 *
 */
public class ArkrefFilesCoreferenceResolver extends CoreferenceResolver<BasicNode>
{
	public static final String TEMP_DIR_PREFIX = "arkref";
	public static final String TEXT_FILE_BASE_NAME = "text" ;
	public static final String TEXT_FILE_NAME = TEXT_FILE_BASE_NAME+".txt";
	public static final String TAGGED_FILE_NAME = TEXT_FILE_BASE_NAME+".tagged";
	

	@Override
	public void init() throws CoreferenceResolutionException
	{
	}

	@Override
	public void cleanUp()
	{
	}

	@Override
	protected void implementResolve() throws CoreferenceResolutionException
	{
		try
		{
			String arkrefOutputString = runArkref();
			this.coreferenceInformation = createWithArkrefOutput(arkrefOutputString);
		}
		catch (IOException | TreeCoreferenceInformationException | CoreferenceResolutionException | TextPreprocessorException | RuntimeException e)
		{
			throw new CoreferenceResolutionException("Failed to resolve co-references using ArkRef. See nested exception.\n"+
					"The text was:\n" +
					this.originalText+"\n",e);
		}

	}
	
	private String runArkref() throws CoreferenceResolutionException, IOException, TextPreprocessorException
	{
		File tempDir = ArkreffilesUtils.createTempDirectory(TEMP_DIR_PREFIX);
		try
		{
			File textFile = new File(tempDir,TEXT_FILE_NAME);
			ArkreffilesUtils.writeTextToFile(normalizeText(), textFile);
			ArkreffilesUtils.runArkref(textFile);
			File taggedFile = new File(tempDir,TAGGED_FILE_NAME);
			String arkrefOutputString = ArkreffilesUtils.readTextFile(taggedFile.getPath());
			return arkrefOutputString;
		}
		finally
		{
			try
			{
				for (File fileInTempDir : tempDir.listFiles())
				{
					fileInTempDir.delete();
				}
				tempDir.delete();
			}
			catch(RuntimeException re){}
		}
		
	}
	
	private String normalizeText() throws TextPreprocessorException
	{
		ArkrefFilesWorkaroundTextPreprocessor processor = new ArkrefFilesWorkaroundTextPreprocessor();
		processor.setText(this.originalText);
		processor.preprocess();
		return processor.getPreprocessedText();
	}
	
	private TreeCoreferenceInformation<BasicNode> createWithArkrefOutput(final String arkrefOutputString) throws CoreferenceResolutionException, TreeCoreferenceInformationException
	{
		ArkrefOutputReader<Info,BasicNode> reader = new ArkrefOutputReader<Info,BasicNode>(arkrefOutputString);
		reader.read();
		ArrayList<ArkrefOutputWord<Info,BasicNode>> arkrefOutput = reader.getArkrefOutput();
		
		ArkrefOutputAlignToTrees<Info,BasicNode> aligner = new ArkrefOutputAlignToTrees<Info,BasicNode>(this.trees,arkrefOutput);
		aligner.align();
		
		ArkrefMergeWithTrees<Info,BasicNode> merger = new ArkrefMergeWithTrees<Info,BasicNode>(this.trees,arkrefOutput);
		merger.merge();
		BidirectionalMap<BasicNode, ArkrefMention> map = merger.getMergedOutput();
		
		Set<String> entities = new LinkedHashSet<>();
		for (ArkrefMention entity : map.rightSet())
		{
			String entityId = entity.getEntityId();
			if (!entities.contains(entityId))
			{
				entities.add(entityId);
			}
		}
		
		ValueSetMap<String, BasicNode> mapEntityToNode = new SimpleValueSetMap<>();
		for (BasicNode node : map.leftSet())
		{
			String entityId = map.leftGet(node).getEntityId();
			mapEntityToNode.put(entityId, node);
		}
		
		TreeCoreferenceInformation<BasicNode> corefInfo = new TreeCoreferenceInformation<>();
		for (String entityId : mapEntityToNode.keySet())
		{
			if (mapEntityToNode.get(entityId).size()>1)
			{
				Integer groupId = corefInfo.createNewGroup();
				for (BasicNode node : mapEntityToNode.get(entityId))
				{
					corefInfo.addNodeToGroup(groupId, node);
				}
			}
		}
		
		return corefInfo;
		
	}

}
