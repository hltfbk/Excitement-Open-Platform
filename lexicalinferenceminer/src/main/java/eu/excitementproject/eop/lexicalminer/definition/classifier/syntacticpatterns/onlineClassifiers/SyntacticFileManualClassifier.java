package eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.onlineClassifiers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import eu.excitementproject.eop.lexicalminer.LexiclRulesRetrieval.RuleData;
import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;
import eu.excitementproject.eop.lexicalminer.definition.classifier.OnlineClassifier;

public class SyntacticFileManualClassifier extends OnlineClassifier {

	private HashMap<String, Double> m_PosMap; 
	private HashMap<String, Double> m_RelationMap; 
	private HashMap<String, Double> m_PosRelationMap;
	private HashMap<String, Double> m_WordsMap; 
	private HashMap<String, Double> m_FullMap; 		
	private HashMap<String, Double> m_PosMapContains; 
	private HashMap<String, Double> m_RelationMapContains; 
	private HashMap<String, Double> m_PosRelationMapContains;
	private HashMap<String, Double> m_WordsMapContains; 
	private HashMap<String, Double> m_FullMapContains; 	

	private String m_classifierComment;
	
	//TODO- change info format in comment (include the "contains" and multy patterns type
	/**
	 * The Defualt c-tor calles the other c-tor with the "ManualClassifierInputFile" file_name,  a file name to read the patterns-rank dictionary from.
	 * The file is in the next format:
	 * <first line> comment of that classifier, will be used in the "toString" function
	 * <second line> what kinds of patterns are we looking for? can be: "words", "pos", "relation", "pos_relation" OR "full"
	 * And then pairs (2 lines for each pair) of the pattern to look for (first line) and the rank to give to rules with that pattern (second line)
	 * 
	 * Lines that start with "//" are ignored (and trying to use the next line ad the main...
	 * @param classifierInputFileName
	 * @throws Exception 
	 */
	public SyntacticFileManualClassifier(RetrievalTool retrivalTool, double NPBonus) throws Exception {
		this("src" + File.separator + "ac" + File.separator + "biu" + File.separator + "nlp" + File.separator
				+ "lexicalMiner" + File.separator + "definition" + File.separator + 
				"classifier" + File.separator + "syntacticpatterns" + File.separator 
				+ "onlineClassifiers" + File.separator + "ClassifierInputFiles" +
				File.separator + "ManualClassifierInputFile.txt", retrivalTool, NPBonus);
	}	
	
	@Override
	public String toString() {
		return "SyntacticFileManualCalssifier, " + m_classifierComment;
	}

	/**
	 * The C-tor gets a file name to read the patterns-rank dictionary from.
	 * The file is in the next format:
	 * <first line> comment of that classifier, will be used in the "toString" function
	 * <second line> what kinds of patterns are we looking for? can be: "words", "pos", "relation", "pos_relation" OR "full"
	 * And then pairs (2 lines for each pair) of the pattern to look for (first line) and the rank to give to rules with that pattern (second line)
	 * @param classifierInputFileName
	 * @throws Exception 
	 */
	public SyntacticFileManualClassifier(String classifierInputFileName, RetrievalTool retrivalTool, Double NPBonus) throws Exception {
		super(retrivalTool, NPBonus);
		m_PosMap = new HashMap<String, Double>();		
		m_RelationMap = new HashMap<String, Double>();
		m_PosRelationMap = new HashMap<String, Double>();
		m_WordsMap = new HashMap<String, Double>();
		m_FullMap = new HashMap<String, Double>();
		
		m_PosMapContains = new HashMap<String, Double>();		
		m_RelationMapContains = new HashMap<String, Double>();
		m_PosRelationMapContains = new HashMap<String, Double>();
		m_WordsMapContains = new HashMap<String, Double>();
		m_FullMapContains = new HashMap<String, Double>();		
		
		File file = new File(classifierInputFileName);
		if (!file.exists())
			throw new FileNotFoundException(String.format("Missing classifier input file in :%s",classifierInputFileName));
		
		BufferedReader input =  new BufferedReader(new FileReader(file));
		
		String line;
		String rank;
		m_classifierComment = getLine(input);
		
		String m_patternColumnName = null;
		while ((line=getLine(input))!=null)
		{
			// if got a new pattern type
			if (line.startsWith(">>> "))
			{
				m_patternColumnName = line.replaceFirst(">>> ", "");			
			}
			else // got a pattern
			{	
				if (m_patternColumnName == null)
				{
					throw new Exception("Didn't found  (In file) a pattern type to check");
				}
				
				rank = getLine(input);
				if (rank == null)
				{
					throw new Exception("Missing a rank in the end of the file");				
				}
				 
				Double rankD =null;
				try
				{
					rankD = new Double(rank); 
				}
				catch (NumberFormatException nE)
				{
					throw new NumberFormatException("Got a String and not a rank. " +nE.getMessage());
				}
								
				if (m_patternColumnName.equalsIgnoreCase("words"))
				{
					m_WordsMap.put(line, rankD);
				}
				else if (m_patternColumnName.equalsIgnoreCase("pos"))
				{
					m_PosMap.put(line, rankD);
				}
				else if (m_patternColumnName.equalsIgnoreCase("relation"))
				{
					m_RelationMap.put(line, rankD);
				}
				else if (m_patternColumnName.equalsIgnoreCase("pos_relation"))
				{
					m_PosRelationMap.put(line, rankD);
				}
				else if (m_patternColumnName.equalsIgnoreCase("full"))
				{
					m_FullMap.put(line, rankD);
				}
				else if (m_patternColumnName.equalsIgnoreCase("words contains"))
				{
					m_WordsMapContains.put(line, rankD);
				}
				else if (m_patternColumnName.equalsIgnoreCase("pos contains"))
				{
					m_PosMapContains.put(line, rankD);
				}
				else if (m_patternColumnName.equalsIgnoreCase("relation contains"))
				{
					m_RelationMapContains.put(line, rankD);
				}
				else if (m_patternColumnName.equalsIgnoreCase("pos_relation contains"))
				{
					m_PosRelationMapContains.put(line, rankD);
				}
				else if (m_patternColumnName.equalsIgnoreCase("full contains"))
				{
					m_FullMapContains.put(line, rankD);
				}				
				else
				{
					throw new Exception("pattern type " + m_patternColumnName + " is not supported");				
				}
			}
		}	
		int i =0;
		i++;
		System.out.println(i);

	}
	
