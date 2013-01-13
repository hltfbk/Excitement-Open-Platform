package ac.biu.nlp.nlp.engineml.rteflow.macro;
import java.io.Serializable;
import java.util.Map;

import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;

/**
 * Encapsulates a tree and a feature-vector.
 * 
 * @author Asher Stern
 * 
 *
 */
public class TreeAndFeatureVector implements Serializable
{
	private static final long serialVersionUID = 699297065323409485L;
	
	public TreeAndFeatureVector(ExtendedNode tree, Map<Integer, Double> featureVector) throws TeEngineMlException
	{
		if (null==tree) throw new TeEngineMlException("Null tree");
		if (null==featureVector) throw new TeEngineMlException("Null featureVector");
		this.tree = tree;
		this.featureVector = featureVector;
	}
	
	
	
	public ExtendedNode getTree()
	{
		return tree;
	}
	public Map<Integer, Double> getFeatureVector()
	{
		return featureVector;
	}



	private ExtendedNode tree;
	private Map<Integer, Double> featureVector;

}
