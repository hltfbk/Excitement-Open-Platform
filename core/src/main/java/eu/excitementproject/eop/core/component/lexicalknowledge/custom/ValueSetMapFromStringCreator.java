package eu.excitementproject.eop.core.component.lexicalknowledge.custom;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.file.FileUtils;


/**
 * Utils for building a {@link ValueSetMap} from text, and converting to text.
 *
 * @author Erel Segal the Levite
 * @since 2012-07-24
 */
public class ValueSetMapFromStringCreator {
	
	/**
	 * <p>Build a string-to-string bidirectional multi-map from text in a table with two columns.
	 * <p>Each line in the text contains a single key-value association.
	 * <p>The key and the value are separated by the given regular-expression pattern.
	 * <p>For example, if separatorPattern="->", then the lines should be of the format:
	 * <pre>key->value</pre>
	 * @param text the entire text.
	 * @return the map.
	 */
	public static ValueSetMap<String,String> mapFromTwoColumnsText(String text, String separatorPattern) {
		return mapFromTwoColumnsText(text.split("[\\n\\r]+"), separatorPattern);
	}

	/**
	 * @see #mapFromTwoColumnsText(String)
	 */
	public static ValueSetMap<String,String> mapFromTwoColumnsText(String[] lines, String separatorPattern) {
		return mapFromTwoColumnsText(Arrays.asList(lines), separatorPattern);
	}

	/**
	 * @see #mapFromTwoColumnsText(String)
	 */
	public static ValueSetMap<String,String> mapFromTwoColumnsText(Iterable<String> lines, String separatorPattern) {
		ValueSetMap<String,String> theMap = new SimpleValueSetMap<String,String>();
		for (String line: lines) {
			line = line.replaceAll(commentPattern.pattern(), "");
			line = line.trim();
			String[] fields = line.split(separatorPattern);
			if (fields.length<2)
				continue; // ignore empty lines
			theMap.put(fields[0], fields[1]);
		}
		return theMap;
	}

	/**
	 * Build a string-to-string bidirectional multimap from text in a wiki-like format.
	 * 
	 * @param text text of the format:
	 * <pre>
	 * == value 1 ==
	 * * key 1-1
	 * * key 1-2
	 * ...
	 * == value 2 ==
	 * * key 2-1
	 * * key 2-2
	 * ...
	 * ...
	 * </pre>
	 * or of the format:
	 * <pre>
	 * value 1:
	 * * key 1-1
	 * * key 1-2
	 * ...
	 * value 2:
	 * ...
	 * </pre>
	 * @param addHeadingToHeadingMatch if true, add the pair heading=>heading for each heading (e.g. value1=>value1, value2=>value2...) 
	 * @param headingsAreKeys 
	 *   <li>If false, the headings are the values and the items below them are the keys, as in the example above. 
	 *   <li>If true, the headings are the keys and the items below them are the values. 
	 * @return the map.
	 */
	public static ValueSetMap<String,String> mapFromWikiText(String text, boolean addHeadingToHeadingMatch, boolean headingsAreKeys) {
		return mapFromWikiText(text.split("[\\n\\r]+"), addHeadingToHeadingMatch, headingsAreKeys);
	}

	/**
	 * @see #mapFromWikiText(String)
	 */
	public static ValueSetMap<String,String> mapFromWikiText(String[] lines, boolean addHeadingToHeadingMatch, boolean headingsAreKeys) {
		return mapFromWikiText(Arrays.asList(lines), addHeadingToHeadingMatch, headingsAreKeys);
	}

	/**
	 * @see #mapFromWikiText(String, boolean, boolean)
	 */
	public static ValueSetMap<String,String> mapFromWikiText(Iterable<String> lines, boolean addHeadingToHeadingMatch, boolean headingsAreKeys) {
		ValueSetMap<String,String> theMap = new SimpleValueSetMap<String,String>();
		String currentTitle = null;
		Matcher matcher = null;
		for (String line: lines) {
			line = line.replaceAll(commentPattern.pattern(), "");
			if ((matcher = headingMatcher(line))!=null) {
				currentTitle = matcher.group(1);
				if (currentTitle.isEmpty() || currentTitle.equalsIgnoreCase("null") || currentTitle.equalsIgnoreCase("Uncategorized"))
					currentTitle = null;
				if (addHeadingToHeadingMatch && currentTitle != null)
					theMap.put(currentTitle, currentTitle);
			} else if ((matcher = listItemMatcher(line))!=null && currentTitle!=null) {
				String document = matcher.group(3);
				if (!document.isEmpty()) {
					String tag = currentTitle;
					if (headingsAreKeys) {
						tag = document;
						document = currentTitle;
					}
					/*if (matcher.group(2)!=null) {
						String group = matcher.group(2);
						Integer newDocumentIndex = theMap.put(document, tag);
						mapGroupsToDocuments.put(Integer.valueOf(group), newDocumentIndex);
					} else*/ {
						theMap.put(document, tag);
					}
				}
			} 
		}
		return theMap;
	}
	
