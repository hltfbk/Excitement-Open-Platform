package ac.biu.nlp.nlp.engineml.small_unit_tests.old_small_tests;


import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.operations.DuplicateAndMoveNodeOperation;
import ac.biu.nlp.nlp.engineml.operations.operations.InsertNodeOperation;
import ac.biu.nlp.nlp.engineml.operations.operations.MoveNodeOperation;
import ac.biu.nlp.nlp.engineml.operations.operations.SubstituteNodeOperation;
import ac.biu.nlp.nlp.engineml.operations.operations.SubstituteSubtreeOperation;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNodeConstructor;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.TreeUtilities;
import ac.biu.nlp.nlp.engineml.utilities.preprocess.ParserFactory;
import ac.biu.nlp.nlp.general.ExceptionUtil;
import ac.biu.nlp.nlp.general.StringUtil;
import ac.biu.nlp.nlp.general.ValueSetMap;
import ac.biu.nlp.nlp.instruments.parse.BasicParser;
import ac.biu.nlp.nlp.instruments.parse.ParserRunException;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultNodeInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultSyntacticInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.NodeInfo;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNodeUtils;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import ac.biu.nlp.nlp.representation.MiniparPartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;

public class DemoSomeOntheflyTransformations
{
	public static void f(String[] args) throws TeEngineMlException, ParserRunException, TreeStringGeneratorException, OperationException, TreeAndParentMapException, UnsupportedPosTagStringException 
	{
		@SuppressWarnings("deprecation")
		BasicParser parser =  ParserFactory.getParser("localhost");
		parser.init();
		try
		{
			parser.setSentence("This is my day.");
			parser.parse();
			BasicNode originalTree = parser.getParseTree();
			ExtendedNode tree = TreeUtilities.copyFromBasicNode(originalTree);
			System.out.println(TreeUtilities.treeToString(tree));
			
			ExtendedNode copied = AbstractNodeUtils.strictTypeCopyTree(tree, new ExtendedNodeConstructor()).leftGet(tree);
			System.out.println("---");
			System.out.println(TreeUtilities.treeToString(copied));
			
			ExtendedNode dayNode = null;
			for (ExtendedNode node : AbstractNodeUtils.treeToSet(tree))
			{
				if (InfoGetFields.getLemma(node.getInfo()).equalsIgnoreCase("day"))
				{
					dayNode = node;
					break;
				}
			}
			TreeAndParentMap<ExtendedInfo,ExtendedNode> tapm = new TreeAndParentMap<ExtendedInfo,ExtendedNode>(tree);
			NodeInfo ni = new DefaultNodeInfo("week", "week", 0, null, new DefaultSyntacticInfo(new MiniparPartOfSpeech("N")));
			//Info info = new DefaultInfo("100", ni, new DefaultEdgeInfo(new DependencyRelation("i", null)));
			
			SubstituteNodeOperation operation = new SubstituteNodeOperation(tapm, tapm, dayNode, ni,dayNode.getInfo().getAdditionalNodeInformation());
			operation.generate();
			ExtendedNode stree = operation.getGeneratedTree();
			System.out.println(TreeUtilities.treeToString(stree));
			System.out.println(StringUtil.generateStringOfCharacter('-', 200));
			
			ValueSetMap<ExtendedNode, ExtendedNode> vsm = operation.getMapOriginalToGenerated();
			int index=1;
			for (ExtendedNode node : vsm.keySet())
			{
				System.out.println(index);
				for (ExtendedNode vnode : vsm.get(node))
				{
					System.out.println("from node: "+InfoGetFields.getLemma(node.getInfo())+" to "+InfoGetFields.getLemma(vnode.getInfo()));
				}
				++index;
				
			}
			
			
			
			ExtendedNode thisNode = null;
			for (ExtendedNode node : AbstractNodeUtils.treeToSet(tree))
			{
				if (InfoGetFields.getLemma(node.getInfo()).equalsIgnoreCase("this"))
				{
					thisNode = node;
					break;
				}
			}
			if (null==thisNode)throw new RuntimeException();

			
			ExtendedNode myNode = null;
			for (ExtendedNode node : AbstractNodeUtils.treeToSet(tree))
			{
				if (InfoGetFields.getLemma(node.getInfo()).equalsIgnoreCase("my"))
				{
					myNode = node;
					break;
				}
			}
			if (null==myNode)throw new RuntimeException();

			
			
			
			MoveNodeOperation moperation = new MoveNodeOperation(tapm, tapm, myNode, thisNode, dayNode.getInfo().getEdgeInfo());
			moperation.generate();
			ExtendedNode treeMove = moperation.getGeneratedTree();
			System.out.println(TreeUtilities.treeToString(treeMove));
			
			ValueSetMap<ExtendedNode, ExtendedNode> vsm2 = moperation.getMapOriginalToGenerated();
			index=1;
			for (ExtendedNode node : vsm2.keySet())
			{
				System.out.println(index);
				for (ExtendedNode vnode : vsm2.get(node))
				{
					System.out.println("from node: "+InfoGetFields.getLemma(node.getInfo())+" to "+InfoGetFields.getLemma(vnode.getInfo()));
				}
				++index;
				
			}
			
			
			InsertNodeOperation ioperation = new InsertNodeOperation(tapm, tapm, dayNode.getInfo(), thisNode);
			ioperation.generate();
			ExtendedNode itree = ioperation.getGeneratedTree();
			System.out.println(TreeUtilities.treeToString(itree));
			
			
			ValueSetMap<ExtendedNode, ExtendedNode> vsm3 = ioperation.getMapOriginalToGenerated();
			index=1;
			for (ExtendedNode node : vsm3.keySet())
			{
				System.out.println(index);
				for (ExtendedNode vnode : vsm3.get(node))
				{
					System.out.println("from node: "+InfoGetFields.getLemma(node.getInfo())+" to "+InfoGetFields.getLemma(vnode.getInfo()));
				}
				++index;
				
			}
			
			
			DuplicateAndMoveNodeOperation damoperation = new DuplicateAndMoveNodeOperation(tapm, tapm, myNode, thisNode, dayNode.getInfo().getEdgeInfo());
			damoperation.generate();
			ExtendedNode damtree = damoperation.getGeneratedTree();
			System.out.println(TreeUtilities.treeToString(damtree));
			
			ValueSetMap<ExtendedNode, ExtendedNode> vsm4 = damoperation.getMapOriginalToGenerated();
			index=1;
			for (ExtendedNode node : vsm4.keySet())
			{
				System.out.println(index);
				for (ExtendedNode vnode : vsm4.get(node))
				{
					System.out.println("from node: "+InfoGetFields.getLemma(node.getInfo())+" to "+InfoGetFields.getLemma(vnode.getInfo()));
				}
				++index;
				
			}
			
			parser.setSentence("The day is nice.");
			parser.parse();
			BasicNode tree2Original = parser.getParseTree();
			ExtendedNode tree2 = TreeUtilities.copyFromBasicNode(tree2Original);
			ExtendedNode day2node = null;
			for(ExtendedNode node : AbstractNodeUtils.treeToSet(tree2))
			{
				if (InfoGetFields.getLemma(node.getInfo()).equalsIgnoreCase("day"))
				{
					day2node = node;
					break;
				}
			}
			SubstituteSubtreeOperation ssoperation = new SubstituteSubtreeOperation(tapm, tapm, dayNode, day2node,null);
			ssoperation.generate();
			ExtendedNode sstree =  ssoperation.getGeneratedTree();
			System.out.println(TreeUtilities.treeToString(sstree));
			ValueSetMap<ExtendedNode, ExtendedNode> vsm5 = ssoperation.getMapOriginalToGenerated();
			index=1;
			for (ExtendedNode node : vsm5.keySet())
			{
				System.out.println(index);
				for (ExtendedNode vnode : vsm5.get(node))
				{
					System.out.println("from node: "+node.getInfo().getId()+":"+InfoGetFields.getLemma(node.getInfo())+" to "+InfoGetFields.getLemma(vnode.getInfo()));
				}
				++index;
				
			}

			


			
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
