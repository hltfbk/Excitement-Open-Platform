package eu.excitementproject.eop.lap.lappoc;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.XMLSerializer;
import org.uimafit.component.NoOpAnnotator;
import org.uimafit.factory.AggregateBuilder;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import eu.excitement.type.entailment.EntailmentMetadata;
import eu.excitement.type.entailment.Hypothesis;
import eu.excitement.type.entailment.Pair;
import eu.excitement.type.entailment.Text;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;

public class ExampleLAPTZ implements LAPAccess {

    public ExampleLAPTZ() throws LAPException {
        
        try {
            this.typeAE = createPrimitive(NoOpAnnotator.class);
        }
        catch (ResourceInitializationException e) {
            throw new LAPException(e);
        }
    }

    @Override
    public void processRawInputFormat(File inputFile, File outputDir)
            throws LAPException {
        JCas aJCas = null; 
        try {
            aJCas = typeAE.newJCas(); 
        } catch(ResourceInitializationException e)
        {
            throw new LAPException("Failed to create a JCAS", e); 
        }
        
        // prepare the reader  
        RawDataFormatReader input = null;
        
        try {
            input = new RawDataFormatReader(inputFile); 
        }
        catch (RawFormatReaderException e)
        {
            throw new LAPException("Failed to read XML input format", e); 
        }
            
        @SuppressWarnings("unused")
        String lang = input.getLanguage(); 
        @SuppressWarnings("unused")
        String channel = input.getChannel(); 
        
        // for each Pair data 
        while(input.hasNextPair())
        {
            RawDataFormatReader.PairXMLData pair; 
            try {
                 pair = input.nextPair(); 
            }
            catch (RawFormatReaderException e)
            {
                throw new LAPException("Failed to read XML input format", e); 
            }       
            
            // Add TE structure (Views) and types (Entailment.Pair, .Text, .Hypothesis, and .EntailmentMetadata) 
            addTEViewAndAnnotations(aJCas, pair.getText(), pair.getHypothesis(), pair.getId(), pair.getTask(), pair.getGoldAnswer()); 

            // TODO If you need to annotate "channel" in EntailmentMetadata, here is the place to annotate it. 
            // (above method does not annotate "channel")           
            { // not now. maybe in "real" annotator. 
            }

            // call to addAnnotationOn() each view 
            addAnnotationOn(aJCas, TEXTVIEW);
            addAnnotationOn(aJCas, HYPOTHESISVIEW);
            
            // serialize 
            String xmiName = pair.getId() + ".xmi"; 
            File xmiOutFile = new File(outputDir, xmiName); 
            
            try {
                FileOutputStream out = new FileOutputStream(xmiOutFile);
                XmiCasSerializer ser = new XmiCasSerializer(aJCas.getTypeSystem());
                XMLSerializer xmlSer = new XMLSerializer(out, false);
                ser.serialize(aJCas.getCas(), xmlSer.getContentHandler());
                out.close();
            } catch (FileNotFoundException e) {
                throw new LAPException("Unable to create/open the file" + xmiOutFile.toString(), e);
            } catch (SAXException e) {
                throw new LAPException("Failed to serialize the CAS into XML", e); 
            } catch (IOException e) {
                throw new LAPException("Unable to access/close the file" + xmiOutFile.toString(), e);
            }

            // TODO replace this with CommonLogger, when it is there. 
            System.out.println("Pair " + pair.getId() + "\twritten as " + xmiOutFile.toString() ); 
            // prepare next round
            aJCas.reset(); 
        }
    }

    @Override
    public JCas generateSingleTHPairCAS(String text, String hypothesis)
            throws LAPException {
        // get a new JCAS
        JCas aJCas = null; 
        try {
            aJCas = typeAE.newJCas(); 
        }
        catch (ResourceInitializationException e)
        {
            throw new LAPException("Unable to create new JCas", e); 
        }
        
        // prepare it with Views and Entailment.* annotations. 
        addTEViewAndAnnotations(aJCas, text, hypothesis, null, null, null); // last three args are PairId, task, and golden Answer --  which we don't fill here 
        
        // now aJCas has TextView, HypothesisView and Entailment.* types. (Pair, T and H) 
        // it is time to add linguistic annotations 
        addAnnotationOn(aJCas, TEXTVIEW);
        addAnnotationOn(aJCas, HYPOTHESISVIEW); 
        
        return aJCas; 
    }


