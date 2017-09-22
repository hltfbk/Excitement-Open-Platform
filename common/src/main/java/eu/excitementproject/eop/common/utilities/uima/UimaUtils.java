package eu.excitementproject.eop.common.utilities.uima;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.Subiterator;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.uima.fit.factory.TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath;

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
	public static final String DUMMY_AE_DESC = "/desc/DummyAE.xml";
	
	// This class should not be instantiated
	private UimaUtils() {}

	/**
	 * Loads an AE from its descriptor.
	 * 
	 * @param aeDescriptorPath path to an xml descriptor of the AE
	 * @return
	 * @throws UimaUtilsException 
	 */
	public static AnalysisEngine loadAE(String aeDescriptorPath) throws UimaUtilsException {
		InputStream s = UimaUtils.class.getResourceAsStream(aeDescriptorPath);
		XMLInputSource in = new XMLInputSource(s, null);
		try {
			ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);		
			return UIMAFramework.produceAnalysisEngine(specifier);
		}
		catch (ResourceInitializationException | InvalidXMLException e) {
			throw new UimaUtilsException(e);
		}
	}
	
	/**
	 * Loads a CAS from its XMI file.
	 * 
	 * @param xmiFile file to load
	 * @param aeDescriptorPath path to an XML descriptor of SOME analysis engine that connects
	 * to the type system used in the XMI. You can create some Dummy AE for that
	 * (see the one in lap: <tt>src/main/resources/desc/DummyAE.xml</tt>)
	 * @return a JCas object loaded to memory
	 * @throws UimaUtilsException 
	 */
	public static JCas loadXmi(File xmiFile, String aeDescriptorPath) throws UimaUtilsException {
		AnalysisEngine ae = UimaUtils.loadAE(aeDescriptorPath);
		try {
			JCas jcas = ae.newJCas(); 
			FileInputStream inputStream = new FileInputStream(xmiFile);
			XmiCasDeserializer.deserialize(inputStream, jcas.getCas()); 
			inputStream.close();
			return jcas; 
		}
		catch (ResourceInitializationException | IOException | SAXException e) {
			throw new UimaUtilsException(e);
		}

	}
	
	/**
	 * Loads a CAS from its XMI file. Uses <tt>src/main/resources/desc/DummyAE.xml</tt>
	 * as the required analysis engine descriptor.
	 * 
	 * @param xmiFile file to load
	 * @return a JCas object loaded to memory
	 * @throws UimaUtilsException 
	 */
	public static JCas loadXmi(File xmiFile) throws UimaUtilsException {
		return loadXmi(xmiFile, DUMMY_AE_DESC);
	}
	
	/**
	 * Returns a new JCas. Uses <tt>src/main/resources/desc/DummyAE.xml</tt>
	 * as the required analysis engine descriptor.
	 * 
	 * @return a new JCas
	 * @throws UimaUtilsException
	 */
	public static JCas newJcas() throws UimaUtilsException {
		try {
			AnalysisEngine ae = loadAE(DUMMY_AE_DESC);
			return ae.newJCas();
		}
		catch (ResourceInitializationException e) {
			throw new UimaUtilsException(e);
		}
	}
	
	/**
	 * Dumps the given JCas to a file on disk.
	 *  
	 * @param xmiFile
	 * @param jcas
	 * @throws UimaUtilsException 
	 */
	public static void dumpXmi(File xmiFile, JCas jcas) throws UimaUtilsException {
		try {
			FileOutputStream out = new FileOutputStream(xmiFile);
			XmiCasSerializer ser = new XmiCasSerializer(jcas.getTypeSystem());
			XMLSerializer xmlSer = new XMLSerializer(out, false);
			ser.serialize(jcas.getCas(), xmlSer.getContentHandler());
			out.close();
		}
		catch (IOException | SAXException e) {
			throw new UimaUtilsException(e);
		}

	}
	
	/**
	 * Loads a {@link TypeSystemDescription} from a given descriptor file path.
	 * This is useful for performing operation on the type system, e.g. for adding
	 * types dynamically in runtime.
	 * 
	 * @param typeSystemDescriptorPath
	 * @return
	 * @throws UimaUtilsException 
	 */
	public static TypeSystemDescription loadTypeSystem(String typeSystemDescriptorPath) throws UimaUtilsException {
		URL tsUrl = UimaUtils.class.getResource(typeSystemDescriptorPath);
		TypeSystemDescription typeSystem = createTypeSystemDescriptionFromPath(tsUrl.toString());
		try {
			typeSystem.resolveImports();
			return typeSystem;
		}
		catch (InvalidXMLException e) {
			throw new UimaUtilsException(e);
		}
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
	 * @throws UimaUtilsException 
	 */
	public static TypeSystemDescription loadTypeSystem(String typeSystemDescriptorPath, String existingTypeName) throws UimaUtilsException {
		TypeSystemDescription typeSystem = loadTypeSystem(typeSystemDescriptorPath);
		TypeDescription type = typeSystem.getType(existingTypeName);
		if (type == null) {
			throw new UimaUtilsException("Could not find type " + existingTypeName + " in type system loaded from " + typeSystemDescriptorPath);
		}
		return typeSystem;
	}
	

	/**
	 * Get the annotation of the given annotation type constrained by a 'covering' annotation.
	 * Iterates over all annotations of the given type to find the covered annotations.
	 * Does not use subiterators.
	 *
	 * @param <T>
	 *            the required annotation type to be retrieved.
	 * @param jCas
	 *            a JCas containing the annotation.
	 * @param type
	 *            the required annotation type to be retrieved.
	 * @param coveringAnnotation
	 *            the covering annotation.
	 * @return the single instance of the required type.
	 * @throws IllegalArgumentException if not exactly one instance if the given type is present
	 * 		   under the covering annotation.
	 * @see Subiterator
	 * 
	 * @author Ofer Bronstein
	 * @since April 2014
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T selectCoveredSingle(JCas jCas, final Class<T> type, AnnotationFS coveringAnnotation) {
		return (T) selectCoveredSingle(jCas.getCas(), JCasUtil.getType(jCas, type),
				coveringAnnotation);
	}

	/**
	 * Get the annotation of the given annotation type constrained by a 'covering' annotation.
	 * Iterates over all annotations of the given type to find the covered annotations.
	 * Does not use subiterators.
	 *
	 * @param <T>
	 *            the required annotation type to be retrieved.
	 * @param jCas
	 *            a JCas containing the annotation.
	 * @param type
	 *            the required annotation type to be retrieved.
	 * @param coveringAnnotation
	 *            the covering annotation.
	 * @return the single instance of the required type.
	 * @throws IllegalArgumentException if not exactly one instance if the given type is present
	 * 		   under the covering annotation.
	 * @see Subiterator
	 * 
	 * @author Ofer Bronstein
	 * @since April 2014
	 */
	public static <T extends Annotation> T selectCoveredSingle(final Class<T> type,	AnnotationFS coveringAnnotation) throws CASException {
		return selectCoveredSingle(coveringAnnotation.getCAS().getJCas(), type, coveringAnnotation);
	}

	/**
	 * Get the annotation of the given annotation type constrained by a 'covering' annotation.
	 * Iterates over all annotations of the given type to find the covered annotations.
	 * Does not use subiterators.
	 *
	 * @param cas
	 *            a cas containing the annotation.
	 * @param type
	 *            the required annotation type to be retrieved.
	 * @param coveringAnnotation
	 *            the covering annotation.
	 * @return the single instance of the required type.
	 * @throws IllegalArgumentException if not exactly one instance if the given type is present
	 * 		   under the covering annotation.
	 * @see Subiterator
	 * 
	 * @author Ofer Bronstein
	 * @since April 2014
	 */
	public static AnnotationFS selectCoveredSingle(CAS cas, Type type, AnnotationFS coveringAnnotation) {
		List<AnnotationFS> annotations = CasUtil.selectCovered(cas, type, coveringAnnotation);
		
		if (annotations.isEmpty()) {
			throw new IllegalArgumentException("No annotations of type [" + type.getName() + "] in selected range");
		}
		if (annotations.size() > 1)  {
			throw new IllegalArgumentException("More than one annotation of type [" + type.getName()
					+ "] in selected range");
		}
		
		return annotations.get(0);
	}

	public static <A extends Annotation> String annotationToString(A anno) {
		return String.format("%s[%s:%s]", anno.getCoveredText(), anno.getBegin(), anno.getEnd());
	}

	public static String annotationToString(Token token) {
		return annotationToString(token, true, true);
	}
	
	public static String annotationToString(Token token, boolean writeLemma, boolean writePOS) {
		String strLemma = "";
		if (writeLemma && (token.getLemma() != null && !token.getLemma().getValue().isEmpty())) {
			strLemma = String.format("(%s)", token.getLemma().getValue());
		}
		String strPOS = "";
		if (writePOS && (token.getPos() != null && !token.getPos().getPosValue().isEmpty())) {
			strPOS = String.format("/%s", token.getPos().getPosValue());
		}
		return String.format("%s%s%s[%s:%s]", token.getCoveredText(), strLemma, strPOS, token.getBegin(), token.getEnd());
	}
	
	public static String annotationToString(Dependency dep) {
		Token dependent = dep.getDependent();
		Token governer = dep.getGovernor();
		return String.format("%s(%s->%s)", dep.getDependencyType(),
				annotationToString(dependent, false, false), annotationToString(governer, false, false));
	}
	
	public static <A extends Annotation> String annotationIterableToString(Iterable<A> annos) {
		List<String> strs = new ArrayList<String>();
		for (A anno : annos) {
			String annoStr;
			if (anno instanceof Token) {
				annoStr = annotationToString((Token) anno);
			}
			else if (anno instanceof Dependency) {
				annoStr = annotationToString((Dependency) anno);
			}
			else {
				annoStr = annotationToString(anno);
			}
			strs.add(annoStr);
		}
		return String.format("[%s]", StringUtils.join(strs, ", "));
	}

	public static <A extends Annotation> String annotationCollectionToString(Collection<A> annos) {
		return String.format("%s/%s", annos.size(), annotationIterableToString(annos));
	}

	/**
	 * Calculates an annotation's hash code only by its type and span (begin..end).
	 * This is useful for times when you can assume that only one annotation of a certain type
	 * will be on a specific span (when that's not the case you'll get the same hash code
	 * for multiple distinct annotations, so don't use this!).
	 * 
	 * @param anno
	 * @return
	 */
	public static int hashCodeAnnotationByTypeAndSpan(Annotation anno) {
	     return new HashCodeBuilder(131, 79).append(anno.getType()).append(anno.getBegin()).append(anno.getEnd()).toHashCode();
	}

	/**
	 * Check whether two annotations are equals only by their type and span (begin..end).
	 * This is useful for times when you can assume that only one annotation of a certain type
	 * will be on a specific span (when that's not the case you'll get equality
	 * for multiple distinct annotation, so don't use this!).
	 * 
	 * @param anno
	 * @return
	 */
	public static boolean equalsAnnotationByTypeAndSpan(Object obj1, Object obj2) {
		if (obj1 == null && obj2 == null) { return true; }
		if (obj1 == null || obj2 == null) { return false; }
		if (obj1 == obj2) { return true; }
		if (obj1.getClass() != obj2.getClass()) { return false; }
		Annotation anno1 = (Annotation) obj1;
		Annotation anno2 = (Annotation) obj2;
		return new EqualsBuilder().append(anno1.getBegin(), anno2.getBegin()).append(anno1.getEnd(), anno2.getEnd()).isEquals();
	}
}
