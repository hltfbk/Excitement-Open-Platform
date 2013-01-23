package eu.excitementproject.eop.biutee.rteflow.preprocess;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessor;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolver;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;


/**
 * An {@linkplain Instruments} object contain methods that
 * return the instruments, <B>not initialized</B>.
 * 
 * @author Asher Stern
 * @since Feb 26, 2011
 *
 * @param <I>
 * @param <S>
 */
public interface Instruments<I, S extends AbstractNode<I, S>>
{
	public BasicParser getParser();
	
	public NamedEntityRecognizer getNamedEntityRecognizer();
	
	public CoreferenceResolver<S> getCoreferenceResolver();
	
	public SentenceSplitter getSentenceSplitter();
	
	public TextPreprocessor getTextPreprocessor();
}
