package eu.excitementproject.eop.lap.biu.uima;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import eu.excitementproject.eop.common.datastructures.OneToManyBidiMultiHashMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.lap.biu.PreprocessUtilities;
import eu.excitementproject.eop.lap.biu.uima.ae.parser.StanfordDependenciesParserAE;

/**
 * Performs various conversions from a JCas created by an EOP LAP, to BIU types.
 * 
 * @author Ofer Bronstein
 * @since March 2013
 */
public class CasTreeConverter {

	//////////////// PUBLIC ZONE /////////////////////////
	
	/**
	 * 
	 * @param jcas a JCas created by an EOP LAP
	 * @return A list of roots, each belonging to a BIU dependency parse tree of a sentence in the CAS. Trees are ordered by sentence order.
	 * @throws CasTreeConverterException
	 * @throws UnsupportedPosTagStringException
	 */
	public List<BasicNode> convertCasToTrees(JCas jcas) throws CasTreeConverterException, UnsupportedPosTagStringException {
		Collection<Sentence> sentenceAnnotations = JCasUtil.select(jcas, Sentence.class);
		lastSentenceList = new ArrayList<Sentence>(sentenceAnnotations);
		lastTokenToNodeBySentence = new LinkedHashMap<Sentence, OneToManyBidiMultiHashMap<Token,BasicNode>>();
		lastRootList = new ArrayList<BasicNode>(sentenceAnnotations.size());
		lastSentenceToRoot = new DualHashBidiMap<>();
		
		for (Sentence sentenceAnno : lastSentenceList) {
			BasicNode root = convertSentenceToTree(jcas, sentenceAnno);
			lastSentenceToRoot.put(sentenceAnno, root);
			lastRootList.add(root);
			
			OneToManyBidiMultiHashMap<Token,BasicNode> tokenToNodeCopy = new OneToManyBidiMultiHashMap<Token,BasicNode>(tokenToNode);
			lastTokenToNodeBySentence.put(sentenceAnno, tokenToNodeCopy);
		}
		
		return lastRootList;
	}
	
	/**
	 * 
	 * @param jcas a JCas created by an EOP LAP
	 * @param sentenceAnno one sentence in the CAS
	 * @return a root of a BIU dependency parse tree of the sentence
	 * @throws CasTreeConverterException
	 * @throws UnsupportedPosTagStringException
	 */
	public BasicNode convertSingleSentenceToTree(JCas jcas, Sentence sentenceAnno) throws CasTreeConverterException, UnsupportedPosTagStringException {
		BasicNode root = convertSentenceToTree(jcas, sentenceAnno);
		
		lastRootList = Arrays.asList(new BasicNode[] {root});
		lastSentenceList = Arrays.asList(new Sentence[] {sentenceAnno});
		
		lastTokenToNodeBySentence = new LinkedHashMap<Sentence, OneToManyBidiMultiHashMap<Token,BasicNode>>();
		OneToManyBidiMultiHashMap<Token,BasicNode> tokenToNodeCopy = new OneToManyBidiMultiHashMap<Token,BasicNode>(tokenToNode);
		lastTokenToNodeBySentence.put(sentenceAnno, tokenToNodeCopy);
		
		return root;
	}
	
	/**
	 * <B>NOTE:</B> Must be called only after one of the conversion methods was called.
	 * @return a mapping between a root, and the text of the tree's sentence. This is an ordered map,
	 * ordered by the order of sentences in the text.
	 * @throws CasTreeConverterException
	 */
	public LinkedHashMap<BasicNode, String> getTreesToSentences() throws CasTreeConverterException {
		if (lastRootList == null || lastSentenceList == null) {
			throw new CasTreeConverterException("getTreesToSentences() called before a conversion method was called.");
		}
		if (lastRootList.size() != lastSentenceList.size()) {
			throw new CasTreeConverterException("Internal error - lastRootList(size=" + lastRootList.size() + ") and lastSentenceList(size=" +
												lastSentenceList.size() + ") are in different sizes.");
		}
		
		LinkedHashMap<BasicNode, String> result = new LinkedHashMap<BasicNode, String>(lastRootList.size());
		Iterator<Sentence> iterSentences = lastSentenceList.iterator();
		for (BasicNode node : lastRootList) {
			Sentence sentence = iterSentences.next();
			result.put(node, sentence.getCoveredText());
		}
		
		return result;
	}
	
