package eu.excitementproject.eop.transformations.operations.rules.distsim;


/**
 *
 * Collection of parameters relevant to knowledge-resources that physically
 * stored in a Data-Base, in the format of DIRT.
 * 
 * The Data-Base contains a table of templates, which assigns an ID for each
 * template, and a table of rules, which assigns a score for pairs-of-IDs.
 * 
 * @author Asher Stern
 * @since Feb 17, 2011
 *
 */
public class DistSimParameters
{
	/**
	 * Constructors with all parameters.
	 * 
	 * @param templatesTableName the name of the template table.
	 * @param rulesTableName the name of the rules table.
	 * @param limitNumberOfRules limit is the number of rules returned for each template.
	 * This parameters is used in the SELECT query to limit the number of rules returned by the query.
	 * 
	 * @param cacheLhsSize cache is used to store templates and rules in the RAM memory. Actually - these parameters are no longer used. Ignore them.
	 * @param cacheRulesSize cache is used to store templates and rules in the RAM memory. Actually - these parameters are no longer used. Ignore them.
	 */
	public DistSimParameters(String templatesTableName, String rulesTableName,
			int limitNumberOfRules, int cacheLhsSize, int cacheRulesSize)
	{
		super();
		this.templatesTableName = templatesTableName;
		this.rulesTableName = rulesTableName;
		this.limitNumberOfRules = limitNumberOfRules;
		this.cacheLhsSize = cacheLhsSize;
		this.cacheRulesSize = cacheRulesSize;
	}
	
	
	public String getTemplatesTableName()
	{
		return templatesTableName;
	}
	public String getRulesTableName()
	{
		return rulesTableName;
	}
	public int getLimitNumberOfRules()
	{
		return limitNumberOfRules;
	}
	public int getCacheLhsSize()
	{
		return cacheLhsSize;
	}
	public int getCacheRulesSize()
	{
		return cacheRulesSize;
	}

	
	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + cacheLhsSize;
		result = prime * result + cacheRulesSize;
		result = prime * result + limitNumberOfRules;
		result = prime * result
				+ ((rulesTableName == null) ? 0 : rulesTableName.hashCode());
		result = prime
				* result
				+ ((templatesTableName == null) ? 0 : templatesTableName
						.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DistSimParameters other = (DistSimParameters) obj;
		if (cacheLhsSize != other.cacheLhsSize)
			return false;
		if (cacheRulesSize != other.cacheRulesSize)
			return false;
		if (limitNumberOfRules != other.limitNumberOfRules)
			return false;
		if (rulesTableName == null)
		{
			if (other.rulesTableName != null)
				return false;
		} else if (!rulesTableName.equals(other.rulesTableName))
			return false;
		if (templatesTableName == null)
		{
			if (other.templatesTableName != null)
				return false;
		} else if (!templatesTableName.equals(other.templatesTableName))
			return false;
		return true;
	}




	private final String templatesTableName;
	private final String rulesTableName;
	private final int limitNumberOfRules;
	private final int cacheLhsSize;
	private final int cacheRulesSize;
}
