
package eu.excitementproject.eop.lexicalminer.wikipedia.extractors.allNoun;


import java.io.FileNotFoundException;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;



import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.InitException;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.LingPipeSentenceSplitter;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;
import eu.excitementproject.eop.lexicalminer.definition.Common.RelationType;
import eu.excitementproject.eop.lexicalminer.definition.Common.UtilClass;
import eu.excitementproject.eop.lexicalminer.definition.idm.IIDM;
import eu.excitementproject.eop.lexicalminer.wikipedia.common.IExtractor;


/*
 * The all noun extraction method
 * 
 */
public class AllNounExtractor implements IExtractor {

	 int CHARACTERS_COUNT_OF_MAIN_PARAGRAPH=500;//how many characters from the article text the SentenceSplitter get (to improve performance)
	 											//(we don't have to split the whole article to sentences because we only need the first one
	 											// so we assume the first paragraph is no longer than CHARACTERS_COUNT_OF_MAIN_PARAGRAPH)

	 
	 // the substring of the text which we are looking for the /n for to remove is the length of the title + EXTRA_NEWLINES_BUFFER
	 int EXTRA_NEWLINES_BUFFER = 12;
	 
	 IIDM _idm;
	 private Lemmatizer m_lemmatizer;
	private Logger m_logger;
	
	public AllNounExtractor(IIDM idm,Lemmatizer lemmatizer)
	{		m_lemmatizer=lemmatizer;
	
	m_logger = org.apache.log4j.Logger.getLogger(AllNounExtractor.class.getName());
		try {
			m_lemmatizer.init();
		} catch (LemmatizerException e) {
			m_logger.fatal("failed to initialize lemmatizer in AllNounExtractor",e);
		}

		_idm=idm;
		
	}

	@Override
	public List<LexicalRule<RuleInfo>> ExtractDocument(Page page) throws FileNotFoundException, SQLException, InitException, ConfigurationException {
		List<LexicalRule<RuleInfo>> inferences = new ArrayList<LexicalRule<RuleInfo>>();
		try {
			
			

			if (page.isDisambiguation()==true) 
				page.equals(null);
			String title = page.getTitle().getPlainTitle();
			int fullTitleLength=title.length();
			

		
			if (title.contains("(")) // remove the brackets from the title
				title=title.split(" \\(")[0];
			title = UtilClass.getInstance().deAccent(title).trim();
			try {
			    title = title.toLowerCase();
				m_lemmatizer.set(title,new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag.N.name()));
				m_lemmatizer.process();
				title=m_lemmatizer.getLemma();
			} catch (LemmatizerException e) {

				m_logger.warn("Lemmatizer exception in AllNoun when trying to lemmatizer word:"+title,e);
			} catch (UnsupportedPosTagStringException e) {
				m_logger.warn("should never happend because we only create CanonicalPosTag.NOUN   . Exception:"+title,e);
				
			}
			
			
			// first we need to extract the first sentence of the article
			String text = page.getPlainText();
			

			if (text.contains("#REDIRECT"))
				return new LinkedList<LexicalRule<RuleInfo>>(); 
			
			
			// get rid of the title in case it's part of the text
			if (text.length()<fullTitleLength+EXTRA_NEWLINES_BUFFER) // if the text is too short we assume it's garbage and ignore it.
				return new LinkedList<LexicalRule<RuleInfo>>(); 
			
			
			// get rid of the title in case it's part of the text	
			if (text.substring(0, fullTitleLength+EXTRA_NEWLINES_BUFFER).contains("\n"))
				text=text.split("\n",2)[1];
			

			
			if (text.length()>CHARACTERS_COUNT_OF_MAIN_PARAGRAPH)
				text=text.substring(0,CHARACTERS_COUNT_OF_MAIN_PARAGRAPH);
			
			text = text.replace("\n", ""); // remove new lines

			text=text.trim(); // remove whitespaces
			if (text.length()==0) // no text after processing (e.g for the article "Cogency")
				return new LinkedList<LexicalRule<RuleInfo>>();
			
			String firstSen;
			SentenceSplitter sentSplitter = new LingPipeSentenceSplitter();
			try {
				sentSplitter.setDocument(text);
				sentSplitter.split();
				firstSen=sentSplitter.getSentences().get(0);
				
			} catch (SentenceSplitterException e) {
				m_logger.warn("error in SentenceSplitter in AllNouns Extractor",e);
				// fall-back to simpler method for getting the first sentence

				firstSen=text.split("[\\.\\?\\!][\\r\\n\\t ]+")[0];
			}

			
			//use the IDM on the first sentence
			
			
			List<LexicalRule<RuleInfo>> rules = _idm.retrieveSentenceLexicalRules(UtilClass.getInstance().deAccent(firstSen),title,page.getPageId());
            
			return rules;
			
		} catch (WikiApiException e) {

			m_logger.error("Exception in wikipedia in AllNounExtractor",e);
		}

		
		
		return inferences;
		
	}

	@Override
	public RelationType getRelationType() {

		return _idm.getRelationType();
	}

}
