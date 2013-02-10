package eu.excitementproject.eop.core.component.lexicalknowledge.custom;

import java.util.Collection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

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
				theMap,	SimplerCanonicalPosTag.NOUN,
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
		assertCollection(nounOcean.getRulesForRight("animal", new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN)), 2);
		assertCollection(nounOcean.getRules("bird", null, "animal", null), 1);
		assertCollection(nounOcean.getRulesForLeft("bird", new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.VERB)), 0);
		assertCollection(nounOcean.getRulesForLeft("cow", new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN)), 1);
		assertCollection(nounOcean.getRulesForRight("flyable", null), 1);
	}
	
	public static void main(String[] args) throws Exception {
		setUpBeforeClass();
		new ValueSetMapLexicalResourceTest().test();
		tearDownAfterClass();
		System.out.println("Test done!");
	}

}

