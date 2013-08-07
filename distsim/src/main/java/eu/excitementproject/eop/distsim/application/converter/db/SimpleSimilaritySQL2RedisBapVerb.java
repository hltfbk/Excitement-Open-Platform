package eu.excitementproject.eop.distsim.application.converter.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

/**
 * @author Meni Adler
 * @since 28 Feb 2013
 *
 * This program gets a dump of SQL table contains element similarities, represented by three fiedls: left, right score
 * and add each row to Redis db, composed of element id map, and element similarity map
 */
public class SimpleSimilaritySQL2RedisBapVerb {
	public static void main(String[] args) throws Exception {
		
		if (args.length != 2) {
			System.out.println("Usage: java eu.excitementproject.eop.distsim.application.converter.db.SimpleSimilaritySQL2Redis" +
					"<in sql dump fule> " +
					"<out fule> ");
			
			/*
					"<in sql left field> " +
					"<in sql right field> " +
					"<in sql score field> " +
					"<out redis elements host> " +
					"<out redis elements port> " +
					"<out redis similarity host>" +
					"<out redis similarity port");*/
		}
		
		String inSQLDump = args[0];
		PrintStream out = new PrintStream(args[1]);
		
		/*String leftField = args[1];
		String rightField = args[2];
		String scoreField = args[3];
		String redisElementHost = args[4];
		int redisElementPort = Integer.parseInt(args[5]);
		String redisSimilarityHost = args[6];
		int redisSimilarityPort = Integer.parseInt(args[7]);*/
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(inSQLDump)));
		String line=null;
		String prefix = "INSERT INTO `verbs_1000` VALUES ";
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
					out.print(toks[0].substring(1,toks[0].length()-1) + "#V");
					out.print("\t");
					out.print(toks[1].substring(1,toks[1].length()-1) + "#V");
					out.print("\t");
					out.println(toks[2].substring(1,toks[2].length()-1));
					
					i++;
				}				
			}
		}
		reader.close();
		out.close();
	}
}
