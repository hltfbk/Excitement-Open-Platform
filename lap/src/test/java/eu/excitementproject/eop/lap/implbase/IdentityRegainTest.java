package eu.excitementproject.eop.lap.implbase;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import eu.excitement.type.alignment.Target;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.dkpro.OpenNLPTaggerEN;


import org.uimafit.util.JCasUtil;



public class IdentityRegainTest {

	public static void main(String args[])
	{
		try {
			LAPAccess lap = new OpenNLPTaggerEN(); 
			JCas aJCas = lap.generateSingleTHPairCAS("hello", "world"); 
			
			Token tokenBefore = new Token(aJCas); 
			Lemma lemmaBefore = new Lemma(aJCas); 
			
			lemmaBefore.setBegin(0); 
			lemmaBefore.setEnd(3);
			lemmaBefore.setValue("dog"); 
			tokenBefore.setBegin(0); 
			tokenBefore.setEnd(3); 
			tokenBefore.setLemma(lemmaBefore); 
			
			Annotation a = tokenBefore; 
			Type t = a.getType(); 
			System.out.println(t.getName());
			System.out.println("Number of features: " + t.getNumberOfFeatures());
			Feature f = a.getType().getFeatureByBaseName("lemma"); 
			System.out.println("Feature name: " + f.getShortName() + " (range: " + f.getRange() + ")"); 
			FeatureStructure fs = a.getFeatureValue(f); 
			System.out.println("FeatureStructure as string: " + fs.getStringValue(fs.getType().getFeatureByBaseName("value"))); 
			
			Target tr = new Target(aJCas);  
			FSArray fsarr= tr.getTargetAnnotations(); 
			fsarr = new FSArray(aJCas, 5); 
			fsarr.set(0, lemmaBefore); 
			fsarr.set(1, tokenBefore); 
			System.out.println("size of fsarray: "+ fsarr.size()); 
			//select(fsarr,Token.class); 
			//JCasUtil.select(aJCas, Token.class);
			for (Token token : JCasUtil.select(fsarr, Token.class))
			{
				System.out.println("Token got from FSArray has "); 
				System.out.println("lemma value with: " + token.getLemma().getValue()); 
			}
			
		} catch (Exception e)
		{
			System.out.println(e.getMessage()); 
		}
	}
}
