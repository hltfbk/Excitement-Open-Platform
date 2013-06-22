/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.cooccurrence;

import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.domains.relation.PredicateArgumentSlots;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.DefaultCooccurrence;
import eu.excitementproject.eop.distsim.items.DefaultRelation;
import eu.excitementproject.eop.distsim.items.LexicalUnit;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * 
 * Extraction of co-occurrences, composed of predicates and arguments, based on a given string, composed of a binary predicate and its arguments, 
 * in the format: arg1 \t predicate \t arg2
 * 
 * @author Meni Adler
 * @since 04/09/2012
 *
 */
public class TupleBasedPredArgCooccurrenceExtraction extends PredArgCooccurrenceExtraction<String> {

	public static final String DELIMITER = "\t";
	public static final int ARG1_INDEX = 0;
	public static final int PRED_INDEX = 1;
	public static final int ARG2_INDEX = 2;
	public static final int COUNT_INDEX = 3;


	public TupleBasedPredArgCooccurrenceExtraction() {}
	
	public TupleBasedPredArgCooccurrenceExtraction(ConfigurationParams params) {}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.cooccurrence.CooccurrenceExtraction#extractCooccurrences(java.lang.Object)
	 */
	@Override
	public Pair<? extends List<? extends TextUnit>, ? extends List<? extends Cooccurrence<PredicateArgumentSlots>>> extractCooccurrences(String line) throws CooccurrenceExtractionException {
		List<LexicalUnit> textUnints = new LinkedList<LexicalUnit>();
		List<DefaultCooccurrence<PredicateArgumentSlots>> coOccurrences = new LinkedList<DefaultCooccurrence<PredicateArgumentSlots>>();
		try {
			String[] tokens = line.split(DELIMITER);
			LexicalUnit pred = new LexicalUnit(tokens[PRED_INDEX]);
			LexicalUnit arg1 = new LexicalUnit(tokens[ARG1_INDEX]);
			LexicalUnit arg2 = new LexicalUnit(tokens[ARG2_INDEX]);
			textUnints.add(pred);
			textUnints.add(arg1);
			textUnints.add(arg2);
			coOccurrences.add(new DefaultCooccurrence<PredicateArgumentSlots>(pred, arg1, new DefaultRelation<PredicateArgumentSlots>(PredicateArgumentSlots.X)));
			coOccurrences.add(new DefaultCooccurrence<PredicateArgumentSlots>(pred, arg2, new DefaultRelation<PredicateArgumentSlots>(PredicateArgumentSlots.Y)));
		} catch (Exception e) {			
			throw new CooccurrenceExtractionException(e);
		}
		return new Pair<List<LexicalUnit>,List<DefaultCooccurrence<PredicateArgumentSlots>>>(textUnints,coOccurrences);
	}
}
