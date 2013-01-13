package ac.biu.nlp.nlp.engineml.small_unit_tests;
import java.io.File;

import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleWithConfidenceAndDescription;
import ac.biu.nlp.nlp.engineml.operations.rules.SetBagOfRulesRuleBase;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.TreeUtilities;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.StringUtil;

public class DemoShowGenericRules
{
	public static void f(String[] args) throws RuleBaseException, ClassNotFoundException, TreeStringGeneratorException
	{
		String genericRulesFileName = args[0];
		SetBagOfRulesRuleBase<Info, BasicNode> ruleBase =
			SetBagOfRulesRuleBase.fromSimpleSerializationFile(new File(genericRulesFileName));
		
		for (RuleWithConfidenceAndDescription<Info,BasicNode> rule : ruleBase.getRules())
		{
			String leftString = TreeUtilities.treeToString(rule.getRule().getLeftHandSide());
			String rightString = TreeUtilities.treeToString(rule.getRule().getRightHandSide());
			
			System.out.println(leftString);
			System.out.println(StringUtil.generateStringOfCharacter('-', 50));
			System.out.println(rightString);
			System.out.println(StringUtil.generateStringOfCharacter('-', 50));
			
			for (BasicNode mappedLeftNode : rule.getRule().getMapNodes().leftSet())
			{
				BasicNode mappedRightNode = rule.getRule().getMapNodes().leftGet(mappedLeftNode);
				String leftId = mappedLeftNode.getInfo().getId();
				String rightId = mappedRightNode.getInfo().getId();
				System.out.println(leftId+" -> "+rightId);
			}
			
			System.out.println("Confidence = "+rule.getConfidence()+". Description = "+rule.getDescription());
			
			System.out.println();
			System.out.println(StringUtil.generateStringOfCharacter('*', 50));
			System.out.println();
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
