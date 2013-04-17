package eu.excitementproject.eop.distsim.application.converter.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author Meni Adler
 * @since 28 Feb 2013
 *
 * This program gets a dump of SQL table contains element similarities, represented by three fiedls: left, right score
 * and add each row to Redis db, composed of element id map, and element similarity map
 */
public class SimpleSimilaritySQL2Redis {
	public static void main(String[] args) throws Exception {
		
		if (args.length != 8) {
			System.out.println("Usage: java eu.excitementproject.eop.distsim.application.converter.db.SimpleSimilaritySQL2Redis" +
					"<in sql dump fule> " +
					"<in sql left field> " +
					"<in sql right field> " +
					"<in sql score field> " +
					"<out redis elements host> " +
					"<out redis elements port> " +
					"<out redis similarity host>" +
					"<out redis similarity port");
		}
		
		String inSQLDump = args[0];
		/*String leftField = args[1];
		String rightField = args[2];
		String scoreField = args[3];
		String redisElementHost = args[4];
		int redisElementPort = Integer.parseInt(args[5]);
		String redisSimilarityHost = args[6];
		int redisSimilarityPort = Integer.parseInt(args[7]);*/
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(inSQLDump)));
		String line=null;
		
		while ((line=reader.readLine())!=null) {
			System.out.println(line);
		}
		reader.close();
		
	}
}
