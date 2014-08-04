package eu.excitementproject.eop.transformations.generic.truthteller;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.resource.ResourceInitializationException;

import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.transformations.generic.truthteller.conll.AnnotateSentenceToConll;
import eu.excitementproject.eop.transformations.generic.truthteller.conll.ConllConverterException;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/*
 * An implementation of the "inner tool" of the analysis engine,
 * serves a wrapper for the TruthTeller calls.
 */

public class TruthTellerAnnotator implements TruthAnnotator {

	
	private AnnotateSentenceToConll app;
	private ConfigurationFile confFile;
	private String sentence;
	private Map<Integer, SingleTokenTruthAnnotation> annotationResult;
	
	public TruthTellerAnnotator(File config) throws TruthAnnotatorException{
		annotationResult = new  HashMap<Integer, SingleTokenTruthAnnotation>();
		
		try{
			confFile = new ConfigurationFile(config);
		}
		catch(Exception e){
			throw new TruthAnnotatorException(e.getMessage(),e);
		}
	}
	
	
	@Override
	public void init() throws TruthAnnotatorException, ResourceInitializationException, ConllConverterException {		
		try {
			
			app = new AnnotateSentenceToConll(confFile);
			
		} catch (ConfigurationException e) {
			throw new ResourceInitializationException(e);
		} catch (ConllConverterException e) {
			throw new ConllConverterException(e.getMessage(),e);
		}

	}

	@Override
	public void setSentence(String sentence) {
		this.sentence = sentence;
		// clear annotation result
		annotationResult = new  HashMap<Integer, SingleTokenTruthAnnotation>();
	}

	@Override
	public void annotate() throws TruthAnnotatorException, ConllConverterException {
		// run TruthTeller
		ExtendedNode annotatedSentece = app.annotateSentece(sentence);
			
		// iterate over nodes and extract annotations to UIMA format
		List<ExtendedNode> nodes = AbstractNodeUtils.treeToList(annotatedSentece);
		
		for (ExtendedNode node : nodes){
			int id = Integer.parseInt(node.getInfo().getId()); // this node's id in the original sentence
			int minimalIndex = -1,maximalIndex = -1; // variables to store the boundaries of the subtree
			AdditionalNodeInformation info = node.getInfo().getAdditionalNodeInformation();
			// store result from info, according to index in the original sentence
			SingleTokenTruthAnnotation singleTokenAnnotation =new SingleTokenTruthAnnotation(info.getPredTruth(),info.getClauseTruth(),info.getNegationAndUncertainty(),info.getPredicateSignature()); 
		
			if (singleTokenAnnotation.getClauseTruthValue() !=null){
				// get a list of all subtree tokens, by getting the deep antecedent of all 
				// the subtree, and storing in the set - thus obtaining a unique copy of all "real" tokens
				Set<ExtendedNode> subtree = new HashSet<ExtendedNode>();
				for (ExtendedNode child : AbstractNodeUtils.treeToList(node)){
					ExtendedNode toAdd =AbstractNodeUtils.getDeepAntecedentOf(child);
					int curId =  Integer.parseInt(toAdd.getInfo().getId()) -1; 
					subtree.add(toAdd);
					// calculate boundaries
					if ((minimalIndex == -1)||(curId < minimalIndex)){
						minimalIndex = curId;
					}
					if ((maximalIndex == -1)||(curId > maximalIndex)){
						maximalIndex = curId;
					}
				}
				
				// store the subtree and its boundaries
				singleTokenAnnotation.setSubtree(subtree);
				singleTokenAnnotation.setSubtreeMinimalIndex(minimalIndex);
				singleTokenAnnotation.setSubtreeMaximalIndex(maximalIndex);
			}
			annotationResult.put(id-1,singleTokenAnnotation); //TODO: make sure that id-1 is right

		}
	}

	@Override
	public Map<Integer, SingleTokenTruthAnnotation> getAnnotatedEntities() {
		return annotationResult;
	}

	@Override
	public void cleanUp() {
		// stub - nothing to do to close TruthTeller

	}

}
