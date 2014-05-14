package eu.excitementproject.eop.lap.biu.uima;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.uima.jcas.JCas;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.biu.test.BiuTestUtils;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 *  Test all BIU linguistic tools using a simple LAP.
 */
public abstract class BIU_LAP_Test {
	
	// We check only TEXT, not HYPOTHESIS
	//public static final String TEXT = "Ken likes to eat apples in Rome. Julie Andrews likes to drink juice.";
	public static final String TEXT = "Ken likes to eat his apples in Rome. Julie Andrews likes to drink her juice when she travels with Ken.";
	public static final String HYPOTHESIS = "";
	
	private static final TestSentenceInfo[] EXPECTED_SENTENCES = new TestSentenceInfo[] {
		new TestSentenceInfo(0,  36,  "Ken likes to eat his apples in Rome."),
		new TestSentenceInfo(37, 102, "Julie Andrews likes to drink her juice when she travels with Ken.")
	};
	
	private static final TestTokenInfo[] EXPECTED_TOKENS = new TestTokenInfo[] {
		new TestTokenInfo(1,  0,  3,  "Ken",     "ken",    "NP",    "NNP", "Person",   new TestDependencyInfo[]{new TestDependencyInfo("nsubj", 2), new TestDependencyInfo("xsubj", 4)}),
		new TestTokenInfo(2,  4,  9,  "likes",   "like",   "V",     "VBZ", null,       new TestDependencyInfo[]{}),
		new TestTokenInfo(3,  10, 12, "to",      "to",     "O",     "TO",  null,       new TestDependencyInfo[]{new TestDependencyInfo("AUX0", 4)}),
		new TestTokenInfo(4,  13, 16, "eat",     "eat",    "V",     "VB",  null,       new TestDependencyInfo[]{new TestDependencyInfo("xcomp", 2)}),
		new TestTokenInfo(5,  17, 20, "his",     "his",    "PR",    "PRP$",null,       new TestDependencyInfo[]{new TestDependencyInfo("poss", 6)}),
		new TestTokenInfo(6,  21, 27, "apples",  "apple",  "NN",    "NNS", null,       new TestDependencyInfo[]{new TestDependencyInfo("dobj", 4)}),
		new TestTokenInfo(7,  28, 30, "in",      "in",     "PP",    "IN",  null,       new TestDependencyInfo[]{new TestDependencyInfo("prep", 6)}),
		new TestTokenInfo(8,  31, 35, "Rome",    "rome",   "NP",    "NNP", "Location", new TestDependencyInfo[]{new TestDependencyInfo("pobj", 7)}),
		new TestTokenInfo(9,  35, 36, ".",       ".",      "PUNC",  ".",   null,       new TestDependencyInfo[]{new TestDependencyInfo("punct", 2)}),
		
		new TestTokenInfo(10, 37, 42, "Julie",   "julie",  "NP",    "NNP", "Person",   new TestDependencyInfo[]{new TestDependencyInfo("nn", 11)}),
		new TestTokenInfo(11, 43, 50, "Andrews", "andrews","NP",    "NNP", "Person",   new TestDependencyInfo[]{new TestDependencyInfo("nsubj", 12), new TestDependencyInfo("xsubj", 14)}),
		new TestTokenInfo(12, 51, 56, "likes",   "like",   "V",     "VBZ", null,       new TestDependencyInfo[]{}),
		new TestTokenInfo(13, 57, 59, "to",      "to",     "O",     "TO",  null,       new TestDependencyInfo[]{new TestDependencyInfo("AUX0", 14)}),
		new TestTokenInfo(14, 60, 65, "drink",   "drink",  "V",     "VB",  null,       new TestDependencyInfo[]{new TestDependencyInfo("xcomp", 12)}),
		new TestTokenInfo(15, 66, 69, "her",     "her",    "PR",    "PRP$",null,       new TestDependencyInfo[]{new TestDependencyInfo("poss", 16)}),
		new TestTokenInfo(16, 70, 75, "juice",   "juice",  "NN",    "NN",  null,       new TestDependencyInfo[]{new TestDependencyInfo("dobj", 14)}),
		new TestTokenInfo(17, 76, 80, "when",    "when",   "ADV",   "WRB", null,       new TestDependencyInfo[]{new TestDependencyInfo("advmod", 19)}),
		new TestTokenInfo(18, 81, 84, "she",     "she",    "PR",    "PRP", null,       new TestDependencyInfo[]{new TestDependencyInfo("nsubj", 19)}),
		new TestTokenInfo(19, 85, 92, "travels", "travel", "V",     "VBZ", null,       new TestDependencyInfo[]{new TestDependencyInfo("advcl", 14)}),
		new TestTokenInfo(20, 93, 97, "with",    "with",   "PP",    "IN",  null,       new TestDependencyInfo[]{new TestDependencyInfo("prep", 19)}),
		new TestTokenInfo(21, 98, 101,"Ken",     "ken",    "NP",    "NNP", "Person",   new TestDependencyInfo[]{new TestDependencyInfo("pobj", 20)}),
		new TestTokenInfo(22, 101,102,".",       ".",      "PUNC",  ".",   null,       new TestDependencyInfo[]{new TestDependencyInfo("punct", 12)}),
	};
	
