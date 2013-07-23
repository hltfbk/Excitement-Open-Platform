package eu.excitementproject.eop.biutee.small_unit_tests.old_small_tests;
import java.util.Set;

import eu.excitementproject.eop.biutee.utilities.preprocess.ParserFactory;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lap.biu.en.parser.EnglishSingleTreeParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.operations.InsertNodeOperation;
import eu.excitementproject.eop.transformations.operations.operations.MoveNodeOperation;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

public class DemoMoveInsert
{
	public static void f(String[] args) throws ConfigurationException, TeEngineMlException, ParserRunException, TreeStringGeneratorException, OperationException, TreeAndParentMapException
	{
		ConfigurationFile confFile = new ConfigurationFile(args[0]);
		ConfigurationParams params = confFile.getModuleConfiguration("prototype1");
		
		String miniparArg = params.get("minipar");
		@SuppressWarnings("deprecation")
		EnglishSingleTreeParser parser = ParserFactory.getParser(miniparArg);
		parser.setSentence("I love you very much.");
		parser.parse();
		BasicNode originalTree1 = parser.getParseTree();
		ExtendedNode tree1 = TreeUtilities.copyFromBasicNode(originalTree1);
		TreeAndParentMap<ExtendedInfo,ExtendedNode> tree1AndParentMap = new TreeAndParentMap<ExtendedInfo,ExtendedNode>(tree1);
		
		String tree1Str = TreeUtilities.treeToString(tree1);
		System.out.println(tree1Str);
		
		Set<ExtendedNode> tree1Set = AbstractNodeUtils.treeToLinkedHashSet(tree1);
		ExtendedNode muchNode = null;
		for (ExtendedNode node : tree1Set)
		{
			if (InfoGetFields.getLemma(node.getInfo()).equalsIgnoreCase("much"))
				muchNode = node;
		}
		if (muchNode!=null)
			System.out.println("found");
		
		ExtendedNode loveNode = null;
		for (ExtendedNode node : tree1Set)
		{
			if (InfoGetFields.getLemma(node.getInfo()).equalsIgnoreCase("love"))
				loveNode = node;
		}
		if (loveNode!=null)
			System.out.println("found");
		
		MoveNodeOperation operation = new MoveNodeOperation(new TreeAndParentMap<ExtendedInfo,ExtendedNode>(tree1), new TreeAndParentMap<ExtendedInfo,ExtendedNode>(tree1), muchNode, loveNode, loveNode.getInfo().getEdgeInfo());
		operation.generate();
		ExtendedNode generatedTree = operation.getGeneratedTree();
		System.out.println(TreeUtilities.treeToString(generatedTree));
		
		System.out.println("-------------------------------------------------------------");
		
		parser.setSentence("John loves you very much.");
		parser.parse();
		BasicNode originalTree2 = parser.getParseTree();
		ExtendedNode tree2 = TreeUtilities.copyFromBasicNode(originalTree2);
		
		ExtendedNode johnNode = null;
		Set<ExtendedNode> tree2Set = AbstractNodeUtils.treeToLinkedHashSet(tree2);
		for (ExtendedNode node : tree2Set)
		{
			if (InfoGetFields.getLemma(node.getInfo()).equalsIgnoreCase("John"))
				johnNode = node;
		}
		if (johnNode!=null)
			System.out.println("found.");
		
		InsertNodeOperation operation2 = new InsertNodeOperation(tree1AndParentMap, new TreeAndParentMap<ExtendedInfo,ExtendedNode>(tree2), johnNode.getInfo(), loveNode);
		operation2.generate();
		ExtendedNode generatedTree2 = operation2.getGeneratedTree();
		System.out.println(TreeUtilities.treeToString(generatedTree2));
		
		
		
		
		
		
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
