
package eu.excitementproject.eop.lexicalminer.Italian.language;

import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lexicalminer.Italian.language.textpro.TextProPartOfSpeech;
import eu.excitementproject.eop.lexicalminer.definition.Common.RelationType;
import eu.excitementproject.eop.lexicalminer.definition.Common.Resource;
import eu.excitementproject.eop.lexicalminer.definition.Common.UtilClass;
import eu.excitementproject.eop.lexicalminer.definition.Common.PatternRuleInfo.SyntacticPatternRuleInfo;
import eu.excitementproject.eop.lexicalminer.definition.idm.IIDM;
import eu.excitementproject.eop.lexicalminer.definition.idm.SyntacticIDM;

//import tools.fbk.textpro.TextProPartOfSpeech;

/**
 * Inference from Definition Module (IDM) which extracts inference 
 * rules connecting the term to be defined, the <i>definiendum</i>, with terms
 * of the definition.   
 * 
 * This IDM considers the path connecting rule sides. A path is represented
 * as a Syntactic path in a parsed tree.
 * 
 * @author Alberto Lavelli
 * @since Sep 2012
 *
 */
public class ItalianSyntacticIDM implements IIDM {

	protected BasicParser m_treeParser; 
	protected PartOfSpeech m_nounPOS;
	protected ItalianSyntacticUtils m_utils;
	protected int m_totalParserExceptions=0;
	protected org.apache.log4j.Logger m_logger;
	public BasicParser getParser() {
		return m_treeParser;
	}
	

	public ItalianSyntacticIDM(BasicParser treeParser, ItalianSyntacticUtils npExtractor) throws ParserRunException, UnsupportedPosTagStringException{
		super();
		m_logger = org.apache.log4j.Logger.getLogger(SyntacticIDM.class.getName());
		m_treeParser = treeParser;
		m_treeParser.init();
		m_utils = npExtractor;
		m_nounPOS = new TextProPartOfSpeech("SS");
	}
		
	@Override
	public List<LexicalRule<RuleInfo>> retrieveSentenceLexicalRules(String sentence, String title, int sourceId) throws FileNotFoundException  
	{
		try {
			//process sentence
			m_treeParser.setSentence(sentence);

			//TODO- remove it...
			//System.out.println("Before parse at:" + System.currentTimeMillis());
//			long start = System.currentTimeMillis();
			m_treeParser.parse();
			BasicNode tree = m_treeParser.getParseTree();
			
			//System.out.println("After parse at:" + System.currentTimeMillis() + ", it took: " + (System.currentTimeMillis()-start) + " ms");
			
			LinkedList<BasicNode> path = new LinkedList<BasicNode>();
			path.addLast(tree);
			return retrieveSentenceLexicalRules(tree, sentence, tree,  path ,title, sourceId);
		}
		catch (ParserRunException e) {
			m_totalParserExceptions++;
			m_logger.error(String.format("Parser exception (%d so far) in sentence \"%s\", sourceID: %d.",m_totalParserExceptions, sentence,sourceId),e);
			return new ArrayList<LexicalRule<RuleInfo>>();
		}
	}		

	/**
	 * Retrieve rules for a tree-view sentence
	 * @param full_tree
	 * @param current_tree
	 * @param title
	 * @return
	 * @throws FileNotFoundException
	 */
	private List<LexicalRule<RuleInfo>> retrieveSentenceLexicalRules(BasicNode full_tree, String originalSentence, BasicNode current_tree, LinkedList<BasicNode> path, String title, int sourceId) throws FileNotFoundException  
	{
		List<LexicalRule<RuleInfo>> inferences=new ArrayList<LexicalRule<RuleInfo>>(); 
		List<LexicalRule<RuleInfo>> temp_results = null;
		
		//check if need to save the header
		/*
		System.out.println("-");
		System.out.println(originalSentence);
		System.out.println("-");
		System.out.println(path);
		System.out.println("-");
		System.out.println(current_tree);
		System.out.println("");
		*/
		
		if (UtilClass.isANoun(current_tree.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech()))
		{
			temp_results = getRulesForNouns(full_tree, originalSentence, current_tree, path, title, sourceId);
				
			if (temp_results != null)
			{
				inferences.addAll(temp_results);
			}
		}
		
		if (current_tree.hasChildren())
		{
			// call Recursively to the function with the children
			for (BasicNode node : current_tree.getChildren()) 
			{ 
				path.addLast(node);
				temp_results = retrieveSentenceLexicalRules(full_tree, originalSentence, node, path, title, sourceId);
				path.removeLast();	//remove it from the path after finished using it
				if (temp_results != null)
				{
					inferences.addAll(temp_results);
				}
			}
		}

		return inferences;
	}
	
	
	/**
	 * Returns the relevant rules, for a Noun in the tree (a rule of it, and of the NP)
	 * @param full_tree
	 * @param current_tree
	 * @param titleLemma
	 * @return
	 * @throws FileNotFoundException
	 */
	public List<LexicalRule<RuleInfo>> getRulesForNouns(BasicNode full_tree, String originalSentence, BasicNode current_tree, LinkedList<BasicNode> path, String titleLemma, int sourceId) throws FileNotFoundException 
	{
		
		List<LexicalRule<RuleInfo>> inferences = new ArrayList<LexicalRule<RuleInfo>>();
		
		int id = current_tree.getInfo().getNodeInfo().getSerial();
		String word_lemma = current_tree.getInfo().getNodeInfo().getWordLemma();
		PartOfSpeech word_pos = current_tree.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech();

		SyntacticPatternRuleInfo pattern;
		
		try
		{
			if (UtilClass.isValidRule(titleLemma, word_lemma))
			{
				pattern = new SyntacticPatternRuleInfo(this.m_utils, full_tree, id, path, sourceId, false, titleLemma, word_lemma, originalSentence);
				LexicalRule<RuleInfo> regular_inferences = 
				new LexicalRule<RuleInfo>(titleLemma, m_nounPOS,	
						word_lemma,
						word_pos,
						RelationType.SyntacticIDM.toString(),
						Resource.Wiki.toString(),
						pattern);
				inferences.add(regular_inferences);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			m_logger.warn("Lexical resource exception in SyntacticIDM",e);
		}

		
		LexicalRule<RuleInfo> NPrule = null;
		try
		{			
			String NP_lemma = this.m_utils.getNPRuleForNoun(full_tree, current_tree);

			if ((NP_lemma != null) && (NP_lemma.length() > 0) && (UtilClass.isValidRule(titleLemma, NP_lemma)))
			{	
				SyntacticPatternRuleInfo NPpattern = new SyntacticPatternRuleInfo(this.m_utils, full_tree, id, path, sourceId, true, titleLemma, word_lemma, originalSentence);				
				NPrule = new LexicalRule<RuleInfo>(titleLemma.toLowerCase(), m_nounPOS,
								NP_lemma.toLowerCase(),
								word_pos,
								getRelationType().toString(),
								Resource.Wiki.toString(),
								NPpattern);
			}
		} catch (Exception e) {
					// TODO Auto-generated catch block
			m_logger.warn("Lexical resource exception in SyntacticIDM",e);
		}					
						
		if (NPrule != null)
		{
			inferences.add(NPrule);
		}
		
		return inferences;
	}



	public RelationType getRelationType() {
		// TODO Auto-generated method stub
		return RelationType.SyntacticIDM;
	}

	
	
}

