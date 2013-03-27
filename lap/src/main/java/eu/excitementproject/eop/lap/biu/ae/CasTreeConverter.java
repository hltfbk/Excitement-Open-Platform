package eu.excitementproject.eop.lap.biu.ae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
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
import eu.excitementproject.eop.lap.biu.ae.parser.StanfordDependenciesParserAE;

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
		List<BasicNode> roots = new ArrayList<BasicNode>(sentenceAnnotations.size());
		
		for (Sentence sentenceAnno : lastSentenceList) {
			roots.add(convertSentenceToTree(jcas, sentenceAnno));
		}
		
		lastRootList = roots;
		return roots;
	}
	
	/**
	 * <B>NOTE:</B> Must be called only after {@link #convertCasToTrees(JCas)} was called.
	 * @return a mapping between a root, and the text of the tree's sentence.
	 * @throws CasTreeConverterException
	 */
	public Map<BasicNode, String> getTreesToSentences() throws CasTreeConverterException {
		if (lastRootList == null || lastSentenceList == null) {
			throw new CasTreeConverterException("getTreesToSentences() called before convertCasToTrees().");
		}
		if (lastRootList.size() != lastSentenceList.size()) {
			throw new CasTreeConverterException("Internal error - lastRootList(size=" + lastRootList.size() + ") and lastSentenceList(size=" +
												lastSentenceList.size() + ") are in different sizes.");
		}
		
		Map<BasicNode, String> result = new HashMap<BasicNode, String>(lastRootList.size());
		Iterator<Sentence> iterSentences = lastSentenceList.iterator();
		for (BasicNode node : lastRootList) {
			Sentence sentence = iterSentences.next();
			result.put(node, sentence.getCoveredText());
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param jcas a JCas created by an EOP LAP
	 * @param sentenceAnno one sentence in the CAS
	 * @return a root of a BIU dependency parse tree of the sentence
	 * @throws CasTreeConverterException
	 * @throws UnsupportedPosTagStringException
	 */
	public BasicNode convertSentenceToTree(JCas jcas, Sentence sentenceAnno) throws CasTreeConverterException, UnsupportedPosTagStringException {
		tokenAnnotations = JCasUtil.selectCovered(Token.class, sentenceAnno);
		tokenToNode.clear();
		nodes = new HashSet<BasicNode>();
		childrenByParent = new HashMap<Token, Set<BasicNode>>(tokenAnnotations.size());
		children = new HashSet<Token>(tokenAnnotations.size()-1);
		dependencyAnnotations = JCasUtil.selectCovered(Dependency.class, sentenceAnno);
		deepDependencies = new ArrayList<Dependency>();
		extraNodes = new HashSet<BasicNode>();
		
		logger.trace(String.format("\n***************\n***************\n%s\n***************\n", sentenceAnno.getCoveredText()));
		
		// Get token positions in sentence (1-based)
		Map<Token, Integer> tokenPositions = new HashMap<Token, Integer>(tokenAnnotations.size());
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
			
			BasicNode dependentNode = tokenToNode.get(dependent);
			BasicNode node = buildNode(jcas, dependentNode.getInfo().getNodeInfo().getSerial(), id, dependent, depType, governor);
			extraNodes.add(node);
			node.setAntecedent(dependentNode);

			// Prepare for next node
			id++;

			logger.trace(String.format("--------\n[DEEP]%s--(%s)-->%s\n%s", governor.getCoveredText(), depType, dependent.getCoveredText(), outChildrenMap(childrenByParent)));
		}

		
		// Link all nodes to a tree
		for (Entry<Token, Set<BasicNode>> entry : childrenByParent.entrySet()) {
			BasicNode parent = tokenToNode.get(entry.getKey());
			if (parent != null) {
				addChildren(parent, entry.getValue());
			}
		}
		
		// Verify exactly one root
		Set<Token> tokensThatAreNotchildren = new HashSet<Token>(tokenAnnotations);
		tokensThatAreNotchildren.removeAll(children);
		if (tokensThatAreNotchildren.size() == 0) {
			throw new CasTreeConverterException("All nodes are children - no node to be root!");
		}
		else if (tokensThatAreNotchildren.size() > 1) {
			throw new CasTreeConverterException(String.format(
					"Got %d nodes that are tokens and not children and thus should be root - there should be exactly one: %s",
					tokensThatAreNotchildren.size(), tokensThatAreNotchildren));
		}
		
		// Build root
		Token rootToken = tokensThatAreNotchildren.iterator().next();
		int serialandId = tokenPositions.get(rootToken);
		BasicNode root = buildNode(jcas, serialandId, serialandId, rootToken, null, null);
		addChildren(root, childrenByParent.get(rootToken));
				
		if (nodes.size() != tokenAnnotations.size() + extraNodes.size()) {
			throw new CasTreeConverterException(String.format(
					"Built %d nodes, but there are %d tokens and %d extra nodes in the sentence",
					nodes.size(), tokenAnnotations.size(), extraNodes.size()));
		}
		
		return root;
	}
	
	
	//////////////// PRIVATE ZONE /////////////////////////
	
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
			childrenByParent.put(parent, new HashSet<BasicNode>());
		}
		childrenByParent.get(parent).add(childNode);
	}
	
	private static String getDependency(Dependency depAnno) {
		String dep = depAnno.getClass().getSimpleName().toLowerCase();
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
		if (!tokenToNode.containsKey(token)) {
			tokenToNode.put(token, node);
		}
		return node;
	}
	
	private void addChildren(BasicNode parent, Set<BasicNode> children) {
		List<BasicNode> childrenList= new ArrayList<BasicNode>(children);
		Collections.sort(childrenList, CHILDREN_ORDER);
		
		logger.trace(String.format("\n$%s", outNode(parent)));

		// Add all children to node
		for (BasicNode child : childrenList) {
			parent.addChild(child);
			logger.trace(String.format("$\t%s", outNode(child)));
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
	private final Map<Token, BasicNode> tokenToNode = new HashMap<Token, BasicNode>();
	private Set<BasicNode> nodes;
	private Map<Token, Set<BasicNode>> childrenByParent;
	private Set<Token> children;
	private List<Dependency> dependencyAnnotations;
	private List<Dependency> deepDependencies;
	private Set<BasicNode> extraNodes;
	
	private List<BasicNode> lastRootList = null;
	private List<Sentence> lastSentenceList = null;
	
	private static final Logger logger = Logger.getLogger(CasTreeConverter.class);
}
