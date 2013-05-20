package eu.excitementproject.eop.common.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * A collection of string utility functions.
 * 
 * @author Asher Stern & other people in the lab.
 *
 */
public final class StringUtil
{

	////////////////////// PUBLIC PART ////////////////////////////
	
	
	public static final String PATTERN_ENVIRONMENT_VARIABLES_WINDOWS = "%(\\w+)%"; 
	public static final String PATTERN_ENVIRONMENT_VARIABLES_LONG_UNIX = "\\$\\{(\\w+)\\}";
	public static final String PATTERN_ENVIRONMENT_VARIABLES_SHORT_UNIX = "\\$(\\w+)";
	public static final List<Pattern> PATTERNS_ENVIRONMENT_VARIABLES;
	static
	{
		PATTERNS_ENVIRONMENT_VARIABLES = new LinkedList<Pattern>();
		PATTERNS_ENVIRONMENT_VARIABLES.add(Pattern.compile(PATTERN_ENVIRONMENT_VARIABLES_LONG_UNIX));
		PATTERNS_ENVIRONMENT_VARIABLES.add(Pattern.compile(PATTERN_ENVIRONMENT_VARIABLES_SHORT_UNIX));
		PATTERNS_ENVIRONMENT_VARIABLES.add(Pattern.compile(PATTERN_ENVIRONMENT_VARIABLES_WINDOWS));
	}
	
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// PLEASE DO NOT CHANGE THIS!!!!!!!! (Used for ArkRef)
	private static final Set<Character> MAY_END_SENTENCE_CHARS = Utils.arrayToCollection(new Character[]{'.','!','?',';'}, new HashSet<Character>());
	
	
	/**
	 * Converts the string to a string that can be printed
	 * into a source code file.
	 * <P>
	 * For example, if we have a source code <code>System.out.println("</code>,
	 * we can use the output of this function and print into that code, then
	 * appending the code with <code>");</code>
	 * Later - we can run that code, and the original string will be printed.
	 *  
	 * @param str - a string to be converted
	 * @return the converted string (a "source code" C string).
	 * @throws StringUtilException if str is null
	 */
	public static String convertStringToCString(String str) throws StringUtilException
	{		
		if (str == null)
			throw new StringUtilException("input is null");
		
		StringBuffer stringBuffer = new StringBuffer();
		for (int index=0;index<str.length();++index)
		{
			char currentChar = str.charAt(index);
			String escapeSequenceRepresentation = getStringOfEscapeSequenceChar(currentChar);
			if (escapeSequenceRepresentation==null)
				stringBuffer.append(currentChar);
			else
				stringBuffer.append(escapeSequenceRepresentation);
		}
		return stringBuffer.toString();
	}
	
	/**
	 * Returns <tt>true</tt> if the given string contains only letters and/or digits. 
	 * @param str
	 * @return
	 */
	public static boolean hasOnlyLetterOrDigit(String str)
	{
		boolean ret = true;
		if (null==str) 
		{ // do nothing
		}  
		else
		{
			char[] asCharArray = str.toCharArray();
			if (asCharArray!=null)
			{
				for (char c : asCharArray)
				{
					if (Character.isLetterOrDigit(c)) 
					{ // do nothing
					}  
					else
						ret = false;
					
				}
			}
		}
		return ret;
	}

	/**
	 * Returns <tt>true</tt> if the string does not contain any letter or digit.
	 * @param str
	 * @return
	 */
	public static boolean hasNeitherLetterNorDigit(String str)
	{
		boolean ret = true;
		char[] charArray = str.toCharArray();
		for (char c : charArray)
		{
			if (Character.isLetterOrDigit(c))
			{
				ret = false;
				break;
			}
		}
		return ret;
	}
	
	/**
	 * Splits the given string into words.
	 * A "word" is defined as a sequence of characters that each of them
	 * is either letter or digit.
	 * @param str
	 * @return
	 */
	public static List<String> stringToWords(String str)
	{
		List<String> ret = new LinkedList<String>();
		if (str!=null)
		{
			StringBuffer buffer = new StringBuffer(str.length());
			for (int index=0;index<str.length();++index)
			{
				char ch = str.charAt(index);
				if (Character.isLetterOrDigit(ch))
					buffer.append(ch);
				else
				{
					if (buffer.length()>0)
						ret.add(buffer.toString());
					
					buffer.setLength(0);
				}
			}
			if (buffer.length()>0)
				ret.add(buffer.toString());
			
		}
		return ret;
	}

