package eu.excitementproject.eop.biutee.version;
import java.util.Comparator;

/**
 * Comparator of {@link Version} that ignores the {@link BuildType}.
 * @author Asher Stern
 * @since Apr 30, 2012
 *
 */
public class VersionComparator implements Comparator<Version>
{
	public int compare(Version o1, Version o2)
	{
		if (o1.getProduct()<o2.getProduct())
			return -1;
		else if (o1.getProduct()>o2.getProduct())
			return 1;
		else
		{
			if (o1.getMajor()<o2.getMajor())
				return -1;
			else if (o1.getMajor()>o2.getMajor())
				return 1;
			else
			{
				if (o1.getMinor()<o2.getMinor())
					return -1;
				else if (o1.getMinor()>o2.getMinor())
					return 1;
				else
					return 0;
			}
		}
	}

}
