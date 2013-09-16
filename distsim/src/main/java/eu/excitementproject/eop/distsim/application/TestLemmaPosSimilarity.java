package eu.excitementproject.eop.distsim.application;

import java.util.List;

import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.distsim.domains.FilterType;
import eu.excitementproject.eop.distsim.domains.RuleDirection;
import eu.excitementproject.eop.distsim.items.LemmaPos;
import eu.excitementproject.eop.distsim.items.LemmaPosBasedElement;
import eu.excitementproject.eop.distsim.scoring.ElementsSimilarityMeasure;
import eu.excitementproject.eop.distsim.storage.DefaultSimilarityStorage;
import eu.excitementproject.eop.distsim.storage.RedisBasedStringListBasicMap;
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
public class TestLemmaPosSimilarity {
	public static void main(String[] args) throws SimilarityNotFoundException {
		
		if (args.length != 4) {
			System.out.println("Usage: eu.excitementproject.eop.distsim.application.TestLemmaPosSimilarity " +
					" <l2r similarity redis host> <l2r similarity redis port>" + 
			        " <r2l similarity redis host> <r2l similarity redis port>");
			System.exit(0);
		}
		
		String l2rRedisHost = args[0];
		int l2rRedisPort = Integer.parseInt(args[1]);
		String r2lRedisHost = args[2];
		int r2lRredisPort = Integer.parseInt(args[3]);
		
		SimilarityStorage similarityStorage = new DefaultSimilarityStorage(
				new RedisBasedStringListBasicMap(l2rRedisHost,l2rRedisPort),
				new RedisBasedStringListBasicMap(r2lRedisHost,r2lRredisPort),
				"lin-dist-sim", null, "eu.excitementproject.eop.distsim.items.LemmaPosBasedElement");
		
		
		List<ElementsSimilarityMeasure> similarities = similarityStorage.getSimilarityMeasure(
				new LemmaPosBasedElement(new LemmaPos("affect", CanonicalPosTag.V)), 
				RuleDirection.RIGHT_TO_LEFT,
				FilterType.TOP_N, 10);
		
		for (ElementsSimilarityMeasure similarity : similarities)
			System.out.println(similarity.getRightElement().getData() + "\t" + similarity.getRightElement().getData() + ": " + similarity.getSimilarityMeasure());
	}
}