	/**
	 * Returns <tt>true</tt> if the lists are equal, ignoring the case.
	 * <BR>
	 * I.e. if for each index <code>i</code>, item <code>#i</code> in <code>list1</code>
	 * is equals, ignoring case, to item <code>#i</code> in <code>list2</code>.
	 * @param list1
	 * @param list2
	 * @return <tt>true</tt> if the lists are equal, ignoring the case.
	 */
	public static boolean listsEqualIgnoreCase(List<String> list1, List<String> list2)
	{
		boolean ret = true;
		if (list1==list2)
			ret = true;
		else
		{
			if (null==list1)
			{
				if (null==list2)
					ret = true;
				else
					ret = false;
			}
			else
			{
				if (null==list2)
					ret = false;
				else
				{
					// both lists are not null.
					if (list1.size()==list2.size())
					{
						ret = true;
						Iterator<String> iter1 = list1.iterator();
						Iterator<String> iter2 = list2.iterator();
						while (iter1.hasNext())
						{
							String str1 = iter1.next();
							String str2 = iter2.next();
							if (str1.trim().equalsIgnoreCase(str2.trim())) 
							{ // do nothing
							}  
							else
							{
								ret = false;
								break;
							}
						}
						
					}
					else
					{
						ret = false;
					}
				}
			}
					
		}
		return ret;
	}

	/**
	 * Returns <tt>true</tt> if the Set contains the string, ignoring case, 
	 * if any parameter is null, <tt>false</tt> is returned 
	 * @param set
	 * @param str
	 * @return Returns <tt>true</tt> if the Set contains the string, ignoring case,
	 * if any parameter is null, <tt>false</tt> is returned 
	 */
	public static boolean setContainsIgnoreCase(Iterable<String> set, String str)
	{
		boolean found = false;
		if (set != null && str != null)
		{
			Iterator<String> iter = set.iterator();
			while ( (!found) && (iter.hasNext()) )
			{
				String stringInSet = iter.next();
				if (str.equalsIgnoreCase(stringInSet))
					found = true;
			}
		}
		return found;
	}
	
	public static Set<String> intersectionIgnoreCase(Iterable<String> set1, Iterable<String> set2)
	{
		Set<String> ret = new LinkedHashSet<String>();
		for (String str1 : set1)
		{
			for (String str2 : set2)
			{
				if (str1.equalsIgnoreCase(str2))
				{
					ret.add(str1);
				}
			}
		}
		return ret;
		
	}
	
	/**
	 * Removes leading and trailing non-letter characters.
	 * @param str
	 * @return
	 */
	public static String trimNonLetters(String str)
	{
		return trimNotInCriterion(str,letterCharacterCriterion);
	}

	/**
	 * Removes leading and trailing non-letters and non-digits characters.<P>
	 * For example: for input "!2abc* " it returns "2abc"
	 * @param str
	 * @return
	 */
	public static String trimNeitherLettersNorDigits(String str)
	{
		return trimNotInCriterion(str,letterOrDigitCharacterCriterion);
	}

	/**
	 * An interface for {@link StringUtil#trimNotInCriterion(String, CharacterCriterion)}
	 * @author Asher Stern
	 */
	public static interface CharacterCriterion
	{
		public boolean is(char c);
	}

	/**
	 * Trims leading and trailing characters according to the given CharacterCriterion
	 * This method is used as the actual implementation of the methods
	 * {@link #trimNonLetters(String)} and {@link #trimNeitherLettersNorDigits(String)}.
	 * @param str
	 * @param criterion
	 * @return
	 */
	public static String trimNotInCriterion(String str, final CharacterCriterion criterion)
	{
		if (null==str) return str;
		char[] charArray = str.toCharArray();
		int startIndex = 0;
		while ( (startIndex<charArray.length) && (!criterion.is(charArray[startIndex])) )
		{
			startIndex++;
		}
		int endIndex = charArray.length-1;
		while ( (endIndex>=0) && (!criterion.is(charArray[endIndex])) )
		{
			--endIndex;
		}
		if (startIndex<=endIndex)
		{
			if ( (startIndex==0) && (endIndex==(charArray.length-1)) )
			{
				return str;
			}
			else
			{
				return str.substring(startIndex, endIndex+1);
			}
		}
		else
		{
			return "";
		}
	}

