package eu.excitementproject.eop.lap.biu.ae;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.junit.Ignore;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.lap.LAPAccess;

/**
 *  Test all BIU linguistic tools using a simple LAP.
 */
public class BIU_LAP_Test {
	
	// We check only TEXT, not HYPOTHESIS
	private static final String TEXT = "Tom likes to eat apples at home. Julie likes to drink juice.";
	private static final String HYPOTHESIS = "";
	
	private static final TestSentenceInfo[] EXPECTED_SENTENCES = new TestSentenceInfo[] {
		new TestSentenceInfo(0,  32, "Tom likes to eat apples at home."),
		new TestSentenceInfo(33, 60, "Julie likes to drink juice.")
	};
	
	private static final TestTokenInfo[] EXPECTED_TOKENS = new TestTokenInfo[] {
		new TestTokenInfo(1,  0,  3,  "Tom",     "tom",    "NP",    "NNP", new TestDependencyInfo[]{new TestDependencyInfo("nsubj", 2), new TestDependencyInfo("xsubj", 4)}),
		new TestTokenInfo(2,  4,  9,  "likes",   "like",   "V",     "VBZ", new TestDependencyInfo[]{}),
		new TestTokenInfo(3,  10, 12, "to",      "to",     "O",     "TO",  new TestDependencyInfo[]{new TestDependencyInfo("AUX0", 4)}),
		new TestTokenInfo(4,  13, 16, "eat",     "eat",    "V",     "VB",  new TestDependencyInfo[]{new TestDependencyInfo("xcomp", 2)}),
		new TestTokenInfo(5,  17, 23, "apples",  "apple",  "NN",    "NNS", new TestDependencyInfo[]{new TestDependencyInfo("dobj", 4)}),
		new TestTokenInfo(6,  24, 26, "at",      "at",     "PP",    "IN",  new TestDependencyInfo[]{new TestDependencyInfo("prep", 4)}),
		new TestTokenInfo(7,  27, 31, "home",    "home",   "NN",    "NN",  new TestDependencyInfo[]{new TestDependencyInfo("pobj", 6)}),
		new TestTokenInfo(8,  31, 32, ".",       ".",      "PUNC",  ".",   new TestDependencyInfo[]{new TestDependencyInfo("punct", 2)}),
		new TestTokenInfo(9,  33, 38, "Julie",   "julie",  "NP",    "NNP", new TestDependencyInfo[]{new TestDependencyInfo("nsubj", 10), new TestDependencyInfo("xsubj", 12)}),
		new TestTokenInfo(10, 39, 44, "likes",   "like",   "V",     "VBZ", new TestDependencyInfo[]{}),
		new TestTokenInfo(11, 45, 47, "to",      "to",     "O",     "TO",  new TestDependencyInfo[]{new TestDependencyInfo("AUX0", 12)}),
		new TestTokenInfo(12, 48, 53, "drink",   "drink",  "V",     "VB",  new TestDependencyInfo[]{new TestDependencyInfo("xcomp", 10)}),
		new TestTokenInfo(13, 54, 59, "juice",   "juice",  "NN",    "NN",  new TestDependencyInfo[]{new TestDependencyInfo("dobj", 12)}),
		new TestTokenInfo(14, 59, 60, ".",       ".",      "PUNC",  ".",   new TestDependencyInfo[]{new TestDependencyInfo("punct", 10)}),
	};
	
	private LinkedHashMap<Integer, TestTokenInfo> tokensById;
	private LinkedHashMap<Token, TestTokenInfo> expectedByGeneratedToken;
	private LinkedHashMap<Token, Set<TestDependencyInfo>> governors;
	

	@Ignore("Environment doesn't support yet Stanford POS tagger model file + running easyfirst")
	@Test
	public void test() throws Exception {
		try {
			// Map token infos by ID
			tokensById = new LinkedHashMap<Integer, TestTokenInfo>(EXPECTED_TOKENS.length);
			for (TestTokenInfo info : EXPECTED_TOKENS) {
				tokensById.put(info.id, info);
			}
			
			// Run LAP
			LAPAccess lap = new BIUFullLAP(); 
			JCas mainJcas = lap.generateSingleTHPairCAS(TEXT, HYPOTHESIS);
			JCas jcas = mainJcas.getView("TextView");
			
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
		Set<TestDependencyInfo> infoDependencies = new HashSet<TestDependencyInfo>(Arrays.asList(info.dependencies));
		if (!infoDependencies.equals(governors.get(token)))		
			throw new LAPVerificationException("Bad token dependencies for " + info.id + ":" + info.text + ", expected " + infoDependencies + ", got " + governors.get(token));
		
		System.out.println("Verified token: " + info);
	}
}
