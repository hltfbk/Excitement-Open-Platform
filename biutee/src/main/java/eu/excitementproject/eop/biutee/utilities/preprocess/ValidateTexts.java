package eu.excitementproject.eop.biutee.utilities.preprocess;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.utilities.StringUtil;

/**
 * Compares whether two texts are identical, up to spaces.
 * 
 * @author Asher Stern
 * @since Apr 1, 2014
 *
 */
public class ValidateTexts
{
	public static ValidateTexts createForSentences(String originalText, List<String> sentences)
	{
		return new ValidateTexts(originalText,sentencesToText(sentences));
	}
	
	public static String sentencesToText(List<String> sentences)
	{
		StringBuilder sb = new StringBuilder();
		boolean firstIteration = true;
		for (String sentence : sentences)
		{
			if (firstIteration) {firstIteration=false;}
			else {sb.append(' ');}
			
			sb.append(sentence.trim());
		}
		return sb.toString();		
	}
	
	public static String generateMismatchMarks(List<Integer> listMismaches)
	{
		Set<Integer> setMismatches = new LinkedHashSet<>();
		setMismatches.addAll(listMismaches);
		int max=0;
		for (Integer i : listMismaches)
		{
			if (max<i) {max=i;}
		}
		char[] array = new char[max+1];
		for (int index=0;index<array.length;++index)
		{
			if (setMismatches.contains(index))
			{
				array[index]='^';
			}
			else
			{
				array[index]=' ';
			}
		}
		return new String(array);
	}

	
	public ValidateTexts(String originalText, String generatedText)
	{
		super();
		this.originalText = originalText;
		this.generatedText = generatedText;
	}

	public boolean compare()
	{
		originalText_noDuplicateSpaces = removeDuplicatedSpaces(originalText);
		
		generatedText_noDuplicateSpaces = removeDuplicatedSpaces(generatedText);
		
		mismatches = new LinkedList<>();
		for (int index=0;index<originalText_noDuplicateSpaces.length();++index)
		{
			if (generatedText_noDuplicateSpaces.length()>index)
			{
				if (originalText_noDuplicateSpaces.charAt(index)!=generatedText_noDuplicateSpaces.charAt(index))
				{
					mismatches.add(index);
				}
			}
			else
			{
				mismatches.add(index);
			}
		}
		if (originalText_noDuplicateSpaces.length()<generatedText_noDuplicateSpaces.length())
		{
			for (int i=originalText_noDuplicateSpaces.length();i<generatedText_noDuplicateSpaces.length();++i)
			{
				mismatches.add(i);
			}
		}
		
		return (mismatches.size()==0);
	}
	
	
	
	public String getOriginalText_noDuplicateSpaces()
	{
		return originalText_noDuplicateSpaces;
	}

	public String getGeneratedText_noDuplicateSpaces()
	{
		return generatedText_noDuplicateSpaces;
	}
	
	public List<Integer> getMismatches()
	{
		return mismatches;
	}
	

	private static String removeDuplicatedSpaces(String text)
	{
		char[] array = StringUtil.trimNotInCriterion(text, new StringUtil.CharacterCriterion()
		{
			@Override
			public boolean is(char c){return (!Character.isSpaceChar(c))&&(!Character.isIdentifierIgnorable(c));}
		}).toCharArray();
		char[] removed = new char[array.length+1];
		
		boolean lastWasSpace = false;
		int removedIndex=0;
		for (int index=0;index<array.length;++index)
		{
			char c = array[index];
			if (Character.isWhitespace(c) || Character.isIdentifierIgnorable(c))
			{
				if (!lastWasSpace)
				{
					removed[removedIndex]=' ';
					++removedIndex;
				}
				lastWasSpace=true;
			}
			else
			{
				removed[removedIndex]=c;
				++removedIndex;
				lastWasSpace=false;
			}
		}
		return new String(removed,0,removedIndex);
	}
	
	private final String originalText;
	private final String generatedText;
	
	private String originalText_noDuplicateSpaces = null;
	private String generatedText_noDuplicateSpaces = null;
	
	private List<Integer> mismatches = null;

}