	/**
	 * Given a string, splits it to several lines, each no longer than the given
	 * maximum.
	 * @param line
	 * @param maxCharsInLine
	 * @return
	 */
	public static String splitLongLine(String line, int maxCharsInLine)
	{
		StringBuffer buffer = new StringBuffer();
		StringTokenizer stringTokenizer = new StringTokenizer(line);
		if (line!=null)
		{
			int oneLineIndex=0;
			while (stringTokenizer.hasMoreTokens())
			{
				String token = stringTokenizer.nextToken();
				if (oneLineIndex!=0)
				{
					buffer.append(" ");
					oneLineIndex++;
				}
				if ( (oneLineIndex+token.length()) <= maxCharsInLine) 
				{ // do nothing
				}  
				else
				{
					buffer.append("\n");
					oneLineIndex=0;
				}
				buffer.append(token);
				oneLineIndex+=token.length();
			}
		}
		return buffer.toString();
	}
	
	
	/**
	 * Given a string that contains an environment variable, this function returns the same
	 * string but with the environment variable replaced by its value.
	 * For example "abc%PATH%def" will return "abcC:\Windows;C:\Windows\System32def" (assuming
	 * PATH is set to "C:\Windows;C:\Windows\System32").
	 * 
	 * The environment variables are specified as %VARNAME% or ${VARNAME} or $VARNAME. All three
	 * types of specifications are valid, independent of operating system. 
	 * 
	 * @param str A string that may contain environment variables like $PATH or %PATH%
	 * 
	 * @return The given string but with the variables replaced by their values.
	 */
	public static String expandEnvironmentVariables(String str)
	{
		Map<String, String> envVarsMap = System.getenv();
		List<Pattern> patterns = PATTERNS_ENVIRONMENT_VARIABLES;
		for (Pattern pattern : patterns)
		{
			StringBuffer sb = new StringBuffer();
			Matcher matcher = pattern.matcher(str);
			int nextAppendIndex=0;
			while (matcher.find())
			{
				String replacement = matcher.group(0);
				if (matcher.groupCount()>=1)
				{
					if (matcher.group(1)!=null)
					{
						if (envVarsMap.get(matcher.group(1))!=null)
						{
							replacement = envVarsMap.get(matcher.group(1));
						}
					}
				}
				sb.append(str.substring(nextAppendIndex, matcher.start()));
				sb.append(replacement);
				nextAppendIndex=matcher.end();
			}
			sb.append(str.substring(nextAppendIndex, str.length()));
			
			str = sb.toString();
		} // end of for loop.
		
		return str;
	}
	

	/**
	 * gets a string and a term and returns an int array containing the indexes
	 * of the term in the string
	 * 
	 * @param str
	 * @param term
	 * @return Returns an array consisting of all the indexes of the term in the
	 * string
	 * @throws StringUtilException 
	 */
	public static int[] allIndexesOf(String str, String term) throws StringUtilException {	
		return allIndexesOf(str,term,0,str.length());
	}

	/**
	* gets a string and a term, and a range within the string, and returns an int array containing the indexes
	* of the term in the string
	*
	* @param str
	* @param term
	* @param iStart
	* @param iEnd
	* @return Returns an array consisting of all the indexes of the term in the
	* string
	* @throws StringUtilException 
	*/
	public static int[] allIndexesOf(String str, String term, int iStart,
			int iEnd) throws StringUtilException 
	{
		if (str == null)
			throw new StringUtilException("the string is null");
		
		if (iStart < 0 || iStart > str.length() || iEnd < 0
				|| iEnd > str.length() || iEnd < iStart)
			throw new StringUtilException("Illegal start/end value "
					+ iStart + "," + iEnd);
	
		Vector<Integer> list = new Vector<Integer>();
		int start = iStart;	//starting from a certain location
	
		while (start < iEnd) {
			int i = str.indexOf(term, start);
			if (i == -1 || i >=iEnd)
				break;	// finished searching the string
	
			start = i + 1;
			list.addElement(new Integer(i));
		}
	
		// copy the list into an array
		int[] arrIndexes = new int[list.size()];
		for (int i = 0; i < arrIndexes.length; i++) {
			arrIndexes[i] = list.elementAt(i).intValue();
		}
	
		return arrIndexes;
	}

