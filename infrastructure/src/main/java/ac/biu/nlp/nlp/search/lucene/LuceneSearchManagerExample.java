package ac.biu.nlp.nlp.search.lucene;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import ac.biu.nlp.nlp.datasets.trec.TrecDocReader;
import ac.biu.nlp.nlp.datasets.trec.TrecException;


/**
 * This class is an example code for how you use the Lucene API in our environment. <br>
 * It wraps {@code lucene-core-3.1.0.jar} to give you simplified {@link #index(DocReader, File, FieldsCombination)} and 
 * {@link #search(Query, int)} methods.
 * <p>
 * <b>Notice</b> that indexing might fail due to a "java.IO.Exception: cannot create directory" when running on the server. 
 * This should be solved by adding the following JVM parameter to your run configuration: 
 * -Djava.io.tmpdir=your_temp_directory>
 * 
 * @see http://lucene.apache.org/java/3_1_0/api/all/index.html
 * @see /TPS/lucene_3.1.0/src
 * <br>http://alias-i.com/lingpipe-book/lucene-3-tutorial.pdf
 * @author Amnon Lotan
 * @since 06.03.11
 */
public class LuceneSearchManagerExample 
{

	public static final  Version LUCENE_CURRENT_VERSION = Version.LUCENE_31;
	
	/**
	 * The fields of a document. Every lucene {@link Document} may be indexed and searched by {@link LuceneSearchManagerExample} using any of these fields. 
	 * 
	 * @author Amnon Lotan
	 *
	 * @since Apr 6, 2011
	 */
	public enum DocField
	{
		ID,
		TEXT,
		TERM_VECTOR,
		PERIOD,
		SOURCE
	}

	/**
 	 * Index the contents of the reader into the dir indexDir. Each doc will be indexed with certain fields, as specified in the code here
 	 * <p>
	 * Uses StandardAnalyzer 
 	 * 
	 * @param docReader
	 * @param indexDir
	 * @return the number of docs indexed
	 * @throws IrException
	 */
	public int index(TrecDocReader docReader, File indexDir) throws IrException
	{
		return index(docReader, indexDir, true, false);
	}


	/**
 	 * Index the contents of the reader into the dir indexDir. Each doc will be indexed with certain fields, as specified in the code here.
 	 * <p>
 	 * Analyzes the input with the given Analyzer.
 	 * 
 	 * @param analyzer
	 * @param docReader
	 * @param indexDir
	 * @return the number of docs indexed
	 * @throws IrException
	 */
	public int index(TrecDocReader docReader, Analyzer analyzer, File indexDir) throws IrException
	{
		return index(docReader, analyzer, indexDir, true, false);
	}
	
	/**
	 * Index the contents of the reader into the dir indexDir. Each doc will be indexed with certain fields, as specified in the code here.
	 * <p>
	 * Uses StandardAnalyzer 
	 * 
	 * @param docReader
	 * @param indexDir
	 * @param dontModifyExistingIndex if true, this method will require indexDir to be empty, and will otherwise throw an exception
	 * @param overwriteNotApped in case there already is an index in indexDir, true means overwrite it; false means append
	 * @return the number of docs indexed
	 * @throws IrException
	 */
	public int index(TrecDocReader docReader, File indexDir, boolean dontModifyExistingIndex, boolean overwriteNotApped) 
		throws IrException
	{
		return index(docReader, new StandardAnalyzer(LUCENE_CURRENT_VERSION), indexDir, dontModifyExistingIndex, overwriteNotApped);
	}
		
	/**
	 * Index the contents of the reader into the dir indexDir. Each doc will be indexed with certain fields, as specified in the code here.
	 * <p>
	 * Analyzes the input with the given Analyzer. 
	 * 
	 * @param analyzer
	 * @param docReader
	 * @param indexDir
	 * @param dontModifyExistingIndex if true, this method will require indexDir to be empty, and will otherwise throw an exception
	 * @param overwriteNotApped in case there already is an index in indexDir, true means overwrite it; false means append
	 * @return the number of docs indexed
	 * @throws IrException
	 */
	public int index(TrecDocReader docReader, Analyzer analyzer, File indexDir, boolean dontModifyExistingIndex, 
			boolean overwriteNotApped) throws IrException
	{
		if (docReader == null)
			throw new IrException("got null document reader");
		if (analyzer == null)
			throw new IrException("null analyzer");
		if (indexDir == null)
			throw new IrException("null indexDir");	
		
		try
		{
			Directory fsDir = FSDirectory.open(indexDir);
			if ( dontModifyExistingIndex && fsDir.listAll().length > 0)
				throw new IrException("Failed creating index " + indexDir + ": Index already exists and write protection is turned on");

			// in case the index dir doesn't have a working index in it, overwriteNotApped  must be lit. otherwise, the indexWriter fails
			if (!indexDir.exists() || indexDir.listFiles().length == 0)		
				overwriteNotApped = true;

			IndexWriterConfig conf = new IndexWriterConfig(LUCENE_CURRENT_VERSION, analyzer);
			conf.setOpenMode(overwriteNotApped ? OpenMode.CREATE : OpenMode.CREATE_OR_APPEND);
			IndexWriter indexWriter = new IndexWriter(fsDir, conf);
			try
			{
				while ( docReader.next())	// have the reader load the next encapsulated document 
				{
					// create a lucenen Document, fill it with Fields containing our application-specific data (document number, text), and then add it to the index writer
					Document doc = new Document();
					try {
						// a field for the document ID. It's short so we specify Store.YES, and do not analyze nor normalize it
						doc.add(new Field(DocField.ID.name(), docReader.getCurrentDocNo(), Store.YES, Index.NOT_ANALYZED_NO_NORMS));

						/* Create a tokenized and indexed field, for the document's text, that is not stored. Term vectors will not be stored. The Reader is read only 
						 * when the Document is added to the index, i.e. you may not close the Reader (by calling  TrecDocReader.next()) until 
						 * IndexWriter.addDocument(Document) has been called.
						 * Notice the  field is not stored, so it won't be possible to retrieve the plain text from the index later
						 */
						doc.add(new Field(DocField.TEXT.name(), docReader.getCurrentReader()));
					} catch (Exception e) 
					{
						throw new IrException("Problem with docReader. Might have returned a null field value", e);
					}

					indexWriter.addDocument(doc);
				}
				indexWriter.optimize();
				return indexWriter.numDocs();
			}
			finally
			{
				indexWriter.close();
			}
		} catch (CorruptIndexException e)
		{
			throw new IrException("corrupt index " + indexDir,e);
		} catch (LockObtainFailedException e)
		{
			throw new IrException("Error obtaining lock on index " + indexDir,e);
		} catch (IOException e)
		{
			throw new IrException("IO exception occured with index " + indexDir,e);
		} catch (TrecException e) {
			throw new IrException("Error reading TREC",e);
		}
	}
	
