package eu.excitementproject.eop.alignmentedas.p1eda.tools;

import java.io.File;
import java.util.HashMap;

import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.TEDecision;

/**
 * This class holds some static methods that are useful, or needed to 
 * compare and visualize differences between two different (P1) EDAs
 * 
 * @author Tae-Gil Noh 
 *
 */
public class CompareTwoEDAs {


	/**
	 * Pass two initialized (ready to be call process()) EDAs, 
	 * and one dir Path to XMI files. 
	 * 
	 * The utility will print out to Logger; all pair IDs that the two given 
	 * EDAs did *not* agree. 
	 * 
	 * @param eda1
	 * @param eda2
	 * @param dirXMITestSet
	 */
	static public void logDiffPairs(EDABasic<? extends TEDecision> eda1, EDABasic<? extends TEDecision> eda2, File dirXMITestSet)
	{
		// well. Run each of the XMI pairs on two EDAs, and keep the 
		// pairID, and results. 
		// oh, by the way, keep general accuracy too. ... 

		HashMap<String, String> diffPairs = new HashMap<String, String>(); 		// diffPairs.get("id") = "eda 1 result (confidence), eda2 result(confidence), gold result"  
		int countEda1Correct = 0; 
		int countEda2Correct = 0; 
		int countTotalPair = 0; 
				
		// for each XMI ... 
		{
			TEDecision eda1s; 
			TEDecision eda2s; 
			// load XMI to CAS 
			
			// pass CAS to get eda1 result, record it ... 
			
			// pass CAS to get eda2 result, record it ...
			
			
		}
		
	}

}
