package eu.excitementproject.eop.lap.biu.uima.ae.postagger;

/**
 * A UIMA wrapper for BIU's OpenNlpPosTagger.
 * This class is commented-out, since during BIU's migration to EXCITEMENT (January 2013)
 * the OpenNlpPosTagger class was removed, due to a jar conflict.
 * If this class is ever needed in BIU, it should be implemented over the jar
 * used in EOP, and then this wrapper class can be used (assuming the constructor and
 * interface methods have the same input).
 */

//import java.io.File;
//
//import org.uimafit.descriptor.ConfigurationParameter;
//
//import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
//import eu.excitementproject.eop.lap.biu.en.postagger.OpenNlpPosTagger;
//import eu.excitementproject.eop.lap.util.Envelope;
//
//public class OpenNlpPosTaggerAE extends PosTaggerAE<OpenNlpPosTagger> {
//
//	/**
//	 * Model file of this POS tagger.
//	 */
//	public static final String PARAM_MODEL_FILE = "model_file";
//	@ConfigurationParameter(name = PARAM_MODEL_FILE, mandatory = true)
//	private File modelFile;
//	
//	public static final String PARAM_TAG_DICT = "tag_dict";
//	@ConfigurationParameter(name = PARAM_TAG_DICT, mandatory = true)
//	private String tagDict;
//
//	private static Envelope<OpenNlpPosTagger> envelope = new Envelope<OpenNlpPosTagger>();
//	
//	@Override
//	protected final Envelope<OpenNlpPosTagger> getEnvelope(){return envelope;}
//	
//	@Override
//	protected OpenNlpPosTagger buildInnerTool() throws Exception {
//		OpenNlpPosTagger tagger = new OpenNlpPosTagger(modelFile, tagDict);
//		tagger.init();
//		return tagger;
//	}
//
//	@Override
//	protected void configureMapping() {
//		mappingProvider.setDefault(MappingProvider.LOCATION, PennPOSMapping.MAPPING_LOCATION);
//		mappingProvider.setDefault("tagger.tagset", "default");
//	}
//	
//}
