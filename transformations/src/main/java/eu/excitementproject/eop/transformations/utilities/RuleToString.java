package eu.excitementproject.eop.transformations.utilities;
import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.IdLemmaPosRelNodeString;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;

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
	String ruleToString(SyntacticRule<T,S> rule) throws TreeStringGeneratorException
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
