package ac.biu.nlp.nlp.engineml.rteflow.preprocess;
import ac.biu.nlp.nlp.instruments.coreference.CoreferenceResolver;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizer;
import ac.biu.nlp.nlp.instruments.parse.BasicParser;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;
import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessor;


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
