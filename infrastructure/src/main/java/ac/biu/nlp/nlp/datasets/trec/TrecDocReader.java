/**
 * 
 */
package ac.biu.nlp.nlp.datasets.trec;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import ac.biu.nlp.nlp.general.StringUtil;
import ac.biu.nlp.nlp.general.Utils;
import ac.biu.nlp.nlp.general.configuration.JaxbLoader;

/**
 * Use this class to read all of TREC {@link \\nlp-srv\data2\CORPORA2\TREC\} into document-size text chunks (one String is returned for each DOC xml object).
 * It marshals the TREC xml files with {@link JaxbLoader} and
 * the schema info in  <code>org.BIU.NLP.corpora.trec_new.</code>
 * 
 * TODO make sure this reader reads all the text, i.e. the atomic xml elements in all file. maybe print it all
 * via a py script and with jaxb and compare the two printouts.
 * <p>
 * <b>IMPORTANT </b>currently there is a JaxB error that prevents {@link JaxbLoader} from loading the <i>PATENTS</i> folder. Therefore, there is a special
 * workaround in this class that skips <i>PATENTS</i> when reading the TREC directory. It's probably solvable by either upgrading
 * to java 7 (in hope that the upgraded jaxb loader that ships with rt.jar will contain a fix), or perhas somehow recreating the xsd files for 
 * <i>PATENTS</i> in a different way.
 * <p>
 * This is the exception:<br>
 * Exception in thread "main" ac.biu.nlp.nlp.search.IrException: failed to load the folder D:\data2\TREC\PATENTS
	at ac.biu.nlp.nlp.search.readers.TrecSentenceReader.nextCoupusDir(TrecSentenceReader.java:248)
	at ac.biu.nlp.nlp.search.readers.TrecSentenceReader.<init>(TrecSentenceReader.java:85)
	at ac.biu.nlp.nlp.search.readers.TrecDemo.main(TrecDemo.java:62)
Caused by: ac.biu.nlp.nlp.search.IrException: failed to create JaxbLoader with package org.BIU.NLP.corpora.trec_new.patents, see nested exception from JaxbLoader:
	at ac.biu.nlp.nlp.search.readers.TrecSentenceReader.getLoader(TrecSentenceReader.java:160)
	at ac.biu.nlp.nlp.search.readers.TrecSentenceReader.nextCoupusDir(TrecSentenceReader.java:246)
	... 2 more
Caused by: com.sun.xml.bind.v2.runtime.IllegalAnnotationsException: 1 counts of IllegalAnnotationExceptions
The element name {}DOCNO has more than one mapping.
	this problem is related to the following location:
		at public javax.xml.bind.JAXBElement org.BIU.NLP.corpora.trec_new.patents.ObjectFactory.createDOCNO(java.lang.String)
		at org.BIU.NLP.corpora.trec_new.patents.ObjectFactory
	this problem is related to the following location:
		at public javax.xml.bind.JAXBElement org.BIU.NLP.corpora.trec_new.fr94.ObjectFactory.createDOCNO(java.lang.String)
		at org.BIU.NLP.corpora.trec_new.fr94.ObjectFactory
		at protected java.util.List org.BIU.NLP.corpora.trec_new.fr94.TEXT.content
		at org.BIU.NLP.corpora.trec_new.fr94.TEXT
		at protected org.BIU.NLP.corpora.trec_new.fr94.TEXT org.BIU.NLP.corpora.trec_new.patents.DOC.text
		at org.BIU.NLP.corpora.trec_new.patents.DOC
		at protected java.util.List org.BIU.NLP.corpora.trec_new.patents.PATENTSFILE.doc
		at org.BIU.NLP.corpora.trec_new.patents.PATENTSFILE
		at public org.BIU.NLP.corpora.trec_new.patents.PATENTSFILE org.BIU.NLP.corpora.trec_new.patents.ObjectFactory.createPATENTSFILE()
		at org.BIU.NLP.corpora.trec_new.patents.ObjectFactory

	at com.sun.xml.bind.v2.runtime.IllegalAnnotationsException$Builder.check(IllegalAnnotationsException.java:102)
	at com.sun.xml.bind.v2.runtime.JAXBContextImpl.getTypeInfoSet(JAXBContextImpl.java:472)
	at com.sun.xml.bind.v2.runtime.JAXBContextImpl.<init>(JAXBContextImpl.java:302)
	at com.sun.xml.bind.v2.runtime.JAXBContextImpl$JAXBContextBuilder.build(JAXBContextImpl.java:1140)
	at com.sun.xml.bind.v2.ContextFactory.createContext(ContextFactory.java:154)
	at com.sun.xml.bind.v2.ContextFactory.createContext(ContextFactory.java:121)
	at com.sun.xml.bind.v2.ContextFactory.createContext(ContextFactory.java:202)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
	at java.lang.reflect.Method.invoke(Method.java:597)
	at javax.xml.bind.ContextFinder.newInstance(ContextFinder.java:128)
	at javax.xml.bind.ContextFinder.find(ContextFinder.java:277)
	at javax.xml.bind.JAXBContext.newInstance(JAXBContext.java:372)
	at javax.xml.bind.JAXBContext.newInstance(JAXBContext.java:337)
	at javax.xml.bind.JAXBContext.newInstance(JAXBContext.java:244)
	at ac.biu.nlp.nlp.general.configuration.JaxbLoader.<init>(JaxbLoader.java:55)
	at ac.biu.nlp.nlp.search.readers.TrecSentenceReader.getLoader(TrecSentenceReader.java:158)
	... 3 more

 * 
 * @author Eyal Shnarch
 * @since 06/07/2011
 */
