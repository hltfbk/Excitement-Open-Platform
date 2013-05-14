package eu.excitementproject.eop.lap.dkpro;

import static org.junit.Assert.fail;

import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;

public class MaltParserDeTest {
	
	@Test
	public void test() {		
		LAPAccess lap = null; 
		JCas aJCas = null; 

		// Generating a Single CAS 
		try {
			// linear test 
			lap = new MaltParserDE(); // same as default, which is linear
			
			// one of the LAPAccess interface: that generates single TH CAS. 
			aJCas = lap.generateSingleTHPairCAS("Freiheit und Leben kann man uns nehmen, die Ehre nicht", "Otto Wels hat das gesagt."); 

			
			//PlatformCASProber.probeCas(aJCas, System.out); 
			
			// poly model test 
			// does't work for German!
//			HashMap<String,String> m = new HashMap<String,String>(); 
//			m.put("PARSER_MODEL_VARIANT", "poly"); 
//			lap = new MaltParserDE(m); 
//			aJCas = lap.generateSingleTHPairCAS("Freiheit und Leben kann man uns nehmen, die Ehre nicht", "Otto Wels hat das gesagt."); 
		
			//PlatformCASProber.probeCas(aJCas, System.out); 
		}
		catch(LAPException e)
		{
			fail(e.getMessage()); 
		}
		
		try {
			JCas textCas = aJCas.getView("TextView");
			JCas hypoCas = aJCas.getView("HypothesisView");
		for (Dependency dep : JCasUtil.select(textCas, Dependency.class)) {
			System.out.println(dep.getGovernor().getCoveredText() + " -" + dep.getDependencyType() + "-> " + dep.getDependent().getCoveredText());
		}
		for (Dependency dep : JCasUtil.select(hypoCas, Dependency.class)) {
			System.out.println(dep.getGovernor().getCoveredText() + " -" + dep.getDependencyType() + "-> " + dep.getDependent().getCoveredText());
		}
		
		} catch (Exception e) {
			
		}
	}

}
