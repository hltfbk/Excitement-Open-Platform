package eu.excitementproject.eop.lap.biu.uima;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import eu.excitementproject.eop.common.datastructures.OneToManyBidiMultiHashMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.biu.test.BIUFullLAPConfigured;
import eu.excitementproject.eop.lap.biu.test.BiuTreeBuilder;
import eu.excitementproject.eop.lap.biu.uima.CasTreeConverter;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

public class CasTreeConverterTester {

	public static void testConverter(String text) throws Exception {
		
		try {
			// Run LAP, get CAS
			LAPAccess lap = new BIUFullLAPConfigured(); 
			JCas mainJcas = lap.generateSingleTHPairCAS(text, "");
			JCas jcas = mainJcas.getView(LAP_ImplBase.TEXTVIEW);
			
			// Run Converter, get trees
			CasTreeConverter converter = new CasTreeConverter();
			List<BasicNode> testedTrees = converter.convertCasToTrees(jcas);
			
			// Build reference trees
			BiuTreeBuilder builder = new BiuTreeBuilder();
			List<BasicNode> referenceTrees = builder.buildTrees(text);
			
			assertTreesEqual(referenceTrees, testedTrees);
			
			// Get token-node mapping
			Map<Sentence, OneToManyBidiMultiHashMap<Token, BasicNode>> tokenToNodesBySentence = converter.getTokenToNodesBySentence();
			assertTokenToNodes(jcas, testedTrees, tokenToNodesBySentence);
		}
		catch (Throwable e) {
			ExceptionUtil.logException(e, logger);
			throw e;
		}
	}
	
	/**
	 * Asserts that both lists of trees are equal.
	 * @param referenceTrees a list of roots of reference trees
	 * @param testedTrees a list of roots of tested trees.
	 */
	private static void assertTreesEqual(List<BasicNode> referenceTrees, List<BasicNode> testedTrees) {
		assertEquals(String.format("Got %d reference trees, but %d test trees", referenceTrees.size(), testedTrees.size()),
				referenceTrees.size(), testedTrees.size());
		
		for (Iterator<BasicNode> refIter=referenceTrees.iterator(), testIter=testedTrees.iterator(); refIter.hasNext();) {
			assertNodesEqual(refIter.next(), testIter.next());
		}
	}
	
	/**
	 * Asserts that both lists of subtrees are equal.
	 * <B>NOTE:</B> It is required to have a separate method from {@link assertTreesEqual()}, since in that method
	 * we are not allowed to change the order of trees (they are by sentence-order), but here we do change it
	 * (since sibling-order within a tree doesn't matter, and this way we can consistently compare nodes in both trees). 
	 * @param referenceNodes a list of sibling nodes from reference tree
	 * @param testedNodes a list of sibling nodes from tested tree
	 */
	private static void assertSubtreesEqual(List<BasicNode> referenceNodes, List<BasicNode> testedNodes) {
		if (!(referenceNodes == null && testedNodes == null)) {
			assertEquals(String.format("Got %d reference nodes, but %d test nodes", referenceNodes.size(), testedNodes.size()),
					referenceNodes.size(), testedNodes.size());
			
			// Sort children by ChildrenOrder - their order is not meaningful for equality, but is required
			// in order to iterate over comparable nodes
			CasTreeConverter.ChildrenOrder order = new CasTreeConverter.ChildrenOrder();
			Collections.sort(referenceNodes, order);
			Collections.sort(testedNodes, order);
			
			for (Iterator<BasicNode> refIter=referenceNodes.iterator(), testIter=testedNodes.iterator(); refIter.hasNext();) {
				assertNodesEqual(refIter.next(), testIter.next());
			}
		}
	}
	
	private static void assertNodesEqual(BasicNode refNode, BasicNode testNode) {
		if (!(refNode == null && testNode == null)) {
			assertNodeInfoEqual(refNode.getInfo().getNodeInfo(), testNode.getInfo().getNodeInfo());
			assertEdgeInfoEqual(refNode.getInfo().getEdgeInfo(), testNode.getInfo().getEdgeInfo());
			
			//assertEquals(String.format("Got different antecedents: refNode.getAntecedent()=%s, testNode.getAntecedent()=%s", refNode.getAntecedent(), testNode.getAntecedent()),
			assertNodesEqual(refNode.getAntecedent(), testNode.getAntecedent());
			
			// ID's must be the same only if this is not an extra node
			if (refNode.getAntecedent()==null) {
				assertEquals(String.format("refNode's id is %s, but testNode's id is %s", refNode.getInfo().getId(), testNode.getInfo().getId()),
						refNode.getInfo().getId(), testNode.getInfo().getId());
			}
	
			assertSubtreesEqual(refNode.getChildren(), testNode.getChildren());
		}
	}
	
