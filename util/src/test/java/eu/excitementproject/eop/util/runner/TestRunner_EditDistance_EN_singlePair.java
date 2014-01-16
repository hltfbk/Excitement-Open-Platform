package eu.excitementproject.eop.util.runner;

import org.junit.*;

//test for English with EditDistanceEDA
public class TestRunner_EditDistance_EN_singlePair {

	@Ignore
	@Test
	public void test() {
		String[] cmd = {
		        "-config", "../core/src/test/resources/configuration-file/EditDistanceEDA_EN.xml",
		        "-train",
		        "-trainFile","../core/src/test/resources/data-set/ENG/English_dev_small.xml",
		        "-test",
		        //"-text","Hubble is a telescope.",
		        //"-hypothesis","Hubble is an instrument.",
		        "-testFile","../core/src/test/resources/data-set/ENG/English_test_small.xml",
		        "-output","../core/src/test/resources/results/"};
		/*
		String[] cmd = {"-config", "./src/main/resources/configuration-file/EditDistanceEDA_EN.xml",
                "-train",
                "-trainFile","./src/test/resources/data-set/en_demo_dev.xml",
                "-test",
				//"-text","Hubble is a telescope.",
				//"-hypothesis","Hubble is an instrument.",
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
