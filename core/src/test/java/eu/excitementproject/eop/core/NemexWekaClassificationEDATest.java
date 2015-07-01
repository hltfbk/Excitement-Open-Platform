package eu.excitementproject.eop.core;
import java.io.File;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.core.ClassificationTEDecision;
import eu.excitementproject.eop.core.NemexWekaClassificationEDA;
import eu.excitementproject.eop.lap.PlatformCASProber;

public class NemexWekaClassificationEDATest {

	static Logger logger = Logger
			.getLogger(NemexWekaClassificationEDATest.class.getName());

	@Test
	public void test() {

		NemexWekaClassificationEDA nemexWekaEDA = new NemexWekaClassificationEDA();

		try {

			File configFile = new File(
					"./src/test/resources/configuration-file/NemexWekaClassificationEDA_EN.xml");
			File testDir = new File("./src/test/resources/data-set/ENG/test/");

			CommonConfig config = new ImplCommonConfig(configFile);

			// training
			long startTime = System.currentTimeMillis();
			nemexWekaEDA.startTraining(config);
			long endTime = System.currentTimeMillis();
			logger.info("Training Time:" + (endTime - startTime) / 1000);
			nemexWekaEDA.shutdown();

			// testing
			nemexWekaEDA = new NemexWekaClassificationEDA();
			nemexWekaEDA.initialize(config);

			startTime = System.currentTimeMillis();
			for (File xmi : (testDir.listFiles())) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				JCas cas = PlatformCASProber.probeXmi(xmi, null);
				ClassificationTEDecision teDecision1 = nemexWekaEDA
						.process(cas);
				System.out.println("Classification decision: "
						+ teDecision1.getDecision() + " with confidence: "
						+ teDecision1.getConfidence());
			}
			endTime = System.currentTimeMillis();
			logger.info("Testing Time:" + (endTime - startTime) / 1000);

			nemexWekaEDA.shutdown();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
}
