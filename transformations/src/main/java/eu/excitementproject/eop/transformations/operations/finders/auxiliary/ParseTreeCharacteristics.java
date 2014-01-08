package eu.excitementproject.eop.transformations.operations.finders.auxiliary;

import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * 
 * @author Asher Stern
 * @since Dec 25, 2013
 *
 * @param <I>
 * @param <S>
 */
public class ParseTreeCharacteristics<I extends Info, S extends AbstractNode<I,S>>
{
	public ParseTreeCharacteristics(Set<PosRelPos> posRelPosSet,
			Set<LemmaAndSimplerCanonicalPos> lemmaAndPosSet)
	{
		super();
		this.posRelPosSet = posRelPosSet;
		this.lemmaAndPosSet = lemmaAndPosSet;
	}
	
	
	
	public Set<PosRelPos> getPosRelPosSet()
	{
		return posRelPosSet;
	}
	public Set<LemmaAndSimplerCanonicalPos> getLemmaAndPosSet()
	{
		return lemmaAndPosSet;
	}



	private final Set<PosRelPos> posRelPosSet;
	private final Set<LemmaAndSimplerCanonicalPos> lemmaAndPosSet;
}
