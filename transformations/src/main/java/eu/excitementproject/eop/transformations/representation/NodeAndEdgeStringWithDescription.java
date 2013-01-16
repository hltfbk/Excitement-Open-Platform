package eu.excitementproject.eop.transformations.representation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.NodeAndEdgeString;

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
