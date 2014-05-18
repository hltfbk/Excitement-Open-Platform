package eu.excitementproject.eop.common.utilities;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

/**
 * Tests for {@link DockedTokenFinder}.
 * @author Ofer Bronstein
 * @since June 2013
 */
public class DockedTokenFinderTests {

	@Test
	public void normalCase() throws DockedTokenFinderException {
		String text = "The program is John's.";
		List<String> tokens = Arrays.asList(new String[] {"The", "program", "is", "John", "'s", "."});
		
		SortedMap<Integer, DockedToken> expected = new TreeMap<Integer, DockedToken>();
		expected.put(0, new DockedToken("The", 0, 3));
		expected.put(1, new DockedToken("program", 4, 11));
		expected.put(2, new DockedToken("is", 12, 14));
		expected.put(3, new DockedToken("John", 15, 19));
		expected.put(4, new DockedToken("'s", 19, 21));
		expected.put(5, new DockedToken(".", 21, 22));
		assertEquals("fail(T,T)", expected, DockedTokenFinder.find(text, tokens, true, true));
		assertEquals("fail(T,F)", expected, DockedTokenFinder.find(text, tokens, true, false));
		assertEquals("fail(F,T)", expected, DockedTokenFinder.find(text, tokens, false, true));
		assertEquals("fail(F,F)", expected, DockedTokenFinder.find(text, tokens, false, false));
	}
	
	@Test
	public void duplicateWords() throws DockedTokenFinderException {
		String text = "If you go go, a manly man said";
		List<String> tokens = Arrays.asList(new String[] {"If", "you", "go", "go", ",", "a", "manly", "man", "said"});
		
		SortedMap<Integer, DockedToken> expected = new TreeMap<Integer, DockedToken>();
		expected.put(0, new DockedToken("If", 0, 2));
		expected.put(1, new DockedToken("you", 3, 6));
		expected.put(2, new DockedToken("go", 7, 9));
		expected.put(3, new DockedToken("go", 10, 12));
		expected.put(4, new DockedToken(",", 12, 13));
		expected.put(5, new DockedToken("a", 14, 15));
		expected.put(6, new DockedToken("manly", 16, 21));
		expected.put(7, new DockedToken("man", 22, 25));
		expected.put(8, new DockedToken("said", 26, 30));
		assertEquals("fail(T,T)", expected, DockedTokenFinder.find(text, tokens, true, true));
		assertEquals("fail(T,F)", expected, DockedTokenFinder.find(text, tokens, true, false));
		assertEquals("fail(F,T)", expected, DockedTokenFinder.find(text, tokens, false, true));
		assertEquals("fail(F,F)", expected, DockedTokenFinder.find(text, tokens, false, false));
	}
	
	@Test
	public void modifiedTokensWithSpaces() throws DockedTokenFinderException {
		String text = "The program ( Hello World ) is John's.";
		List<String> tokens = Arrays.asList(new String[] {"The", "program", "-LRB-", "Hello", "World", "-RRB-", "is", "John", "'s", "."});
		
		SortedMap<Integer, DockedToken> expected = new TreeMap<Integer, DockedToken>();
		expected.put(0, new DockedToken("The", 0, 3));
		expected.put(1, new DockedToken("program", 4, 11));
		expected.put(3, new DockedToken("Hello", 14, 19));  //NOTE! this is after a gap!
		expected.put(4, new DockedToken("World", 20, 25));
		expected.put(6, new DockedToken("is", 28, 30));  //NOTE! this is after a gap!
		expected.put(7, new DockedToken("John", 31, 35));
		expected.put(8, new DockedToken("'s", 35, 37));
		expected.put(9, new DockedToken(".", 37, 38));
		assertEquals("fail(F,F)", expected, DockedTokenFinder.find(text, tokens, false, false));
		try{DockedTokenFinder.find(text, tokens, true, false);fail("fail(T,F)");}catch(TokenMissingException e){}

		expected.put(2, new DockedToken("(", 12, 13));  //NOTE! gap filler!
		expected.put(5, new DockedToken(")", 26, 27));  //NOTE! gap filler!
		assertEquals("fail(F,T)", expected, DockedTokenFinder.find(text, tokens, false, true));
		assertEquals("fail(T,T)", expected, DockedTokenFinder.find(text, tokens, true, true));
	}