	/**
	 * <B>NOTE:</B> Must be called only after one of the conversion methods was called.
	 * @return a mapping between each sentence and an internal map, that maps between any Token annotation
	 * and all its tree nodes. This is an ordered map, ordered by the order of sentences in the text.<BR>
	 * for each token, the first node is always the (single) non-deep node. Any subsequent nodes,
	 * if any, are deep nodes.
	 * @throws CasTreeConverterException
	 */
	public LinkedHashMap<Sentence, OneToManyBidiMultiHashMap<Token,BasicNode>> getTokenToNodesBySentence() throws CasTreeConverterException {
		if (lastTokenToNodeBySentence == null) {
			throw new CasTreeConverterException("getTokenToNodesBySentence() called before a conversion method was called.");
		}
		return lastTokenToNodeBySentence;
	}
	
	/**
	 * <B>NOTE:</B> Must be called only after one of the conversion methods was called.
	 * @return a mapping between any Token annotation and all its tree nodes. If the last conversion
	 * had several sentences, they are all return without any division to sentences. If you wish
	 * to have a division by sentence, use {@link #getTokenToNodesBySentence()}.<BR>
	 * for each token, the first node is always the (single) non-deep node. Any subsequent nodes,
	 * if any, are deep nodes.
	 * @throws CasTreeConverterException
	 */
	public OneToManyBidiMultiHashMap<Token,BasicNode> getAllTokensToNodes() throws CasTreeConverterException {
		if (lastTokenToNodeBySentence == null) {
			throw new CasTreeConverterException("getAllTokensToNodes() called before a conversion method was called.");
		}
		
		OneToManyBidiMultiHashMap<Token,BasicNode> result = new OneToManyBidiMultiHashMap<Token,BasicNode>();
		for (OneToManyBidiMultiHashMap<Token,BasicNode> map : lastTokenToNodeBySentence.values()) {
			OneToManyBidiMultiHashMap<Token,BasicNode> mapCopy = new OneToManyBidiMultiHashMap<Token,BasicNode>(map);
			result.putAll(mapCopy);
		}
		return result;
	}

	public BidiMap<Sentence, BasicNode> getSentenceToRootMap() throws CasTreeConverterException {
		if (lastSentenceToRoot == null) {
			throw new CasTreeConverterException("getSentenceToRootMap() called before a conversion method was called.");
		}
		return lastSentenceToRoot;
	}

	//////////////// PRIVATE ZONE /////////////////////////
	
