package eu.excitementproject.eop.biutee;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.aliasi.util.Files;

import eu.excitementproject.eop.biutee.rteflow.systems.excitement.BiuteeEDA;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.utilities.file.FileFilterByExtension;
import eu.excitementproject.eop.common.utilities.file.FileFilters;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.core.ImplCommonConfig;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.biu.BIUFullLAP;
import eu.excitementproject.eop.lap.biu.ae.ner.StanfordNamedEntityRecognizerAE;
import eu.excitementproject.eop.lap.biu.ae.parser.EasyFirstParserAE;
import eu.excitementproject.eop.lap.biu.ae.postagger.MaxentPosTaggerAE;
import eu.excitementproject.eop.lap.biu.ae.sentencesplitter.LingPipeSentenceSplitterAE;
import eu.excitementproject.eop.lap.biu.ae.tokenizer.MaxentTokenizerAE;

public class TestBiuteeUsage {
	
	@Test
	public void runFullBiutee() throws ConfigurationException, IOException, EDAException, ComponentException {
		
		// Set up environment
		CommonConfig config = new ImplCommonConfig(configuration);
		clearDirectory(lapOutputFolder);

		// Preprocess test data
		BIUFullLAP lap = new BIUFullLAP(config);
		lap.processRawInputFormat(testData, lapOutputFolder);
		
		// Run testing
		BiuteeEDA eda = new BiuteeEDA();
		eda.initialize(config);
		for (File xmi : lapOutputFolder.listFiles(new FileFilters.ExtFileFilter("xmi"))) {
			JCas jcas = PlatformCASProber.probeXmi(xmi, null);
			eda.process(jcas);
		}
	
	}
	
	private void clearDirectory(File path) throws IOException {
		if (!FileUtils.deleteDirectory(path)) {
			throw new IOException("Failed to delete path: " + path.getAbsolutePath());
		}
		path.mkdirs();
	}
	
	private static final File trainData = new File("./dummy.xml");
	private static final File testData = new File("./dummy.xml");
	private static final File configuration = new File("./biutee.xml");
	private static final File lapOutputFolder = new File("./lap_output");
	
	private static final Logger logger = Logger.getLogger(TestBiuteeUsage.class);
}