	@Test
	public void modifiedTokensNoSpaces() throws DockedTokenFinderException {
		String text = "The program ''Hello World ''  is John's.";
		List<String> tokens = Arrays.asList(new String[] {"The", "program", "-DQ-", "Hello", "World", "-DQ-", "is", "John", "'s", "."});
		
		SortedMap<Integer, DockedToken> expected = new TreeMap<Integer, DockedToken>();
		expected.put(0, new DockedToken("The", 0, 3));
		expected.put(1, new DockedToken("program", 4, 11));
		expected.put(3, new DockedToken("Hello", 14, 19));  //NOTE! this is after a gap!
		expected.put(4, new DockedToken("World", 20, 25));
		expected.put(6, new DockedToken("is", 30, 32));  //NOTE! this is after a gap!
		expected.put(7, new DockedToken("John", 33, 37));
		expected.put(8, new DockedToken("'s", 37, 39));
		expected.put(9, new DockedToken(".", 39, 40));
		assertEquals("fail(F,F)", expected, DockedTokenFinder.find(text, tokens, false, false));
		try{DockedTokenFinder.find(text, tokens, true, false);fail("fail(T,F)");}catch(TokenMissingException e){}

		expected.put(2, new DockedToken("''", 12, 14));  //NOTE! gap filler!
		expected.put(5, new DockedToken("''", 26, 28));  //NOTE! gap filler!
		assertEquals("fail(F,T)", expected, DockedTokenFinder.find(text, tokens, false, true));
		assertEquals("fail(T,T)", expected, DockedTokenFinder.find(text, tokens, true, true));
	}

	@Test
	public void extraTokensWithSpaces() throws DockedTokenFinderException {
		String text = "The program is John's.";
		List<String> tokens = Arrays.asList(new String[] {"The", "program", "Hello", "is", "John", "'s", "."});
		
		SortedMap<Integer, DockedToken> expected = new TreeMap<Integer, DockedToken>();
		expected.put(0, new DockedToken("The", 0, 3));
		expected.put(1, new DockedToken("program", 4, 11));
		expected.put(3, new DockedToken("is", 12, 14));
		expected.put(4, new DockedToken("John", 15, 19));
		expected.put(5, new DockedToken("'s", 19, 21));
		expected.put(6, new DockedToken(".", 21, 22));
		assertEquals("fail(F,F)", expected, DockedTokenFinder.find(text, tokens, false, false));
		assertEquals("fail(F,T)", expected, DockedTokenFinder.find(text, tokens, false, true));
		try{DockedTokenFinder.find(text, tokens, true, false);fail("fail(T,F)");}catch(TokenMissingException e){}
		try{DockedTokenFinder.find(text, tokens, true, true);fail("fail(T,T)");}catch(TokenMissingException e){}
	}

	@Test
	public void moifiedConsecutiveTokens() throws DockedTokenFinderException {
		String text = "*The program ('Hello World ' ) is John's.";
		List<String> tokens = Arrays.asList(new String[] {"-STAR-", "The", "program", "-LRB-", "-Q-", "Hello", "World", "-Q-", "-RRB-", "is", "John", "'s", "."});
		
		SortedMap<Integer, DockedToken> expected = new TreeMap<Integer, DockedToken>();
		expected.put(1, new DockedToken("The", 1, 4));
		expected.put(2, new DockedToken("program", 5, 12));
		expected.put(5, new DockedToken("Hello", 15, 20));  //NOTE! this is after a gap!
		expected.put(6, new DockedToken("World", 21, 26));
		expected.put(9, new DockedToken("is", 31, 33));  //NOTE! this is after a gap!
		expected.put(10, new DockedToken("John", 34, 38));
		expected.put(11, new DockedToken("'s", 38, 40));
		expected.put(12, new DockedToken(".", 40, 41));
		assertEquals("fail(F,F)", expected, DockedTokenFinder.find(text, tokens, false, false));
		try{DockedTokenFinder.find(text, tokens, true, false);fail("fail(T,F)");}catch(TokenMissingException e){}

		expected.put(0, new DockedToken("*", 0, 1));  //NOTE! gap filler!
		expected.put(3, new DockedToken("(", 13, 14));  //NOTE! gap filler!
		expected.put(4, new DockedToken("'", 14, 15));  //NOTE! gap filler!
		expected.put(7, new DockedToken("'", 27, 28));  //NOTE! gap filler!
		expected.put(8, new DockedToken(")", 29, 30));  //NOTE! gap filler!
		assertEquals("fail(F,T)", expected, DockedTokenFinder.find(text, tokens, false, true));
		assertEquals("fail(T,T)", expected, DockedTokenFinder.find(text, tokens, true, true));
	}

	@Test
	public void lastTokenModified() throws DockedTokenFinderException {
		String text = "The program is John's.";
		List<String> tokens = Arrays.asList(new String[] {"The", "program", "is", "John", "'s", "-PERIOD-"});
		
		SortedMap<Integer, DockedToken> expected = new TreeMap<Integer, DockedToken>();
		expected.put(0, new DockedToken("The", 0, 3));
		expected.put(1, new DockedToken("program", 4, 11));
		expected.put(2, new DockedToken("is", 12, 14));
		expected.put(3, new DockedToken("John", 15, 19));
		expected.put(4, new DockedToken("'s", 19, 21));
		assertEquals("fail(F,F)", expected, DockedTokenFinder.find(text, tokens, false, false));

		expected.put(5, new DockedToken(".", 21, 22));
		assertEquals("fail(T,T)", expected, DockedTokenFinder.find(text, tokens, true, true));
		try{DockedTokenFinder.find(text, tokens, true, false);fail("fail(T,F)");}catch(TokenMissingException e){}
		assertEquals("fail(F,T)", expected, DockedTokenFinder.find(text, tokens, false, true));
	}
	

}
