package eu.excitementproject.eop.gui;

import org.junit.Test;

public class TestDemo_EditDistance_EN_singlePair {

	@Test
	public void test() {
		String[] cmd = {"-config", "../core/src/main/resources/configuration-file/EditDistanceEDA_Wordnet_EN.xml",
				        "-train",
				        "-trainFile","./src/test/resources/data-set/en_demo_dev.xml",
				        "-test",
				        "-text","Hubble is a telescope.",
				        "-hypothesis","Hubble is an instrument.",
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
