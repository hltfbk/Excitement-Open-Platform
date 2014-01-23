package eu.excitementproject.eop.distsim.application;

import java.io.IOException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.InvalidCountException;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
import eu.excitementproject.eop.distsim.storage.BasicMap;
import eu.excitementproject.eop.distsim.storage.BasicMapException;
import eu.excitementproject.eop.distsim.storage.File;
import eu.excitementproject.eop.distsim.storage.IDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.storage.IdTroveBasicIntDoubleMapFile;
import eu.excitementproject.eop.distsim.storage.ItemNotFoundException;
import eu.excitementproject.eop.distsim.storage.LoadingStateException;
import eu.excitementproject.eop.distsim.storage.MemoryBasedCountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.TroveBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.util.Pair;

public class CompareElementFeatureData {

	/**
	 * Compare data of two given item-pair files
	 * @throws LoadingStateException 
	 * @throws IOException 
	 * @throws UndefinedKeyException 
	 * @throws ItemNotFoundException 
	 * @throws InvalidCountException 
	 */
	public static void main(String[] args) throws LoadingStateException, IOException, ItemNotFoundException, UndefinedKeyException, InvalidCountException {
		
		if (args.length != 3 && args.length != 4) {
			System.out.println("Usage: java eu.excitementproject.eop.distsim.application.CompareItemData <in dir 1> <in dir 2> <item-pair file name> [<check elements and features>, default no]");
			System.exit(0);
		}
		
		String dir1 = args[0];
		String dir2 = args[1];
		
		boolean bCheckElementsAndFeatures = (args.length == 4 ? Boolean.parseBoolean(args[3]) : false);
		System.out.println("bCheckElementsAndFeatures = " + bCheckElementsAndFeatures);
		
		IDKeyPersistentBasicMap<BasicMap<Integer,Double>> elementFeatureData1 =  new TroveBasedIDKeyPersistentBasicMap<BasicMap<Integer,Double>>();
		File file1 = new IdTroveBasicIntDoubleMapFile(new java.io.File(dir1 + "/" + args[2]),true);
		file1.open();
		elementFeatureData1.loadState(file1);
		file1.close();
		IDKeyPersistentBasicMap<BasicMap<Integer,Double>> elementFeatureData2 =  new TroveBasedIDKeyPersistentBasicMap<BasicMap<Integer,Double>>();
		File file2 = new IdTroveBasicIntDoubleMapFile(new java.io.File(dir2 + "/" + args[2]),true);
		file2.open();
		elementFeatureData2.loadState(file2);		
		file2.close();

		file1 = new File(new java.io.File(dir1 + "/elements"),true);
		file1.open();
		MemoryBasedCountableIdentifiableStorage<Element> elements1 = new MemoryBasedCountableIdentifiableStorage<Element>(file1);
		file1.close();
		

		file2 = new File(new java.io.File(dir2 + "/elements"),true);
		file2.open();
		MemoryBasedCountableIdentifiableStorage<Element> elements2 = new MemoryBasedCountableIdentifiableStorage<Element>(file2);
		file2.close();

		//compare elements
		if (bCheckElementsAndFeatures) {
			System.out.println("comparing elements");
			ImmutableIterator<Element> itE = elements1.iterator();
			while (itE.hasNext()) {
				Element e1 = itE.next();
				Element e2 = elements2.getData(elements2.getId(e1));
				if (e1.getCount() != e2.getCount())
					System.out.println("Differnt element counts: " + e1.getCount() + "\t" + e2.getCount());
			}
		}
		
		file1 = new File(new java.io.File(dir1 + "/features"),true);
		file1.open();
		MemoryBasedCountableIdentifiableStorage<Feature> features1 = new MemoryBasedCountableIdentifiableStorage<Feature>(file1);
		file1.close();
		

		file2 = new File(new java.io.File(dir2 + "/features"),true);
		file2.open();
		MemoryBasedCountableIdentifiableStorage<Feature> features2 = new MemoryBasedCountableIdentifiableStorage<Feature>(file2);
		file2.close();

		//compare features
		if (bCheckElementsAndFeatures) {
			System.out.println("comparing features");
			ImmutableIterator<Feature> itF = features1.iterator();
			while (itF.hasNext()) {
				Feature f1 = itF.next();
				Feature f2 = features2.getData(features2.getId(f1));
				if (f1.getCount() != f2.getCount())
					System.out.println("Differnt feature counts: " + f1.getCount() + "\t" + f2.getCount());
			}
		}
		
		
		ImmutableIterator<Pair<Integer, BasicMap<Integer, Double>>> it = elementFeatureData1.iterator();
		while (it.hasNext()) {
			Pair<Integer, BasicMap<Integer, Double>> pair1 = it.next();
			int elementId1 = pair1.getFirst();
			int elementId2 = elements2.getId(elements1.getData(elementId1));
			BasicMap<Integer, Double> featureData1 = pair1.getSecond();
			try {
				BasicMap<Integer, Double> featureData2 = elementFeatureData2.get(elementId2);
				if (featureData2 == null) {
					System.out.println("No feature data were found for element " + elementId1 + ", in " + args[1]);
				} else {
					ImmutableIterator<Pair<Integer, Double>> it2 = featureData1.iterator();
					while (it2.hasNext()) {
						Pair<Integer, Double> pair2 = it2.next();
						int featureId1 = pair2.getFirst();
						Double data1 = pair2.getSecond();
						Double data2 = featureData2.get(features2.getId(features1.getData(featureId1)));
						if (data2 == null) {
							System.out.println("Element: " + elementId1 + ", Feature: " + featureId1 + " - data2 was not found");
						} else {
							if (!data1.equals(data2)) 
								System.out.println("Element: " + elementId1 + ", Feature: " + featureId1 + " - different data where found: " + data1 + "\t" + data2);
						}
					}
				}
			} catch (BasicMapException e) {				
				System.out.println(e.toString());
			}
		}
	}

}
