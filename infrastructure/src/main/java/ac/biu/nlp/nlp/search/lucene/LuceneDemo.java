package ac.biu.nlp.nlp.search.lucene;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import ac.biu.nlp.nlp.datasets.trec.TrecDocReader;
import ac.biu.nlp.nlp.datasets.trec.TrecException;
import ac.biu.nlp.nlp.datasets.trec.TrecSentenceReader;
import ac.biu.nlp.nlp.instruments.sentencesplit.LingPipeSentenceSplitter;


/**
 * @author Amnon Lotan
 *
 * @since 10/03/2011
 */
public class LuceneDemo
{
	/*
	 * 	the corpus dir "C:/temp/shit" should include a couple of text files like these:
	 *  hamburger
		french fries
		steak
		mushrooms
		artichokes

		apples
		bananas
		salad
		mushrooms
		cheese
	 */
	
	
	/**
	 * @param args
	 * @throws IrException 
	 * @throws TrecException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IrException, TrecException
	{
		List<List<String>> terms = new Vector<List<String>>();
		terms.add(new Vector<String>());		
		
		// Use QueryBuilder to construct a query that requires the words "reporter" and "said" appear, and requires the word "french" not appear.
		List<String> mustNotOccurTerms = new Vector<String>();
		mustNotOccurTerms.add("french");
		terms.get(0).add("reporter");
		terms.get(0).add("said");
		QueryBuilder queryBuilder = new QueryBuilder(LuceneSearchManagerExample.DocField.TEXT.name());
		Query query = queryBuilder.andQuery(terms.get(0), mustNotOccurTerms);
		
		// construct the TrecDocReader, used to read the TREC corpora, document by document 
		
		// the LuceneSearchManagerDemo manages the index and search operations
		LuceneSearchManagerExample manager = new LuceneSearchManagerExample();
		File indexDir = new File("C:/temp/index");
		
		///////////////////////////////////////////////////////////////////
		//
		// Index - comment these lines out if you already have your lucene index prepared!
		//
		////////////////////////////////////////////////////////////////////
		System.out.println("Indexing Trec. This will take at least 10 minutes..." );
		File trecDir = new File("//nlp-srv/data2/CORPORA2/TREC");
		TrecDocReader trecReader = new TrecSentenceReader(trecDir, 	new LingPipeSentenceSplitter());
		manager.index( trecReader, indexDir, false, true);
		System.out.println("... Done!" );
		
		System.out.println("Executing this query on the index: " +query);
		int maxHits = 10;
		TopDocs hits = manager.search(indexDir, query, maxHits);
		System.out.println("Got "+hits.totalHits+" hits, and here are the first "+maxHits);
		for (ScoreDoc doc : hits.scoreDocs)
			System.out.println("retrieved: " + doc.toString());
		System.out.println(manager.getDocument(indexDir, 10).get(LuceneSearchManagerExample.DocField.TEXT.name()));
	}
}