	private BasicNode convertSentenceToTree(JCas jcas, Sentence sentenceAnno) throws CasTreeConverterException, UnsupportedPosTagStringException {
		tokenAnnotations = JCasUtil.selectCovered(Token.class, sentenceAnno);
		tokenToNode = new OneToManyBidiMultiHashMap<Token, BasicNode>();
		nodes = new LinkedHashSet<BasicNode>();
		childrenByParent = new LinkedHashMap<Token, Set<BasicNode>>(tokenAnnotations.size());
		children = new LinkedHashSet<Token>(tokenAnnotations.size()-1);
		dependencyAnnotations = JCasUtil.selectCovered(Dependency.class, sentenceAnno);
		deepDependencies = new ArrayList<Dependency>();
		extraNodes = new LinkedHashSet<BasicNode>();
		
		logger.trace(String.format("\n***************\n***************\n%s\n***************\n", sentenceAnno.getCoveredText()));
		
		// Get token positions in sentence (1-based)
		Map<Token, Integer> tokenPositions = new LinkedHashMap<Token, Integer>(tokenAnnotations.size());
		int i=1;
		for (Token token : tokenAnnotations) {
			tokenPositions.put(token, i);
			i++;
		}
		
		// Create all nodes from dependents in Dependencies
		for (Dependency depAnno : dependencyAnnotations) {
			String depType = getDependency(depAnno);
			Token governor = depAnno.getGovernor();
			Token dependent = depAnno.getDependent();
			
			// Handle deep dependencies and antecedents
			if (StanfordDependenciesParserAE.getDeepDependencyRelations().contains(depType)) {
				deepDependencies.add(depAnno);
			}
			
			// Make sure the same token is not created twice
			// NOTE that LATER (after deep dependencies) a token actually may have a few nodes. But not here. 
			else if (tokenToNode.containsKey(dependent)) {
				throw new CasTreeConverterException(String.format(
						"Got dependent in more than one non-deep dependency. Dependent:%s, current governor:%s",
						tokenString(dependent), tokenString(governor)));
			}
			
			// Create a node for dependent
			else {
				int serialAndId = tokenPositions.get(dependent);
				buildNode(jcas, serialAndId, serialAndId, dependent, depType, governor);
			}
			
			logger.trace(String.format("--------\n%s--(%s)-->%s\n%s", governor.getCoveredText(), depType, dependent.getCoveredText(), outChildrenMap(childrenByParent)));
		}
		
		// handle deep-dependencies and antecedents
		int id = tokenAnnotations.size()+1;
		for (Dependency depAnno : deepDependencies) {
			String depType = getDependency(depAnno);
			Token governor = depAnno.getGovernor();
			Token dependent = depAnno.getDependent();
			
			Collection<BasicNode> dependentNodes = tokenToNode.get(dependent);
			// Actually, dependentNodes may have more than one node in it - if we already had some
			// deep-dependency iterations, and this dependent Token has a few deep-dependency nodes
//			if (dependentNodes.size()!=1) {
//				throw new CasTreeConverterException("While handling deep dependencies, found a token ( " + dependent + 
//						" ) that is not mapped to exactly one node, it is mapped to " + dependentNodes.size() + " nodes");
//			}			
			BasicNode dependentNode = dependentNodes.iterator().next(); // get first element - that is the non-deep node!
			BasicNode node = buildNode(jcas, dependentNode.getInfo().getNodeInfo().getSerial(), id, dependent, depType, governor);
			extraNodes.add(node);
			node.setAntecedent(dependentNode);

			// Prepare for next node
			id++;

			logger.trace(String.format("--------\n[DEEP]%s--(%s)-->%s\n%s", governor.getCoveredText(), depType, dependent.getCoveredText(), outChildrenMap(childrenByParent)));
		}

		
		// Link all nodes to a tree
		for (Entry<Token, Set<BasicNode>> entry : childrenByParent.entrySet()) {
			Collection<BasicNode> parentNodes = tokenToNode.get(entry.getKey());
			if (parentNodes != null && parentNodes.size()!=0) {
				
				// Take only the first node - this is always the non-deep node
				BasicNode parent = parentNodes.iterator().next();
				
				addChildren(parent, entry.getValue());
			}
		}
		
		// Verify exactly one root
		Set<Token> tokensThatAreNotchildren = new LinkedHashSet<Token>(tokenAnnotations);
		tokensThatAreNotchildren.removeAll(children);
		if (tokensThatAreNotchildren.size() == 0) {
			throw new CasTreeConverterException("All nodes are children - no node to be root!");
		}
		else if (tokensThatAreNotchildren.size() > 1) {
			//TODO hack due to issue: https://github.com/hltfbk/Excitement-Open-Platform/issues/220
			// When it is resolved, remove all content of this if body, and uncomment and leave in only this exception-throwing
//			throw new CasTreeConverterException(String.format(
//					"Got %d nodes that are tokens and not children and thus should be root - there should be exactly one: %s",
//					tokensThatAreNotchildren.size(), tokensThatAreNotchildren));
			logger.error(String.format(
					"Got %d nodes that are tokens and not children and thus should be root - there should be exactly one: %s",
					tokensThatAreNotchildren.size(), tokensThatAreNotchildren));
			Token root = tokensThatAreNotchildren.iterator().next();
			tokensThatAreNotchildren = new LinkedHashSet<Token>();
			tokensThatAreNotchildren.add(root);
			//TODO finish temp solution
		}
		
		// Build root
		Token rootToken = tokensThatAreNotchildren.iterator().next();
		int serialandId = tokenPositions.get(rootToken);
		BasicNode root = buildNode(jcas, serialandId, serialandId, rootToken, null, null);
		addChildren(root, childrenByParent.get(rootToken));
				
		if (nodes.size() != tokenAnnotations.size() + extraNodes.size()) {
			//TODO hack due to issue: https://github.com/hltfbk/Excitement-Open-Platform/issues/220
			// When it is resolved, remove all content of this if body, and uncomment and leave in only this exception-throwing
//			throw new CasTreeConverterException(String.format(
//					"Built %d nodes, but there are %d tokens and %d extra nodes in the sentence",
//					nodes.size(), tokenAnnotations.size(), extraNodes.size()));
			logger.error(String.format(
					"Built %d nodes, but there are %d tokens and %d extra nodes in the sentence",
					nodes.size(), tokenAnnotations.size(), extraNodes.size()));
			//TODO finish temp solution
		}
		
		// Add artificial root on top (this is mandatory in BIU parse trees) 
		BasicNode artificialRoot = PreprocessUtilities.addArtificialRoot(root);
		
		return artificialRoot;
	}
	
	
	private static NodeInfo buildNodeInfo(JCas jcas, Token tokenAnno, int serial) throws CasTreeConverterException, UnsupportedPosTagStringException {
		String word = tokenAnno.getCoveredText();
		String lemma = tokenAnno.getLemma().getValue();
		String pos = tokenAnno.getPos().getPosValue();
		
		// We rely on the fact the NamedEntity enum values have the same names as the ones
		// specified in the DKPro mapping (e.g. PERSON, ORGANIZATION)
		eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity namedEntity=null;
		List<NamedEntity> namedEntities = JCasUtil.selectCovered(NamedEntity.class, tokenAnno);
		switch (namedEntities.size()) {
		case 0: break; // if no NER - ignore and move on
		case 1: namedEntity = eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity.valueOf(namedEntities.get(0).getValue());
				break;
		default: throw new CasTreeConverterException(String.format("Got %d NamedEntity annotations for token %s", namedEntities.size(), tokenAnno));
		}
				
		return new DefaultNodeInfo(word, lemma, serial, namedEntity, new DefaultSyntacticInfo(new PennPartOfSpeech(pos)));
	}
	
