package eu.excitementproject.eop.biutee;

import java.io.File;
import java.io.IOException;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.excitementproject.eop.biutee.rteflow.systems.excitement.ExcitementToBiuConfigurationFileConverter.ExcitementToBiuConfigurationFileConverterException;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEMainReaderException;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;
import eu.excitementproject.eop.lap.biu.test.BiuTestUtils;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Tests for BIUTEE in EOP. Runs full BIUTEE.<BR>
 * Should be run from the <tt>workdir</tt> folder in the BIUTEE distribution.
 * 
 * @author Ofer Bronstein
 * @since May 2013
 */
public class TestBiuteeUsage {
	
	@BeforeClass
	public static void prepareBiuteeRun() {
		// Run tests only under BIU environment
		BiuTestUtils.assumeBiuEnvironment();
	}

	/**
	 * Run full BIUTEE: LAP for training, training, LAP for testing, testing.
	 */
	@Test
	public void runFullBiutee() throws EDAException, ComponentException, ConfigurationException, ConfigurationFileDuplicateKeyException, TeEngineMlException, BiuteeMainException, IOException, ExcitementToBiuConfigurationFileConverterException, eu.excitementproject.eop.common.utilities.configuration.ConfigurationException, RTEMainReaderException, ParserRunException, SentenceSplitterException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException, NamedEntityRecognizerException, TreeStringGeneratorException{
		BiuteeMain.runBiuteeCustomFlow(System.getProperty(CONFIG_PROP_NAME), "full");
	}
	
	public static final String CONFIG_PROP_NAME = "config";
}
