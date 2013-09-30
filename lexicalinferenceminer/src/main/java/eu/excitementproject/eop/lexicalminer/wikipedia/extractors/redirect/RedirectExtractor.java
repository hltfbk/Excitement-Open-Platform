package eu.excitementproject.eop.lexicalminer.wikipedia.extractors.redirect;

import java.io.FileNotFoundException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

//import org.apache.commons.collections.map.LinkedMap;

import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiTitleParsingException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.InitException;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.lexicalminer.definition.Common.RelationType;
import eu.excitementproject.eop.lexicalminer.definition.Common.Resource;
import eu.excitementproject.eop.lexicalminer.definition.Common.UtilClass;
import eu.excitementproject.eop.lexicalminer.wikipedia.common.IExtractor;
import eu.excitementproject.eop.lexicalminer.wikipedia.common.ruleInfo.RedirectRuleInfo;


public class RedirectExtractor implements IExtractor{


	PartOfSpeech m_nounPOS;
	Lemmatizer m_lemmatizer;
	Logger m_logger;
	public RedirectExtractor(Lemmatizer lemmatizer)
	{
		m_lemmatizer=lemmatizer;
		m_logger = org.apache.log4j.Logger.getLogger(RedirectExtractor.class.getName());
		
		try {
			m_lemmatizer.init();
			m_nounPOS = new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag.N.name());
		} catch (UnsupportedPosTagStringException e) {
			m_logger.warn("should never happend because we only create CanonicalPosTag.NOUN   . Exception:",e);
			
		} catch (LemmatizerException e) {
			m_logger.fatal("failed to initialize lemmatizer in RedirectExtractor",e);
		}
		
	}
	@Override
	public List<LexicalRule<RuleInfo>> ExtractDocument(Page page) throws FileNotFoundException, SQLException, InitException, ConfigurationException {
		List<LexicalRule<RuleInfo>> rules = new ArrayList<LexicalRule<RuleInfo>>();
		
		int pageID = page.getPageId();
		String pageTitle;
		try {
			pageTitle=page.getTitle().getPlainTitle();
		} catch (WikiTitleParsingException e2) {
			m_logger.warn("wikipedia title exception in RedirectExtractor. skipping page id:"+pageID,e2);
			return rules; // we can't create rules without the title...
		}

		
		String pageTitleLemma;
		pageTitle=UtilClass.getInstance().deAccent(pageTitle);
		try {
		    pageTitle = pageTitle.toLowerCase();
			m_lemmatizer.set(pageTitle,new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag.N.name()));
			m_lemmatizer.process();
			pageTitleLemma=m_lemmatizer.getLemma();
		} catch (LemmatizerException e) {
			m_logger.warn("error lemmatizing in RedirectExtractor. using base word instead. word:"+pageTitle);
			pageTitleLemma=pageTitle;
		} catch (UnsupportedPosTagStringException e) {
			m_logger.warn("should never happend because we only create CanonicalPosTag.NOUN   . Exception:",e);
			pageTitleLemma=pageTitle;
		}
		
		if (pageTitleLemma.contains("(")) // remove the brackets from the title
			pageTitleLemma=pageTitleLemma.split(" \\(")[0];
		
		try {
			//Set<Page> linkedPages = page.getOutlinks(); this function return the pages that their title equals the link source
			// and we have nothing to do with them. so we use page.getOutlinkAnchors() instead.
			Set<String> redirects = page.getRedirects();
			

			for (String redirect : redirects)
			{
					String srcRedirectLemma;
					redirect=UtilClass.getInstance().deAccent(redirect).replace("_", " "); // term from redirect comes with _ instead of spaces
					try {
            		    redirect = redirect.toLowerCase();
						m_lemmatizer.set(redirect,new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag.N.name()));
						m_lemmatizer.process();
						srcRedirectLemma=m_lemmatizer.getLemma();
					} catch (LemmatizerException e) {
						m_logger.warn("failed to use the lemmatizer for"+redirect+"using the original word instead. Exception:",e);
						
						srcRedirectLemma=redirect;
					} catch (UnsupportedPosTagStringException e) {
						m_logger.warn("should never happend because we only create CanonicalPosTag.NOUN   . Exception:",e);
						
						srcRedirectLemma=redirect;
					}

					
					if (UtilClass.getInstance().isValidRule(srcRedirectLemma,pageTitleLemma))
					{
						LexicalRule<RuleInfo> rule = new LexicalRule<RuleInfo>(srcRedirectLemma.toLowerCase(),
								m_nounPOS, //
								pageTitleLemma.toLowerCase(),
								m_nounPOS,
								this.getRelationType().toString(),
								Resource.Wiki.toString(),
								new RedirectRuleInfo(pageID,pageTitle,false));
						rules.add(rule);
					}
					
					
					// The opposite direction is also rule
					if (UtilClass.getInstance().isValidRule(pageTitleLemma,srcRedirectLemma))
					{
						LexicalRule<RuleInfo> rule = new LexicalRule<RuleInfo>(pageTitleLemma.toLowerCase(),
								m_nounPOS, //
								srcRedirectLemma.toLowerCase(),
								m_nounPOS,
								this.getRelationType().toString(),
								Resource.Wiki.toString(),
								new RedirectRuleInfo(pageID,pageTitle,true));
						rules.add(rule);
					}
					
			}
	
		} catch (LexicalResourceException e) {
			m_logger.error("Lexical resource exception on pageId"+pageID,e);
		}
		

		return rules;
	}
	public RelationType getRelationType() {
		return RelationType.Redirect;
	}
}
