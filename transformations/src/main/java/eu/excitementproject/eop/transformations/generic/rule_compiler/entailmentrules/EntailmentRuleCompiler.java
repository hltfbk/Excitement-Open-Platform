/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;
import eu.excitementproject.eop.transformations.generic.rule_compiler.Constants;
import eu.excitementproject.eop.transformations.generic.rule_compiler.RuleCompilerParameterNames;
import eu.excitementproject.eop.transformations.generic.rule_compiler.annotationrules.DefaultAnnotationRuleCompileServices;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.CgxMultipleChoiceExpander;
import eu.excitementproject.eop.transformations.generic.rule_compiler.utils.RuleCompilerUtils;
import eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames;

/**
 * Read all the .cgx files in the given entailment rule directory and compile each file into {@link SyntacticRule}s.
 * <p>
	 * 
	 * a rule is composed of:<br>
	 * 	Left Hand Side == a tree of AnnotatedNodes, each containing:<br>
	 * 		String ID,<br>
	 * 		String word,<br>
	 * 		String lemma,<br>
	 * 		int serial (location in the sentence)<br>
	 * 		null namedEntity,<br>
	 * 		String PartOfSpeech<br>
	 * 		DependencyRelation - for example "subj", "subject", "s".<br>
	 *  RightHandSide == like LHS<br>
	 * 	BidirectionalMap == a 1to1 mapping from (part of) the left hand nodes to the right hand side nodes<br> 
	 *
 * @author Amnon Lotan
 * @since Jun 1, 2011
 *
 */
public final class EntailmentRuleCompiler  {
	
	private final CgxEntailmentRuleCompiler<Info, BasicNode, BasicConstructionNode> cgxRuleCompiler;
	
	/**
	 * Ctor using {@link DefaultAnnotationRuleCompileServices} as the RuleCompileServices class
	 * @param predicateListFile name of the predicate type lists properties file to load onto a map of predicate-types to predicates
	 * @throws EntailmentCompilationException 
	 */
	public EntailmentRuleCompiler() throws EntailmentCompilationException 
	{
		cgxRuleCompiler = new CgxEntailmentRuleCompiler<Info, BasicNode, BasicConstructionNode>(DefaultEntailmentRuleCompileServices.getInstance());
	}
	
	/**
	 * Compile all the rule files (ending with FILE_SUFFIX) in the given directory. <br>
	 * Importantly, it compiles the files in lexicographical order.
	 * 
	 * @param dir
	 * @param ruleDefFileSuffix
	 * @return
	 * @throws EntailmentCompilationException 
	 */
	public List<RuleWithConfidenceAndDescription<Info, BasicNode>> compileFolder(	File dir, final String ruleDefFileSuffix) throws EntailmentCompilationException 
	{
		if (dir == null )
			throw new EntailmentCompilationException("Got a null directory");
		if (!dir.exists())
			throw new EntailmentCompilationException(dir + " doesn't exist");
		
		// if a filter string was given, filter the list of returned files
		FilenameFilter filter = ruleDefFileSuffix == null ? null :
			new FilenameFilter() {			
			    public boolean accept(File dir, String name) {
			        return name.endsWith(ruleDefFileSuffix);
			    }
			};
			
		File[] files = dir.listFiles(filter);
		if (files == null)
			throw new EntailmentCompilationException(dir + " has no files" + ruleDefFileSuffix == null ? null : " ending with " + ruleDefFileSuffix);
		Arrays.sort(files);		// it's important to compile the files in lexicographical order!
		
		// now compile the files into rules
		List<RuleWithConfidenceAndDescription<Info, BasicNode>> rulesWithCD = new Vector<RuleWithConfidenceAndDescription<Info, BasicNode>>();
		new LinkedHashMap<SyntacticRule<Info, BasicNode>, String>();
		for (File file : files)
		{
			String fileName = file.getName();
			System.out.println("***compiling " + fileName);
			List<RuleWithConfidenceAndDescription<Info, BasicNode>> tmpRulesWithCD = makeRules(file);
			rulesWithCD.addAll( tmpRulesWithCD );
		}
		return rulesWithCD;
	}
	
