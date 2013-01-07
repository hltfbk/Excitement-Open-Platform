package ac.biu.nlp.nlp.engineml.utilities.view;

import java.util.List;

import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.engineml.generic.truthteller.representation.AnnotationRule;
import ac.biu.nlp.nlp.engineml.generic.truthteller.representation.AnnotationRuleWithDescription;
import ac.biu.nlp.nlp.engineml.generic.truthteller.representation.BasicRuleAnnotations;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeStringGenerator;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;

/**
 * To be used with {@link AnnotationNodeString}
 * 
 * @author amnon
 * @since 19 ���� 2011
 * 
 * @param <I>
 * @param <N>
 */
public class AnnotationRulesViewer
{
	public AnnotationRulesViewer(List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> rulesList)
	{
		this.rules = rulesList;
	}

	public void view()
	{
		try
		{
			for (AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations> ruleWithDesc : rules)
			{
				logger.debug(ruleWithDesc.getDescription());
				AnnotationRule<ExtendedNode, BasicRuleAnnotations> rule = ruleWithDesc.getRule();
				if (rule.getLeftHandSide() != null && rule.getMapLhsToAnnotations() != null)
					viewRule(rule);
				else
					logger.debug("This rule has null componants, and cannot be displayed.");
				logger.debug("------------------------------------");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public void viewRule(AnnotationRule<ExtendedNode, BasicRuleAnnotations> rule) throws TreeStringGeneratorException
	{
		logger.debug("left hand side:");
		ExtendedNode lhsRoot = rule.getLeftHandSide();
		TreeStringGenerator<ExtendedInfo> tsg = new TreeStringGenerator<ExtendedInfo>(new AnnotationNodeString(rule.getMapLhsToAnnotations()), lhsRoot); 
		String treeString = tsg.generateString();
		logger.debug(treeString);
	}

	protected List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> rules;
	private static final Logger logger = Logger.getLogger(AnnotationRulesViewer.class);
}
