package ac.biu.nlp.nlp.engineml.utilities.safemodel.classifiers_io;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import ac.biu.nlp.nlp.engineml.classifiers.io.LearningModel;
import ac.biu.nlp.nlp.engineml.rteflow.systems.FeatureVectorStructure;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.engineml.utilities.safemodel.SafeModel;

/**
 * 
 * @author Asher Stern
 * @since Dec 26, 2012
 *
 */
@XmlRootElement
public class SafeLearningModel extends SafeModel<LearningModel>
{
	private static final long serialVersionUID = 5382612167397445128L;

	public SafeLearningModel()
	{
		super();
	}

	public SafeLearningModel(FeatureVectorStructure featureVectorStructure,
			LearningModel modelObject,List<String> learningModelClassNames) throws TeEngineMlException
	{
		super(featureVectorStructure, modelObject);
		this.learningModelClassNames = learningModelClassNames;
	}
	
	public List<String> getLearningModelClassNames()
	{
		return learningModelClassNames;
	}

	public void setLearningModelClassNames(List<String> learningModelClassNames)
	{
		this.learningModelClassNames = learningModelClassNames;
	}





	private List<String> learningModelClassNames;
}
