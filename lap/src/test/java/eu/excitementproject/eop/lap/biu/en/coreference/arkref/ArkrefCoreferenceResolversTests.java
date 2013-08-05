package eu.excitementproject.eop.lap.biu.en.coreference.arkref;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.excitementproject.eop.common.representation.coreference.DockedMention;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolver;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolverNoTrees;
import eu.excitementproject.eop.lap.biu.en.coreference.arkref.ArkrefClient.ArkrefClientException;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TokenizerException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggerException;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;
import eu.excitementproject.eop.lap.biu.test.BiuTestUtils;
import eu.excitementproject.eop.lap.biu.test.BiuTreeBuilder;
import eu.excitementproject.eop.lap.biu.uima.BIU_LAP_Test;

/**
 * Test both arkref coreference resolvers: {@link ArkrefCoreferenceResolver}
 * and {@link ArkrefCoreferenceResolverNoTrees}.
 * 
 * Actually this test doesn't really test anything and never fails -
 * it merely prints the outputs of both resolvers in a convenient way.
 * With this class it's eash to debug them both.
 * 
 * @author Ofer Bronstein
 * @since August 2013
 */
public class ArkrefCoreferenceResolversTests {
	@BeforeClass
	public static void beforeClass() throws IOException {
		// Run tests only under BIU environment
		BiuTestUtils.assumeBiuEnvironment();
	}
	
	private void assertBothCorefsAgree(String text) throws ArkrefClientException, IOException, SentenceSplitterException, TokenizerException, PosTaggerException, NamedEntityRecognizerException, ParserRunException, CoreferenceResolutionException{

		BiuTreeBuilder builder = new BiuTreeBuilder();
		List<BasicNode> referenceTrees = builder.buildTrees(text);

		CoreferenceResolver<BasicNode> coref1 = new ArkrefCoreferenceResolver();
		coref1.init();
		coref1.setInput(referenceTrees, text);
		coref1.resolve();
		TreeCoreferenceInformation<BasicNode> treeCorefInfo = coref1.getCoreferenceInformation();
		coref1.cleanUp();

		CoreferenceResolverNoTrees coref2 = new ArkrefCoreferenceResolverNoTrees();
		coref2.init();
		coref2.setInput(text);
		coref2.resolve();
		List<List<DockedMention>> dockedCorefInfo = coref2.getCoreferenceInformation();
		coref2.cleanUp();
		
		
		// Prints
		System.out.printf("TEXT:\n%s\n\n", text);

		System.out.printf("TEXT TREES:\n");
		for (BasicNode tree : referenceTrees) {
			System.out.printf("%s\n", AbstractNodeUtils.getIndentedString(tree));
		}
		System.out.printf("\n\n");

		System.out.printf("TREE COREFERENCE INFORMATION:\n%s\n\n", treeCorefInfo.toStringWriteSubtrees());

		System.out.printf("DOCKED COREFERENCE INFORMATION:\n");
		for (List<DockedMention> dockedMentions: dockedCorefInfo) {
			System.out.printf("%s\n", dockedMentions);
		}
		
		System.out.printf("\n==================================\n");

		//throw new CoreferenceResolutionException("Test no complete yet");
	}
	
	@Test
	public void test1() throws Exception {
		// This is a good example, has a lot of coreference
		assertBothCorefsAgree("Prince George of Cambridge (George Alexander Louis, born 22 July 2013) " +
				"is the only child of Prince William, Duke of Cambridge, and his wife Catherine, " +
				"Duchess of Cambridge. He is the only grandchild of Charles, Prince of Wales, " +
				"and is third in line to succeed his great-grandmother, Queen Elizabeth II, " +
				"after his grandfather and father.");
	}

	@Test
	public void test2() throws Exception {
		assertBothCorefsAgree(BIU_LAP_Test.TEXT);
	}
}
