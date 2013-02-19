package eu.excitementproject.eop.common.representation.parse.tree.dependency.basic;

import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

/**
 * Conversion of a formal string to a tree, and vice versa.
 * <P>
 * <I>Formal String</I> is a string in braces-language, such that the head is the first item,
 * then the children recursively (see examples below).
 * <BR>
 * The information is represented as: word|lemma|part-of-speech|relation
 * <P>
 * Examples:
 * <BR>
 * "( see|see|VERB|i (I|I|NOUN|subj) (you|you|NOUN|obj))"
 * represents a tree for "I see you".
 * <BR>
 * 
 * @author Asher Stern
 *
 */
public class BasicTreeFormalString
{
	public static final char OPEN_BRACE = '(';
	public static final char CLOSE_BRACE = ')';
	public static final char SPACE = ' ';
	public static final char FIELD_SEPARATOR = '|';
	public static final String DEFAULT_STRING = "-";
	public static final String VARIABLE_INDICATOR = "*";
	
	
	@SuppressWarnings("serial")
	public static class EnglishTreeFormalStringException extends Exception
	{
		public EnglishTreeFormalStringException(String message, Throwable cause)
		{
			super(message, cause);
		}

		public EnglishTreeFormalStringException(String message)
		{
			super(message);
		}
	}

	///////////////////////////////////////////////////////////////////////

	public BasicTreeFormalString(String currentSubTreeString, int id) throws EnglishTreeFormalStringException
	{
		super();
		this.currentSubTreeString = currentSubTreeString;
		this.id = id;
		setCurrentSubTree();
	}
	
	public BasicTreeFormalString(BasicNode root) throws EnglishTreeFormalStringException
	{
		super();
		this.currentSubTree = root;
		setCurrentSubTreeString();
	}

	public BasicNode getTree()
	{
		return currentSubTree;
	}
	
	public String getFormalString()
	{
		return currentSubTreeString;
	}
	
	private void setCurrentSubTreeString() throws EnglishTreeFormalStringException
	{
		String currentNodeString = null;
		String word = InfoGetFields.getWord(currentSubTree.getInfo(),DEFAULT_STRING);
		String lemma = InfoGetFields.getLemma(currentSubTree.getInfo(),DEFAULT_STRING);
		String partOfSpeech = InfoGetFields.getPartOfSpeech(currentSubTree.getInfo(),DEFAULT_STRING);
		String relation = InfoGetFields.getRelation(currentSubTree.getInfo(),DEFAULT_STRING);
		
		
		currentNodeString = word+FIELD_SEPARATOR+lemma+FIELD_SEPARATOR+partOfSpeech+FIELD_SEPARATOR+relation;
		StringBuffer sb = new StringBuffer();
		sb.append(OPEN_BRACE);
		sb.append(currentNodeString);
		if (currentSubTree.getChildren()!=null)
		{
			for (BasicNode child : currentSubTree.getChildren())
			{
				BasicTreeFormalString childFormalString = new BasicTreeFormalString(child);
				sb.append(childFormalString.getFormalString());
			}
			
		}
		sb.append(CLOSE_BRACE);
		this.currentSubTreeString = sb.toString();
	}

	private static List<String> extractBracesUnits(String str) throws EnglishTreeFormalStringException 
	{
		List<String> ret = new LinkedList<String>();
		int startIndex=0;
		
		while (startIndex<str.length())
		{
			int counter = 0;
			int index=startIndex;
			if (str.charAt(startIndex)!=OPEN_BRACE)
				throw new EnglishTreeFormalStringException("illegal");
			
			index++;
			counter = 1;
			while (counter > 0)
			{
				if (str.charAt(index)==OPEN_BRACE)
					++counter;
				else if (str.charAt(index)==CLOSE_BRACE)
					--counter;
				
				++index;
			}
			ret.add(str.substring(startIndex, index));
			while ((index<str.length())&&(str.charAt(index)==SPACE))
			{
				++index;
			}
			startIndex=index;
		}
		return ret;
	}
	
	private void setCurrentNodeFromString(String str) throws EnglishTreeFormalStringException
	{
		final String fieldSeparatorRegExt = "\\"+String.valueOf(FIELD_SEPARATOR);
		String[] fieldsArray = str.split(fieldSeparatorRegExt);
		if (fieldsArray.length!=(1+1+1+1))
			throw new EnglishTreeFormalStringException("fields in string "+str+" are malformed. Not enough fields.");
		
		int fieldIndex=0;
		String word = fieldsArray[fieldIndex];
		fieldIndex++;
		String lemma= fieldsArray[fieldIndex];
		fieldIndex++;
		String partOfSpeech= fieldsArray[fieldIndex];
		fieldIndex++;
		String relation= fieldsArray[fieldIndex];
		fieldIndex++;
		
		NodeInfo nodeInfo;
		try
		{
			if (lemma.startsWith(VARIABLE_INDICATOR))
			{
				String variableIdStr = lemma.substring(VARIABLE_INDICATOR.length());
				Integer variableId = Integer.parseInt(variableIdStr);
				nodeInfo = DefaultNodeInfo.newVariableDefaultNodeInfo(variableId, new DefaultSyntacticInfo(new BySimplerCanonicalPartOfSpeech(partOfSpeech)));
			}
			else
			{
				nodeInfo = new DefaultNodeInfo(word, lemma, 0, null, new DefaultSyntacticInfo(new BySimplerCanonicalPartOfSpeech(partOfSpeech)));
			}
		}
		catch(NumberFormatException e)
		{
			throw new EnglishTreeFormalStringException("Bad variable id at: "+lemma);
		}
		catch (UnsupportedPosTagStringException e)
		{
			throw new EnglishTreeFormalStringException("Problem with PartOfSpeech instantiation");
		}
		EdgeInfo edgeInfo = new DefaultEdgeInfo(new DependencyRelation(relation, null));
		this.currentSubTree = new BasicNode(new DefaultInfo(String.valueOf(id), nodeInfo, edgeInfo));

	}

	private void setCurrentSubTree() throws EnglishTreeFormalStringException
	{
		if ( (currentSubTreeString.charAt(0)==OPEN_BRACE) && (currentSubTreeString.charAt(currentSubTreeString.length()-1)==CLOSE_BRACE) )
			;
		else
			throw new EnglishTreeFormalStringException("malformed");
		
		int startIndex=1;
		int index=startIndex;
		while ( (currentSubTreeString.charAt(index)!=OPEN_BRACE) && (currentSubTreeString.charAt(index)!=CLOSE_BRACE) )
			++index;
		
		setCurrentNodeFromString(currentSubTreeString.substring(startIndex,index).trim());
		this.lastUsedId = id;
		if (currentSubTreeString.charAt(index)==OPEN_BRACE)
		{
			List<String> braceUnits = extractBracesUnits(currentSubTreeString.substring(index, currentSubTreeString.length()-1));
			
			for (String braceUnit : braceUnits)
			{
				BasicTreeFormalString child = new BasicTreeFormalString(braceUnit, lastUsedId+1);
				this.currentSubTree.addChild(child.getTree());
				lastUsedId = child.getLastUsedId();
			}
		}
	}
	
	private int getLastUsedId()
	{
		return this.lastUsedId;
	}
	
	
	



	private String currentSubTreeString;
	private BasicNode currentSubTree;
	private int id;
	private int lastUsedId;
	
	
	

}
