package eu.excitementproject.eop.lexicalminer.wikipedia.extractors.parentheses;

import java.io.FileNotFoundException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

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
import eu.excitementproject.eop.lexicalminer.wikipedia.common.ruleInfo.ParenthesesRuleInfo;

public class ParenthesesExtractor implements IExtractor {


	Lemmatizer m_lemmatizer;
	PartOfSpeech m_nounPOS;
	private Logger m_logger;
	
	public ParenthesesExtractor(Lemmatizer lemmatizer)
	{
		m_lemmatizer=lemmatizer;
		m_logger = org.apache.log4j.Logger.getLogger(ParenthesesExtractor.class.getName());
		try {
			m_lemmatizer.init();
			m_nounPOS = new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag.N.name());
		} catch (UnsupportedPosTagStringException e) {
			m_logger.warn("should never happend because we only create CanonicalPosTag.NOUN   . Exception:",e);		
		} catch (LemmatizerException e) {

			m_logger.fatal("failed to initialize lemmatizer in ParenthesesExtractor",e);
		}
		

		
	}
	
	@Override
	public List<LexicalRule<RuleInfo>> ExtractDocument(Page page) throws FileNotFoundException, SQLException, InitException, ConfigurationException {
		List<LexicalRule<RuleInfo>> rules = new ArrayList<LexicalRule<RuleInfo>>();
		try {
			
			int pageID = page.getPageId();
			String pageTitle;
			try {
				pageTitle=page.getTitle().getPlainTitle();
			} catch (WikiTitleParsingException e2) {
				m_logger.warn("wikipedia title exception in ParenthesesExtractor. skipping page id:"+pageID,e2);
				return rules; // we can't create rules without the title...
			}
			
			
			
			String title = page.getTitle().getPlainTitle();
			if (title.contains("("))
			{
				String [] parts = title.split(" \\(");
				if (parts.length<2)
					return new ArrayList<LexicalRule<RuleInfo>>();
				String left=parts[0].trim();
				String right=parts[1].replace(")", "").trim();
				
				left=UtilClass.getInstance().deAccent(left);
				String leftLemma;
				try {
				    left = left.toLowerCase();
					m_lemmatizer.set(left,new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag.N.name()));
					m_lemmatizer.process();
					leftLemma=m_lemmatizer.getLemma();
				} catch (LemmatizerException e) {
					m_logger.warn("failed to use the lemmatizer for"+left+"using the original word instead. Exception:",e);
					leftLemma=left;
				} catch (UnsupportedPosTagStringException e) {
					m_logger.warn("should never happend because we only create CanonicalPosTag.NOUN   . Exception:"+title,e);
					
					leftLemma=left;
				}
				right=UtilClass.getInstance().deAccent(right);
				String rightLemma;
				try {
				    right = right.toLowerCase();
					m_lemmatizer.set(right,new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag.N.name()));
					m_lemmatizer.process();
					rightLemma=m_lemmatizer.getLemma();
				} catch (LemmatizerException e) {
					m_logger.warn("failed to use the lemmatizer for"+right+"using the original word instead. Exception:",e);
					rightLemma=right;
				} catch (UnsupportedPosTagStringException e) {

					m_logger.warn("should never happend because we only create CanonicalPosTag.NOUN   . Exception:"+title,e);
					
					rightLemma=right;
				}
				
				LexicalRule<RuleInfo> rule;

				UtilClass.getInstance();
				if (UtilClass.isValidRule(leftLemma, rightLemma))
				{
					rule = new LexicalRule<RuleInfo>(leftLemma.toLowerCase(),
							m_nounPOS, //
							rightLemma.toLowerCase(),
							m_nounPOS,
							this.getRelationType().toString(),
							Resource.Wiki.toString(),
							new ParenthesesRuleInfo(pageID,pageTitle));
	
					rules.add(rule);
				}
				
				return rules;
			}
		} catch (WikiTitleParsingException e) {
			m_logger.error("Wiki title parsing error",e);
		} catch (LexicalResourceException e) {
			m_logger.error("lexical resource exception on pageID",e);
		}
		return rules;
		
	}
	public RelationType getRelationType() {
		return RelationType.Parenthesis;
	}
}
