package eu.excitementproject.eop.distsim.application;

import java.io.IOException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
import eu.excitementproject.eop.distsim.storage.BasicMapException;
import eu.excitementproject.eop.distsim.storage.BasicSet;
import eu.excitementproject.eop.distsim.storage.File;
import eu.excitementproject.eop.distsim.storage.IDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.storage.IdTroveBasicIntSetFile;
import eu.excitementproject.eop.distsim.storage.ItemNotFoundException;
import eu.excitementproject.eop.distsim.storage.LoadingStateException;
import eu.excitementproject.eop.distsim.storage.MemoryBasedCountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.TroveBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.util.Pair;

public class CompareFeatureElements {

	/**
	 * Compare the feature elements in two given files
	 * @throws LoadingStateException 
	 * @throws IOException 
	 * @throws UndefinedKeyException 
	 * @throws ItemNotFoundException 
	 */
	public static void main(String[] args) throws LoadingStateException, IOException, ItemNotFoundException, UndefinedKeyException {
		
		if (args.length != 3) {
			System.out.println("Usage: java eu.excitementproject.eop.distsim.application.CompareFeatureElements <in dir 1> <in dir 2> <compared file name>");
			System.exit(0);
		}
		
		String dir1 = args[0];
		String dir2 = args[1];
		
		IDKeyPersistentBasicMap<BasicSet<Integer>> featureElements1 =  new TroveBasedIDKeyPersistentBasicMap<BasicSet<Integer>>();
		File file1 = new IdTroveBasicIntSetFile(new java.io.File(dir1 + "/" + args[2]),true);
		file1.open();
		featureElements1.loadState(file1);
		file1.close();
		IDKeyPersistentBasicMap<BasicSet<Integer>> featureElements2 =  new TroveBasedIDKeyPersistentBasicMap<BasicSet<Integer>>();
		File file2 = new IdTroveBasicIntSetFile(new java.io.File(dir2 + "/" + args[2]),true);
		file2.open();
		featureElements2.loadState(file2);		
		file2.close();

		
		file1 = new File(new java.io.File(dir1 + "/elements"),true);
		file1.open();
		MemoryBasedCountableIdentifiableStorage<Element> elements1 = new MemoryBasedCountableIdentifiableStorage<Element>(file1);
		file1.close();
		

		file2 = new File(new java.io.File(dir2 + "/elements"),true);
		file2.open();
		MemoryBasedCountableIdentifiableStorage<Element> elements2 = new MemoryBasedCountableIdentifiableStorage<Element>(file2);
		file2.close();

		file1 = new File(new java.io.File(dir1 + "/features"),true);
		file1.open();
		MemoryBasedCountableIdentifiableStorage<Feature> features1 = new MemoryBasedCountableIdentifiableStorage<Feature>(file1);
		file1.close();
		

		file2 = new File(new java.io.File(dir2 + "/features"),true);
		file2.open();
		MemoryBasedCountableIdentifiableStorage<Feature> features2 = new MemoryBasedCountableIdentifiableStorage<Feature>(file2);
		file2.close();

		
		ImmutableIterator<Pair<Integer, BasicSet<Integer>>> it = featureElements1.iterator();
		while (it.hasNext()) {
			Pair<Integer, BasicSet<Integer>> pair1 = it.next();
			int featureId1 = pair1.getFirst();
			int featureId2 = features2.getId(features1.getData(featureId1));
			BasicSet<Integer> fElements1 = pair1.getSecond();
			try {
				BasicSet<Integer> fElements2 = featureElements2.get(featureId2);
				if (fElements2 == null) {
					System.out.println("No feature data were found for feature " + featureId1 + ", in " + args[1]);
				} else {
					ImmutableIterator<Integer> it2 = fElements1.iterator();
					while (it2.hasNext()) {
						Integer elementId1 = it2.next();
						if (!fElements2.contains(elements2.getId(elements1.getData(elementId1))))
							System.out.println("Feature: " + featureId1 + ", element " +  elementId1 + " is not founf in " + args[1]);
					}
				}
			} catch (BasicMapException e) {				
				System.out.println(e.toString());
			}
		}
	}

}
