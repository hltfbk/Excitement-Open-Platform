package eu.excitementproject.eop.distsim.application;

import java.util.LinkedHashMap;
import java.util.List;

import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.distsim.domains.FilterType;
import eu.excitementproject.eop.distsim.domains.RuleDirection;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.LemmaPos;
import eu.excitementproject.eop.distsim.items.LemmaPosBasedElement;
import eu.excitementproject.eop.distsim.scoring.ElementSimilarityMeasure;
import eu.excitementproject.eop.distsim.storage.DefaultSimilarityStorage;
import eu.excitementproject.eop.distsim.storage.RedisBasedCountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.RedisBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.storage.SimilarityNotFoundException;
import eu.excitementproject.eop.distsim.storage.SimilarityStorage;

/**
 * A program which demonstrates how to access a given distributional similarity model via SimilarityStorage interface, 
 * stored in Redis dbs, given by the hosts and the ports of the element db, and the l2r and r-2l similarity dbs
 * 
 * @author Meni Adler
 * @since 07/01/2013
 *
 */
public class TestLemmaPosSimilarity1 {
	public static void main(String[] args) throws SimilarityNotFoundException {
		
		if (args.length != 6) {
			System.out.println("Usage: eu.excitementproject.eop.distsim.application.TestLemmaPosSimilarity " +
					" <element redis host>  <element redis port>" +
					" <l2r similarity redis host> <l2r similarity redis port>" + 
			        " <r2l similarity redis host> <r2l similarity redis port>");
			System.exit(0);
		}
		
		String elementRedisHost = args[0];
		int elementRedisPort = Integer.parseInt(args[1]);
		String l2rRedisHost = args[2];
		int l2rRedisPort = Integer.parseInt(args[3]);
		String r2lRedisHost = args[4];
		int r2lRredisPort = Integer.parseInt(args[5]);
		
		SimilarityStorage similarityStorage = new DefaultSimilarityStorage(
				new RedisBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>>(l2rRedisHost,l2rRedisPort),
				new RedisBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>>(r2lRedisHost,r2lRredisPort),
				new RedisBasedCountableIdentifiableStorage<Element>(elementRedisHost,elementRedisPort),
				"lin-dist-sim");
		
		
		List<ElementSimilarityMeasure> similarities = similarityStorage.getSimilarityMeasure(
				new LemmaPosBasedElement(new LemmaPos("affect", CanonicalPosTag.V)), 
				RuleDirection.RIGHT_TO_LEFT,
				FilterType.TOP_N, 10);
		
		System.out.println("affect");
		for (ElementSimilarityMeasure similarity : similarities)
			System.out.println("\t" + similarity.getElement().getData() + ": " + similarity.getSimilarityMeasure());
	}
}
