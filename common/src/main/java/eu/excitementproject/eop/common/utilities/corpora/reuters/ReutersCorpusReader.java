package eu.excitementproject.eop.common.utilities.corpora.reuters;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.corpora.CorporaException;
import eu.excitementproject.eop.common.utilities.corpora.CorpusDocumentEntity;
import eu.excitementproject.eop.common.utilities.corpora.CorpusReader;


/**
 * A {@link CorpusReader} for Reuters corpus. The Reuters corpus is organized in
 * two directories, named "CD1" and "CD2", each of these directories has
 * many sub-directories, and each of these sub-directories has many XML files.
 * The {@link ReutersDocumentReader} knows how to read these XML files.
 * 
 * @author Asher Stern
 * @since Oct 18, 2012
 *
 */
public class ReutersCorpusReader implements CorpusReader<ReutersDocumentReader>
{
	public ReutersCorpusReader(File reutersDirectory)
	{
		super();
		this.reutersDirectory = reutersDirectory;
	}
	
	public ReutersCorpusReader(File[] reuters_CD_Directories)
	{
		super();
		this.reuters_CD_Directories = reuters_CD_Directories;
	}

	
	
	public Iterator<CorpusDocumentEntity<ReutersDocumentReader>> iterator() throws CorporaException
	{
		return new ReutersCorpusReaderIterator();
	}
	
	
	
	////////////////// PRIVATE NESTED CLASS //////////////////
	
	private class ReutersCorpusReaderIterator implements Iterator<CorpusDocumentEntity<ReutersDocumentReader>>
	{
		public ReutersCorpusReaderIterator() throws CorporaException
		{
			if (ReutersCorpusReader.this.reutersDirectory!=null)
			{
				if (!reutersDirectory.exists()) throw new CorporaException(reutersDirectory.getPath()+" does not exist.");
				if (!reutersDirectory.isDirectory()) throw new CorporaException(reutersDirectory.getPath()+" is not a directory.");
				subdirs = new File[REUTERS_SUB_DIRECTORIES.size()];
				for (int index=0;index<REUTERS_SUB_DIRECTORIES.size();++index)
				{
					subdirs[index] = new File(reutersDirectory,REUTERS_SUB_DIRECTORIES.get(index));
				}
			}
			else if (ReutersCorpusReader.this.reuters_CD_Directories != null)
			{
				subdirs = reuters_CD_Directories;
			}
			else
			{
				throw new CorporaException("Bug: nothing given for Reuters path (I don\'t know where the corpus exists).");
			}
			for (File subdir : subdirs)
			{
				if (!subdir.isDirectory()) throw new CorporaException("Bad input. \""+subdir.getAbsolutePath()+"\" is not a directory.");
			}
			currentSubdirIndex = 0;
			buildSubdirDirs();
			buildSubsubdirFiles();
		}

		@Override
		public synchronized boolean hasNext()
		{
			return itHasNext;
		}

		@Override
		public synchronized CorpusDocumentEntity<ReutersDocumentReader> next()
		{
			if (!itHasNext) throw new NoSuchElementException();
			// take the file
			File currentFile = currentSubsubdirFiles[currentSubsubdirFilesIndex];
			
			// Construct the returned object
			ReutersDocumentReader reader = new ReutersDocumentReader(currentFile);
			List<String> paths = new ArrayList<String>(1+1+1);
			paths.add(subdirs[currentSubdirIndex].getName());
			paths.add(currentSubdirDirs[currentSubdirDirsIndex].getName());
			paths.add(currentSubsubdirFiles[currentSubsubdirFilesIndex].getName());
			
			CorpusDocumentEntity<ReutersDocumentReader> ret = new
					CorpusDocumentEntity<ReutersDocumentReader>(
							reader,
							paths,
							StringUtil.join(paths, File.separator) // just a description
							);
			
			// prepare the subsequent calls for next() and hasNext()
			goAhead();

			// and return
			return ret;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
		private void buildSubdirDirs()
		{
			File subdir = subdirs[currentSubdirIndex];
			currentSubdirDirs = subdir.listFiles(new FileFilter()
			{
				public boolean accept(File pathname)
				{
					boolean ret = false;
					if (pathname.isDirectory())
					{
						if (StringUtil.stringOnlyDigits(pathname.getName()))
						{
							ret = true;
						}
					}
					return ret;
				}
			});
			currentSubdirDirsIndex = 0;
		}
		
		private void buildSubsubdirFiles()
		{
			File subsubdir = currentSubdirDirs[currentSubdirDirsIndex];
			currentSubsubdirFiles = subsubdir.listFiles(new FileFilter()
			{
				public boolean accept(File pathname)
				{
					if (pathname.isFile())
					{
						if (pathname.getName().endsWith("xml"))
						{
							return true;
						}
					}
					return false;
				}
			});
			currentSubsubdirFilesIndex = 0;
		}
		
		private synchronized void goAhead()
		{
			if (currentSubsubdirFilesIndex<(currentSubsubdirFiles.length-1))
			{
				++currentSubsubdirFilesIndex;
			}
			else
			{
				if (currentSubdirDirsIndex<(currentSubdirDirs.length-1))
				{
					++currentSubdirDirsIndex;
					buildSubsubdirFiles();
				}
				else
				{
					if (currentSubdirIndex<(subdirs.length-1))
					{
						++currentSubdirIndex;
						buildSubdirDirs();
						buildSubsubdirFiles();
					}
					else
					{
						itHasNext = false;
					}
				}
			}
		}
		
		
		private File[] subdirs;
		private int currentSubdirIndex;
		private File[] currentSubdirDirs;
		private int currentSubdirDirsIndex;
		private File[] currentSubsubdirFiles;
		private int currentSubsubdirFilesIndex;
		
		private boolean itHasNext = true;
		
	}

	private static final ArrayList<String> REUTERS_SUB_DIRECTORIES = Utils.arrayToCollection(new String[]{"CD1","CD2"}, new ArrayList<String>());
	private File reutersDirectory = null;
	private File[] reuters_CD_Directories = null;

}
