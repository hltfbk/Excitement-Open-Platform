package ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.xmldom;

import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.PartOfSpeech;
import ac.biu.nlp.nlp.representation.PennPartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;

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
		return new PennPartOfSpeech(partOfSpeechRepresentation);
	}

}
