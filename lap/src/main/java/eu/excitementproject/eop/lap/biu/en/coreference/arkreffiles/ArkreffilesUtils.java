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
import java.util.regex.Pattern;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;

/**
 * 
 * @author Asher Stern
 * @since Dec 9, 2013
 *
 */
public class ArkreffilesUtils
{

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
	
	public static void writeTextToFile(String text, File file) throws FileNotFoundException
	{
		try(PrintWriter writer = new PrintWriter(file))
		{
			writer.println(text);
		}
	}
	

	
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
			if (exitValue!=0) {throw new CoreferenceResolutionException("ArkRef exited with non zero exit code: "+exitValue);}
		}
		catch (IOException | InterruptedException e)
		{
			throw new CoreferenceResolutionException("Failed to run ArkRef. See nested exception.",e);
		}
	}
	
	
	
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
	
	public static String extractJarFromClassPath(String jarname)
	{
		final Pattern jarPattern = Pattern.compile(".*"+jarname+"[^"+File.separatorChar+"]*\\.jar");
		String classpath = System.getProperty("java.class.path");
		String[] classpathComponents = classpath.split(File.pathSeparator);
		String jarComponent = null;
		for (String component : classpathComponents)
		{
			if (jarPattern.matcher(component).matches())
			{
				if (null==jarComponent)
				{
					jarComponent = component;
					break;
				}
			}
		}
		return jarComponent;
	}

	
	
	
	private static File getJavaExecutable()
	{
		String javaHome = System.getProperty("java.home");
		File javaHomeDir = new File(javaHome);
		return new File(new File(javaHomeDir,"bin"),"java");
	}
	
	private static String buildArkrefClassPathString() throws CoreferenceResolutionException
	{
		// TODO too many hard-coded strings
		final String LIB_DIR = "lib";
		final String ARKREF_JAR = "arkref";
		
		String arkrefJarComponent = extractJarFromClassPath(ARKREF_JAR);
		if (null==arkrefJarComponent) {throw new CoreferenceResolutionException(ARKREF_JAR+" could not be found in the classpath.");}
		
		String pathSeparator = System.getProperty("path.separator");
		StringBuilder sb = new StringBuilder();
		if (!(new File(arkrefJarComponent).exists())) {throw new CoreferenceResolutionException(ARKREF_JAR+" could not be found in the file system. Tried to detect "+arkrefJarComponent);}
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
}
