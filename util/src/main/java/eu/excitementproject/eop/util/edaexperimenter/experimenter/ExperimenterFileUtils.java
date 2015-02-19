package eu.excitementproject.eop.util.edaexperimenter.experimenter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;


import eu.excitementproject.eop.lap.implbase.RawDataFormatReader;
import eu.excitementproject.eop.lap.implbase.RawDataFormatReader.PairXMLData;
import eu.excitementproject.eop.lap.implbase.RawFormatReaderException;


@SuppressWarnings("unused")
public class ExperimenterFileUtils {

	
	// load the data from the RTE-style XML files (one per cluster)
	// into a hash organized by cluster and by class (Entailment/Non-entailment)
	public static HashMap<String,HashMap<String,Set<PairXMLData>>> loadDataFromXML(String dataDir, String pattern) throws IOException{
		
		HashMap<String,HashMap<String,Set<PairXMLData>>> data = new HashMap<String,HashMap<String,Set<PairXMLData>>>();
		Logger logger =  Logger.getLogger("eu.excitementproject.eda_exp.experimenter.ExperimenterFileUtils / loadDataFromXML");
		
		//prepare the reader
		RawDataFormatReader input = null;
		
		String clusterName;
		int count = 0;
		
		logger.info("Loading data from files in directory " + dataDir + " with pattern " + pattern);
		
//		for(File file: FileUtils.listFiles(new File(dataDir), new String[] {"xml", "out", "txt"}, true)) {
		File file;
		for(Object o: FileUtils.listFiles(new File(dataDir), new String[] {"xml", "out", "txt"}, true)) {
			
			file = (File) o;

			clusterName = file.getName();
			if (clusterName.matches(".*" + pattern + ".*")) {
				HashMap<String, Set<PairXMLData>> thisData = new HashMap<String,Set<PairXMLData>>();
			
				logger.info("\tprocessing file " + clusterName);
				
				try {
					input = new RawDataFormatReader(file); 
				} catch (RawFormatReaderException e) {
					throw new IOException("Failed to read XML input format (creating input object)", e); 
				}
			
				// for each Pair data 
				while(input.hasNextPair())
				{
					RawDataFormatReader.PairXMLData pair; 
					try {
						pair = input.nextPair();
						String cls = pair.getGoldAnswer();
						Set<PairXMLData> set;
						if (thisData.containsKey(cls)) {
							set = thisData.get(cls);
						} else {
							set = new HashSet<PairXMLData>();
						}
						set.add(pair);
						thisData.put(cls, set);
						count++;
					} catch (RawFormatReaderException e) {
						throw new IOException("Failed to read XML input format (iterating through pairs)", e); 
					}		
				}

				logger.info(count + " pairs read");
				data.put(clusterName, thisData);
			}
		}

		logger.info(count + " pairs read");
//		System.out.println(count + " pairs read");
		return data;
	}

	/**
	 * Writes the collected RTE pairs data to one file
	 * 
	 * @param data
	 * @param file
	 * @param language
	 */
	@SuppressWarnings("unchecked")
	public static void writeDataToFile(
			HashMap<String, ?> data,
			String file, String language) {

		Logger logger = Logger.getLogger("eu.excitementproject.eda-exp.experimenter.ExperimenterFileUtils / writeDataToFile");
		
		logger.info("Writing " + data.size() + " cluster pairs to " + file);
//		System.out.println("Writing " + data.size() + " cluster pairs to " + file);
		
		try {
			
			OutputStream out = Files.newOutputStream(Paths.get(file));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			writer.write("<entailment-corpus lang=\"" + language + "\">\n");

			for(String key: data.keySet()) {
				if (data.get(key).getClass().equals(Set.class)) {
					writeData((Set<PairXMLData>) data.get(key), writer);
				} else {
					writeData((HashMap<String, Set<PairXMLData>>) data.get(key), writer);
				}
			}
			
			writer.write("</entailment-corpus>\n");
			writer.close();

		} catch (IOException e) {
			logger.info("Could not wrote to output file " + file);
//			System.out.println("Could not write to output file " + file);
			e.printStackTrace();
		}
	}

	
	/**
	 * Writes the collected RTE pairs data to one file
	 * 
	 * @param data
	 * @param file
	 * @param language
	 */

