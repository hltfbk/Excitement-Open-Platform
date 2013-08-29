package eu.excitementproject.eop.core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasure;

import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.scoring.DKProSimilarityScoring;


/**
 * Abstract base class for all DKProSimilarity EDAs. Supports loading text similarity
 * measures directly from the configuration.
 */
public abstract class DKProSimilarityEDA_ImplBase<T extends TEDecision>
	implements EDABasic<ClassificationTEDecision>
{
	public static final String LF = System.getProperty("line.separator");
	
	/**
	 * Types which can be read from the configuration and wrapped automatically. 
	 */
	public enum WrappableType
	{
		Integer,
		Double,
		String,
		File
	}
	
	private List<ScoringComponent> components;
	
	@Override
	public void initialize(CommonConfig config)
		throws ConfigurationException, EDAException, ComponentException
	{
		initializeComponents(config);
	}
	
	/**
	 * Loads the text similarity measures from the configuration and assigns
	 * them to the {@link DKProSimilarityScoring} component. 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initializeComponents(CommonConfig config)
			throws ConfigurationException, ComponentException
	{
		NameValueTable EDA = null;
		try {
			EDA = config.getSection(this.getClass().getName());
		} catch (ConfigurationException e) {
			throw new ConfigurationException(e.getMessage()
					+ " No EDA section.");
		}
		String tempComps = EDA.getString("components");
		if (null == tempComps || 0 == tempComps.trim().length()) {
			throw new ConfigurationException(
					"Wrong configuation: no components contained in the EDA!");
		}
		String[] componentArray = tempComps.split(",");

		// Set up an empty component list
		components = new ArrayList<ScoringComponent>();

		for (String compName : componentArray)
		{
			NameValueTable compSection = config.getSection(compName);
			
			if (compSection == null) {
				throw new ConfigurationException(
						"Wrong configuation: didn't find the corresponding setting for the component: "
								+ compName);
			}

			try
			{
				// Instantiate the scoring component class
				String compClassName = config.getSection(compName).getString("scoringComponent");				
				
				Class<? extends DKProSimilarityScoring> comp = (Class<? extends DKProSimilarityScoring>) Class
						.forName(compClassName);
				DKProSimilarityScoring scoringComponent = comp.newInstance();
				components.add(scoringComponent);
				
				// Get the source annotation
				Class<Annotation> annotation = (Class<Annotation>) Class.forName(compSection.getString("annotation"));
				scoringComponent.setAnnotation(annotation);
				
				// Instantiate the text similarity measure for this component
				Class<? extends TextSimilarityMeasure> measure = (Class<? extends TextSimilarityMeasure>) Class
						.forName(compSection.getString("measure"));
				
				NameValueTable paramsSection = config.getSubSection(compName, "parameters");
				
				List<Class> paramClasses = new ArrayList<Class>();
				List<Object> paramValues = new ArrayList<Object>();
				
				int i = 1;
				while (paramsSection.getString("type_" + i) != null)
				{
					String type = paramsSection.getString("type_" + i);
					String value = paramsSection.getString("value_" + i);
					
					paramClasses.add(getClassForType(WrappableType.valueOf(type)));
					paramValues.add(wrapValue(value, WrappableType.valueOf(type)));
					
					i++;
				}
				
				// Instantiate the text similarity measure
				Constructor<TextSimilarityMeasure> c = (Constructor<TextSimilarityMeasure>) measure.getConstructor(paramClasses.toArray(new Class[paramClasses.size()]));
				TextSimilarityMeasure m = (TextSimilarityMeasure) c.newInstance(paramValues.toArray());
				
				// Assign the text similarity measure to the scoring component
				scoringComponent.setMeasure(m);
			}
			catch (Exception e) {
				throw new ConfigurationException(e.getMessage());
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private Class getClassForType(WrappableType type)
	{
		switch (type)
		{
			case Integer: return int.class;
			case Double: return double.class;
			case String: return String.class;
			case File: return File.class;
			default: return null;
		}
	}
	
	private Object wrapValue(String value, WrappableType type)
	{
		switch (type)
		{
			case Integer: return Integer.valueOf(value);
			case Double: return Double.valueOf(value);
			case String: return value;
			case File: return new File(value);
			default: return null;
		}
	}

	public List<ScoringComponent> getComponents() {
		return components;
	}
}
