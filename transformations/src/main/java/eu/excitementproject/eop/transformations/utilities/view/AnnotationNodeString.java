/**
 * 
 */
package eu.excitementproject.eop.transformations.utilities.view;
import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.NodeString;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRule;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.BasicAnnotationsGetFields;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.BasicRuleAnnotations;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;


/**
 * Get an {@link AnnotationRule}'s mapping from {@link ExtendedNode}s to {@link AdditionalNodeInformation}s, and then in each call 
 * to {@link #getStringRepresentation()} return a string, representing that node and its mapped annotations
 *  
 * @author Amnon Lotan
 * @since 04/06/2011
 * 
 */
public class AnnotationNodeString implements NodeString<ExtendedInfo> 
{
	private Map<ExtendedNode, BasicRuleAnnotations> mapNodesToAnnotations;

	/**
	 * Ctor
	 * @throws TreeStringGeneratorException 
	 */
	public AnnotationNodeString(Map<ExtendedNode, BasicRuleAnnotations> map) throws TreeStringGeneratorException {
		if (map == null)
			throw new TreeStringGeneratorException("Got null mapping");
		this.mapNodesToAnnotations = map;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.NodeString#getStringRepresentation()
	 */
	public String getStringRepresentation()
	{
		String id = "";
		String lemma 			= ExtendedInfoGetFields.getLemma(node.getInfo());
		String pos 				= ExtendedInfoGetFields.getPartOfSpeech(node.getInfo());
		String rel 				= ExtendedInfoGetFields.getRelation(node.getInfo());
		String clauseTruth 		=  ExtendedInfoGetFields.getClauseTruth(node.getInfo());
		String predTruth		=  ExtendedInfoGetFields.getPredTruth(node.getInfo());
		String monotonicity 	=  ExtendedInfoGetFields.getMonotonicity(node.getInfo());
		String predicateType 	=  ExtendedInfoGetFields.getPredicateSignature(node.getInfo());
		String negation  		=  ExtendedInfoGetFields.getNegationAndUncertainty(node.getInfo());
		String var = null;
		
		BasicRuleAnnotations annotations = mapNodesToAnnotations.get(node);
		String newClauseTruth 		= BasicAnnotationsGetFields .getClauseTruth(annotations, "");	//		 ExtendedInfoGetFields.getClauseTruth(annotations, "");
		String newPredTruth			=  BasicAnnotationsGetFields.getPredTruth(annotations, "");
		String newMonotonicity	 	=  BasicAnnotationsGetFields.getMonotonicity(annotations, "");
		String newPredicateType 	=  BasicAnnotationsGetFields.getPredicateSignature(annotations, "");
		String newNegation  		=  BasicAnnotationsGetFields.getNegationAndUncertainty(annotations, "");
		

		try
		{
			String id_ = node.getInfo().getId();
			if (id_!=null) id = id_;
		}
		catch(NullPointerException e){}
		try
		{
			if (node.getInfo().getNodeInfo().getVariableId()!=null)
				lemma = "*"+node.getInfo().getNodeInfo().getVariableId().toString();
		}
		catch(NullPointerException e){} 
		
		try
		{
			if (node.getInfo().getNodeInfo().isVariable())
			{
				var = node.getInfo().getNodeInfo().getVariableId().toString();
			}
		}
		catch(NullPointerException e){}
		
		String ret = null;
		String varStr = var==null ? "" : "{X_"+var+"}";
		ret = id+":"+lemma+varStr+"/["+pos+"]"+"/<"+rel+">"+"/{"+twoAnnotations(predicateType, newPredicateType)+"}/{"+twoAnnotations(negation, newNegation)+"}" +
				"/{"+twoAnnotations(clauseTruth, newClauseTruth)+"}/{"+twoAnnotations(predTruth, newPredTruth)+"}/{"+twoAnnotations(monotonicity, newMonotonicity)+"}";
		
		return ret;
		
	}

	/**
	 * Assume both string are not null
	 * @param lhsAnnotation
	 * @param rhsAnnotation 
	 * @return
	 */
	private String twoAnnotations(String lhsAnnotation, String rhsAnnotation) {
		String ret = " ";
		if (!lhsAnnotation.equals(""))
			ret = lhsAnnotation;
		if (!rhsAnnotation.equals(""))
		{
			ret += lhsAnnotation.equals(rhsAnnotation) ? "" : "-->" + rhsAnnotation;
		}
		return ret;
	}

	public void set(AbstractNode<? extends ExtendedInfo, ?> node)
	{
		this.node = node;
	}
	
	protected AbstractNode<? extends ExtendedInfo, ?> node;

}
