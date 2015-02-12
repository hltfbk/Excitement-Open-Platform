package eu.excitementproject.eop.alignmentedas.p1eda.visualization;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.alignmentedas.p1eda.TEDecisionWithAlignment;

/**
 * This interface defines the basic functionality of the visualizer: generation of a stand-alone html given a JCas.
 * The generated html visualizes the various annotations and alignments defined in the JCas (with filtering options).
 * 
 * 
 * @author Meni Adler
 * @since Jan 6, 2015
 *
 */


public interface Visualizer {
	
	/**
	 * Generates an html string, which visualizes the various annotations and alignments defined in the JCas (with filtering options).
	 * 
	 * @param jcas JCas object, composed of text, hypothesis and their annotations (e.g., part-of-speech, dependency relations, alignments)
	 * @return an html string, which visualizes the various annotations and alignments defined in the JCas.
	 * @throws VisualizerGenerationException
	 */
	String generateHTML(JCas jcas) throws VisualizerGenerationException;
	
	/**
	 * Generates an html string, which visualizes the various annotations and alignments defined in the JCas (with filtering options), and some details on the entailment decision
	 * 
	 * @param decision TEDecisionWithAlignment object, composed of JCas, feature vector and entailment decision
	 * @return an html string, which visualizes the various annotations and alignments defined in the JCas, the features, and the entailment decision.
	 * @throws VisualizerGenerationException
	 */
	String generateHTML(TEDecisionWithAlignment decision) throws VisualizerGenerationException;
}

