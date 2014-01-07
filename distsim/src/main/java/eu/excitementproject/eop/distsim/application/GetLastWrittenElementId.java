package eu.excitementproject.eop.distsim.application;

import java.io.File;
import java.io.Serializable;

import eu.excitementproject.eop.distsim.storage.IdTroveBasicIntDoubleMapFile;
import eu.excitementproject.eop.distsim.util.Pair;


public class GetLastWrittenElementId {
	
	public static void main(String[] args) throws Exception {
		
		/*
		 * Usage: java eu.excitementproject.eop.distsim.application.GetLastWrittenElementId <in similarity file> 
		 * 
		 */
		
		if (args.length != 1) {
			System.out.println("Usage: java eu.excitementproject.eop.distsim.application.GetLastWrittenElementId <in similarity file>");
			System.exit(0);
		}
		File similarityFile = new File(args[0]);
		eu.excitementproject.eop.distsim.storage.File similarities= new IdTroveBasicIntDoubleMapFile(similarityFile, true);
		similarities.open();
		Pair<Integer, Serializable> pair = null;
		int elementId = 0;
		while ((pair = similarities.read()) != null) {
			elementId = pair.getFirst();			
			System.out.println(elementId);
		}
		System.out.println("Last written element id: " + elementId);
		similarities.close();
	}


}
