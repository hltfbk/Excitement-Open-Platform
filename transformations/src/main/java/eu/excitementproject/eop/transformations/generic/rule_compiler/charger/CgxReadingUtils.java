package eu.excitementproject.eop.transformations.generic.rule_compiler.charger;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.StanfordDependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.SyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.StanfordDependencyRelation.StanfordDependencyException;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.representation.partofspeech.WildcardPartOfSpeech;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.RuleType;
import eu.excitementproject.eop.transformations.representation.ExtendedMatchCriteria;

/**
 * This class contains some static generic utilities for reading parameter values in CGX rule files
 * @author Amnon Lotan
 *
 * @since Jul 5, 2012
 */
public class CgxReadingUtils {
	
	/////////////////////////////////////////////// CONSTANTS //////////////////////////////////////////////////////
	private static final String EQUALS = "=";
	private static final String QUOTE = "\"";
	/**
	 * The regex with which we find parameter names in a label
	 */
	private static final String PARAM_NAME_PATTERN = "([a-zA-Z]+)" + EQUALS + QUOTE;

	/////////////////////////////////////////////// PUBLIC //////////////////////////////////////////////////////

	/**
	 * return the string parameter pointed at by paramName. If there is no 'paramName' in 'label', return null. 
	 * 
	 * @param label
	 * @param paramName
	 * @return
	 * @throws CompilationException
	 */
	public static String readStringParam(String label, String paramName) throws CompilationException 
	{
		paramName += EQUALS + QUOTE;
		String ret = null;
		
		int from = label.indexOf(paramName, 0);
		if (from >= 0)
		{
			from += paramName.length();
			try {
				ret = label.substring(from, label.indexOf(QUOTE, from));
			} catch (Exception e) {
				throw new CompilationException("Unknown error. Index from=" + from + " out of bounds for label=" + label);
			}
		}
		return ret;
	}
		
	/**
	 * Make sure the label doesn't contain any unrecognized param names
	 * @param label
	 * @param paramNames	set of all supported parameter names
	 * @throws CompilationException 
	 */
	public static void sanityCheckLabel(String label, Set<String> paramNames) throws CompilationException 
	{
		Matcher m = Pattern.compile(PARAM_NAME_PATTERN).matcher(label);
		while (m.find())
			if (!paramNames.contains(m.group(1)))
				throw new CompilationException(m.group(1) + " is not a valid parameter name to write inside this node");
		
		/* TODO go over all rules to replace all non-node-Concepts with Relations. Then you know whatever label gets its ass here is a real 
		 	node's label. So write some real marshaling code for it, to check that it's a non-empty sequence of 'param="value" ' tokens (or
		 	RHS or LHS)
		 	*/
	}

	/**
	 * return the rule type of the given cgx rule text. Sanity check it too.
	 * @param ruleText
	 * @return
	 * @throws CompilationException 
	 */
	public static RuleType readCgxRuleType(String ruleText) throws CompilationException {
	
		Matcher m = Pattern.compile(RULE_TYPE_PATTERN).matcher(ruleText);
		if ( !m.find() )
			throw new CompilationException("No \"ruleType=\" Concept was found");
	
		String ruleTypeStr = m.group(1);
		RuleType ruleType;
		try {	
			ruleType = RuleType.valueOf(ruleTypeStr.toUpperCase());
		} catch (Exception e) {
			throw new CompilationException(ruleTypeStr + " is not a valid ruleType value. \nOnly these "+RuleType.PRINTED_VALUES+" are allowed");
		}
		if (m.find())
			throw new CompilationException("Two \"ruleType=\" labels were found. Only one is allowed");
		return ruleType;
	}

	private static final String RULE_TYPE_PATTERN = "<label>ruleType=(\\w+)</label>";

	/**
	 * Get a string POS, and return it in a {@link SyntacticInfo} object. If possible, it uses {@link PennPartOfSpeech} instead of 
	 * {@link UnspecifiedPartOfSpeech}. 
	 * @param partOfSpeech string representation
	 * @return SyntacticInfo object made out of the string
	 * @throws CompilationException 
	 */
	public static SyntacticInfo stringToSyntacticInfo(String partOfSpeech) throws CompilationException 
	{
		SyntacticInfo syntacticInfo = null;
		if (partOfSpeech != null)
		{
			PartOfSpeech pos;
			if (partOfSpeech.equals(WildcardPartOfSpeech.WILDCARD_POS_STR))
				pos = WildcardPartOfSpeech.getWildcardPOS();
			else
			{
				partOfSpeech = partOfSpeech.toUpperCase();
				if (BySimplerCanonicalPartOfSpeech.SIMPLER_CANONICAL_POS_TAG_STRINGS.contains(partOfSpeech))
					try 	{ pos = new BySimplerCanonicalPartOfSpeech(partOfSpeech);	} 
					catch (UnsupportedPosTagStringException e) { throw new CompilationException("Error reading this part of speech: " + partOfSpeech, e);	}
				else 
					try		{	pos = new PennPartOfSpeech(partOfSpeech);	}  
					catch 	(	UnsupportedPosTagStringException e) { throw new CompilationException("Error reading this part of speech: " + partOfSpeech +
							". It's probably neigher a canonical POS nor a Penn POS, and should be conformed to one of them.", e);	}	
			}
			syntacticInfo = new DefaultSyntacticInfo(pos);
		}
		return syntacticInfo;
	}

	/**
	 * @param relation
	 * @param object
	 * @return
	 * @throws CompilationException 
	 */
	public static DependencyRelation newDependencyRelation(String relation) throws CompilationException {
		if (relation.equals(ExtendedMatchCriteria.WILDCARD_RELATION))	// accept the wildcard
			return new DependencyRelation(ExtendedMatchCriteria.WILDCARD_RELATION, null);
		try {
			return new StanfordDependencyRelation(relation);
		} catch (StanfordDependencyException e) {
			throw new CompilationException("see nested", e);
		}
	}
}
