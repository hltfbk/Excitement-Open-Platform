package eu.excitementproject.eop.lap.textpro;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.*;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.*;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.*;

import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * LAP using TextPro
 * 
 * TextPro must be installed, which means there is an environment variable
 * that has as value the location of the TextPro installation. The system reads 
 * this environment variables, and makes the system call to TextPro, and then
 * uploads the annotation from the text file TextPro produces into the CAS
 * 
 * 
 * @author Vivi Nastase (FBK)
 *
 */
public class LAP_TextPro extends LAP_ImplBase {

	/**
	 * Command line option that specifies the annotations we want from TextPro 
	 */
	protected String CMD = "-y -no_abstract_lemma -c token+tokenstart+sentence+pos+entity+lemma"; 
	protected String TXP_LANGUAGE = "ita";

	protected TextProHandler txp = null;
	protected TextProAnnotation txpAnn = null;
	
	/**
	 * Part-of-speech mapping from the TextPro set to the DKPro set
	 */
	private String TXPAnnotMapFile = "/TextPro/it-tagger.map";
			
	protected HashMap<String,String> AnnotMap = null; 
	
	/**
	 * Constructor
	 * Default language is Italian
	 * 
	 * @throws LAPException
	 */
	public LAP_TextPro() throws LAPException {

		super();
		setLanguage("IT");
		initializeTXP();
	}
	
	/**
	 * Constructor
	 * 
	 * @param language -- sets the language for TextPro (it can process Italian and English)
	 * @throws LAPException
	 */
	public LAP_TextPro(String language) throws LAPException {
		super();
		setLanguage(language);
		initializeTXP();
	}

	private void initializeTXP() throws LAPException{
		try {
			if (txp == null) {
				txp = new TextProHandler();
			}
		} catch(Exception e) {
			throw new LAPException(e.getMessage());
		}
		
		loadTXPAnnotMap();
	}
	
	/**
	 * This should map the platform language parameter and the TextPro language parameter
	 * @param language
	 */
	private void setLanguage(String language) {
		languageIdentifier = language;
		if (languageIdentifier.matches("IT")) {
			TXP_LANGUAGE = "ita";
		} else {
			TXP_LANGUAGE = languageIdentifier.toLowerCase();
		}
	}
	

	@Override
	public void addAnnotationOn(JCas aJCas, String viewName)
			throws LAPException {
		
		try {
			String text = aJCas.getView(viewName).getDocumentText();
			txpAnn = new TextProAnnotation(text, txp.getAnalysis(text, "-l " + TXP_LANGUAGE + " " + CMD));
			
//			System.out.println("Adding annotations for text: " + text);
			
			for(String type: txpAnn.getAnnotationTypes()) {
				addTXPAnnotations(aJCas, viewName, type);
			}
			
//			PrintAnnotations.printAnnotations(aJCas.getView(viewName).getCas(), System.out); 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new LAPException("Error processing " + viewName );
		}
	}
	
	
	private void addTXPAnnotations(JCas aJCas, String viewName, String type) {
		Annotation a;
		
		if (txpAnn.hasAnnotation(type)) {
			try{
				String str = "";
			
				HashMap<Integer[],String> annotation = txpAnn.getAnnotation(type);
				
				for(Integer[] position: annotation.keySet()) {
					
					str = txpAnn.getString(position,type);
										
					if (position[0] >= 0 && position[1] >= 0) {
						a = getAnnotationObject(aJCas, viewName, type, position);
						a.setBegin(position[0]);
						a.setEnd(position[1]);
						if (type.matches(txpAnn.lemma)) { 
							((Lemma) a).setValue(annotation.get(position));
						} else {
							if (type.matches(txpAnn.ne)) {
								((NamedEntity) a).setValue(txpAnn.getNEtype(str));							
							} else {
								if (type.matches(txpAnn.pos)) {
									((POS) a).setPosValue(annotation.get(position));
								}
							}
						}
						a.addToIndexes();
					
						addLinks(a, aJCas, viewName, type);
					}
				}
			} catch (Exception e) {
				System.out.println("Error adding " + type + " annotations (" + viewName + ")" );
				e.printStackTrace();
			}
		}
	}
	
	
	private Annotation getAnnotationObject(JCas aJCas, String viewName, String type, Integer[] index) throws Exception{
		
		if (type.matches(txpAnn.token))
			return new Token(aJCas.getView(viewName));
		if (type.matches(txpAnn.lemma))
			return new Lemma(aJCas.getView(viewName));
		if (type.matches(txpAnn.pos))
			return getAnnotationObject(aJCas, viewName, txpAnn.getAnnotation(type).get(index), POS.class);
		if (type.matches(txpAnn.sentence))
			return new Sentence(aJCas.getView(viewName));
		if (type.matches(txpAnn.ne))
			return getAnnotationObject(aJCas, viewName, txpAnn.getNEtype(index), NamedEntity.class);
		
		return new Annotation(aJCas.getView(viewName));
	}
	
	
	private Annotation getAnnotationObject(JCas aJCas, String viewName, String txpAnnotValue, Class<?> defaultClass) throws Exception{
		
		if (AnnotMap.containsKey(txpAnnotValue)) {
			Class<?> txpAnnotClass = Class.forName(AnnotMap.get(txpAnnotValue));
			Constructor<?> txpAnnotClassConstr = txpAnnotClass.getConstructor(JCas.class);
			return (Annotation) txpAnnotClassConstr.newInstance(aJCas.getView(viewName));
		}
		
		Constructor<?> txpAnnotClassConstr = defaultClass.getConstructor(JCas.class);
		return (Annotation) txpAnnotClassConstr.newInstance(aJCas.getView(viewName));
	}
		
