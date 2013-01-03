package ac.biu.nlp.nlp.lexical_resource.impl.custom;

import java.util.Collection;

import org.junit.*;

import ac.biu.nlp.nlp.general.SimpleValueSetMap;
import ac.biu.nlp.nlp.general.ValueSetMap;
import ac.biu.nlp.nlp.lexical_resource.LexicalResourceException;
import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.UnspecifiedPartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;

/**
 * JUnit test for {@link ValueSetMapLexicalResource}
 *
 * @author Erel Segal Halevi
 * @since 2012
 */
public class ValueSetMapLexicalResourceTest {
	private static ValueSetMapLexicalResource nounOcean;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass public static void setUpBeforeClass() throws Exception {
		ValueSetMap<String,String> theMap = new SimpleValueSetMap<String,String>();
		theMap.put("bird", "animal");
		theMap.put("cow", "animal");
		theMap.put("bird", "flyable");
		
		nounOcean = new ValueSetMapLexicalResource(
				theMap,	CanonicalPosTag.NOUN,
				"DemoResource", "DemoRelation");
	}

	@AfterClass public static void tearDownAfterClass() throws Exception {
		nounOcean = null;
	}

	
	protected static void assertCollection(Collection<?> theCollection, int expectedSize) {
		if (expectedSize!=theCollection.size()) {
			System.out.println("ERROR! Expected a collection of size "+expectedSize+", but got "+theCollection);
		}
	}

	@Test public void test() throws LexicalResourceException, UnsupportedPosTagStringException {
		assertCollection(nounOcean.getRulesForLeft("bird", null), 2);
		assertCollection(nounOcean.getRulesForRight("animal", new UnspecifiedPartOfSpeech(CanonicalPosTag.NOUN)), 2);
		assertCollection(nounOcean.getRules("bird", null, "animal", null), 1);
		assertCollection(nounOcean.getRulesForLeft("bird", new UnspecifiedPartOfSpeech(CanonicalPosTag.VERB)), 0);
		assertCollection(nounOcean.getRulesForLeft("cow", new UnspecifiedPartOfSpeech(CanonicalPosTag.NOUN)), 1);
		assertCollection(nounOcean.getRulesForRight("flyable", null), 1);
	}
	
	public static void main(String[] args) throws Exception {
		setUpBeforeClass();
		new ValueSetMapLexicalResourceTest().test();
		tearDownAfterClass();
		System.out.println("Test done!");
	}

}
