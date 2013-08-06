/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.rulematcher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.IdLemmaPosRelNodeString;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.NoAntLemmaPosRelNodeAndEdgeString;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeDotFileGenerator;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeDotFileGenerator.TreeDotFileGeneratorException;
import eu.excitementproject.eop.common.representation.parse.tree.match.AllEmbeddedMatcher;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatcherException;
import eu.excitementproject.eop.common.utilities.log4j.BasicVerySimpleLoggerInitializer;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;
import eu.excitementproject.eop.transformations.datastructures.FlippedBidirectionalMap;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;
import eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules.EntailmentRuleCompiler;
import eu.excitementproject.eop.transformations.operations.operations.ExtendedSubstitutionRuleApplicationOperation;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.operations.operations.IntroductionRuleApplicationOperation;
import eu.excitementproject.eop.transformations.operations.rules.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.transformations.representation.AdditionalInformationServices;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedMatchCriteria;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.view.RulesViewer;

/**
 * Match rules using the engine's code, and print the matched trees to DOTs
 * 
 * @author Amnon Lotan
 *
 * @since 10/04/2011
 */
@Deprecated
public class RuleMatcher
{
	private static EasyFirstParser parser;
	private String dotDir;
	
	/**
	 * @throws ParserRunException 
	 * 
	 */
	public RuleMatcher(String dotDir) throws ParserRunException
	{
		parser = new EasyFirstParser("b:/jars/stanford-postagger-2008-09-28/bidirectional-wsj-0-18.tagger");
		parser.init();
		this.dotDir = dotDir;
	}
	
	/**
	 * match ...
	 * 
	 * @param rulesWithCD
	 * @param tree
	 * @throws Exception 
	 */
	public List<RuleWithConfidenceAndDescription<Info, BasicNode>> matchRules(List<RuleWithConfidenceAndDescription<Info, BasicNode>> rulesWithCD, String text) 
			throws Exception
	{
		List<RuleWithConfidenceAndDescription<Info, BasicNode>> matchedRules = new ArrayList<RuleWithConfidenceAndDescription<Info, BasicNode>>();
		parser.setSentence(text);
		parser.parse();
		BasicNode englishTree = parser.getParseTree();
		ExtendedNode tree = englishToExtendedNode(englishTree);
		TreeAndParentMap<ExtendedInfo, ExtendedNode> textTreeAndParentMap = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(tree);
		 
		int appNdx = 0;
		
		// apply all these substitution rules on the text in sequence
		for (RuleWithConfidenceAndDescription<Info, BasicNode> ruleWithCD : rulesWithCD)
		{
			SyntacticRule<Info, BasicNode> rule = ruleWithCD.getRule();
			for (BidirectionalMap<BasicNode, ExtendedNode> mapLhsToTree : mapRuleToTree(tree, rule))
			{
				GenerationOperation<ExtendedInfo, ExtendedNode> operation = rule.isExtraction() ?
						new IntroductionRuleApplicationOperation(textTreeAndParentMap, textTreeAndParentMap, rule, mapLhsToTree)
					:
						new ExtendedSubstitutionRuleApplicationOperation(textTreeAndParentMap, 
								new TreeAndParentMap<ExtendedInfo, ExtendedNode>(tree), rule, mapLhsToTree);
				operation.generate();
				ExtendedNode generatedTree = operation.getGeneratedTree();
				
				// print out a DOT of the original, and of the generated tree
				printDotFiles(textTreeAndParentMap.getTree(), generatedTree, text, ruleWithCD.getDescription(), dotDir, appNdx++);
				matchedRules.add(new RuleWithConfidenceAndDescription<Info, BasicNode>(rule, ruleWithCD.getConfidence(), ruleWithCD.getDescription()));
			}
		}
		
		return matchedRules;
	}

	/**
	 * @param tree
	 * @param rule
	 * @return
	 * @throws MatcherException 
	 */
	private Set<BidirectionalMap<BasicNode, ExtendedNode>> mapRuleToTree(ExtendedNode tree, SyntacticRule<Info, BasicNode> rule) throws MatcherException
	{
		Set<BidirectionalMap<BasicNode, ExtendedNode>> mapsOfLhsToTree = new LinkedHashSet<BidirectionalMap<BasicNode,ExtendedNode>>();
		
		AllEmbeddedMatcher<ExtendedInfo, Info, ExtendedNode, BasicNode> matcher =
			new AllEmbeddedMatcher<ExtendedInfo, Info, ExtendedNode, BasicNode>(new ExtendedMatchCriteria());
		matcher.setTrees(tree, rule.getLeftHandSide());
		matcher.findMatches();
		Set<BidirectionalMap<ExtendedNode, BasicNode>> matches = matcher.getMatches();
		if (matches!=null){if (matches.size()>0)
		{
			for (BidirectionalMap<ExtendedNode, BasicNode> singleMatch : matches)
			{
				mapsOfLhsToTree.add(new FlippedBidirectionalMap<BasicNode, ExtendedNode>(singleMatch));
			}
		}}
		
		return mapsOfLhsToTree;
	}

