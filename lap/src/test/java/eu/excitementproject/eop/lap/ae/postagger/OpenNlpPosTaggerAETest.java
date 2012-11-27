package eu.excitementproject.eop.lap.ae.postagger;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregate;
import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;

public class OpenNlpPosTaggerAETest {

	@Test
	public void openNlpPosTaggerTest() throws Exception {
		
		AnalysisEngineDescription desc = createAggregateDescription(
				createPrimitiveDescription(BreakIteratorSegmenter.class),
				createPrimitiveDescription(
						OpenNlpPosTaggerAE.class, 
						OpenNlpPosTaggerAE.PARAM_MODEL_FILE, "src/test/resources/parser/tag.bin.gz",
						OpenNlpPosTaggerAE.PARAM_TAG_DICT, "src/test/resources/parser/tagdict"
				)
		);

		AnalysisEngine engine = createAggregate(desc);
		JCas jcas = engine.newJCas();
		jcas.setDocumentLanguage("en");
		jcas.setDocumentText("This is an example.");
		
		engine.process(jcas);

		for (POS  pos : JCasUtil.select(jcas, POS.class)) {
			System.out.println(pos);
		}
	}
}
