package eu.excitementproject.eop.core.alignment;

import java.io.File;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.biu.uima.BIUFullLAP;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * Test class to {@link eu.excitementproject.eop.core.alignment.LexicalAligner}.
 * @author Vered Shwartz
 *
 */
public class LexicalAlignerTest {

	static Logger logger = Logger.getLogger(LexicalAligner.class.getName());
	
	@Test
	public void test() {
		
		try {
			
			// Create a sentence pair example and annotate with tokens			
			LAPAccess lap = new BIUFullLAP(
					"src/test/resources/model/left3words-wsj-0-18.tagger",
					"src/test/resources/model/ner-eng-ie.crf-3-all2008-distsim.ser.gz",
					"localhost",
					8080);
			
			JCas aJCas = lap.generateSingleTHPairCAS("The assassin was convicted and sentenced to death penalty", 
					"The killer has been accused of murder and doomed to capital punishment");
			
//			JCas aJCas = lap.generateSingleTHPairCAS("Kennedy was killed in Dallas", 
//					"Kennedy was wounded and died in Texas");
			
			// Create and initialize the aligner
			LexicalAligner aligner = new LexicalAligner();
			File configFile = new File("src/test/resources/configuration-file/LexicalAligner_EN.xml");
			ImplCommonConfig commonConfig = new ImplCommonConfig(configFile);
			aligner.init(commonConfig);
			
			// Call the aligner to align T and H
			aligner.annotate(aJCas);
			
			// Show the alignments of T and H
			JCas hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
			for (Link link : JCasUtil.select(hypoView, Link.class)) {
				
				System.out.println(String.format("Text start: %d, text end: %d, " +
									"hypothesis start: %d, hypothesis end: %d, " + 
									"resource: %s, confidence: %f", 
									link.getTSideTarget().getBegin(), 
									link.getTSideTarget().getEnd(), 
									link.getHSideTarget().getBegin(), 
									link.getHSideTarget().getEnd(), 
									link.getAlignerID(),
									link.getStrength()));
			}
			
			// Dispose the aligner
			aligner.cleanUp();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
