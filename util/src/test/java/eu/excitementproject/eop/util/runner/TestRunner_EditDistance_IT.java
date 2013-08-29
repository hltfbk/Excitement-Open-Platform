package eu.excitementproject.eop.util.runner;

import org.junit.*;

//test for Italian with EditDistanceEDA
public class TestRunner_EditDistance_IT {

	@Ignore
	@Test
	public void test() {
		String[] cmd = {
				        "-config", "../core/src/main/resources/configuration-file/EditDistanceEDA_IT.xml",
				        "-train",
				        "-trainFile","./src/test/resources/data-set/it_demo_dev.xml",
				        "-test",
				        "-testFile","./src/test/resources/data-set/it_demo_test.xml",
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
