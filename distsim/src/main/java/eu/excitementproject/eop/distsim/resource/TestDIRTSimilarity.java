package eu.excitementproject.eop.distsim.resource;

import java.io.File;



import java.io.FileNotFoundException;


import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.domains.RuleDirection;
import eu.excitementproject.eop.distsim.items.StringBasedElement;
import eu.excitementproject.eop.distsim.scoring.ElementsSimilarityMeasure;
import eu.excitementproject.eop.distsim.storage.DefaultSimilarityStorage;
import eu.excitementproject.eop.distsim.storage.ElementTypeException;
import eu.excitementproject.eop.distsim.storage.SimilarityNotFoundException;
import eu.excitementproject.eop.distsim.storage.SimilarityStorage;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.redis.RedisRunException;

/**
 * A program which demonstrates how to access a given dirt distributional similarity model via SimilarityStorage interface, 
 * stored in Redis dbs, given by the l2r and r-2l similarity dbs
 * 
 * 
 * @author Meni Adler
 * @since 8/5/2014
 *
 */
public class TestDIRTSimilarity {
	
	public static void main(String[] args) throws SimilarityNotFoundException, LexicalResourceException, UnsupportedPosTagStringException, ElementTypeException, FileNotFoundException, RedisRunException, eu.excitementproject.eop.common.exception.ConfigurationException {
		
		//Assumption: the running directory contains a subdirectory 'redis' with two files: redis-rever and redis.conf
		if (args.length != 1) {
			System.err.printf("Usage: %s <configuration file>\n", TestDIRTSimilarity.class.getName());
			System.exit(0);
		}
		
	    ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(args[0])));		
		ConfigurationParams confParams = confFile.getModuleConfiguration(Configuration.KNOWLEDGE_RESOURCE);
		SimilarityStorage similarityStorage = new DefaultSimilarityStorage(confParams);		
		
		StringBasedElement leftElement = new StringBasedElement("n<xsubj<v:buy:v>dobj>n");
		for (ElementsSimilarityMeasure similarity : similarityStorage.getSimilarityMeasure(leftElement, RuleDirection.LEFT_TO_RIGHT))
			System.out.println(similarity.getLeftElement() + " --> " + similarity.getRightElement() + " :  " + similarity.getSimilarityMeasure());
	}
}
