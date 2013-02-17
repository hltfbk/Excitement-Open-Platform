package eu.excitementproject.eop.lap.biu.ae.ner;

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
}
