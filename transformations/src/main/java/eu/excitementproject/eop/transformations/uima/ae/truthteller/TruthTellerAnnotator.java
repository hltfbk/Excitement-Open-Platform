package eu.excitementproject.eop.transformations.uima.ae.truthteller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.lap.biu.test.BiuTestParams;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.generic.truthteller.DefaultSentenceAnnotator;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * An implementation of the "inner tool" of the analysis engine,
 * serves a wrapper for the TruthTeller calls.
 * @author Gabi Stanovsky
 * @since Aug 2014
 */

public class TruthTellerAnnotator implements PredicateTruth {

	
	private DefaultSentenceAnnotator annotator;
	private File annotationRulesFile;
	private ExtendedNode annotatedSentence;
	private List<SingleTokenTruthAnnotation> annotationResult;
	
	/**
	 * Constructor which receives the annotation rules file
	 * @param IannotationRulesFile
	 * @throws PredicateTruthException
	 */
	public TruthTellerAnnotator(File IannotationRulesFile) throws PredicateTruthException{
			annotationRulesFile = IannotationRulesFile;
	}
	
	/**
	 * Default constructor which uses a default file location for annotation rules file
	 * @throws PredicateTruthException
	 */
	public TruthTellerAnnotator() throws PredicateTruthException{
		annotationRulesFile = new File(BiuTestParams.TRUTH_TELLER_MODEL_FILE);
	}
	
	
	@Override
	public void init() throws PredicateTruthException {					
		try {
			annotator = new DefaultSentenceAnnotator(annotationRulesFile);
		} catch (AnnotatorException e) {
			throw new PredicateTruthException(e.getMessage(),e);
		}
	}

	@Override
	public void setSentence(ExtendedNode annotatedSentence) {
		this.annotatedSentence = annotatedSentence;
		// clear annotation result
		annotationResult = new  ArrayList<SingleTokenTruthAnnotation>();
	}

	@Override
	public void annotate() throws PredicateTruthException {
		try {
			// run TruthTeller
			annotator.setTree(annotatedSentence);
			annotator.annotate();
			ExtendedNode ttResult = annotator.getAnnotatedTree();
			Map<Integer,SingleTokenTruthAnnotation> annotationMap = new HashMap<Integer, SingleTokenTruthAnnotation>(); //needed since truth annotations won't be read in the sentence order
			
			// iterate over nodes and extract annotations to UIMA format
			List<ExtendedNode> nodes = AbstractNodeUtils.treeToList(ttResult);
			
			for (ExtendedNode node : nodes){
				assert(node.getInfo().getNodeInfo().getWord() != null);
				int id = node.getInfo().getNodeInfo().getSerial()-1; // this node's id in the original sentence
				AdditionalNodeInformation info = node.getInfo().getAdditionalNodeInformation();
				// store result from info, according to index in the original sentence
				SingleTokenTruthAnnotation singleTokenAnnotation =new SingleTokenTruthAnnotation(info.getPredTruth(),info.getClauseTruth(),info.getNegationAndUncertainty(),info.getPredicateSignature()); 
			
				if (singleTokenAnnotation.getClauseTruthValue() !=null){
					// get a list of all subtree tokens, by getting the deep antecedent of all 
					// the subtree, and storing in the set - thus obtaining a unique copy of all "real" tokens
					int minimalIndex = -1,maximalIndex = -1; // variables to store the boundaries of the subtree
					Set<ExtendedNode> subtree = new HashSet<ExtendedNode>();
					for (ExtendedNode child : AbstractNodeUtils.treeToList(node)){
						ExtendedNode toAdd =AbstractNodeUtils.getDeepAntecedentOf(child);
						int curId =  node.getInfo().getNodeInfo().getSerial()-1;
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
					singleTokenAnnotation.setSubtree(new ArrayList<ExtendedNode>(subtree));
					singleTokenAnnotation.setSubtreeMinimalIndex(minimalIndex);
					singleTokenAnnotation.setSubtreeMaximalIndex(maximalIndex);
				}
				annotationMap.put(id,singleTokenAnnotation); 
	
			}
			
			//convert the map into a list - assumes there's a truth annotation for each token index
			for (int i=0; i < nodes.size();i++){
				annotationResult.add(annotationMap.get(i));
			}
			
		} catch (AnnotatorException e) {
			throw new PredicateTruthException(e.getMessage(),e);
		}
	}

	@Override
	public List<SingleTokenTruthAnnotation> getAnnotatedEntities() {
		return annotationResult;
	}

	@Override
	public void cleanUp() {
		// stub - nothing to do to close TruthTeller

	}

}