	/**
	 * check if a string is included in the array
	 * index numbers follow the convention of substring
	 * @param arr
	 * @param str
	 * @param isCaseSensitive
	 * @return true if str isn't null and is found in arr, false otherwise
	 */
	public static boolean arrayContainsString(String[] arr, String str,	boolean isCaseSensitive)
	{		
		boolean found = false;
		if (arr != null)
			for (int i = 0; i < arr.length && !found; i++) {
				if (arr[i].equals(str)
						|| (!isCaseSensitive && arr[i].equalsIgnoreCase(str)))
					found = true;
			}
		return found;
	}

	/**
	 * replace all spaces with underscores
	 * @param phrase
	 * @return
	 */
	public static String avoidSpacesInPhrase(String phrase) 
	{	
		String ret = null;
		
		if (phrase != null)
		{			
			StringTokenizer st = new StringTokenizer(phrase);
			StringBuffer sb = new StringBuffer();
			while(st.hasMoreTokens()) {
				sb.append(st.nextToken());
				sb.append(UNDERSCORE);
			}
			ret = sb.substring(0,sb.length()-1);
		}
		
		return ret;
	}

	/**
	 * removes non alpha numeric characters from the beginnings and ends of all words in inputLine
	 * @param inputLine
	 * @return inputLine without non alpha numeric characters at the beginnings and ends of words
	 */
	public static String cleanString(String inputLine)
	{
		String ret = null;
		
		if (inputLine != null)
		{			
			StringBuffer toReturn = new StringBuffer();
			for(String word : inputLine.split(SPLIT_STR)){
				toReturn.append(word.replaceAll(REPLACE_WHAT1, "").replaceAll(REPLACE_WHAT2, "") + " ");
			}
			ret = toReturn.deleteCharAt(toReturn.length()-1).toString();
		}
		return ret;
	}


	/**
	 * count the occurrences of string in the array
	 * @param arr
	 * @param str
	 * @param isCaseSensitive
	 * @return number of occurrences of the string in the array. if input is null, 0 is returned
	 */
	public static int countStringOccurs(String[] arr, String str, boolean isCaseSensitive)
	{
		int count = 0;
		if (arr != null && str != null)
			for (int i = 0; i < arr.length; i++) {
				if (arr[i].equals(str)
						|| (!isCaseSensitive && arr[i].equalsIgnoreCase(str)))
					count++;
			}
		return count;
	}


	/**
	 * converting regular string to html-escaped string - just some of the chars are here
	 * @param inputStr
	 * @return html-escaped string
	 */
	public static String escapeHTML(String inputStr)
	{
		String ret = null;
		
		if (inputStr != null)
		{			
			StringBuffer sb = new StringBuffer();
			int n = inputStr.length();
			for (int i = 0; i < n; i++) {
				char c = inputStr.charAt(i);
				switch (c) {
				case '<': sb.append("&lt;"); break;
				case '>': sb.append("&gt;"); break;
				case '&': sb.append("&amp;"); break;
				case '"': sb.append("&quot;"); break;
				// be careful with this one (non-breaking white space)
				case ' ': sb.append("&nbsp;");break;
				case '\n': sb.append("<BR>");break;
	
				default:  sb.append(c); break;
				}
			}
			ret = sb.toString();
		}
		return ret;
	}


	/**
	 * @author: Shachar
	 * Gets a source string and a destination string and makes the destination
	 * string have the same case as the source. Three options are supported:
	 * 1)All lower case 2) Title case 3) All upper case 
	 * 
	 * other options are not supported as the words may be of different length
	 * 
	 * @param srcString - the source string by which the case is defined
	 * @param destString - the string to which case definition should apply
	 * @return the destination string with the same case as the source string
	 */
	public static String makeSameCase(String srcString, String destString)
	{
		String ret;
	
		if (destString == null)
			ret = null;
		
		else if (srcString == null)
			ret = destString;
		
		else if (srcString.length() == 0 || destString.length() == 0)
			ret = destString;
	
		else if (srcString.toUpperCase().equals(srcString)) {
			ret = destString.toUpperCase();
		}
	
		else if (srcString.toLowerCase().equals(srcString)) {
			ret = destString.toLowerCase();
		}
	
		else if (Character.isUpperCase(srcString.charAt(0))
				&& srcString.substring(1).toLowerCase().equals(
						srcString.substring(1))) {
			ret = Character.toUpperCase(destString.charAt(0))
					+ destString.substring(1).toLowerCase();
		}
		
		else ret = destString;
		
		return ret;
	}

