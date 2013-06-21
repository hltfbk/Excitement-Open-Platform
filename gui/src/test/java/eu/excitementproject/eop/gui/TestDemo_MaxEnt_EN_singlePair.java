package eu.excitementproject.eop.gui;

import org.junit.*;

public class TestDemo_MaxEnt_EN_singlePair {

	@Ignore
	@Test
	public void test() {
		String[] cmd = {"-config", "./src/test/resources/configuration-file/demo_config_test_maxent_EN.xml",
//				        "-train",
//				        "-trainFile","./src/test/resources/data-set/en_demo_dev.xml",
				        "-test",
				        "-text","Hubble is a telescope",
				        "-hypothesis","Hubble is an instrument",
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
