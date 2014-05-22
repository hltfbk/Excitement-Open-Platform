package eu.excitementproject.eop.transformations.generic.truthteller.conll;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules.conll.RuleConllStringConverter;

/**
 * Convert a  tree(s) into CoNLL String/file format.<br>
 * @see	http://ilk.uvt.nl/conll/\#dataformat
 * 
 * @author Amnon Lotan
 *
 * @since Jul 14, 2012
 */
public class AnnotatedTreeToConllCoverter {
	
	/**
	 * Suffix for CoNLL tree files
	 */
	public static final String FILE_SUFFIX = ".cnt";
	
	//////////////////////////////////////////////// PUBLIC	////////////////////////////////////////////////////////
	
	/**
	 * Print the given set of rules with descriptions out to a set of text files in CoNLL format in the given directory, using the given {@link RuleConllStringConverter}.<br>
	 * Each CoNLL file's title is the description of its original rule, with a suffix that is a running index for all rules that share the same description.<br>
	 * If the folder contains files with same names, they will be overwritten. 
	 * @param trees a set of rules, does not have to be sorted
	 * @param outputFolder
	 * @param nodeToConllStringConverter
	 * @throws ConllConverterException
	 */
	public static <I extends Info, N extends AbstractNode<I, N>> void treesToConllFiles(List<N> trees, File outputFolder, 
			TreeConllStringConverter<I, N> nodeToConllStringConverter) throws ConllConverterException
	{
		if (trees == null)
			throw new ConllConverterException("got null trees");
		if (outputFolder == null)
			throw new ConllConverterException("got null folder");
		if (!outputFolder.isDirectory())
			throw new ConllConverterException("not a folder: " + outputFolder);
		if (nodeToConllStringConverter == null)
			throw new ConllConverterException("got null ConllStringConvertor");

		// pad filename numbers with leading zeros
		final int digits = getDigits( trees.size());
		final String formatString = "%0" + digits + "d"; 
		int i = 1;
		for (N tree : trees)
		{
			File ruleFile = new File(outputFolder, "sentence_" + String.format(formatString, i++) + FILE_SUFFIX);
			ruleFile.delete();
			try {
				FileUtils.writeFile(ruleFile,  treeToConll(tree, nodeToConllStringConverter));
			} catch (IOException e) {
				throw new ConllConverterException("Error writing to file: " + ruleFile, e);
			}
		}
	}
	
	/**
	 * Print a tree to CoNLL format String:<br>
	 * @param tree
	 * @param nodeToConllStringConvertor
	 * @return
	 * @throws ConllConverterException
	 */
	public static <I extends Info, N extends AbstractNode<I, N>> String treeToConll(N tree, TreeConllStringConverter<I, N> nodeToConllStringConvertor) 
		throws ConllConverterException
	{
		if (tree == null)
			throw new ConllConverterException("got null tree");
		if (nodeToConllStringConvertor == null)
			throw new ConllConverterException("got null ConllStringConvertor");
		
		// first assign a CoNLL ID to each node
		Map<N, Integer> mapNodeToId = mapNodesToConllIds(tree);
		
		String conllStr = null;
		try {
			conllStr = treeToConll(tree, mapNodeToId , nodeToConllStringConvertor);
		} catch (ConllConverterException e) {
			throw new ConllConverterException("Error cnverting this rule's LHS tree to CoNLL representation, see nested: " + tree, e);
		}
		
		return conllStr;
	}

	////////////////////////////////////////////////////////// PRIVATE	////////////////////////////////////////////////////////////////////

	/**
	 * @param treeRoot
	 * @param mapNodeToId
	 * @param nodeToConllStringConvertor
	 * @return
	 * @throws ConllConverterException
	 */
	private static <I extends Info, N extends AbstractNode<I, N>> String treeToConll(N treeRoot, Map<N, Integer> mapNodeToId, 
			TreeConllStringConverter<I, N> nodeToConllStringConvertor) throws ConllConverterException 
	{
		Map<N, N> nodeToParentMap = AbstractNodeUtils.parentMap(treeRoot);
		
		StringBuilder conllStr = new StringBuilder();
		if (treeRoot != null)
		{
			conllStr.append(nodeToConll(treeRoot, mapNodeToId , nodeToParentMap, nodeToConllStringConvertor));
			conllStr.append('\n');
			
		}
		return conllStr.toString();
	}

	/**
	 * @param node
	 * @param mapNodeToId
	 * @param nodeToConllStringConvertor
	 * @return
	 * @throws ConllConverterException
	 */
	private static <I extends Info, N extends AbstractNode<I, N>> StringBuilder nodeToConll(N node,  Map<N, Integer> mapNodeToId, Map<N, N> nodeToParentMap,
			TreeConllStringConverter<I, N> nodeToConllStringConvertor) throws ConllConverterException 
	{
		StringBuilder conllStr = new StringBuilder();
		conllStr.append(nodeToConllStringConvertor.convert(node, mapNodeToId, nodeToParentMap.get(node))).append('\n');
		if (node.hasChildren())
			for (N child : node.getChildren())
				conllStr.append(nodeToConll(child, mapNodeToId, nodeToParentMap, nodeToConllStringConvertor));
		
		return conllStr;
	}
	
	private static <I extends Info, N extends AbstractNode<I, N>> Map<N, Integer> mapNodesToConllIds(N tree)
	{
		Map<N, Integer> mapNodesToIds = mapNodesOfTreeToIds(tree);
		mapNodesToIds.put(null, TreeConllStringConverter.ROOT_ID);	
		return  mapNodesToIds;
	}
	
	private static <I extends Info, N extends AbstractNode<I, N>> Map<N, Integer> mapNodesOfTreeToIds(N root )
	{
		Map<N, Integer> mapNodesToIds = new LinkedHashMap<N, Integer>();
		Integer id = 1;
		
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