	private static void assertNodeInfoEqual(NodeInfo refInfo, NodeInfo testInfo) {
		assertEquals(refInfo.getWord(), testInfo.getWord());
		assertEquals(refInfo.getSerial(), testInfo.getSerial());
		assertEquals(refInfo.getWordLemma(), testInfo.getWordLemma());
		assertEquals(refInfo.getSyntacticInfo(), testInfo.getSyntacticInfo());
		assertEquals(refInfo.getNamedEntityAnnotation(), testInfo.getNamedEntityAnnotation());
		assertEquals(refInfo.getVariableId(), testInfo.getVariableId());
	}
	
	private static void assertEdgeInfoEqual(EdgeInfo refInfo, EdgeInfo testInfo) {
		assertEquals(refInfo.getDependencyRelation(), testInfo.getDependencyRelation());

	}
	
	private static void assertTokenToNodes(JCas jcas, List<BasicNode> testedTrees, Map<Sentence, OneToManyBidiMultiHashMap<Token, BasicNode>> tokenToNodesBySentence) {
		Collection<Sentence> sentences = JCasUtil.select(jcas, Sentence.class);
		assertOrderedFeatureStructureCollectionsEqual(sentences, tokenToNodesBySentence.keySet());
		
		assertEquals(testedTrees.size(), tokenToNodesBySentence.size());
		
		Iterator<BasicNode> rootIter = testedTrees.iterator();
		for (Entry<Sentence, OneToManyBidiMultiHashMap<Token, BasicNode>> entry : tokenToNodesBySentence.entrySet()) {
			OneToManyBidiMultiHashMap<Token, BasicNode> tokenToNodes = entry.getValue();
			
			List<Token> tokens = JCasUtil.selectCovered(jcas, Token.class, entry.getKey());
			assertUnorderedFeatureStructureCollectionsEqual(tokens, tokenToNodes.keySet());
			
			BasicNode root = rootIter.next();
			Set<BasicNode> nodes = AbstractNodeUtils.treeToSet(root);
			
			// Remove null-word nodes, since this is the "fake" root node, and we don't need it
			// for the comparison (it is not present in the tokenToNodes map)
			for (Iterator<BasicNode> iterNodes = nodes.iterator(); iterNodes.hasNext();) {
				if (iterNodes.next().getInfo().getNodeInfo().getWord()==null) {
					iterNodes.remove();
				}
			}
			assertEquals(nodes, new HashSet<BasicNode>(tokenToNodes.values()));
			
			for (Entry<Token, Collection<BasicNode>> tokenNodesEntry : tokenToNodes.entrySet()) {
				List<BasicNode> nodeList = (List<BasicNode>) tokenNodesEntry.getValue();
				for (int i=0; i<nodeList.size(); i++) {
					assertEquals(tokenNodesEntry.getKey().getLemma().getValue(), nodeList.get(i).getInfo().getNodeInfo().getWordLemma());
					if (i==0) {
						assertNull(nodeList.get(i).getAntecedent()); // only first element is non-deep node (no antecedent)
					}
					else {
						assertNotNull(nodeList.get(i).getAntecedent()); // all other element are deep (have antecedent)
					}
				}
			}
		}
	}
	
	private static <T extends TOP, S extends TOP> void assertOrderedFeatureStructureCollectionsEqual(Collection<T> c1, Collection<S> c2) {
		List<T> list1 = new ArrayList<T>(c1);
		List<S> list2 = new ArrayList<S>(c2);
		assertEquals("Non-equals ordered feature-structure collections, c1 has " + c1.size() + " elements, c2 has " + c2.size() + " elements", list1, list2);
	}
	
	private static <T extends TOP, S extends TOP> void assertUnorderedFeatureStructureCollectionsEqual(Collection<T> c1, Collection<S> c2) {
		Set<T> list1 = new HashSet<T>(c1);
		Set<S> list2 = new HashSet<S>(c2);
		assertEquals("Non-equals unordered feature-structure collections, c1 has " + c1.size() + " elements, c2 has " + c2.size() + " elements", list1, list2);
	}
	
	private static Logger logger = Logger.getLogger(CasTreeConverterTester.class);
}