	/**
	 * print two dot files
	 * 
	 * @param tree
	 * @param rhs
	 * @param dotDir
	 * @param rightLabel 
	 * @param ndx
	 * @throws CompilationException
	 */
	private static void printDotFiles(ExtendedNode lhs, ExtendedNode rhs, String leftLabel, String rightLabel, String dotDir, int ndx) 
		throws CompilationException
	{
		// print out a DOT of the original, and of the generated tree
	
		File lhsFile = new File(dotDir, ndx + "_lhs.dot");
		lhsFile.delete();
		TreeDotFileGenerator<Info> tdfg;
		try
		{
			tdfg = new TreeDotFileGenerator<Info>(new NoAntLemmaPosRelNodeAndEdgeString() ,lhs, leftLabel,lhsFile);
			tdfg.generate();
		} catch (TreeDotFileGeneratorException e)
		{
			throw new CompilationException("Error printing a dot file", e);
		}
		// print generatedTree
		File rhsFile = new File(dotDir, ndx + "_rhs.dot");
		rhsFile.delete();		
		try
		{
			String alteredLabel = "Application of "+rightLabel;
			tdfg = new TreeDotFileGenerator<Info>(new NoAntLemmaPosRelNodeAndEdgeString(),rhs, alteredLabel,rhsFile);
			tdfg.generate();
		} catch (TreeDotFileGeneratorException e)
		{
			throw new CompilationException("Error printing a dot file", e);
		}
	}

	/**
	 * @param englishTree
	 * @return
	 */
	private static ExtendedNode englishToExtendedNode(BasicNode englishTree)
	{
		ExtendedNode newTree = new ExtendedNode(new ExtendedInfo(englishTree.getInfo(), AdditionalInformationServices.emptyInformation()));
		if (englishTree.getChildren() != null)
			for (BasicNode child : englishTree.getChildren())
			{
				ExtendedNode newChild = englishToExtendedNode(child);
				newTree.addChild(newChild);
				newChild.setAntecedent(newTree);
			}
		
		return newTree;
	}

	////////////////////////////////////////////////////////////// MAIN ///////////////////////////////////////////////////////////////////////////
	/**
	 * This method assumes that dot.exe is in the path!
	 * <p>Accepts the properties file as the only arg.
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		Properties props;	
		// open files and feed them to the RuleFactory
		props = new Properties();				
		try {
			props.load(new FileInputStream(args[0]));
		} catch (FileNotFoundException e1) {
			System.out.println("Couldn't load props file " + args[0]);
		} catch (IOException e1) {
			System.out.println("Couldn't load props file " + args[0]);
		}
		
		File dir = new File(props.getProperty("directoryName").trim());
		final String filterStr = props.getProperty("graphFileSuffix").trim();

		// create an english node rule compiler
		EntailmentRuleCompiler compiler = new EntailmentRuleCompiler(); 
		List<RuleWithConfidenceAndDescription<Info, BasicNode>> rulesWithCD = compiler.compileFolder(dir, filterStr);
		
		String outDirStr = props.getProperty("outputDir").trim();
		File outDir = new File(outDirStr);
		FilenameFilter dotFileFilter = new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.endsWith(".dot");
			}
		};
		FilenameFilter dotJpgFileFilter = new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.endsWith(".dot") | name.endsWith(".jpg");
			}
		};

		// clean outdir
		File[] dotFiles = outDir.listFiles(dotJpgFileFilter);
		for (File file : dotFiles)
			file.delete();
		
		String sentence = props.getProperty("sentence").trim();
		
		sentence = "Kashmir is divided between India and Pakistan but both claim the region in its entirety.";
//		sentence = "Coal is important but is not a fuel resource.";		
//		sentence = "However detectives said they had not found any proof that the 35-year-old, who went missing on 18 March, was dead.";
		
		new BasicVerySimpleLoggerInitializer().initLogger();
		//new LogInitializer("B:/workspaceEclipse/engineml/biutee.xml").init();
		
//		RulesViewer rv = new RulesViewer(rulesWithCD, new IdLemmaPosRelNodeString());
//		rv.view();
		
		RuleMatcher matcher = new RuleMatcher(outDirStr);
		List<RuleWithConfidenceAndDescription<Info, BasicNode>> matchedRulesWithCD = matcher.matchRules(rulesWithCD, sentence);
		
		
		
		RulesViewer<Info, BasicNode> rv = new RulesViewer<Info, BasicNode>(matchedRulesWithCD, new IdLemmaPosRelNodeString());
		rv.view(false);
		
		// dot the outDir
		dotFiles = outDir.listFiles(dotFileFilter);
		for (File file : dotFiles)
			Runtime.getRuntime().exec("dot -O -Tjpg " + file);
		
		System.out.println("\n\nMade " + rulesWithCD.size() + " rules.");
		System.out.println("Matched " + matchedRulesWithCD.size() + " rules.");
		System.out.println("See generated trees in " + outDir);
		
		
		
	}
}
