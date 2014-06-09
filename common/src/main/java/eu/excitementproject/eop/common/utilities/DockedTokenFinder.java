package eu.excitementproject.eop.common.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DockedTokenFinder {
	/**
	 * Aligns given ordered list of tokens with given text. Mostly used on an output of a tokenizer
	 * and its original sentence. Note that the tokens don't need to cover the entire text -
	 * any arbitrary number of characters can appear in the text but not in the tokens.
	 * @param text The given full text
	 * @param tokens A list of Strings, each represents a substring in the text. Must be in order of appearance in text.
	 * @param strict whether to throw an exception if a token is not found in the text. When this is false, an unfound
	 * token will be ignored, and won't be included in the result.
	 * @param tryRecoverMissing whether to use a heuristic for recovering some tokens that are missing from text.
	 * These heuristics have two assumptions (that are usually correct):
	 * 1. A whitespace is never part of a token
	 * 2. Every token has at least one character
	 * @return a sorted map, where each entry maps a position from the parameter token list, to 
	 * a DockedToken - a DockedToken has the token string, a start offset and an end offset (in the text,
	 * counting characters). Usually this map will have the same size as the token list, yet if some of the
	 * tokens were not found in the text, and the policy is not strict (<tt>strict=false</tt>), then these
	 * tokens are ignored and the returned map has a smaller size.
	 * @throws DockedTokenFinderException Specifically, throws {@link TokenMissingException} if a token
	 * is not found in the text and <tt>strict=true</tt>
	 * 
	 * @author Ofer Bronstein
	 * @since 1/8/2012
	 */
	public static SortedMap<Integer, DockedToken> find(String text, List<String> tokens, boolean strict, boolean tryRecoverMissing) throws DockedTokenFinderException {
		SortedMap<Integer, DockedToken> matches = new TreeMap<Integer, DockedToken>();
		SortedMap<Integer, DockedToken> nonMatches = new TreeMap<Integer, DockedToken>();
		DockedToken lastNonMatch = null;
		int searchStartOffset = 0;
		int endOffset;
		for (int i=0; i<tokens.size(); i++) {
			String token = tokens.get(i);
			int foundOffset = text.indexOf(token, searchStartOffset);
			if (foundOffset == -1) {
				if (tryRecoverMissing) {
					// Store this unfound token for later processing
					DockedToken docked = new DockedToken(token, searchStartOffset, INVALID_INDEX);
					nonMatches.put(i, docked);
					lastNonMatch = docked;
				}
				else {
					//throw an exception only if strict policy is required
					if (strict) {
						throw new TokenMissingException("Could not find token \"" + token + "\" from offset " + searchStartOffset);
					}
					// if not strict - do nothing
				}
			}
			else {
				endOffset = foundOffset + token.length();
				DockedToken docked = new DockedToken(token, foundOffset, endOffset);
				matches.put(i, docked);
				searchStartOffset = endOffset;
				if (tryRecoverMissing && lastNonMatch != null) {
					lastNonMatch.setCharOffsetEnd(foundOffset);
					lastNonMatch = null;
				}
			}
		}
		
		// Add an end-marker. Needed so that iterating this map won't ignore the last item.
		nonMatches.put(Integer.MAX_VALUE, new DockedToken(null, INVALID_INDEX, INVALID_INDEX));
		if (tryRecoverMissing && lastNonMatch != null) {
			lastNonMatch.setCharOffsetEnd(text.length());
		}
		
		// this heuristic assumes that a whitespace is always a delimiter between tokens (yet it does
		// not assume it is the only delimiter)
		if (tryRecoverMissing) {
			int prevI = INVALID_INDEX;
			int i;
			List<DockedToken> nonMatchedRange = new ArrayList<DockedToken>();
			Iterator<Entry<Integer, DockedToken>> iter = nonMatches.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Integer, DockedToken> entry = iter.next();
				i = entry.getKey();
				if (prevI >= Integer.MAX_VALUE-1) {
					throw new DockedTokenFinderException("Class does not support having " + (Integer.MAX_VALUE-1) + " tokens");
				}
				if (prevI != INVALID_INDEX && prevI + 1 != i) {
					
					// close and process current range
					handleNonMatchedRange(nonMatchedRange, matches, text, strict, prevI);
					
					// open new range
					nonMatchedRange.clear();
				}
				nonMatchedRange.add(entry.getValue());
				iter.remove();
				prevI = i;
			}
			if (strict && !nonMatches.isEmpty()) {
				throw new TokenMissingException("After recovery attempts, some tokens could still not be recovered: " + nonMatches);
			}
		}
		return matches;

	}

	/**
	 * Aligns given ordered list of tokens with given text. Mostly used on an output of a tokenizer
	 * and its original sentence. Note that the tokens don't need to cover the entire text -
	 * any arbitrary number of characters can appear in the text but not in the tokens. 
	 * @Note this is a version of {@link #find(String, List, boolean, boolean)} where <tt>strict=true</tt> and
	 * <tt>tryRecoverMissing=false</tt>, meaning that TokenMissingException is thrown if a token is
	 * not found in the text, and no heuristic recovery is attempted.
	 * @param text The given full text
	 * @param tokens A list of Strings, each represents a substring in the text. Must be in order of appearance in text.
	 * @return a sorted map, where each entry maps a position from the parameter token list, to 
	 * a DockedToken - a DockedToken has the token string, a start offset and an end offset (in the text,
	 * counting characters). This map will have the exact same size as the token list.
	 * @throws DockedTokenFinderException Specifically, throws {@link TokenMissingException} if a token
	 * is not found in the text and <tt>strict=true</tt>
	 * 
	 * @author Ofer Bronstein
	 * @since 1/8/2012
	 */
	public static SortedMap<Integer, DockedToken> find(String text, List<String> tokens) throws DockedTokenFinderException {
		return find(text, tokens, true, false);
	}
	
	private static void handleNonMatchedRange(List<DockedToken> nonMatchedRange, SortedMap<Integer, DockedToken> matches, String text, boolean strict, int prevI) throws DockedTokenFinderException {
		int rangeTextStart = nonMatchedRange.get(0).getCharOffsetStart();
		int rangeTextEnd = nonMatchedRange.get(nonMatchedRange.size()-1).getCharOffsetEnd();
		if (rangeTextEnd==INVALID_INDEX) {
			throw new DockedTokenFinderException("Internal error: got endOffset=" + INVALID_INDEX + "for DockedToken with token=\"" + nonMatchedRange.get(nonMatchedRange.size()-1).getToken() + "\""); 
		}
		String rangeText = text.substring(rangeTextStart, rangeTextEnd);
		List<DockedToken> recoveredTokens = new ArrayList<DockedToken>();
		Matcher matcher = NON_WHITESPACE.matcher(rangeText);
		int recoveredLen = 0;
		while (matcher.find()) {
			DockedToken docked = new DockedToken(matcher.group(), matcher.start()+rangeTextStart, matcher.end()+rangeTextStart);
			recoveredTokens.add(docked);
			recoveredLen += matcher.group().length();
		}
		
		// Recovery method #1: if splitting by whitespace yields exactly the amount of tokens
		// in the nonMatchedRange. If so, each whitespace-delimited range is a token.
		if (recoveredTokens.size() == nonMatchedRange.size()) {
			//TODO: note that the text in the recovered docked tokens is the one from text and not from tokens.
			for (int j=0; j<recoveredTokens.size(); j++) {
				int calculatedIndex = prevI-nonMatchedRange.size()+j+1;
				matches.put(calculatedIndex, recoveredTokens.get(j));
			}
		}
		// Recovery method #2: if the amount of characters is exactly the amount of tokens
		// in the nonMatchedRange. If so, each token is exactly one-character.
		else if (recoveredLen == nonMatchedRange.size()) {
			int j = prevI-nonMatchedRange.size()+1;
			for (DockedToken docked : recoveredTokens) {
				char[] chars = docked.getToken().toCharArray();
				for (int k=0; k<chars.length; k++) {
					DockedToken oneCharToken = new DockedToken(""+chars[k], docked.getCharOffsetStart()+k, docked.getCharOffsetStart()+k+1);
					matches.put(j, oneCharToken);
					j += 1;
				}
			}
		}
		else {
			//throw an exception only if strict policy is required
			if (strict) {
				throw new TokenMissingException("Could not recover tokens " + nonMatchedRange + " from range: " + new DockedToken(rangeText, rangeTextStart, rangeTextEnd));
			}
			// if not strict - do nothing
		}

	}
	
	private static final int INVALID_INDEX = -1;
	private static final Pattern NON_WHITESPACE = Pattern.compile("\\S+");

}
