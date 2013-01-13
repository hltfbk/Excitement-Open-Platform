/**
 * 
 */
package ac.biu.nlp.nlp.engineml.generic.rule_compiler.annotationrules;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import ac.biu.nlp.nlp.engineml.generic.rule_compiler.RuleCompilerParameterNames;
import ac.biu.nlp.nlp.engineml.generic.truthteller.AnnotatorException;
import ac.biu.nlp.nlp.engineml.generic.truthteller.DefaultSentenceAnnotator;
import ac.biu.nlp.nlp.engineml.generic.truthteller.SentenceAnnotator;
import ac.biu.nlp.nlp.engineml.generic.truthteller.representation.AnnotationRuleWithDescription;
import ac.biu.nlp.nlp.engineml.generic.truthteller.representation.BasicRuleAnnotations;
import ac.biu.nlp.nlp.engineml.representation.AnnotatedExtendedNodeAndEdgeString;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.TreeUtilities;
import ac.biu.nlp.nlp.engineml.utilities.view.ExtendedRulesViewer;
import ac.biu.nlp.nlp.instruments.parse.ParserRunException;
import ac.biu.nlp.nlp.instruments.parse.easyfirst.EasyFirstParser;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeDotFileGenerator;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeDotFileGenerator.TreeDotFileGeneratorException;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

/**
 * @author Amnon Lotan
 *
 * @since Oct 31, 2011
 */
@Deprecated
public class AnnotatorDemo {

	///////////////////////////////////////////////////////// MAIN ///////////////////////////////////////////////////////////////////////


	public static void main(String[] args) throws AnnotationCompilationException, AnnotatorException, ParserRunException, TreeStringGeneratorException, IOException, AnnotatorException, ConfigurationException 
	{
		if (args.length == 0)
			throw new AnnotationCompilationException("usage: AnnotationRuleCompiler configurationFile.xml");
		ConfigurationFile confFile = new ConfigurationFile(new File(args[0]));
		ConfigurationParams compilationParams = confFile.getModuleConfiguration(RuleCompilerParameterNames.RULE_COMPILER_PARAMS_MODULE);
		ConfigurationParams annotationParams = confFile.getModuleConfiguration(ConfigurationParametersNames.TRUTH_TELLER_MODULE_NAME);
		File dir = compilationParams.getFile(RuleCompilerParameterNames.ANNOTATION_RUELS_DIRECTORY);		//new File(props.getProperty("annotationDirectoryName").trim());
		final String ruleFileSuffix = compilationParams.get(RuleCompilerParameterNames.RULE_FILE_SUFFIX);	//props.getProperty("graphFileSuffix").trim();
		
		// create an Annotated rule compiler
		AnnotationRuleCompiler compiler = new AnnotationRuleCompiler(compilationParams);
		List<AnnotationRuleWithDescription<ExtendedNode, BasicRuleAnnotations>> rulesWithDesc;
		
		rulesWithDesc =  compiler.compileFolder(dir, ruleFileSuffix);
		
		// serialize rules to file
		try 
		{
			String outFileName = annotationParams.getFile(ConfigurationParametersNames.ANNOTATION_RULES_FILE).getName();	
			FileOutputStream fos = new FileOutputStream(outFileName);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(rulesWithDesc);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		RulesViewer rv = new RulesViewer(rules);
//		AnnotatedRulesViewer rv = new AnnotatedRulesViewer(rules.subList(570, 600));
//		rv.view();
		
		System.out.println("\n\nMade " + rulesWithDesc.size() + " rules.\n\n");
		
		String text = "While the FDA has approved it only for prescription use, 6 states have passed laws that allow pharmacists to dispense the drug without a prescription after a discussion with the woman.";
		
		EasyFirstParser parser = new EasyFirstParser("b:/jars/stanford-postagger-2008-09-28/bidirectional-wsj-0-18.tagger");
		parser.init();
		parser.setSentence(text);
		parser.parse();
		BasicNode englishTree = parser.getParseTree();
		ExtendedNode sentence = TreeUtilities.copyFromBasicNode(englishTree);
		
		System.out.println("Annotating sentence: " + text);
		ExtendedRulesViewer rv = new ExtendedRulesViewer(null);
		rv.printTree(sentence, false);
		System.out.println("\n\n");
		
		SentenceAnnotator annotator = new DefaultSentenceAnnotator(annotationParams);
		annotator.setTree(sentence);
		annotator.annotate();
		ExtendedNode annotatedSentence = annotator.getAnnotatedTree(); 
		
		// print out a DOT of the original, and of the generated tree
		String outDirStr =  "c:/temp/shit";
		File outDir = new File(outDirStr);
		
		// clean outdir
		FilenameFilter dotJpgFileFilter = new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.endsWith(".dot") | name.endsWith(".jpg");
			}
		};
		File[] dotFiles = outDir.listFiles(dotJpgFileFilter);
		for (File file : dotFiles)
			file.delete();

		printAnnotatedDotFiles(sentence, annotatedSentence, text, "Annotation: " + text, outDir.getName(), 1);
		
		FilenameFilter dotFileFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".dot");
			}
		};
		// dot the outDir
		dotFiles = outDir.listFiles(dotFileFilter);
		for (File file : dotFiles)
			Runtime.getRuntime().exec("dot -O -Tjpg " + file);
		
	}

	/**
	 * print two dot files
	 * 
	 * @param tree
	 * @param rhs
	 * @param dotDir
	 * @param rightLabel 
	 * @param ndx
	 * @throws AnnotationCompilationException
	 */
	private static void printAnnotatedDotFiles(ExtendedNode lhs, ExtendedNode rhs, String leftLabel, String rightLabel, String dotDir, int ndx) 
		throws AnnotationCompilationException
	{
		// print out a DOT of the original, and of the generated tree
	
		File lhsFile = new File(dotDir, ndx + "_lhs.dot");
		lhsFile.delete();
		TreeDotFileGenerator<ExtendedInfo> tdfg;
		try
		{
			tdfg = new TreeDotFileGenerator<ExtendedInfo>(new AnnotatedExtendedNodeAndEdgeString() ,lhs, leftLabel,lhsFile);
			tdfg.generate();
		} catch (TreeDotFileGeneratorException e)
		{
			throw new AnnotationCompilationException("Error printing a dot file", e);
		}
		// print generatedTree
		File rhsFile = new File(dotDir, ndx + "_rhs.dot");
		rhsFile.delete();		
		try
		{
			tdfg = new TreeDotFileGenerator<ExtendedInfo>(new AnnotatedExtendedNodeAndEdgeString(),rhs, rightLabel,rhsFile);
			tdfg.generate();
		} catch (TreeDotFileGeneratorException e)
		{
			throw new AnnotationCompilationException("Error printing a dot file", e);
		}
	}
}
