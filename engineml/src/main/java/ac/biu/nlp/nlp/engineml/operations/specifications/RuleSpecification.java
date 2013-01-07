package ac.biu.nlp.nlp.engineml.operations.specifications;

import java.util.Set;

import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.PRINT_SENTENCE_PART_IN_RULE_SPECIFICATION;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.WHEN_PRINT_SENTENCE_PART_IN_RULE_SPECIFICATION_INCLUDE_NON_RULE_MODIFIERS;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleWithConfidenceAndDescription;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.general.BidirectionalMap;
import ac.biu.nlp.nlp.general.StringUtil;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
/**
 * 
 * Represents the transformation of "rule application".
 * 
 * @author Asher Stern
 * @since Feb 14, 2011
 *
 */
public class RuleSpecification extends Specification
{
	private static final long serialVersionUID = 7881074559715296738L;

	public RuleSpecification(String ruleBaseName,
			RuleWithConfidenceAndDescription<Info, BasicNode> rule,
			BidirectionalMap<BasicNode, ExtendedNode> mapLhsToTree,
			boolean extraction)
	{
		super();
		this.ruleBaseName = ruleBaseName;
		this.rule = rule;
		this.mapLhsToTree = mapLhsToTree;
		this.extraction = extraction;
	}
	
	
	

	public String getRuleBaseName()
	{
		return ruleBaseName;
	}

	public RuleWithConfidenceAndDescription<Info, BasicNode> getRule()
	{
		return rule;
	}

	public BidirectionalMap<BasicNode, ExtendedNode> getMapLhsToTree()
	{
		return mapLhsToTree;
	}

	public boolean isExtraction()
	{
		return extraction;
	}
	
	@Override
	public Set<ExtendedNode> getInvolvedNodesInTree()
	{
		return getMapLhsToTree().rightSet().getMutableSetCopy();
	}


	@Override
	public StringBuffer specString()
	{
		StringBuffer ret = new StringBuffer();
		ret.append("<").append(StringUtil.capitalizeFirstLetterOnly(ruleBaseName)).append("> ");
		if (extraction)
			ret.append(" extraction");
		else
			ret.append(" substitution");
		ret.append(" rule: \"");
		ret.append(rule.getDescription());
		ret.append("\"");
		
		if (PRINT_SENTENCE_PART_IN_RULE_SPECIFICATION)
		{
			ret.append(" The part-of-sentence (bag of words) is: \"");
			printLhsAsString(ret,rule.getRule().getLeftHandSide());
			ret.append("\"");
		}
		
		return ret;
	}

	private void printLhsAsString(StringBuffer sb, BasicNode node)
	{
		if (node.hasChildren())
		{
			for (BasicNode child : node.getChildren())
			{
				printLhsAsString(sb,child);
			}
		}
		if (WHEN_PRINT_SENTENCE_PART_IN_RULE_SPECIFICATION_INCLUDE_NON_RULE_MODIFIERS)
		{
			if (mapLhsToTree.leftContains(node))
			{
				ExtendedNode nodeInTree = mapLhsToTree.leftGet(node);
				if (nodeInTree.hasChildren())
				{
					for (ExtendedNode child : nodeInTree.getChildren())
					{
						if (!mapLhsToTree.rightContains(child))
						{
							printSubTree(sb, child);
						}
					}
				}
			}
		}
		if (mapLhsToTree.leftContains(node))
		{
			ExtendedNode nodeInTree = mapLhsToTree.leftGet(node);
			printNodeInfo(sb, nodeInTree.getInfo());
		}
		
	}
	

	private void printSubTree(StringBuffer sb, ExtendedNode node)
	{
		if (node.hasChildren())
		{
			for (ExtendedNode child : node.getChildren())
			{
				printSubTree(sb, child);
			}
		}
		printNodeInfo(sb, node.getInfo());
	}
	
	private void printNodeInfo(StringBuffer sb, Info info)
	{
		String lemma = InfoGetFields.getLemma(info);
		String word = InfoGetFields.getWord(info);
		String wordOrLemma = word;
		if (wordOrLemma.length()==0) wordOrLemma = lemma;
		sb.append(wordOrLemma).append(" ");
	}
	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.operations.specifications.Specification#toShortString()
	 */
	@Override
	public String toShortString() {
		StringBuffer ret = new StringBuffer();
		ret.append(StringUtil.capitalizeFirstLetterOnly(ruleBaseName));
		if (extraction)
			ret.append(" extraction");
		else
			ret.append(" substitution");
		
		return ret.toString();
	}

	

	private String ruleBaseName;
	private RuleWithConfidenceAndDescription<Info, BasicNode> rule;
	private BidirectionalMap<BasicNode, ExtendedNode> mapLhsToTree;
	
	/**
	 * <tt> true </tt> = introduction rule
	 * <tt> false </tt> = substitution rule
	 */
	private boolean extraction = false;

}
