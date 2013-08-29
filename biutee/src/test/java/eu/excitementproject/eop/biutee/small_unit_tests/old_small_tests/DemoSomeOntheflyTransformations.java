package eu.excitementproject.eop.biutee.small_unit_tests.old_small_tests;
import eu.excitementproject.eop.biutee.utilities.preprocess.ParserFactory;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
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
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.operations.DuplicateAndMoveNodeOperation;
import eu.excitementproject.eop.transformations.operations.operations.InsertNodeOperation;
import eu.excitementproject.eop.transformations.operations.operations.MoveNodeOperation;
import eu.excitementproject.eop.transformations.operations.operations.SubstituteNodeOperation;
import eu.excitementproject.eop.transformations.operations.operations.SubstituteSubtreeOperation;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

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
			for (ExtendedNode node : TreeIterator.iterableTree(tree))
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
			for (ExtendedNode node : TreeIterator.iterableTree(tree))
			{
				if (InfoGetFields.getLemma(node.getInfo()).equalsIgnoreCase("this"))
				{
					thisNode = node;
					break;
				}
			}
			if (null==thisNode)throw new RuntimeException();

			
			ExtendedNode myNode = null;
			for (ExtendedNode node : TreeIterator.iterableTree(tree))
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
			for(ExtendedNode node : TreeIterator.iterableTree(tree2))
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