    @Override
    public void addAnnotationOn(JCas aJCas, String viewName)
            throws LAPException
    {   
        AggregateBuilder builder = new AggregateBuilder();
        try {
            builder.add(
                    createPrimitiveDescription(
                            BreakIteratorSegmenter.class
                    ),
                    "_InitialView", viewName
            );
            AnalysisEngine ae = builder.createAggregate();
            ae.process(aJCas);
        }
        catch (ResourceInitializationException e) {
            throw new LAPException(e);
        }
        catch (AnalysisEngineProcessException e) {
            throw new LAPException(e);
        }        
    }

    @Override
    public void addAnnotationOn(JCas aJCas) throws LAPException {
        addAnnotationOn(aJCas, "_InitialView"); 
    }
    
    /**
     * 
     * This method adds two views (TextView, HypothesisView) and set their 
     * SOFA string (text on TextView, h on HView). 
     * And it also annotates them with Entailment.Metadata, Entailment.Pair, 
     * Entailment.Text and Entailment.Hypothesis. (But it adds no linguistic annotations. )  
     * 
     * <P>
     * This method annotates Metadata, but only its "Language" and "task" 
     * Other features like channel, source, task, collection and document ID, etc. 
     * are *not* set by this method. 
     *  
     * They should be set by the caller, if needed. 
     * @param aJCas 
     * @param text
     * @param hypothesis
     * @return
     */
    private void addTEViewAndAnnotations(JCas aJCas, String text, String hypothesis, String pairId, String task, String goldAnswer) throws LAPException {
        
        // generate views and set SOFA 
        JCas textView = null; JCas hypoView = null; 
        try {
            textView = aJCas.createView(TEXTVIEW);
            hypoView = aJCas.createView(HYPOTHESISVIEW); 
        }
        catch (CASException e) 
        {
            throw new LAPException("Unble to create new views", e); 
        }
        textView.setDocumentLanguage(this.languageIdentifier); 
        hypoView.setDocumentLanguage(this.languageIdentifier);
        textView.setDocumentText(text);
        hypoView.setDocumentText(hypothesis);
        
        // annotate Text (on TextView) 
        Text t = new Text(textView);
        t.setBegin(0); t.setEnd(text.length()); 
        t.addToIndexes(); 
        
        // annotate Hypothesis (on HypothesisView) 
        Hypothesis h = new Hypothesis(hypoView);
        h.setBegin(0); h.setEnd(hypothesis.length()); 
        h.addToIndexes(); 
        
        // annotate Pair (on the top CAS) 
        Pair p = new Pair(aJCas); 
        p.setText(t); // points T & H 
        p.setHypothesis(h); 
        p.setPairID(pairId); 
        
        if (goldAnswer != null && goldAnswer.length() > 0)
        {
            try {           
                p.setGoldAnswer(goldAnswer.toUpperCase()); 
            }
            catch (CASRuntimeException e)
            {
                // goldAnswer (Decision type) is string-sub-type 
                // where only some specific strings are permitted. 
                // If not permitted string was given, it raises CASRuntimeException 
                // (if the XML is validated, this try/catch is not needed. ... ) 
                throw new LAPException("Not permitted String value for Pair.goldAnswer: " + goldAnswer.toUpperCase(), e);           
            }
        }
        p.addToIndexes(); 
        
        // annotate Metadata (on the top CAS) 
        EntailmentMetadata m = new EntailmentMetadata(aJCas); 
        m.setLanguage(this.languageIdentifier); 
        m.setTask(task); 
        // the method don't set channel, origin, etc on the metadata. If needed, the caller should set it. 
        m.addToIndexes(); 
    }
        
    /**
     * Analysis engine that holds the type system. Note that even if you 
     * don't call AE, (or not using any AE), you need this. AE provides .newJCas() 
     */
    private final AnalysisEngine typeAE; 
    
    /**
     * We will set language directly with this id. 
     */
    private final String languageIdentifier = "EN"; 

    /**
     *  string constants 
     */
    private final String TEXTVIEW = "TextView";
    private final String HYPOTHESISVIEW = "HypothesisView";

    
    
    // From "interface Components" 
    //@Override //Gil: initialize is removed from interface Component
    public void initialize(CommonConfig config) throws ConfigurationException,
            ComponentException {
        // TODO this example does not use configuration. But when you replace it with 
        // your own annotator, use this part to get CommonConfiguration, and setup your 
        // annotator properly.      
        return; 
    }

    @Override
    public String getComponentName() { 
        return this.getClass().getName(); // name of this component that is used to identify the related configuration section ... 
    }

    @Override
    public String getInstanceName() {
        return null; // this component does not support instance configuration 
    } 
}
