package eu.excitementproject.eop.biutee.version;
import java.io.Serializable;

/**
 * Version of BIUTEE.
 * <BR>
 * Usage: <code>Version.getVersion().toString()</code>
 * <P>
 * Version is composed of "product", "major", "minor" and "build-type".
 * "product" = Main algorithms and methods. Roy's system is 1. The system described in
 * "A Confidence Model for Syntactically-Motivated Entailment Proofs" is 2.
 * "major" = Major additions and improvements.
 * "minor" - Minor additions and improvements
 * "build-type" - dev, alpha, beta, release
 * 
 * @author Asher Stern
 * @since Jul 19, 2011
 *
 */
public class Version implements AbstractVersion,Serializable
{
	private static final long serialVersionUID = -7334425936656293917L;
	
	public static Version getVersion(){return instance;}
	
	public Version(int product, int major, int minor, BuildType buildType)
	{
		super();
		this.product = product;
		this.major = major;
		this.minor = minor;
		this.buildType = buildType;
	}
	
	


	
	public int getProduct()
	{
		return product;
	}



	@Override
	public int getMajor()
	{
		return major;
	}



	@Override
	public int getMinor()
	{
		return minor;
	}



	@Override
	public BuildType getBuildType()
	{
		return buildType;
	}



	/**
	 * Like equals(), but ignores {@linkplain BuildType}.
	 * @param versionObject
	 * @return
	 */
	public boolean identicalTo(Version versionObject)
	{
		boolean ret = false;
		if (null==versionObject)ret=false;
		else
		{
			if (
					(this.getProduct()==versionObject.getProduct())
					&&
					(this.getMajor()==versionObject.getMajor())
					&&
					(this.getMinor()==versionObject.getMinor())
			)
				ret = true;
			else
				ret=false;
		}
		return ret;
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((buildType == null) ? 0 : buildType.hashCode());
		result = prime * result + major;
		result = prime * result + minor;
		result = prime * result + product;
		return result;
	}



	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Version other = (Version) obj;
		if (buildType != other.buildType)
			return false;
		if (major != other.major)
			return false;
		if (minor != other.minor)
			return false;
		if (product != other.product)
			return false;
		return true;
	}




	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("BIUTEE - version ");
		sb.append(getProduct());
		sb.append('.');
		sb.append(getMajor());
		sb.append('.');
		sb.append(getMinor());
		sb.append(" (");
		sb.append(getBuildType().name().toLowerCase());
		sb.append(")");
		
		return sb.toString();
	}






	// older versions:
	// 2.2.0 is RTE7
	// 2.3.0 is search paper submitted to ACL 2012. Also includes annotation rules support.
	// Plug-ins are introduced in 2.4.0
	// 2.4.1 - a development version between ACL 2012 to the migration into Excitement
	// 2.5.0 - Release 1.0.0 of Excitement
	// 2.6.0 - (planned) Excitement release of January 2014 
	private static final Version instance = new Version(
			2, // product
			6, 
			1, // minor  
			BuildType.DEV);
	
	
	private final int product;
	private final int major;
	private final int minor;
	private final BuildType buildType;
}
