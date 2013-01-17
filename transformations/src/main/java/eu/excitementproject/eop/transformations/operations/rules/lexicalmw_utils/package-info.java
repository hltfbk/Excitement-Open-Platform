/**
 * This package handles a limitation of our scheme regarding multi-word
 * lexical rules.
 * Since we "leave" the string representation, and work with tree representation,
 * Nodes contain single words, not multi-word-expression (for EasyFirst. For Minipar
 * there are some multi-word expressions that exist as a single lemma in a single
 * node, while other multi-word expressions also split in several nodes).
 * 
 * Thus, when trying to apply a lexical rule that its right-hand-side
 * is a multi word, we get a node that contains a multi word expression, that
 * does not match any node in the hypothesis tree, since they all contain
 * only single words, not multi words.
 * 
 * In order to handle this, a pre-processing of the hypothesis and the lexical
 * rule bases is done. All rule bases that can be matched for the original text
 * trees, and their right hand side is a multi word expression, and that multi
 * word expression appears in the hypothesis, split in several nodes -
 * all those rules are being grouped in one new rule base, which is a regular
 * rule base ( {@link eu.excitementproject.eop.transformations.operations.rules.RuleBase} ).
 * When finding and applying rules from lexical rule base, the system also
 * investigates those "new" rule bases, and applies their rules.
 * This is done, of course, in {@link eu.excitementproject.eop.transformations.rteflow.micro.TreesGeneratorByOperations}.
 * 
 * 
 * @author Asher Stern
 * @since 4-July-2011
 * 
 */
package eu.excitementproject.eop.transformations.operations.rules.lexicalmw_utils;

