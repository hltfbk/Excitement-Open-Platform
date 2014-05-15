package eu.excitementproject.eop.biutee.small_unit_tests.old_small_tests;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import eu.excitementproject.eop.biutee.utilities.preprocess.ParserFactory;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.lap.biu.en.parser.EnglishSingleTreeParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.transformations.alignment.AlignmentCriteria;
import eu.excitementproject.eop.transformations.alignment.DefaultAlignmentCriteria;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.InsertNodeOperationFinder;
import eu.excitementproject.eop.transformations.operations.finders.MoveNodeOperationFinder;
import eu.excitementproject.eop.transformations.operations.specifications.InsertNodeSpecification;
import eu.excitementproject.eop.transformations.operations.specifications.MoveNodeSpecification;
import eu.excitementproject.eop.transformations.representation.AdditionalInformationServices;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.AdvancedEqualities;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.ContentAncestorSetter;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

public class DemoOntheflyWithAdvancedEqualities
{
	public static void f(String[] args) throws TeEngineMlException, ParserRunException, IOException, TreeStringGeneratorException, TreeAndParentMapException, OperationException
	{
		if (args.length<1) throw new TeEngineMlException("args");
		@SuppressWarnings("deprecation")
		EnglishSingleTreeParser parser = ParserFactory.getParser(args[0]);
		parser.init();
		String sentence = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		ExtendedNode prevTree = null;
		System.out.println("Enter sentence:");
		sentence = reader.readLine();
		while (sentence != null)
		{
			ExtendedNode extTree = createTree(sentence,parser);

			String strTree = TreeUtilities.treeToString(extTree);
			System.out.println();
			System.out.println(strTree);
			System.out.println(StringUtil.generateStringOfCharacter('-', 50));
			
			if (prevTree!=null)
			{
				testAdvancedEqualities(extTree,prevTree);
				textFinders(extTree,prevTree);
			}
			prevTree = extTree;
			
			
			System.out.println("Enter sentence:");
			sentence = reader.readLine();
			if (sentence.equals("exit"))
				sentence = null;
		}
	}
	
	public static void textFinders(ExtendedNode textTree, ExtendedNode hypothesisTree) throws TreeAndParentMapException, OperationException, TeEngineMlException
	{
		TreeAndParentMap<ExtendedInfo, ExtendedNode> text = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(textTree); 
		TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(hypothesisTree);
		
		AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria = new DefaultAlignmentCriteria();
		InsertNodeOperationFinder finder = new InsertNodeOperationFinder(text, hypothesis,alignmentCriteria);
		finder.find();
		Set<InsertNodeSpecification> specs = finder.getSpecs();
		System.out.println("InsertNodeSpecification");
		for (InsertNodeSpecification spec : specs)
		{
			System.out.println(spec.toString());
		}
		
		System.out.println(StringUtil.generateStringOfCharacter('~', 50));
		MoveNodeOperationFinder moveFinder = new MoveNodeOperationFinder(text, hypothesis,alignmentCriteria);
		moveFinder.find();
		Set<MoveNodeSpecification> moveSpecs = moveFinder.getSpecs();
		System.out.println("MoveNodeSpecification");
		for (MoveNodeSpecification moveSpec : moveSpecs)
		{
			System.out.println(moveSpec.toString());
		}
		System.out.println(StringUtil.generateStringOfCharacter('~', 50));
	}
	
	public static void testAdvancedEqualities(ExtendedNode textTree, ExtendedNode hypothesisTree) throws TreeAndParentMapException
	{
		TreeAndParentMap<ExtendedInfo, ExtendedNode> text = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(textTree); 
		TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(hypothesisTree);
		Set<ExtendedNode> missingNodes = AdvancedEqualities.findMissingNodes(text, hypothesis);
		System.out.println("missing nodes:");
		for (ExtendedNode missingNode : missingNodes)
		{
			System.out.println(missingNode.getInfo().getId()+": "+InfoGetFields.getLemma(missingNode.getInfo()));
		}
		System.out.println(StringUtil.generateStringOfCharacter('~', 50));
		Set<ExtendedNode> missingRelations = AdvancedEqualities.findMissingRelations(text, hypothesis);
		System.out.println("missing relations:");
		for (ExtendedNode missingNode : missingRelations)
		{
			System.out.println(missingNode.getInfo().getId()+": "+InfoGetFields.getLemma(missingNode.getInfo()));
		}
		System.out.println(StringUtil.generateStringOfCharacter('~', 50));
	}
	
	public static  ExtendedNode createTree(String sentence, EnglishSingleTreeParser parser) throws ParserRunException, TeEngineMlException
	{
		parser.setSentence(sentence);
		parser.parse();
		BasicNode engTree = parser.getParseTree();
		
		TreeCopier<Info, BasicNode, ExtendedInfo, ExtendedNode> treeCopier =
			new TreeCopier<Info, BasicNode, ExtendedInfo, ExtendedNode>(
					engTree,
					new TreeCopier.InfoConverter<BasicNode, ExtendedInfo>()
					{
						public ExtendedInfo convert(BasicNode oi)
						{
							return new ExtendedInfo(oi.getInfo(), AdditionalInformationServices.emptyInformation());
						}
					},
					new ExtendedNodeConstructor()
					);
		
		treeCopier.copy();
		ExtendedNode extTree = treeCopier.getGeneratedTree();
		if (!TreeUtilities.isArtificialRoot(extTree))
		{
			extTree = TreeUtilities.addArtificialRoot(extTree);
		}
		
		extTree = ContentAncestorSetter.generateWithAncestorInformation(extTree);

		return extTree;
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