	/**
	 * @author Shachar
	 * Given a string, return the string in reversed order
	 * @param str the string to reverse
	 * @return the reversed string, or null if str is null
	 */
	public static String reverse(String str)
	{
		return (new StringBuffer(str)).reverse().toString();
	}

	/**
	 * Combine the list of strings into one string
	 * 
	 * @param list
	 * @return
	 */
	public static String joinIterableToString(Iterable<String> list) 
	{
		return joinIterableToString(list, "");
	}
	
	/**
	 * Combine the list of strings into one string, using the given separator
	 *  
	 * @param list
	 * @param property
	 * @return
	 */
	public static String joinIterableToString(Iterable<String> list, String separator) 
	{
		StringBuffer buf = new StringBuffer();
		for (String line : list)
			buf.append(line).append(separator);

		return buf.toString();
	}
	

	
	/**
	 * Generates a string of "character"s of length "length".
	 * For example generateStringOfCharacter("#",5) will return "#####". 
	 * @param character
	 * @param length
	 * @return
	 */
	public static String generateStringOfCharacter(char character, int length)
	{
		char[] array = new char[length];
		for (int index=0;index<array.length;index++)
			array[index]=character;
		return new String(array);
	}
	
	/**
	 * To convert the InputStream to String we use the
	 * Reader.read(char[] buffer) method. We iterate until the
	 * Reader return -1 which means there's no more data to
     * read. We use the StringWriter class to produce the string.
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String convertStreamToString(InputStream is) throws IOException 
	{
		if (is != null) 
		{
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try 
			{
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			}
			finally 
			{
				is.close();
			}
			return writer.toString();
		} 
		else 
		{        
			return "";
		}
	}
	
	public static boolean isNumber(String str)
	{
		boolean ret = true;
		if (str.length()==0)
			ret = false;
		else
		{
			char[] chars = str.toCharArray();
			for (char c : chars)
			{
				if (
					(Character.isDigit(c))
					||
					c == '-'
					||
					c == '+'
					||
					c == '.'
					)
				{ // do nothing
				}  
				else
				{
					ret = false;
					break;
				}

			}
		}
		return ret;
	}

	
	

	/**
	 * Create a string containing the string representations of all elements in the given collection, in order, glued with the given glue-string  
	 * @param glue - space, comma, newline, etc.
	 * @author Erel Segal
	 * @since 2011-10-27
	 */
	public static <T> String join(Iterable<T> elements, String glue) {
		StringBuffer result = new StringBuffer();
		for (T element: elements) {
			if (element==null) continue; 
			if (result.length()>0)  result.append(glue);
			result.append(element.toString());
		}
		return result.toString();
	}
	
	/**
	 * @param word
	 * @return
	 */
	public static String capitalizeFirstLetter(String word) {
		if (word == null || word.isEmpty() || !Character.isLowerCase(word.charAt(0)))
			return word;
		return Character.toUpperCase(word.charAt(0)) + word.substring(1);
	}

	/**
	 * Capitalize first letter, and lower case the rest
	 * @param word
	 * @return
	 */
	public static String capitalizeFirstLetterOnly(String word) {
		if (word == null || word.isEmpty() )
			return word;
		return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
	}
	
	/**
	 * @return If the length of the original is at most maxLength - return original with no change.
	 * Otherwise, return the first maxLength chars of the original.
	 * @author Erel Segal Halevi
	 * @since 2012-05-29
	 */
	public static String startOfString(String original, int maxLength) {
		if (original.length()<=maxLength)
			return original;
		else
			return original.substring(0, maxLength);
	}
	
