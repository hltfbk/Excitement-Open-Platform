package eu.excitementproject.eop.common.representation.parse.representation.basic;

import eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

/**
 * Easy get the fields of {@link Info}, {@link NodeInfo} and {@link EdgeInfo}, with no null
 * in the return value.
 * 
 * @author Asher Stern
 * @since Aug 11, 2010
 *
 */
public class InfoGetFields
{
	public static final String VARIABLE_PREFIX = "X_";
	
	public static String getLemma(Info info)
	{
		return getLemma(info,"");
	}
	
	public static String getWord(Info info)
	{
		return getWord(info,"");
	}
	
	public static int getSerial(Info info)
	{
		return getSerial(info,-1);
	}
	
	public static String getPartOfSpeech(Info info)
	{
		return getPartOfSpeech(info,"");
	}

	public static String getRelation(Info info)
	{
		return getRelation(info,"");
	}

	public static String getLemma(NodeInfo nodeInfo)
	{
		return getLemma(nodeInfo,"");
	}
	
	public static String getWord(NodeInfo nodeInfo)
	{
		return getWord(nodeInfo,"");
	}
	
	public static String getPartOfSpeech(NodeInfo nodeInfo)
	{
		return getPartOfSpeech(nodeInfo,"");
	}
	
	public static NamedEntity getNamedEntityAnnotation(Info nodeInfo)
	{
		return getNamedEntityAnnotation(nodeInfo, null);
	}
	
	public static String getRelation(EdgeInfo edgeInfo)
	{
		return getRelation(edgeInfo,"");
	}

	
	
	public static String getLemma(Info info, String defaultValue)
	{
		if (info!=null)
			return getLemma(info.getNodeInfo(),defaultValue);
		else
			return defaultValue;
	}
	
	public static String getWord(Info info, String defaultValue)
	{
		if (info!=null)
			return getWord(info.getNodeInfo(),defaultValue);
		else
			return defaultValue;
	}
	
	public static int getSerial(Info info, int defaultValue)
	{
		return null==info? defaultValue: getSerial(info.getNodeInfo(),defaultValue);
	}
	
	public static String getPartOfSpeech(Info info,String defaultValue)
	{
		if (info!=null)
			return getPartOfSpeech(info.getNodeInfo(), defaultValue);
		else
			return defaultValue;
	}
	
	public static NamedEntity getNamedEntityAnnotation(Info info, NamedEntity defaultValue)	{
		return (info==null? defaultValue: getNamedEntityAnnotation(info.getNodeInfo(), defaultValue));
	}
	
	public static String getRelation(Info info, String defaultValue)
	{
		if (info!=null)
			return getRelation(info.getEdgeInfo(),defaultValue);
		else
			return defaultValue;
	}
	
	public static String getLemma(NodeInfo nodeInfo, String defaultValue)
	{
		String ret = defaultValue;
		if (nodeInfo!=null)if (nodeInfo.getWordLemma()!=null)
			ret = nodeInfo.getWordLemma();
		
		return ret;
	}
	
	public static String getWord(NodeInfo nodeInfo, String defaultValue)
	{
		String ret = defaultValue;
		if (nodeInfo!=null)if (nodeInfo.getWord()!=null)
			ret = nodeInfo.getWord();
		
		return ret;
	}
	
	public static int getSerial(NodeInfo nodeInfo, int defaultValue)
	{
		return null==nodeInfo? defaultValue: nodeInfo.getSerial();
	}
	
	public static String getPartOfSpeech(NodeInfo nodeInfo,String defaultValue)
	{
		String ret = defaultValue;
		
		// Check at every level if it is null. Don't catch NullPointerException because it halts the debugger.
		if (nodeInfo!=null && nodeInfo.getSyntacticInfo()!=null && nodeInfo.getSyntacticInfo().getPartOfSpeech()!=null) {
			String pos_=nodeInfo.getSyntacticInfo().getPartOfSpeech().getStringRepresentation();
			if (pos_!=null)
				ret=pos_;
		}
		
		return ret;
	}
	
	public static NamedEntity getNamedEntityAnnotation(NodeInfo nodeInfo,NamedEntity defaultValue)	{
		return (nodeInfo==null? defaultValue: nodeInfo.getNamedEntityAnnotation());
	}
	
	public static String getRelation(EdgeInfo edgeInfo, String defaultValue)
	{
		String ret = defaultValue;
		
		// Check at every level if it is null. Don't catch NullPointerException because it halts the debugger.
		if (edgeInfo!=null && edgeInfo.getDependencyRelation()!=null) {
			String rel_ = edgeInfo.getDependencyRelation().getStringRepresentation();
			if (rel_!=null)
				ret=rel_;
		}

		return ret;
	}
	
	public static boolean isVariable(Info info)
	{
		boolean ret = false;
		if (info!=null)
		{
			if (info.getNodeInfo()!=null)
			{
				if (info.getNodeInfo().getVariableId()!=null)
					ret = true;
			}
		}
		return ret;
	}

	
	public static String getVariable(Info info)
	{
		return getVariable(info,"");
	}
	
	public static String getVariable(NodeInfo nodeInfo)
	{
		return getVariable(nodeInfo,"");
	}
	
	public static String getVariable(Info info, String defaultValue)
	{
		if (info!=null)
			return getVariable(info.getNodeInfo(),defaultValue);
		else
			return defaultValue;
	}
	
	public static String getVariable(NodeInfo nodeInfo, String defaultValue)
	{
		String ret = defaultValue;
		if (nodeInfo!=null)
		{
			if (nodeInfo.isVariable())
			{
				String variableString = null;
				if (nodeInfo.getVariableId()!=null)
					variableString = nodeInfo.getVariableId().toString();
				else
					variableString = "null";
				
				ret = VARIABLE_PREFIX+variableString;
			}
		}
		return ret;
	}
	
	public static PartOfSpeech getPartOfSpeechObject(Info info)
	{
		NodeInfo nodeInfo = null;
		if (info!=null)
			nodeInfo = info.getNodeInfo();
		
		return getPartOfSpeechObject(nodeInfo);
	}
	
	public static PartOfSpeech getPartOfSpeechObject(NodeInfo nodeInfo)
	{
		PartOfSpeech ret = null;
		
		if (nodeInfo!=null){if (nodeInfo.getSyntacticInfo()!=null){if (nodeInfo.getSyntacticInfo().getPartOfSpeech()!=null)
		{
			ret = nodeInfo.getSyntacticInfo().getPartOfSpeech();
		}}}
		if (null==ret)
			try{ret = new ByCanonicalPartOfSpeech(CanonicalPosTag.OTHER.name());}catch(UnsupportedPosTagStringException e){ /* ignore exception */}
		
		return ret;
	}

	public static CanonicalPosTag getCanonicalPartOfSpeech(Info info)
	{
		NodeInfo nodeInfo = null;
		if (info!=null)
			nodeInfo = info.getNodeInfo();
		
		return getCanonicalPartOfSpeech(nodeInfo);
	}
	
	public static CanonicalPosTag getCanonicalPartOfSpeech(NodeInfo nodeInfo)
	{
		CanonicalPosTag ret = null;
		
		if (nodeInfo!=null){if (nodeInfo.getSyntacticInfo()!=null){if (nodeInfo.getSyntacticInfo().getPartOfSpeech()!=null)
		{
			ret = nodeInfo.getSyntacticInfo().getPartOfSpeech().getCanonicalPosTag();
		}}}
		if (null==ret)
			ret = CanonicalPosTag.OTHER;
		
		return ret;
	}
}
