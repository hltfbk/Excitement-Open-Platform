package eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom;

import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

/**
 * Creates {@link PennPartOfSpeech}
 * based on the given String representation of the part-of-speech.
 * 
 * @author Asher Stern
 * @since Oct 3, 2012
 *
 */
public class PennXmlTreePosFactory implements XmlTreePartOfSpeechFactory
{
	public PartOfSpeech createPartOfSpeech(CanonicalPosTag canonicalPosTag,
			String partOfSpeechRepresentation) throws UnsupportedPosTagStringException
	{
		return createPartOfSpeech(partOfSpeechRepresentation);
	}

	@Override
	public PartOfSpeech createPartOfSpeech(String partOfSpeechRepresentation) throws UnsupportedPosTagStringException
	{
		return new PennPartOfSpeech(partOfSpeechRepresentation);
	}

}
