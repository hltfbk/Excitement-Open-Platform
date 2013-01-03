package ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.xmldom;

import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.PartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;

/**
 * This factory creates an actual object of {@link PartOfSpeech}, based
 * on the String representation of the part-of-speech and the {@link CanonicalPosTag}.
 * <P>
 * This factory is used when reading from an XML file the value of the part-of-speech
 * (i.e., the String representation and the {@link CanonicalPosTag}). The XML does not
 * specify the actual type of the {@link PartOfSpeech}, so such a factory should be given
 * as parameter to the class that reads this information, in order to build the
 * appropriate {@link PartOfSpeech}.
 * 
 * @author Asher Stern
 * @since Oct 3, 2012
 *
 */
public interface XmlTreePartOfSpeechFactory
{
	public PartOfSpeech createPartOfSpeech(CanonicalPosTag canonicalPosTag, String partOfSpeechRepresentation) throws UnsupportedPosTagStringException;
}
