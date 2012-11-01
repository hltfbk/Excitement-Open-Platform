package eu.excitementproject.eop.lap.lappoc;

import java.io.File;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.PlatformCASProber;
//import java.io.InputStream;
//import java.net.URL;

public class UsageExampleTZ
{

    public static void main(String[] args)
        throws Exception
    {
        LAPAccess lap = new ExampleLAPTZ();
        JCas jcas = lap.generateSingleTHPairCAS("This is Something.", "This is something else.");

        PlatformCASProber.probeCas(jcas, System.out);

        File input = new File("src/test/resources/small.xml");
        File outputDir = new File("target/");
        lap.processRawInputFormat(input, outputDir);

        File testXmi = new File("target/3.xmi");
        PlatformCASProber.probeXmi(testXmi, System.out);
    }
}