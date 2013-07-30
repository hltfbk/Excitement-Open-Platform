package eu.excitementproject.eop.biutee.small_unit_tests.old_small_tests;
import eu.excitementproject.eop.biutee.utilities.preprocess.ParserFactory;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.representation.partofspeech.MiniparPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.transformations.datastructures.SimpleNullForbiddenBidirectionalMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.operations.ExtendedSubstitutionRuleApplicationOperation;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

public class DemoRuleApplication
{
	public static SyntacticRule<Info, BasicNode> createRule() throws UnsupportedPosTagStringException
	{
		BasicNode lhsRoot = new BasicNode(new DefaultInfo("lhsRoot", new DefaultNodeInfo("love", "love", 0, null, new DefaultSyntacticInfo(new MiniparPartOfSpeech("V"))), new DefaultEdgeInfo(null)));
		BasicNode lhsSubj = new BasicNode(new DefaultInfo("lhsSubj",DefaultNodeInfo.newVariableDefaultNodeInfo(0, new DefaultSyntacticInfo(new MiniparPartOfSpeech("N"))),new DefaultEdgeInfo(new DependencyRelation("subj", null))));
		BasicNode lhsObj = new BasicNode(new DefaultInfo("lhsObj",DefaultNodeInfo.newVariableDefaultNodeInfo(1, new DefaultSyntacticInfo(new MiniparPartOfSpeech("N"))),new DefaultEdgeInfo(new DependencyRelation("obj", null))));
		lhsRoot.addChild(lhsSubj);
		lhsRoot.addChild(lhsObj);
		
		
		BasicNode rhsRoot = new BasicNode(new DefaultInfo("rhsRoot", new DefaultNodeInfo("like", "like", 0, null, new DefaultSyntacticInfo(new MiniparPartOfSpeech("V"))), new DefaultEdgeInfo(null)));
		BasicNode rhsinter = new BasicNode(new DefaultInfo("rhsinter", new DefaultNodeInfo("inter", "inter", 0, null, new DefaultSyntacticInfo(new MiniparPartOfSpeech("V"))), new DefaultEdgeInfo(new DependencyRelation("inter", null))));
		BasicNode rhsSubj = new BasicNode(new DefaultInfo("rhsSubj",DefaultNodeInfo.newVariableDefaultNodeInfo(0, new DefaultSyntacticInfo(new MiniparPartOfSpeech("N"))),new DefaultEdgeInfo(new DependencyRelation("subj", null))));
		BasicNode rhsObj = new BasicNode(new DefaultInfo("rhsObj",DefaultNodeInfo.newVariableDefaultNodeInfo(1, new DefaultSyntacticInfo(new MiniparPartOfSpeech("N"))),new DefaultEdgeInfo(new DependencyRelation("obj", null))));
		rhsRoot.addChild(rhsinter);
		rhsinter.addChild(rhsSubj);
		rhsinter.addChild(rhsObj);
		
		BidirectionalMap<BasicNode, BasicNode> map = new SimpleNullForbiddenBidirectionalMap<BasicNode, BasicNode>();
		map.put(lhsRoot,rhsRoot);
		map.put(lhsSubj, rhsSubj);
		map.put(lhsObj, rhsObj);
		
		SyntacticRule<Info,BasicNode> ret = new SyntacticRule<Info, BasicNode>(lhsRoot, rhsRoot, map);
		return ret;
	}
	
	public static void f(String[] args) throws TeEngineMlException, ParserRunException, TreeStringGeneratorException, OperationException, TreeAndParentMapException, UnsupportedPosTagStringException
	{
		@SuppressWarnings("deprecation")
		BasicParser parser = ParserFactory.getParser("localhost");
		parser.init();
		try
		{
			parser.setSentence("I love you and him very much.");
			parser.parse();
			BasicNode originalTree = parser.getParseTree();
			ExtendedNode tree = TreeUtilities.copyFromBasicNode(originalTree);
			TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(tree);
			System.out.println(TreeUtilities.treeToString(tree));
			System.out.println(StringUtil.generateStringOfCharacter('-', 100));
			
			ExtendedNode loveNode = null;
			ExtendedNode subjNode = null;
			ExtendedNode objNode = null;
			for (ExtendedNode node : TreeIterator.iterableTree(tree))
			{
				if (InfoGetFields.getLemma(node.getInfo()).equalsIgnoreCase("love"))
					loveNode = node;
				if (InfoGetFields.getRelation(node.getInfo()).equalsIgnoreCase("subj"))
					subjNode = node;
				if (InfoGetFields.getRelation(node.getInfo()).equalsIgnoreCase("obj"))
					objNode = node;
			}
			SyntacticRule<Info,BasicNode> rule = createRule();
			BasicNode lhsloveNode = null;
			BasicNode lhssubjNode = null;
			BasicNode lhsobjNode = null;
			for (BasicNode node : AbstractNodeUtils.treeToLinkedHashSet(rule.getLeftHandSide()))
			{
				if (InfoGetFields.getLemma(node.getInfo()).equalsIgnoreCase("love"))
					lhsloveNode = node;
				if (InfoGetFields.getRelation(node.getInfo()).equalsIgnoreCase("subj"))
					lhssubjNode = node;
				if (InfoGetFields.getRelation(node.getInfo()).equalsIgnoreCase("obj"))
					lhsobjNode = node;
			}
			BidirectionalMap<BasicNode, ExtendedNode> mapLhsToTree = new SimpleNullForbiddenBidirectionalMap<BasicNode, ExtendedNode>();
			mapLhsToTree.put(lhsloveNode,loveNode);
			mapLhsToTree.put(lhssubjNode,subjNode);
			mapLhsToTree.put(lhsobjNode, objNode);
			if (loveNode==null)System.out.println("loveNode is null");
			if (lhsloveNode==null)System.out.println("lhsloveNode is null");
			if (lhsloveNode==rule.getLeftHandSide())System.out.println("OK");else System.out.println("not OK");
			
			if (subjNode==null)System.out.println("subjNode is null");else System.out.println("subjNode is not null");
			if (objNode==null)System.out.println("objNode is null");else System.out.println("objNode is not null");

			if (lhssubjNode==null)System.out.println("lhssubjNode is null");else System.out.println("lhssubjNode is not null");
			if (lhsobjNode==null)System.out.println("lhsobjNode is null");else System.out.println("lhsobjNode is not null");

			ExtendedSubstitutionRuleApplicationOperation operation = new ExtendedSubstitutionRuleApplicationOperation(treeAndParentMap, treeAndParentMap, rule, mapLhsToTree);
			//IntroductionRuleApplicationOperation operation = new IntroductionRuleApplicationOperation(treeAndParentMap, treeAndParentMap, rule, mapLhsToTree);
			operation.generate();
			ExtendedNode generated = operation.getGeneratedTree();
			System.out.println(TreeUtilities.treeToString(generated));
		}
		finally
		{
			parser.cleanUp();
		}
		
		
		
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			f(args);
			
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}

	}

}
