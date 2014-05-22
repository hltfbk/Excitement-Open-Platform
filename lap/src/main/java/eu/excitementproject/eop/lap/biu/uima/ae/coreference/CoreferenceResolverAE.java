package eu.excitementproject.eop.lap.biu.uima.ae.coreference;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;
import eu.excitementproject.eop.common.representation.coreference.DockedMention;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolverNoTrees;
import eu.excitementproject.eop.lap.biu.uima.ae.SingletonSynchronizedAnnotator;

public abstract class CoreferenceResolverAE<T extends CoreferenceResolverNoTrees> extends SingletonSynchronizedAnnotator<T> {
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			// Resolver doesn't handle empty text well - just return without running it
			if (jcas.getDocumentText().trim().equals("")) {
				return;
			}
			
			List<List<DockedMention>> dockedMentionsGroups;
			synchronized (innerTool) {
				innerTool.setInput(jcas.getDocumentText());
				innerTool.resolve();
				dockedMentionsGroups = innerTool.getCoreferenceInformation();
			}
			
			for (List<DockedMention> dockedMentions: dockedMentionsGroups) {
				
				CoreferenceLink last = null;
				for (DockedMention mention : dockedMentions) {
					CoreferenceLink link = new CoreferenceLink(jcas, mention.getCharOffsetStart(), mention.getCharOffsetEnd());
					link.addToIndexes();
					
					if (last == null) {
						// Make chain (anchor) for the first mention in the group
						CoreferenceChain chain = new CoreferenceChain(jcas);
						chain.addToIndexes();
						chain.setFirst(link);
					}
					else {
						last.setNext(link);
					}
					
					last = link;
				}
			}
		} catch (CoreferenceResolutionException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		}
	}
}
