package eu.excitementproject.eop.gui;

import org.junit.*;

//test for English with MaxEnt
public class TestDemo_MaxEnt_EN {

	@Ignore
	@Test
	public void test() {
		String[] cmd = {"-config", "./src/test/resources/configuration-file/demo_config_test_maxent_EN.xml",
				        "-train",
				        "-trainFile","./src/test/resources/data-set/en_demo_dev.xml",
						"-lap","opennlp",
				        "-test",
				        "-testFile","./src/test/resources/data-set/en_demo_test.xml",
				        "-output","./src/test/resources/results/"};
		try {
			Demo demo = new Demo(cmd);
			demo.run();
		} catch (Exception e) {
			System.out.println("Demo test failed! Command arguments: " + cmd.toString());
			e.printStackTrace();
		}
		
	}
}
