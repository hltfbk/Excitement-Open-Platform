package eu.excitementproject.eop.lap.biu.test;

import java.io.File;
import java.io.IOException;

import org.junit.Assume;

/**
 * Contains static methods used for BIU tests.
 * 
 * @TODO This class is located in src/main and not src/test, since it is not trivial to export
 * code from src/test to other Maven modules, and this class is used also in other projects
 * (e.g. eop/biutee). It may be a good idea to try to succeed making it available also from src/test
 * (seems like it requires some pom/jar manipulations).
 * 
 * @author Ofer Bronstein
 * @since May 2013
 */
public class BiuTestUtils {

	/**
	 * Checks whether the current test is run from within a BIU environment.
	 * Currently it only checks we are running from a folder with a name starting in "workdir"
	 * (which is the convention in the BIUTEE environment). In the future more condition
	 * may be add here - like verifying that EasyFirst parser server is running.<br>
	 * <br>
	 * This method must be called in a @BeforeClass method in any test class that should
	 * be run only in a BIU environment. This method uses junit's Assume functionality,
	 * meaning that when the test will be run not in a BIU environment, the test will be
	 * silently skipped and not fail the entire test suite.
	 */
	public static void assumeBiuEnvironment() throws IOException {
		String workingFolderName = new File(".").getCanonicalFile().getName();
		boolean isInBiuteeEnv = workingFolderName.toLowerCase().startsWith("workdir");
		if (!isInBiuteeEnv) {
			System.err.printf("Skipping test since we are not in BIUTEE env (current folder name does not start with 'workdir')");
		}
		Assume.assumeTrue(isInBiuteeEnv);

	}

}
