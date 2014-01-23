package eu.excitementproject.eop.distsim.application;

import java.io.IOException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
import eu.excitementproject.eop.distsim.storage.BasicMapException;
import eu.excitementproject.eop.distsim.storage.File;
import eu.excitementproject.eop.distsim.storage.IDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.storage.IdDoubleFile;
import eu.excitementproject.eop.distsim.storage.ItemNotFoundException;
import eu.excitementproject.eop.distsim.storage.LoadingStateException;
import eu.excitementproject.eop.distsim.storage.MemoryBasedCountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.TroveBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.util.Pair;

public class CompareElementScores {

	/**
	 * Compare data of two given item-pair files
	 * @throws LoadingStateException 
	 * @throws IOException 
	 * @throws UndefinedKeyException 
	 * @throws ItemNotFoundException 
	 */
	public static void main(String[] args) throws LoadingStateException, IOException, ItemNotFoundException, UndefinedKeyException {
		
		if (args.length != 3) {
			System.out.println("Usage: java eu.excitementproject.eop.distsim.application.CompareElementScores <in dir 1> <in dir 2> <item-pair file name>");
			System.exit(0);
		}
		
		String dir1 = args[0];
		String dir2 = args[1];
		
		IDKeyPersistentBasicMap<Double> elementScores1 =  new TroveBasedIDKeyPersistentBasicMap<Double>();
		File file1 = new IdDoubleFile(new java.io.File(dir1 + "/" + args[2]),true);
		file1.open();
		elementScores1.loadState(file1);
		file1.close();
		IDKeyPersistentBasicMap<Double> elementScores2 =  new TroveBasedIDKeyPersistentBasicMap<Double>();
		File file2 = new IdDoubleFile(new java.io.File(dir2 + "/" + args[2]),true);
		file2.open();
		elementScores2.loadState(file2);		
		file2.close();

		file1 = new File(new java.io.File(dir1 + "/elements"),true);
		file1.open();
		MemoryBasedCountableIdentifiableStorage<Element> elements1 = new MemoryBasedCountableIdentifiableStorage<Element>(file1);
		file1.close();
		

		file2 = new File(new java.io.File(dir2 + "/elements"),true);
		file2.open();
		MemoryBasedCountableIdentifiableStorage<Element> elements2 = new MemoryBasedCountableIdentifiableStorage<Element>(file2);
		file2.close();

		ImmutableIterator<Pair<Integer, Double>> it = elementScores1.iterator();
		while (it.hasNext()) {
			Pair<Integer, Double> pair1 = it.next();
			int elementId1 = pair1.getFirst();
			int elementId2 = elements2.getId(elements1.getData(elementId1));
			Double score1 = pair1.getSecond();
			try {
				Double score2 = elementScores2.get(elementId2);
				if (!score1.equals(score2)) 
					System.out.println("Element " + elementId1 + " score " + score1 + " != " + score2);
			} catch (BasicMapException e) {
				e.printStackTrace();
			}
		}
	}

}
