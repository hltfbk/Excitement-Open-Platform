package eu.excitementproject.eop.distsim.application;

import java.io.File;
import java.io.Serializable;
import eu.excitementproject.eop.distsim.storage.IdTroveBasicIntDoubleMapFile;
import eu.excitementproject.eop.distsim.storage.TroveBasedIDKeyBasicMap;
import eu.excitementproject.eop.distsim.util.Pair;


public class CountRules {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		
		/*
		 * Usage: java eu.excitementproject.eop.distsim.application.CountRules <in id-based similarity file>
		 * 
		 */
		
		
		if (args.length != 1) {
			System.out.println("Usage: java eu.excitementproject.eop.distsim.application.CountRules <in id-based similarity file>");
			System.exit(0);
		}
		long i=0;
		File similarityFile = new File(args[0]);
		eu.excitementproject.eop.distsim.storage.File similarities= new IdTroveBasicIntDoubleMapFile(similarityFile, true);
		similarities.open();
		Pair<Integer, Serializable> pair = null;
		while ((pair = similarities.read()) != null) 
			i += ((TroveBasedIDKeyBasicMap<Double>)pair.getSecond()).size();
		similarities.close();
		
		System.out.println("Number of rules: " + i);
	}


}