	/**
	 * Removes characters from the right of a given string.
	 * The removed characters are characters that are less likely to end a sentence -
	 * characters that are neither letter nor digit nor an ending point/question mark/exclamation mark/semicolon
	 * 
	 * @param sentence A string contains what should be a sentence or concatenation
	 * of sentences.
	 * @return
	 */
	public static String trimSentenceRight(String sentence)
	{
		if (sentence.length()<1) return sentence;
		char[] chararray = sentence.toCharArray();
		int index = chararray.length-1;
		while (!charMayEndSentence(chararray[index]))
		{
			--index;
		}
		return new String(chararray, 0, index+1);
	}
	
	
	/**
	 * Aligns given ordered list of tokens with given text. Mostly used on an output of a tokenizer
	 * and its original sentence. Note that the tokens don't need to cover the entire text -
	 * any arbitrary number of characters can appear in the text but not in the tokens.
	 * @param text The given full text
	 * @param tokens A list of Strings, each represents a substring in the text. Must be in order of appearance in text.
	 * @param strict whether to throw an exception if a token is not found in the text. When this is false, an unfound
	 * token will be ignored, and won't be included in the result.
	 * @return a sorted map, where each entry maps a position from the parameter token list, to 
	 * a DockedToken - a DockedToken has the token string, a start offset and an end offset (in the text,
	 * counting characters). Usually this map will have the same size as the token list, yet if some of the
	 * tokens were not found in the text, and the policy is not strict (<tt>strict=false</tt>), then these
	 * tokens are ignored and the returned map has a smaller size.
	 * @throws StringUtilException if a token is not found in the text and <tt>strict=true</tt>
	 * 
	 * @author Ofer Bronstein 1/8/2012
	 */
	public static SortedMap<Integer, DockedToken> getTokensOffsets(String text, List<String> tokens, boolean strict) throws StringUtilException {
		SortedMap<Integer, DockedToken> result = new TreeMap<Integer, DockedToken>();
		int startOffset = 0;
		int endOffset;
		for (int i=0; i<tokens.size(); i++) {
			String token = tokens.get(i);
			int foundOffset = text.indexOf(token, startOffset);
			if (foundOffset == -1) {
				//throw an exception only of strict policy is required
				if (strict) {
					throw new StringUtilException("Could not find token \"" + token + "\" from offset " + startOffset);
				}
				else {
					continue;
				}
			}
			else {
				startOffset = foundOffset;
				endOffset = startOffset + token.length();
				DockedToken docked = new DockedToken(token, startOffset, endOffset);
				result.put(i, docked);
			}
		}
		return result;
	}

