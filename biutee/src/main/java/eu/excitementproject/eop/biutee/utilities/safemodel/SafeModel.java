package eu.excitementproject.eop.biutee.utilities.safemodel;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructure;
import eu.excitementproject.eop.biutee.version.Version;
import eu.excitementproject.eop.transformations.datastructures.BooleanAndString;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * @author Asher Stern
 * @since Dec 18, 2012
 *
 * @param <T>
 */
@XmlType(propOrder={"version", "featureVectorStructure" , "modelObject"})
public abstract class SafeModel<T> implements Serializable
{
	private static final long serialVersionUID = 7208333404551139729L;
	
	protected SafeModel(FeatureVectorStructure featureVectorStructure, T modelObject) throws TeEngineMlException
	{
		super();
		this.version = Version.getVersion();
		this.featureVectorStructure = featureVectorStructure;
		this.modelObject = modelObject;
		
		validateFieldsCorrecteness();
	}
	
	protected SafeModel(){}
	
	
	
	@XmlJavaTypeAdapter(VersionAdapter.class)
	public Version getVersion()
	{
		return version;
	}
	
	@XmlJavaTypeAdapter(FeatureVectorStructureAdapter.class)
	public FeatureVectorStructure getFeatureVectorStructure()
	{
		return featureVectorStructure;
	}
	
	public T getModelObject()
	{
		return modelObject;
	}
	
	
	
	public void setVersion(Version version)
	{
		if (this.version!=null) throw new SafeModelSafetyException("Cannot set version twice!");
		this.version = version;
	}

	public void setFeatureVectorStructure(FeatureVectorStructure featureVectorStructure)
	{
		if (this.featureVectorStructure!=null) throw new SafeModelSafetyException("Cannot set feature-vector-structure twice!");
		this.featureVectorStructure = featureVectorStructure;
	}

	public void setModelObject(T modelObject)
	{
		if (this.modelObject!=null) throw new SafeModelSafetyException("Cannot set model twice!");
		this.modelObject = modelObject;
	}

	private void validateFieldsCorrecteness() throws TeEngineMlException
	{
		if (null==modelObject) throw new TeEngineMlException("null model");
		if (null==featureVectorStructure) throw new TeEngineMlException("null featureVectorStructure");
		// if (!featureVectorStructure.isBuilt()) throw new TeEngineMlException("featureVectorStructure not built");
	}

	
	
	
	/**
	 * Checks whether the given {@linkplain SafeModel} are compatible with the current
	 * system.
	 * @return a {@link BooleanAndString} which stores <tt>true</tt> or <tt>false</tt> for compatible or incompatible,
	 * and an error-message in case of incompatibility.
	 * 
	 * @throws TeEngineMlException
	 */
	public static BooleanAndString isCompatible(SafeModel<?> safeModel, FeatureVectorStructure featureVectorStructure, boolean withString) throws TeEngineMlException
	{
		if (null==safeModel) throw new TeEngineMlException("null");
		if (null==featureVectorStructure) throw new TeEngineMlException("null");
		BooleanAndString featureVectorStructureEquals = 
				featureVectorStructure.isCompatibleWithString(safeModel.getFeatureVectorStructure(),withString);
		if (featureVectorStructureEquals.getBooleanValue())
		{
			if (Version.getVersion().identicalTo(safeModel.getVersion()))
			{
				return new BooleanAndString(true, null);
			}
			else
			{
				return new BooleanAndString(false, "incompatible system versions");
			}
		}
		else
		{
			return featureVectorStructureEquals;
		}
	}
	



	
	private Version version=null;
	private FeatureVectorStructure featureVectorStructure=null;
	protected T modelObject=null;
}
