package ac.biu.nlp.nlp.instruments.ner.stanford;

import java.util.HashMap;

import ac.biu.nlp.nlp.instruments.parse.representation.basic.NamedEntity;


/**
 * The Stanford NER returns strings for named entity values.
 * This class is used to map those strings into {@link NamedEntity} object.
 * <P>
 * For an unrecognized string - it maps a <code> null </code> {@link NamedEntity}.
 * 
 * @author Asher Stern
 *
 */
public class StanfordAnswerToNamedEntityMapper
{
	private static HashMap<String,NamedEntity> map;
	static
	{
		map = new HashMap<String, NamedEntity>();
		map.put("PERSON", NamedEntity.PERSON);
		map.put("LOCATION", NamedEntity.LOCATION);
		map.put("ORGANIZATION", NamedEntity.ORGANIZATION);
	}
	
	
	public static NamedEntity convert(String stanfordAnswer)
	{
		NamedEntity ret = null;
		if (map.containsKey(stanfordAnswer))
		{
			ret = map.get(stanfordAnswer);
		}
		else
		{
			ret = null;
		}
		return ret;
	}
	
	
	

}
