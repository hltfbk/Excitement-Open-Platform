package eu.excitementproject.eop.lexicalminer.wikipedia;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.postagger.PosTagger;
import eu.excitementproject.eop.lexicalminer.definition.idm.EnglishSyntacticUtils;

import eu.excitementproject.eop.lexicalminer.definition.idm.IIDM;
import eu.excitementproject.eop.lexicalminer.definition.idm.LexicalIDM;
import eu.excitementproject.eop.lexicalminer.definition.idm.SyntacticIDM;
import eu.excitementproject.eop.lexicalminer.instrumentscombination.InstrumentCombinationException;
import eu.excitementproject.eop.lexicalminer.instrumentscombination.LexicalToolsFactory;
import eu.excitementproject.eop.lexicalminer.wikipedia.common.IExtractor;
import eu.excitementproject.eop.lexicalminer.wikipedia.extractors.allNoun.AllNounExtractor;
import eu.excitementproject.eop.lexicalminer.wikipedia.extractors.categories.CategoryExtractor;
import eu.excitementproject.eop.lexicalminer.wikipedia.extractors.links.LinksExtractor;
import eu.excitementproject.eop.lexicalminer.wikipedia.extractors.parentheses.ParenthesesExtractor;
import eu.excitementproject.eop.lexicalminer.wikipedia.extractors.redirect.RedirectExtractor;

/** 
 * 
 * The English version of {@link WikipediaLexicalInferencesMiner}.<br>
 * Created By: Dov Miron and Alon Halfon<br>
 * Modified by: Eyal Shnarch
 * @since 2 June 2013
 */


public class WikipediaLexicalInferencesMinerForEnglish extends WikipediaLexicalInferencesMiner{
	
	protected IExtractor getSyntacticIDMExtractor(int m_maxNPSize) {
		IIDM idm=null;
		try {
			Tokenizer tokenizer   = LexicalToolsFactory.createTokenizer(processingToolsConf);
			PosTagger posTagger   = LexicalToolsFactory.createPosTagger(processingToolsConf);
			//initialize SyntacticPatternIDM
			idm = new SyntacticIDM(new EnglishSyntacticUtils(m_maxNPSize, tokenizer, posTagger,processingToolsConf));
			
			} catch (Exception e) {
			m_logger.error("on creation of idm in all nouns extractor", e);
			return null;
		}
		
		IExtractor extractor=null;
		try {
			Lemmatizer lemmatizer = LexicalToolsFactory.createLemmatizer(processingToolsConf);
			extractor = new AllNounExtractor(idm, lemmatizer);

		} catch (Exception e) {
			m_logger.error("error initializing  AllNounExtractor. "+ e.getMessage());
			
		}
		return extractor;
	}

	protected IExtractor getLexicalIDMExtractor() {
		IIDM idm=null;
		try {
			Tokenizer tokenizer   = LexicalToolsFactory.createTokenizer(processingToolsConf);
			PosTagger posTagger   = LexicalToolsFactory.createPosTagger(processingToolsConf);
			Lemmatizer lemmatizer = LexicalToolsFactory.createLemmatizer(processingToolsConf);
			idm = new LexicalIDM(tokenizer, posTagger, lemmatizer);
					

		
			} catch (Exception e) {
			m_logger.error("on initialization of lexical IDM", e);
		}
		
		IExtractor extractor=null;
		try {
			Lemmatizer lemmatizer = LexicalToolsFactory.createLemmatizer(processingToolsConf);
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
				Lemmatizer lemmatizer = LexicalToolsFactory.createLemmatizer(processingToolsConf);
				extractor = new ParenthesesExtractor(lemmatizer);

			} catch (InstrumentCombinationException e) {
				m_logger.error("error initializing  ParenthesesExtractor. "+ e.getMessage());
			}
			
			
			return extractor;
		}
		
	

	protected IExtractor getCategoryExtractor()
		{
			
			IExtractor extractor=null;
			try 
			{
				Lemmatizer lemmatizer = LexicalToolsFactory.createLemmatizer(processingToolsConf);
				extractor = new CategoryExtractor(lemmatizer);

			} catch (InstrumentCombinationException e) {
				m_logger.error("error initializing CategoryExtractor. "+ e.getMessage());
			}
			
			
			return extractor;
		}
	
	protected IExtractor getLinksExtractor() {
		IExtractor extractor=null;
		try {
			Lemmatizer lemmatizer = LexicalToolsFactory.createLemmatizer(processingToolsConf);
			extractor = new LinksExtractor(lemmatizer);

		} catch (InstrumentCombinationException e) {
			m_logger.error("error initializing  LinksExtractor. "+ e.getMessage());
		}
		return extractor;
	}

	protected IExtractor getRedirectExtractor() {
		IExtractor extractor=null;
		try {
			Lemmatizer lemmatizer = LexicalToolsFactory.createLemmatizer(processingToolsConf);
			extractor = new RedirectExtractor(lemmatizer);

		} catch (InstrumentCombinationException e) {
			m_logger.error("error initializing  RedirectExtractor. "+ e.getMessage());
		}
		return extractor;
	}
	

}
