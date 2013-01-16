package eu.excitementproject.eop.biutee.version;
import java.io.Serializable;

/**
 * Base class for {@link Version}
 * 
 * @see Version
 * @author Asher Stern
 * @since Dec 19, 2012
 *
 */
public interface AbstractVersion extends Serializable
{
	public int getProduct();
	public int getMajor();
	public int getMinor();
	public BuildType getBuildType();
}
