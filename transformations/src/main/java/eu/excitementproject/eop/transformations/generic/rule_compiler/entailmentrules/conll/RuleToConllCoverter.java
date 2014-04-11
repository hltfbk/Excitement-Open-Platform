package eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules.conll;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.transformations.generic.rule_compiler.RuleCompilerParameterNames;
import eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules.EntailmentCompilationException;
import eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules.EntailmentRuleCompiler;
import eu.excitementproject.eop.transformations.generic.truthteller.conll.TreeConllStringConverter;

/**
 * Convert a {@link SyntacticRule} or a set of rules into CoNLL String/file format.<br>
 * <b>Assumption:</b> each node in the LHS and RHS trees points at its parent 
 * @see	http://ilk.uvt.nl/conll/\#dataformat
 * 
 * @author Amnon Lotan
 *
 * @since Jul 14, 2012
 */
public class RuleToConllCoverter {
	
	/**
	 * Suffix for CoNLL rule files
	 */
	public static final String FILE_SUFFIX = ".cnr";

	public static void main(String[] args) throws EntailmentCompilationException, ConfigurationException, ConllConverterException 
	{
		if (args.length == 0)
			throw new EntailmentCompilationException("usage: EntailmentRuleCompiler configurationFile.xml");
		ConfigurationFile confFile = new ConfigurationFile(new File(args[0]));
		confFile.setExpandingEnvironmentVariables(true);
		ConfigurationParams compilationParams = confFile.getModuleConfiguration(RuleCompilerParameterNames.RULE_COMPILER_PARAMS_MODULE);
		File dir = compilationParams.getDirectory(RuleCompilerParameterNames.ENTAILMENT_RULES_DIRECTORY);	
		final String ruleFileSuffix = compilationParams.get(RuleCompilerParameterNames.RULE_FILE_SUFFIX);	

		// create an english node rule compliler
		EntailmentRuleCompiler compiler = new EntailmentRuleCompiler(); 
		Set<RuleWithConfidenceAndDescription<Info, BasicNode>> rulesWithCD = new LinkedHashSet<RuleWithConfidenceAndDescription<Info,BasicNode>>(
				compiler.compileFolder(dir, ruleFileSuffix));
		File conllDir = new File(compilationParams.get(RuleCompilerParameterNames.CONLL_RULES_DIRECTORY));	
		conllDir.mkdirs();
		
		
		System.out.println("Now printing " + rulesWithCD.size() + " rules into CoNLL format...");
		rulesToConllFiles(rulesWithCD, conllDir, new BasicConllStringConvertor());
		System.out.println("Done!");
	}
	
	//////////////////////////////////////////////// PUBLIC	////////////////////////////////////////////////////////
	
	/**
	 * Print the given set of rules with descriptions out to a set of text files in CoNLL format in the given directory, using the given {@link RuleConllStringConverter}.<br>
	 * Each CoNLL file's title is the description of its original rule, with a suffix that is a running index for all rules that share the same description.<br>
	 * If the folder contains files with same names, they will be overwritten. 
	 * @param rulesWithDescription a set of rules, does not have to be sorted
	 * @param outputFolder
	 * @param nodeToConllStringConvertor
	 * @throws ConllConverterException
	 */
	public static <I extends Info, N extends AbstractNode<I, N>> void rulesToConllFiles(Set<RuleWithConfidenceAndDescription<I, N>> rulesWithDescription, 
			File outputFolder, RuleConllStringConverter<I, N> nodeToConllStringConvertor) 
		throws ConllConverterException
	{
		if (rulesWithDescription == null)
			throw new ConllConverterException("got null rules");
		if (outputFolder == null)
			throw new ConllConverterException("got null folder");
		if (!outputFolder.isDirectory())
			throw new ConllConverterException("not a folder: " + outputFolder);
		if (nodeToConllStringConvertor == null)
			throw new ConllConverterException("got null ConllStringConvertor");

		// pad filename numbers with leading zeros
		final int digits = getDigits( rulesWithDescription.size());
		final String formatString = "%0" + digits + "d"; 
		
		// for each distinct description, keep track of how many times we've seen it
		Map<String, Integer> mapDescriptionToIndex = new LinkedHashMap<String, Integer>();
		for (RuleWithConfidenceAndDescription<I, N> ruleWithDesc : rulesWithDescription)
		{
			String description = ruleWithDesc.getDescription();
			Integer descriptionCount = mapDescriptionToIndex.containsKey(description) ? mapDescriptionToIndex.get(description) : 1;
			mapDescriptionToIndex.put(description, descriptionCount + 1);
			
			File ruleFile = new File(outputFolder, description + '_' + String.format(formatString, descriptionCount) + FILE_SUFFIX);
			ruleFile.delete();
			try {
				FileUtils.writeFile(ruleFile,  ruleToConll(ruleWithDesc.getRule(), nodeToConllStringConvertor));
			} catch (IOException e) {
				throw new ConllConverterException("Error writing to file: " + ruleFile, e);
			}
		}
	}
	
