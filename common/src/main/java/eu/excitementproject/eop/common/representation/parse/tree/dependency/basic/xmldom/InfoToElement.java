package eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelationType;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.SyntacticInfo;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

/**
 * Gets an {@link Info}, and creates an XML element ({@link Element}) which
 * represents this {@linkplain Info}.
 * 
 * @author Asher Stern
 * @since October 2, 2012
 *
 */
public class InfoToElement
{
	public static final String EXIST_ATTRIBUTE_NAME = "exists";
	public static final String VARIABLE_ATTRIBUTE_NAME = "variable";
	public static final String TRUE_VALUE = "true";
	public static final String FALSE_VALUE = "false";
	public static final String SERIAL_ELEMENT_NAME = "serial";
	public static final String ID_ELEMENT_NAME = "id";
	public static final String POS_STRING_ELEMENT_NAME = "PartOfSpeechString";
	public static final String VARIABLE_ID_ELEMENT_NAME = "variableId";
	public static final String WORD_ELEMENT_NAME = "word";
	public static final String LEMMA_ELEMENT_NAME = "lemma";
	public static final String DEPENDENCY_RELATION_STRING_ELEMENT_NAME = "dependencyRelationString";
	

	public InfoToElement(Document document, Info info)
	{
		super();
		this.document = document;
		this.info = info;
	}


	public void generate()
	{
		infoElement = document.createElement(Info.class.getSimpleName());
		if (info!=null)
		{
			infoElement.setAttribute(EXIST_ATTRIBUTE_NAME, TRUE_VALUE);
			generateChildren();
		}
		else
		{
			infoElement.setAttribute(EXIST_ATTRIBUTE_NAME, FALSE_VALUE);
		}
	}
	

	public Element getInfoElement() throws TreeXmlException
	{
		if (null==infoElement) throw new TreeXmlException("Not generated. Please call the method generate()");
		return infoElement;
	}



	///////////////////////// PRIVATE /////////////////////////

	private void generateChildren()
	{
		generateId();
		generateNodeInfo();
		generateEdgeInfo();
	}

	
	private void generateId()
	{
		Element idElement = document.createElement(ID_ELEMENT_NAME);
		infoElement.appendChild(idElement);
		String id = info.getId();
		if (id!=null)
		{
			idElement.setAttribute(EXIST_ATTRIBUTE_NAME, TRUE_VALUE);
			Text idText = document.createTextNode(id);
			idElement.appendChild(idText);
		}
		else
		{
			idElement.setAttribute(EXIST_ATTRIBUTE_NAME, FALSE_VALUE);
		}
		
	}
	
