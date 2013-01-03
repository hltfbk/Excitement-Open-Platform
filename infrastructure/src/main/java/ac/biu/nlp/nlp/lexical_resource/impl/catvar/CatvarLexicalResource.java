package ac.biu.nlp.nlp.lexical_resource.impl.catvar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.general.DummyList;
import ac.biu.nlp.nlp.general.configuration.ConfigurationException;
import ac.biu.nlp.nlp.general.configuration.ConfigurationParams;
import ac.biu.nlp.nlp.lexical_resource.EmptyRuleInfo;
import ac.biu.nlp.nlp.lexical_resource.LexicalResourceException;
import ac.biu.nlp.nlp.lexical_resource.LexicalResourceNothingToClose;
import ac.biu.nlp.nlp.lexical_resource.LexicalRule;
import ac.biu.nlp.nlp.lexical_resource.impl.catvar.EquivalenceClasses.EquivalenceClassesException;
import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.PartOfSpeech;
import ac.biu.nlp.nlp.representation.UnspecifiedPartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;

/**
 * Wraps CatVar lexical resource.
 * The file with the rules is "catvar21", in our SVN: trunk/common/data/CatVar 
 * This class loads all the rules in advance into local memory.
 * <P>
 * Note - the interpretation here for the input is input of words, not lemmas.
 * <BR>
 * See: CatVar: A Database of Categorial Variations for English, Nizar Habash and Bonnie Dorr
 * 
 * @author Asher Stern
 * @since Feb 27, 2012
 *
 */
public class CatvarLexicalResource extends LexicalResourceNothingToClose<EmptyRuleInfo>
{
	public static final String SEPARATOR = "#";
	public static final String POS_SEPARATOR = "_";
	public static final String PARAM_CATVAR_FILE_NAME = "catver-file-name";
	
	public static final String RESOURCE_NAME = "CatVar";
	
	
	/**
	 * File should be "catvar21"
	 * 
	 * @param catvarFile should be "catvar21"
	 * @throws LexicalResourceException
	 */
	public CatvarLexicalResource(File catvarFile) throws LexicalResourceException
	{
		loadAllRules(catvarFile);
	}
	
