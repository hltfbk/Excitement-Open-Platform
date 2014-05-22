package eu.excitementproject.eop.lap.biu.en.ner.stanford;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityPhrase;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityWord;
import eu.excitementproject.eop.lap.biu.test.BiuTestParams;
import eu.excitementproject.eop.lap.biu.test.BiuTestUtils;

public class StanfordNamedEntityRecognizerTest {


	private final List<List<String>> SENTENCES = Arrays.asList(
		Arrays.asList("I", "saw", "Clinton", "and", "Barack", "Obama", "over", "there"),
		Arrays.asList("Clinton", "and", "Barack", "Hussein", "Obama", "visited", "New", "York", "the", "United", "Nations", "Security", "Council"),
		Arrays.asList("Clinton", "and", "Barack", "Hussein", "Obama", "visited", "Canada", "Joe")
	);	
	
	private final List<List<NamedEntityWord>> LISTS = Arrays.asList(
		Arrays.asList(
			new NamedEntityWord("I", null),
			new NamedEntityWord("saw", null),
			new NamedEntityWord("Clinton", NamedEntity.PERSON),
			new NamedEntityWord("and", null),
			new NamedEntityWord("Barack", NamedEntity.PERSON),
			new NamedEntityWord("Obama", NamedEntity.PERSON),
			new NamedEntityWord("over", null),
			new NamedEntityWord("there", null)
		),
		Arrays.asList(
			new NamedEntityWord("Clinton", NamedEntity.PERSON),
			new NamedEntityWord("and", null),
			new NamedEntityWord("Barack", NamedEntity.PERSON),
			new NamedEntityWord("Hussein", NamedEntity.PERSON),
			new NamedEntityWord("Obama", NamedEntity.PERSON),
			new NamedEntityWord("visited", null),
			new NamedEntityWord("New", NamedEntity.LOCATION),
			new NamedEntityWord("York", NamedEntity.LOCATION),
			new NamedEntityWord("the", null),
			new NamedEntityWord("United", NamedEntity.ORGANIZATION),
			new NamedEntityWord("Nations", NamedEntity.ORGANIZATION),
			new NamedEntityWord("Security", NamedEntity.ORGANIZATION),
			new NamedEntityWord("Council", NamedEntity.ORGANIZATION)
		),
		Arrays.asList(
			new NamedEntityWord("Clinton", NamedEntity.PERSON),
			new NamedEntityWord("and", null),
			new NamedEntityWord("Barack", NamedEntity.PERSON),
			new NamedEntityWord("Hussein", NamedEntity.PERSON),
			new NamedEntityWord("Obama", NamedEntity.PERSON),
			new NamedEntityWord("visited", null),
			new NamedEntityWord("Canada", NamedEntity.LOCATION),
			new NamedEntityWord("Joe", NamedEntity.PERSON)
		)
	);
	
	@SuppressWarnings("serial")
	private final List<HashMap<Integer, NamedEntityPhrase>> MAPS = Arrays.asList(
			new HashMap<Integer, NamedEntityPhrase>() {{
				put(2, new NamedEntityPhrase("Clinton", NamedEntity.PERSON));
				put(4, new NamedEntityPhrase("Barack Obama", NamedEntity.PERSON));
			}},
			new HashMap<Integer, NamedEntityPhrase>() {{
				put(0, new NamedEntityPhrase("Clinton", NamedEntity.PERSON));
				put(2, new NamedEntityPhrase("Barack Hussein Obama", NamedEntity.PERSON));
				put(6, new NamedEntityPhrase("New York", NamedEntity.LOCATION));
				put(9, new NamedEntityPhrase("United Nations Security Council", NamedEntity.ORGANIZATION));
			}},
			new HashMap<Integer, NamedEntityPhrase>() {{
				put(0, new NamedEntityPhrase("Clinton", NamedEntity.PERSON));
				put(2, new NamedEntityPhrase("Barack Hussein Obama", NamedEntity.PERSON));
				put(6, new NamedEntityPhrase("Canada", NamedEntity.LOCATION));
				put(7, new NamedEntityPhrase("Joe", NamedEntity.PERSON));
			}}
	);

	@BeforeClass
	public static void beforeClass() throws IOException {
		// Run test only under BIU environment
		BiuTestUtils.assumeBiuEnvironment();
	}

	@Test
	public void test() throws Exception {
		StanfordNamedEntityRecognizer ner = new StanfordNamedEntityRecognizer(new File(BiuTestParams.STANFORD_NER_CLASSIFIER_PATH));
		ner.init();
		
		for (int i=0; i<SENTENCES.size(); i++) {
			try {
				ner.setSentence(SENTENCES.get(i));
				ner.recognize();
				List<NamedEntityWord> list = ner.getAnnotatedSentence();
				Map<Integer, NamedEntityPhrase> map = ner.getAnnotatedEntities();
				Assert.assertEquals(LISTS.get(i), list);
				Assert.assertEquals(MAPS.get(i), map);
			}
			catch (Throwable e) {
				Exception exc = new Exception("Exception in sentence: " + SENTENCES.get(i), e);
				ExceptionUtil.outputException(exc, System.out);
				throw exc;
			}
		}
	}
}
