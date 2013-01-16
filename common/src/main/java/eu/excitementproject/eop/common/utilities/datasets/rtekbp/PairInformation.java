package eu.excitementproject.eop.common.utilities.datasets.rtekbp;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * 
 * @author Asher Stern
 * @since Aug 23, 2010
 *
 */
public class PairInformation implements Serializable
{
	private static final long serialVersionUID = -7063241746644339852L;
	
	public PairInformation(String id, String query, String entityType,
			String attribute, EntailmentAnnotation annotation, String entity,
			String value, String textDocId,
			LinkedHashMap<Integer, String> mapHypotheses)
	{
		super();
		this.id = id;
		this.query = query;
		this.entityType = entityType;
		this.attribute = attribute;
		this.annotation = annotation;
		this.entity = entity;
		this.value = value;
		this.textDocId = textDocId;
		this.mapHypotheses = mapHypotheses;
	}
	
	
	
	public String getId()
	{
		return id;
	}
	public String getQuery()
	{
		return query;
	}
	public String getEntityType()
	{
		return entityType;
	}
	public String getAttribute()
	{
		return attribute;
	}
	public EntailmentAnnotation getAnnotation()
	{
		return annotation;
	}
	public String getEntity()
	{
		return entity;
	}
	public String getValue()
	{
		return value;
	}
	public String getTextDocId()
	{
		return textDocId;
	}
	public LinkedHashMap<Integer, String> getMapHypotheses()
	{
		return mapHypotheses;
	}
	
	
	



	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((annotation == null) ? 0 : annotation.hashCode());
		result = prime * result
				+ ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result
				+ ((entityType == null) ? 0 : entityType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((mapHypotheses == null) ? 0 : mapHypotheses.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result
				+ ((textDocId == null) ? 0 : textDocId.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		PairInformation other = (PairInformation) obj;
		if (annotation != other.annotation)
			return false;
		if (attribute == null)
		{
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		if (entity == null)
		{
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (entityType == null)
		{
			if (other.entityType != null)
				return false;
		} else if (!entityType.equals(other.entityType))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (mapHypotheses == null)
		{
			if (other.mapHypotheses != null)
				return false;
		} else if (!mapHypotheses.equals(other.mapHypotheses))
			return false;
		if (query == null)
		{
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		if (textDocId == null)
		{
			if (other.textDocId != null)
				return false;
		} else if (!textDocId.equals(other.textDocId))
			return false;
		if (value == null)
		{
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}






	private final String id;
	private final String query;
	private final String entityType;
	private final String attribute;
	private final EntailmentAnnotation annotation;
	private final String entity;
	private final String value;
	private final String textDocId;
	private final LinkedHashMap<Integer, String> mapHypotheses;
	

}
