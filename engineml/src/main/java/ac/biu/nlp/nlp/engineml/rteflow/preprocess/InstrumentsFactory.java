package ac.biu.nlp.nlp.engineml.rteflow.preprocess;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.common.representation.parse.ParserRunException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;


/**
 * 
 * @author Asher Stern
 * @since Feb 26, 2011
 *
 */
public class InstrumentsFactory
{
	public Instruments<Info, BasicNode> getDefaultInstruments(ConfigurationParams params) throws NumberFormatException, ConfigurationException, TeEngineMlException, ParserRunException, NamedEntityRecognizerException, TextPreprocessorException
	{
		return DefaultInstruments.fromConfigurationFile(params);
	}

}
