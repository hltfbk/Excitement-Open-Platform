package eu.excitementproject.eop.distsim.builders.mapred;

import java.io.BufferedReader;



import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.domains.relation.PredicateArgumentSlots;
import eu.excitementproject.eop.distsim.items.ArgumentFeature;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.storage.TroveBasedBasicIntSet;
import eu.excitementproject.eop.distsim.storage.TroveBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.Pair;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * @author Meni Adler
 * @since May 26, 2013
 * 
 * Post-processing of the map-reduce process (which extracts, counts and filters elements and features, from a given corpus).
 * The extracted elements and features are organized, in this class, into the 'traditional' distsim format, the output files: elements,features, element-feature-counts, feature-elements  
 *
 */
public class SeparateFilterAndIndexElementsFeatures  {
	
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	public static void main(String[] args) throws Exception {
	    //ConfigurationFile confFile = new ConfigurationFile(args[0]);
	    ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(args[0])));
	    separateFilterAndIndexElementsFeatures1(confFile.getModuleConfiguration(Configuration.MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_1));
	    separateFilterAndIndexElementsFeatures2(confFile.getModuleConfiguration(Configuration.MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_2));
	}
	
	/**
	 * Generates the 'elements' and the 'features' output files, from the given output of the map-reduce process, composed of elements and features counts
	 * 
	 * @param confParams
	 * @throws Exception
	 */
	public static void separateFilterAndIndexElementsFeatures1(ConfigurationParams confParams) throws Exception {
		separateFilterAndIndexElementsFeatures1(confParams,null);
	}

	/**
	 * Generates the 'elements' and the 'features' output files, from the given output of the map-reduce process, composed of elements and features counts
	 * 
	 * @param confParams
	 * @param selectedSlot - for DIRT models, the slot of the feature argument (X or Y). Default, null
	 * @throws Exception
	 */
	public static void separateFilterAndIndexElementsFeatures1(ConfigurationParams confParams, PredicateArgumentSlots selectedSlot) throws Exception {
	    
		String encoding = DEFAULT_ENCODING;
		try {
			encoding = confParams.get(Configuration.ENCODING);
		} catch (ConfigurationException e) {
		}

	    String indir = confParams.get(Configuration.INDIR);
		int minCount = confParams.getInt(Configuration.MIN_COUNT);
		eu.excitementproject.eop.distsim.storage.File outFeatures = 
				new eu.excitementproject.eop.distsim.storage.File(
						new File(confParams.get(Configuration.FEATURES_FILE)),false, encoding);
		outFeatures.open();
		String featureClass = confParams.get(Configuration.FEATURE_CLASS);
		
		eu.excitementproject.eop.distsim.storage.File outElements = null;
		String elementClass = null;
		try {
			outElements = new eu.excitementproject.eop.distsim.storage.File(
					new File(confParams.get(Configuration.ELEMENTS_FILE)),false, encoding);
			outElements.open();
			elementClass = confParams.get(Configuration.ELEMENT_CLASS);
		} catch (ConfigurationException e) {	
			if (outElements != null && elementClass == null)
				throw e;
		}
		
		String line = null;
		int iElementId=0, iFeatureId=0;
		for (File file : new File(indir).listFiles()) {
			if (!file.getName().startsWith(".")) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),encoding));
				while ((line = reader.readLine()) != null) {
					String[] toks = line.split("\t");
					String data = toks[0];
					
					long count = Long.parseLong(toks[1]);
					if (data.startsWith(Defs.ELEMENT_TAG) && outElements != null ) {
						if (count >= minCount) {
							String elementKey = data.substring(Defs.ELEMENT_TAG.length());
							iElementId++;
							Element element = (Element) Factory.create(elementClass);
							element.fromKey(elementKey);
							element.setID(iElementId);
							element.setCount(count);
							outElements.write(iElementId,element);
						}					
					}
					if (data.startsWith(Defs.FEATURE_TAG)) {
						String featureKey = data.substring(Defs.FEATURE_TAG.length());
						iFeatureId++;
						Feature feature = (Feature) Factory.create(featureClass);
						feature.fromKey(featureKey);
						
						if (selectedSlot == null || ((ArgumentFeature)feature).getData().getFirst() == selectedSlot) {
							feature.setID(iFeatureId);
							feature.setCount(count);
							outFeatures.write(iFeatureId,feature);
						}
					}
				}
				reader.close();
			}
		}
		if (outElements != null)
			outElements.close();
		outFeatures.close();
	}
	
	/**
	 * Generates the 'element-feature-counts' and the 'features-elements' output files, from the given output of the map-reduce process, composed of elements and features counts
	 * 
	 * @param confParams
	 * @throws Exception
	 */
	public static void separateFilterAndIndexElementsFeatures2(ConfigurationParams confParams) throws Exception {
		
		TObjectIntMap<String> elements = new TObjectIntHashMap<String>();
		TObjectIntMap<String> features = new TObjectIntHashMap<String>();

		String encoding = DEFAULT_ENCODING;
		try {
			encoding = confParams.get(Configuration.ENCODING);
		} catch (ConfigurationException e) {
		}
		
		String indir = confParams.get(Configuration.INDIR);
		eu.excitementproject.eop.distsim.storage.File inElements = 
				new eu.excitementproject.eop.distsim.storage.File(new File(confParams.get(Configuration.ELEMENTS_FILE)),true, encoding);
		eu.excitementproject.eop.distsim.storage.File inFeatures = 
				new eu.excitementproject.eop.distsim.storage.File(new File(confParams.get(Configuration.FEATURES_FILE)),true, encoding);

		eu.excitementproject.eop.distsim.storage.IdTroveBasicIntDoubleMapFile outElementFeatures = 
				new eu.excitementproject.eop.distsim.storage.IdTroveBasicIntDoubleMapFile(new File(confParams.get(Configuration.ELEMENT_FEATURE_COUNTS_FILE)),false, encoding);
		eu.excitementproject.eop.distsim.storage.IdTroveBasicIntSetFile outFeatureElements = 
				new eu.excitementproject.eop.distsim.storage.IdTroveBasicIntSetFile(new File(confParams.get(Configuration.FEATURE_ELEMENTS_FILE)),false, encoding);
		
		inElements.open();
		inFeatures.open();
		outElementFeatures.open();
		outFeatureElements.open();
		
		Pair<Integer, Serializable> pair = null;
		while ((pair = inElements.read()) != null) {
			int id = pair.getFirst();
			Element element = (Element) pair.getSecond();
			elements.put(element.toKey(), id);
		}
		inElements.close();
		
		while ((pair = inFeatures.read()) != null) {
			int id = pair.getFirst();
			Feature feature = (Feature) pair.getSecond();
			features.put(feature.toKey(), id);
		}
		inFeatures.close();
		
		String line = null;
		TroveBasedIDKeyPersistentBasicMap<TroveBasedBasicIntSet> featureElements = 
				new TroveBasedIDKeyPersistentBasicMap<TroveBasedBasicIntSet>(); 
		
		TroveBasedIDKeyPersistentBasicMap<Double> elementFeatureCounts = null;
		
		int posEF = Defs.ELEMENTFEATURE_TAG.length();
		int currentElementid = -1;
		for (File file : new File(indir).listFiles()) {
			if (!file.getName().startsWith(".")) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),encoding));
				while ((line = reader.readLine()) != null) {
					String[] toks = line.split("\t");
					String data = toks[0];
					long count = Long.parseLong(toks[1]);
					if (data.startsWith(Defs.ELEMENTFEATURE_TAG)) {
						String[] elementfeature = data.substring(posEF).split(Defs.ELEMENTFEATURE_SEPARATOR);
						String element = elementfeature[0];
						String feature = elementfeature[1];
						
						if (elements.containsKey(element)) {
							if (!features.containsKey(feature)) {
								//System.out.println("unrecognized feature: " + feature);
								continue;
							}
							int elementId = elements.get(element);
							int featureId = features.get(feature);

							if (elementId != currentElementid ||  currentElementid == -1) {
								if (currentElementid != -1)
									outElementFeatures.write(currentElementid,elementFeatureCounts);
								elementFeatureCounts = new TroveBasedIDKeyPersistentBasicMap<Double>();
								currentElementid = elementId;
							}
							elementFeatureCounts.put(featureId, (double)count);
							
							TroveBasedBasicIntSet fElements = featureElements.get(featureId);
							if (fElements == null) {
								fElements = new TroveBasedBasicIntSet();
								featureElements.put(featureId, fElements);
							}
							fElements.add(elementId);
						}
					}
				}
				reader.close();
			}
		}
		
		if (currentElementid != -1)
			outElementFeatures.write(currentElementid,elementFeatureCounts);
		featureElements.saveState(outFeatureElements);
		
		outElementFeatures.close();
		outFeatureElements.close();
	}

}