	public static void writeDataToFile(
			Set<PairXMLData> data,
			String file, String language) {
		
		Logger logger = Logger.getLogger("eu.excitementproject.eda-exp.experimenter.ExperimenterFileUtils / writeDataToFile");
		logger.info("Writing " + data.size() + " pairs to " + file);
		
//		System.out.println("Writing " + data.size() + " pairs to " + file);
		
		try {
			
			OutputStream out = Files.newOutputStream(Paths.get(file));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			writer.write("<entailment-corpus lang=\"" + language + "\">\n");

			writeData(data, writer);
			writer.write("</entailment-corpus>\n");
			writer.close();
		} catch (IOException e) {
			logger.info("Could not write to output file " + file);
//			System.out.println("Could not write to output file " + file);
			e.printStackTrace();
		}
	}
	
	
	// iterate through classes if necessary, and write data to file
	private static void writeData(HashMap<String, Set<PairXMLData>> instances, BufferedWriter writer) throws IOException {
		
		for(String key : instances.keySet()) {
			writeData(instances.get(key), writer);
		}
	}
	

	// write RTE pairs to file
	private static void writeData(Set<PairXMLData> instances, BufferedWriter writer) throws IOException {

		Logger logger = Logger.getLogger("eu.excitementproject.eda-exp.experimenter.ExperimenterFileUtils / writeDataToFile");
		logger.info("\twriting " + instances.size() + " pairs");
//		System.out.println("\twriting " + instances.size() + " pairs");
		
		for(PairXMLData p: instances) {
				writer.write("  <pair id=\"" + p.getId() + "\" entailment=\"" + p.getGoldAnswer() + "\" task=\"" + p.getTask() + "\">\n");
				writer.write("    <t>" + p.getText() + "</t>\n");
				writer.write("    <h>" + p.getHypothesis() + "</h>\n");
				writer.write("  </pair>\n");
		}
	}
	
	

	

	/**
	 * Replace the name of the model file in the configuration file
	 * 
	 * @param config -- the EDA configuration file to edit 
	 * @param model -- the model file to be used for experiments
	 */
	public static void writeModelInConfig(String config, String model) {

//		ConfigFileUtils.editConfigFile(config, "modelFile", model);
		
		if (model != null) {
			Path path = Paths.get(config);
			Charset charset = StandardCharsets.UTF_8;

			String content;
			try {
				content = new String(Files.readAllBytes(path), charset);
				content = content.replaceAll("<modelFile>.*?<\\/modelFile>", "<modelFile>" + model + "<\\/modelFile>");
				Files.write(path, content.getBytes(charset));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Get the language of the given data 
	 * 
	 * @param dataDir -- directory with RTE formatted data 
	 * @return the language of the data
	 * 
	 * @throws IOException
	 */
	public static String getLanguage(String dataDir, String pattern) throws IOException {
		
		RawDataFormatReader input = null;		
//		for(File file: FileUtils.listFiles(new File(dataDir), new String[] {"xml", "out", "txt"}, true)) {
		File file;
		for(Object o: FileUtils.listFiles(new File(dataDir), new String[] {"xml", "out", "txt"}, true)) {
			
			file = (File) o;
			
			if (file.getName().matches(".*" + pattern + ".*")) {
				try {	
					input = new RawDataFormatReader(file);
					return input.getLanguage();
				} catch (RawFormatReaderException e) {
					throw new IOException("Failed to obtain language information from file " + file.getAbsolutePath(), e); 
				}
			}
		}
		return null;
	}
	
}
