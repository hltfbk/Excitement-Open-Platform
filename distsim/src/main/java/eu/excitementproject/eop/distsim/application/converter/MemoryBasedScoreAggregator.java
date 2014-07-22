package eu.excitementproject.eop.distsim.application.converter;

import eu.excitementproject.eop.distsim.storage.RedisBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.util.SortUtil;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;

public class MemoryBasedScoreAggregator {
	public static void main(String[] args) {
				
		if (args.length != 2) {
			System.err.println("Usage: MemoryBasedLeft2Right <in score file> <out aggregated scores redis file>");
			System.exit(0);
		}


		String infile = args[0];
		String redisSimilarityFile = args[1];
		
		try {			
			
			BufferedReader reader = new BufferedReader(new FileReader(new File(infile)));
			String line;
			TIntObjectMap<TIntDoubleMap> scoresMap = new TIntObjectHashMap<TIntDoubleMap>();
			
			while ((line=reader.readLine())!=null) {
				String[] toks = line.split("\t");
				
				int id1 = Integer.parseInt(toks[0]);
				int id2 = Integer.parseInt(toks[1]);
				double score = Double.parseDouble(toks[2]);
				TIntDoubleMap scores = scoresMap.get(id1);
				if (scores == null) {
					scores = new TIntDoubleHashMap();
					scoresMap.put(id1, scores);
				}
				scores.put(id2, score);
			}
			reader.close();

			RedisBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>> redis = new RedisBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>>(redisSimilarityFile,false);
			redis.clear();			
			TIntObjectIterator<TIntDoubleMap> it = scoresMap.iterator();
			while (it.hasNext()) {				
				it.advance();
				int id1 = it.key();
				TIntDoubleMap scores = it.value();
				redis.put(id1, SortUtil.sortMapByValue(scores,true));
			}			

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}


