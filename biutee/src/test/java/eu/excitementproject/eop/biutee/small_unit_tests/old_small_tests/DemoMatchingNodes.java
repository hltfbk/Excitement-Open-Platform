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
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lap.biu.en.parser.EnglishSingleTreeParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

public class DemoMatchingNodes
{
	public static void f(String[] args) throws ConfigurationFileDuplicateKeyException, ConfigurationException, TeEngineMlException, ParserRunException, TreeStringGeneratorException, TreeAndParentMapException
	{
		ConfigurationFile configurationFile = new ConfigurationFile(args[0]);
		ConfigurationParams params = configurationFile.getModuleConfiguration("prototype1");
		String miniparParameter = params.get("minipar");
		@SuppressWarnings("deprecation")
		EnglishSingleTreeParser parser = ParserFactory.getParser(miniparParameter);
		parser.setSentence("Yossi loves Danny very much.");
		parser.parse();
		BasicNode tree1Original = parser.getParseTree();
		ExtendedNode tree1 = TreeUtilities.copyFromBasicNode(tree1Original);
		
		System.out.println(TreeUtilities.treeToString(tree1));
		System.out.println("---------------------------------");
		parser.setSentence("Danny loves Yossi");
		parser.parse();
		BasicNode tree2Original = parser.getParseTree();
		ExtendedNode tree2 = TreeUtilities.copyFromBasicNode(tree2Original);
		System.out.println(TreeUtilities.treeToString(tree2));
		
		TreeAndParentMap<ExtendedInfo,ExtendedNode> tree1AndParentMap = new TreeAndParentMap<ExtendedInfo,ExtendedNode>(tree1);
		TreeAndParentMap<ExtendedInfo,ExtendedNode> tree2AndParentMap = new TreeAndParentMap<ExtendedInfo,ExtendedNode>(tree2);
		
		System.out.println("\n--------------------------------\nNo matching nodes:\n");
		Set<ExtendedNode> noMatchNodes = TreeUtilities.findNodesNoMatch(tree2AndParentMap, tree1AndParentMap);
		for (ExtendedNode node : noMatchNodes)
		{
			System.out.println("\""+InfoGetFields.getLemma(node.getInfo())+"\"");
		}
		
		System.out.println("\n--------------------------------\nNo matching relations:\n");
		Set<ExtendedNode> noMatchRelations = TreeUtilities.findRelationsNoMatch(tree2AndParentMap, tree1AndParentMap);
		for (ExtendedNode node : noMatchRelations)
		{
			System.out.println("\""+InfoGetFields.getLemma(node.getInfo())+":"+InfoGetFields.getRelation(node.getInfo())+"\"");
		}
		
		System.out.println("--------------------------------------------------------------");
		System.out.println("Total number of nodes in tree1: "+AbstractNodeUtils.treeToLinkedHashSet(tree1).size());
		System.out.println(String.format("%f.2", TreeUtilities.missingNodesPortion(tree2AndParentMap, tree1AndParentMap)));
		System.out.println(String.format("%f.2", TreeUtilities.missingRelationsPortion(tree2AndParentMap, tree1AndParentMap)));
		
		System.out.println("##################################################################");
		
		
		@SuppressWarnings("deprecation")
		Set<ExtendedNode> nodesNotCompatible = TreeUtilities.findNodesNotCompatible(tree2AndParentMap, tree1AndParentMap);
		for (ExtendedNode node : nodesNotCompatible)
		{
			System.out.println(InfoGetFields.getLemma(node.getInfo()));
		}
		
		System.out.println("-------------------------------------------------------------------");
		
		Set<ExtendedNode> nodesWithBadParents = TreeUtilities.findNodeBadParents(tree2AndParentMap,tree1AndParentMap);
		for (ExtendedNode node : nodesWithBadParents)
		{
			System.out.println(InfoGetFields.getLemma(node.getInfo()));
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
