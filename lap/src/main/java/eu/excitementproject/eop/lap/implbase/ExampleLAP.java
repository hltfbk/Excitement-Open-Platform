package eu.excitementproject.eop.lap.implbase;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UIMAFramework;
import org.apache.uima.UimaContextAdmin;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.Resource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;

/**
 * 
 * This is an example LAP, that uses LAP_ImplBase. It relies on ImplBase for 
 * all capabilities. It provides the single annotation method 
 * addAnnotationOn(JCas, String); 
 * 
 * <P>
 * Note that addAnnotationOn() of this sample LAP only annotates "Token" by 
 * whitespace string separation. Replace it with a real linguistic component, 
 * and override "Language" identifier, to make it real. 
 * (also, and other metadata if needed). 
 * 
 * <P> As you see in this class, basically, two things should be provided to turn 
 * the LAP_ImplBase abstract class into a working LAP.  
 * <LI> Implementation of abstract method addAnnotationOn() </LI>
 * <LI> languageIdentifier (in the constructor) </LI> 
 *
 * <P> Note that, if you use an AE, you can simply change the AE of this class to 
 * get it done ... (descClasspathName)</P> 
 * 
 * @author Gil
 *
 */
public class ExampleLAP extends LAP_ImplBase implements LAPAccess {

	public ExampleLAP() throws LAPException {
		super(); 
		languageIdentifier = "EN"; // set languageIdentifer, this is needed for generateTHPair from String  		
	}

	@Override
	public void addAnnotationOn(JCas aJCas, String viewName)
			throws LAPException {
		// prepare UIMA context (For "View" mapping), for the AE.  
		UimaContextAdmin rootContext = UIMAFramework.newUimaContext(UIMAFramework.getLogger(), UIMAFramework.newDefaultResourceManager(), UIMAFramework.newConfigurationManager());
		ResourceSpecifier desc = null; 
		try {
			InputStream s = this.getClass().getResourceAsStream(descClasspathName); 
			XMLInputSource input = new XMLInputSource(s, null); 
			desc = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(input);
		}
		catch (InvalidXMLException e) {
			throw new LAPException("AE descriptor is not a valid XML", e);			
		}
		
		//setup sofa name mappings using the api
		HashMap<String,String> sofamappings = new HashMap<String,String>();
		sofamappings.put("_InitialView", viewName);
		UimaContextAdmin childContext = rootContext.createChild("WSSeparator", sofamappings);
		Map<String,Object> additionalParams = new HashMap<String,Object>();
		additionalParams.put(Resource.PARAM_UIMA_CONTEXT, childContext);

		// time to run 
		try {
			//instantiate AE, passing the UIMA Context through the additional parameters map
			AnalysisEngine ae =  UIMAFramework.produceAnalysisEngine(desc,additionalParams);
			// and run the AE 
			ae.process(aJCas); 
		}
		catch (ResourceInitializationException e) {
			throw new LAPException("Unable to initialize the AE", e); 
		} 
		catch (AnalysisEngineProcessException e) {
			throw new LAPException("AE reported back an Exception", e); 
		}
	}
	
	/**
	 * Path to actual "worker AE". So this code is also generic in some sense ... 
	 */
	protected String descClasspathName = "/desc/WSSeparator.xml"; 

}