	/**
	 * Add links from lemmas/pos to tokens, 
	 * @param a	annotation 
	 * @param aJCas	JCas
	 * @param viewName test/hypothesis
	 * @param type	annotation type (token/lemma/pos/...)
	 */
	public void addLinks(Annotation a, JCas aJCas, String viewName, String type) {
		if (type.matches(txpAnn.lemma) || type.matches(txpAnn.pos)) {
			linkToToken(a, aJCas, viewName, type, Token.type, null);
		} else {
			if (type.matches(txpAnn.token)) {
				linkToToken(a, aJCas, viewName, type, Lemma.type, txpAnn.lemma);
				linkToToken(a, aJCas, viewName, type, POS.type, txpAnn.pos);
			}
		}
		
	}
	
	public void linkToToken(Annotation a, JCas aJCas, String viewName, String type, int featureType, String typeToAdd){

		try {
			AnnotationIndex<Annotation> annIndex = aJCas.getView(viewName).getAnnotationIndex(featureType);
			Iterator<Annotation> annIter = annIndex.iterator();
			Boolean found = false;
				
			while(annIter.hasNext() && !found)
			{
				Annotation curr = annIter.next();
	    	
				if ( (curr.getBegin() == a.getBegin()) 
						&& (curr.getEnd() == a.getEnd())) {
	    		
//	    			aJCas.removeFsFromIndexes(curr);
	    		
					if (type.matches(txpAnn.lemma)) {
						((Token) curr).setLemma((Lemma) a);
					} else {
						if (type.matches(txpAnn.pos)) {
							((Token) curr).setPos((POS) a);
						} else {
							if (type.matches(txpAnn.token)) {
								if (typeToAdd.matches(txpAnn.lemma)) {
									((Token) a).setLemma((Lemma) curr);
								} else {
									if (typeToAdd.matches(txpAnn.pos)) {
										((Token) a).setPos((POS) curr);
									}
								}
							}
						}
					}
	    		
//	    			curr.addToIndexes(aJCas);
				}	    	
			}	 
		} catch (Exception e) {
			System.err.println("Problems linking other annotations to tokens for annotation type " + a.getClass().toString() + " / " + a.getCoveredText());
			e.printStackTrace();
		}
	}

	/**
	 * loads the mapping between TextPro annotations (POS and NE types for Italian!) and the POS/NE types types generated based on DKpro 
	 */
	private void loadTXPAnnotMap(){
		AnnotMap = new HashMap<String,String>();
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader(this.getClass().getResourceAsStream(TXPAnnotMapFile)));
			String line = null;
			Pattern posLine = Pattern.compile("^\\s*(.*?)\\s*=\\s*(.*)");
			Matcher matcher;
			while ((line = reader.readLine()) != null) {
				matcher = posLine.matcher(line);
				if (matcher.matches()) {
					AnnotMap.put(matcher.group(1), matcher.group(2));
//					System.out.println("\t/" + matcher.group(1) + " = " + matcher.group(2) + "/");
				}
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error loading POS map from file " + TXPAnnotMapFile);
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		
		try {
			LAP_TextPro ltp = new LAP_TextPro();
			
			System.out.println("TEXTPRO path: " + ltp.txp.TEXTPRO);
//			System.out.println("YAMCHA_HOME path: " + ltp.txp.YAMCHA_HOME);
			
			System.out.println("Text pro arguments: " + "-l " + ltp.TXP_LANGUAGE + " " + ltp.CMD);

			String testFile = "src/test/resources/small_it.xml";
			ltp.processRawInputFormat(new File(testFile), new File("./"));			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