	/**
	 * Print a {@link SyntacticRule} to CoNLL format String:<br>
	 * <li>The LHS is a CoNLL paragraph
	 * <li>The RHS is a CoNLL paragraph beneath it
	 * <li>To express alignments, each node has an extra last column, which is the ID (first column) of its aligned node in the other tree. '_' means no alignment
	 * @param rule
	 * @param nodeToConllStringConvertor
	 * @return
	 * @throws ConllConverterException
	 */
	public static <I extends Info, N extends AbstractNode<I, N>> String ruleToConll(SyntacticRule<I, N> rule, RuleConllStringConverter<I, N> nodeToConllStringConvertor) 
		throws ConllConverterException
	{
		if (rule == null)
			throw new ConllConverterException("got null rule");
		if (nodeToConllStringConvertor == null)
			throw new ConllConverterException("got null ConllStringConvertor");
		
		// first assign a coNLL ID to each node
		Map<N, Integer> mapNodeToId = mapNodesToConllIds(rule);
		
		// second, create a map from node to the ID of its aligned node
		BidirectionalMap<N, N> alignments = rule.getMapNodes();
		
		StringBuilder conllStr = new StringBuilder();
		try {
			conllStr.append(ruleTreeToConll(rule.getLeftHandSide(), alignments, mapNodeToId , nodeToConllStringConvertor));
		} catch (ConllConverterException e) {
			throw new ConllConverterException("Error cnverting this rule's LHS tree to CoNLL representation, see nested: " + rule, e);
		}
		try {
			conllStr.append(ruleTreeToConll(rule.getRightHandSide(), alignments, mapNodeToId , nodeToConllStringConvertor));
		} catch (ConllConverterException e) {
			throw new ConllConverterException("Error cnverting this rule's RHS tree to CoNLL representation, see nested: " + rule, e);
		}
		
		return conllStr.toString();
	}

	////////////////////////////////////////////////////////// PRIVATE	////////////////////////////////////////////////////////////////////
	/**
	 * @param treeRoot
	 * @return
	 * @throws ConllConverterException 
	 */
	private static <I extends Info, N extends AbstractNode<I, N>> StringBuilder ruleTreeToConll(N treeRoot, BidirectionalMap<N, N> alignments, 
			Map<N, Integer> mapNodeToId, RuleConllStringConverter<I, N> nodeToConllStringConvertor) throws ConllConverterException 
	{
		StringBuilder conllStr = new StringBuilder();
		if (treeRoot != null)
		{
			conllStr.append(nodeToConll(treeRoot, alignments , mapNodeToId , nodeToConllStringConvertor));
			conllStr.append('\n');
			
		}
		return conllStr;
	}

	/**
	 * @param node
	 * @param mapNodeToId
	 * @param id
	 * @param nodeToConllStringConvertor 
	 * @param variableId 
	 * @return
	 * @throws ConllConverterException 
	 */
	private static <I extends Info, N extends AbstractNode<I, N>> StringBuilder nodeToConll(N node,  BidirectionalMap<N, N> alignments, Map<N, Integer> mapNodeToId, 
			RuleConllStringConverter<I, N> nodeToConllStringConvertor) throws ConllConverterException 
	{
		StringBuilder conllStr = new StringBuilder();
		int variableId = -1;		// -1 means no alignment
		if (alignments.leftContains(node))
			variableId = mapNodeToId.get(alignments.leftGet(node));
		else if (alignments.rightContains(node))
			variableId = mapNodeToId.get(alignments.rightGet(node));
		
		conllStr.append(nodeToConllStringConvertor.convert(node, mapNodeToId, variableId)).append('\n');
		if (node.hasChildren())
			for (N child : node.getChildren())
				conllStr.append(nodeToConll(child, alignments ,  mapNodeToId, nodeToConllStringConvertor));
		
		return conllStr;
	}
	
	private static <I extends Info, N extends AbstractNode<I, N>> Map<N, Integer> mapNodesToConllIds(SyntacticRule<I,N> rule)
	{
		Map<N, Integer> mapNodesToIds = mapNodesOfTreeToIds(rule.getLeftHandSide());
		mapNodesToIds.putAll(mapNodesOfTreeToIds(rule.getRightHandSide()));
		mapNodesToIds.put(null, TreeConllStringConverter.ROOT_ID);	
		return  mapNodesToIds;
	}
	
	private static <I extends Info, N extends AbstractNode<I, N>> Map<N, Integer> mapNodesOfTreeToIds(N root )
	{
		Map<N, Integer> mapNodesToIds = new LinkedHashMap<N, Integer>();
		Integer id = 1;
		
		// fill up the queue with nodes
		List<N> nodes = AbstractNodeUtils.treeToList(root);
		for (N node : nodes)
			mapNodesToIds.put(node, id++);
		
		return mapNodesToIds;
	}
	
	
	/**
	 * Count number of decimal digits
	 * @param size
	 * @return
	 * @throws ConllConverterException 
	 */
	private static int getDigits(int size) throws ConllConverterException {
		if (size == 0)
			return 1;
		if (size < 0)
			throw new ConllConverterException("Got negative size: " + size);
		int digits;
		for(digits = 0; size > 0; size /= 10, digits++ )
			;
		return digits;
	}

}
