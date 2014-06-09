package eu.excitementproject.eop.common.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * JUnit tests for various static functions in {@link StringUtil}.
 * These tests were initially taken from that classes main() method, that was
 * intended for demo and testing.
 * 
 * @author Ofer Bronstein
 * @since June 2013
 */
public class StringUtilTests {
	
	@Test
	public void testReverse() {
		assertEquals("wrong reverse", "cba", StringUtil.reverse("abc"));
		assertEquals("reverse fails on empty string", "", StringUtil.reverse(""));
		assertEquals("wrong case sensitive reverse", "cbA", StringUtil.reverse("Abc"));
		
		//This test fails, currently reverse() throws an exception on null input
		//TODO: change method implementation to fit documentation and test, or change documentation and test to fit method ipmlementation
		//assertEquals("reverse fails on null", null, StringUtil.reverse(null));
	}

	@Test
	public void testMakeSameCase() {
		assertEquals("failure on all upper", "DEF", StringUtil.makeSameCase("ABC", "dEf"));
		assertEquals("failure on first empty", "dEf", StringUtil.makeSameCase("", "dEf"));
		assertEquals("failure on second empty", "", StringUtil.makeSameCase("ABC", ""));
		assertEquals("failure on both empty", "", StringUtil.makeSameCase("", ""));
		assertEquals("failure on first null", "dEf", StringUtil.makeSameCase(null, "dEf"));
		assertEquals("failure on second null", null, StringUtil.makeSameCase("ABC", null));
		assertEquals("failure on both null", null, StringUtil.makeSameCase(null, null));
		assertEquals("failure on all lower", "def", StringUtil.makeSameCase("abc", "deF"));
		assertEquals("failure on title case", "Def", StringUtil.makeSameCase("Abc", "dEf"));
		assertEquals("failure on mixed case - should make no change", "dEf", StringUtil.makeSameCase("abC", "dEf"));

	}

	@Test
	public void testArrayContainsString() {
		assertTrue("failure on ignore case", StringUtil.arrayContainsString(new String[]{"abc","def","ghi"}, "ABc", false));
		assertFalse("failure on case sensitive", StringUtil.arrayContainsString(new String[]{"abc","def","ghi"}, "Abc", true));
		assertTrue("failure on case insensitive", StringUtil.arrayContainsString(new String[]{"abc","def","ghi"}, "Def", false));
	}
	
}
