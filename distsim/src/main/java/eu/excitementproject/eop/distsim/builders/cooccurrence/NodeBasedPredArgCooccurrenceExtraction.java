/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.cooccurrence;

import java.util.LinkedList;

import java.util.List;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNodeConstructor;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.dependencypath.DependencyPathsFromTreeBinary;
import eu.excitementproject.eop.distsim.domains.relation.PredicateArgumentSlots;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.DefaultCooccurrence;
import eu.excitementproject.eop.distsim.items.DefaultRelation;
import eu.excitementproject.eop.distsim.items.LexicalUnit;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.util.Pair;


/**
 * Extraction of co-occurrences, composed of predicates and arguments, from a given parsed sentence, represented by a BasicNode object
 * 
 * @author Meni Adler
 * @since 04/09/2012
 *
 */
public class NodeBasedPredArgCooccurrenceExtraction extends PredArgCooccurrenceExtraction<BasicNode> {

	public NodeBasedPredArgCooccurrenceExtraction(CommonConfig conf) /*throws ConfigurationException*/ {
		this();
	} 

	public NodeBasedPredArgCooccurrenceExtraction(ConfigurationParams confParams) /*throws ConfigurationException*/ {
		this();
	} 

	public NodeBasedPredArgCooccurrenceExtraction() {
		this.extractor =  new DependencyPathsFromTreeBinary<Info, BasicNode>(new BasicNodeConstructor(), new eu.excitementproject.eop.distsim.dependencypath.DependencyPathsFromTree.VerbAdjectiveNounPredicate<Info>(), true, true);
	} 
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.cooccurrence.CooccurrenceExtraction#extractCooccurrences(java.lang.Object)
	 */
	@Override
	public Pair<? extends List<? extends TextUnit>, ? extends List<? extends Cooccurrence<PredicateArgumentSlots>>> extractCooccurrences(BasicNode root) throws CooccurrenceExtractionException {
		List<LexicalUnit> textUnints = new LinkedList<LexicalUnit>();
		List<DefaultCooccurrence<PredicateArgumentSlots>> coOccurrences = new LinkedList<DefaultCooccurrence<PredicateArgumentSlots>>();
		try {
			for (String dependencyPath : extractor.stringDependencyPaths(root)) {
				
				//debug
				//System.out.println(dependencyPath);
				
				//dependencyPath = dependencyPath.trim();
				int pos1 = dependencyPath.indexOf("<");
				int pos2 = dependencyPath.lastIndexOf(">");
				LexicalUnit arg1 = new LexicalUnit(dependencyPath.substring(2,pos1-2));
				LexicalUnit arg2 = new LexicalUnit(dependencyPath.substring(pos2+3,dependencyPath.length()-2));
				LexicalUnit pred = new LexicalUnit(dependencyPath.substring(pos1-1,pos2+2));
				textUnints.add(pred);
				textUnints.add(arg1);
				textUnints.add(arg2);
				
				coOccurrences.add(new DefaultCooccurrence<PredicateArgumentSlots>(pred, arg1, new DefaultRelation<PredicateArgumentSlots>(PredicateArgumentSlots.X)));
				coOccurrences.add(new DefaultCooccurrence<PredicateArgumentSlots>(pred, arg2, new DefaultRelation<PredicateArgumentSlots>(PredicateArgumentSlots.Y)));
			}
		} catch (Exception e) {			
			throw new CooccurrenceExtractionException(e);
		}
		return new Pair<List<LexicalUnit>,List<DefaultCooccurrence<PredicateArgumentSlots>>>(textUnints,coOccurrences);
	}

	DependencyPathsFromTreeBinary<Info, BasicNode> extractor;
}
