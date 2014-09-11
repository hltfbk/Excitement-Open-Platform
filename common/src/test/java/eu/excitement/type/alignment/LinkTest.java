package eu.excitement.type.alignment;

import static org.junit.Assert.*;

import java.util.Set;

import junit.framework.Assert;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.common.utilities.uima.UimaUtils;

public class LinkTest {

	@Test
	public void test() {
		
		// Set Log4J for the test 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);  // for UIMA (hiding < INFO) 
		Logger testlogger = Logger.getLogger(getClass()); 

		try {
			// a small test for group label setter, getter. 
			JCas aJCas = UimaUtils.newJcas(); 
			Link aLink = new Link(aJCas); 
			
			aLink.addGroupLabel(GroupLabelInferenceLevel.LOCAL_ENTAILMENT); 
			aLink.addGroupLabel(GroupLabelInferenceLevel.LOCAL_SIMILARITY); 
			aLink.addGroupLabel(GroupLabelDomainLevel.HYPERNYM); 
			aLink.addGroupLabel(GroupLabelDomainLevel.HYPERNYM); 
			aLink.addGroupLabel(GroupLabelDomainLevel.HYPERNYM); // additional labels would be ignored when you use "getter" (getter returns a set). 

			Set<GroupLabelInferenceLevel> iSet = aLink.getGroupLabelsInferenceLevel(); 
			Set<GroupLabelDomainLevel> dSet = aLink.getGroupLabelsDomainLevel(); 
			
			Assert.assertEquals(2, iSet.size()); 
			testlogger.info(iSet); 
			Assert.assertEquals(1, dSet.size()); 
			testlogger.info(dSet); 
			
			// empty set test 
			aJCas = UimaUtils.newJcas(); 
			aLink = new Link(aJCas); 

			iSet = aLink.getGroupLabelsInferenceLevel(); 
			dSet = aLink.getGroupLabelsDomainLevel(); 
			Assert.assertEquals(0, iSet.size()); 
			Assert.assertEquals(0, dSet.size()); 

		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
	}
}
