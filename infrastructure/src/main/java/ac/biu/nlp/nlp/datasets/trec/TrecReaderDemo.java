/**
 * 
 */
package ac.biu.nlp.nlp.datasets.trec;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.bind.JAXBException;


import ac.biu.nlp.nlp.instruments.lemmatizer.LemmatizerException;
import ac.biu.nlp.nlp.instruments.postagger.PosTaggerException;
import ac.biu.nlp.nlp.instruments.sentencesplit.LingPipeSentenceSplitter;
import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitterException;
import ac.biu.nlp.nlp.instruments.tokenizer.TokenizerException;
import ac.biu.nlp.nlp.instrumentscombination.InstrumentCombinationException;
import ac.biu.nlp.nlp.search.lucene.IrException;

/**
 * @author Amnon Lotan
 *
 * @since Nov 20, 2011
 */
public class TrecReaderDemo {

	/**
	 * @param args
	 * @throws LemmatizerException 
	 * @throws PosTaggerException 
	 * @throws InstrumentCombinationException 
	 * @throws IOException 
	 * @throws SentenceSplitterException 
	 * @throws TrecException 
	 * @throws JAXBException 
	 * @throws IrException 
	 * @throws MalformedURLException 
	 * @throws TokenizerException 
	 */
	public static void main(String[] args) throws MalformedURLException, IrException, JAXBException, TrecException, SentenceSplitterException, IOException, InstrumentCombinationException, PosTaggerException, LemmatizerException, TokenizerException {

		File trecDir = new File("//nlp-srv/data2/CORPORA2/TREC");
		TrecDocReader reader = new TrecSentenceReader(trecDir, 	new LingPipeSentenceSplitter());
//		TrecDocReader reader = new TrecDocReader(trecDir);
		
		System.out.println("Reading TREC sentence by sentence. This may take over 10 minutes...");
		int docs = 0;
		while (reader.hasNext())
		{
			reader.next();
			
			// printing out the IDs of all sentences of TREC can crash your eclipse. So you may want to comment out the following print
//			String docID = reader.getCurrentDocNo();
//			System.out.println("TrecReader: now reading "+docID);
			
			docs++;
			
		}
		System.out.println("Done!\nRead " + docs + " docs");
	}
}