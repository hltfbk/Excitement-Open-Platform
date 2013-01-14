package eu.excitementproject.eop.transformations.operations.specifications;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.NodePrintUtilities;

/**
 * Represents the transformation of lexical-rules applications.
 * 
 * @author Asher Stern
 * @since Feb 13, 2011
 *
 */
public class RuleSubstituteNodeSpecification<T extends LexicalRule> extends SubstituteNodeSpecification
{
	private static final long serialVersionUID = 3297065955749182506L;
	
	public RuleSubstituteNodeSpecification(ExtendedNode textNodeToBeSubstituted, NodeInfo newNodeInfo,
			AdditionalNodeInformation additionalNodeInformation,
			double confidence, String ruleBaseName, T rule, boolean writeConfidenceInDescription)
	{
		super(textNodeToBeSubstituted, newNodeInfo,additionalNodeInformation);
		this.confidence = confidence;
		this.ruleBaseName = ruleBaseName;
		this.rule = rule;
		this.writeConfidenceInDescription = writeConfidenceInDescription;
	}

	public double getConfidence()
	{
		return confidence;
	}
	
	public String getRuleBaseName()
	{
		return ruleBaseName;
	}
	
	public T getRule()
	{
		return rule;
	}

	
	public StringBuffer specString()
	{
		StringBuffer ret = new StringBuffer();
		
		String id = textNodeToBeSubstituted.getInfo().getId();
		String lemma = InfoGetFields.getLemma(textNodeToBeSubstituted.getInfo());
		String pos = InfoGetFields.getPartOfSpeech(textNodeToBeSubstituted.getInfo());
		
		String newLemma = InfoGetFields.getLemma(newNodeInfo);
		String newPos = InfoGetFields.getPartOfSpeech(newNodeInfo);
		
		ret.append("<");
		ret.append(StringUtil.capitalizeFirstLetterOnly(ruleBaseName));
		ret.append(">: substitute node: ");
		ret.append(id);
		ret.append(": \"");
		ret.append(lemma);
		ret.append("\"(");
		ret.append(pos);
		ret.append(")");
		ret.append(" to: \"");
		ret.append(newLemma);
		ret.append("\"(");
		ret.append(newPos);
		ret.append(")");
		if (writeConfidenceInDescription)
		{
			ret.append(" with confidence: ");
			ret.append(String.format("%.7f", confidence));
		}

		
		return ret;
	}
	
	public String toShortString()
	{
		StringBuffer ret = new StringBuffer();
		
		String id = textNodeToBeSubstituted.getInfo().getId();
		String lemma = InfoGetFields.getLemma(textNodeToBeSubstituted.getInfo());
		String pos = InfoGetFields.getPartOfSpeech(textNodeToBeSubstituted.getInfo());
		
		ret.append(StringUtil.capitalizeFirstLetterOnly(ruleBaseName));
		ret.append(" substitute ");
		ret.append(NodePrintUtilities.nodeDetailsToString(id, lemma, pos));
		
		return ret.toString();
	}
	

	
	private double confidence;
	private String ruleBaseName;
	private T rule;
	private boolean writeConfidenceInDescription;
}
