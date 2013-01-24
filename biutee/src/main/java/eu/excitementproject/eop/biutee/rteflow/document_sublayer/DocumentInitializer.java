package eu.excitementproject.eop.biutee.rteflow.document_sublayer;

import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.Workarounds.ANNOTATOR_FAILURE_IS_BLOCKING;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.codeannotations.Workaround;
import eu.excitementproject.eop.transformations.datastructures.DsUtils;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatedTreeAndMap;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.generic.truthteller.SynchronizedAtomicAnnotator;
import eu.excitementproject.eop.transformations.representation.AdditionalInformationServices;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since 10 August 2012
 *
 */
@NotThreadSafe
public class DocumentInitializer
{
	public DocumentInitializer(
			TreeCoreferenceInformation<BasicNode> originalCoreferenceInformation,
			TESystemEnvironment teSystemEnvironment, BasicNode givenTree)
	{
		super();
		this.originalCoreferenceInformation = originalCoreferenceInformation;
		this.teSystemEnvironment = teSystemEnvironment;
		this.givenTree = givenTree;
	}

	public DocumentInitializer(
			TreeCoreferenceInformation<BasicNode> originalCoreferenceInformation,
			TESystemEnvironment teSystemEnvironment, 
			List<BasicNode> givenTreesAsList)
	{
		super();
		this.originalCoreferenceInformation = originalCoreferenceInformation;
		this.teSystemEnvironment = teSystemEnvironment;
		this.givenTreesAsList = givenTreesAsList;
	}

	public DocumentInitializer(
			TreeCoreferenceInformation<BasicNode> originalCoreferenceInformation,
			TESystemEnvironment teSystemEnvironment,
			Map<Integer, BasicNode> givenTreesAsMap)
	{
		super();
		this.originalCoreferenceInformation = originalCoreferenceInformation;
		this.teSystemEnvironment = teSystemEnvironment;
		this.givenTreesAsMap = givenTreesAsMap;
	}


	public void initialize() throws TeEngineMlException, AnnotatorException, TreeCoreferenceInformationException
	{
		logStartInitialize();
		
		mapOriginalToGenerated = new SimpleBidirectionalMap<BasicNode, ExtendedNode>();
		BasicNode tree = getNextTree();
		while (tree!=null)
		{
			createTree(tree);
			tree = getNextTree();
		}
		
		if (originalCoreferenceInformation!=null)
		{
			createCorefInformation();
		}
		
		logger.debug("DocumentInitializer: Initialization done.");
	}
	

	
	
	public TreeCoreferenceInformation<ExtendedNode> getCreatedCoreferenceInformation()
	{
		return createdCoreferenceInformation;
	}

	public ExtendedNode getDocumentAsTree() throws TeEngineMlException
	{
		if (null==givenTree) throw new TeEngineMlException("Cannot get output of other structure than input");
		return generatedTree;
	}

	public List<ExtendedNode> getDocumentAsTreesList() throws TeEngineMlException
	{
		if (null==givenTreesAsList) throw new TeEngineMlException("Cannot get output of other structure than input");
		return generatedTreesAsList;
	}

	public Map<Integer, ExtendedNode> getDocumentAsTreesMap() throws TeEngineMlException
	{
		if (null==givenTreesAsMap) throw new TeEngineMlException("Cannot get output of other structure than input");
		return generatedTreesAsMap;
	}
	
	public BidirectionalMap<BasicNode, ExtendedNode> getMapOriginalToGenerated()
	{
		return mapOriginalToGenerated;
	}

	
	
	
	
	private final void logStartInitialize()
	{
		if (logger.isDebugEnabled())
		{
			if (givenTree!=null)
			{
				logger.debug("DocumentInitializer (single tree): Initializing...");
			}
			else if (givenTreesAsList!=null)
			{
				logger.debug("DocumentInitializer (list of trees): Initializing...");
			}
			else if (givenTreesAsMap!=null)
			{
				logger.debug("DocumentInitializer (map of trees): Initializing...");
			}
			else
			{
				logger.error("DocumentInitializer - manner of given document was not detected - BUG!!!. The running continues...");
				logger.debug("DocumentInitializer (BUG - NOT DETECTED): Initializing...");
			}
		}
	}
	
