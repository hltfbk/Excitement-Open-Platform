package ac.biu.nlp.nlp.engineml.rteflow.systems.rtesum;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import ac.biu.nlp.nlp.datasets.rte6main.Rte6mainIOException;
import ac.biu.nlp.nlp.datasets.rte6main.SentenceIdentifier;
import ac.biu.nlp.nlp.engineml.classifiers.LabeledSample;
import ac.biu.nlp.nlp.engineml.generic.truthteller.AnnotatorException;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationException;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;

/**
 * 
 * @author Asher Stern
 * @since Aug 8, 2012
 *
 */
public interface AllTopicsProcessor
{

	public void process() throws InterruptedException, TeEngineMlException, FileNotFoundException, IOException, Rte6mainIOException, OperationException, TreeStringGeneratorException, TreeCoreferenceInformationException, AnnotatorException;
	
	public Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> getAllTopicsResults() throws TeEngineMlException;
	
	public Vector<LabeledSample> getResultsSamples() throws TeEngineMlException;
	
}
