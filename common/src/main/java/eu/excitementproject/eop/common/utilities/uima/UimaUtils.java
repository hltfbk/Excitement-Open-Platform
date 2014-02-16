package eu.excitementproject.eop.common.utilities.uima;

import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

/**
 * Generic convenience methods when using UIMA.
 * 
 * @author Ofer Bronstein
 * @since August 2013
 */
public class UimaUtils {
	
	/**
	 * Path, inside the project, to a descriptor file of a "dummy" analysis engine.
	 * This is required for working with XMI. 
	 */
	public static final String DUMMY_AE_DESC = "src/main/resources/desc/DummyAE.xml";
	
	// This class should not be instantiated
	private UimaUtils() {}

	/**
	 * Loads an AE from its descriptor.
	 * 
	 * @param aeDescriptorPath path to an xml desciptor of the AE
	 * @return
	 * @throws InvalidXMLException
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngine loadAE(String aeDescriptorPath) throws InvalidXMLException, ResourceInitializationException {
		InputStream s = UimaUtils.class.getResourceAsStream(aeDescriptorPath);
		XMLInputSource in = new XMLInputSource(s, null); 
		ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);		
		return UIMAFramework.produceAnalysisEngine(specifier); 
	}
	
	/**
	 * Loads a CAS from its XMI file.
	 * 
	 * @param xmiFile file to load
	 * @param aeDescriptorPath path to an XML descriptor of SOME analysis engine that connects
	 * to the type system used in the XMI. You can create some Dummy AE for that
	 * (see the one in lap: <tt>src/main/resources/desc/DummyAE.xml</tt>)
	 * @return a JCas object loaded to memory
	 */
	public static JCas loadXmi(File xmiFile, String aeDescriptorPath) throws InvalidXMLException, ResourceInitializationException, SAXException, IOException {
		AnalysisEngine ae = UimaUtils.loadAE(aeDescriptorPath);
		JCas jcas = ae.newJCas(); 
		FileInputStream inputStream = new FileInputStream(xmiFile);
		XmiCasDeserializer.deserialize(inputStream, jcas.getCas()); 
		inputStream.close();
		return jcas; 
	}
	
	/**
	 * Loads a CAS from its XMI file. Uses <tt>src/main/resources/desc/DummyAE.xml</tt>
	 * as the required analysis engine descriptor.
	 * 
	 * @param xmiFile file to load
	 * @return a JCas object loaded to memory
	 */
	public static JCas loadXmi(File xmiFile) throws InvalidXMLException, ResourceInitializationException, SAXException, IOException {
		return loadXmi(xmiFile, DUMMY_AE_DESC);
	}
	
	/**
	 * Dumps the given JCas to a file on disk.
	 *  
	 * @param xmiFile
	 * @param jcas
	 */
	public static void dumpXmi(File xmiFile, JCas jcas) throws SAXException, IOException {
		FileOutputStream out = new FileOutputStream(xmiFile);
		XmiCasSerializer ser = new XmiCasSerializer(jcas.getTypeSystem());
		XMLSerializer xmlSer = new XMLSerializer(out, false);
		ser.serialize(jcas.getCas(), xmlSer.getContentHandler());
		out.close();
	}
	
	/**
	 * Loads a {@link TypeSystemDescription} from a given descriptor file path.
	 * This is useful for performing operation on the type system, e.g. for adding
	 * types dynamically in runtime.
	 * 
	 * @param typeSystemDescriptorPath
	 * @return
	 */
	public static TypeSystemDescription loadTypeSystem(String typeSystemDescriptorPath) throws InvalidXMLException {
		URL tsUrl = UimaUtils.class.getResource(typeSystemDescriptorPath);
		TypeSystemDescription typeSystem = createTypeSystemDescriptionFromPath(tsUrl.toString());
		typeSystem.resolveImports();
		return typeSystem;
	}
	
	/**
	 * Loads a {@link TypeSystemDescription} from a given descriptor file path,
	 * and verifies that the given type exists there (in order to avoid loading
	 * and manipulating the wrong type system).
	 * This is useful for performing operation on the type system, e.g. for adding
	 * types dynamically in runtime.
	 * 
	 * @param typeSystemDescriptorPath
	 * @param existingTypeName name of type to verify that exists in the loaded type system
	 * @return
	 */
	public static TypeSystemDescription loadTypeSystem(String typeSystemDescriptorPath, String existingTypeName) throws InvalidXMLException, UimaUtilsException {
		TypeSystemDescription typeSystem = loadTypeSystem(typeSystemDescriptorPath);
		TypeDescription type = typeSystem.getType(existingTypeName);
		if (type == null) {
			throw new UimaUtilsException("Could not find type " + existingTypeName + " in type system loaded from " + typeSystemDescriptorPath);
		}
		return typeSystem;
	}
}
