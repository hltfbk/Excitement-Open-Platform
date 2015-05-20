package eu.excitementproject.eop.lap.btbpipe;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.junit.Assume;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;

public class BTBPreprocessorBGTest {

	@Test
	public void test() throws IOException {

		LAPAccess lap = null;
		JCas aJCas = null;

		String eopResources = System.getenv("EOP_RESOURCES");
		if (eopResources != null) {
			// let the test pass in case the variable was not set, since it
			// currently used only for this lap

			try {
				lap = new BTBPreprocessorBG();
			} catch (LAPException e) {
				e.printStackTrace();
			}

			Assume.assumeNotNull(lap);

			try {
				String t = "Г-жа Минтън напусна Австралия през 1961 за да учи в Лондон.";
				String h = "Г-жа Минтън е родена в Австралия.";

				// one of the LAPAccess interface: that generates single TH CAS.
				aJCas = lap.generateSingleTHPairCAS(t, h);

				// probeCas check whether or not the CAS has all needed
				// "Entailment" information.
				// If it does not, it raises an LAPException.
				// It will also print the summarized data of the CAS to the
				// PrintStream.
				// If the second argument is null, it will only check the format
				// and raise Exceptions, without printing
				PlatformCASProber.probeCas(aJCas, System.out);

				// To see the full content of each View, use this
				PlatformCASProber.probeCasAndPrintContent(aJCas, System.out);
			} catch (LAPException e) {
				fail(e.getMessage());
			}
		}
	}

}
