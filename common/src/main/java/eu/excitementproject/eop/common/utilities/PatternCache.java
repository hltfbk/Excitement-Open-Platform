package eu.excitementproject.eop.common.utilities;
import java.util.*;
import java.util.regex.*;

import junit.framework.Assert;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.map.*;

/**
 * A cache for compiled regular expression patterns.
 * Each pattern will be compiled only once - when it is first required.
 * 
 * @see {@link #main}
 * 
 * @author Erel Segal
 * @since 01/12/2011
 */
public class PatternCache {
	public static final int CAPACITY=100;
	
	public static Pattern get(String regexp) {
		return cache.get(regexp);
	}
	
	public static Matcher matcher(String regexp, String input) {
		return get(regexp).matcher(input);
	}
	
	public static int size() {
		return cache.size();
	}
	
	protected static Map<String,Pattern> cache = LazyMap.decorate(
		new LRUMap<String,Pattern>(CAPACITY),
			new Transformer<String,Pattern>() {
				public Pattern transform(String regexp) { return Pattern.compile(regexp); }
			});

	/**
	 * demo program
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Matcher matcher;
		System.out.println("Testing...");
		if ((matcher=PatternCache.matcher(".*b=(\\d+).*", "a=1 b=2 c=3")).matches()) 
			Assert.assertEquals("2",matcher.group(1));
		Assert.assertEquals(1,PatternCache.size());
		if ((matcher=PatternCache.matcher(".*b=(\\d+).*", "a=1 b=22 c=3")).matches())  // should be faster
			Assert.assertEquals("22",matcher.group(1));
		Assert.assertEquals(1,PatternCache.size());

		// Test the capacity:
		if (CAPACITY==5) {
			PatternCache.get("a");
			Assert.assertEquals(2,PatternCache.size());
			PatternCache.get("b");
			Assert.assertEquals(3,PatternCache.size());
			PatternCache.get("c");
			Assert.assertEquals(4,PatternCache.size());
			PatternCache.get("d");
			Assert.assertEquals(5,PatternCache.size());
			PatternCache.get("e");
			Assert.assertEquals(5,PatternCache.size());
		}

		System.out.println("OK!");
	}

}
