package eu.excitementproject.eop.biutee.utilities.safemodel;
import eu.excitementproject.eop.biutee.version.AbstractVersion;
import eu.excitementproject.eop.biutee.version.BuildType;

/**
 * 
 * 
 * @author Asher Stern
 * @since Dec 19, 2012
 *
 */
public class AdaptedVersion implements AbstractVersion
{
	private static final long serialVersionUID = -6928955750320214693L;
	
	public AdaptedVersion(){}
	
	public AdaptedVersion(int product, int major, int minor, BuildType buildType)
	{
		super();
		this.product = product;
		this.major = major;
		this.minor = minor;
		this.buildType = buildType;
	}
	@Override
	public int getProduct()
	{
		return product;
	}
	public void setProduct(int product)
	{
		this.product = product;
	}
	@Override
	public int getMajor()
	{
		return major;
	}
	public void setMajor(int major)
	{
		this.major = major;
	}
	@Override
	public int getMinor()
	{
		return minor;
	}
	public void setMinor(int minor)
	{
		this.minor = minor;
	}
	@Override
	public BuildType getBuildType()
	{
		return buildType;
	}
	public void setBuildType(BuildType buildType)
	{
		this.buildType = buildType;
	}
	private int product;
	private int major;
	private int minor;
	private BuildType buildType;
}
