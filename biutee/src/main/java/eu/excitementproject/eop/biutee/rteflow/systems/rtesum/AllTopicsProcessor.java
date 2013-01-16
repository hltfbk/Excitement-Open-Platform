package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6mainIOException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

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
