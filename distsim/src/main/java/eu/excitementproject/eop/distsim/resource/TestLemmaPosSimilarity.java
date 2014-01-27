package eu.excitementproject.eop.distsim.resource;

import java.io.File;

import java.io.FileNotFoundException;
import java.util.List;


import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.redis.RedisRunException;
import eu.excitementproject.eop.distsim.storage.ElementTypeException;
import eu.excitementproject.eop.distsim.storage.SimilarityNotFoundException;
import eu.excitementproject.eop.distsim.util.Configuration;

/**
 * A program which demonstrates how to access a given distributional similarity model via Lexical interface, 
 * stored in Redis dbs, given by the l2r and r-2l similarity dbs
 * 
 * 
 * @author Meni Adler
 * @since 07/01/2013
 *
 */
public class TestLemmaPosSimilarity {
	
	public static void main(String[] args) throws SimilarityNotFoundException, LexicalResourceException, UnsupportedPosTagStringException, ElementTypeException, FileNotFoundException, RedisRunException, eu.excitementproject.eop.common.exception.ConfigurationException {
		
		//Assumption: the running directory contains a subdirectory 'redis' with two file: redis-rever and redis.cof
		if (args.length != 1) {
			System.out.println("Usage: eu.excitementproject.eop.distsim.application.TestLemmaPosSimilarity <configuration file>");
			System.exit(0);
		}
		
		//ConfigurationFile confFile = new ConfigurationFile(args[0]);
		ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(args[0])));
		
		ConfigurationParams confParams = confFile.getModuleConfiguration(Configuration.KNOWLEDGE_RESOURCE);

		LexicalResource<? extends RuleInfo> resource = new SimilarityStorageBasedLexicalResource(confParams);
		
		List<? extends LexicalRule<? extends RuleInfo>> similarities = resource.getRulesForLeft("affect",null);
		System.out.println("left-2-right rules for affect: ");
		for (LexicalRule<? extends RuleInfo> similarity : similarities)
			System.out.println("<" + similarity.getLLemma() + "," + similarity.getLPos() + ">" + " --> " + "<" + similarity.getRLemma() + "," + similarity.getRPos() + ">" + ": " + similarity.getConfidence());
		
		similarities = resource.getRulesForRight("affect",null);
		System.out.println("\nright-2-left rules for affect: ");
		for (LexicalRule<? extends RuleInfo> similarity : similarities)
			System.out.println("<" + similarity.getLLemma() + "," + similarity.getLPos() + ">" + " --> " + "<" + similarity.getRLemma() + "," + similarity.getRPos() + ">" + ": " + similarity.getConfidence());

	
		similarities = resource.getRulesForLeft("affect",new ByCanonicalPartOfSpeech(CanonicalPosTag.V.name()));
		System.out.println("\nleft-2-right rules for affect as a verb: ");
		for (LexicalRule<? extends RuleInfo> similarity : similarities)
			System.out.println("<" + similarity.getLLemma() + "," + similarity.getLPos() + ">" + " --> " + "<" + similarity.getRLemma() + "," + similarity.getRPos() + ">" + ": " + similarity.getConfidence());

		similarities = resource.getRulesForRight("affect",new ByCanonicalPartOfSpeech(CanonicalPosTag.V.name()));
		System.out.println("\nright-2-left rules for affect as a verb: ");
		for (LexicalRule<? extends RuleInfo> similarity : similarities)
			System.out.println("<" + similarity.getLLemma() + "," + similarity.getLPos() + ">" + " --> " + "<" + similarity.getRLemma() + "," + similarity.getRPos() + ">" + ": " + similarity.getConfidence());

	}
}
