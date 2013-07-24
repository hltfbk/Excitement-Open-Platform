/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.annotationrules.lexicon;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwi.JwiDictionary;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;
import eu.excitementproject.eop.transformations.generic.rule_compiler.RuleCompilerParameterNames;
import eu.excitementproject.eop.transformations.generic.rule_compiler.annotationrules.AnnotationCompilationException;
import eu.excitementproject.eop.transformations.generic.rule_compiler.annotationrules.AnnotationRuleLexiconUtils;
import eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature;

/**
 * This class is an executable that gets: the single word predicate lexicon properties file, word net dictionary path, and path to a new expanded lexicon. 
 * It loads the predicate list properties file, and for each pred, output to the expanded lexicon the same pred, along with its WN synonyms and entailments.
 * @author Amnon Lotan
 * 
 * @since Apr 11, 2012
 */
public class ExpandPredicatedWithWN {

	private static HashMap<String, PredicateSignature> mapPredToType;
	private static HashMap<String, PredicateSignature> map2ndPredToType = new HashMap<String, PredicateSignature>();
	private static HashMap<String, Set<PredicateSignature>> mapDuplicate2ndPreds = new HashMap<String, Set<PredicateSignature>>();

	/**
	 * Load the predicate list properties file, and for each pred, output its WN synonyms and entailments
	 * 
	 * @param args
	 * @throws CompilationException 
	 * @throws WordNetException 
	 * @throws IOException 
	 * @throws ConfigurationException 
	 * @throws ConfigurationFileDuplicateKeyException 
	 */
	public static void main(String[] args) throws CompilationException, WordNetException, IOException, ConfigurationFileDuplicateKeyException, ConfigurationException {
		
		if (args.length == 0)
			throw new AnnotationCompilationException("usage: AnnotationRuleCompiler configurationFile.xml");
		ConfigurationFile confFile = new ConfigurationFile(new File(args[0]));
		confFile.setExpandingEnvironmentVariables(true);
		ConfigurationParams compilationParams = confFile.getModuleConfiguration(RuleCompilerParameterNames.RULE_COMPILER_PARAMS_MODULE);
		
		String kernalPredListPropsFile = compilationParams.get(RuleCompilerParameterNames.KERNAL_SINGLE_WORD_LEXICON);
		mapPredToType = AnnotationRuleLexiconUtils.loadPredicateTypePropertiesFile(kernalPredListPropsFile);
		
		File pathToDict = compilationParams.getDirectory(RuleCompilerParameterNames.WORDNET_DICT); 
		Dictionary wn = new JwiDictionary(pathToDict);
		
		Set<String> predsBadForWN = Utils.arrayToCollection(new String[]{}, new LinkedHashSet<String>());
		
		File outFileName = new File("ExpandedPredicateList.txt");
		String expandedPropertiesFileName = compilationParams.get(RuleCompilerParameterNames.SINGLE_WORD_PREDICATE_TYPE_LEXICON); 
		File expandedPropertiesFile = new File(expandedPropertiesFileName);
		
		List<String> output = new Vector<String>();
		for (String pred : mapPredToType.keySet())
		{
			if (predsBadForWN.contains(pred))
				System.out.println("skipped " + pred);
			else
			{
				StringBuilder buf = new StringBuilder();
				buf.append(pred + "...\n");
				System.out.println(pred + "...");
				PredicateSignature predType = mapPredToType.get(pred);
				int i = 1;
				// get synsets

				for (Entry<WordNetPartOfSpeech, List<Synset>> synsetsEntry : wn.getSortedSynsetOf(pred).entrySet())
				{
					List<Synset> synsetList = synsetsEntry.getValue();
					WordNetPartOfSpeech pos = synsetsEntry.getKey();
					for (Synset synset : synsetList)
					{
						buf.append("(syn) ");
						printWordsToNewLineAndCount(buf, synset.getWords(), i, predType , pred, pos);
						i++;

						// get ENTAILMENTS
						for ( Synset entailedSynset : synset.getNeighbors(WordNetRelation.ENTAILMENT))
						{
							buf.append("(ent) ");
							printWordsToNewLineAndCount(buf, entailedSynset.getWords(), i, predType, pred, pos);
							i++;
						}

					}
				}


				output.add(buf.toString());
				System.out.println(buf.toString());
			}
		}

		
		output.add("\n\n*** Second hand determined predicates:");
		for(String pred : map2ndPredToType.keySet())
			output.add(pred + '/' + map2ndPredToType.get(pred));

		output.add("\n\n*** Second hand ambiguous predicates:");
		for(String pred : mapDuplicate2ndPreds.keySet())
			output.add(pred + ": " + mapDuplicate2ndPreds.get(pred));

		FileUtils.writeFile(outFileName, output);
				
		printWNExpandedPredicateTypePropertiesFile(mapPredToType, map2ndPredToType, expandedPropertiesFile);
		
		System.out.println("Expanded predicate lexicon to " + outFileName + " and to " + expandedPropertiesFile);

	}

