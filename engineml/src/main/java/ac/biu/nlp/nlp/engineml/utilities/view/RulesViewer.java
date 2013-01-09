package ac.biu.nlp.nlp.engineml.utilities.view;

import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;

import ac.biu.nlp.nlp.engineml.operations.rules.Rule;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleWithConfidenceAndDescription;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.NodeString;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeStringGenerator;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;


/**
 * Creates a string which represents a given list-of-rules, and prints it
 * to the logger.
 * Each method has an optional flag parameter to choose whether to print to info or to debug logger.
 * 
 * @author Asher Stern
 * 
 *
 * @param <I>
 * @param <N>
 */
public class RulesViewer<I extends Info, N extends AbstractNode<I, N>>
{
	public RulesViewer(List<RuleWithConfidenceAndDescription<I, N>> rulesList, NodeString<I> nodeString)
	{
		this.rules = rulesList;
		this.nodeString = nodeString;

	}

	/**
	 * Print a view of the rule to the info logger
	 */
	public void view()
	{
		view(false);
	}
	
	/**
	 * Print a view of the rule to the debug/info logger
	 * @param debugPrint
	 */
	public void view(boolean debugPrint)
	{
		try
		{
			for (RuleWithConfidenceAndDescription<I, N> ruleWithCD : rules)
			{
				log(ruleWithCD.getDescription(), debugPrint);
				Rule<I, N> rule = ruleWithCD.getRule();
				if (rule.getLeftHandSide() != null && rule.getRightHandSide() != null && rule.getMapNodes() != null)
					viewRule(ruleWithCD.getRule());
				else
					log("This rule has null componants, and cannot be displayed.", debugPrint);
				log("------------------------------------", debugPrint);
			}
		}
		catch(Exception e)
		{
			logger.error("An error occured when trying to view the rules",e);

		}

	}

	/**
	 * Print a view of the rule to the info logger
	 * @param rule
	 * @throws TreeStringGeneratorException
	 */
	public void viewRule(Rule<I, N> rule) throws TreeStringGeneratorException
	{
		viewRule(rule, false);
	}
	
	/**
	 * Print a view of the rule to the debug/info logger
	 * @param rule
	 * @param debugPrint
	 * @throws TreeStringGeneratorException
	 */
	public void viewRule(Rule<I, N> rule, boolean debugPrint) throws TreeStringGeneratorException
	{
		log("left hand side:", debugPrint); 
		AbstractNode<I, ?> lhsRoot = rule.getLeftHandSide();
		TreeStringGenerator<I> tsg = new TreeStringGenerator<I>(nodeString, lhsRoot); 
		String treeString = tsg.generateString();
		log('\n' + treeString, debugPrint);
		log("right hand side:", debugPrint);
		AbstractNode<I, ?> rhsRoot = rule.getRightHandSide();
		tsg = new TreeStringGenerator<I>(nodeString, rhsRoot);
		treeString = tsg.generateString();
		log('\n' + treeString, debugPrint);
		log("Mapping:", debugPrint);
		BidirectionalMap<N, N> mapNodes = rule.getMapNodes();
		for (N lhsNode : mapNodes.leftSet())
		{
			AbstractNode<I, ?> rhsNode = mapNodes.leftGet(lhsNode);
			try
			{
				String lid = lhsNode.getInfo().getId();
				String rid = rhsNode.getInfo().getId();
				log(lid+" -> "+rid, debugPrint);
			}
			catch(NullPointerException e){
				log("? -> ?", debugPrint);
			}
		}
	}
	
	/**
	 * Print a view of the rule to the info logger
	 * @param tree
	 * @param debugPrint
	 * @throws TreeStringGeneratorException
	 */
	public void printTree(N tree) throws TreeStringGeneratorException
	{
		printTree(tree, false);
	}
	
	/**
	 * Print a view of the rule to the debug/info logger
	 * @param tree
	 * @param debugPrint
	 * @throws TreeStringGeneratorException
	 */
	public void printTree(N tree, boolean debugPrint) throws TreeStringGeneratorException
	{
		TreeStringGenerator<I> tsg = new TreeStringGenerator<I>(nodeString, tree);
		String treeString = tsg.generateString();
		log("-------------------------------\ntree:", debugPrint);
		log("\n" + treeString + "\n", debugPrint);
	}

	private void log(String message, boolean debugPrint)
	{
		if (debugPrint)
			logger.debug(message);
		else
			logger.info(message);
	}

	protected List<RuleWithConfidenceAndDescription<I, N>> rules;
	private NodeString<I> nodeString;
	private static final Logger logger = Logger.getLogger(RulesViewer.class);
}
