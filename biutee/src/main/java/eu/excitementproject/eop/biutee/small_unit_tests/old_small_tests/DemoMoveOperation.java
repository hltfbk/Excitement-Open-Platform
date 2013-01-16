package eu.excitementproject.eop.biutee.small_unit_tests.old_small_tests;
import java.util.Set;

import eu.excitementproject.eop.biutee.utilities.preprocess.ParserFactory;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
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
import eu.excitementproject.eop.transformations.operations.finders.MoveNodeOperationFinder;
import eu.excitementproject.eop.transformations.operations.operations.MoveNodeOperation;
import eu.excitementproject.eop.transformations.operations.specifications.MoveNodeSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

public class DemoMoveOperation
{
	public static void f(String[] args) throws TeEngineMlException, ParserRunException, TreeStringGeneratorException, OperationException, TreeAndParentMapException
	{
		String text1 = "I saw that Dana loves you";
		String hyp1 = "I love you.";
		@SuppressWarnings("deprecation")
		EnglishSingleTreeParser parser = ParserFactory.getParser(args[0]);
		parser.init();
		parser.setSentence(text1);
		parser.parse();
		BasicNode originalText1Tree = parser.getParseTree();
		ExtendedNode text1Tree = TreeUtilities.copyFromBasicNode(originalText1Tree);
		parser.setSentence(hyp1);
		parser.parse();
		BasicNode hyp1TreeOriginal = parser.getParseTree();
		ExtendedNode hyp1Tree = TreeUtilities.copyFromBasicNode(hyp1TreeOriginal);
		
		System.out.println(TreeUtilities.treeToString(hyp1Tree));
		System.out.println(StringUtil.generateStringOfCharacter('*', 500));
		
		System.out.println(TreeUtilities.treeToString(text1Tree));
		
		TreeAndParentMap<ExtendedInfo,ExtendedNode> text1TreeAndMap = new TreeAndParentMap<ExtendedInfo,ExtendedNode>(text1Tree);
		TreeAndParentMap<ExtendedInfo,ExtendedNode> hyp1TreeAndMap = new TreeAndParentMap<ExtendedInfo,ExtendedNode>(hyp1Tree);
		
		System.out.println(StringUtil.generateStringOfCharacter('*', 500));
		
		AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria = new DefaultAlignmentCriteria();
		
		MoveNodeOperationFinder finder = new MoveNodeOperationFinder(text1TreeAndMap, hyp1TreeAndMap,alignmentCriteria);
		finder.find();
		Set<MoveNodeSpecification> moveSpecs = finder.getSpecs();
		System.out.println(moveSpecs.size());
		for (MoveNodeSpecification spec : moveSpecs)
		{
			System.out.print("move: "+
					spec.getTextNodeToMove().getInfo().getId()+": "+InfoGetFields.getLemma(spec.getTextNodeToMove().getInfo())
			);
			System.out.print(" to: "+
					spec.getTextNodeToBeParent().getInfo().getId()+": "+InfoGetFields.getLemma(spec.getTextNodeToBeParent().getInfo())
			);
			System.out.println(" with relation: "+
					InfoGetFields.getRelation(spec.getNewEdgeInfo())
					);
		}
		
		System.out.println(StringUtil.generateStringOfCharacter('*', 500));
		
		for (MoveNodeSpecification spec : moveSpecs)
		{
			System.out.println(StringUtil.generateStringOfCharacter('*', 500));
			System.out.println();
			System.out.print("move: "+
					spec.getTextNodeToMove().getInfo().getId()+": "+InfoGetFields.getLemma(spec.getTextNodeToMove().getInfo())
			);
			System.out.print(" to: "+
					spec.getTextNodeToBeParent().getInfo().getId()+": "+InfoGetFields.getLemma(spec.getTextNodeToBeParent().getInfo())
			);
			System.out.println(" with relation: "+
					InfoGetFields.getRelation(spec.getNewEdgeInfo())
					);

			MoveNodeOperation operation = new MoveNodeOperation(text1TreeAndMap, hyp1TreeAndMap, spec.getTextNodeToMove(), spec.getTextNodeToBeParent(), spec.getNewEdgeInfo());
			operation.generate();
			ExtendedNode generatedTree = operation.getGeneratedTree();
			System.out.println();
			System.out.println(
					TreeUtilities.treeToString(generatedTree)
			);
			
		}
		
		
//		Iterator<MoveNodeSpecification> moveSpecsIterator = moveSpecs.iterator();
//		moveSpecsIterator.next();
//		moveSpecsIterator.next();
//		MoveNodeSpecification spec = moveSpecsIterator.next();
//		System.out.print(spec.getTextNodeToMove().getInfo().getId()+": ");
//		System.out.println(InfoGetFields.getLemma(spec.getTextNodeToMove().getInfo()));
//		
//		
//		MoveNodeOperation operation = new MoveNodeOperation(text1TreeAndMap, hyp1TreeAndMap, spec.getTextNodeToMove(), spec.getTextNodeToBeParent(), spec.getNewEdgeInfo());
//		operation.generate();
//		EnglishNode generatedTree = operation.getGeneratedTree();
//		System.out.println(StringUtil.generateStringOfCharacter('*', 500));
//		System.out.println(TreeUtilities.treeToString(generatedTree));
		
		
		
		
		
		
		
//		InsertNodeOperationFinder finder = new InsertNodeOperationFinder(text1TreeAndMap, hyp1TreeAndMap);
//		finder.find();
//		Set<InsertNodeSpecification> setInsertNodeSpecifications = finder.getInsertSpecifications();
//		InsertNodeSpecification spec1 = setInsertNodeSpecifications.iterator().next();
//		System.out.println("########################################################################################");
//		System.out.print("inserting node: "+InfoGetFields.getLemma(spec1.getHypothesisNodeToInsert().getInfo()));
//		System.out.print(" into: "+InfoGetFields.getLemma(spec1.getTextNodeToBeParent().getInfo()));
//		System.out.println();
//		InsertNodeOperation operation1 = new InsertNodeOperation(text1TreeAndMap, hyp1TreeAndMap, spec1.getHypothesisNodeToInsert().getInfo(), spec1.getTextNodeToBeParent());
//		operation1.generate();
//		EnglishNode text1ModifiedTree = operation1.getGeneratedTree();
//		
//		System.out.println("########################################################################################");
//		System.out.println(TreeUtilities.treeToString(text1ModifiedTree));
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
