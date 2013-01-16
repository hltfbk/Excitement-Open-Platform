package eu.excitementproject.eop.biutee.utilities.safemodel.classifiers_io;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.classifiers.io.LearningModel;
import eu.excitementproject.eop.biutee.classifiers.io.LearningModelToClassifier;
import eu.excitementproject.eop.biutee.classifiers.io.LoadableClassifier;
import eu.excitementproject.eop.biutee.classifiers.io.StorableClassifier;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructure;
import eu.excitementproject.eop.biutee.utilities.safemodel.SafeModel;
import eu.excitementproject.eop.biutee.utilities.safemodel.SafeModelSafetyException;
import eu.excitementproject.eop.transformations.datastructures.BooleanAndString;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * Methods for save and load a classifier into/from a model file.
 * 
 * @author Asher Stern
 * @since Dec 26, 2012
 *
 */
public class SafeClassifiersIO
{
	public static LoadableClassifier load(FeatureVectorStructure featureVectorStructure, File file) throws TeEngineMlException
	{
		if (null==file) throw new TeEngineMlException("Null file given to load() method of "+SafeClassifiersIO.class.getSimpleName());
		try
		{
			JAXBContext contextToGetNames = JAXBContext.newInstance(SafeLearningModel.class);
			Unmarshaller unmarshallerToGetNames = contextToGetNames.createUnmarshaller();
			SafeLearningModel safeLearningModel =
					(SafeLearningModel) unmarshallerToGetNames.unmarshal(file);

			List<String> classNames = safeLearningModel.getLearningModelClassNames();


			Vector<Class<?>> classes = new Vector<Class<?>>();
			classes.add(SafeLearningModel.class);
			for (String className : classNames)
			{
				classes.add(Class.forName(className));
			}

			JAXBContext contextToGetClassifier = JAXBContext.newInstance(classes.toArray(new Class<?>[0]));
			Unmarshaller unmarshallerToGetClassifier = contextToGetClassifier.createUnmarshaller();
			SafeLearningModel safeLearningModel2 =
					(SafeLearningModel) unmarshallerToGetClassifier.unmarshal(file);


			BooleanAndString compatilibity = SafeModel.isCompatible(safeLearningModel2, featureVectorStructure, true);
			if (false==compatilibity.getBooleanValue())
			{
				throw new SafeModelSafetyException("Classifier loaded from file: \""+file.getPath()+"\" is incompatible with the current system.\n"+compatilibity.getString());
			}


			LearningModel learningModel = safeLearningModel2.getModelObject();
			LoadableClassifier classifier = LearningModelToClassifier.createForModel(learningModel);
			return classifier;
		}
		catch (JAXBException e)
		{
			throw new TeEngineMlException("Failed to load a classifier from file: "+file.getPath(),e);
		}
		catch (ClassNotFoundException e)
		{
			throw new TeEngineMlException("Failed to load a classifier from file: "+file.getPath(),e);
		}
		catch (ClassifierException e)
		{
			throw new TeEngineMlException("Failed to load a classifier from file: "+file.getPath(),e);
		}
	}
	
	public static LinearClassifier loadLinearClassifier(FeatureVectorStructure featureVectorStructure, File file) throws TeEngineMlException
	{
		return (LinearClassifier)load(featureVectorStructure,file);
	}

	public static void store(StorableClassifier classifier, FeatureVectorStructure featureVectorStructure, File file) throws TeEngineMlException 
	{
		try
		{
			LearningModel learningModel = classifier.store();
			
			Vector<Class<?>> classes = new Vector<Class<?>>();
			classes.add(SafeLearningModel.class);
			Set<Class<?>> learningModelClasses = findAllClassesOfLearningModel(learningModel);
			classes.addAll(learningModelClasses);
			List<String> learningModelClassNames = createListOfClassNames(learningModelClasses);
			
			SafeLearningModel safeModel = new SafeLearningModel(featureVectorStructure,learningModel,learningModelClassNames);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(classes.toArray(new Class<?>[0]));
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(safeModel, file);
		}
		catch (JAXBException e)
		{
			throw new TeEngineMlException("Storing classifier model in and XML file failed. (Tried to save to file: \""+file.getPath()+")",e);
		}
		catch (ClassifierException e)
		{
			throw new TeEngineMlException("Storing classifier model in and XML file failed. (Tried to save to file: \""+file.getPath()+")",e);
		}
	}
	
	
	///////////////////// PRIVATE ///////////////////// 
	
	private static Set<Class<?>> findAllClassesOfLearningModel(LearningModel learningModel)
	{
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		LearningModel current = learningModel;
		while (current != null)
		{
			classes.add(current.getClass());
			current = current.getNestedModel();
		}
		
		return classes;
	}
	
	private static List<String> createListOfClassNames(Collection<? extends Class<?>> classes)
	{
		List<String> ret = new ArrayList<String>(classes.size());
		for (Class<?> clazz : classes)
		{
			ret.add(clazz.getName());
		}
		return ret;
	}
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(SafeClassifiersIO.class);
}
