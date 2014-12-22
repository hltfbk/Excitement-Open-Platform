package eu.excitementproject.eop.alignmentedas.p1eda.inspector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;

//import eu.excitementproject.eop.alignmentedas.p1eda.P1EDATemplate;
//import eu.excitementproject.eop.alignmentedas.p1eda.sandbox.WithVO;
//import eu.excitementproject.eop.alignmentedas.p1eda.sandbox.WithoutVO;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import static eu.excitementproject.eop.alignmentedas.p1eda.inspector.InspectUtilsJCasAndLinks.*; 

/**
 * This class holds some static methods that are useful, or needed to 
 * compare and visualize differences between two different (P1) EDAs
 * 
 * @author Tae-Gil Noh 
 *
 */
public class CompareTwoEDAs {

	// TODO: Two lists output? instead of one? a.) improved on EDA2.   b.) degraded on EDA2. 
	// TODO: startTraining(XML file?, modelToStore)? (on template side) 
	// TODO: include training... 
				
	/**
	 * Pass two initialized (ready to be call process()) EDAs, 
	 * and one dir Path to XMI files. 
	 * 
	 * The utility will print out to Logger; all pair IDs that the two given 
	 * EDAs did *not* agree. And the method will also return the result as a list of IDs. 
	 * 
	 * @param eda1 first EDA 
	 * @param eda2 second EDA 
	 * @param dirXMITestSet directory (File object) to where already a RTE test set has been properly processed into a set of XMI files.  
	 * @return List<String>, which that holds IDs (of that test set pairs) where the two EDAs disagreed on the decision.  
	 */
	static public List<String> getDiffPairs(EDABasic<? extends TEDecision> eda1, EDABasic<? extends TEDecision> eda2, File dirXMITestSet) throws Exception
	{
		// well. Run each of the XMI pairs on two EDAs, and keep the 
		// pairID, and results. 
		// oh, by the way, keep general accuracy too. ... 

		List<String> diffPairIDs = new ArrayList<String>(); 
		HashMap<String, String> diffPairs = new HashMap<String, String>(); 		// diffPairs.get("id") = "eda 1 result (confidence), eda2 result(confidence), gold result"  
		int countEda1Correct = 0; 
		int countEda2Correct = 0; 
		int countTotalPair = 0; 
				
		File[] files = dirXMITestSet.listFiles(); 
		
		// for each XMI ... 
		for(File f : files)
		{
			// sanity check first 
			logger.info("Working with file " + f.getName()); 
			if(!f.isFile()) 
			{	// no ... 
				logger.warn(f.toString() + " is not a file... ignore this"); 
				continue; 
			}
			if(!f.getName().toLowerCase().endsWith("xmi")) // let's trust name, if it does not end with XMI, pass
			{
				logger.warn(f.toString() + " is not a XMI file... ignoring this"); 
				continue; 
			}
			
			// load XMI to two CASes
			// (Note that we can't share CASes between the two EDAs. Alignments are being added, 
			// and a CAs is updated with run of alignment based EDA process() call. 
			
			JCas pairForEDA1 = null; 
			JCas pairForEDA2 = null; 
			try {
				 pairForEDA1 = PlatformCASProber.probeXmi(f, null);
				 pairForEDA2 = PlatformCASProber.probeXmi(f, null);
			}
			catch (LAPException le)
			{
				logger.warn("File " + f.toString() + " looks like XMI file, but its contents are *not* proper EOP EDA JCas"); 
				throw new EDAException("failed to read XMI file into a JCas", le); 
			}

			// get pair ID and gold annotation 
			String pairId = InspectUtilsJCasAndLinks.getTEPairID(pairForEDA1); 
			logger.info("comparing two edas on pair " + pairId); 
			DecisionLabel gold = getGoldLabel(pairForEDA1); 

			// get the result from the  two edas 
			TEDecision eda1s = eda1.process(pairForEDA1); 
			TEDecision eda2s = eda2.process(pairForEDA2); 
						
			// update counters 
			countTotalPair ++; 
			if (eda1s.getDecision() == gold)
			{
				countEda1Correct++; 
			}
			
			if (eda2s.getDecision() == gold)
			{
				countEda2Correct++; 
			}
			
			// update diff list 
			if (! (eda1s.getDecision() == eda2s.getDecision()))
			{
				logger.debug("different results on pair " + pairId + ": " + eda1s.getDecision().toString() + ", " + eda2s.getDecision().toString()); 
				diffPairs.put(pairId, eda1s.getDecision().toString() + ", " + eda2s.getDecision().toString() + " (gold: " + gold.toString() + ")"); 
				diffPairIDs.add(pairId); 
			}			
		}
		
		logger.info("In total, " + countTotalPair + " pairs tested"); 
		logger.info("eda1: " + countEda1Correct + " / " + countTotalPair); 
		logger.info("eda2: " + countEda2Correct + " / " + countTotalPair); 
		logger.info("Diff list is:"); 
		
		for (String s : diffPairs.keySet())
		{
			logger.info(s + ": " + diffPairs.get(s)); 
		}
		
		return diffPairIDs; 
	}

	//
	// privates 
	// 
	
	// logger 
	private static Logger logger = Logger.getLogger(CompareTwoEDAs.class); 	

}