	public static String getLine (BufferedReader input) throws IOException
	{
		String line;
		do
		{
			line = input.readLine();
			if (line != null)
			{
				int index = line.indexOf("//");
				if (index >= 0)
				{
					line = line.substring(0, index).trim();	//remove all comment
				}
			}
		} while ((line != null) && (line.length() == 0 ));
		System.out.println(line);
		return line;
	}

	@Override
	public double getClassifierRank(RuleData rule) {
		
		double max_rank = 0;

		max_rank = getPatternMaxRank(max_rank, this.m_WordsMap, rule.getWordsPattern());
		max_rank = getPatternMaxRank(max_rank, this.m_PosMap, rule.getPOSPattern());
		max_rank = getPatternMaxRank(max_rank, this.m_RelationMap, rule.getRelationPattern());
		max_rank = getPatternMaxRank(max_rank, this.m_PosRelationMap, rule.getPOSrelationsPattern());
		max_rank = getPatternMaxRank(max_rank, this.m_FullMap, rule.getFullPattern());
		max_rank = getPatternMaxRankContains(max_rank, this.m_WordsMapContains, rule.getWordsPattern());
		max_rank = getPatternMaxRankContains(max_rank, this.m_PosMapContains, rule.getPOSPattern());
		max_rank = getPatternMaxRankContains(max_rank, this.m_RelationMapContains, rule.getRelationPattern());
		max_rank = getPatternMaxRankContains(max_rank, this.m_PosRelationMapContains, rule.getPOSrelationsPattern());
		max_rank = getPatternMaxRankContains(max_rank, this.m_FullMapContains, rule.getFullPattern());
		
		if (max_rank > 0)
		{
			return max_rank;
		}
		else
		{
			return rule.getDefultRank();
		}
	}

	private double getPatternMaxRank(double max_rank, HashMap<String, Double> current_map, String key) {
		if (key != null)
		{
			if (current_map.containsKey(key))
			{
				double current_rank = current_map.get(key);
				if (current_rank > max_rank)
				{
					return current_rank;
				}
			}
		}
		return max_rank;
	}	
	
	private double getPatternMaxRankContains(double max_rank, HashMap<String, Double> current_map, String key) {
		double local_max_rank = 0;
		if (key != null)
		{
			if (current_map.size() > 0)	//there are values to compare to
			{
				for (String map_key : current_map.keySet())	//check each map key if it contained in the rule key
				{
					if (key.contains(map_key))
					{
						double current_rank = current_map.get(map_key);
						if (current_rank > local_max_rank)
						{
							local_max_rank = current_rank;
						}
					}
				}
				
				if (local_max_rank > max_rank)
				{
					return local_max_rank;
				}
			}			
		}
		
		return max_rank;
	}
}



