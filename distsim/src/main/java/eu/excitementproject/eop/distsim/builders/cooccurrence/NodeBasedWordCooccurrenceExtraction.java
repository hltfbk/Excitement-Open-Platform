/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.cooccurrence;

import java.util.LinkedList;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNodeConstructor;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.dependencypath.DependencyPathsFromTreeBinary;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.DefaultCooccurrence;
import eu.excitementproject.eop.distsim.items.DefaultRelation;
import eu.excitementproject.eop.distsim.items.LemmaPosTextUnit;
import eu.excitementproject.eop.distsim.items.Relation;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.util.Filter;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SetBasedPOSFilter;


/**
  * Extraction of co-occurrences, composed of lemma-pos pairs and their dependency relation, from a given parsed sentence, represented by a BasicNode object
  * 
 * @author Meni Adler
 * @since 04/09/2012
 *
 */
public class NodeBasedWordCooccurrenceExtraction extends WordCooccurrenceExtraction<BasicNode> {

	public NodeBasedWordCooccurrenceExtraction() {
		this.extractor =  new DependencyPathsFromTreeBinary<Info, BasicNode>(new BasicNodeConstructor(), true, true);
		this.posFilter = new SetBasedPOSFilter();
	}

	public NodeBasedWordCooccurrenceExtraction(ConfigurationParams confParams) throws ConfigurationException {
		this.extractor =  new DependencyPathsFromTreeBinary<Info, BasicNode>(new BasicNodeConstructor(), true, true);
		this.posFilter = new SetBasedPOSFilter(confParams);
		
		//System.out.println("posFilter = " + posFilter);
	}

	public NodeBasedWordCooccurrenceExtraction(CanonicalPosTag... relevantPOSs) {
		this.extractor =  new DependencyPathsFromTreeBinary<Info, BasicNode>(new BasicNodeConstructor(), true, true);
		this.posFilter = new SetBasedPOSFilter(relevantPOSs);
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.cooccurrence.CooccurrenceExtraction#extractCooccurrences(java.lang.Object)
	 */
	@Override
	public Pair<? extends List<? extends TextUnit>, ? extends List<? extends Cooccurrence<String>>> extractCooccurrences(BasicNode root) throws CooccurrenceExtractionException {
		List<LemmaPosTextUnit> textUnints = new LinkedList<LemmaPosTextUnit>();
		List<DefaultCooccurrence<String>> coOccurrences = new LinkedList<DefaultCooccurrence<String>>();
		try {
			Map<BasicNode, BasicNode> child2parent = AbstractNodeUtils.parentMap(root);	
			for (Entry<BasicNode,BasicNode> entry : child2parent.entrySet()) {
				BasicNode childNode = entry.getKey();
				BasicNode parentNode = entry.getValue();
				if (relevantNode(childNode) && relevantNode(parentNode)) {
					LemmaPosTextUnit child = new LemmaPosTextUnit(childNode.getInfo().getNodeInfo().getWordLemma(),childNode.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag());
					LemmaPosTextUnit parent = new LemmaPosTextUnit(parentNode.getInfo().getNodeInfo().getWordLemma(),parentNode.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag());
					
					Relation<String> rel = new DefaultRelation<String>(childNode.getInfo().getEdgeInfo().getDependencyRelation().getStringRepresentation());
					textUnints.add(parent);
					textUnints.add(child);
					
					//debug
					/*if (childNode.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag() == CanonicalPosTag.V ||
						parentNode.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag() == CanonicalPosTag.V) {					
							System.out.println();
							System.out.println(parent);
							System.out.println(rel);
							System.out.println(child);
							System.out.println();
						}*/
						
					coOccurrences.add(new DefaultCooccurrence<String>(child, parent,rel));
				}
			}
		} catch (Exception e) {			
			throw new CooccurrenceExtractionException(e);
		}
		return new Pair<List<LemmaPosTextUnit>,List<DefaultCooccurrence<String>>>(textUnints,coOccurrences);
	}

	protected boolean relevantNode(BasicNode node) {		
		return node != null && node.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech() != null &&  posFilter.isRelevant(node.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag());
	}

	DependencyPathsFromTreeBinary<Info, BasicNode> extractor;
	Filter<CanonicalPosTag> posFilter;
}
