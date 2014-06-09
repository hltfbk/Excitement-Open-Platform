package eu.excitementproject.eop.util.edaexperimenter;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import eu.excitementproject.eop.util.edaexperimenter.experimenter.Experimenter;

@SuppressWarnings("unused")
public class ExperimenterTest {

	@Ignore
	@Test
	public void test() {
		String[] com_args = {"-config", "src/test/resources/configuration-file/MaxEntClassificationEDA_Base_EN.xml", 
							 "-dataDir", "src/test/resources/WP2_public_RTE_pair_data_per_cluster/exp-testing/ENG-DEV/",
							 "-pattern", "Closure.xml",
							 "-split", "mixed",
							 "-xval", "3",
//							 "-balance",
							 "-output", "/tmp/eop-eda-exp/exp-test/",
//							 "-fakeRun"
		};
		
		Experimenter exp = new Experimenter(com_args);
		try {
			exp.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
