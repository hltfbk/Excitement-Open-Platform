package eu.excitementproject.eop.gui;

import org.junit.*;

//test for Italian with MaxEnt
public class TestDemo_MaxEnt_IT {

//	@Ignore
	@Test
	public void test() {
		// needs TextPro to be installed
		String[] cmd = {"-config", "./src/test/resources/configuration-file/demo_config_test_maxent_IT.xml",
//				        "-train",
//				        "-trainFile","./src/test/resources/data-set/it_demo_dev.xml",
				        "-test",
				        "-testFile","./src/test/resources/data-set/it_demo_test.xml",
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
