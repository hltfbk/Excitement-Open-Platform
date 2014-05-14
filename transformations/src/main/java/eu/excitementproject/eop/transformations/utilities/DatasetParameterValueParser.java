package eu.excitementproject.eop.transformations.utilities;
import java.util.LinkedHashSet;

import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;


/**
 * Parses the value of the parameter "dataset" in the configuration file.
 * <P>
 * If, like in most cases, the value is just a file-name of the data-set, or a directory
 * name (in case of RTE-sum) - then this value is returned. However, if multiple
 * datasets are given as the value of that parameter, then the (String) value is
 * parsed and the list of datasets, each with a corresponding "meta name", is provided.
 * <P>
 * The "meta name" is required to identify each dataset to its "type" or "distribution".
 * For example, the behavior of the classifier on RTE2 might differ from its
 * behavior on RTE3. So, in addition to the file-name, the "type" (RTE2, RTE3, etc.)
 * should be provided as well. This type ("meta-name") will also be used in test time.
 * The syntax in the configuration file should be:
 * <BR>
 * "RTE2=rte2.xml;RTE3=rte3.xml"
 * <BR>
 * In test, some "meta-name"s can be "empty" (i.e. with no data-set attached). For example:
 * <BR>
 * "RTE2;RTE3=rte3test.xml"
 * <BR>
 * This indicates that the classifier should be aware that in training there was also
 * a dataset for RTE2, though no RTE2 dataset exists in test.
 * 
 * @author Asher Stern
 * @since 27 july 2012
 *
 */
public class DatasetParameterValueParser
{
	public static final String SEPARATOR = ";";
	public static final String NAME_AND_FILENAME_SEPARATOR = "=";
	
	/**
	 * A constructor with the value of the dataset parameter in the configuration file.
	 * @param parameterValue
	 */
	public DatasetParameterValueParser(String parameterValue)
	{
		super();
		this.parameterValue = parameterValue;
	}

	/**
	 * Parses the dataset parameter value.
	 * 
	 * @throws TeEngineMlException
	 */
	public void parse() throws TeEngineMlException
	{
		String[] components = parameterValue.split(SEPARATOR);
		if (components.length<=0) throw new TeEngineMlException("No components. It seems that the given parameter value for dataset(s) was empty or null");
		
		if (components.length==1)
		{
			if (!components[0].contains(NAME_AND_FILENAME_SEPARATOR))
			{
				singleFileNameWithNoDatasetName = components[0].trim();
			}
		}
		
		if (null==singleFileNameWithNoDatasetName)
		{
			parseToSetAndMap(components);
		}
		parsed = true;
	}
	
	
	/**
	 * Returns a set of dataset-types (or "meta-name"). E.g., RTE2, RTE3.
	 * @return
	 * @throws TeEngineMlException
	 */
	public LinkedHashSet<String> getDatasetNames() throws TeEngineMlException
	{
		if (!parsed) throw new TeEngineMlException("Not parsed");
		return datasetNames;
	}

	/**
	 * Returns a map from each type to a set of dataset files of that type.
	 * (dataset file = the actual files with the data).
	 * 
	 * @return
	 * @throws TeEngineMlException
	 */
	public ValueSetMap<String, String> getMapDatasetNameToFileName() throws TeEngineMlException
	{
		if (!parsed) throw new TeEngineMlException("Not parsed");
		return mapDatasetNameToFileName;
	}

	/**
	 * If the dataset parameter value was just a single dataset (just a file name),
	 * with no "meta-name" - then this method returns this dataset.
	 * @return
	 * @throws TeEngineMlException
	 */
	public String getSingleFileNameWithNoDatasetName() throws TeEngineMlException
	{
		if (!parsed) throw new TeEngineMlException("Not parsed");
		return singleFileNameWithNoDatasetName;
	}

	////////////////////////// PRIVATE //////////////////////////
	
	private void parseToSetAndMap(String[] components) throws TeEngineMlException
	{
		datasetNames = new LinkedHashSet<String>();
		mapDatasetNameToFileName = new SimpleValueSetMap<String, String>();
		for (String component : components)
		{
			if (!component.contains(NAME_AND_FILENAME_SEPARATOR)) // then it is only dataset name, no file name.
			{
				datasetNames.add(component.trim());
			}
			else
			{
				String[] dsNameAndFileName = component.split(NAME_AND_FILENAME_SEPARATOR);
				if (dsNameAndFileName.length!=(1+1))
					throw new TeEngineMlException("Illegal parameter value of dataset(s): "+parameterValue);
				else
				{
					String datasetNameComponent = dsNameAndFileName[0].trim();
					String fileNameComponent = dsNameAndFileName[1].trim();
					datasetNames.add(datasetNameComponent);
					mapDatasetNameToFileName.put(datasetNameComponent,fileNameComponent);
				}
			}
			
		}
		
	}

	private String parameterValue;
	
	private boolean parsed = false;
	
	private LinkedHashSet<String> datasetNames = null;
	private ValueSetMap<String, String> mapDatasetNameToFileName = null;
	private String singleFileNameWithNoDatasetName = null;
}
