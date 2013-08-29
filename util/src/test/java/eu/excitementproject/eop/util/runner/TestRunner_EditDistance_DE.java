package eu.excitementproject.eop.util.runner;

import org.junit.*;

// test for German with EditDistanceEDA
public class TestRunner_EditDistance_DE {

	@Ignore
	@Test
	public void test() {
		String[] cmd = {
						"-config", "./src/test/resources/configuration-file/demo_config_test_maxent_DE.xml",
				        "-train",
				        "-trainFile","./src/test/resources/data-set/de_demo_dev.xml",
				        "-test",
				        //"-testFile","./src/test/resources/data-set/de_demo_test.xml",
				        "-testFile","./src/test/resources/data-set/de_demo_test.xml",
				        "-output","./src/test/resources/results/",
				        "-score"};
		try {
			EOPRunner runner = new EOPRunner(cmd);
			runner.run();
		} catch (Exception e) {
			System.out.println("Demo test failed! Command arguments: " + cmd.toString());
			e.printStackTrace();
		}
		
	}
}
