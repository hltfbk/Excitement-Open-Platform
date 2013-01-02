package eu.excitementproject.eop.lap;

import java.io.File;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.component.Component;

/**
 * 
 * This interface defines the minimal set of methods that a working LAP pipeline should provide 
 * for EXCITEMENT platform. It defines three types of methods. One for generic annotation, one
 * for single pair online-generation, and one for collection processing of single-pair input 
 * files. Note that this interface extends interface Components. This enables an implementation 
 * of LAPAccess to receive configuration data in the same way to other core components (via 
 * initialize()).
 * 
 * See specification 1.1.1 (or later) "interface LAPAccess". 
 * 
 * @author Gil
 * @since oct 1, 2012 
 */
public interface LAPAccess extends Component {

	/**
	 * This method gets two strings as input and generates a CAS for an EDA. The resulting CAS must 
	 * be a well-formed CAS that contains all types can are necessary for a complete description 
	 * of the entailment problem (i.e. all types from the EXCITEMENT.entailment namespace), as 
	 * defined in Spec 3.3.4, “Additional Types for Textual Entailment”. The amount of linguistic
	 * annotation can differ among implementations (say, EnglishTokenizationOnly(t,h) only returns 
	 * token with TE annotations, while GermanParsingAndNER(t,h) returns tagging, parsing, POS and
	 *  NER, etc).
	 * @param text holds the text string of the single pair TE problem.
	 * @param hypothesis holds the hypothesis string of the problem.
	 * @return a JCas, where the CAS is ready to be used as an input for an EDA.
	 * @throws LAPException
	 */
	public JCas generateSingleTHPairCAS(String text, String hypothesis) throws LAPException; 
	
	
	/**
	 * This method gets two File as arguments. First argument is a single input file that follows 
	 * the raw input XML format (as defined in Section 5.2, “Input file format ”), and the second 
	 * argument outputDir is the output directory. The method returns nothing. Analysis results 
	 * are stored as serialized CAS files in the directory. (standard XMI serialization of UIMA 
	 * CAS). Again, the generated CASes should be well-formed and must contain all types can are 
	 * necessary for a complete description of the entailment problem (i.e. all types from the 
	 * EXCITEMENT.entailment namespace).
	 * 
	 * @param inputFile holds the raw input XML file.
	 * @param outputDir holds the output directory where the resulting CAS XMI should be stored.
	 * @throws LAPException
	 */
	public void processRawInputFormat(File inputFile, File outputDir) throws LAPException; 
	

	/**
	 * The interface is a generic interface that can be called for adding linguistic annotations 
	 * for any data. addAnnotattionOn method does not produce CASes that are ready for EDAs. 
	 * It only adds generic language analysis results to the provided CAS. This method provides 
	 * users with the ability to analyze some text data that is not directly a TE pair.
	 * 
	 * Note that addAnnotationOn exists in two versions: one that applies to the single view 
	 * CAS and one that applies just a specific View within the CAS. The pipeline implementer 
	 * must provide both of them; we recommend to implement addAnnotationOn(JCas) simply as a 
	 * call of addAnnotationOn(JCas,"_InitialView") (single view CAS is a CAS with one view, 
	 * that is implicitly named as "InivitalView"). 
	 * 
	 * addAnnotationOn should not add any type that belongs to EXCITEMENT.entailment.* types
	 * (types related to Entailment.Text, Entailment.Hypothesis, Entailment.Pair, etc).
	 * 
	 * @param aJCas holds the CAS object that will be annotated by the analysis pipeline. aJCas holds two or more view, where one of them
has the view name equal to viewName.
	 * @param viewName holds the name of the view (in aJCas) that will be annotated. 
	 * @return a reference to aJCas -- the same reference to the input aJCas object, which is successfully annotated, if no exception is thrown.   
	 * @throws LAPException
	 */
	public void addAnnotationOn(JCas aJCas, String viewName) throws LAPException; 
	
	/**
	 * Same as addAnnotationOn(JCas, String), but on the single view CAS (_InitialView). 
	 * 
	 * @param aJCas gets the CAS object, where its _InitialView (single view CAS)  will be annotated. 
	 * @return a reference to aJCas -- the same reference to the input aJCas object, which is successfully annotated, if no exception is thrown.
	 * @throws LAPException
	 */
	public void addAnnotationOn(JCas aJCas) throws LAPException; 
	
	
}
