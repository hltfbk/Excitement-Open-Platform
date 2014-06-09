package eu.excitementproject.eop.distsim.application.converter.db;


import eu.excitementproject.eop.distsim.storage.Redis;
import eu.excitementproject.eop.distsim.util.SortUtil;
import eu.excitementproject.eop.redis.RedisBasedStringListBasicMap;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * @author Meni Adler
 * @since 17 December 2013
 *
 * This program gets a dump of DIRT SQL rules table contains element similarities and convert it into Redis
 */
public class DIRTSimilaritySQL2Redis {
	public static void main(String[] args) throws Exception {
		
		if (args.length != 2) {
			System.out.println("Usage: java eu.excitementproject.eop.distsim.application.converter.db.DIRTSimilaritySQL2Redis" +
					"<in sql dump fule> " +
					"<out redis file> ");
		
		}
		
		Redis redis = new Redis(args[1]);
		redis.open();
		redis.clear();

		redis.write(RedisBasedStringListBasicMap.ELEMENT_CLASS_NAME_KEY,"eu.excitementproject.eop.distsim.items.PredicateElement");
		
		TIntObjectMap<String> id2description = new TIntObjectHashMap<String>();
		
		TIntObjectMap<TIntDoubleMap> element2similarities = new TIntObjectHashMap<TIntDoubleMap>();
		
		String line=null;

		BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
		String prefix = "INSERT INTO `framenet_easyfirst_element_table_no_duplicates` VALUES ";
		//String prefix = "INSERT INTO `reverb_templates` VALUES ";

		while ((line=reader.readLine())!=null) {
			if (line.startsWith(prefix)) {
				line = line.substring(prefix.length());
				String[] splits = line.split("\\),\\(");
				int i =0;
				for (String split : splits) {					
					if (i == 0)
						split = split.substring(1);
					if (i == splits.length-1) 
						split = split.substring(0,split.length()-2);
					
					String[] toks = split.split(",");
					int id = Integer.parseInt(toks[0]);
					String description = toks[1].substring(1,toks[1].length()-1);
					id2description.put(id, description);
					i++;
				}				
			}
		}		
		reader.close();
				
		System.out.println(id2description.size() + " items were found");
			
		reader = new BufferedReader(new FileReader(new File(args[0])));
		prefix = "INSERT INTO `framenet_rules_easyfirst_server_rules8_no_duplicates` VALUES ";
		//prefix = "INSERT INTO `reverb_rules` VALUES ";
		int pushedItems=0;
		while ((line=reader.readLine())!=null) {
			if (line.startsWith(prefix)) {
				line = line.substring(prefix.length());
				String[] splits = line.split("\\),\\(");
				int i =0;
				for (String split : splits) {					
					if (i == 0)
						split = split.substring(1);
					if (i == splits.length-1) 
						split = split.substring(0,split.length()-2);
					
					String[] toks = split.split(",");
					int id1 = Integer.parseInt(toks[0]);
					int id2 = Integer.parseInt(toks[1]);
					double score = Double.parseDouble(toks[2].substring(1,toks[2].length()-1));
					TIntDoubleMap similarities = element2similarities.get(id1);
					if (similarities == null) {
						similarities = new TIntDoubleHashMap();
						element2similarities.put(id1, similarities);
					}
					similarities.put(id2, score);					
					i++;
				}
			}
		}
		reader.close();
		
		TIntObjectIterator<TIntDoubleMap> it = element2similarities.iterator();
		while (it.hasNext()) {
			
			it.advance();
			int leftId = it.key();
			String left = id2description.get(leftId);
			if (left == null) {
				System.out.println("Unmapped item leftId: " + leftId);
				continue;
			}
			TIntDoubleMap rightSimilarities = it.value();
			LinkedHashMap<Integer, Double> sortedElementScores = SortUtil.sortMapByValue(rightSimilarities,true);			

			for (Entry<Integer, Double> entry : sortedElementScores.entrySet()) {
				int rightId = entry.getKey();
				String right = id2description.get(rightId);
				if (right == null) {
					System.out.println("Unmapped item rightId: " + rightId);
					continue;
				}
				double score = entry.getValue();
				if (right.contains(RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER))
					System.out.println("Element " + right + " contains the delimiter '" + RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER + "', and considered insignificant. The element is filtered from the model");
				else {
					redis.rpush(left, right + RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER + score);
					pushedItems++;
				}				
			}
		}
		
		System.out.println(pushedItems + " items were added to Redis");

	}
}
