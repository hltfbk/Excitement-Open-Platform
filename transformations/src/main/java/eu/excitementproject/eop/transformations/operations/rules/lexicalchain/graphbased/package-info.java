/**
 * This package contains an actual implementation of
 * {@link eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase} with
 * generic parameter {@link eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfLexicalRules}.
 * <BR>
 * That implementation was programmed by Eyal Shnarch, using PLIS (Probabilistic Lexical
 * Inference System). {@link eu.excitementproject.eop.transformations.operations.rules.lexicalchain.graphbased.PlisRuleBase} 
 * integrate knowledge from various input lexical resources. Given a learnt model, it
 * can also provide probabilities for the lexical rules it suggests. There are several
 * probability models which extends {@link ac.biu.nlp.models.PLM}.
 * 
 * plisRuleBase_config.xml is an example for the configuration file needed to run package.
 * mplm_model_parameters.txt provide the learnt parameters for the M-PLM model. Other
 * models can be provided. The configuration file points at the model file to use.
 * 
 * @since 25 December 2012
 */
package eu.excitementproject.eop.transformations.operations.rules.lexicalchain.graphbased;

