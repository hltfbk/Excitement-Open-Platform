/**
 * 
 */
package eu.excitementproject.eop.core.component.syntacticknowledge;

import java.io.File;


import java.io.FileNotFoundException;

import java.util.List;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.syntacticknowledge.RuleMatch;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResource;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResourceException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.builders.reader.FileBasedSentenceReader;
import eu.excitementproject.eop.distsim.builders.reader.SentenceReaderException;
import eu.excitementproject.eop.distsim.builders.reader.XMLNodeSentenceReader;
import eu.excitementproject.eop.distsim.storage.ElementTypeException;
import eu.excitementproject.eop.distsim.storage.SimilarityNotFoundException;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.FileUtils;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.redis.RedisRunException;

/**
 * A program which demonstrates how to access a given DIRT distributional similarity model via {@link SyntacticInterface}, 
 * stored in Redis dbs, given by the hosts and the ports of the l2r and r-2l similarity dbs
 * 
 * @author Meni Adler
 * @since 11/9/2013
 *
 */
public class TestDIRTSimilarity {
	
	public static void main(String[] args) throws SimilarityNotFoundException, LexicalResourceException, UnsupportedPosTagStringException, SyntacticResourceException, SentenceReaderException, ElementTypeException, FileNotFoundException, RedisRunException, eu.excitementproject.eop.common.exception.ConfigurationException {

		if (args.length != 2) {
			System.out.println("Usage: eu.excitementproject.eop.distsim.application.TestLemmaPosSimilarity " +
					" <knowledge resource configuration file>" + 
					" <parsed corpus file/dir (xml representation)> ");
			System.exit(0);
		}
		
		//ConfigurationFile confFile = new ConfigurationFile(args[0]);
		ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(args[0])));
		ConfigurationParams confParams = confFile.getModuleConfiguration(Configuration.KNOWLEDGE_RESOURCE);
		
		SyntacticResource<Info, BasicNode> resource = new SimilarityStorageBasedDIRTSyntacticResource(confParams);
		
		for (File f : FileUtils.getFiles(new File(args[1]))) {
			FileBasedSentenceReader<BasicNode> reader = new XMLNodeSentenceReader();
			reader.setSource(f);
			Pair<BasicNode, Long> tree;
			while ((tree = reader.nextSentence())!= null) {
				
				try {
					List<RuleMatch<Info, BasicNode>> matches = resource.findMatches(tree.getFirst());
					
					//debug
					if (matches.size() > 0 ) {
						System.out.println(matches.size() + " matches were found for tree: " + tree.getFirst().getInfo());					
						for (RuleMatch<Info, BasicNode> match : matches)
							System.out.println(match.getRule().getRule().getLeftHandSide().getInfo().toString() + "\t" + match.getRule().getRule().getRightHandSide().getInfo().toString());
						System.out.println();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
