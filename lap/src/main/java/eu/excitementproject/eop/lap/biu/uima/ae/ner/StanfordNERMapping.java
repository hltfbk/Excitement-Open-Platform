package eu.excitementproject.eop.lap.biu.uima.ae.ner;

/**
 * This class is used for accessing DKPro's Stanford NER mapping (currently it
 * only provides a static string of the .map file's location).
 * <BR>
 * May be used by Named Entity Recognizers that work with the Stanford types.
 * <BR>
 * 
 * @author Ofer Bronstein
 * @since Feb 2013
 *
 */
public class StanfordNERMapping {
	public static final String MAPPING_LOCATION = "classpath:/de/tudarmstadt/ukp/dkpro/" +
			"core/stanfordnlp/lib/ner-${language}-${variant}.map";
	
	/// Ofer Bronstein 2.7.13: If we decide to stop depending on DKPro's stanford-gpl jar, we can
	/// just copy the file ner-en-all.3class.distsim.crf.map to this project (lap),
	/// under resources/map, and use this MAPPING_LOCATION:
	//public static final String MAPPING_LOCATION = "classpath:/map/ner-${language}-${variant}.map";
}
