package ac.biu.nlp.nlp.engineml.utilities.preprocess;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import ac.biu.nlp.nlp.instruments.coreference.CoreferenceResolutionException;
import ac.biu.nlp.nlp.instruments.coreference.CoreferenceResolver;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;


/**
 * This is a {@link CoreferenceResolver} that resolves nothing. Its effect is as if
 * a regular {@link CoreferenceResolver} found nothing.
 * 
 * @author Asher Stern
 * @since 2011
 *
 */
public class DummyCoreferenceResolver extends CoreferenceResolver<BasicNode>
{
	@Override
	public void init() throws CoreferenceResolutionException
	{
	}

	@Override
	public void cleanUp()
	{
	}

	@Override
	protected void implementResolve() throws CoreferenceResolutionException
	{
		this.coreferenceInformation = new TreeCoreferenceInformation<BasicNode>();
	}
}
