package eu.excitementproject.eop.distsim.application;

import java.io.IOException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.items.Element;
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

public class CompareElementSimilarities {

	/**
	 * Compare data of two given item-pair files
	 * @throws LoadingStateException 
	 * @throws IOException 
	 * @throws UndefinedKeyException 
	 * @throws ItemNotFoundException 
	 * @throws InvalidCountException 
	 */
	public static void main(String[] args) throws LoadingStateException, IOException, ItemNotFoundException, UndefinedKeyException, InvalidCountException {
		
		if (args.length != 3) {
			System.out.println("Usage: java eu.excitementproject.eop.distsim.application.CompareElementSimilarities <in dir 1> <in dir 2> <similarity file name>");
			System.exit(0);
		}
		
		String dir1 = args[0];
		String dir2 = args[1];
		
		IDKeyPersistentBasicMap<BasicMap<Integer,Double>> elementSimilarityFeatureData1 =  new TroveBasedIDKeyPersistentBasicMap<BasicMap<Integer,Double>>();
		File file1 = new IdTroveBasicIntDoubleMapFile(new java.io.File(dir1 + "/" + args[2]),true);
		file1.open();
		elementSimilarityFeatureData1.loadState(file1);
		file1.close();
		IDKeyPersistentBasicMap<BasicMap<Integer,Double>> elementSimilarityData2 =  new TroveBasedIDKeyPersistentBasicMap<BasicMap<Integer,Double>>();
		File file2 = new IdTroveBasicIntDoubleMapFile(new java.io.File(dir2 + "/" + args[2]),true);
		file2.open();
		elementSimilarityData2.loadState(file2);		
		file2.close();

		file1 = new File(new java.io.File(dir1 + "/elements"),true);
		file1.open();
		MemoryBasedCountableIdentifiableStorage<Element> elements1 = new MemoryBasedCountableIdentifiableStorage<Element>(file1);
		file1.close();
		

		file2 = new File(new java.io.File(dir2 + "/elements"),true);
		file2.open();
		MemoryBasedCountableIdentifiableStorage<Element> elements2 = new MemoryBasedCountableIdentifiableStorage<Element>(file2);
		file2.close();

		ImmutableIterator<Pair<Integer, BasicMap<Integer, Double>>> it = elementSimilarityFeatureData1.iterator();
		while (it.hasNext()) {
			Pair<Integer, BasicMap<Integer, Double>> pair1 = it.next();
			int elementId11 = pair1.getFirst();
			int elementId12 = elements2.getId(elements1.getData(elementId11));
			BasicMap<Integer, Double> similarityData1 = pair1.getSecond();
			try {
				BasicMap<Integer, Double> similarityData2 = elementSimilarityData2.get(elementId12);
				if (similarityData2 == null) {
					System.out.println("No similarity data were found for element " + elementId11 + ", in " + args[1]);
				} else {
					ImmutableIterator<Pair<Integer, Double>> it2 = similarityData1.iterator();
					while (it2.hasNext()) {
						Pair<Integer, Double> pair2 = it2.next();
						int elementId21 = pair2.getFirst();
						int elementId22 = elements2.getId(elements1.getData(elementId21));
						Double score1 = pair2.getSecond();
						Double score2 = similarityData2.get(elementId22);
						if (score2 == null) {
							System.out.println("Element1: " + elementId11 + ", Element2: " + elementId21 + " - data2 was not found");
						} else {
							if (!score1.equals(score2)) 
								System.out.println("Element: " + elementId11 + ", Element2: " + elementId21 + " - different similarity was found: " + score1 + "\t" + score2);
						}
					}
				}
			} catch (BasicMapException e) {				
				System.out.println(e.toString());
			}
		}
	}

}
