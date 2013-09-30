package eu.excitementproject.eop.lexicalminer.wikipedia.extractors.links;

import java.io.FileNotFoundException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

//import org.apache.commons.collections.map.LinkedMap;

import de.tudarmstadt.ukp.wikipedia.api.Page;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.lexicalminer.definition.Common.RelationType;
import eu.excitementproject.eop.lexicalminer.wikipedia.common.IExtractor;

/**
 * IMPORTANT NOTE
 * this extractor works with earlier version on jwpl.
 * the purpose of this class is to extract rules from the links in the wikipidia articles.
 * when new STABLE jwpl comes to the air this code should be uncommented and refactor to fit that version
 * @author mirond and alon halfon
 *
 */
public class LinksExtractor implements IExtractor{


	PartOfSpeech m_nounPOS;
	Lemmatizer m_lemmatizer;
	private Logger m_logger;
	public LinksExtractor(Lemmatizer lemmatizer)
	{
		m_lemmatizer=lemmatizer;
		m_logger = org.apache.log4j.Logger.getLogger(LinksExtractor.class.getName());
	
		
		try {
			m_lemmatizer.init();
			m_nounPOS = new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag.N.name());
		} catch (UnsupportedPosTagStringException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LemmatizerException e) {
			m_logger.fatal("failed to initialize lemmatizer in LinksExtractor",e);
		}
	}
	@Override
	public List<LexicalRule<RuleInfo>> ExtractDocument(Page page) throws FileNotFoundException, SQLException {
		List<LexicalRule<RuleInfo>> rules = new ArrayList<LexicalRule<RuleInfo>>();
		return rules;
		//TODO : Handle Exception !!!!!!!!!!!!!!!!!! IMPORTANT WHEN USING LINKS EXTRACTOR AGAIN
//		int pageID = page.getPageId(); MAKE SURE THIS IS ThE RIGHT LOGIC WHEN UNCOMMENT
//		String pageTitle;
//		
//		try {
//			pageTitle=page.getTitle().getPlainTitle();
//		} catch (WikiTitleParsingException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//			return rules; // we can't create rules without the title...
//		}
//
//		try {
//			//Set<Page> linkedPages = page.getOutlinks(); this function return the pages that their title equals the link source
//			// and we have nothing to do with them. so we use page.getOutlinkAnchors() instead.
//			Map<String,Set<String>> map = page.getOutlinkAnchors();
//			
//			for (Map.Entry<String, Set<String>> entry : map.entrySet())
//			{
//				String src = entry.getKey();
//	
//				for (String possibleLink : entry.getValue())
//				{
//			
//					String srcLemma;
//					src=UtilClass.deAccent(src);
//					try {
//						m_lemmatizer.set(src,new UnspecifiedPartOfSpeech(CanonicalPosTag.NOUN));
//						m_lemmatizer.process();
//						srcLemma=m_lemmatizer.getLemma();
//					} catch (LemmatizerException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						srcLemma=src;
//					}
//					
//					String possibleLinkLemma;
//					possibleLinkLemma=UtilClass.deAccent(possibleLinkLemma);
//					try {
//						m_lemmatizer.set(possibleLink,new UnspecifiedPartOfSpeech(CanonicalPosTag.NOUN));
//						m_lemmatizer.process();
//						possibleLinkLemma=m_lemmatizer.getLemma();
//					} catch (LemmatizerException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						possibleLinkLemma=possibleLink;
//					}
//					
//					if (UtilClass.isValidRule(srcLemma, possibleLinkLemma))
//					{
//						LexicalRule<RuleInfo> rule = new LexicalRule<RuleInfo>(srcLemma.toLowerCase(),
//								m_nounPOS, //
//								possibleLinkLemma.toLowerCase(),
//								m_nounPOS,
//								this.getRelationType().toString(),
//								Resource.Wiki.toString(),
//								new LinkRuleInfo(pageID,pageTitle));
//						rules.add(rule);
//					}
//						
//					
//				}
//			}
//			
//		} catch (WikiTitleParsingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (LexicalResourceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//
//		return rules;
	}
	public RelationType getRelationType() {
		// TODO Auto-generated method stub
		return RelationType.Link;
	}
}
