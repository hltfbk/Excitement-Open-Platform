package eu.excitementproject.eop.biutee.utilities.safemodel.classifiers_io;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import eu.excitementproject.eop.biutee.classifiers.io.LearningModel;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructure;
import eu.excitementproject.eop.biutee.utilities.safemodel.SafeModel;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * A class used for safe storage of {@link LearningModel}.
 * <P>
 * <B>See instructions about classifiers in the package-java-doc (package-info.java)
 * of package <code>eu.excitementproject.eop.biutee.classifiers</code></B>
 * <P>
 * 
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
