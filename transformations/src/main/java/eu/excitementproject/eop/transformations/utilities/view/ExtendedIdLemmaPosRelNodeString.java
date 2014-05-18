/**
 * 
 */
package eu.excitementproject.eop.transformations.utilities.view;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.NodeString;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * A {@link NodeString} that produces string representations for {@link ExtendedNode}s like: 
 * 		<code>id:lemma{X_8}/[NOUN]/<dobj>/{predicateType}/{negation}/{clauseTruth}/{monotonicity}</code> 
 * @author Amnon Lotan
 * @since 04/06/2011
 * 
 */
public class ExtendedIdLemmaPosRelNodeString implements NodeString<ExtendedInfo> 
{
	/**
	 * Returns a string representations for {@link ExtendedNode}s like: 
 * 		<code>id:lemma{X_8}/[NOUN]/<dobj>/{predicateType}/{negation}/{clauseTruth}/{monotonicity}</code>
	 * @see eu.excitementproject.eop.common.representation.parse.tree.dependency.view.NodeString#getStringRepresentation()
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
		if (null==var)
			ret = id+":"+lemma+"/["+pos+"]"+"/<"+rel+">"+"/{"+predicateType+"}/{"+negation+"}/{"+clauseTruth+"}/{"+predTruth+"}/{"+monotonicity+"}";
		else
			ret = id+":"+lemma+"{X_"+var+"}"+"/["+pos+"]"+"/<"+rel+">"+"/{"+predicateType+"}/{"+negation+"}/{"+clauseTruth+"}/{"+monotonicity+"}";
		
		return ret;
		
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.NodeString#set(ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode)
	 */
	public void set(AbstractNode<? extends ExtendedInfo, ?> node)
	{
		this.node = node;
	}
	
	protected AbstractNode<? extends ExtendedInfo, ?> node;

}
