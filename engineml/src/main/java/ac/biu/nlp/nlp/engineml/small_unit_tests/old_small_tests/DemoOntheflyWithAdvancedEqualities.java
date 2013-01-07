package ac.biu.nlp.nlp.engineml.small_unit_tests.old_small_tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.alignment.AlignmentCriteria;
import ac.biu.nlp.nlp.engineml.alignment.DefaultAlignmentCriteria;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.finders.InsertNodeOperationFinder;
import ac.biu.nlp.nlp.engineml.operations.finders.MoveNodeOperationFinder;
import ac.biu.nlp.nlp.engineml.operations.specifications.InsertNodeSpecification;
import ac.biu.nlp.nlp.engineml.operations.specifications.MoveNodeSpecification;
import ac.biu.nlp.nlp.engineml.representation.AdditionalInformationServices;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNodeConstructor;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.AdvancedEqualities;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.ContentAncestorSetter;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.TreeUtilities;
import ac.biu.nlp.nlp.engineml.utilities.preprocess.ParserFactory;
import ac.biu.nlp.nlp.general.ExceptionUtil;
import ac.biu.nlp.nlp.general.StringUtil;
import ac.biu.nlp.nlp.instruments.parse.EnglishSingleTreeParser;
import ac.biu.nlp.nlp.instruments.parse.ParserRunException;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeCopier;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;

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
