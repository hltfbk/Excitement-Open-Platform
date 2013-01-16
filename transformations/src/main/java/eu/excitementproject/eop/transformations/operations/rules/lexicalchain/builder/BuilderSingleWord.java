package eu.excitementproject.eop.transformations.operations.rules.lexicalchain.builder;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.transformations.datastructures.CanonicalLemmaAndPos;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfLexicalRules;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.LexicalRuleWithName;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * Builds lexical-chains whose right-hand-side is the given word.
 * This class is used by {@link BuilderSetOfWords}. See JavaDoc there.
 * <P>
 * The given word should be a word in the hypothesis. Chains are
 * built such that for each chain, the right hand side of the
 * right-most element in the chain is the given word.
 * 
 * @see BuilderSetOfWords
 * @author Asher Stern
 * @since Jan 19, 2012
 *
 */
public class BuilderSingleWord
{
	public BuilderSingleWord(CanonicalLemmaAndPos hypothesisLemma,
			Map<String, ? extends LexicalResource<? extends RuleInfo>> resources)
	{
		super();
		this.hypothesisLemma = hypothesisLemma;
		this.resources = resources;
	}

	/**
	 * Creates the rules. <code>depth</code> indicates the maximum chain-length.
	 * 
	 * @param depth the maximum chain-length
	 * @throws LexicalResourceException
	 * @throws TeEngineMlException
	 */
	public void createRuleBase(int depth) throws LexicalResourceException, TeEngineMlException
	{
		generatedRules = new SimpleValueSetMap<CanonicalLemmaAndPos, ChainOfLexicalRules>();
		List<ChainOrWord> currentRules = createOriginalListOfHypothesis();
		for (int index=0;index<depth;++index)
		{
			logger.debug("expanding...");
			currentRules = expand(currentRules);
			logger.debug("expanding done.");
			for (ChainOrWord chain : currentRules)
			{
				CanonicalLemmaAndPos lemmaAndPos = new CanonicalLemmaAndPos(chain.getChain().getLhsLemma(),chain.getChain().getLhsPos());
				generatedRules.put(lemmaAndPos, chain.getChain());
			}
		}
	}
	
	

	/**
	 * Returns the rules created by {@link #createRuleBase(int)}.
	 * @return
	 * @throws TeEngineMlException
	 */
	public ValueSetMap<CanonicalLemmaAndPos, ChainOfLexicalRules> getGeneratedRules() throws TeEngineMlException
	{
		if (null==generatedRules) throw new TeEngineMlException("rules were not generated");
		return generatedRules;
	}



	private List<ChainOrWord> createOriginalListOfHypothesis()
	{
		List<ChainOrWord> list = new ArrayList<ChainOrWord>(1);
		list.add(new ChainOrWord(this.hypothesisLemma));
		return list;
	}
	
	/**
	 * Creates new chains which are similar to the given chains, but with one additional
	 * rule (element in the chain) added to the left of the original chain.
	 * The given list of chains might be merely the hypothesis word - i.e.,
	 * an "empty" chain, which is not really a chain, but just a word.
	 * 
	 * @param rightHandSides The given chains, such that new chains are rule+given-chain.
	 * @return new chains (does not return the given chains, only the new).
	 * @throws LexicalResourceException
	 * @throws TeEngineMlException
	 */
	private List<ChainOrWord> expand(List<ChainOrWord> rightHandSides) throws LexicalResourceException, TeEngineMlException
	{
		List<ChainOrWord> ret = new LinkedList<ChainOrWord>();
		
		for (ChainOrWord rhs : rightHandSides)
		{
			String rightMostLemma;
			PartOfSpeech rightMostPos;
			String lemma;
			PartOfSpeech pos;
			if (rhs.getChain()!=null)
			{
				ChainOfLexicalRules chain = rhs.getChain();
				// the left hand side of the chain is the right hand side for the new element, to be added to its begining.
				lemma = chain.getLhsLemma();
				pos = chain.getLhsPos();
				
				rightMostLemma=chain.getRhsLemma();
				rightMostPos = chain.getRhsPos();
			}
			else
			{
				lemma = rhs.getWord().getLemma();
				pos = rhs.getWord().getPartOfSpeech();
				rightMostLemma = rhs.getWord().getLemma();
				rightMostPos = rhs.getWord().getPartOfSpeech();
			}
			
			for (Map.Entry<String,? extends LexicalResource<? extends RuleInfo>> resourceEntry : resources.entrySet())
			{
				List<? extends eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule<? extends RuleInfo>> rules =
						resourceEntry.getValue().getRulesForRight(lemma, pos);
				if (logger.isDebugEnabled())
				{
					logger.debug("Number of rules expanded from \""+resourceEntry.getKey()+"\" for word \""+lemma+"\" is "+rules.size());
				}
				
				for (eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule<? extends RuleInfo> rule : rules)
				{
					if (rule.getRLemma().equalsIgnoreCase(lemma))
					{
						if (!simplerPos(rule.getRPos().getCanonicalPosTag()).equals(simplerPos(pos.getCanonicalPosTag()))){throw new TeEngineMlException("Bad rule from lexical resource: "+resourceEntry.getKey()+": Requested right lemma was: "+lemma+", with part of speech: "+pos+", but returned lemma is: "+rule.getRLemma()+" with part of speech: "+rule.getRPos());}
						LexicalRuleWithName lexicalRuleWithName = fromInfrastructureLexicalRule(rule,resourceEntry.getKey());
						int newChainLength = 1;
						if (rhs.getChain()!=null){newChainLength+=rhs.getChain().getChain().size();}
						List<LexicalRuleWithName> newChain = new ArrayList<LexicalRuleWithName>(newChainLength);
						newChain.add(lexicalRuleWithName);
						if (rhs.getChain()!=null)
						{
							for(LexicalRuleWithName inChain : rhs.getChain().getChain())
							{
								newChain.add(inChain);
							}
						}

						ChainOfLexicalRules chainOfLexicalRules = new ChainOfLexicalRules(rule.getLLemma(), rule.getLPos(), rightMostLemma, rightMostPos, E_MINUS1, new ImmutableListWrapper<LexicalRuleWithName>(newChain));
						ret.add(new ChainOrWord(chainOfLexicalRules));
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Converts ac.biu.nlp.nlp.lexical_resource.LexicalRule to LexicalRuleWithName
	 * @param rule
	 * @param ruleBaseName
	 * @return
	 */
	private LexicalRuleWithName fromInfrastructureLexicalRule(eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule<? extends RuleInfo> rule, String ruleBaseName)
	{
		LexicalRuleWithName ret = new LexicalRuleWithName(
				new eu.excitementproject.eop.transformations.operations.rules.LexicalRule(
						rule.getLLemma(),rule.getLPos(),rule.getRLemma(),rule.getRPos(),rule.getConfidence()),
				ruleBaseName);
		
		return ret;
	}

	// input
	private final CanonicalLemmaAndPos hypothesisLemma;
	private final Map<String,? extends LexicalResource<? extends RuleInfo>> resources;
	
	// output
	private ValueSetMap<CanonicalLemmaAndPos, ChainOfLexicalRules> generatedRules;
	
	private static final double E_MINUS1 = Math.exp(-1);
	
	private static final Logger logger = Logger.getLogger(BuilderSingleWord.class);
}