	private void createCorefInformation() throws TreeCoreferenceInformationException, TeEngineMlException
	{
		createdCoreferenceInformation = new TreeCoreferenceInformation<ExtendedNode>();
		int max = 0;
		for (Integer id : originalCoreferenceInformation.getAllExistingGroupIds())
		{
			if (max<id.intValue())max=id.intValue();
		}
		Integer id  = 0;
		while (id.intValue()<max)
		{
			id = createdCoreferenceInformation.createNewGroup();
		}
		
		for (Integer groupId : originalCoreferenceInformation.getAllExistingGroupIds())
		{
			for (BasicNode originalNode : originalCoreferenceInformation.getGroup(groupId))
			{
				if (!mapOriginalToGenerated.leftContains(originalNode)) throw new TeEngineMlException("BUG: bad mapping");
				ExtendedNode generatedNode = mapOriginalToGenerated.leftGet(originalNode);
				if (null==generatedNode) throw new TeEngineMlException("BUG: bad mapping, null node");
				createdCoreferenceInformation.addNodeToGroup(groupId, generatedNode);
			}
		}
	}
	
	
	private void createTree(BasicNode originalTree) throws TeEngineMlException, AnnotatorException
	{
		TreeCopier<Info, BasicNode, ExtendedInfo, ExtendedNode> treeCopier = 
				new TreeCopier<Info, BasicNode, ExtendedInfo, ExtendedNode>(originalTree, new InitializerInfoConverter(), new ExtendedNodeConstructor());
		
		treeCopier.copy();
		ExtendedNode createdTree = treeCopier.getGeneratedTree();
		BidirectionalMap<BasicNode, ExtendedNode> mapping = treeCopier.getNodesMap();
		
		TreeAndMapping treeAndMapping = null;
		
//		if (Constants.TRACE_ORIGINAL_NODES)
//		{
//			treeAndMapping = initTraces(createdTree,mapping);
//			createdTree = treeAndMapping.getTree();
//			mapping = treeAndMapping.getMapping();
//		}

		treeAndMapping = annotateTextAndHypothesis(createdTree,mapping);
		createdTree = treeAndMapping.getTree();
		mapping = treeAndMapping.getMapping();

		
//		if (AdvancedEqualities.USE_ADVANCED_EQUALITIES)
//		{
//			treeAndMapping = refineTree(createdTree,mapping);
//			createdTree = treeAndMapping.getTree();
//			mapping = treeAndMapping.getMapping();
//		}
		
		DsUtils.BidiMapAddAll(mapOriginalToGenerated, mapping);
		putNextTree(createdTree);
	}
	
	
	
//	private TreeAndMapping initTraces(ExtendedNode tree, BidirectionalMap<BasicNode, ExtendedNode> mapping) throws TeEngineMlException
//	{
//		SelfTraceSetter setter = new SelfTraceSetter(tree);
//		setter.set();
//
//		return new TreeAndMapping(
//				setter.getNewTree(),
//				DsUtils.concatenateBidiMaps(mapping, setter.getMapping())
//				);
//	}
	
	
	
	
	@Workaround
	protected TreeAndMapping annotateTextAndHypothesis(ExtendedNode tree, BidirectionalMap<BasicNode, ExtendedNode> mapping) throws AnnotatorException, TeEngineMlException
	{
		SynchronizedAtomicAnnotator annotator = this.teSystemEnvironment.getTreeAnnotator();
		boolean annotatorSucceeded = true;
		AnnotatedTreeAndMap annotated = null;
		try
		{
			annotated = annotator.annotate(tree);
		}
		catch(AnnotatorException e)
		{
			annotatorSucceeded=false;
			if (ANNOTATOR_FAILURE_IS_BLOCKING)
			{
				throw e;
			}
			else
			{
				logger.error("Sentence Annotator FAILED!!! However, since ANNOTATOR_FAILURE_IS_BLOCKING is set to false, the processing continues. Exception is: ",e);
			}
		}
		if (annotatorSucceeded)
		{
			return new TreeAndMapping(
					annotated.getAnnotatedTree(),
					DsUtils.concatenateBidiMaps(mapping, annotated.getMapOriginalToAnnotated())
					);
		}
		else
		{
			return new TreeAndMapping(tree, mapping);
		}
	}
	
	
//	/**
//	 * Sets a value to {@link AdditionalNodeInformation}'s <code>contentAncestor</code> field.
//	 * 
//	 * @throws TeEngineMlException
//	 */
//	protected TreeAndMapping refineTree(ExtendedNode tree, BidirectionalMap<BasicNode, ExtendedNode> mapping) throws TeEngineMlException
//	{
//		ContentAncestorSetter setter = new ContentAncestorSetter(tree);
//		setter.generate();
//		return new TreeAndMapping(
//				setter.getGeneratedTree(),
//				DsUtils.concatenateBidiMaps(mapping,setter.getNodesMap())
//				);
//	}
	
	
	
