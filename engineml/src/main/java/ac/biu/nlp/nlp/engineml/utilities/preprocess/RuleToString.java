package ac.biu.nlp.nlp.engineml.utilities.preprocess;

import ac.biu.nlp.nlp.engineml.operations.rules.Rule;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleWithConfidenceAndDescription;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.IdLemmaPosRelNodeString;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeStringGenerator;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;

/**
 * Static methods that represent a given rule as a string.
 * 
 * @author Asher Stern
 * @since Aug 21, 2011
 *
 */
public class RuleToString
{
	
	public static <T extends Info, S extends AbstractNode<T, S>>
	String ruleToString(RuleWithConfidenceAndDescription<T,S> rule) throws TreeStringGeneratorException
	{
		StringBuffer sb = new StringBuffer();
		sb.append("rule: ");
		sb.append(rule.getDescription());
		sb.append(" with confidence: ");
		sb.append(String.format("%-4.4f",rule.getConfidence()));
		sb.append("\n");
		sb.append(ruleToString(rule.getRule()));
		
		return sb.toString();
	}

	public static <T extends Info, S extends AbstractNode<T, S>>
	String ruleToString(Rule<T,S> rule) throws TreeStringGeneratorException
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Left hand side:\n");
		TreeStringGenerator<Info> tsg = new TreeStringGenerator<Info>(new IdLemmaPosRelNodeString(),rule.getLeftHandSide());
		sb.append(tsg.generateString());
		sb.append("\nRight hand side:\n");
		tsg = new TreeStringGenerator<Info>(new IdLemmaPosRelNodeString(),rule.getRightHandSide());
		sb.append(tsg.generateString());
		return sb.toString();
	}

}