	/**
	 * @param mapPredToType
	 * @param map2ndPredToType
	 * @param expandedPropertiesFile
	 * @throws IOException 
	 */
	private static void printWNExpandedPredicateTypePropertiesFile(HashMap<String, PredicateSignature> mapPredToType, HashMap<String, PredicateSignature> map2ndPredToType,
			File expandedPropertiesFile) throws IOException {
		
		// join maps
		mapPredToType.putAll(map2ndPredToType);
		
		// build reversed map
		ValueSetMap<PredicateSignature, String> mapTypeToPred = Utils.reverseMapIntoValueSetMap(mapPredToType);
		
		StringBuilder buf = new StringBuilder();
		for (PredicateSignature predType : mapTypeToPred.keySet())
			buf.append(predType.name()).append(" = ").append(mapTypeToPred.get(predType)).append("\n\n");
				
		String contents = buf.toString().replaceAll("[\\[\\]]", "");		// remove Java's parenthesis
		FileUtils.writeFile(expandedPropertiesFile, contents);
	}

	/**
	 * @param buf
	 * @param words
	 * @param i
	 * @param predicateType 
	 * @param originalPred 
	 * @param pos 
	 */
	private static void printWordsToNewLineAndCount(StringBuilder buf, Set<String> words, int i, PredicateSignature predicateType, String originalPred, 
			WordNetPartOfSpeech pos) {
		buf.append('\"').append(originalPred).append('\"').append('/').append(pos).append('/').append(predicateType).append(" #"+i+": ");
		for (String word : words)
		{
			if (!word.contains("_"))		// screen out multi word expressions
			{
				buf.append(word).append(", ");
				
				// screen out 2nd hand preds that appear more than once
				if ((!mapPredToType.keySet().contains(word)) )	// preds are expected to have the same predType across different POSs, so it's OK to neglect the POS in the key set
					if (!map2ndPredToType .containsKey(word)) 
						if (!mapDuplicate2ndPreds.containsKey(word))
							map2ndPredToType.put(word, predicateType);
						else
						{
							// add this predType to the word's dup set
							Set<PredicateSignature> predTypes = mapDuplicate2ndPreds.get(word);
							predTypes.add(predicateType);
							mapDuplicate2ndPreds.put(word, predTypes);
						}
					else
						if (map2ndPredToType.get(word).equals(predicateType))
							;
						else
						{
							Set<PredicateSignature> predTypes = new LinkedHashSet<PredicateSignature>();
							PredicateSignature prevPredType = map2ndPredToType.remove(word);
							predTypes.add(prevPredType);
							predTypes.add(predicateType);
							mapDuplicate2ndPreds.put(word, predTypes);
						}
			}
		}	
		buf.append('\n');
		
	}

}
