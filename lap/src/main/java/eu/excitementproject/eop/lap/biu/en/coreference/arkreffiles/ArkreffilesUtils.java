package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.utilities.OS;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;

/**
 * A collections of utilities used by classes in this package.
 * 
 * @author Asher Stern
 * @since Dec 9, 2013
 *
 */
public class ArkreffilesUtils
{

	/**
	 * Creates a map from every parse-tree-node to an integer value which indicates
	 * its depth in the parse-tree.
	 * The root is of depth 0. Direct children of root are of depth 1. Their direct
	 * children are depth 2. etc.
	 * 
	 * @param tree A parse-tree
	 * @return A map from each node to its depth, where the root is depth 0.
	 */
	public static <I extends Info, S extends AbstractNode<I, S>>
	Map<S, Integer> mapNodesToDepth(S tree)
	{
		Map<S, Integer> ret = new LinkedHashMap<>();
		
		List<S> nextLevel = new LinkedList<>();
		nextLevel.add(tree);
		
		int depth = 0;
		while (!nextLevel.isEmpty())
		{
			List<S> currentLevel = nextLevel;
			nextLevel = new LinkedList<>();
			
			for (S node : currentLevel)
			{
				ret.put(node, depth);
				if (node.hasChildren())
				{
					nextLevel.addAll(node.getChildren());
				}
			}
			++depth;
		}
		
		return ret;
	}
	
	/**
	 * Creates a temporary directory, which would be a sub-directory of
	 * the system "temp" directory.
	 * 
	 * @param prefix A prefix string of name of the to-be created directory.  
	 * @return The directory.
	 * @throws CoreferenceResolutionException
	 * @throws IOException
	 */
	public static File createTempDirectory(String prefix) throws CoreferenceResolutionException, IOException
	{
		final File temp = File.createTempFile(prefix+"_"+UUID.randomUUID().toString(),"");

		if(!(temp.delete()))
		{
			throw new CoreferenceResolutionException("Could not delete temp file: " + temp.getAbsolutePath());
		}

		if(!(temp.mkdir()))
		{
			throw new CoreferenceResolutionException("Could not create temp directory: " + temp.getAbsolutePath());
		}

		return (temp);
	}
	
	/**
	 * Writes the given text into the given file. Overrides the file contents if
	 * it is already exists.
	 * @param text A given text.
	 * @param file A given file.
	 * @throws FileNotFoundException If writing into the file is not
	 * possible, due to any reason.
	 */
	public static void writeTextToFile(String text, File file) throws FileNotFoundException
	{
		try(PrintWriter writer = new PrintWriter(file))
		{
			writer.println(text);
		}
	}
	

	/**
	 * Run the ArkRef co-reference resolver as a separate process, where the
	 * input is the given file.
	 * @param textFile
	 * @throws CoreferenceResolutionException
	 */
	public static void runArkref(File textFile) throws CoreferenceResolutionException
	{
		File javaExec = getJavaExecutable();
		String classpath = buildArkrefClassPathString();
		String[] commandArray = new String[]{javaExec.getPath(),"-mx1g","-ea", "-cp", classpath, "arkref.analysis.ARKref", "-input", textFile.getPath()};
//		for (String str : commandArray)
//		{
//			System.out.print(str+" ");
//		}
//		System.out.println();
//		System.exit(0);
		ProcessBuilder builder = new ProcessBuilder(Arrays.asList(commandArray));
		
		try
		{
			Process process = builder.start();
			process.waitFor();
			int exitValue = process.exitValue();
			if (exitValue!=0)
			{
				String command = StringUtil.joinIterableToString(builder.command(), " ", true);
				throw new CoreferenceResolutionException("ArkRef exited with non zero exit code: "+exitValue+". Command was: "+command);
			}
		}
		catch (IOException | InterruptedException e)
		{
			throw new CoreferenceResolutionException("Failed to run ArkRef. See nested exception.",e);
		}
	}
	
	
	/**
	 * Reads the contents of the given file.
	 * @param filename The name of the given file.
	 * @return The file's contents.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readTextFile(String filename) throws FileNotFoundException, IOException
	{
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(filename))))
		{
			String line = reader.readLine();
			while (line != null)
			{
				sb.append(line);
				line = reader.readLine();
				if (line != null) {sb.append("\n");}
			}
		}
		String text = sb.toString();
		return text;
	}
	
	
	/**
	 * Returns the "java" program, which is currently running.
	 * It should be $JAVA_HOME/bin/java (or %JAVA_HOME%\bin\java.exe), where
	 * the JAVA_HOME is the home of the JDK or JRE.
	 * @return
	 */
	private static File getJavaExecutable()
	{
		String javaHome = System.getProperty("java.home");
		File javaHomeDir = new File(javaHome);
		return new File(new File(javaHomeDir,"bin"),OS.programName("java"));
	}


	
	/**
	 * Returns the path+name of a JAR file which should be in the current
	 * process class-path.
	 * @param clazz A class which should be contained in jar in the class-path.
	 * @return
	 */
	private static String extractJarFromClassPath(Class<?> clazz)
	{
		return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
//		
//		
//		final Pattern jarPattern = Pattern.compile(".*"+jarname+"[^"+fileSeparatorForRegExp()+"]*\\.jar");
//		String classpath = System.getProperty("java.class.path");
//		String[] classpathComponents = classpath.split(File.pathSeparator);
//		String jarComponent = null;
//		for (String component : classpathComponents)
//		{
//			if (jarPattern.matcher(component).matches())
//			{
//				if (null==jarComponent)
//				{
//					jarComponent = component;
//					break;
//				}
//			}
//		}
//		return jarComponent;
	}
	
	
	private static String buildArkrefClassPathString() throws CoreferenceResolutionException
	{
		// TODO too many hard-coded strings
		final String LIB_DIR = "lib";
		final Class<?> arkrefClass = arkref.analysis.ARKref.class;
		
		String arkrefJarComponent = extractJarFromClassPath(arkrefClass);
		if (null==arkrefJarComponent) {throw new CoreferenceResolutionException("arkref jar-file could not be found in the classpath.");}
		
		String pathSeparator = System.getProperty("path.separator");
		StringBuilder sb = new StringBuilder();
		if (!(new File(arkrefJarComponent).exists())) {throw new CoreferenceResolutionException("arkref jar-file could not be found in the file system. Tried to detect "+arkrefJarComponent);}
		sb.append(arkrefJarComponent);
		File libDir = new File(LIB_DIR);
		if (!libDir.exists()){throw new CoreferenceResolutionException("Cannot run ArkRef! Directory \"lib\" does not exist in the current working directory.");}
		if (!libDir.isDirectory()){throw new CoreferenceResolutionException("Cannot run ArkRef! Directory \"lib\" does not exist in the current working directory.");}
		
		for (File jarFile : libDir.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				return pathname.getPath().endsWith(".jar");
			}
		}))
		{
			sb.append(pathSeparator);
			sb.append(jarFile.getPath());
		}
		return sb.toString();
	}
	
//	private static final String fileSeparatorForRegExp()
//	{
//		if (File.separatorChar=='\\')
//		{
//			return "\\\\";
//			
//		}
//		else
//		{
//			return File.separator;
//		}
//	}

}
