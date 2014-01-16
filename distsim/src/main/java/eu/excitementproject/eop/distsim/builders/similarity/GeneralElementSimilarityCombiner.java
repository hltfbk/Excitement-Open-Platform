/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.similarity;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.utilities.OS;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.scoring.combine.SimilarityCombination;
import eu.excitementproject.eop.distsim.storage.PersistenceDevice;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.SortUtil;
//import org.apache.log4j.Logger;

/**
 * A program which combines a given set of similarity measure storages into one unified similarity storage.
 * The characteristics of the process is determined by a given set of parameters, which defines the similarity combiner, the data structures, and more 
 * 
 * @author Meni Adler
 * @since 11/09/2012
 *
 */
public class GeneralElementSimilarityCombiner  {

	//private final static Logger logger = Logger.getLogger(GeneralElementSimilarityCombiner.class);
	
	public static void main(String[] args) {
		

		if (args.length != 1) {
			System.err.println("Usage: GeneralElementSimilarityCombiner <configuration file>");
			System.exit(0);
		}

		
		/*
		*/

		
		try {			
		
			//ConfigurationFile confFile = new ConfigurationFile(args[0]);
			ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(args[0])));
			
			ConfigurationParams loggingParams = confFile.getModuleConfiguration(Configuration.LOGGING);
			PropertyConfigurator.configure(loggingParams.get(Configuration.PROPERTIES_FILE));
						
			final ConfigurationParams similarityCombinerParams = confFile.getModuleConfiguration(Configuration.ELEMENT_SIMILARITY_COMBINER);
			
			ElementSimilarityCombiner combiner = (ElementSimilarityCombiner)Factory.create(similarityCombinerParams.get(Configuration.CLASS),similarityCombinerParams);

			String storageClass = similarityCombinerParams.get(Configuration.STORAGE_DEVICE_CLASS);
			
			boolean bSort = false;
			try {
				bSort = !similarityCombinerParams.getBoolean(Configuration.IS_SORTED);
			} catch (ConfigurationException e) {				
			}
			
			String tmpSortDir = "";
			try {
				tmpSortDir = similarityCombinerParams.get(Configuration.TMP_DIR);
			} catch (ConfigurationException e) {				
			}
			
			PersistenceDevice combinedDevice = (PersistenceDevice)Factory.create(storageClass,new java.io.File(similarityCombinerParams.getString(Configuration.OUT_COMBINED_FILE)),false);
			combinedDevice.open();
			
			SimilarityCombination similarityCombination = (SimilarityCombination)Factory.create(similarityCombinerParams.get(Configuration.SIMILARITY_COMBINATION_CLASS),similarityCombinerParams);
			String[] infiles = similarityCombinerParams.getStringArray(Configuration.IN_FILES);
			// build the combined similarity storage
			
			List<PersistenceDevice> similarityStorageDevices = new LinkedList<PersistenceDevice>();
			for (int i = 0; i<infiles.length; i++) {
				File file=null;
				if (bSort) {
					if (OS.isWindows())
						throw new Exception("numeric sort of files is not supported at Windows");
					file = new java.io.File(infiles[i] + ".sorted");
					SortUtil.sortFile(new File(infiles[i]),file,true,tmpSortDir);
				} else
					file = new java.io.File(infiles[i]);
				similarityStorageDevices.add((PersistenceDevice)Factory.create(storageClass,file,true));
			}
			
			
			for (PersistenceDevice device : similarityStorageDevices)
				device.open();
			combiner.combinedScores(similarityStorageDevices, similarityCombination,combinedDevice);
			for (PersistenceDevice device : similarityStorageDevices)
				device.close();			
			combinedDevice.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}