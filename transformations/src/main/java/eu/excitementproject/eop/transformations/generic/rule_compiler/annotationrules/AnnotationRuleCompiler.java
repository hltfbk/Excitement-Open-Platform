/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.annotationrules;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;
import eu.excitementproject.eop.transformations.generic.rule_compiler.RuleCompilerParameterNames;
import eu.excitementproject.eop.transformations.generic.rule_compiler.utils.RuleCompilerUtils;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRule;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRuleWithDescription;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRulesBatch;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.BasicRuleAnnotations;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.RuleAnnotations;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.RuleType;
import eu.excitementproject.eop.transformations.representation.ExtendedConstructionNode;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames;

/**
 * This class compiles CharGer files into {@link AnnotationRule}s
 * <p>
 * 
 * a {@link AnnotationRule} is composed of:<br>
 * 	Left Hand Side == a tree of AnnotatedNodes, each containing:<br>
 * 		String ID,<br>
 * 		String word,<br>
 * 		String lemma,<br>
 * 		String PartOfSpeech<br>
 * 		DependencyRelation - for example "subj", "subject", "s".<br>
 *  A {@link RuleAnnotations} record for each LHS node whose annotations should be changed<br>
 * 	Map == a map from (part of) the left hand nodes to the right hand side annotations.<br> 
 * 
 * @author Amnon Lotan
 * @since Jun 2, 2011
 * 
 */
public final class AnnotationRuleCompiler
{
	private final CgxAnnotationRuleCompiler<ExtendedInfo, ExtendedNode, ExtendedConstructionNode, BasicRuleAnnotations> cgxRuleCompiler;

	/**
	
	 * Ctor that gets the names for the single word and phrasal verb lexicon files. It instantiates a generic {@link CgxAnnotationRuleCompiler} that takes 
	 * the generic annotations-specific service class {@link DefaultAnnotationRuleCompileServices}. 
	 * 
	 * @param predicateListFile name of the predicate type lists properties file to load onto a map of predicate-types to predicates
	 * @param phrasalVerbFamiliesFile
	 * @param phrasalNounFamiliesFile
	 * @param phrasalImplicativeTemplatesFile
	 * @throws AnnotationCompilationException
	 */
	public AnnotationRuleCompiler(String predicateListFile, String phrasalVerbFamiliesFile, String phrasalNounFamiliesFile,	String phrasalImplicativeTemplatesFile
			) throws AnnotationCompilationException 
	{
		AnnotationRuleCompileServices<ExtendedInfo, ExtendedNode, ExtendedConstructionNode, BasicRuleAnnotations> compileServices = 
				new DefaultAnnotationRuleCompileServices(predicateListFile, phrasalVerbFamiliesFile, phrasalNounFamiliesFile, phrasalImplicativeTemplatesFile);
		cgxRuleCompiler = new CgxAnnotationRuleCompiler<ExtendedInfo, ExtendedNode, ExtendedConstructionNode, BasicRuleAnnotations>
		(compileServices.getPredicateList(), compileServices);
	}
	
	/**
	 * Ctor with {@link ConfigurationParams}
	 * @param compilationParams
	 * @throws ConfigurationException 
	 * @throws AnnotationCompilationException 
	 */
	public AnnotationRuleCompiler(ConfigurationParams compilationParams) throws AnnotationCompilationException, ConfigurationException {
		this(
			compilationParams.get(RuleCompilerParameterNames.SINGLE_WORD_PREDICATE_TYPE_LEXICON),
			compilationParams.get(RuleCompilerParameterNames.PHRASAL_VERB_FAMILIES_FILE),
			compilationParams.get(RuleCompilerParameterNames.PHRASAL_NOUN_FAMILIES_FILE),
			compilationParams.get(RuleCompilerParameterNames.PHRASAL_IMPLICATIVE_TEMPLATES_FILE));
	}
	
