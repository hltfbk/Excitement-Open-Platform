package eu.excitementproject.eop.distsim.application.converter.db;

import eu.excitementproject.eop.distsim.storage.Redis;

import eu.excitementproject.eop.distsim.storage.RedisBasedStringListBasicMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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
		String line=null;

		BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
		//String prefix = "INSERT INTO `reverb_local_distlexfeatures_templates` VALUES ";
		String prefix = "INSERT INTO `reverb_templates` VALUES ";

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
		//prefix = "INSERT INTO `reverb_local_distlexfeatures_rules` VALUES ";
		prefix = "INSERT INTO `reverb_rules` VALUES ";
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
					
					String item1 = id2description.get(id1);
					if (item1 == null) {
						System.out.println("Unmapped item id1: " + id1);
						continue;
					}
					String item2 = id2description.get(id2);
					if (item2 == null) {
						System.out.println("Unmapped item id2: " + id2);
						continue;
					}
					
					if (item2.contains(RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER))
						System.out.println("Element " + item2 + " contains the delimiter '" + RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER + "', and considered insignificant. The element is filtered from the model");
					else {
						redis.rpush(item1, item2 + RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER + score);
						pushedItems++;
					}
					i++;
				}
			}
		}		
		reader.close();
		
		System.out.println(pushedItems + " items were added to Redis");

	}
}
