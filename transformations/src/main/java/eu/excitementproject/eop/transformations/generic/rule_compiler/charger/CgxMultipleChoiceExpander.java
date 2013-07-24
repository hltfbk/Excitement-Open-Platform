/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.charger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;

/**
 * This class gets a text of a Cgx rule file, and "expands" it, that is, for each multiple choice parameter value in the text 
 * (like lemma="lemma1\lemma2\lemma3..."), the method 
 * {@link #expandMultipleChoiceParameters(String, Map)} returns several copies of the text, each copy with the disjointed value replaced with one of its 
 * possible replacements.  
 * @author Amnon Lotan
 *
 * @since 3 Jul 2012
 */
public class CgxMultipleChoiceExpander {

	public static final String OR_CHAR_REAL = "\\";
	
	private static final String OR_REGEX = "\\\\"; 
	private static final String AMP_QUOTE = "&quot;";
	private static final String FEATURE_PREFIX= "=" + AMP_QUOTE;
	private static final String WORD_PATTERN = "(?:\\w|&apos;)+";	// a word is a sequence of letters, and '&apos;'
	/**
	 * matches against any param in the xml that is multiple choice, backslash-separated, like &quot;VBZ\VBN\VB&quot;
	 */
	private static final String VARIED_FEATURE_PATTERN = 
		AMP_QUOTE + "((?:" + WORD_PATTERN + "\\\\)+" + WORD_PATTERN + ")" + AMP_QUOTE;

	
	
	
	/**
	 * Find all multiple option attributes (if exist) in the file (like lemma="lemma1\lemma2\lemma3..."), 
	 * 	and return one doc per full attributes' assignment
	 * 
	 * 1. read the file's text
	 * 2. parse all the nodes out of the xml
	 * 3. after parsing, record all the multiple option parameters (like lemma="lemma1\lemma2\lemma3...")
	 * 4. create a new doc for each full selection of all the multiple option parameters
	 * 
	 * @param cgxText
	 * @param extraMultipleChoiceParameters
	 * @return
	 * @throws CompilationException
	 */
	public static List<String> expandMultipleChoiceParameters(String cgxText, Map<String, Set<String>> extraMultipleChoiceParameters) throws CompilationException 
	{
		// find all the varied params (those with backslashes) in the text
		List<String> featuresToReplace = new Vector<String>();
		Map<String, Set<String>> multipleChoiceFeatures = getMultipleChoiceFeatures( cgxText, featuresToReplace, extraMultipleChoiceParameters);	
		
		// create a new CGX file text for each full selection of all the multiple option parameters
		Queue<String> textsQueue = new LinkedList<String>();
		textsQueue.offer(cgxText);

		for(String featureToReplace : featuresToReplace)
		{
			for (int textsToCook = textsQueue.size(); textsToCook > 0; textsToCook--)
			{
				String currText = textsQueue.poll();
				Matcher m = Pattern.compile(FEATURE_PREFIX + featureToReplace + AMP_QUOTE).matcher(currText);
				if ( m.find() )
					for (String replacement : multipleChoiceFeatures.get(featureToReplace))
					{
						String newText = m.replaceFirst(FEATURE_PREFIX + replacement +  AMP_QUOTE);
						textsQueue.offer(newText);
					}
				else
					textsQueue.offer(currText);
			}			
		}

		return new ArrayList<String>(textsQueue);		
	}
	

	/**
	 * find all the multiple choice params (those with backslashes) in the given CGX text, and return a map from each one, to all the single values that 
	 * should replace it.
	 * 
	 * @param text
	 * @param featuresToReplace	id populated with the list of multiple choice params to be replaced in the text. It accompanies the returned map. 
	 * It's important cos it's a list that tells the 
	 * order (inc. repetitions) of the variedFeatures to be replaced in the text
	 * @param predicateLists array of the lemmas of all predicates in the single word predicates' lexicon. To be added to the <code>featuresToReplace</code>
	 * @return
	 * @throws CompilationException
	 */
	private static Map<String, Set<String>> getMultipleChoiceFeatures(String text, List<String> featuresToReplace, Map<String, Set<String>> extraMultipleChoiceParameters ) 
		throws CompilationException
	{
		featuresToReplace.clear();
		Map<String, Set<String>> multipleChoiceFeatures = new HashMap<String, Set<String>>();	
		Pattern p = Pattern.compile(VARIED_FEATURE_PATTERN);
		java.util.regex.Matcher m = p.matcher(text);
		while ( m.find())
		{
			String toReplace = m.group(1).replace(OR_CHAR_REAL, OR_REGEX);
			if (!multipleChoiceFeatures.containsKey(toReplace))
				multipleChoiceFeatures.put(toReplace, getOptions(m.group(1)));
			featuresToReplace.add(toReplace);
		}	
		
		// now add the predicate type keys and their matching lists, from the predicate type lists file
		if (extraMultipleChoiceParameters != null)
			for (String labelToExpand : extraMultipleChoiceParameters.keySet())
			{
				Set<String> expansionStrings = extraMultipleChoiceParameters.get(labelToExpand);
				multipleChoiceFeatures.put(labelToExpand, expansionStrings);
				featuresToReplace.add(labelToExpand);
			}
		
		return multipleChoiceFeatures;
	}

	private  static Set<String> getOptions(String variedParam) 
	{
		return variedParam != null ? 
				Utils.arrayToCollection(variedParam.split(OR_REGEX), new LinkedHashSet<String>()) : 
				null;
	}	
}
