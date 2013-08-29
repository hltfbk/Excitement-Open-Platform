package eu.excitementproject.eop.util.runner;

import org.junit.*;

//test for English with MaxEnt (one pair)
public class TestRunner_MaxEnt_EN_singlePair {

	@Ignore
	@Test
	public void test() {
		String[] cmd = {"-config", "./src/test/resources/configuration-file/demo_config_test_maxent_EN.xml",
				        "-train",
				        "-trainFile","./src/test/resources/data-set/en_demo_dev.xml",
				        "-test",
				        "-text","Hubble is a telescope",
				        "-hypothesis","Hubble is an instrument",
				        "-output","./src/test/resources/results/"};
		try {
			EOPRunner runner = new EOPRunner(cmd);
			runner.run();
		} catch (Exception e) {
			System.out.println("Demo test failed! Command arguments: " + cmd.toString());
			e.printStackTrace();
		}
		
	}
}
