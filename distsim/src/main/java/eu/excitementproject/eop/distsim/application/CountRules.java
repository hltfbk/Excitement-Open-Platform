package eu.excitementproject.eop.distsim.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.IDBasedCooccurrence;
import eu.excitementproject.eop.distsim.items.InvalidCountException;
import eu.excitementproject.eop.distsim.items.InvalidIDException;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.redisinjar.EmbeddedRedisBasedLexicalResource;
import eu.excitementproject.eop.distsim.storage.BasicSet;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.IdTroveBasicIntDoubleMapFile;
import eu.excitementproject.eop.distsim.storage.ItemNotFoundException;
import eu.excitementproject.eop.distsim.storage.LoadingStateException;
import eu.excitementproject.eop.distsim.storage.MemoryBasedCountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.TroveBasedIDKeyBasicMap;
import eu.excitementproject.eop.distsim.storage.TroveBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.Serialization;
import eu.excitementproject.eop.distsim.util.SerializationException;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;


public class CountRules {
	
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
