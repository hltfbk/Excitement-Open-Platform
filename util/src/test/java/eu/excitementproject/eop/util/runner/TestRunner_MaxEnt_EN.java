package eu.excitementproject.eop.util.runner;

import org.junit.*;

//test for English with MaxEnt
public class TestRunner_MaxEnt_EN {

	@Ignore
	@Test
	public void test() {
		String[] cmd = {"-config", "../core/src/test/resources/configuration-file/MaxEntClassificationEDA_Base+OpenNLP_EN.xml",
		        "-train",
		        "-trainFile","../core/src/test/resources/data-set/ENG/English_dev_small.xml",
		        "-test",
		        "-testFile","../core/src/test/resources/data-set/ENG/English_test_small.xml",
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
