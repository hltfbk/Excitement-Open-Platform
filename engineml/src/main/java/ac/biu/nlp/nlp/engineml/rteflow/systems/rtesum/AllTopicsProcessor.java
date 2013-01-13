package ac.biu.nlp.nlp.engineml.rteflow.systems.rtesum;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import ac.biu.nlp.nlp.engineml.classifiers.LabeledSample;
import ac.biu.nlp.nlp.engineml.generic.truthteller.AnnotatorException;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6mainIOException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;

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
