package eu.excitementproject.eop.distsim.application.converter.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashMap;

import eu.excitementproject.eop.distsim.storage.RedisBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.util.SortUtil;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author Meni Adler
 * @since 28 Feb 2013
 *
 * This program gets a dump of SQL table contains element similarities, represented by three fiedls: left, right score
 * and add each row to Redis db, composed of element id map, and element similarity map
 */
public class File2Redis {
	
	public static void main(String[] args) throws Exception {
		
		if (args.length != 3) {
			System.out.println("Usage: java eu.excitementproject.eop.distsim.application.converter.db.SimpleSimilaritySQL2Redis" +
					"<in sql dump fule> " +
					"<out redis host>" +
					"<out redis port");
			System.exit(0);
		}
		

		String infile = args[0];
		String redisHost = args[1];
		int redisPort = Integer.parseInt(args[2]);
		RedisBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>> redis = new RedisBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>>(redisHost,redisPort);
		BufferedReader reader = new BufferedReader(new FileReader(infile));

		String line=null;		
		TIntObjectMap<TIntDoubleMap> scoresMap = new TIntObjectHashMap<TIntDoubleMap>();		
		while ((line=reader.readLine())!=null) {
			String[] toks = line.split("\t");
			
			int rightId = Integer.parseInt(toks[0]);
			int leftId = Integer.parseInt(toks[1]);
			double score = Double.parseDouble(toks[2]);
			TIntDoubleMap scores = scoresMap.get(rightId);
			if (scores == null) {
				scores = new TIntDoubleHashMap();
				scoresMap.put(rightId, scores);
			}
			scores.put(leftId, score);
		}
		reader.close();

		redis.clear();			
		TIntObjectIterator<TIntDoubleMap> it = scoresMap.iterator();
		while (it.hasNext()) {				
			it.advance();
			int rightId = it.key();
			TIntDoubleMap scores = it.value();
			redis.put(rightId, SortUtil.sortMapByValue(scores,true));
		}
	}

}