	public CatvarLexicalResource(ConfigurationParams params) throws LexicalResourceException, ConfigurationException
	{
		this(params.getFile(PARAM_CATVAR_FILE_NAME));
	}


	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.lexical_resource.LexicalResource#getRulesForRight(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	public List<LexicalRule<? extends EmptyRuleInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException
	{
		return getRules(lemma,pos,true);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.lexical_resource.LexicalResource#getRulesForLeft(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	public List<LexicalRule<? extends EmptyRuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException
	{
		return getRules(lemma, pos,false);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.lexical_resource.LexicalResource#getRules(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech, java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	public List<LexicalRule<? extends EmptyRuleInfo>> getRules(
			String leftLemma, PartOfSpeech leftPos, String rightLemma,
			PartOfSpeech rightPos) throws LexicalResourceException
			{
		try
		{
			List<LexicalRule<? extends EmptyRuleInfo>> ret = null;
			WordAndPartOfSpeech waposLeft = new WordAndPartOfSpeech(leftLemma, leftPos);
			WordAndPartOfSpeech waposRight = new WordAndPartOfSpeech(rightLemma, rightPos);
			Set<WordAndPartOfSpeech> rulesForLeft = allCatvarRules.getClassOf(waposLeft);
			if (rulesForLeft!=null)
			{
				if (rulesForLeft.contains(waposRight))
				{
					ret = Collections.<LexicalRule<? extends EmptyRuleInfo>>singletonList(new LexicalRule<EmptyRuleInfo>(leftLemma,leftPos,rightLemma,rightPos,RESOURCE_NAME,RESOURCE_NAME,EmptyRuleInfo.getInstance()));
				}
			}
			if (null==ret)
			{
				ret = dummyList;
			}
			return ret;
		}
		catch (UnsupportedPosTagStringException e)
		{
			throw new LexicalResourceException("Unexpected problem.",e);
		}
		catch (EquivalenceClassesException e)
		{
			throw new LexicalResourceException("Unexpected problem.",e);
		}
			}
	
	protected List<LexicalRule<? extends EmptyRuleInfo>> getRules(String lemma, PartOfSpeech pos, boolean right) throws LexicalResourceException
	{
		try
		{
			WordAndPartOfSpeech wordAndPartOfSpeech = new WordAndPartOfSpeech(lemma.toLowerCase(), pos);	//since all words in the file are in lower case
			Set<WordAndPartOfSpeech> rulesAsWapos = allCatvarRules.getClassOf(wordAndPartOfSpeech);
			List<LexicalRule<? extends EmptyRuleInfo>> ret =null;
			if (null==rulesAsWapos)
			{
				ret = dummyList;
			}
			else
			{
				ret = new ArrayList<LexicalRule<? extends EmptyRuleInfo>>(rulesAsWapos.size());
				if (right)
				{
					for (WordAndPartOfSpeech wapos : rulesAsWapos)
					{
						if (!wapos.equals(wordAndPartOfSpeech))
						{
							ret.add(new LexicalRule<EmptyRuleInfo>(wapos.getWord(),wapos.getPos(),lemma,pos,RESOURCE_NAME,RESOURCE_NAME,EmptyRuleInfo.getInstance()));
						}
					}
				}
				else
				{
					for (WordAndPartOfSpeech wapos : rulesAsWapos)
					{
						if (!wapos.equals(wordAndPartOfSpeech))
						{
							ret.add(new LexicalRule<EmptyRuleInfo>(lemma,pos,wapos.getWord(),wapos.getPos(),RESOURCE_NAME,RESOURCE_NAME,EmptyRuleInfo.getInstance()));
						}
					}
				}

			}
			return ret;
		}
		catch (UnsupportedPosTagStringException e)
		{
			throw new LexicalResourceException("Unexpected problem.",e);
		}
		catch (EquivalenceClassesException e)
		{
			throw new LexicalResourceException("Unexpected problem.",e);
		}
	}


	
	protected static Map<String, PartOfSpeech> posMap = null;
	static
	{
		try
		{
			posMap = new LinkedHashMap<String, PartOfSpeech>();
			posMap.put("N",new UnspecifiedPartOfSpeech(CanonicalPosTag.NOUN));
			posMap.put("V",new UnspecifiedPartOfSpeech(CanonicalPosTag.VERB));
			posMap.put("AJ",new UnspecifiedPartOfSpeech(CanonicalPosTag.ADJECTIVE));
			posMap.put("AV",new UnspecifiedPartOfSpeech(CanonicalPosTag.ADVERB));
		}
		catch(UnsupportedPosTagStringException e)
		{
			posMap = null;
		}
	}

	protected void loadAllRules(File catvarFile) throws LexicalResourceException
	{
		try
		{
			if (null==posMap) throw new LexicalResourceException("posMap is null");
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(catvarFile)));
			try
			{
				String line = reader.readLine();
				while (line != null)
				{
					line = line.trim();
					if (line.length()>0)
					{
						String[] elements = line.split(SEPARATOR);
						if (elements.length>1)
						{
							Set<WordAndPartOfSpeech> wordsInCategory = new LinkedHashSet<WordAndPartOfSpeech>();
							for (int index=0;index<elements.length;++index)
							{
								String element = elements[index];
								int indexOfPosSep = element.lastIndexOf(POS_SEPARATOR);
								String stringPos = element.substring(indexOfPosSep+1);
								PartOfSpeech partOfSpeech = posMap.get(stringPos);
								if (null==partOfSpeech) throw new LexicalResourceException("Could not understand part of speech: \""+stringPos+"\"");
								String word = element.substring(0, indexOfPosSep);
								WordAndPartOfSpeech wapos = new WordAndPartOfSpeech(word,partOfSpeech);
								wordsInCategory.add(wapos);
							}
							allCatvarRules.addEquivalenceClass(wordsInCategory);
						}


					}
					line = reader.readLine();
				}

			}
			finally
			{
				reader.close();
			}
		}
		catch (IOException e)
		{
			throw new LexicalResourceException("Failed to read catvar file.",e);
		}
		catch (UnsupportedPosTagStringException e)
		{
			throw new LexicalResourceException("Failed to read catvar file.",e);
		}
		catch (EquivalenceClassesException e)
		{
			throw new LexicalResourceException("Failed to build CatVar resource.",e);
		}


	}
	

	protected EquivalenceClasses<WordAndPartOfSpeech> allCatvarRules = new EquivalenceClasses<WordAndPartOfSpeech>(true);
	private static final  DummyList<LexicalRule<? extends EmptyRuleInfo>> dummyList = new DummyList<LexicalRule<? extends EmptyRuleInfo>>();
}
