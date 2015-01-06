package eu.excitementproject.eop.alignmentedas.p1eda.visualization;

import java.util.Vector;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.alignmentedas.p1eda.TEDecisionWithAlignment;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.FeatureValue;
import eu.excitementproject.eop.common.DecisionLabel;

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
	 * @param jcas JCas object, composed of text, hypothesis and their annotations (e.g., part-of-speech, dependency relations, alignments)
	 * @return an html string, which visualizes the various annotations and alignments defined in the JCas.
	 * @throws VisualizerGenerationException
	 */
	String generateHTML(JCas jcas) throws VisualizerGenerationException;
	
	/**
	 * @param decision TEDecisionWithAlignment object, composed of JCas, feature vector and entailment decision
	 * @return an html string, which visualizes the various annotations and alignments defined in the JCas, the features, and the entailment decision.
	 * @throws VisualizerGenerationException
	 */
	String generateHTML(TEDecisionWithAlignment decision) throws VisualizerGenerationException;

}


class Temp {
	public static void foo () {
		JCas jcas = null;
		Vector<FeatureValue> featureVector = new Vector<FeatureValue>();
		featureVector.add(new FeatureValue("feature1",0.1));
		featureVector.add(new FeatureValue("feature2",0.3));
		featureVector.add(new FeatureValue("feature3",0.7));
		TEDecisionWithAlignment decision = new TEDecisionWithAlignment(DecisionLabel.Entailment, 0.5, "", jcas, featureVector);
	}
	
}

