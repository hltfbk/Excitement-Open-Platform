package eu.excitementproject.eop.util.runner;

import org.junit.*;

// test for German with EditDistanceEDA
public class TestRunner_Scorer {

	@Ignore
	@Test
	public void test() {
		String[] cmd = {
				"-config", "../core/src/test/resources/configuration-file/EditDistanceEDA_DE.xml",
		        "-train",
		        "-trainFile","../core/src/test/resources/data-set/GER/German_dev_small.xml",
		        "-test",
		        "-testFile","../core/src/test/resources/data-set/GER/German_test_small.xml",
		        "-output","../core/src/test/resources/results/"};
		/*
		String[] cmd = {
                "-config", "./src/test/resources/configuration-file/demo_config_test_maxent_DE.xml",
                // "-train",
                "-trainFile","./src/test/resources/data-set/de_demo_dev.xml",
                // "-test"
                //"-testFile","./src/test/resources/data-set/de_demo_test.xml",
                // "-testFile","./src/test/resources/data-set/de_demo_test.xml",
                // "-output","./src/test/resources/results/",
                //                "-results","./src/test/resources/results/EditDistanceEDA_DE.xml_results.xml",
                // "-score"
		};
		*/	
		try {
			EOPRunner runner = new EOPRunner(cmd);
			runner.run();
		} catch (Exception e) {
			System.out.println("Demo test failed! Command arguments: " + cmd.toString());
			e.printStackTrace();
		}
		
	}
}