	/**
	 * Build a string-to-string bidirectional multi-map according to the specification in the given configuration params.
	 * 
	 * @param params 
	 * <p>should include EITHER the following params:
	 * <li>table_file - path or URL of a text-file that contains the map, in two-column format; see {@link #mapFromTwoColumnsText}.
	 * <li><li>(can be replaced with table_contents - the contents of the file)
	 * <li>table_separator - the pattern that separates between the columns in the file, e.g. "->".
	 * <p>OR the following params: 
	 * <li>wiki_file - path or URL of a text-file that contains the map, in wiki-text format; see {@link #mapFromWikiText}.
	 * <li><li>(can be replaced with wiki_contents - the contents of the file)
	 * 
	 * @return a map built from that text file.
	 * @throws ConfigurationException 
	 * @throws IOException 
	 */
	public static ValueSetMap<String,String> mapFromConfigurationParams(ConfigurationParams params) throws IOException, ConfigurationException {
		if (params.containsKey("table_file")) {
			return mapFromTwoColumnsText(
					FileUtils.loadFileOrUrlToList(params.getString("table_file")),
					params.getString("table_separator"));
		} else if (params.containsKey("table_contents")) {
				return mapFromTwoColumnsText(
						params.getString("table_contents").split("[\n\r]+"),
						params.getString("table_separator"));
		} else if (params.containsKey("wiki_file")) {
			return mapFromWikiText(
					FileUtils.loadFileOrUrlToList(params.getString("wiki_file")),
					params.containsKey("add_heading_to_heading_match")? params.getBoolean("add_heading_to_heading_match"): false, 
					params.containsKey("headings_are_keys")? params.getBoolean("headings_are_keys"): false 
					);
		} else if (params.containsKey("wiki_contents")) {
			return mapFromWikiText(
					params.getString("wiki_contents").split("[\n\r]+"),
					params.containsKey("add_heading_to_heading_match")? params.getBoolean("add_heading_to_heading_match"): false, 
					params.containsKey("headings_are_keys")? params.getBoolean("headings_are_keys"): false 
					);
		} else {
			throw new ConfigurationException("configuration params should contain either table_file or wiki_file ("+params+")");
		}
	}
	
	
	/**
	 * Build a map from string to a ValueSetMap from text in the following format:
	 * <pre>
	 * 
	 * == key1 ==
	 * * key11 -> value11
	 * * key12 -> value12
	 * ...
	 * 
	 * == key2 ==
	 * * key21 -> value21
	 * * key22 -> value22
	 * ...
	 * </pre>
	 * @return the map.
	 */
	public static Map<String, ValueSetMap<String,String>> mapOfMapsFromText(String text, String separatorPattern) {
		return mapOfMapsFromText(text.split("[\\n\\r]+"),separatorPattern);
	}

	/**
	 * @see #mapOfMapsFromText(String)
	 */
	public static Map<String, ValueSetMap<String, String>> mapOfMapsFromText(String[] lines, String separatorPattern) {
		return mapOfMapsFromText(Arrays.asList(lines),separatorPattern);
	}

	/**
	 * @see #mapOfMapsFromText(String)
	 */
	public static Map<String, ValueSetMap<String, String>> mapOfMapsFromText(List<String> lines, String separatorPattern) {
		Map<String,ValueSetMap<String,String>> theMapOfMaps = new TreeMap<String,ValueSetMap<String,String>>();
		String currentTitle = null;
		Matcher matcher = null;
		for (String line: lines) {
			line = line.replaceAll(commentPattern.pattern(), "");
			line = line.trim();
			if ((matcher = headingMatcher(line))!=null) {
				currentTitle = matcher.group(1).trim();
			} else if ((matcher = listItemMatcher(line))!=null && currentTitle!=null) {
				String pair = matcher.group(3);
				String[] fields = pair.split(separatorPattern);
				if (fields.length<2)  // skip empty lines
					continue;
				if (!theMapOfMaps.containsKey(currentTitle))
					theMapOfMaps.put(currentTitle, new SimpleValueSetMap<String,String>());
				theMapOfMaps.get(currentTitle).put(fields[0].trim(), fields[1].trim());
			}
		}
		return theMapOfMaps;
	}
	
	/**
	 * Build a string-to-string bidirectional multi-map according to the specification in the given configuration params.
	 * 
	 * @param params 
	 * <p>should include EITHER the following params:
	 * <li>table_file - path or URL of a text-file that contains the map, in two-column format; see {@link #mapFromTwoColumnsText}.
	 * <li><li>(can be replaced with table_contents - the contents of the file)
	 * <li>table_separator - the pattern that separates between the columns in the file, e.g. "->".
	 * <p>OR the following params: 
	 * <li>wiki_file - path or URL of a text-file that contains the map, in wiki-text format; see {@link #mapFromWikiText}.
	 * <li><li>(can be replaced with wiki_contents - the contents of the file)
	 * 
	 * @return a map built from that text file.
	 * @throws ConfigurationException 
	 * @throws IOException 
	 */
	public static Map<String,ValueSetMap<String,String>> mapOfMapsFromConfigurationParams(ConfigurationParams params) throws IOException, ConfigurationException {
		if (params.containsKey("file")) {
			return mapOfMapsFromText(
					FileUtils.loadFileOrUrlToList(params.getString("file")),
					params.getString("table_separator"));
		} else if (params.containsKey("contents")) {
				return mapOfMapsFromText(
						params.getString("contents").split("[\n\r]+"),
						params.getString("table_separator"));
		} else {
			throw new ConfigurationException("configuration params should contain either 'file' or 'contents' ("+params+")");
		}
	}
	
	
	

