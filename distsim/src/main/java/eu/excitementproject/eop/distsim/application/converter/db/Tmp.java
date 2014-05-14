package eu.excitementproject.eop.distsim.application.converter.db;



import eu.excitementproject.eop.distsim.storage.Redis;
import eu.excitementproject.eop.redis.RedisBasedStringListBasicMap;

public class Tmp {
	public static void main(String[] args) throws Exception {
		
		if (args.length != 1) {
			System.out.println("Usage: java eu.excitementproject.eop.distsim.application.converter.db.Tmp <in redis file> ");
		
		}
		
		Redis redis = new Redis(args[0]);
		redis.open();
		redis.write(RedisBasedStringListBasicMap.ELEMENT_CLASS_NAME_KEY,"eu.excitementproject.eop.distsim.items.PredicateElement");
	}
}