	private static final TestCorefMentionInfo[][] EXPECTED_COREF = new TestCorefMentionInfo[][] {
		//TODO We are not checking coref right now, as it has been removed from the LAP dur to bugs in Arkref
		// if any coref is ever inserted back - uncomment this coref info for testing
		
//		new TestCorefMentionInfo[] {new TestCorefMentionInfo(0,3), new TestCorefMentionInfo(17,20), new TestCorefMentionInfo(98,101)},
//		new TestCorefMentionInfo[] {new TestCorefMentionInfo(37,50), new TestCorefMentionInfo(66,69), new TestCorefMentionInfo(81,84)},
	};
	
	private LinkedHashMap<Integer, TestTokenInfo> tokensById;
	private LinkedHashMap<Token, TestTokenInfo> expectedByGeneratedToken;
	private LinkedHashMap<Token, Set<TestDependencyInfo>> governors;
	private SortedMap<Integer, CoreferenceChain> corefChainsByFirstMentionStart;
	

	@BeforeClass
	public static void beforeClass() throws IOException {
		// Run test only under BIU environment
		BiuTestUtils.assumeBiuEnvironment();
	}

	@Test
	public void test() throws Exception {
		try {
			// Map token infos by ID
			tokensById = new LinkedHashMap<Integer, TestTokenInfo>(EXPECTED_TOKENS.length);
			for (TestTokenInfo info : EXPECTED_TOKENS) {
				tokensById.put(info.id, info);
			}
			
			// Run LAP
			LAPAccess lap = getLAP(); 
			JCas mainJcas = lap.generateSingleTHPairCAS(TEXT, HYPOTHESIS);
			JCas jcas = mainJcas.getView(LAP_ImplBase.TEXTVIEW);
			
			// Verify sentences
			Iterator<TestSentenceInfo> iterSentence = Arrays.asList(EXPECTED_SENTENCES).iterator();
			for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
				verifySentence(sentence, iterSentence.next());
			}
			
			// Map generated Tokens to infos - by their order!
			expectedByGeneratedToken = new LinkedHashMap<Token, TestTokenInfo>(EXPECTED_TOKENS.length);
			Iterator<TestTokenInfo> iterToken = tokensById.values().iterator();
			for (Token token : JCasUtil.select(jcas, Token.class)) {
				expectedByGeneratedToken.put(token, iterToken.next());
			}
			
			// For each Token, find all its dependencies
			governors = new LinkedHashMap<Token, Set<TestDependencyInfo>>(expectedByGeneratedToken.size());
			for (Token token : expectedByGeneratedToken.keySet()) {
				governors.put(token, new HashSet<TestDependencyInfo>());
			}
			for (Dependency dep : JCasUtil.select(jcas, Dependency.class)) {
				int governorId = expectedByGeneratedToken.get(dep.getGovernor()).id;
				TestDependencyInfo depInfo = new TestDependencyInfo(dep.getDependencyType(), governorId);
				governors.get(dep.getDependent()).add(depInfo);
			}
			
			// Verify tokens
			for (Entry<Token, TestTokenInfo> entry : expectedByGeneratedToken.entrySet()) {
				verifyToken(entry.getKey(), entry.getValue());
			}
			
			// Verify coref groups
			corefChainsByFirstMentionStart = new TreeMap<Integer, CoreferenceChain>();
			for (CoreferenceChain chain : JCasUtil.select(jcas, CoreferenceChain.class)) {
				// use this map in order to order chain by a predefined order - the start offset of its first CoreferenceLink
				corefChainsByFirstMentionStart.put(chain.getFirst().getBegin(), chain);
			}
			if (corefChainsByFirstMentionStart.size() != EXPECTED_COREF.length) {
				throw new LAPVerificationException("Bad amount of coreference chains, expected " + EXPECTED_COREF.length + ", got " + corefChainsByFirstMentionStart.size());
			}
			Iterator<TestCorefMentionInfo[]> iterCorefGroups = Arrays.asList(EXPECTED_COREF).iterator();
			for (CoreferenceChain chain : corefChainsByFirstMentionStart.values()) {
				Iterator<TestCorefMentionInfo> iterCoref = Arrays.asList(iterCorefGroups.next()).iterator();
				for (CoreferenceLink link = chain.getFirst(); link!=null; link = link.getNext()) {
					verifyCorefLink(link, iterCoref.next());
				}
			}
		}
		catch (Exception e) {
			ExceptionUtil.outputException(e, System.out);
			throw e;
		}
	}
	
	private void verifySentence(Sentence sentence, TestSentenceInfo info) throws LAPVerificationException {
		if (!info.text.equals(sentence.getCoveredText()))
			throw new LAPVerificationException("Bad sentence text, expected \"" + info.text + "\", got \"" + sentence.getCoveredText() + "\"");
		if (info.begin!=sentence.getBegin())
			throw new LAPVerificationException("Bad sentence begin index, expected " + info.begin + ", got " + sentence.getBegin());
		if (info.end!=sentence.getEnd())
			throw new LAPVerificationException("Bad sentence end index, expected " + info.end + ", got " + sentence.getEnd());
		
		System.out.println("Verified sentence: " + info);
	}

	private void verifyToken(Token token, TestTokenInfo info) throws LAPVerificationException {
		if (!info.text.equals(token.getCoveredText()))
			throw new LAPVerificationException("Bad token text for " + info.id + ":" + info.text + ", expected \"" + info.text + "\", got \"" + token.getCoveredText() + "\"");
		if (info.begin!=token.getBegin())
			throw new LAPVerificationException("Bad token begin index for " + info.id + ":" + info.text + ", expected " + info.begin + ", got " + token.getBegin());
		if (info.end!=token.getEnd())
			throw new LAPVerificationException("Bad token end index for " + info.id + ":" + info.text + ", expected " + info.end + ", got " + token.getEnd());
		if (!info.lemma.equals(token.getLemma().getValue()))
			throw new LAPVerificationException("Bad token lemma for " + info.id + ":" + info.text + ", expected \"" + info.lemma + "\", got \"" + token.getLemma().getValue() + "\"");
		if (!info.posType.equals(token.getPos().getType().getShortName()))
			throw new LAPVerificationException("Bad token POS type for " + info.id + ":" + info.text + ", expected " + info.posType + ", got " + token.getPos().getType().getShortName());
		if (!info.posValue.equals(token.getPos().getPosValue()))
			throw new LAPVerificationException("Bad token POS value for " + info.id + ":" + info.text + ", expected \"" + info.posValue + "\", got \"" + token.getPos().getPosValue() + "\"");
		
		String nerType = null;
		List<NamedEntity> ners = JCasUtil.selectCovered(NamedEntity.class, token);
		if (ners.size() == 1) {
			nerType = ners.get(0).getType().getShortName();
		}
		else if (ners.size() > 1) {
			throw new LAPVerificationException("Got more than one NER annotation for " + info.id + ":" + info.text + " - " + ners);
		}
		if (!Objects.equals(info.nerType, nerType))
			throw new LAPVerificationException("Bad token NER value for " + info.id + ":" + info.text + ", expected \"" + info.nerType + "\", got \"" + nerType + "\"");
		
		Set<TestDependencyInfo> infoDependencies = new HashSet<TestDependencyInfo>(Arrays.asList(info.dependencies));
		if (!infoDependencies.equals(governors.get(token)))		
			throw new LAPVerificationException("Bad token dependencies for " + info.id + ":" + info.text + ", expected " + infoDependencies + ", got " + governors.get(token));
		
		System.out.println("Verified token: " + info);
	}

	private void verifyCorefLink(CoreferenceLink link, TestCorefMentionInfo info) throws LAPVerificationException {
		if (info.begin!=link.getBegin())
			throw new LAPVerificationException("Bad coref link begin index, expected " + info.begin + ", got " + link.getBegin());
		if (info.end!=link.getEnd())
			throw new LAPVerificationException("Bad coref link end index, expected " + info.end + ", got " + link.getEnd());

		System.out.println("Verified coref: " + info);
	}

	protected abstract LAPAccess getLAP() throws LAPException;
}
