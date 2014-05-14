package eu.excitementproject.eop.common.utilities.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * This is a class that holds static implementations of java.io.FilenameFilter
 * 
 * @author Shachar Mirkin 2009 
 * 
 */
public class FileFilters {

	/**
	 * Accept all ".xml" files
	 */
	public static class XMLFileFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {

			if (name.toLowerCase().endsWith(".xml"))
				return true;
			return false;
		}
	}

	/**
	 * Accept all ".text" or ".txt" files
	 */
	public static class TextFileFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {

			if (name.toLowerCase().endsWith(".text")
					|| name.toLowerCase().endsWith(".txt"))
				return true;
			return false;
		}
	}

	/**
	 * Accept only files with a given (parameterized) suffix, set in
	 * ExtFileFilter()
	 */
	public static class ExtFileFilter implements FilenameFilter {

		private String m_ext = null;

		/**
		 * set the desired extension in the constructor
		 * 
		 * @param ext
		 */
		public ExtFileFilter(String ext) {
			m_ext = ext;
		}

		public boolean accept(File dir, String name) {

			if (m_ext == null)
				return false;

			if (name.toLowerCase().endsWith("." + m_ext))
				return true;
			return false;
		}
	}

	/**
	 * Accept only directories
	 * @deprecated Use {@link DirFileFilter} instead
	 */
	@Deprecated
	public static class DirFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {

			//File f = new File(dir + "/" + name); Bad hard-coded style: "/"
			File f = new File(dir + File.separator + name);
			if (!f.isDirectory())
				return false;

			return true;
		}
	}
	
	/**
	 * Implements java.io.FileFilter that accepts only directories
	 * @author Asher Stern
	 * @since Dec 2, 2010
	 *
	 */
	public static class DirFileFilter implements FileFilter
	{
		public boolean accept(File pathname)
		{
			return pathname.isDirectory();
		}
	}
}
