package eu.excitementproject.eop.lexicalminer.redis;

import eu.excitementproject.eop.redis.BasicRedisRunner;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Meni Adler
 * @since 17 December 2013
 *
 * This program gets a dump of DIRT SQL rules table contains element similarities and convert it into Redis
 */
public class SQL2RedisConverter {
	
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {		
		
		if (args.length != 4) {
			System.out.println("Usage: java eu.excitementproject.eop.lexicalminer.redis.SQL2RedisConverter" +
					"<in sql dump file> " +
					"<out l2r redis file> " +
					"<out r2l redis file> +" +
					"<number of classifiers> ");
			System.exit(0);
		}
		
		//String classifierClassName = args[1];
		//int classifierId = Integer.parseInt(args[2]);

		//Assumption: the classifiers are identified by 1...numOfClassifiers
		int numOfClassifiers = Integer.parseInt(args[3]);
		
		int lPort = BasicRedisRunner.getInstance().run(args[1]);
		int rPort = BasicRedisRunner.getInstance().run(args[2]);
		
		JedisPool lpool = new JedisPool(new JedisPoolConfig(), "localhost",lPort,10000);
		Jedis lJedis = lpool.getResource();
		lJedis.connect();
		lJedis.getClient().setTimeoutInfinite();
		lJedis.flushAll();
		
		JedisPool rpool = new JedisPool(new JedisPoolConfig(), "localhost",rPort,10000);
		Jedis rJedis = rpool.getResource();
		rJedis.connect();
		rJedis.getClient().setTimeoutInfinite();
		rJedis.flushAll();

		TIntObjectMap<String> termId2str = new TIntObjectHashMap<String>();		
		TIntObjectMap<String> typeId2StrAndDefRank = new TIntObjectHashMap<String>();
		TIntObjectMap<TDoubleArrayList> ruleId2ClassifierRank = new TIntObjectHashMap<TDoubleArrayList>();
		TIntObjectMap<String> ruleId2Patterns = new TIntObjectHashMap<String>();

		String line=null;
		BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
		
		String termPrefix = "INSERT INTO `terms` VALUES ";
		String typePrefix = "INSERT INTO `ruletypes` VALUES ";
		String rankPrefix = "INSERT INTO `rulesranks` VALUES ";
		String patternPrefix = "INSERT INTO `rulepatterns` VALUES ";
		String rulesPrefix = "INSERT INTO `rules` VALUES ";
		String classifierPrefix = "INSERT INTO `classifiers` VALUES ";

		
		while ((line=reader.readLine())!=null) {
			
			//terms
			if (line.startsWith(termPrefix)) {
				line = line.substring(termPrefix.length());
				String[] splits = line.split("\\),\\(");
								
				int i =0;
				for (String split : splits) {
					
					try {
						if (i == 0)
							split = split.substring(1);
						if (i == splits.length-1) 
							split = split.substring(0,split.length()-2);
						
						int pos = split.indexOf(",");
						int termId = Integer.parseInt(split.substring(0,pos));
						String termStr = split.substring(pos+2,split.length()-1).trim();
						//String[] toks = split.split(",");
						//int termId = Integer.parseInt(toks[0]);
						//String termStr = toks[1].substring(1,toks[1].length()-1);
						termId2str.put(termId, termStr);
						i++;
					} catch (StringIndexOutOfBoundsException e) {
						e.printStackTrace();
						System.out.println("Split: " + split);
						System.exit(0);
					}
				}				
			}
			
			//types
			if (line.startsWith(typePrefix)) {
				line = line.substring(typePrefix.length());
				String[] splits = line.split("\\),\\(");
				int i =0;
				for (String split : splits) {					
					if (i == 0)
						split = split.substring(1);
					if (i == splits.length-1) 
						split = split.substring(0,split.length()-2);
					
					String[] toks = split.split(",");
					int typeId = Integer.parseInt(toks[0]);
					String typeStr = toks[1].substring(1,toks[1].length()-1).trim();
					double typeRank = Double.parseDouble(toks[3]);
					//temp
					//System.out.println("typeRank = " + typeRank);
					typeId2StrAndDefRank.put(typeId, typeStr + RedisRuleData.DELIMITER + typeRank);
					i++;
				}				
			}	
			
			//classifiers
			if (line.startsWith(classifierPrefix)) {
				line = line.substring(classifierPrefix.length());
				String[] splits = line.split("\\),\\(");
								
				int i =0;
				for (String split : splits) {
					
					try {
						if (i == 0)
							split = split.substring(1);
						if (i == splits.length-1) 
							split = split.substring(0,split.length()-2);
						
						String[] toks = split.split(",");
						int classifierId = Integer.parseInt(toks[0]);
						String classifierStr = toks[1].substring(1,toks[1].length()-1).trim();
						rJedis.set(classifierStr, Integer.toString(classifierId));
						lJedis.set(classifierStr, Integer.toString(classifierId));
						i++;
					} catch (StringIndexOutOfBoundsException e) {
						e.printStackTrace();
						System.out.println("Split: " + split);
						System.exit(0);
					}
				}				
			}

			//ranks
			if (line.startsWith(rankPrefix)) {
				line = line.substring(rankPrefix.length());
				String[] splits = line.split("\\),\\(");
				int i =0;
				for (String split : splits) {					
					if (i == 0)
						split = split.substring(1);
					if (i == splits.length-1) 
						split = split.substring(0,split.length()-2);
					
					String[] toks = split.split(",");
					int ruleId = Integer.parseInt(toks[0]);
					int cId = Integer.parseInt(toks[1]);
					double classifierRank = Double.parseDouble(toks[2]);
					TDoubleArrayList classifiersRank = ruleId2ClassifierRank.get(ruleId);
					if (classifiersRank == null) {
						classifiersRank = new TDoubleArrayList(numOfClassifiers);
						for (int j=0;j<numOfClassifiers;j++)
							classifiersRank.add(-1);
						ruleId2ClassifierRank.put(ruleId, classifiersRank);
					}
					//Assumption: the classifiers are identified by 1...numOfClassifiers
					classifiersRank.set(cId-1, classifierRank);
					i++;
				}				
			}			
			
			
			//patterns
			if (line.startsWith(patternPrefix)) {
				line = line.substring(patternPrefix.length());
				String[] splits = line.split("\\),\\(");
				int i =0;
				for (String split : splits) {					
					if (i == 0)
						split = split.substring(1);
					if (i == splits.length-1) 
						split = split.substring(0,split.length()-2);
					
					String[] toks = split.split(",");
					int ruleId = Integer.parseInt(toks[0]);
					String POSPattern = (toks[1].length() < 3 ? "" : toks[1].substring(1,toks[1].length()-1).trim());
					if (POSPattern.contains(RedisRuleData.DELIMITER))
						throw new Exception("Rule id: " + ruleId + ": POSPattern contains delimiter - " + POSPattern);
					String wordsPattern = (toks[2].length() < 3 ? "" : toks[2].substring(1,toks[2].length()-1).trim());
					if (wordsPattern.contains(RedisRuleData.DELIMITER))
						throw new Exception("Rule id: " + ruleId + ": wordsPattern contains delimiter - " + wordsPattern);
					String relationsPattern = (toks[3].length() < 3 ? "" : toks[3].substring(1,toks[3].length()-1).trim());
					if (relationsPattern.contains(RedisRuleData.DELIMITER))
						throw new Exception("Rule id: " + ruleId + ": relationsPattern contains delimiter - " + relationsPattern);					
					String POSrelationsPattern = (toks[4].length() < 3 ? "" : toks[4].substring(1,toks[4].length()-1).trim());
					if (POSrelationsPattern.contains(RedisRuleData.DELIMITER))
						throw new Exception("Rule id: " + ruleId + ": POSrelationsPattern contains delimiter - " + POSrelationsPattern);										
					String fullPattern = (toks[5].length() < 3 ? "" : toks[5].substring(1,toks[5].length()-1).trim());
					if (fullPattern.contains(RedisRuleData.DELIMITER))
						throw new Exception("Rule id: " + ruleId + ": fullPattern contains delimiter - " + fullPattern);	
					ruleId2Patterns.put(ruleId, POSPattern + RedisRuleData.DELIMITER + wordsPattern + RedisRuleData.DELIMITER + relationsPattern + RedisRuleData.DELIMITER + POSrelationsPattern + RedisRuleData.DELIMITER + fullPattern);
					i++;
				}				
			}
			
			
		}		
		reader.close();
		
		System.out.println(termId2str.size() + " terms were found");
		System.out.println(typeId2StrAndDefRank.size() + " rule types were found");
		System.out.println(ruleId2ClassifierRank.size() + " rule ranks were found");
		System.out.println(ruleId2Patterns.size() + " rule patterns were found");
		
		
		//rules
		reader = new BufferedReader(new FileReader(new File(args[0])));
		int pushedRules=0,missedRules=0;
		
		//debug
		int iLine = 0;
		int totalSplits=0;
		
		while ((line=reader.readLine())!=null) {
			line=line.trim();
			if (line.startsWith(rulesPrefix)) {
				line = line.substring(rulesPrefix.length());
				//String[] splits = line.split("\\),\\(");
				
				//debug
				iLine++;

				int pos1=1,pos2=-1;
				boolean bStop = false;
				while (!bStop) {
					try {
						pos2 = line.indexOf(',',pos1);
						int ruleId = Integer.parseInt(line.substring(pos1,pos2));
						pos1=pos2+1;
						
						pos2 = line.indexOf(',',pos1);
						int leftTermId = Integer.parseInt(line.substring(pos1,pos2));
						pos1=pos2+1;
						
						pos2 = line.indexOf(',',pos1);
						int rightTermId = Integer.parseInt(line.substring(pos1,pos2));
						pos1=pos2+1;
						
						pos2 = line.indexOf(',',pos1);
						//data skiped
						pos1=pos2+1;
						
						pos2 = line.indexOf(',',pos1);
						int ruleTypeId = Integer.parseInt(line.substring(pos1,pos2));
						pos1=pos2+1;
						
						//@tmp
						pos2 = line.indexOf("',",pos1);
						if (pos2 == -1) {							
							System.out.println(line.substring(pos1));
							System.exit(0);
						}
						boolean bBonus = line.substring(pos1,pos2).contains("m_isNPphrase=true");
						pos1=pos2+2;
						
						pos2 = line.indexOf(',',pos1);
						pos1=pos2+1;
						pos2 = line.indexOf("),(",pos1);
						if (pos2 == -1)
							bStop = true;
						else
							pos1=pos2+3;
						
						String leftTerm = termId2str.get(leftTermId);
						if (leftTerm == null) {
							System.out.println("Unmapped term id: " + leftTermId);
							continue;
						}
						if (leftTerm.contains(RedisRuleData.DELIMITER))
							throw new Exception("leftTerm contains delimiter: " + leftTerm);
						
						String rightTerm = termId2str.get(rightTermId);
						if (rightTerm == null) {
							System.out.println("Unmapped term id: " + rightTermId);
							continue;
						}
						if (rightTerm.contains(RedisRuleData.DELIMITER))
							throw new Exception("leftTerm contains delimiter: " + rightTerm);
						
						String ruleTypeAndDefRank = typeId2StrAndDefRank.get(ruleTypeId);
						if (ruleTypeAndDefRank == null) {
							System.out.println("Unmapped rule type id: " + ruleTypeId);// + " (parsed from: " + split + ")");
							continue;
						}
						
						TDoubleArrayList ruleClassifierRanks = ruleId2ClassifierRank.get(ruleId);	
						String rulePatterns = ruleId2Patterns.get(ruleId);						
						String[] patterns = (rulePatterns == null ? null : rulePatterns.split("\\" + RedisRuleData.DELIMITER));
						String[] typeAndDefRank = ruleTypeAndDefRank.split("\\" + RedisRuleData.DELIMITER); 
						
						RedisRuleData ruleData = null;
						//tmp
						try {
						ruleData = new RedisRuleData(leftTerm, rightTerm, patterns == null ? null : patterns[0], patterns == null || patterns.length < 2 ? null : patterns[1],patterns == null  || patterns.length < 3 ? null : patterns[2], patterns == null  || patterns.length < 4 ? null : patterns[3], patterns == null  || patterns.length < 5 ? null : patterns[4],
								Double.parseDouble(typeAndDefRank[1]),ruleClassifierRanks,typeAndDefRank[0],bBonus);
						} catch (Exception e) {
							//System.out.println("split = " + split);
							System.out.println("rulePatterns = " + rulePatterns);
							System.out.println("patterns = " + patterns);
							System.out.println("ruleTypeId = " + ruleTypeId);
							System.out.println("ruleTypeAndDefRank = " + ruleTypeAndDefRank);
							System.out.println("typeAndDefRank[0] = " + typeAndDefRank[0] + ", typeAndDefRank[1] " + typeAndDefRank[1]);
							e.printStackTrace();
							System.exit(0);
						}
						
						lJedis.rpush(leftTerm, ruleData.toValue());
						
						RedisRuleData tmpData = null;
						//tmp
						try {
						tmpData = new RedisRuleData(rightTerm,leftTerm, patterns == null ? null : patterns[0], patterns == null || patterns.length < 2 ? null : patterns[1],patterns == null  || patterns.length < 3 ? null : patterns[2], patterns == null  || patterns.length < 4 ? null : patterns[3], patterns == null  || patterns.length < 5 ? null : patterns[4],
								Double.parseDouble(typeAndDefRank[1]),ruleClassifierRanks,typeAndDefRank[0],bBonus);
						} catch (Exception e) {
							//System.out.println("split = " + split);
							System.out.println("rulePatterns = " + rulePatterns); 
							System.out.println("patterns = " + patterns);
							System.out.println("ruleTypeId = " + ruleTypeId);
							System.out.println("ruleTypeAndDefRank = " + ruleTypeAndDefRank);
							System.out.println("typeAndDefRank[0] = " + typeAndDefRank[0] + ", typeAndDefRank[1] " + typeAndDefRank[1]);
							e.printStackTrace();
							System.exit(0);
						}

						rJedis.rpush(rightTerm, tmpData.toValue());
						pushedRules++;
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Line: \n" + line);
						missedRules++;
						//continue;
						System.exit(0);
					}
				}
			}
		}		
		reader.close();
		
		System.out.println(iLine + " lines were processed");
		System.out.println(totalSplits + " line splits were processed");
		System.out.println(pushedRules + " rules were added to Redis");
		System.out.println(missedRules + " rules were missed");

	}
}

