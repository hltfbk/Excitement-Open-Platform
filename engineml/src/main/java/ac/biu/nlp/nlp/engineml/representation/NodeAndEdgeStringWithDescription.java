package ac.biu.nlp.nlp.engineml.representation;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.NodeAndEdgeString;

/**
 * {@link NodeAndEdgeString} with description about the strings.
 * @author Asher Stern
 * @since Jun 25, 2012
 *
 * @param <I>
 */
public interface NodeAndEdgeStringWithDescription<I extends Info> extends NodeAndEdgeString<I>
{
	public String getDescription();
}
