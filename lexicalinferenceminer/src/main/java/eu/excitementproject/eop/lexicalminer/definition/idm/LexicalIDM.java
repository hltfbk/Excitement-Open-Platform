
package eu.excitementproject.eop.lexicalminer.definition.idm;

import java.io.FileNotFoundException;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TokenizerException;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.lap.biu.postagger.PosTagger;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggerException;
import eu.excitementproject.eop.lexicalminer.definition.Common.RelationType;
import eu.excitementproject.eop.lexicalminer.definition.Common.Resource;
import eu.excitementproject.eop.lexicalminer.definition.Common.StopwordsDictionary;
import eu.excitementproject.eop.lexicalminer.definition.Common.UtilClass;
import eu.excitementproject.eop.lexicalminer.definition.Common.PatternRuleInfo.LexicalPatternRuleInfo;
import eu.excitementproject.eop.lexicalminer.instrumentscombination.InstrumentCombinationException;
import eu.excitementproject.eop.lexicalminer.instrumentscombination.LexicalSentenceProcessor;
import eu.excitementproject.eop.lexicalminer.instrumentscombination.TokenInfo;
import eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.InitException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;



/**
 * Inference from Definition Module (IDM) which extracts inference 
 * rules connecting the term to be defined, the <i>definiendum</i>, with terms
 * of the definition.   
 * 
 * This IDM considers the path connecting rule sides. A path is represented
 * as a String, possibly a regular expression.   
 * 
 * @author Eyal Shnarch
 * @since 12/04/12
 *
 */
public class LexicalIDM implements IIDM{
	
	

	protected Tokenizer m_tokenizer;
	protected PosTagger m_postagger;
	protected Lemmatizer m_lemmatizer;
	protected PartOfSpeech m_nounPOS;
	private Logger m_logger;
	

	
	public LexicalIDM(Tokenizer tokenizer, PosTagger postagger,	
			Lemmatizer lemmatizer) throws InstrumentCombinationException, TokenizerException, PosTaggerException, LemmatizerException, UnsupportedPosTagStringException {
		
		
		m_tokenizer = tokenizer;
		m_tokenizer.init(); // we must call init before using the tokenizer
		m_postagger = postagger;
		m_postagger.init();// we must call init before using the tagger (process function)
		m_lemmatizer = lemmatizer;
		m_lemmatizer.init();//we must call it?
		
		m_nounPOS = new ByCanonicalPartOfSpeech(CanonicalPosTag.N.name());
		
		m_lexicalSentenceProcessor=new LexicalSentenceProcessor(tokenizer, postagger, lemmatizer, null);
		
	}

	public LexicalIDM(LexicalSentenceProcessor sentPoc) throws UnsupportedPosTagStringException {
		m_nounPOS = new ByCanonicalPartOfSpeech(CanonicalPosTag.N.name());
		m_lexicalSentenceProcessor = sentPoc;
	}

	protected LexicalSentenceProcessor m_lexicalSentenceProcessor;
	
	
	/*
	 * Simple classification of the sentence by adding all pair of nouns found in it
	 */
	
	public List<LexicalRule<RuleInfo>> retrieveSentenceLexicalRules(String sentence, String title, int sourceId) throws FileNotFoundException, SQLException, InitException, ConfigurationException
	{
		List<LexicalRule<RuleInfo>> inferences=new ArrayList<LexicalRule<RuleInfo>>();
		
		if (StopwordsDictionary.getInstance().isStopWord(title)) // avoid unnecessary computations if the title is stopword.
			return inferences; // empty
		
		try {

		    sentence = sentence.toLowerCase();
			List<TokenInfo> processResults = m_lexicalSentenceProcessor.process(sentence);

			for (int i=0;i<processResults.size();i++)
			{	

				UtilClass.getInstance();
				if (UtilClass.isANoun(processResults.get(i).getPosTag()))
				{
					
					UtilClass.getInstance();
					if (UtilClass.isValidRule(title, processResults.get(i).getLemma()))
					{
						StringBuilder sbWords=new StringBuilder();
						StringBuilder sbPos=new StringBuilder();
						StringBuilder sbFull=new StringBuilder();
						for (int k=0;k<i;k++) // create the rule
						{
							String lemma=processResults.get(k).getLemma();
							String pos=processResults.get(k).getPosTag().getStringRepresentation();
							sbWords.append(lemma);
							sbPos.append(pos);
							sbFull.append(String.format("%s:%s", lemma,pos));
							
							if (k+1!=i)
							{
								sbWords.append(">");
								sbPos.append(">");
								sbFull.append(">");
							}
						}

						
							try {
								
								LexicalRule<RuleInfo> rule = new 
								LexicalRule<RuleInfo>(title.toLowerCase(),
										m_nounPOS,
										processResults.get(i).getLemma().toLowerCase(),
										processResults.get(i).getPosTag(),
										this.getRelationType().toString(),
										Resource.Wiki.toString(),
										new LexicalPatternRuleInfo(sbWords.toString(),sbPos.toString(),sbFull.toString(),sourceId));
								inferences.add(rule);
							
							} catch (LexicalResourceException e) {
								m_logger.warn("Lexical resource exception in SyntacticIDM",e);
							} 
						
					}
					
				}				
			}
			return inferences;
			
		} catch (InstrumentCombinationException e) {
			e.printStackTrace();
			return null;
		}
	}


	public RelationType getRelationType() {
		return RelationType.LexicalIDM;
	}
	
	
	
}
