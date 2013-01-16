package eu.excitementproject.eop.biutee.utilities.safemodel;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.version.Version;


/**
 * 
 * @author Asher Stern
 * @since Dec 19, 2012
 *
 */
public class VersionAdapter extends XmlAdapter<AdaptedVersion, Version>
{

	@Override
	public Version unmarshal(AdaptedVersion v) throws Exception
	{
		logger.debug("VersionAdapter unmarshal");
		return new Version(v.getProduct(), v.getMajor(), v.getMinor(), v.getBuildType());
	}

	@Override
	public AdaptedVersion marshal(Version v) throws Exception
	{
		logger.debug("VersionAdapter marshal");
		return new AdaptedVersion(v.getProduct(), v.getMajor(), v.getMinor(), v.getBuildType());
	}
	
	private static final Logger logger = Logger.getLogger(VersionAdapter.class);

}
