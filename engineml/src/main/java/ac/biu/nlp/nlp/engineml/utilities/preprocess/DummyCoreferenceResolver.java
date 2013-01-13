package ac.biu.nlp.nlp.engineml.utilities.preprocess;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.en.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.en.coreference.CoreferenceResolver;


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