	private void generateNodeInfo()
	{
		NodeInfo nodeInfo = info.getNodeInfo();
		Element nodeInfoElement = document.createElement(NodeInfo.class.getSimpleName());
		infoElement.appendChild(nodeInfoElement);
		if (nodeInfo != null)
		{
			nodeInfoElement.setAttribute(EXIST_ATTRIBUTE_NAME, TRUE_VALUE);
			
			if (nodeInfo.isVariable())
			{
				nodeInfoElement.setAttribute(VARIABLE_ATTRIBUTE_NAME, TRUE_VALUE);
			}
			
			NamedEntity namedEntity = nodeInfo.getNamedEntityAnnotation();
			Element namedEntityElement = document.createElement(NamedEntity.class.getSimpleName());
			nodeInfoElement.appendChild(namedEntityElement);
			if (namedEntity!=null)
			{
				namedEntityElement.setAttribute(EXIST_ATTRIBUTE_NAME, TRUE_VALUE);
				Text namedEntityText = document.createTextNode(namedEntity.name());
				namedEntityElement.appendChild(namedEntityText);
			}
			else
			{
				namedEntityElement.setAttribute(EXIST_ATTRIBUTE_NAME, FALSE_VALUE);
			}
			
			int serial = nodeInfo.getSerial();
			Element serialElement = document.createElement(SERIAL_ELEMENT_NAME);
			nodeInfoElement.appendChild(serialElement);
			Text serialText = document.createTextNode(String.valueOf(serial));
			serialElement.appendChild(serialText);
			
			SyntacticInfo syntacticInfo = nodeInfo.getSyntacticInfo();
			Element syntacticInfoElement = document.createElement(SyntacticInfo.class.getSimpleName());
			nodeInfoElement.appendChild(syntacticInfoElement);
			if (syntacticInfo!=null)
			{
				syntacticInfoElement.setAttribute(EXIST_ATTRIBUTE_NAME, TRUE_VALUE);
				PartOfSpeech partOfSpeech = syntacticInfo.getPartOfSpeech();
				Element partOfSpeechElement = document.createElement(PartOfSpeech.class.getSimpleName());
				syntacticInfoElement.appendChild(partOfSpeechElement);
				if (partOfSpeech!=null)
				{
					partOfSpeechElement.setAttribute(EXIST_ATTRIBUTE_NAME, TRUE_VALUE);
					
					Element canonicalPartOfSpeechElement = document.createElement(CanonicalPosTag.class.getSimpleName());
					partOfSpeechElement.appendChild(canonicalPartOfSpeechElement);
					Text canonicalPosText = document.createTextNode(partOfSpeech.getCanonicalPosTag().name());
					canonicalPartOfSpeechElement.appendChild(canonicalPosText);
					
					Element posStringElement = document.createElement(POS_STRING_ELEMENT_NAME);
					partOfSpeechElement.appendChild(posStringElement);
					Text posStringText = document.createTextNode(partOfSpeech.getStringRepresentation());
					posStringElement.appendChild(posStringText);
				}
				else
				{
					partOfSpeechElement.setAttribute(EXIST_ATTRIBUTE_NAME, TRUE_VALUE);
				}
			}
			else
			{
				syntacticInfoElement.setAttribute(EXIST_ATTRIBUTE_NAME, FALSE_VALUE);
			}
			
			Integer variableId = nodeInfo.getVariableId();
			if (variableId!=null)
			{
				Element variableIdElement = document.createElement(VARIABLE_ID_ELEMENT_NAME);
				nodeInfoElement.appendChild(variableIdElement);
				Text variableIdText = document.createTextNode(variableId.toString());
				variableIdElement.appendChild(variableIdText);
			}
			
			String word = nodeInfo.getWord();
			Element wordElement = document.createElement(WORD_ELEMENT_NAME);
			nodeInfoElement.appendChild(wordElement);
			if (word!=null)
			{
				wordElement.setAttribute(EXIST_ATTRIBUTE_NAME, TRUE_VALUE);
				Text wordText = document.createTextNode(word);
				wordElement.appendChild(wordText);
			}
			else
			{
				wordElement.setAttribute(EXIST_ATTRIBUTE_NAME, FALSE_VALUE);
			}
			
			String lemma = nodeInfo.getWordLemma();
			Element lemmaElement = document.createElement(LEMMA_ELEMENT_NAME);
			nodeInfoElement.appendChild(lemmaElement);
			if (lemma != null)
			{
				lemmaElement.setAttribute(EXIST_ATTRIBUTE_NAME, TRUE_VALUE);
				Text lemmaText = document.createTextNode(lemma);
				lemmaElement.appendChild(lemmaText);
			}
			else
			{
				lemmaElement.setAttribute(EXIST_ATTRIBUTE_NAME, FALSE_VALUE);
			}
		}
		else
		{
			nodeInfoElement.setAttribute(EXIST_ATTRIBUTE_NAME, FALSE_VALUE);
		}

	}
	
	
	private void generateEdgeInfo()
	{
		EdgeInfo edgeInfo = info.getEdgeInfo();
		Element edgeInfoElement = document.createElement(EdgeInfo.class.getSimpleName());
		infoElement.appendChild(edgeInfoElement);
		if (edgeInfo!=null)
		{
			edgeInfoElement.setAttribute(EXIST_ATTRIBUTE_NAME, TRUE_VALUE);
			DependencyRelation dependencyRelation = edgeInfo.getDependencyRelation();
			Element dependencyRelationElement = document.createElement(DependencyRelation.class.getSimpleName());
			edgeInfoElement.appendChild(dependencyRelationElement);
			if (dependencyRelation!=null)
			{
				dependencyRelationElement.setAttribute(EXIST_ATTRIBUTE_NAME, TRUE_VALUE);
				String dependencyRelationString = dependencyRelation.getStringRepresentation();
				Element dependencyRelationStringElement = document.createElement(DEPENDENCY_RELATION_STRING_ELEMENT_NAME);
				dependencyRelationElement.appendChild(dependencyRelationStringElement);
				if (dependencyRelationString!=null)
				{
					dependencyRelationStringElement.setAttribute(EXIST_ATTRIBUTE_NAME, TRUE_VALUE);
					Text dependencyRelationStringText = document.createTextNode(dependencyRelationString);
					dependencyRelationStringElement.appendChild(dependencyRelationStringText);
				}
				else
				{
					dependencyRelationStringElement.setAttribute(EXIST_ATTRIBUTE_NAME, FALSE_VALUE);
				}
				
				DependencyRelationType dependencyRelationType = dependencyRelation.getType();
				Element dependencyRelationTypeElement = document.createElement(DependencyRelationType.class.getSimpleName());
				dependencyRelationElement.appendChild(dependencyRelationTypeElement);
				if (dependencyRelationType!=null)
				{
					dependencyRelationTypeElement.setAttribute(EXIST_ATTRIBUTE_NAME, TRUE_VALUE);
					Text dependencyRelationTypeText = document.createTextNode(dependencyRelationType.name());
					dependencyRelationTypeElement.appendChild(dependencyRelationTypeText);
				}
				else
				{
					dependencyRelationTypeElement.setAttribute(EXIST_ATTRIBUTE_NAME, FALSE_VALUE);
				}
			}
			else
			{
				dependencyRelationElement.setAttribute(EXIST_ATTRIBUTE_NAME, FALSE_VALUE);
			}
		}
		else
		{
			edgeInfoElement.setAttribute(EXIST_ATTRIBUTE_NAME, FALSE_VALUE);
		}
	}
	

	
	
	
	
	private Document document;
	private Info info;
	
	private Element infoElement = null;
}
