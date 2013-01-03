package ac.biu.nlp.nlp.lexical_resource.impl.verb_ocean;

/**
 * Each {@link RelationType} has an {@link EntailmentDirection} i.e. indicates a leftToRight entailment, rigthToLeft, bidirectional, or none.
 * @author Amnon Lotan
 *
 * @since 25 Dec 2011
 */
public enum EntailmentDirection {
	NONE,
	ENTAILING,
	ENTAILED_BY,
	BIDIRECTIONAL;
}
