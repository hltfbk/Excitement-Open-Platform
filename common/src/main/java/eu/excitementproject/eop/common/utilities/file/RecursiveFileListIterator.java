package eu.excitementproject.eop.common.utilities.file;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import eu.excitementproject.eop.common.datastructures.IteratorByMultipleIterables;


/**
 * taken from http://snippets.dzone.com/posts/show/3532
 * 
 * Iterates over all non-directory files under a given subdirectory
 * 
 * @author Jonathan Bernat
 */
public class RecursiveFileListIterator implements Iterator<File> {
	/**
	 * Constructor
	 * @param dir
	 * @param filter
	 * @throws Exception if Null directory given
	 */
	public RecursiveFileListIterator(File dir, FileFilter filter)
			throws InstantiationException {
		if (dir == null)
			throw new InstantiationException("Null directory given");

		this.iteratorByMultipleIterables = new IteratorByMultipleIterables<File>(
				createIteratorOfIteratosOfFiles(dir, filter, false));
	}

	/**
	 * Constructor
	 * @param dir
	 * @param sort whether files and folders should be sorted by name (order not guaranteed otherwise)
	 * @param filter
	 * @throws InstantiationException
	 * @author Ofer Bronstein
	 * @since 20.08.2012 
	 */
	public RecursiveFileListIterator(File dir, boolean sort, FileFilter filter)
			throws InstantiationException {
		if (dir == null)
			throw new InstantiationException("Null directory given");

		this.iteratorByMultipleIterables = new IteratorByMultipleIterables<File>(
				createIteratorOfIteratosOfFiles(dir, filter, sort));
	}

	/**
	 * Constructor, accepting a list of folders instead of a single one
	 * @param dirs
	 * @param filter
	 * @throws InstantiationException
	 */
	public RecursiveFileListIterator(File[] dirs, FileFilter filter)
			throws InstantiationException {
		if (dirs == null)
			throw new InstantiationException("Null directory given");

		this.iteratorByMultipleIterables = new IteratorByMultipleIterables<File>(
				createIteratorOfIteratosOfFiles(dirs, filter, false));
	}

	/**
	 * Constructor, accepting a list of folders instead of a single one
	 * @param dir
	 * @param sort whether files and folders should be sorted by name (order not guaranteed otherwise)
	 * @param filter
	 * @throws InstantiationException
	 * @author Ofer Bronstein
	 * @since 20.08.2012
	 */
	public RecursiveFileListIterator(File[] dirs, boolean sort, FileFilter filter)
			throws InstantiationException {
		if (dirs == null)
			throw new InstantiationException("Null directory given");

		this.iteratorByMultipleIterables = new IteratorByMultipleIterables<File>(
				createIteratorOfIteratosOfFiles(dirs, filter, sort));
	}

	/**
	 * Constructor
	 * @param dir
	 * @throws Exception if Null directory given
	 */
	public RecursiveFileListIterator(File dir) throws InstantiationException {
		this(dir, null);
	}
	
	/**
	 * Constructor, accepting a list of folders instead of a single one
	 * @param dirs
	 * @throws InstantiationException
	 */
	public RecursiveFileListIterator(File[] dirs) throws InstantiationException {
		this(dirs, null);
	}

	/** (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/** (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return this.iteratorByMultipleIterables.hasNext();
	}

	/** (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public File next() {
		return (File) iteratorByMultipleIterables.next();
	}

	//////////////////////////////////////////////////////////////// private ////////////////////////////////////////////////////////////////

	/**
	 * @param dir
	 * @param filter
	 * @return
	 * @author Amnon Lotan
	 * @since Dec 8, 2010
	 */
	private static Iterable<? extends Iterable<File>> createIteratorOfIteratosOfFiles(
			File dir, FileFilter filter, boolean sort) {
		List<Iterable<File>> iterIter = new Vector<Iterable<File>>();
		addIteratorsForDirectory(iterIter, dir, filter, sort);

		return iterIter;
	}

	/**
	 * 
	 * @param dirs - list of directories to add
	 * @param filter
	 * @return
	 */
	private static Iterable<? extends Iterable<File>> createIteratorOfIteratosOfFiles(
			File[] dirs, FileFilter filter, boolean sort) {
		List<Iterable<File>> iterIter = new Vector<Iterable<File>>();
		for (File d : dirs) {
			addIteratorsForDirectory(iterIter, d, filter, sort);
		}

		return iterIter;
	}

	/**
	 * addIteratorsForDirectory
	 * @param dir
	 */
	private static void addIteratorsForDirectory(List<Iterable<File>> iterIter, File dir,
			FileFilter filter, boolean sort) {
		if (dir.exists()) {
			// add a new iterator for this dir
			List<File> fileIter = new Vector<File>();
			iterIter.add(fileIter);

			// iterate this dir
			File[] files = dir.listFiles();
			if (sort) {
				Arrays.sort(files);
			}
			if (files != null) {
				Queue<File> dirQueue = new LinkedList<File>();
				for (File file : files)

					// queue the directories up aside, to go over them after the files
					if (file.isDirectory())
						dirQueue.offer(file);
					else
					// add the files to this dir's iterator
					if (filter.accept(file))
						fileIter.add(file);

				// recurse the subdirs
				File subDir;
				while ((subDir = dirQueue.poll()) != null)
					addIteratorsForDirectory(iterIter, subDir, filter, sort);
			}
		}
	}

	/**
	 * Iterator to iterate over all the files contained in a directory. next() returns
	 * a File object for non directories or a new FileIterator object for directories.
	 */
	private IteratorByMultipleIterables<File> iteratorByMultipleIterables;
	
	//////////////////////////////////////////////////////////////// Testing ////////////////////////////////////////////////////////////////

	public static void main(String[] args) throws InstantiationException {
		File inputDir = new File("C:\\Temp");
		boolean sort = true;
		RecursiveFileListIterator inputFiles = new RecursiveFileListIterator(inputDir, sort, new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".txt");
			}
		});
		
		while (inputFiles.hasNext()) {
			File theFile = inputFiles.next();
			System.out.println("Visiting: " + theFile);
		}
	}
}