	private static EdgeInfo buildEdgeInfo(JCas jcas, String dependencyType) {
		DependencyRelation relation = null;
		if (dependencyType != null) {
			relation = new DependencyRelation(dependencyType, null);
		}
		return new DefaultEdgeInfo(relation);
	}
	
	private void registerChild(Token parent, Token child, BasicNode childNode) {
		
		children.add(child);
		if (!childrenByParent.containsKey(parent)) {
			childrenByParent.put(parent, new LinkedHashSet<BasicNode>());
		}
		childrenByParent.get(parent).add(childNode);
	}
	
	private static String getDependency(Dependency depAnno) {
		String dep = depAnno.getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
		if (dep.equals("aux0")) {
			dep = "aux";
		}
		return dep;
	}

	/**
	 * for tracing.
	 */
	private static String outChildrenMap(Map<Token, Set<BasicNode>> childrenByParent) {
		String result = "childrenByParent(" + childrenByParent.size() + ")\n";
		for (Entry<Token, Set<BasicNode>> entry: childrenByParent.entrySet()) {
			result += String.format("\t%s(%d):[", entry.getKey().getCoveredText(), entry.getValue().size());
			for (BasicNode node: entry.getValue()) {
				result += outNode(node) + ", ";
			}
			result += "]\n";
		}
		result += "\n";
		return result;
	}

