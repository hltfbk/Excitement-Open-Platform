package eu.excitementproject.eop.gui;

import org.junit.*;

public class TestDemo_EditDistance_DE {

	@Ignore("it produces an error when it tries to look for some lemmas in the GermaNet")
	@Test
	public void test() {
		String[] cmd = {
						"-config", "../core/src/main/resources/configuration-file/EditDistanceEDA_Wordnet_DE.xml",
						//"-config", "../core/src/main/resources/configuration-file/EditDistanceEDA_NonLexRes_DE.xml",

				        "-train",
				        "-trainFile","./src/test/resources/data-set/de_demo_dev.xml",
				        "-test",
				        //"-testFile","./src/test/resources/data-set/de_demo_test.xml",
				        "-testFile","./src/test/resources/data-set/de_demo_test.xml",
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
