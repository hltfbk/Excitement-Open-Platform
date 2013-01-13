package eu.excitementproject.eop.lap.biu.en.parser.candc.graph;


import eu.excitementproject.eop.common.datastructures.dgraph.view.StringRepresentation;


/**
 * An implementation of {@link StringRepresentation} for C&C graphs.
 * @author Asher Stern
 *
 */
public class CCGraphStringRepresentation implements StringRepresentation<CCNode, CCEdgeInfo> {

	public String getEdgeRepresentation(CCEdgeInfo e)
	{
		String ret = "";
		if (e!=null)
		{
			if (e.getGrType()!=null)
				ret = e.getGrType();
			if ( (e.getOptionalInitialGr()!=null) || (e.getOptionalSubtype()!=null) )
			{
				String optional = "("
					+
					((e.getOptionalSubtype()!=null)?e.getOptionalSubtype():"")
					+
					","
					+
					((e.getOptionalInitialGr()!=null)?e.getOptionalInitialGr():"")
					+
					")";
				
				ret = ret + optional;
			}
		}
		return ret;
	}

	public String getNodeIdentifier(CCNode n)
	{
		return Integer.toString(n.getSerial());
	}

	public String getNodeRepresentation(CCNode n)
	{
		String ret;
		String id = Integer.toString(n.getSerial());
		try
		{
			String word = n.getInfo().getWord();
			if (null==word)word="";
			String lemma = n.getInfo().getLemma();
			if (null==lemma)lemma="";
			String partOfSpeech = n.getInfo().getPartOfSpeech();
			if (null==partOfSpeech) partOfSpeech = "";
			ret = id + " " + word + "[" + lemma+","+partOfSpeech+ "]";
		}
		catch(NullPointerException e)
		{
			ret = id;
		}
		return ret;
	}


}
