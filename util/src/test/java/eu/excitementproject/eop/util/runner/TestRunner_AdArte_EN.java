package eu.excitementproject.eop.util.runner;

import org.junit.*;

public class TestRunner_AdArte_EN {

	@Ignore
	@Test
	public void test() {
		String[] cmd = {
				        "-config", "../adarte/src/test/resources/configuration-file/AdArte_EN.xml",
//				        "-train",
//					    "-lap", "IT",
//				        "-trainFile","../adarte/src/test/resources/dataset/SICK_EN_EXAMPLE.xml",
//				        "-test",
//				        "-testFile","../adarte/src/test/resources/dataset/SICK_EN_EXAMPLE.xml,
//				        "-output","../core/src/test/resources/results/"
				        };
		/*
		String[] cmd = {
                "-config", "../core/src/main/resources/configuration-file/EditDistanceEDA_IT.xml",
                "-train",
                "-trainFile","./src/test/resources/data-set/it_demo_dev.xml",
                "-test",
                "-testFile","./src/test/resources/data-set/it_demo_test.xml",
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