	/**
	 * Convert the given ValueSetMap to a string of the form:
	 * <p>key1 => value1</p> 
	 * <p>key2 => value2</p>
	 * <p>...</p> 
	 */
	public static <K,V> String mapToString(ValueSetMap<K,V> theMap) {
		StringBuilder sb = new StringBuilder();
		for (K key: theMap.keySet())
			sb.append(key).append(" => ").append(theMap.get(key)).append("\n");
		return sb.toString();
	}

	/**
	 * The inverse of {@link #mapFromWikiText(String, boolean, boolean)}.
	 * <p>Convert the given ValueSetMap to a string in wiki format:
	 * 
	 * <pre>
	 * == key 1 ==
	 * * value 1-1
	 * * value 1-2
	 * ...
	 * == key 2 ==
	 * * value 2-1
	 * * value 2-2
	 * ...
	 * ...
	 * </pre>
	 */
	public static <K,V> String mapToWikiText(ValueSetMap<K,V> theMap) {
		StringBuilder sb = new StringBuilder();
		for (K key: theMap.keySet()) {
			sb.append("== ").append(key).append(" ==\n");
			for (V value: theMap.get(key)) {
				sb.append("* ").append(value).append("\n");
			}
		}
		return sb.toString();
	}

	
	/*
	 * FORMATTING UTILS - PROTECTED ZONE
	 */

	protected static final Pattern headingPattern = Pattern.compile("[=]+\\s*(.*?)\\s*[=]+\\s*");
	protected static final Pattern paragraphStartPattern = Pattern.compile("([^=*].*)\\s*:\\s*");
	protected static final Pattern listItemPattern = Pattern.compile("[*]+\\s*([{][{]group[|](.*?)[}][}])?\\s*(.*)");
	protected static final Pattern commentPattern = Pattern.compile("\\s*[{][{]comment.*?[}][}]\\s*");

	protected static final Matcher headingMatcher(String line) {
		Matcher matcher = null;
		if ((matcher = headingPattern.matcher(line)).matches())
			return matcher;
		if ((matcher = paragraphStartPattern.matcher(line)).matches())
			return matcher;
		return null;
		
	}

	protected static final Matcher listItemMatcher(String line) {
		Matcher matcher = null;
		if ((matcher = listItemPattern.matcher(line)).matches())
			return matcher;
		return null;
	}
	
	
	
	
	/*
	 * TEST ZONE
	 */

	/**
	 * demo program
	 */
	public static void main(String[] args) {
		ValueSetMap<String,String> theMap = ValueSetMapFromStringCreator.mapFromWikiText("" +
				"== {noun}1:born-in:{noun}2 ==\n" +
				"* {noun}1 was born in{noun}2\n" +
				"* {noun}2 is {noun}1's birth place\n" +
				"== {noun}2:home-town:{noun}1 ==\n" +
				"* {noun}1 was born in{noun}2\n" +
				"* {noun}2 is {noun}1's home town\n" +
				"", false, false);
		System.out.println(mapToString(theMap));
			// {noun}2 is {noun}1's home town => [{noun}2:home-town:{noun}1]
			// {noun}2 is {noun}1's birth place => [{noun}1:born-in:{noun}2]
			// {noun}1 was born in{noun}2 => [{noun}2:home-town:{noun}1, {noun}1:born-in:{noun}2]
		
		theMap = ValueSetMapFromStringCreator.mapFromTwoColumnsText(""+
				"bird-> animal\n" +
				"cow -> animal\r" +
				"bird->flyable\r\n" +
				"", "\\s*->\\s*");
		System.out.println(mapToString(theMap));
			// bird => [flyable, animal]
			// cow => [animal]

		System.out.println(mapToWikiText(theMap));
			//	== bird ==
			//	* flyable
			//	* animal
			//	== cow ==
			//	* animal
		Map<String,ValueSetMap<String,String>> theMapOfMaps = ValueSetMapFromStringCreator.mapOfMapsFromText("" +
				"== {action} ==\n" +
				"* I offer {issuevalue} -> OFFER({issuevalue})\n" +
				"* Will you agree to {issuevalue}? -> QUERY({issuevalue})\n" +
				"\n" +
				"== {issuevalue} ==\n" +
				"* A salary of {number}     -> Salary={number}\n" +
				"* {number} percent pension -> Pension Fund={number}\n" +
				"", "->");
		System.out.println(theMapOfMaps);
	}
}
