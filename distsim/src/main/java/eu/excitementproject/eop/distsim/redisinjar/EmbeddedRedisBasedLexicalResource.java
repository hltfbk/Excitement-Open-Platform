package eu.excitementproject.eop.distsim.redisinjar;

import java.io.File;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.google.common.io.Files;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.distsim.resource.SimilarityStorageBasedLexicalResource;
import eu.excitementproject.eop.distsim.storage.DefaultSimilarityStorage;
import eu.excitementproject.eop.distsim.storage.SimilarityStorage;
import eu.excitementproject.eop.redis.RedisBasedStringListBasicMap;

/**
 * This is a lexical-resource implementation that utilizes Redis-In-Jar RedisServerRunner 
 * code. Basically, the resource operates in two modes. 
 *
 * a) redis-server is running on external: the resource will get two server names and port 
 * and utilizes those. 
 * b) redis-server is not running yet, and the resource boot himself up: in this case, the 
 * implementation gets redis-model file resourcePath (classpath); and the resource gets the 
 * model (rdb file) from Jar, run redis server from redis-server Jar (by using RedisServerRunner)
 * and start redis server locally (127.0.0.1), and uses that server.  
 * 
 * NOTE: this resource requires close() call, always. 
 * 
 * 
 * TODO: port checking --- if port is open, don't even start, etc.  
 * 
 * @author Tae-Gil Noh
 *
 */
public class EmbeddedRedisBasedLexicalResource implements LexicalResource<RuleInfo> {
	
	/**
	 * 
	 * This constructor is for those cases where the redis server is already up and 
	 * running somewhere. The resource simply taps to that server/port and offer 
	 * LexicalResource. 
	 * 
	 * @param l2rServer Name (or IP) of the server to connect, L->R sim redis server  
	 * @param l2rPort port number of the server to connect, L->R sim redis server 
	 * @param r2lServer Name (or IP) of the server to connect, R->L sim redis server 
	 * @param r2lPort port number of the server to connect, R->L sim redis server 
	 * @param maxNumOfRetrievedRules Maximum number of rules that will be returned. They are ordered. If null, return all. 
	 */
	public EmbeddedRedisBasedLexicalResource(String l2rServer, int l2rPort, String r2lServer, int r2lPort, int maxNumOfRetrievedRules)
	{
		// set logger 
        logger = Logger.getLogger(this.getClass().getName()); 
		
		RedisBasedStringListBasicMap l2rMap = new RedisBasedStringListBasicMap(l2rServer, l2rPort);
		RedisBasedStringListBasicMap r2lMap = new RedisBasedStringListBasicMap(r2lServer, r2lPort); 
		SimilarityStorage theStorage = new DefaultSimilarityStorage(l2rMap, r2lMap, "CompName", "InstName", "eu.excitementproject.eop.distsim.items.LemmaPosBasedElement");
		// note that Component Name and Instance Name will be overridden by 
		// the LexicalResource that extends this abstract class. so we don't care about them and 
		// set simply as "CompName" and "InstName". Those won't be visible to end users. 

		// actual worker 
		theLexicalResource = new SimilarityStorageBasedLexicalResource(theStorage, maxNumOfRetrievedRules);
	}

