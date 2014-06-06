package eu.excitementproject.eop.lexicalminer.Italian.wikipedia;

import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.lexicalminer.Italian.language.ItalianSyntacticUtils;
import eu.excitementproject.eop.lexicalminer.Italian.language.textpro.TextProItalianLexicalSentenceProcessor;
import eu.excitementproject.eop.lexicalminer.Italian.language.textpro.TextProLemmatizer;
import eu.excitementproject.eop.lexicalminer.definition.idm.IIDM;
import eu.excitementproject.eop.lexicalminer.definition.idm.LexicalIDM;
import eu.excitementproject.eop.lexicalminer.definition.idm.SyntacticIDM;
import eu.excitementproject.eop.lexicalminer.instrumentscombination.LexicalSentenceProcessor;
import eu.excitementproject.eop.lexicalminer.instrumentscombination.LexicalToolsFactory;
import eu.excitementproject.eop.lexicalminer.wikipedia.WikipediaLexicalInferencesMiner;
import eu.excitementproject.eop.lexicalminer.wikipedia.common.IExtractor;
import eu.excitementproject.eop.lexicalminer.wikipedia.extractors.allNoun.AllNounExtractor;
import eu.excitementproject.eop.lexicalminer.wikipedia.extractors.categories.CategoryExtractor;
import eu.excitementproject.eop.lexicalminer.wikipedia.extractors.links.LinksExtractor;
import eu.excitementproject.eop.lexicalminer.wikipedia.extractors.parentheses.ParenthesesExtractor;
import eu.excitementproject.eop.lexicalminer.wikipedia.extractors.redirect.RedirectExtractor;


/** 
 * 
 * The Italian version of {@link WikipediaLexicalInferencesMiner}.<br>
 * Created By: Eyal Shnarch
 * @since 2 June 2013
 * 
 */


public class WikipediaLexicalInferencesMinerForItalian extends WikipediaLexicalInferencesMiner{
	
	protected IExtractor getSyntacticIDMExtractor(int m_maxNPSize) {
		IIDM idm=null;
		try {
			
			//initialize SyntacticPatternIDM
			idm = new SyntacticIDM(new ItalianSyntacticUtils(m_maxNPSize, processingToolsConf));
			
			} catch (Exception e) {
			m_logger.error("on creation of idm in all nouns extractor", e);
			return null;
		}
		
		IExtractor extractor=null;
		try {
//			Lemmatizer lemmatizer = LexicalToolsFactory.createLemmatizer(processingToolsConf);
			Lemmatizer lemmatizer = new TextProLemmatizer(processingToolsConf);
			extractor = new AllNounExtractor(idm, lemmatizer);

		} catch (Exception e) {
			m_logger.error("error initializing  AllNounExtractor. "+ e.getMessage());
			
		}
		return extractor;
	}

	protected IExtractor getLexicalIDMExtractor() {
		IIDM idm=null;
		try {
			
			LexicalSentenceProcessor sentPoc = new TextProItalianLexicalSentenceProcessor(processingToolsConf);
			idm = new LexicalIDM(sentPoc);

		} catch (Exception e) {
			m_logger.error("on initialization of lexical IDM", e);
		}
		
		IExtractor extractor=null;
		try {
//			Lemmatizer lemmatizer = LexicalToolsFactory.createLemmatizer(processingToolsConf);
			Lemmatizer lemmatizer = new TextProLemmatizer(processingToolsConf); 
			extractor = new AllNounExtractor(idm, lemmatizer);

		} catch (Exception e) {

			m_logger.error("on initialization of AllNounExtractor", e);
		}
		
		return extractor;
	}
	
	protected IExtractor getParenthesesExtractor()
		{
			
			IExtractor extractor=null;
			try {
				Lemmatizer lemmatizer = new TextProLemmatizer(processingToolsConf); 
				extractor = new ParenthesesExtractor(lemmatizer);
				
			} catch (LemmatizerException e) {
				m_logger.error("error initializing  ParenthesesExtractor. "+ e.getMessage());
			}
			
			
			return extractor;
		}
		
	protected IExtractor getCategoryExtractor()
		{
			
			IExtractor extractor=null;
			try 
			{
				Lemmatizer lemmatizer = new TextProLemmatizer(processingToolsConf); 
				extractor = new CategoryExtractor(lemmatizer);

			} catch (LemmatizerException e) {
				m_logger.error("error initializing CategoryExtractor. "+ e.getMessage());
			}
			
			return extractor;
		}
	
	protected IExtractor getLinksExtractor() {
		IExtractor extractor=null;
		try {
			Lemmatizer lemmatizer = new TextProLemmatizer(processingToolsConf); 
			extractor = new LinksExtractor(lemmatizer);

		} catch (LemmatizerException e) {
			m_logger.error("error initializing  LinksExtractor. "+ e.getMessage());
		}
		return extractor;
	}

	protected IExtractor getRedirectExtractor() {
		IExtractor extractor=null;
		try {
			Lemmatizer lemmatizer = new TextProLemmatizer(processingToolsConf); 
			extractor = new RedirectExtractor(lemmatizer);
			
		} catch (LemmatizerException e) {
			m_logger.error("error initializing  RedirectExtractor. "+ e.getMessage());
		}
		return extractor;
	}
	

}
