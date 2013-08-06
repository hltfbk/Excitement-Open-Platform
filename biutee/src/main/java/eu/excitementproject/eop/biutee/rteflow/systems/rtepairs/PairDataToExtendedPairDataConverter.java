package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.document_sublayer.DocumentInitializer;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Converts a {@link PairData} into an {@link ExtendedPairData} - mainly
 * it means converting each tree in the {@linkplain PairData}, which is
 * represented as {@link BasicNode}, into an {@link ExtendedNode}. In addition,
 * it has to put the coreference IDs into the nodes themselves.
 * 
 * @author Asher Stern
 * @since Apr 7, 2011
 *
 */
public class PairDataToExtendedPairDataConverter
{
	public PairDataToExtendedPairDataConverter(PairData pairData, TESystemEnvironment teSystemEnvironment) throws TeEngineMlException
	{
		if (null==pairData) throw new TeEngineMlException("null pairData in constructor");
		this.pairData = pairData;
		this.teSystemEnvironment = teSystemEnvironment;
	}

	public void convert() throws TreeCoreferenceInformationException, TeEngineMlException, AnnotatorException
	{
		logger.debug("Convert text...");
		DocumentInitializer documentInitializerForText =
				new DocumentInitializer(pairData.getCoreferenceInformation(),teSystemEnvironment,pairData.getTextTrees());
		documentInitializerForText.initialize();
		List<ExtendedNode> textTrees = documentInitializerForText.getDocumentAsTreesList();
		TreeCoreferenceInformation<ExtendedNode> coreferenceInformation = documentInitializerForText.getCreatedCoreferenceInformation();
		BidirectionalMap<BasicNode, ExtendedNode> textMapping = documentInitializerForText.getMapOriginalToGenerated();
		Map<ExtendedNode, String> convertedMapTreesToSentences = new LinkedHashMap<ExtendedNode, String>();
		for (BasicNode originalTree : pairData.getTextTrees())
		{
			convertedMapTreesToSentences.put(
					textMapping.leftGet(originalTree),
					pairData.getMapTreesToSentences().get(originalTree)
					);
		}
		logger.debug("Convert text - done.");

		logger.debug("Convert hypothesis...");
		DocumentInitializer documentInitializerForHypothesis =
				new DocumentInitializer(null,teSystemEnvironment,pairData.getHypothesisTree());
		documentInitializerForHypothesis.initialize();
		ExtendedNode convertedHypothesisTree = documentInitializerForHypothesis.getDocumentAsTree();
		logger.debug("Convert hypothesis - done.");
		
		extendedPairData = new ExtendedPairData(pairData.getPair(),textTrees,convertedHypothesisTree,convertedMapTreesToSentences,coreferenceInformation,pairData.getDatasetName());
	}
	


	
	public ExtendedPairData getExtendedPairData() throws TeEngineMlException
	{
		if (null==extendedPairData)
			throw new TeEngineMlException("convert() method was not called.");
		return extendedPairData;
	}




	private PairData pairData;
	private TESystemEnvironment teSystemEnvironment;
	private ExtendedPairData extendedPairData = null;
	
	private static final Logger logger = Logger.getLogger(PairDataToExtendedPairDataConverter.class);
}
