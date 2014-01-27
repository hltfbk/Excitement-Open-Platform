/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.reader;

import java.io.File;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.lap.biu.uima.CasTreeConverter;
import eu.excitementproject.eop.lap.biu.uima.CasTreeConverterException;

/**
 * Implements FileBasedSentenceReader over a UIMA Cas input, where the given corpus is a file/dir of 
 * parsed sentences represented by Cas serialization
 * 
 * @author Meni Adler
 * @since Aug 29 2013
 *
 */
public abstract class UIMASentenceReader<T> extends FileBasedSentenceReader<T> {

	protected static final String DEFAULT_AE_TEMPLATE_FILE ="desc/DummyAE.xml";
	
	public UIMASentenceReader(ConfigurationParams params)  {
		converter = new CasTreeConverter();
		data = null;
		position = -1;
		try {
			this.aeTemplateFile = params.get(Configuration.AE_TEMPLATE_FILE); 
		} catch (ConfigurationException e) {
			this.aeTemplateFile = DEFAULT_AE_TEMPLATE_FILE;			
		}
	}
	
	public UIMASentenceReader() {
		this(DEFAULT_AE_TEMPLATE_FILE);
	}
	
	public UIMASentenceReader(String aeTemplateFile) {
		converter = new CasTreeConverter();
		data = null;
		position = -1;
		this.aeTemplateFile = aeTemplateFile;
	}
	
	@Override
	public void setSource(File source) throws SentenceReaderException {
		try {
			//the following should be replaced with: JCas jcas = UimaUtils.loadXmi(source,aeTemplateFile);
			// After UimaUtils will be part of the EOP
			InputStream s = UIMASentenceReader.class.getResourceAsStream(aeTemplateFile);
			XMLInputSource in = new XMLInputSource(s, null); 
			ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);		
			AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier);
			JCas jcas = ae.newJCas(); 
			FileInputStream inputStream = new FileInputStream(source);
			XmiCasDeserializer.deserialize(inputStream, jcas.getCas()); 
			inputStream.close();
			
			converter.convertCasToTrees(jcas);
			extractData();
		} catch (Exception e) {
			throw new SentenceReaderException(e);
		}
	}

	protected abstract void extractData() throws CasTreeConverterException;

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.SentenceReader#closeSource()
	 */
	@Override
	public void closeSource() throws SentenceReaderException {
	}

	@Override
	public Pair<T, Long> nextSentence() throws SentenceReaderException {
		if (data == null || !data.hasNext())
			return null;
		else
			return new Pair<T,Long>(data.next(),1L);	
	}

	protected CasTreeConverter converter;
	protected Iterator<T> data;
	protected String aeTemplateFile;
}