public class TrecDocReader
{

	protected static final String BASE_PACKAGE = DataFile.BASE_TREC_GENERATED_PACKAGE;
	/**
	 * Protected class - Simply holds a {@link Iterator} and a {@link JaxbLoader}
	 * @author Amnon Lotan
	 * @since Nov 21, 2011
	 */
	protected class FileIteratorAndLoader
	{
		public final Iterator<File> fileIter;
		public final JaxbLoader<DataFile> jaxbLoader;
		public FileIteratorAndLoader(Iterator<File> fileIter,
				JaxbLoader<DataFile> jaxbLoader) {
			super();
			this.fileIter = fileIter;
			this.jaxbLoader = jaxbLoader;
		}
	}
	protected static final FilenameFilter SKIP_READMES_FILE_FILTER = new FilenameFilter() {
		
		@Override
		public boolean accept(File dir, String name) {
			return !name.startsWith("READ");
		}
	};
	/**
	 * accept directories, but screen out the "PATENTS" directory, for reasons explains in the class comment
	 */
	protected static final FileFilter GET_DIRS_EXCEPT_PATENTS_FILE_FILTER = new FileFilter() {
		
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory() && !pathname.getName().equals("PATENTS");
		}
	};
	protected static final FileFilter GET_DIRS_FILE_FILTER = new FileFilter() {
		
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};

	protected final File TREC_DIR;
	protected final Iterator<File> CORPUS_ITER;
	
	protected FileIteratorAndLoader fileIteratorAndLoader;
	protected Iterator<Doc>  docIter;
	protected String docNo;
	protected String docText;
	protected StringReader currReader;
	
	/**
	 * Ctor - Puts the reader's head on the first line of the first file of the trecDir
	 * @param trecDir
	 * @param stopWords
	 * @param sentProc
	 * @param sentSplitter
	 * @throws TrecException
	 */
	public TrecDocReader(File trecDir) throws TrecException 	{
		super();
		if (trecDir == null || !trecDir.exists())
			throw new TrecException("the TREC directory " + trecDir + " is null or nonexistant");
		this.TREC_DIR = trecDir;

		/*
		 * Put the reader's head on the first line of the first file of the Trec input dir.
		 */
		CORPUS_ITER = flattenTrecDirs(TREC_DIR).iterator();
		
	}

	/**
	 * Returns true iff there is another document to load
	 * @return
	 */
	public boolean hasNext() {
		return (CORPUS_ITER.hasNext() || fileIteratorAndLoader.fileIter.hasNext()	||	docIter.hasNext() );
	}
	
	/**
	 * Progresses the fields (text reader, text String and document number) to the next document in sequence. Returns true iff there was another document to load, 
	 * and it was loaded.
	 * @throws TrecException 
	 */
	public boolean next() throws TrecException {
		close();
		boolean hasNext = hasNext();
		if (hasNext)
		{
			hasNext = true;
			
			// Load the text and doc number of the next doc
			if (docIter == null || !docIter.hasNext())
				docIter = nextFile();
			Doc doc = docIter.next();
			docNo = doc.getDOCNO();
			docText = StringUtil.joinIterableToString(doc.getTexts(), " ");

			currReader = null;
		}
		return hasNext;
	}
	
	public Reader getCurrentReader()
	{
		if (currReader == null)
			currReader = new StringReader(docText);
		return currReader;
	}
	
	public String getCurrentDocText()
	{
		return docText;
	}
	
	/**
	 * @return the docNo
	 */
	public String getCurrentDocNo() {
		return docNo;
	}
	
	public void close() {
		if (currReader != null)
			currReader.close();
	}
	
	////////////////// protected methods ///////////////////////

	/**
	 * Retrieve a list of all the folders under "TREC" that directly contain xml files. We assume the directory structure under "TREC" has several 
	 * directories where each contains xml files, and other directories that contain a second level of subdirectories, which contain xmls.
	 *  
	 * @param trecDir
	 * @return
	 * @throws TrecException 
	 */
	protected List<File> flattenTrecDirs(File trecDir) throws TrecException {
		List<File> foldersWithXmlFiles = new Vector<File>();
		for(File dir : trecDir.listFiles(GET_DIRS_EXCEPT_PATENTS_FILE_FILTER)){
			File[] subDirs = dir.listFiles(GET_DIRS_FILE_FILTER);
			if (subDirs.length == 0)
				foldersWithXmlFiles.add(dir);	// add directories that contain no subdirectories, (assuming those contain only xml files)
			else
			{									// add the subdirectories (presumably containing xmls) but not the dir (which contains only directories)
				for(File subdir : subDirs)
					foldersWithXmlFiles.add(subdir);
			}
		}
		if (foldersWithXmlFiles.isEmpty())
			throw new TrecException(trecDir + " is empty");
		return foldersWithXmlFiles;
	}
		
	/**
	 * Load the document iterator for the next file
	 * @param corpusIter 
	 * @param loader
	 * @return 
	 * @throws TrecException 
	 */
	protected Iterator<Doc> nextFile() throws TrecException  {
		Iterator<Doc> docsIter = null;
		
		if (fileIteratorAndLoader == null || !fileIteratorAndLoader.fileIter.hasNext())
			fileIteratorAndLoader = nextCorpusDir(CORPUS_ITER);

		Iterator<File> fileIter = fileIteratorAndLoader.fileIter;
		File currFile = fileIter.next();
		//		System.out.println("TrecReader: now reading "+currFile);
		List<Doc> docs;
		try {	docs = fileIteratorAndLoader.jaxbLoader.load(currFile).getDocs();	}
		catch (TrecException e) {	throw new TrecException("Error loading the Doc objects in " + currFile + "\n" +  e.getStackTrace());	} 
		catch (JAXBException e)		{	e.printStackTrace(); throw new TrecException("Error loading the Doc objects in " + currFile + "\n" +  e.toString()); 	}
		docsIter = docs.iterator();
		return docsIter;
	}

	/**
	 * advance the reader to the next corpus directory
	 * @return
	 * @throws TrecException 
	 */
	protected FileIteratorAndLoader nextCorpusDir(Iterator<File> corpusIter) throws TrecException  {
		FileIteratorAndLoader fileIterAndLoader = null;
		if (!corpusIter.hasNext())
			throw new TrecException("no more docs in the TREC folder");

		File currCorpusDir = corpusIter.next();
		List<File> files = Utils.arrayToCollection(currCorpusDir.listFiles(SKIP_READMES_FILE_FILTER), new Vector<File>()); 
		if (files.isEmpty())
			throw new TrecException("No files found in " + currCorpusDir);
		Iterator<File> fileIter = files.iterator();
		JaxbLoader<DataFile> loader;
		try {	loader = getLoader(currCorpusDir);	}
		catch (TrecException e) {	throw new TrecException("failed to load the folder " + currCorpusDir + ". see nested", e);	}
		fileIterAndLoader = new FileIteratorAndLoader(fileIter, loader); 
		
		return fileIterAndLoader;
	}

	protected JaxbLoader<DataFile> getLoader(File currCorpusDir) throws TrecException {
		String pack;
		if(currCorpusDir.getParentFile().equals(TREC_DIR)){
			pack = BASE_PACKAGE + currCorpusDir.getName().toLowerCase();
		}else{
			pack = BASE_PACKAGE + currCorpusDir.getParentFile().getName().toLowerCase();
		}
		try {
			return new JaxbLoader<DataFile>(pack);
		} catch (JAXBException e) {
			throw new TrecException("failed to create JaxbLoader with package " + pack + ", see nested exception from JaxbLoader:", e);		}
	}
}