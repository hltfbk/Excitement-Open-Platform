package eu.excitementproject.eop.biutee.small_unit_tests;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;


import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import eu.excitementproject.eop.biutee.utilities.preprocess.ParserFactory;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultMatchCriteria;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.representation.parse.tree.match.AllEmbeddedMatcher;
import eu.excitementproject.eop.common.representation.parse.tree.match.Matcher;
import eu.excitementproject.eop.common.representation.parse.tree.match.MatcherException;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.PARSER;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.TemplateToTree;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.TemplateToTreeException;
import eu.excitementproject.eop.lap.biu.en.parser.EnglishSingleTreeParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.RuleByDynamicRuleBaseFinder;
import eu.excitementproject.eop.transformations.operations.rules.DynamicRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.rules.distsim.DistSimParameters;
import eu.excitementproject.eop.transformations.operations.rules.distsim.DistSimRuleBase;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

@Deprecated
public class DynRuleBaseTester
{
	public static final int LIMIT_DISTSIM_RULES = 30;
	public static void main(String[] args)
	{
		try
		{
			BasicConfigurator.configure();
			DynRuleBaseTester app = new DynRuleBaseTester(args);
			app.run();
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}
	}

	public DynRuleBaseTester(String[] args)
	{
		this.miniparParameter = args[0];
		this.parserMode = PARSER.valueOf(args[1]);
	}
	
	public void run() throws SQLException, RuleBaseException, TeEngineMlException, ParserRunException, TreeStringGeneratorException, TemplateToTreeException, MatcherException, InterruptedException, TreeAndParentMapException, OperationException
	{
		
		System.out.println("init...");
		init();
		System.out.println("init done.");
		try
		{
			System.out.println("test...");
			String sentence = "I say up him.";
			testSentence(sentence);
			System.out.println("test done.");
			
		}
		finally
		{
			System.out.println("clean up...");
			cleanUp();
			System.out.println("clean up done.");
		}
	}
	
	public void init() throws SQLException, RuleBaseException, TeEngineMlException, ParserRunException
	{
		ruleBaseName = "origdirt";
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setServerName("qa-srv");
		dataSource.setPort(3308);
		dataSource.setUser("db_readonly");
		//dataSource.setPassword("");
		distSimConnection = dataSource.getConnection();
		
		DistSimParameters originalDirtParameters =
			new DistSimParameters("original_dirt.od_templates", "original_dirt.od_rules", LIMIT_DISTSIM_RULES, 2*Constants.DEFAULT_DIRT_LIKE_RESOURCES_CACHE_SIZE, Constants.DEFAULT_DIRT_LIKE_RESOURCES_CACHE_SIZE);

		
		ruleBase = new DistSimRuleBase(distSimConnection,originalDirtParameters,ruleBaseName,parserMode);
		
		
		parser = ParserFactory.getParser(miniparParameter);
	}
	
	public void cleanUp() throws SQLException
	{
		if (distSimConnection!=null)
			distSimConnection.close();
		
		if (parser!=null)
			parser.cleanUp();
	}
	
	public void testSentence(String sentence) throws ParserRunException, TeEngineMlException, TreeStringGeneratorException, TemplateToTreeException, MatcherException, InterruptedException, TreeAndParentMapException, OperationException
	{
		parser.setSentence(sentence);
		parser.parse();
		BasicNode tree = parser.getParseTree();
		System.out.println("parse tree of sentence:");
		System.out.println(TreeUtilities.treeToString(tree));
		System.out.println(StringUtil.generateStringOfCharacter('-', 100));
		TemplateToTree ttt = new TemplateToTree("n<p:up:p<v:say:v>subj>n",parserMode);
		ttt.createTree();
		BasicNode ruleTree = ttt.getTree();
		System.out.println("tree of rule:");
		System.out.println(TreeUtilities.treeToString(ruleTree));
		BasicNode sayNode = null;
		for (BasicNode node : AbstractNodeUtils.treeToSet(tree))
		{
			if (InfoGetFields.getLemma(node.getInfo()).equalsIgnoreCase("say"))
				sayNode = node;
		}
		Matcher<Info, Info, BasicNode, BasicNode> matcher = new Matcher<Info, Info, BasicNode, BasicNode>(new DefaultMatchCriteria());
		matcher.setTreeRoots(sayNode, ruleTree);
		matcher.findMathces();
		System.out.println("matcher.getMatches().size() = " + matcher.getMatches().size());
		
		AllEmbeddedMatcher<Info, Info, BasicNode, BasicNode> aem =
			new AllEmbeddedMatcher<Info, Info, BasicNode, BasicNode>(new DefaultMatchCriteria());
		aem.setTrees(tree, ruleTree);
		aem.findMatches();
		Set<BidirectionalMap<BasicNode, BasicNode>> matches = aem.getMatches();
		System.out.println("matches.size() = " + matches.size());
		System.out.println(StringUtil.generateStringOfCharacter('-', 100));
		
		
		
		findSpecs(tree);
		printSpecsInformation();
		System.out.println(StringUtil.generateStringOfCharacter('-', 100));
		System.out.println("sleeping...");
		Thread.sleep(1000);
		System.out.println("starting again...");
		findSpecs(tree);
		printSpecsInformation();
		
	}
	
	private void printSpecsInformation()
	{
		System.out.println("specs size = " + specs.size());
		for (RuleSpecification spec : specs)
		{
			System.out.println(spec.toString());
		}
	}
	

	private void findSpecs(BasicNode originalTree) throws TeEngineMlException, TreeAndParentMapException, OperationException
	{
		ExtendedNode tree = TreeUtilities.copyFromBasicNode(originalTree);
		TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(tree);
		RuleByDynamicRuleBaseFinder finder = new RuleByDynamicRuleBaseFinder(treeAndParentMap, false, ruleBase, ruleBaseName);
		finder.find();
		specs = finder.getSpecs();
	}
	
	

	private final PARSER parserMode;
	private DynamicRuleBase<Info, BasicNode> ruleBase;
	private String ruleBaseName;
	private Set<RuleSpecification> specs;
	private EnglishSingleTreeParser parser;
	private String miniparParameter;
	private Connection distSimConnection;
	
	
}