	/**
	 * 
	 * This constructor is for the cases where there is no running redis server for the 
	 * requested file (resource). 
	 * 
	 * The method gets two URL of the RDB files in the JAR (e.g. Classloader.getResource(classPathOfTheFileInJar)). 
	 * And it runs Redis-Server, by running binaries provided by embedded-redis artifact. 
	 * It runs the server on the local host, but it requires the user to provide the port number.  
	 *  
	 * Note: Extracting and Loading Redis models on redis-server takes some time; depending on the 
	 * redis RDB file size. 
	 * 
	 * @param l2rResource URL of the resource (e.g. Classloader.getResource()) that holds redis rdb file in Jar, L->R similarity   
	 * @param r2lResource URL of the resource (e.g. Classloader.getResource()) that holds redis rdb file in Jar, R->L similarity
	 * @param l2rPort port number in string; to be opened on local host for redis-server L->R sim 
	 * @param r2lPort port number in string; to be opened on local host for redis-server R->L sim 
	 * @param maxNumOfRetrievedRules Maximum number of rules that will be returned. They are ordered. If null, return all.
	 */
	public EmbeddedRedisBasedLexicalResource(URL l2rResource, URL r2lResource, int l2rPort, int r2lPort, int maxNumOfRetrievedRules) throws LexicalResourceException
	{

		// set logger 
        logger = Logger.getLogger(this.getClass().getName()); 

		try {
			// URL actually exist? 
			if (l2rResource == null || r2lResource == null)
			{
				throw new LexicalResourceException("Distsim rdb files (as URL) are not found on the designated path. (Probably missing the artifact? or Jar?) Cannot proceed further\n"); 
			}
			
			logger.info("extracting l2r redis db file");
			File l2rRdb = extractDataFileFromResource(l2rResource); 
			logger.info("extracted in: " + l2rRdb.getParent() + "//" + l2rRdb.getName()); 
			
			logger.info("extracting r2l redis db file");
			File r2lRdb = extractDataFileFromResource(r2lResource); 
			logger.info("extracted in: " + r2lRdb.getParent() + "//" + r2lRdb.getName()); 
			
			rs_l = new EmbeddedRedisServerRunner(l2rPort, l2rRdb.getParent(), l2rRdb.getName()); 
			rs_r = new EmbeddedRedisServerRunner(r2lPort, r2lRdb.getParent(), r2lRdb.getName()); 
			
			// run the redis server ... 
			rs_l.start();
			//Thread.sleep(1000); // do we need this? no. 
			rs_r.start(); 
			//Thread.sleep(5000); 
		} catch (IOException e)
		{
			throw new LexicalResourceException("Failed to run Redis-Embedded Server!" + e.getMessage()); 
		}
		
		if ( (rs_l == null) || (rs_r == null) )
		{
			throw new LexicalResourceException("Failed to run Redis-Embedded Server! - RedisServerRunner returned null (integrity failure, you shouldn't see this exception)"); 			
		}
		
		// Okay. now redis servers are up and running on local. 

		RedisBasedStringListBasicMap l2rMap = new RedisBasedStringListBasicMap("127.0.0.1", l2rPort);
		RedisBasedStringListBasicMap r2lMap = new RedisBasedStringListBasicMap("127.0.0.1", r2lPort); 
		SimilarityStorage theStorage = new DefaultSimilarityStorage(l2rMap, r2lMap, "CompName", "InstName", "eu.excitementproject.eop.distsim.items.LemmaPosBasedElement");
		// note that Component Name and Instance Name will be overridden by 
		// the LexicalResource that extends this abstract class. so we don't care about them and 
		// set simply as "CompName" and "InstName". Those won't be visible to end users. 

		// actual worker 
		theLexicalResource = new SimilarityStorageBasedLexicalResource(theStorage, maxNumOfRetrievedRules);
	}
	
	@Override
	public List<LexicalRule<? extends RuleInfo>> getRulesForRight(String lemma,
			PartOfSpeech pos) throws LexicalResourceException {
		
		return this.theLexicalResource.getRulesForRight(lemma,  pos); 		
		
	}

	@Override
	public List<LexicalRule<? extends RuleInfo>> getRulesForLeft(String lemma,
			PartOfSpeech pos) throws LexicalResourceException {
		
		return this.theLexicalResource.getRulesForLeft(lemma,  pos); 
		
	}

	@Override
	public List<LexicalRule<? extends RuleInfo>> getRules(String leftLemma,
			PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos)
			throws LexicalResourceException {
		
		return this.theLexicalResource.getRules(leftLemma, leftPos, rightLemma, rightPos); 
		
	}

	@Override
	public void close() throws LexicalResourceCloseException {
		// shutdown the redis server, if it has any. 
		
		if (rs_l!=null)
		{
			rs_l.stop(); 
		}
		if (rs_r!=null)
		{
			rs_r.stop(); 
		}
	}
	
	//
	// private data 
	
	private SimilarityStorageBasedLexicalResource theLexicalResource;
	private Logger logger; 
	private EmbeddedRedisServerRunner rs_l = null; 
	private EmbeddedRedisServerRunner rs_r = null; 


	//
	// private methods 
	private static File extractDataFileFromResource(URL resource) throws IOException
	{
		File tmpDir = Files.createTempDir();
		tmpDir.deleteOnExit();
		
		File rdb = new File(tmpDir, resource.getFile());
		FileUtils.copyURLToFile(resource, rdb);
		rdb.deleteOnExit();
				
		return rdb; 
	}



}
