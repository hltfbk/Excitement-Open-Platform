package eu.excitementproject.eop.biutee.utilities;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import eu.excitementproject.eop.common.utilities.Utils;

/**
 * 
 * @author Asher Stern
 * @since Feb 1, 2013
 *
 */
public class EclipseCP
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			Iterator<String> argsIterator = Utils.arrayToCollection(args, new LinkedList<String>()).iterator();
//			while (argsIterator.hasNext())
//			{
//				System.out.println(argsIterator.next());
//			}
//			System.out.println();
//			argsIterator = Utils.arrayToCollection(args, new LinkedList<String>()).iterator();

			new EclipseCP(
					argsIterator.next(),argsIterator.next(),argsIterator.next(),argsIterator.next(),argsIterator.next()
					).go();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	

	



	public EclipseCP(String delimiter, String prefixMavenRepository,
			String replacementPrefixMavenRepository, String javaHome,
			String replacementJavaHome)
	{
		super();
		this.delimiter = delimiter;
		this.prefixMavenRepository = prefixMavenRepository;
		this.replacementPrefixMavenRepository = replacementPrefixMavenRepository;
		this.javaHome = javaHome;
		this.replacementJavaHome = replacementJavaHome;
	}



	public void go()
	{
		String cp = System.getProperty("java.class.path");
		cp = cp.replaceAll(File.pathSeparator, delimiter);
		cp = cp.replaceAll(prefixMavenRepository, replacementPrefixMavenRepository);
		cp = cp.replaceAll(javaHome, replacementJavaHome);
		System.out.println(cp);
	}
	
	private String delimiter;
	private String prefixMavenRepository;
	private String replacementPrefixMavenRepository;
	private String javaHome;
	private String replacementJavaHome;
	
}
