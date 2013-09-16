package eu.excitementproject.eop.lexicalminer.wikipedia.extractors.categories;

import java.io.FileNotFoundException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.wikipedia.api.Category;
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
import eu.excitementproject.eop.lexicalminer.wikipedia.common.ruleInfo.CategoryRuleInfo;

public class CategoryExtractor implements IExtractor {

	Lemmatizer m_lemmatizer;
	PartOfSpeech m_nounPOS;
	private Logger m_logger;
	
	public CategoryExtractor(Lemmatizer lemmatizer)
	{
		m_lemmatizer=lemmatizer;
		m_logger = org.apache.log4j.Logger.getLogger(CategoryExtractor.class.getName());
		try {
			m_lemmatizer.init();
			m_nounPOS = new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag.N.name());
		} catch (UnsupportedPosTagStringException e) {

			e.printStackTrace();
		} catch (LemmatizerException e) {
			m_logger.fatal("failed to initialize lemmatizer in CategoryExtractor",e);
		}
		

	}
	
	
	@Override
	public List<LexicalRule<RuleInfo>> ExtractDocument(Page page)
			throws FileNotFoundException, SQLException, InitException, ConfigurationException {
		String title;
		String titleBase;
		List<LexicalRule<RuleInfo>> rules = new ArrayList<LexicalRule<RuleInfo>>();
		int pageID=page.getPageId();
		try {
			title = titleBase = page.getTitle().getPlainTitle();
		} catch (WikiTitleParsingException e3) {

			 m_logger.warn("wikipedia title exception in CategoryExtractor. skipping page id:"+pageID,e3);
			return rules; // we can't create rules without the title...
		}
		
		if (title.contains("(")) // remove the brackets from the title
			title=title.split(" \\(")[0];
		
		title=UtilClass.getInstance().deAccent(title);
		// get the lemma of the title
		try {
		    title = title.toLowerCase();
			m_lemmatizer.set(title,new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag.N.name()));
			m_lemmatizer.process();
			title = m_lemmatizer.getLemma();
			

		} catch (LemmatizerException e) {
			m_logger.warn("Error in lemmatizer word:"+title,e);
		} catch (UnsupportedPosTagStringException e) {
			m_logger.warn("should never happend because we only create CanonicalPosTag.NOUN   . Exception:"+title,e);
			
		}
		
		
		Set<Category> cats = page.getCategories();
		
		
		for (Category cat : cats)
		{
			try {
				String category = cat.getTitle().getPlainTitle();
				if (category.toLowerCase().contains("article") || category.toLowerCase().contains("wikipedia")) // ignore all the categories like "articles contains Spanish artists"
					continue;
				category=UtilClass.getInstance().deAccent(category);
				category = category.toLowerCase();
				m_lemmatizer.set(category,new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag.N.name()));
				m_lemmatizer.process();
				category=m_lemmatizer.getLemma();
				
				if (UtilClass.getInstance().isValidRule(title, category))
				{
					LexicalRule<RuleInfo> rule = new LexicalRule<RuleInfo>(title.toLowerCase(),
							m_nounPOS, 
							category.toLowerCase(),
							m_nounPOS,
							this.getRelationType().toString(),
							Resource.Wiki.toString(),
							new CategoryRuleInfo(pageID,titleBase));
					rules.add(rule);
				}
				
			} catch (WikiTitleParsingException e) {

				m_logger.error("title parsing exception in CategoryExtractor in title:"+title,e);
			} catch (LemmatizerException e) {

				m_logger.error("Error in lemmatizer in CategoryExtractor title:"+title,e);
			} catch (LexicalResourceException e) {

				m_logger.warn("Lexical resource exception in CategoryExtractor. title:"+title,e);
			} catch (UnsupportedPosTagStringException e) {
				m_logger.warn("should never happend because we only create CanonicalPosTag.NOUN   . Exception:"+title,e);
				
			}
		}
			
		
		return rules;
	}


	public RelationType getRelationType() {
		return RelationType.Category;
	}

}
