package eu.excitementproject.eop.biutee;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.excitementproject.eop.biutee.rteflow.systems.excitement.BiuteeMain;
import eu.excitementproject.eop.lap.biu.test.BiuTestUtils;

/**
 * Tests for BIUTEE in EOP. Runs full BIUTEE.<BR>
 * Should be run from the <tt>workdir</tt> folder in the BIUTEE distribution.
 * 
 * @author Ofer Bronstein
 * @since May 2013
 */
public class TestBiuteeUsage {
	
	@BeforeClass
	public static void prepareBiuteeRun() throws IOException {
		// Run tests only under BIU environment
		BiuTestUtils.assumeBiuEnvironment();
	}

	/**
	 * Run full BIUTEE: LAP for training, training, LAP for testing, testing.
	 * @throws Throwable 
	 */
	@Test
	public void runFullBiutee() throws Throwable{
		BiuteeMain.runBiuteeCustomFlow(CONFIG_FILE_PATH, "full");
	}
	
	public static final String CONFIG_FILE_PATH = "./biutee.xml";
}
