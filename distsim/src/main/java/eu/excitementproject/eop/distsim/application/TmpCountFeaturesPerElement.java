package eu.excitementproject.eop.distsim.application;

import java.io.File;
import java.util.LinkedHashMap;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.builders.ConfigurationBasedDataStructureFactory;
import eu.excitementproject.eop.distsim.builders.DataStructureFactory;
import eu.excitementproject.eop.distsim.storage.IDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.storage.PersistenceDevice;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.Pair;

public class TmpCountFeaturesPerElement {

	public static void main(String[] args) throws Exception {
			if (args.length != 2) {
				System.err.println("Usage: java eu.excitementproject.eop.distsim.application.TmpCountFeaturesPerElement <element-similarity-calculation configuration file> <max feature num>");
				System.exit(0);
			}

			//ConfigurationFile confFile = new ConfigurationFile(args[0]);
			ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(args[0])));
			
			DataStructureFactory dataStructureFactory = new ConfigurationBasedDataStructureFactory(confFile);			
			// build the element score storage
			ConfigurationParams elemntFeaturesScoreParams = confFile.getModuleConfiguration(Configuration.ELEMENT_FEATURE_SCORES_STORAGE_DEVICE);
			PersistenceDevice elemntFeaturesScoresDevice = (PersistenceDevice)Factory.create(elemntFeaturesScoreParams.get(Configuration.CLASS), elemntFeaturesScoreParams);
			elemntFeaturesScoresDevice.open();
			IDKeyPersistentBasicMap<LinkedHashMap<Integer, Double>> elementFeatureScores = dataStructureFactory.createElementFeatureScoresDataStructure();
			elementFeatureScores.loadState(elemntFeaturesScoresDevice);
			elemntFeaturesScoresDevice.close();
			
			System.out.println("Finshed loading storage");
			
			int upbound = Integer.parseInt(args[1]);
			int maxFeatureNum = 0;
			ImmutableIterator<Pair<Integer, LinkedHashMap<Integer, Double>>> it = elementFeatureScores.iterator();
			while (it.hasNext()) {
				Pair<Integer, LinkedHashMap<Integer, Double>> pair = it.next();
				int featureNum = pair.getSecond().size();
				if (featureNum > maxFeatureNum)
					maxFeatureNum = featureNum;
				if (maxFeatureNum > upbound) {
					System.out.println(maxFeatureNum + " features were found for element " + pair.getFirst());
					System.exit(0);
				}
			}
			
			System.out.println("maxFeatureNum = " + maxFeatureNum);			
	}

}
