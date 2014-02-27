package eu.excitementproject.eop.transformations.operations.rules.lexicalmw_utils;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.SubstitutionLexicalRuleByLemmaPosFinder;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSubstituteNodeSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jul 4, 2011
 *
 */
public class MultiWordRuleBaseCreator<T extends LexicalRule>
{
	public MultiWordRuleBaseCreator(BasicNode hypothesisTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTreeAndParentMap,
			ByLemmaPosLexicalRuleBase<T> ruleBase, String ruleBaseName,
			ImmutableSet<String> stopWords)
			
	{
		super();
		this.hypothesisTree = hypothesisTree;
		this.textTreeAndParentMap = textTreeAndParentMap;
		this.ruleBase = ruleBase;
		this.ruleBaseName = ruleBaseName;
		this.stopWords = stopWords;
	}



	public void create() throws TeEngineMlException, OperationException
	{
		setRules = new LinkedHashSet<RuleWithConfidenceAndDescription<Info,BasicNode>>();
		SubstitutionLexicalRuleByLemmaPosFinder<T> finder = new SubstitutionLexicalRuleByLemmaPosFinder<T>(textTreeAndParentMap, ruleBase, ruleBaseName,
				Constants.FILTER_STOP_WORDS_IN_LEFT_IN_LEXICAL_RESOURCES,Constants.FILTER_STOP_WORDS_IN_LEFT_IN_LEXICAL_RESOURCES,stopWords);
		finder.setSearchingForMultiWords(true);
		finder.find();
		if (logger.isDebugEnabled())
		{
			logger.debug("Number of all rules from resource: "+finder.getSpecs().size()+".");
			
			
			StringBuffer sb = new StringBuffer();
			sb.append("Lexical rules (not yet matched to hypothesis):");
			
			for (RuleSubstituteNodeSpecification<?> spec : finder.getSpecs())
			{
				sb.append('\n');
				sb.append(spec.getRule().getLhsLemma());
				sb.append(" => ");
				sb.append(spec.getRule().getRhsLemma());
			}
			logger.debug(sb.toString());
			
			logger.debug("Now trying to find relevant multi-word rules.");
		}
		for (RuleSubstituteNodeSpecification<T> spec : finder.getSpecs())
		{
			LexicalRuleInTreeFinder ruleFinder = new LexicalRuleInTreeFinder();
			List<RuleWithConfidenceAndDescription<Info, BasicNode>> rulesFound =
				ruleFinder.findRule(spec.getRule(), hypothesisTree,ruleBaseName);

			if (logger.isDebugEnabled())
			{
				boolean mwRulesForHypothesisDetected = false;
				if (rulesFound!=null){if(rulesFound.size()>0)
				{	
					mwRulesForHypothesisDetected = true;
					logger.debug("Adding lexical multi word rule: " +spec.getRule().getLhsLemma()+" => "+spec.getRule().getRhsLemma());
				}}
				if (!mwRulesForHypothesisDetected)
				{
					// logger.debug("No rules matched the hypothesis.");
				}
			}

			if (rulesFound!=null)
				setRules.addAll(rulesFound);
		}
		logger.debug("Work on Multi Word for the given resource done.");
	}


	public Set<RuleWithConfidenceAndDescription<Info, BasicNode>> getSetRules() throws TeEngineMlException
	{
		if (setRules==null)throw new TeEngineMlException("null");
		return setRules;
	}





	private BasicNode hypothesisTree;
	private TreeAndParentMap<ExtendedInfo, ExtendedNode> textTreeAndParentMap;
	private ByLemmaPosLexicalRuleBase<T> ruleBase;
	private String ruleBaseName;
	
	private Set<RuleWithConfidenceAndDescription<Info, BasicNode>> setRules=null;
	
	private ImmutableSet<String> stopWords;
	
	private static final Logger logger = Logger.getLogger(MultiWordRuleBaseCreator.class);
}
