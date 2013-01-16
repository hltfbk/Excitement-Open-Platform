package eu.excitementproject.eop.common.utilities.log4j;

import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;

/**
 * 
 * @author Asher Stern
 * @since Dec 23, 2011
 *
 */
public class LoggerUtilities
{
	public static Set<Appender> getAllAppendersIncludingParents(Logger logger)
	{
		Set<Appender> ret = new LinkedHashSet<Appender>();
		for (Category category = logger; category != null; category = category.getParent())
		{
			Enumeration<?> enumeration = category.getAllAppenders();
			while (enumeration.hasMoreElements())
			{
				// I have to use RTTI, since current version of log4j provides no
				// other alternative.
				Appender appender = (Appender) enumeration.nextElement();
				ret.add(appender);
			}
		}
		return ret;
	}

}
