package eu.excitementproject.eop.lap.biu.ae;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.biu.uima.ae.CasTreeConverter;
import eu.excitementproject.eop.lap.lappoc.LAP_ImplBase;

public class CasTreeConverterTester {

	public static void testConverter(String text) throws Exception {
		
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
}
