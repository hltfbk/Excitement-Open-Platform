package eu.excitementproject.eop.core.component.distance;

import java.io.File;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.common.component.distance.DistanceComponentException;
import eu.excitementproject.eop.common.component.distance.DistanceValue;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerEN;

public class BagOfWordsSimilarityTest {
	@Test
	public void test() {
		
		BagOfWordsSimilarity bs = new BagOfWordsSimilarity();
		
		JCas cas = null;
		OpenNLPTaggerEN lap = null;
		
		File inputFile = new File("./src/test/resources/small.xml"); // this only holds the first 3 of them.. generate 3 XMIs (first 3 of t.xml) 
//		File inputFile = new File("./src/test/resources/t.xml");  // this is full, and will generate 800 XMIs (serialized CASes)
//		File inputFile = new File("./src/test/resources/English_dev.xml");  // this is full, and will generate 800 XMIs (serialized CASes)
//		File inputFile = new File("./src/test/resources/German_dev.xml");  // this is full, and will generate 800 XMIs (serialized CASes)
		File outputDir = new File("./target/EN/");
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
//		File outputDir = new File("./target/DE/"); 

		
        try 
        {
        	// LAP
        	lap = new OpenNLPTaggerEN();
//        	lap = new OpenNLPTaggerDE();
			lap.processRawInputFormat(inputFile, outputDir);
        	
        	// test BoW similarity module
			for (File xmi : outputDir.listFiles()) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				cas = PlatformCASProber.probeXmi(xmi, System.out);
				DistanceValue bowsim = bs.calculation(cas);
	    		System.out.println(bowsim.getDistance());
	    		System.out.println(bowsim.getUnnormalizedValue());
	    		//System.out.println(bowsim.getDistanceVector());
	    		System.out.println(bs.calculateScores(cas)); 
	    		
				cas.reset(); 
			}
        }
        catch(LAPException e)
        {
        	System.err.println(e.getMessage()); 
        }
        catch (DistanceComponentException e) {
        	System.err.println(e.getMessage()); 
        }
        catch (ScoringComponentException e) {
        	System.err.println(e.getMessage()); 
        }
    }
}
