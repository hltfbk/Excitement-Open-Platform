package eu.excitementproject.eop.transformations.operations.finders.auxiliary;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor;
import eu.excitementproject.eop.common.representation.partofspeech.WildcardPartOfSpeech;
import eu.excitementproject.eop.core.component.syntacticknowledge.BasicMatchCriteria;


/**
 * 
 * @author Asher Stern
 * @since Dec 25, 2013
 *
 * @param <I>
 * @param <S>
 */
public class ParseTreeCharacteristicsCollector<I extends Info, S extends AbstractNode<I,S>>
{
	public ParseTreeCharacteristicsCollector(S tree)
	{
		super();
		this.tree = tree;
	}

	public void extract()
	{
		posRelPosSet = new LinkedHashSet<>();
		lemmaAndPosSet = new LinkedHashSet<>();
		
		addForNode(tree,null);
	}
	
	
	
	public Set<PosRelPos> getPosRelPosSet()
	{
		return posRelPosSet;
	}

	public Set<LemmaAndSimplerCanonicalPos> getLemmaAndPosSet()
	{
		return lemmaAndPosSet;
	}

	
	
	
	private void addForNode(final S node, final SimplerCanonicalPosTag posParent)
	{
		SimplerCanonicalPosTag pos = null;
		if (
			(node.getInfo()!=null)
			&&
			(!(WildcardPartOfSpeech.isWildCardPOS(InfoGetFields.getPartOfSpeechObject(node.getInfo()))))
			)
		{
			pos = SimplerPosTagConvertor.simplerPos(InfoGetFields.getCanonicalPartOfSpeech(node.getInfo()));
			String lemma = InfoGetFields.getLemma(node.getInfo()).trim().toLowerCase();
			if (
					(!(InfoGetFields.isVariable(node.getInfo())))
					&&
					(lemma.length()>0)
					)
			{
				LemmaAndSimplerCanonicalPos lemmaAndPos = new LemmaAndSimplerCanonicalPos(lemma,pos);
				lemmaAndPosSet.add(lemmaAndPos);
			}
			if (posParent!=null)
			{
				String relation = InfoGetFields.getRelation(node.getInfo());
				if (
						(!(BasicMatchCriteria.WILDCARD_RELATION.equals(relation)))
						&&
						(relation.length()>0)
						)
				{
					PosRelPos posRelPos = new PosRelPos(posParent,relation,pos);
					posRelPosSet.add(posRelPos);
				}
			}
		}
		
		if (node.getChildren()!=null)
		{
			for (S child : node.getChildren())
			{
				addForNode(child,pos);
			}
		}
	}

	private final S tree;
	
	private Set<PosRelPos> posRelPosSet;
	private Set<LemmaAndSimplerCanonicalPos> lemmaAndPosSet;
}