	/**
	 * for tracing.
	 */
	private static String outNode(BasicNode node) {
		
		String result = String.format("id=%s:ser=%d:%s:%s",
				node.getInfo().getId(),
				node.getInfo().getNodeInfo().getSerial(),
				node.getInfo().getNodeInfo().getWord(),
				node.getInfo().getEdgeInfo().getDependencyRelation()!=null?node.getInfo().getEdgeInfo().getDependencyRelation().getStringRepresentation():"null");
		if (node.getAntecedent() != null) {
			result += String.format("(^%s)", outNode(node.getAntecedent()));
		}
		return result;
	}
	
	private BasicNode buildNode(JCas jcas, int serial, int id, Token token, String depType, Token parent) throws CasTreeConverterException, UnsupportedPosTagStringException {
		String idString = Integer.toString(id);
		NodeInfo nodeInfo = buildNodeInfo(jcas, token, serial);
		EdgeInfo edgeInfo = buildEdgeInfo(jcas, depType);
		BasicNode node = new BasicNode(new DefaultInfo(idString, nodeInfo, edgeInfo));
		nodes.add(node);
		if (parent != null) {
			registerChild(parent, token, node);
		}
		
		// Note that the first this method is called for each token, we build its non-deep node.
		// So for every token, the first node is always the non-deep one. All subsequent nodes
		// may be deep (if any). Note that the value collection in this MultiMap is a List.
		tokenToNode.put(token, node);
		
		return node;
	}
	
	private void addChildren(BasicNode parent, Set<BasicNode> children) {
		if (children != null) {
			List<BasicNode> childrenList= new ArrayList<BasicNode>(children);
			Collections.sort(childrenList, CHILDREN_ORDER);
			
			logger.trace(String.format("\n$%s", outNode(parent)));
	
			// Add all children to node
			for (BasicNode child : childrenList) {
				parent.addChild(child);
				logger.trace(String.format("$\t%s", outNode(child)));
			}				
		}
	}
	
	private static String tokenString(Token token) {
		return String.format("'%s'[%d:%d]", token.getCoveredText(), token.getBegin(), token.getEnd());
	}

	/**
	 * Defines an order for children of a node.<BR>
	 * First come all the regular nodes, ordered by serial. Then come
	 * the extra nodes (nodes with antecedent) ordered by serial as well.
	 * @author Ofer Bronstein
	 * @since March 2013
	 */
	public static class ChildrenOrder implements Comparator<BasicNode> {

		@Override
		public int compare(BasicNode o1, BasicNode o2) {
			if (o1.getAntecedent() != null && o2.getAntecedent() == null) {
				return 1;
			}
			else if (o1.getAntecedent() == null && o2.getAntecedent() != null) {
				return -1;
			}
			else {
				return o1.getInfo().getNodeInfo().getSerial() - o2.getInfo().getNodeInfo().getSerial();
			}
		}
		
	}

	private final Comparator<BasicNode> CHILDREN_ORDER = new ChildrenOrder();
	private List<Token> tokenAnnotations;
	
	// We need the BasicNodes in the value collections to be ordered, as the first one will always
	// be a non-deep node, and all subsequent nodes will be deep nodes (if any)
	// The default collection used for values collection is ArrayList, which suits our needs.
	// Anyway, the value collection has to be a list!
	private OneToManyBidiMultiHashMap<Token, BasicNode> tokenToNode = null;
	
	private Set<BasicNode> nodes;
	private Map<Token, Set<BasicNode>> childrenByParent;
	private Set<Token> children;
	private List<Dependency> dependencyAnnotations;
	private List<Dependency> deepDependencies;
	private Set<BasicNode> extraNodes;
	
	private List<BasicNode> lastRootList = null;
	private List<Sentence> lastSentenceList = null;
	private LinkedHashMap<Sentence, OneToManyBidiMultiHashMap<Token, BasicNode>> lastTokenToNodeBySentence = null;
	private BidiMap<Sentence, BasicNode> lastSentenceToRoot = null;
	
	private static final Logger logger = Logger.getLogger(CasTreeConverter.class);
}
