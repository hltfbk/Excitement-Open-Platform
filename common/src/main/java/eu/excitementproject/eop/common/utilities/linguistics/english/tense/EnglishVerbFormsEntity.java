package eu.excitementproject.eop.common.utilities.linguistics.english.tense;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;


/**
 * 
 * @author Asher Stern
 * @since October 9 2012
 *
 */
@LanguageDependent("english")
public final class EnglishVerbFormsEntity
{
	public EnglishVerbFormsEntity(String infinitive, String pastTense, String pastParticiple)
	{
		super();
		this.infinitive = infinitive;
		
		this.pastTense = getSingle(pastTense);
		this.pastTenseAlternatives = getMultiple(pastTense);
		this.pastParticiple = getSingle(pastParticiple);
		this.pastParticipleAlternatives = getMultiple(pastParticiple);
	}
	
	
	
	public String getInfinitive()
	{
		return infinitive;
	}
	public String getPastTense()
	{
		return pastTense;
	}
	public String getPastParticiple()
	{
		return pastParticiple;
	}
	public Set<String> getPastTenseAlternatives()
	{
		return pastTenseAlternatives;
	}
	public Set<String> getPastParticipleAlternatives()
	{
		return pastParticipleAlternatives;
	}
	
	@Override
	public String toString()
	{
		return getInfinitive()+": "+getPastTense()+" ("+setToString(getPastTenseAlternatives())+") / "+getPastParticiple()+" ("+setToString(getPastParticipleAlternatives())+")";
		
	}



	private String getSingle(String str)
	{
		if (str.indexOf(',')<0)
		{
			return str.trim();
		}
		else
		{
			return str.substring(0, str.indexOf(',')).trim();
		}
	}
	
	private Set<String> getMultiple(String str)
	{
		if (str.indexOf(',')<0)
		{
			return Collections.singleton(str.trim());
		}
		else
		{
			String[] components = str.split(",");
			Set<String> ret = new LinkedHashSet<String>();
			for (String component : components)
			{
				ret.add(component.trim());
			}
			return ret;
		}
		
		
	}

	private static final String setToString(Set<String> set)
	{
		StringBuilder sb = new StringBuilder();
		boolean firstIteration = true;
		for (String s : set)
		{
			if (firstIteration) {firstIteration=false;} else {sb.append(", ");}
			sb.append(s);
		}
		return sb.toString();
	}
	
	private String infinitive; // present tense
	private String pastTense;
	private String pastParticiple; // 3rd form
	
	private Set<String> pastTenseAlternatives;
	private Set<String> pastParticipleAlternatives;
}
