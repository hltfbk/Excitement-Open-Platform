package eu.excitementproject.eop.distsim.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import eu.excitementproject.eop.distsim.storage.BasicSet;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.ItemNotFoundException;
import eu.excitementproject.eop.distsim.storage.LoadingStateException;
import eu.excitementproject.eop.distsim.storage.MemoryBasedCountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.TroveBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.Serialization;
import eu.excitementproject.eop.distsim.util.SerializationException;


public class ResolveSerializedStorages {
	
	public static void listCooccurrences(File textunitFile,File cooccurrenceFile) throws IOException, SerializationException, InvalidCountException, InvalidIDException, LoadingStateException, ItemNotFoundException {

		eu.excitementproject.eop.distsim.storage.File device = new eu.excitementproject.eop.distsim.storage.File(textunitFile,true);
		device.open();
		CountableIdentifiableStorage<TextUnit> textUnitStorage = new MemoryBasedCountableIdentifiableStorage<TextUnit>(device);
		System.out.println("\n\nCooccurrences: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cooccurrenceFile),"UTF-8"));
		String line = null;
		while ((line=reader.readLine())!=null) {
		//for (int i =0; i<10;i++) {
			//line=reader.readLine();
			String[] toks = line.split("\t");
			@SuppressWarnings("rawtypes")
			IDBasedCooccurrence cooccurrence = (IDBasedCooccurrence)Serialization.deserialize(toks[1]);
			System.out.println(cooccurrence.getID() + ". " + textUnitStorage.getData(cooccurrence.getTextUnitID1()) + "-" +  cooccurrence.getRelation() + "-" + textUnitStorage.getData(cooccurrence.getTextUnitID2()) + ": " + cooccurrence.getCount());
		}
		reader.close();
	}

	
	public static void listElementCounts(File file) throws IOException, SerializationException, InvalidCountException, InvalidIDException {
		System.out.println("\n\nElement counts: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String line = null;
		while ((line=reader.readLine())!=null) {
		//for (int i =0; i<10;i++) {
			//line=reader.readLine();
			String[] toks = line.split("\t");
			Element element = (Element)Serialization.deserialize(toks[1]);
			System.out.println(element.getID() + ". " + element.getData() + ": " + element.getCount());
		}
		reader.close();
	}
	
	public static void listFeatureCounts(File file) throws IOException, SerializationException, InvalidCountException, InvalidIDException {
		System.out.println("\n\nFeature counts: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String line = null;
		while ((line=reader.readLine())!=null) {
		//for (int i =0; i<10;i++) {
			//line=reader.readLine();
			String[] toks = line.split("\t");
			Feature feature = (Feature)Serialization.deserialize(toks[1]);
			System.out.println(feature.getID() + ". " + feature.getData() + ": " + feature.getCount());
		}
		reader.close();
	}
	
	public static void listElementFeatureCounts(File file) throws IOException, SerializationException, InvalidCountException {
			System.out.println("\n\nElement-feature counts: ");
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			String line = null;
			while ((line=reader.readLine())!=null) {
			//for (int i =0; i<10;i++) {
				//line=reader.readLine();
				String[] toks = line.split("\t");
				int elementId = Integer.parseInt(toks[0]);
				@SuppressWarnings("unchecked")
				TroveBasedIDKeyPersistentBasicMap<Double> featureCounts = (TroveBasedIDKeyPersistentBasicMap<Double>)Serialization.deserialize(toks[1]);
				ImmutableIterator<Pair<Integer, Double>> it = featureCounts.iterator();
				while (it.hasNext()) {
					Pair<Integer, Double> pair = it.next();
					System.out.println(elementId + "," + pair.getFirst() + ": " + pair.getSecond());
				}
			}
			reader.close();
		}	

	public static void listFeatureElementCounts(File file) throws IOException, SerializationException, InvalidCountException {
		System.out.println("\n\nFeature elements: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String line = null;
		while ((line=reader.readLine())!=null) {
		//for (int i =0; i<10;i++) {
			//line=reader.readLine();
			String[] toks = line.split("\t");
			int featureId = Integer.parseInt(toks[0]);
			@SuppressWarnings("unchecked")
			BasicSet<Integer> elements = (BasicSet<Integer>)Serialization.deserialize(toks[1]);
			ImmutableIterator<Integer> it = elements.iterator();
			while (it.hasNext()) {
				System.out.println(featureId + ": " + it.next());
			}
		}
		reader.close();
	}	

	public static void listElementFeatureScoring(File file) throws IOException, SerializationException, InvalidCountException {
		System.out.println("\n\nElement-feature scoring: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String line = null;
		while ((line=reader.readLine())!=null) {
		//for (int i =0; i<10;i++) {
			//line=reader.readLine();
			String[] toks = line.split("\t");
			int elementId = Integer.parseInt(toks[0]);
			@SuppressWarnings("unchecked")
			LinkedHashMap<Integer,Double> featureCounts = (LinkedHashMap<Integer,Double>)Serialization.deserialize(toks[1]);
			Iterator<Entry<Integer, Double>> it = featureCounts.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, Double> entry = it.next();
				System.out.println(elementId + "," + entry.getKey() + ": " + entry.getValue());
			}
		}
		reader.close();
	}


	public static void listElementScoring(File file) throws IOException, SerializationException, InvalidCountException {
		System.out.println("\n\nElement scoring: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String line = null;
		while ((line=reader.readLine())!=null) {
		//for (int i =0; i<10;i++) {
			//line=reader.readLine();
			String[] toks = line.split("\t");
			int elementId = Integer.parseInt(toks[0]);
			Double score = Serialization.deserialize(toks[1]);
			System.out.println(elementId + ", " + score);
		}
		reader.close();
	}

	
	public static void listElementSimilarities(File file) throws IOException, SerializationException, InvalidCountException {
		System.out.println("\n\nElement similarities: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String line = null;
		while ((line=reader.readLine())!=null) {
		//for (int i =0; i<10;i++) {
			//line=reader.readLine();
			String[] toks = line.split("\t");
			int elementId = Integer.parseInt(toks[0]);
			@SuppressWarnings("unchecked")
			LinkedHashMap<Integer,Double> featureCounts = (LinkedHashMap<Integer,Double>)Serialization.deserialize(toks[1]);
			Iterator<Entry<Integer, Double>> it = featureCounts.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, Double> entry = it.next();
				System.out.println(elementId + "," + entry.getKey() + ": " + entry.getValue());
			}
		}
		reader.close();
	}
	
	public static void main(String[] args) throws Exception {
		//ResolveSerializedStorages.listCooccurrences(new File(args[0]),new File(args[1]));
		//ResolveSerializedStorages.listElementCounts(new File(args[2]));
		//ResolveSerializedStorages.listFeatureCounts(new File(args[3]));
		//ResolveSerializedStorages.listElementFeatureCounts(new File(args[4]));
		//ResolveSerializedStorages.listFeatureElementCounts(new File(args[5]));
		//ResolveSerializedStorages.listElementFeatureScoring(new File(args[6]));
		//ResolveSerializedStorages.listElementScoring(new File(args[7]));
		//ResolveSerializedStorages.listElementSimilarities(new File(args[8]));
		ResolveSerializedStorages.listElementCounts(new File(args[0]));
	}


}
