package eu.excitementproject.eop.distsim.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains various file utilities
 * 
 * @author Meni Adler
 * @since 28/12/2012
 *
 */
public class FileUtils {
	
	/**
	 * Gets all files under a given root
	 * 
	 * @param root a root directory/file
	 * @return all files under the given root
	 */
	public static Set<File> getFiles(File root) {
		Set<File> ret = new HashSet<File>();
		getFiles(root,ret);
		return ret;
	}
	
	private static void getFiles(File file, Collection<File> ret) {
		if (file.isDirectory()) {
			for (File f : file.listFiles())
				getFiles(f,ret);
		} else
			ret.add(file);
	}
	
	/**
	 * Gets all directories for a given root (including nested sub-directories)
	 * In case, the given root is a file, returns the root
	 * 
	 * @param root a root directory/file
	 * @return all files under the given root
	 */
	public static Set<File> getDirs(File root) {
		Set<File> ret = new HashSet<File>();
		
		if (root.isFile())
			ret.add(root);
		else
			getDirs(root,ret);
		return ret;
	}
	
	private static void getDirs(File file, Collection<File> ret) {
		if (file.isDirectory()) {
			ret.add(file);
			for (File f : file.listFiles())
				getDirs(f,ret);
		} 
	}
	
	/**
	 * Splits a given file to a given number of files
	 * 
	 * @param file a given file to be splitted
	 * @param splits the required number of splits
	 * @throws IOException
	 */
	public static void splitFile(File file,int splits) throws IOException {
		if (file.isDirectory())
			return;
		else {
			
			// count the number of lines in the file
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line=null;
			int totalLineNum=0;
			while ((line = reader.readLine())!=null)
				totalLineNum++;
			reader.close();
			
			// create a new directory for the splits
			String dir = (file.getParent() == null ? "" : file.getParent() + "/") + file.getName().substring(0,file.getName().lastIndexOf("."));
			System.out.println(file.getParent() == null);
			System.out.println(file.getParent());
			System.out.println(dir);
			
			new File(dir).mkdir();
			int splitLinesNum = totalLineNum / splits;
			
			// split the given file to files
			reader = new BufferedReader(new FileReader(file));
			line=null;
			int f=1;
			PrintWriter writer = new PrintWriter(new FileWriter(new File(dir,f + ".txt")));;
			int i=0;
			while ((line = reader.readLine())!=null) {
				if (i == splitLinesNum) {
					if (writer != null)
						writer.close();
					writer = new PrintWriter(new FileWriter(new File(dir,f + ".txt")));
					i = 0;
					f++;
				}
				writer.println(line);
				i++;
			}
			if (writer != null)
				writer.close();
		}
	}
	
	public static void main(String[] args ) throws IOException {
		FileUtils.splitFile(new File(args[0]), Integer.parseInt(args[1]));
	}
}