	/**
	 * return a set of rules based on this CharGer file. All Rules are different from each other. 
	 * If the LHS of the file contains a lemma with multiple choices, 
	 * like "lemma1/lemma2/lemma3...",
	 * then one rule is created per lemma option
	 * otherwise, naturally, one rule is created 
	 * 
	 */
	public List<RuleWithConfidenceAndDescription<Info, BasicNode>> makeRules(File file ) throws EntailmentCompilationException
	{
		// first add all the rules into a set, to eliminate duplicates. Then put 'em in  a list
		Set<RuleWithConfidenceAndDescription<Info, BasicNode>> setOfRulesWithCD =  new LinkedHashSet<RuleWithConfidenceAndDescription<Info, BasicNode>>();
		String description = RuleCompilerUtils.getDescription(file);
		
		String origText;
		try 					{ origText = FileUtils.loadFileToString(file);	} 
		catch (IOException e) 	{ throw new EntailmentCompilationException("error reading " + file, e);		}
		try {
			List<String> expandedTexts = CgxMultipleChoiceExpander.expandMultipleChoiceParameters(origText, null);
	
			for (String text : expandedTexts)
			{
				setOfRulesWithCD.add( new RuleWithConfidenceAndDescription<Info, BasicNode>(cgxRuleCompiler.makeRule(text), Constants.RULE_CONFIDENCE, description)); 
			}
	
			EntailmentRuleBuildingUtils.addReversedRules(setOfRulesWithCD, expandedTexts.get(0));
		}
		catch (Exception e)	{	throw new EntailmentCompilationException("Error in file " + file, e);	}
		
		List<RuleWithConfidenceAndDescription<Info, BasicNode>> rulesWithCD =  new Vector<RuleWithConfidenceAndDescription<Info, BasicNode>>(setOfRulesWithCD);
		return rulesWithCD;		
	}
	
	///////////////////////////////////////////////////// Main	//////////////////////////////////////////////////////////////////////

	/**
	 * @param args
	 * @throws UnsupportedPosTagStringException 
	 * @throws EntailmentCompilationException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ConfigurationException 
	 */
	public static void main(String[] args) throws UnsupportedPosTagStringException, EntailmentCompilationException, FileNotFoundException, IOException, ConfigurationException 
	{
		if (args.length < 1)
			throw new EntailmentCompilationException("usage: EntailmentRuleCompiler configurationFile.xml");
		ConfigurationFile confFile = new ConfigurationFile(new File(args[0]));
		confFile.setExpandingEnvironmentVariables(true);
		ConfigurationParams compilationParams = confFile.getModuleConfiguration(RuleCompilerParameterNames.RULE_COMPILER_PARAMS_MODULE);
		//ConfigurationParams applictionParams = confFile.getModuleConfiguration(KnowledgeResource.SYNTACTIC.getModuleName());
		ConfigurationParams applictionParams = confFile.getModuleConfiguration(RuleCompilerParameterNames.SYNTACTIC_PARAMS_MODULE);
		
		File dir = compilationParams.getDirectory(RuleCompilerParameterNames.ENTAILMENT_RULES_DIRECTORY);	//new File(props.getProperty("directoryName").trim());
		final String ruleFileSuffix = compilationParams.get(RuleCompilerParameterNames.RULE_FILE_SUFFIX);	//props.getProperty("graphFileSuffix").trim();

		// create an english node rule compliler
		EntailmentRuleCompiler compiler = new EntailmentRuleCompiler(); 
		List<RuleWithConfidenceAndDescription<Info, BasicNode>> rulesWithCD;

		rulesWithCD = compiler.compileFolder(dir, ruleFileSuffix);

		//EnglishRulesViewer rv = new EnglishRulesViewer(rulesWithCD);
		//ExtendedRulesViewer rv = new ExtendedRulesViewer(rulesWithCD.subList(0,1));
		//rv.view();

		// serialize rules to file
		Set<RuleWithConfidenceAndDescription<Info, BasicNode>> rules = new LinkedHashSet<RuleWithConfidenceAndDescription<Info, BasicNode>>(rulesWithCD);

		String outFile = applictionParams.get(TransformationsConfigurationParametersNames.SYNTACTIC_RULES_FILE);
		try {
			RuleCompilerUtils.serializeToFile(rules, outFile);
		} catch (CompilationException e) {
			throw new EntailmentCompilationException("see nested", e);	
		}

		System.out.println("\n\nMade " + rules.size() + " rules.");
		System.out.println("Serialized them into " + outFile);
	}
}
