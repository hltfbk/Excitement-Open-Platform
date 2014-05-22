/**
 * UIMA layer of the BIU LAP.<BR>
 * 
 * This package provides a two-way conversion:<BR>
 * From a BIU dependency parse tree (referenced by its root {@link AbstractNode}) into a fully
 * processed CAS, and from a fully process CAS back to a BIU dependency tree.<BR>
 * 
 * With this functionality, BIU preprocessing tools still support their current interfaces,
 * where this package wraps them in a thin layer that just performs the proper UIMA conversions.<BR>
 * 
 * Implementing the conversion for each tool is divided to three sublayers:
 * <ol>
 * 
 * <li><b>Generic Layer</b> - implemented in the abstract class 
 * {@link eu.excitementproject.eop.lap.biu.uima.ae.SingletonSynchronizedAnnotator}.
 * This generic annotator holds the instance of the BIU tool, and makes sure that constructing and
 * initializing the tool is synchronized, implementing this as a singleton (any subsequent references
 * to the same type of tool will acquire the same instance). 
 * 
 * <li><b>Tool Type Layer</b> - implemented in {@link eu.excitementproject.eop.lap.biu.uima.ae.*} packages.
 * Types can be sentence splitter, tokenizer, pos tagger, dependency parser, etc. Each type has its own
 * subpackage, such as <code>postagger</code>, and its own abstract class, such as
 * {@link eu.excitementproject.eop.lap.biu.uima.ae.postagger.PosTaggerAE}. The class mainly overrides
 * the abstract method {@link org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)}.
 * This method accesses the inner tool via the corresponding BIU type, such as
 * {@link eu.excitementproject.eop.lap.biu.postagger.PosTagger}. The method calls the relevant methods
 * of the BIU type with appropriate input, processes the output, and adds relevant annotations to
 * the CAS.
 * 
 * <li><b>Specific Tool Layer</b> - implemented in the same packages as the Tool Type Layer.
 * Each class refers to a specific tool. For instance, for the tool type
 * {@link eu.excitementproject.eop.lap.biu.uima.ae.postagger.PosTaggerAE}, a specific tool may be
 * {@link eu.excitementproject.eop.lap.biu.uima.ae.postagger.MaxentPosTaggerAE}. These classes are
 * deliberately very small, and are only in charge of getting configuration parameters, and
 * constructing and initializing the specific tool.
 * 
 * </ol>
 * 
 * 
 * Main components:
 * <ol>
 * <li>Class {@link BIUFullLAP} - The full BIU LAP, consisting of an aggregate analysis engine
 * with specific tools. 
 * <li>Class {@link CasTreeConverter} - Converts a CAS with annotations into a BIU dependency tree.
 * <li>Packages {@link eu.excitementproject.eop.lap.biu.uima.ae.*} - Implementation of the three layers,
 * containing tools such as sentence splitter, tokenizer, pos tagger, ner and parser.
 * </ol>
 * 
 * Main test components:
 * <ol>
 * <li>{@link BIU_LAP_Test} - Tests {@link BIUFullLAP} against hard-coded fully parsed data.
 * <li>{@link CasTreeConverterTest} - Tests {@link CasTreeConverter} against a tree built by BIU non-UIMA tools.
 * </ol>
 * 
 * @author Ofer Bronstein
 */
package eu.excitementproject.eop.lap.biu.uima;