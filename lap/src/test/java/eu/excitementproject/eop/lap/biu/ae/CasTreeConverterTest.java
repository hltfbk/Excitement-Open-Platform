package eu.excitementproject.eop.lap.biu.ae;

import java.io.File;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.common.utilities.log4j.LoggerUtilities;


@Ignore("Environment doesn't support yet storing model files + running easyfirst")
public class CasTreeConverterTest {
	
	/**
	 * Initialize log4j. Taken from {@link eu.excitementproject.eop.biutee.utilities.LogInitializer}.
	 */
	@BeforeClass
	public static void beforeClass() {
		final String LOG4J_PROPERTIES = "log4j.properties";
		
		// Use the file log4j.properties to initialize log4j
		PropertyConfigurator.configure(LOG4J_PROPERTIES);
		
		// Pick the logger, and start writing log messages
		logger = Logger.getLogger(CasTreeConverter.class);
		
		// Register the log-file(s) (if exist(s)) as file(s) to be saved by ExperimentManager.
		for (Appender appender : LoggerUtilities.getAllAppendersIncludingParents(logger))
		{
			// cannot avoid RTTI, since current implementation of log4j provides
			// no other alternative.
			if (appender instanceof FileAppender)
			{
				File file = new File(((FileAppender)appender).getFile());
				ExperimentManager.getInstance().register(file);
			}
		}
	}

	@Test
	public void test1() throws Exception {
		CasTreeConverterTester.testConverter(BIU_LAP_Test.TEXT);
	}

	@Test
	public void test2() throws Exception {
		// Two duplicated nodes
		CasTreeConverterTester.testConverter("The news which Ken likes to eat apples stopped being interesting.");
	}
	
	@Test
	public void test3() throws Exception {
		// same node ("that") duplicated twice!
		CasTreeConverterTester.testConverter("The boy that wanted to leave, was found.");
	}
	
	@Test
	public void test4() throws Exception {
		// Sibling extra nodes
		CasTreeConverterTester.testConverter("Ken wants to buy apples which will be fun.");
	}
	
	@Test
	public void test5() throws Exception {
		// Simple sentence, no antecedents
		CasTreeConverterTester.testConverter("Another turning point, a fork stuck in the road.");
	}
	
	private static Logger logger = Logger.getLogger(CasTreeConverter.class);
}