	/**
	 * run the query on the field #IndexSearcher
	 * 
	 * @param query
	 * @param maxHits 
	 * @throws IOException 
	 * @throws IrException 
	 */
	public TopDocs search(File indexDir, Query query, int maxHits) throws IrException
	{
		IndexSearcher searcher =  getSearcher(indexDir);
		try
		{
			return searcher.search(query, maxHits);
		} catch (IOException e)
		{
			throw new IrException("IO error when searching for " + query.toString(), e);
		}
	}
	
	/**
	 * @param docId
	 * @return the doc matching docID
	 * @throws IrException 
	 */
	public Document getDocument(File indexDir, int docId) throws IrException
	{
		IndexSearcher searcher =  getSearcher(indexDir);
		try
		{
			return searcher.doc(docId);
		} catch (CorruptIndexException e)
		{
			throw new IrException("IO error when searching for docID " + docId, e);
		} catch (IOException e)
		{
			throw new IrException("IO error when searching for docID " + docId, e);
		}
	}
	
	/**
	 * @param indexDir
	 * @param term
	 * @param field 
	 * @return
	 * @throws IrException
	 */
	public TermDocs getTermDocs(File indexDir, String field, String term ) throws IrException
	{
		if (indexDir == null)
			throw new IrException("null indexDir");
		if (field == null)
			throw new IrException("null field");
		if (term == null)
			throw new IrException("null term");
		
		TermDocs termDocs;
		try
		{
			termDocs = getIndexReader(indexDir).termDocs(new Term(field, term));
		} catch (IOException e)
		{
			throw new IrException("Error getting termDocs " + indexDir, e);
		}
		return termDocs;
	}
	
	/**
	 * 
	 * @param indexDir
	 * @param docId
	 * @param termVectorFieldName 
	 * @return
	 * @throws IrException
	 */
	public TermFreqVector getTermFreqVec(File indexDir, int docId, String termVectorFieldName) throws IrException
	{
		if (indexDir == null)
			throw new IrException("null indexDir");
		if (termVectorFieldName == null)
			throw new IrException("null termVectorFieldName");
		
		TermFreqVector termFreqVec;
		try
		{
			termFreqVec = getIndexReader(indexDir).getTermFreqVector(docId, termVectorFieldName);
		} catch (IOException e)
		{
			throw new IrException("Error getting TermFreqVector " + indexDir, e);
		}
		return termFreqVec;
	}
	
	/**
	 * 
	 * @param indexDir
	 * @return
	 * @throws IrException
	 */
	public int getNumDocs(File indexDir) throws IrException
	{
		return getIndexReader(indexDir).numDocs();
	}

	//////////////////////////////////////////////////////////////  PRIVATE //////////////////////////////////////////////////////////////////
	
	/**
	 * @param indexDir
	 * @return
	 * @throws IrException 
	 */
	private IndexSearcher getSearcher(File indexDir) throws IrException 
	{
		if ( !searchers.containsKey(indexDir) )		
			try
			{
				searchers.put(indexDir, new IndexSearcher(IndexReader.open(FSDirectory.open(indexDir))));
			} catch (IOException e)
			{
				throw new IrException("Error accessing index dir " + indexDir, e);
			}
		return searchers.get(indexDir);
	}
	
	/**
	 * @param indexDir
	 * @return
	 * @throws IrException 
	 */
	private IndexReader getIndexReader(File indexDir) throws IrException 
	{
		return getSearcher(indexDir).getIndexReader();
	}
	
	/**
	 * Holds a searcher for each index directory 
	 */
	private Map<File, IndexSearcher> searchers = new HashMap<File, IndexSearcher>();
}