	/**
	 * Aligns given ordered list of tokens with given text. Mostly used on an output of a tokenizer
	 * and its original sentence. Note that the tokens don't need to cover the entire text -
	 * any arbitrary number of characters can appear in the text but not in the tokens. 
	 * @Note this is a version of {@link #getTokensOffsets(String, List, boolean)} where <tt>strict=true</tt>, meaning that
	 * StringUtilException is thrown if a token is not found in the text.
	 * @param text The given full text
	 * @param tokens A list of Strings, each represents a substring in the text. Must be in order of appearance in text.
	 * @return a sorted map, where each entry maps a position from the parameter token list, to 
	 * a DockedToken - a DockedToken has the token string, a start offset and an end offset (in the text,
	 * counting characters). This map will have the exact same size as the token list.
	 * @throws StringUtilException if a token is not found in the text
	 * 
	 * @author Ofer Bronstein 1/8/2012
	 */
	public static SortedMap<Integer, DockedToken> getTokensOffsets(String text, List<String> tokens) throws StringUtilException {
		return getTokensOffsets(text, tokens, true);
	}
	
	
	
	
	
	
//	// demo and test some utils:
//	public static void main(String[] args) throws TokenizerException, StringUtilException
//	{
//	
//		// 	testReverse() {
//
//		if (!StringUtil.reverse("abc").equals("cba"))
//			System.out.println("wrong reverse");
//
//		if (StringUtil.reverse(null) != null)
//			System.out.println("reverse fails on null");
//		if (!StringUtil.reverse("").equals(""))
//			System.out.println("reverse fails on empty string");
//		if (!StringUtil.reverse("Abc").equals("cbA"))
//			System.out.println("wrong case sensitive reverse");
//
//
//		// testMakeSameCase() {
//
//		if (!StringUtil.makeSameCase("ABC", "dEf").equals("DEF"))
//			System.out.println("failure on all upper");
//		if (!StringUtil.makeSameCase("", "dEf").equals("dEf"))
//			System.out.println("failure on first empty");
//		if (!StringUtil.makeSameCase("ABC", "").equals(""))
//			System.out.println("failure on second empty");
//		if (!StringUtil.makeSameCase("", "").equals(""))
//			System.out.println("failure on both empty");
//		if (!StringUtil.makeSameCase(null, "dEf").equals("dEf"))
//			System.out.println("failure on first null");
//		if (!StringUtil.makeSameCase(null, "dEf").equals("dEf"))
//			System.out.println("failure on first null");
//		if (StringUtil.makeSameCase("ABC", null) != null)
//			System.out.println("failure on second null");
//		if (StringUtil.makeSameCase(null, null) != null)
//			System.out.println("failure on both null");
//		if (!StringUtil.makeSameCase("abc", "deF").equals("def"))
//			System.out.println("failure on all lower");
//		if (!StringUtil.makeSameCase("Abc", "dEf").equals("Def"))
//			System.out.println("failure on title case");
//		if (!StringUtil.makeSameCase("abC", "dEf").equals("dEf"))
//			System.out.println("failure on mixed case - should make no change");
//
//		// testArrayContainsString() 	
//	
//		if (!StringUtil.arrayContainsString(new String[]{"abc","def","ghi"}, "ABc", false))
//			System.out.println("failure on ignore case");
//		
//		if (StringUtil.arrayContainsString(new String[]{"abc","def","ghi"}, "Abc", true))
//			System.out.println("failure on case sensitive");
//		
//		if (!StringUtil.arrayContainsString(new String[]{"abc","def","ghi"}, "Def", false))
//			System.out.println("failure on case insensitive ");
//	
//		
//		// test getTokensOffsets
//		
//		String input = "the dog's owner didn't     see the other dog .";
//		Tokenizer tokenizer = new MaxentTokenizer();
//		tokenizer.init();
//		tokenizer.setSentence(input);
//		tokenizer.tokenize();
//		
//		List<String> tokens = tokenizer.getTokenizedSentence();
//		SortedMap<Integer, DockedToken> dockedtokens = StringUtil.getTokensOffsets(input, tokens);
//		
//		System.out.println("Input: \"" + input + "\"");
//		System.out.println("Docked tokens: " + dockedtokens);
//
//	}
	
	public static boolean stringOnlyDigits(String str)
	{
		if (null==str) return false;
		if (str.length()==0) return false;
		
		boolean ret = true;
		for (char c : str.toCharArray())
		{
			if (!Character.isDigit(c))
			{
				ret = false;
				break;
			}
		}
		return ret;
	}

	
	/////////////////// PRIVATE PART /////////////////////////
	
	
	
	// final classes and final methods - to improve efficiency 
	
	private static final class LetterCharacterCriterion implements CharacterCriterion
	{
		public final boolean is(char c){return Character.isLetter(c);}
	}

	private static final class LetterOrDigitCharacterCriterion implements CharacterCriterion
	{
		public final boolean is(char c){return Character.isLetterOrDigit(c);}
	}
	
	private static final LetterCharacterCriterion letterCharacterCriterion = new LetterCharacterCriterion();
	private static final LetterOrDigitCharacterCriterion letterOrDigitCharacterCriterion = new LetterOrDigitCharacterCriterion();

	private static boolean charMayEndSentence(char c)
	{
		boolean ret = true;
		if (!Character.isLetterOrDigit(c))
		{
			if (!MAY_END_SENTENCE_CHARS.contains(Character.valueOf(c)))
			{
				ret = false;
			}
		}
		return ret;
	}
	
	/**
	 * @param c char
	 * @return
	 */
	private static String getStringOfEscapeSequenceChar(char c)
	{
		String ret;
		switch(c)
		{
		case '\'': ret = "\\\'"; break;
		case '\"': ret = "\\\""; break;
		case '\\': ret = "\\\\"; break;
		case '\0': ret = "\\0"; break;
		case '\b': ret = "\\b"; break;
		case '\f': ret = "\\f"; break;
		case '\n': ret = "\\n"; break;
		case '\r': ret = "\\r"; break;
		case '\t': ret = "\\t"; break;
		default: ret = null;
		}

		return ret;
	}
	
	private static final String REPLACE_WHAT1 = "^\\W+";
	private static final String REPLACE_WHAT2 = "\\W+$";
	private static final String SPLIT_STR = "\\s";
	private static final Object UNDERSCORE = "_";
	
}