	/**
	 * Compile all the rule files (ending with FILE_SUFFIX) in the given directory. <br>
	 * Importantly, it compiles the files in lexicographical order.
	 * 
	 * @param dir
	 * @param ruleDefFileSuffix
	 * @return
	 * @throws AnnotationCompilationException 
	 */
	public List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> compileFolder(	File dir, final String ruleDefFileSuffix) throws AnnotationCompilationException 
	{
		if (dir == null )
			throw new AnnotationCompilationException("Got a null directory");
		if (!dir.exists())
			throw new AnnotationCompilationException(dir + " doesn't exist");
		
		// if a filter string was given, filter the list of returned files
		FilenameFilter filter = ruleDefFileSuffix == null ? null :
			new FilenameFilter() {			
			    public boolean accept(File dir, String name) {
			        return name.endsWith(ruleDefFileSuffix);
			    }
			};
			
		File[] files = dir.listFiles(filter);
		if (files == null)
			throw new AnnotationCompilationException(dir + " has no files" + ruleDefFileSuffix == null ? null : " ending with " + ruleDefFileSuffix);
		Arrays.sort(files);		// it's important to compile the files in lexicographical order!
		
		// now compile the files into rules
		List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> rulesWithCD = 
				new Vector<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>>();
		for (File file : files)
		{
			String fileName = file.getName();
			System.out.println("***compiling " + fileName);
			List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> tmpRulesWithCD = makeRules(file);
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
	public List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> makeRules(File file ) throws AnnotationCompilationException
	{
		// first add all the rules into a set, to eliminate duplicates. Then put 'em in  a list
		Set<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> setOfRulesWithCD = 
				new HashSet<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>>();
		String description = RuleCompilerUtils.getDescription(file);
		
		String origText;
		try 					{ origText = FileUtils.loadFileToString(file);	} 
		catch (IOException e) 	{ throw new AnnotationCompilationException("error reading " + file, e);		}
		try {
			Set<String> texts = cgxRuleCompiler.expandMultipleChoiceParameters(origText);
	
			for (String text : texts)
			{
				setOfRulesWithCD.add( new AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>(cgxRuleCompiler.makeRule(text), description)); 
			}
		}
		catch (Exception e)	{	throw new AnnotationCompilationException("Error in file " + file, e);	}
		
		List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> rulesWithCD =  
				new Vector<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>>(setOfRulesWithCD);
		return rulesWithCD;		
	}
	
	///////////////////////////////////////////////////////////////////////// MAIN ////////////////////////////////////////////////////////////

	/**
	 * @param args
	 * @throws AnnotationCompilationException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ConfigurationException 
	 * @throws AnnotationException 
	 */
	public static void main(String[] args) throws AnnotationCompilationException, FileNotFoundException, IOException, ConfigurationException 
	{
		if (args.length == 0)
			throw new AnnotationCompilationException("usage: AnnotationRuleCompiler configurationFile.xml");
		ConfigurationFile confFile = new ConfigurationFile(new File(args[0]));
		confFile.setExpandingEnvironmentVariables(true);
		ConfigurationParams compilationParams = confFile.getModuleConfiguration(RuleCompilerParameterNames.RULE_COMPILER_PARAMS_MODULE);
		ConfigurationParams annotationParams = confFile.getModuleConfiguration(TransformationsConfigurationParametersNames.TRUTH_TELLER_MODULE_NAME);
		File dir = compilationParams.getDirectory(RuleCompilerParameterNames.ANNOTATION_RUELS_DIRECTORY);
		File recursiveCtCalcRulesDir = compilationParams.getDirectory(RuleCompilerParameterNames.RECURSIVE_CT_RUELS_DIRECTORY);
		final String ruleFileSuffix = compilationParams.get(RuleCompilerParameterNames.RULE_FILE_SUFFIX);	

		// create an Annotated rule compiler
		AnnotationRuleCompiler compiler = new AnnotationRuleCompiler(compilationParams);
		System.out.println("\nCompiling main list of annotation rules:");
		List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> rulesWithD = compiler.compileFolder(dir, ruleFileSuffix);
		System.out.println("\nCompiling directory of the rules dependent of the recursive CT calculator:");
		List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> recursiveCtCalcRulesWithD = 
				compiler.compileFolder(recursiveCtCalcRulesDir, ruleFileSuffix);
		
		for (AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations> ruleWithD : recursiveCtCalcRulesWithD)
			if (ruleWithD.getRule().getRuleType().equals(RuleType.COMPUTE_RECURSIVE_CT) )
				throw new AnnotationCompilationException("It is forbidden to place a ref to the ClauseTruthAnnotationRuleApplier within the list of annotation " +
					"rule applied by the ClauseTruthAnnotationRuleApplier itself" );

		AnnotationRulesBatch<ExtendedNode, BasicRuleAnnotations> rulesBatch = 
			new AnnotationRulesBatch<ExtendedNode, BasicRuleAnnotations>(rulesWithD, recursiveCtCalcRulesWithD);
		//AnnotationRulesViewer rv = new AnnotationRulesViewer(rulesWithCD.subList(0,576));
		//rv.view();

		String outFileName = annotationParams.get(TransformationsConfigurationParametersNames.ANNOTATION_RULES_FILE);	//props.getProperty("annotationOutFile");

		// serialize rules to file
		try {
			RuleCompilerUtils.serializeToFile(rulesBatch, outFileName );
		} catch (CompilationException e) {
			throw new AnnotationCompilationException("See nested", e);
		}

		System.out.println("\n\nSerialized " + rulesWithD.size() + " rules into " + outFileName);
	}
}
