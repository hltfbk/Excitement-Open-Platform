package eu.excitementproject.eop.distsim.storage;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.TextUnit;

public class TestCoOccurrenceDB {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) throws Exception {
		PersistenceDevice textUnitDevice = new File(new java.io.File(args[0]),true);
		PersistenceDevice coOccurrenceDevice = new File(new java.io.File(args[1]),true);
		textUnitDevice.open();
		coOccurrenceDevice.open();
		CountableIdentifiableStorage<TextUnit> textUnitStorage = new MemoryBasedCountableIdentifiableStorage<TextUnit>(textUnitDevice);
		BasicCooccurrenceStorage cooccurrenceDB = new DeviceBasedCooccurrenceStorage(textUnitStorage,coOccurrenceDevice);
		
		ImmutableIterator<Cooccurrence> it = cooccurrenceDB.getCooccurrenceInstances();
		for (int i=0; i<10;i++)
			System.out.println(it.next().getTextItem2());
		
		//ImmutableIterator<TextUnit> it = textUnitStorage.iterator();
		//for (int i=0; i<1000;i++)
			//System.out.println(it.next());
		
		textUnitDevice.close();
		coOccurrenceDevice.close();

	}
}