	private BasicNode getNextTree() throws TeEngineMlException
	{
		BasicNode ret = null;
		if (givenTree!=null)
		{
			if (!givenTreeReturned)
			{
				ret = givenTree;
				givenTreeReturned = true;
			}
			else
			{
				ret = null;
			}
		}
		else if (givenTreesAsList!=null)
		{
			if (null==givenTreesAsListIterator) givenTreesAsListIterator = givenTreesAsList.iterator();
			if (givenTreesAsListIterator.hasNext())
			{
				ret = givenTreesAsListIterator.next();
			}
			else
			{
				ret = null;
			}
		}
		else if (givenTreesAsMap!=null)
		{
			if (null==givenTreesAsMapKeyIterator) givenTreesAsMapKeyIterator = givenTreesAsMap.keySet().iterator();
			if (givenTreesAsMapKeyIterator.hasNext())
			{
				currentKey = givenTreesAsMapKeyIterator.next();
				ret = givenTreesAsMap.get(currentKey);
			}
			else
			{
				ret = null;
			}
		}
		else throw new TeEngineMlException("Parse trees were not given.");
		lastGetTree = ret;
		return ret;
	}
	
	private void putNextTree(ExtendedNode tree) throws TeEngineMlException
	{
		if (null==lastGetTree) throw new TeEngineMlException("BUG: try to put a tree than was not given");
		lastGetTree=null;
		if (givenTree!=null)
		{
			generatedTree = tree;
		}
		else if (givenTreesAsList!=null)
		{
			if (null==generatedTreesAsList) generatedTreesAsList = new ArrayList<ExtendedNode>(givenTreesAsList.size());
			generatedTreesAsList.add(tree);
		}
		else if (givenTreesAsMap!=null)
		{
			if (null==generatedTreesAsMap) generatedTreesAsMap = new LinkedHashMap<Integer, ExtendedNode>();
			if (null==currentKey) throw new TeEngineMlException("BUG");
			generatedTreesAsMap.put(currentKey,tree);
		}
		else throw new TeEngineMlException("Parse trees were not given.");
	}
	
	
	
	
	private class InitializerInfoConverter implements TreeCopier.InfoConverter<BasicNode, ExtendedInfo>
	{
		@Override
		public ExtendedInfo convert(BasicNode os)
		{
			AdditionalNodeInformation additionalInfo = null;
			if (null==originalCoreferenceInformation)
			{
				additionalInfo = AdditionalInformationServices.emptyInformation();
			}
			else
			{
				Integer corefGroup = originalCoreferenceInformation.getIdOf(os);
				additionalInfo = AdditionalInformationServices.generateFromCorefGroup(corefGroup,uniqueIdForCoref);
				uniqueIdForCoref++;
			}
			return new ExtendedInfo(os.getInfo(), additionalInfo);
		}
	}

	// Input
	private TreeCoreferenceInformation<BasicNode> originalCoreferenceInformation = null;
	private TESystemEnvironment teSystemEnvironment;
	private BasicNode givenTree = null;
	private List<BasicNode> givenTreesAsList = null;
	private Map<Integer,BasicNode> givenTreesAsMap = null;
	
	// Internals
	private int uniqueIdForCoref = 1;
	private boolean givenTreeReturned = false;
	private Iterator<BasicNode> givenTreesAsListIterator = null;
	private Iterator<Integer> givenTreesAsMapKeyIterator = null;
	private Integer currentKey = null;
	private BasicNode lastGetTree = null;
	
	
	
	// Output
	private BidirectionalMap<BasicNode, ExtendedNode> mapOriginalToGenerated;
	private TreeCoreferenceInformation<ExtendedNode> createdCoreferenceInformation = null;
	private ExtendedNode generatedTree;
	private List<ExtendedNode> generatedTreesAsList;
	private Map<Integer,ExtendedNode> generatedTreesAsMap;

	
	private static final Logger logger = Logger.getLogger(DocumentInitializer.class);
}
