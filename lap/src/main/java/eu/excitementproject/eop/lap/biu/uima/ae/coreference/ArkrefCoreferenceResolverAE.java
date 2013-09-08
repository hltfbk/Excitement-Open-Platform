package eu.excitementproject.eop.lap.biu.uima.ae.coreference;

import eu.excitementproject.eop.common.datastructures.Envelope;
import eu.excitementproject.eop.lap.biu.en.coreference.arkref.ArkrefCoreferenceResolverNoTrees;

public class ArkrefCoreferenceResolverAE extends CoreferenceResolverAE<ArkrefCoreferenceResolverNoTrees> {

	private static Envelope<ArkrefCoreferenceResolverNoTrees> envelope = new Envelope<ArkrefCoreferenceResolverNoTrees>();
	
	@Override
	protected final Envelope<ArkrefCoreferenceResolverNoTrees> getEnvelope(){return envelope;}
	
	@Override
	protected ArkrefCoreferenceResolverNoTrees buildInnerTool() throws Exception {
		ArkrefCoreferenceResolverNoTrees coref = new ArkrefCoreferenceResolverNoTrees();
		coref.init();
		return coref;
	}	
}
