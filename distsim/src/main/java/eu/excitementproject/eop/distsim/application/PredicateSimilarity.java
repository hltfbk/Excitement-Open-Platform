package eu.excitementproject.eop.distsim.application;

import java.io.File;


import java.util.LinkedHashMap;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.DependencyPathsFromTreeBinary;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNodeConstructor;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.distsim.builders.cooccurrence.CollNodeSentenceReader;
import eu.excitementproject.eop.distsim.domains.FilterType;
import eu.excitementproject.eop.distsim.domains.RuleDirection;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.PredicateElement;
import eu.excitementproject.eop.distsim.scoring.ElementSimilarityMeasure;
import eu.excitementproject.eop.distsim.storage.DefaultSimilarityStorage;
import eu.excitementproject.eop.distsim.storage.RedisBasedCountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.RedisBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.storage.SimilarityStorage;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * A program which demonstrates how to access a given distributional similarity model via SimilarityStorage interface, 
 * stored in Redis dbs, given by the hosts and the ports of the element db, and the l2r and r-2l similarity dbs
 * 
 * @author Meni Adler
 * @since 11/04/2013
 *
 */
public class PredicateSimilarity {
	public static void main(String[] args) {
		
		if (args.length != 8) {
			System.out.println("Usage: eu.excitementproject.eop.distsim.application.TestLemmaPosSimilarity " +
					" <element redis host>  <element redis port>" +
					" <l2r similarity redis host> <l2r similarity redis port>" + 
			        " <r2l similarity redis host> <r2l similarity redis port>" +
					" parsed corpus file (conll format) " +
			        " part-of-speech class, e.g., eu.excitementproject.eop.common.representation.partofspeech.DKProPartOfSpeech");
			System.exit(0);
		}
		
		
		try {
			String elementRedisHost = args[0];
			int elementRedisPort = Integer.parseInt(args[1]);
			String l2rRedisHost = args[2];
			int l2rRedisPort = Integer.parseInt(args[3]);
			String r2lRedisHost = args[4];
			int r2lRredisPort = Integer.parseInt(args[5]);
			String corpus = args[6];
			String posClassName = args[7];
		
			DependencyPathsFromTreeBinary<Info, BasicNode> extractor =  new DependencyPathsFromTreeBinary<Info, BasicNode>(new BasicNodeConstructor(), true, true);
	
			SimilarityStorage similarityStorage = new DefaultSimilarityStorage(
					new RedisBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>>(l2rRedisHost,l2rRedisPort),
					new RedisBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>>(r2lRedisHost,r2lRredisPort),
					new RedisBasedCountableIdentifiableStorage<Element>(elementRedisHost,elementRedisPort),
					"dirt-dist-sim");
			
			
			CollNodeSentenceReader sentenceReader = new CollNodeSentenceReader((PartOfSpeech)Factory.create(posClassName,""));
			sentenceReader.setSource(new File(corpus));
			Pair<BasicNode,Long> sent = null;
			while((sent  = sentenceReader.nextSentence()) != null) {
				for (String dependencyPath : extractor.stringDependencyPaths(sent.getFirst())) {
					
					// find top10 similar RHS depenedncy-paths for the given LHS dependency-path 
					List<ElementSimilarityMeasure> similarities = similarityStorage.getSimilarityMeasure(
							new PredicateElement(dependencyPath), 
							RuleDirection.LEFT_TO_RIGHT,
							FilterType.TOP_N, 10);
					
					// print similarities for the given dependency path
					System.out.println(dependencyPath);
					for (ElementSimilarityMeasure similarity : similarities)
						System.out.println("\t" + similarity.getElement().getData() + ": " + similarity.getSimilarityMeasure());
	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
