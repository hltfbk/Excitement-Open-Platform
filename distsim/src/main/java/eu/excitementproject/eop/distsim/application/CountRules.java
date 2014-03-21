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
		long total=0, elements=0,max=0;
		File similarityFile = new File(args[0]);
		eu.excitementproject.eop.distsim.storage.File similarities= new IdTroveBasicIntDoubleMapFile(similarityFile, true);
		similarities.open();
		Pair<Integer, Serializable> pair = null;
		while ((pair = similarities.read()) != null) { 
			long size = ((TroveBasedIDKeyBasicMap<Double>)pair.getSecond()).size();
			elements++;
			total += size;
			if (max < size)
				max = size;
		}
		similarities.close();
		
		System.out.println("Total number of rules: " + total);
		System.out.println("Total number of elements: " + elements);
		System.out.println("Average number of rules per element: " + (((double)total) / ((double)elements)));
		System.out.println("Max number of rules per element: " + max);
	}


}
