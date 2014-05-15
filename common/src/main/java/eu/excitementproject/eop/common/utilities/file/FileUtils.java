package eu.excitementproject.eop.common.utilities.file;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import eu.excitementproject.eop.common.utilities.OS;


/**
 * This class holds various useful static methods for file-related utilities
 * @author Shachar Mirkin
 *
 */
public class FileUtils {

	public static final String WORKING_DIRECTORY_PROPERTY_NAME = "user.dir";
	
	/**
	 * Append the contents of source to target 
	 * 
	 * @param source
	 * @param target
	 * @throws IOException
	 */
	public static void append(File source, File target) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(source));
		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(target,true));
			try
			{
				String line;
				while((line=br.readLine())!=null)
					pw.println(line);
			}
			finally
			{
				pw.close();
			}
		}
		finally
		{
			br.close();
		}
	}
	
	/**
	 * Append a bunch of String to a new File, line by line. If necessary, the file is created.
	 * 
	 * @param lines
	 * @param outFile
	 * @throws IOException
	 */
	public static void appendLinesToFile(Iterable<String> lines, File outFile) throws IOException
	{
		if (outFile == null || lines == null)
			throw new IOException("Got null args");
		
		outFile.createNewFile();
		PrintWriter pw = new PrintWriter(new FileOutputStream(outFile,true));
		try
		{
			for( String line : lines)
				pw.println(line);
		}
		finally
		{
			pw.close();
		}
	}
	
	/**
	 * Write a bunch of Strings to a file, overwriting anything that was on it
	 * 
	 * @param file
	 * @param lines
	 * @throws IOException
	 */
	public static void writeFile(File file, Iterable<String> lines) throws IOException
	{
		if (file == null)
			throw new IOException("got null file");
		if (file.isDirectory())
			throw new IOException(file + " is a directory");
		
		file.createNewFile();
		PrintWriter pw = new PrintWriter(file,"UTF8");
		try
		{
			for( String line : lines)
				pw.println(line);
		}
		finally
		{
			pw.close();
		}
	}
	
	/**
	 * overwrite the given file with the given contents. 
	 * @param file
	 * @param contents
	 * @throws IOException
	 */
	public static void writeFile(File file, String contents) throws IOException {
		if (file == null)
			throw new IOException("got null file");
		if (file.isDirectory())
			throw new IOException(file + " is a directory");

		FileWriter writer = new FileWriter(file);
		writer.write(contents);
		writer.close();
	}

	/**
	 * Delete a non-empty directory
	 * Copied from http://www.rgagnon.com/javadetails/java-0483.html
	 * @param path
	 * @return true if and only if the file or directory is successfully deleted; false otherwise
	 */
	public static boolean deleteDirectory(File path) 
	{
		boolean succeeded = true;
		
		// first empty it
		if (path.exists()) 
		{
			File[] files = path.listFiles();
			
			if (files != null)
			{
				for(int i = 0; succeeded && (i < files.length); i++)
				{
					if (files[i].isDirectory()) {
						succeeded &= deleteDirectory(files[i]);
					} else {
						succeeded &= files[i].delete();
					}
				}
			}
		}
		// then delete it
		if (succeeded)
			succeeded &= path.delete();
		return succeeded;
	}
	
	/**
	 * Screen out all recurrences of all lines in the given file
	 * The lines are screened through a HashSet
	 * @param ioFile
	 * @throws IOException
	 */
	public static void ridIdenticalLines(File ioFile) throws IOException {
		
		HashSet<String> linesHash = new HashSet<String>();
		BufferedReader ruleReader = new BufferedReader(new FileReader(ioFile));
		
		// Read all lines into the linesHash 
		String line;
		while((line=ruleReader.readLine())!=null)
			linesHash.add(line);
		
		ruleReader.close();
		
		// write all the distinct lines back to the file
		PrintWriter ruleWriter = new PrintWriter(new FileOutputStream(ioFile));
		for(String rule: linesHash)
			ruleWriter.println(rule);
		ruleWriter.close();
	}
	
	/**
	 * Reads text from an open reader into a list of strings, each containing one line of the file.
	 */
	public static List<String> loadReaderToList(Reader reader) throws IOException {
		List<String> outList = new LinkedList<String>();
		String line;
		BufferedReader bufferedReader = new BufferedReader(reader);
		while ((line = bufferedReader.readLine()) != null) 
			outList.add(line);
		return outList;
	}
	
	/**
	 * Reads text from a local file into a list of strings, each containing one line of the file.
	 */
	public static List<String> loadFileToList(File iFile) throws IOException {
		return loadReaderToList(new FileReader(iFile));
	}
	
	/**
	 * Reads text from a remote URL into a list of strings, each containing one line of the file.
	 */
	public static List<String> loadUrlToList(URL iUrl) throws IOException {
		return loadReaderToList(new InputStreamReader(iUrl.openStream()));
	}
	
	/**
	 * Reads text from EITHER a remote URL (that starts with http://) OR a local file (doesn't start with http://), into a list of strings, each containing one line of the file.
	 */
	public static List<String> loadFileOrUrlToList(String iFileOrUrl) throws IOException {
		return iFileOrUrl.startsWith("http://")?
				loadUrlToList(new URL(iFileOrUrl)):
				loadFileToList(new File(iFileOrUrl));
	}

	/**
	 * Read a file into a String
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String loadFileToString(File file) throws IOException 
	{
		return loadFileToString(file.getAbsolutePath());
	}
	
	/**
	 * Read a file into a String
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String loadFileToString(String fileName) throws IOException {

		FileInputStream fis = new FileInputStream(fileName);
		try
		{
			int x= fis.available();
			byte b[]= new byte[x];
			fis.read(b);
			String content = new String(b);
			return content;
		}
		finally
		{
			fis.close();
		}
	}

	/**
	 * Get the package name of the object's class 
	 * @param o 
	 * @return
	 */
	public String getPackegeName(Object o) {
		return o.getClass().getPackage().getName();
	}

	/**
	 * Copies the sourceFile to the destinationFile
	 * @param sourceFile
	 * @param destinationFile
	 * @throws IOException
	 * @throws InterruptedException
	 * @author Asher Stern
	 */
	public static void copyFile(File sourceFile, File destinationFile) throws IOException, InterruptedException
	{
		if (OS.isUnix())
		{
			if (!sourceFile.exists()) throw new FileNotFoundException("source file: "+sourceFile.getPath()+" does not exist.");
			if (!sourceFile.isFile()) throw new FileNotFoundException("source file: "+sourceFile.getPath()+" is not a file.");
			String[] command = new String[]{"cp",sourceFile.getPath(),destinationFile.getPath()};
			Process process =  Runtime.getRuntime().exec(command);
			process.waitFor();
			if (!destinationFile.exists()) throw new FileNotFoundException("Copy failed. destination file: "+destinationFile.getPath()+" was not created.");
			if (!destinationFile.isFile()) throw new FileNotFoundException("Copy failed. destination file: "+destinationFile.getPath()+" was not created.");
		}
		else // from : http://www.rgagnon.com/javadetails/java-0064.html
		{
			FileInputStream fis  = new FileInputStream(sourceFile);
			FileOutputStream fos = new FileOutputStream(destinationFile);
			try
			{
				byte[] buf = new byte[BUF_SZ];
				int i = 0;
				while ((i = fis.read(buf)) != -1) {
					fos.write(buf, 0, i);
				}
			} 
			finally
			{
				if (fis != null) fis.close();
				if (fos != null) fos.close();
			}
		}
	}
	
	
	@SuppressWarnings("serial")public static class CopyDirectoryException extends Exception{CopyDirectoryException(String message){super(message);}}
	/**
	 * Copies recursively the source directory to the destination directory.
	 * Both directories must exist. 
	 * @param sourceDirectory
	 * @param destinationDirectory
	 * @param includeHiddenFiles
	 * @throws IOException
	 * @throws InterruptedException
	 * 
	 * @author Asher Stern
	 * @throws CopyDirectoryException 
	 */
	public static void copyDirectory(File sourceDirectory, File destinationDirectory, boolean includeHiddenFiles) throws IOException, InterruptedException, CopyDirectoryException
	{
		if (sourceDirectory.exists() && sourceDirectory.isDirectory()){}else throw new CopyDirectoryException("source directory : "+sourceDirectory.getPath()+" does not exist.");
		if (destinationDirectory.exists() && destinationDirectory.isDirectory()){}else throw new CopyDirectoryException("destination directory : "+destinationDirectory.getPath()+" does not exist.");
		File[] files = sourceDirectory.listFiles();
		for (File file : files)
		{
			File destFile = new File(destinationDirectory,file.getName());
			if (file.isFile())
			{
				if ( (!file.isHidden()) || includeHiddenFiles)
					copyFile(file, destFile);
			}
			else if (file.isDirectory())
			{
				if (!destFile.mkdir()) throw new CopyDirectoryException("Could not create directory: "+destFile.getPath());
				copyDirectory(file, destFile, includeHiddenFiles);
			}
		}
	}

	/**
	 * Returns a string that represents the path of the given file.
	 * Used as workaround to Windows abnormal behavior of JDK.
	 * @param file
	 * @return
	 */
	public static String filePathToString(File file)
	{
		String ret = file.getAbsolutePath();
		if (OS.isWindows())
		{
			ret = ret.replaceAll("\\\\", "\\\\\\\\");
		}
		return ret;
	}

	/**
	 * Returns the current working directory (".").
	 * @return
	 */
	public static File getWorkingDirectory()
	{
		return new File(System.getProperty(WORKING_DIRECTORY_PROPERTY_NAME));
	}

	/**
	 * Return the file's short name, without the extension
	 * @param file
	 * @return
	 */
	public static String getNameWithoutExtension(File file) {

		return  nameWithoutExtension(file.getName());
	}

	/**
	 * Return the file's full name and path, without the file extension
	 * @param file
	 * @return
	 */
	public static String getPathWithoutExtension(File file) {

		return nameWithoutExtension(file.getPath());
	}
	
	/**
	 * Normalizes the backslash and slash in the file name according to the operating system.
	 * @param filename
	 * @return
	 */
	public static String normalizeFileNameByOS(String filename)
	{
		char incompatibleChar;
		char compatibleChar;
		if (OS.isWindows())
		{
			incompatibleChar = '/';
			compatibleChar = File.separatorChar;
		}
		else
		{
			incompatibleChar = '\\';
			compatibleChar = File.separatorChar;
		}
		
		char[] charArray = filename.toCharArray();
		for (int index=0;index<charArray.length;++index)
		{
			if (charArray[index]==incompatibleChar)
				charArray[index] = compatibleChar;
		}
		return new String(charArray);
	}
	
	/**
	 * Normalizes the backslash and slash in the file name according to the operating system.
	 * @param file
	 * @return
	 */
	public static File normalizeFileNameByOS(File file)
	{
		return new File(normalizeFileNameByOS(file.getPath()));
	}
	
	/**
	 * Normalizes a cygwin path (e.g. "/cygdrive/d/Jars/") to Windows path (e.g. "D:\Jars")
	 * Does nothing if the OS is not windows, or if the path is not cygwin path.
	 * 
	 * @param path
	 * @return
	 */
	public static String normalizeCygwinPathToWindowsPath(String path)
	{
		String ret = path;
		if (OS.isWindows()){if (path.startsWith(CYGWIN_CYGDRIVE_PREFIX))
		{
			char driveLetter = path.substring(CYGWIN_CYGDRIVE_PREFIX.length()).charAt(1);
			String pathAfterDriveLetter = path.substring(CYGWIN_CYGDRIVE_PREFIX.length()+1+1);
			StringBuffer sb = new StringBuffer();
			sb.append(Character.toUpperCase(driveLetter));
			sb.append(':');
			sb.append(normalizeFileNameByOS(pathAfterDriveLetter));
			ret = sb.toString();
		}}

		return ret;
	}
	
	/**
	 * Quickly counts number of lines in file
	 * @param file 
	 * @return number of lines in the file
	 * @throws IOException
	 */
	public static int countNumOfLines(File file) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(file));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        while ((readChars = is.read(c)) != -1) {
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n')
	                    ++count;
	            }
	        }
	        return count;
	    } finally {
	        is.close();
	    }
	}
	
	/**
	 * write a file where the lines are the permutation of the input file
	 * @param inFile
	 * @param outFile
	 * @throws IOException
	 */
	public static void permuteFile(File inFile, File outFile) throws IOException {
		
		//upload lines of input file to list
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		List<String> lines = new ArrayList<String>();
		String line;
		while((line=reader.readLine())!=null) 
			lines.add(line);
		reader.close();
		
		//permute list
		
		List<String> permutedLines = permuteList(lines);
		
		//write permuted files
		PrintWriter writer = new PrintWriter(new FileOutputStream(outFile));
		for(String permutedLine: permutedLines)
			writer.println(permutedLine);
		writer.close();
	}
	
	/**
	 * returns a list that is a permutation of the lines of a file
	 * @param inFile
	 * @return list with permuted lines
	 * @throws IOException
	 */
	public static List<String> permuteFile(File inFile) throws IOException {
		
		//upload lines of input file to list
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		List<String> lines = new ArrayList<String>();
		String line;
		while((line=reader.readLine())!=null) 
			lines.add(line);
		reader.close();
		
		//permute list		
		return permuteList(lines);
	}
	
	/**
	 * returns the file extension, or empty-string of the file has no extension.
	 * @param file
	 * @return
	 */
	public static String getFileExtension(File file)
	{
		String filePath = file.getPath();
		int index = filePath.lastIndexOf(DOT);
		if (index>=0)
			return filePath.substring(index+1);
		else
			return "";
	}
	
	
	
	

	/////////////////////////////////// private section /////////////////////////////////////////////

	private static List<String> permuteList(List<String> inList) {
		
		//permute list
		List<String> result = new ArrayList<String>();
		Random rand = new Random(System.currentTimeMillis());
		
		int count=0;
		int currIndex = inList.size()-1;
		for( ; count < inList.size() ;count++) {
			
			int randPosition = rand.nextInt(currIndex+1);
			String currLine = inList.get(randPosition);
			result.add(currLine);
			
			inList.set(randPosition, inList.get(currIndex));
			inList.set(currIndex, currLine);			
			currIndex--;	
		}
		return result;
	}

	
	/**
	 * Return a file's name without the extension
	 * @param name
	 * @return
	 */
	private static String nameWithoutExtension(String name) {
		return name.substring(0, name.lastIndexOf(DOT) - 1);
	}
	
	private static final String DOT = ".";
	private static final int BUF_SZ = 1024;
	private static final String CYGWIN_CYGDRIVE_PREFIX = "/cygdrive";
}
