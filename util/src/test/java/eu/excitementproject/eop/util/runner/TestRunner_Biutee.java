package eu.excitementproject.eop.util.runner;

import org.junit.*;

//test for English with MaxEnt
public class TestRunner_Biutee {

	@Ignore
	@Test
	public void test() {

		System.out.println("PATH: " + System.getenv("PATH"));
		
		String[] cmd = {
//				"-config", "./src/test/resources/biutee_demo/biutee_demo.xml",
//				"-config", "../core/src/main/resources/configuration-file/biutee.xml",
				"-config", "./src/test/resources/configuration-file/biutee_2.xml",
		        "-train",
		        "-trainFile","../core/src/test/resources/data-set/ENG/English_dev_small.xml",
		        "-trainDir","/tmp/eop-runs/biutee/train",
		        "-test",
		        "-testFile","../core/src/test/resources/data-set/ENG/English_test_small.xml",
		        "-testDir","/tmp/eop-runs/biutee/test",
		        "-output","../core/src/test/resources/results/",
		        "-score"};
		/*
		String[] cmd = {"-config", "./src/test/resources/configuration-file/demo_config_test_maxent_EN.xml",
				        "-train",
				        "-trainFile","./src/test/resources/data-set/en_demo_dev.xml",
						"-lap","opennlp",
				        "-test",
				        "-testFile","./src/test/resources/data-set/en_demo_test.xml",
				        "-output","./src/test/resources/results/"};
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
