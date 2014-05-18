/**
 * 
 */
package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.NodeAndEdgeString;
import eu.excitementproject.eop.transformations.representation.AnnotatedExtendedNodeAndEdgeString;
import eu.excitementproject.eop.transformations.representation.AnnotatedLemmaPosExtendedNodeAndEdgeString;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedLemmaPosNodeAndEdgeString;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeNodeAndEdgeString;
import eu.excitementproject.eop.transformations.representation.NodeAndEdgeStringWithDescription;

/**
 * Indicates what will be printed on each node of a parse-tree in the Visual-Tracing-Tool.
 * The string printed (on screen) for each node (and each edge) is determined
 * by a {@link NodeAndEdgeString}, which is what this class holds.
 * 
 * @author Amnon Lotan
 *
 * @since 27 Feb 2012
 */
public enum NodeDisplayMode
{
	// ENUM-CONSTANTS
	SHOW_ANNOTATIONS(new AnnotatedExtendedNodeAndEdgeString()),
	SHORT_NODES (new ExtendedLemmaPosNodeAndEdgeString()),
	NORMAL( new ExtendedNodeNodeAndEdgeString()),
	SHORT_NODES_WITH_ANNOTATIONS(new AnnotatedLemmaPosExtendedNodeAndEdgeString());

	

	// CONSTRUCTOR
	private NodeDisplayMode(NodeAndEdgeStringWithDescription<ExtendedInfo> nodeAndEdgeString) {
		this.nodeAndEdgeString = nodeAndEdgeString;
	}
	
	
	/**
	 * A static method which returns one of the enum-constants, based on given parameters.
	 * 
	 * @param annotationsSelected
	 * @param shortNodesSelected
	 * @return
	 */
	public static NodeDisplayMode newNodeDisplayMode(boolean annotationsSelected, boolean shortNodesSelected) {
		NodeDisplayMode ret = NodeDisplayMode.NORMAL;
		if (annotationsSelected)
			ret = ret.addShowAnnotations();
		else
			ret = ret.removeShowAnnotations();
		if (shortNodesSelected)
			ret = ret.addShortNodeContents();
		else
			ret = ret.removeShortNodeContents();
		return ret;
	}


	// Methods that return a new enum-constant, based on the existing one and a required change.
	
	public NodeDisplayMode addShowAnnotations() {
		switch (this)
		{
		case SHORT_NODES:
			return SHORT_NODES_WITH_ANNOTATIONS;
		case NORMAL:
			return SHOW_ANNOTATIONS;
		default:
			return this;
		}
	}

	public NodeDisplayMode removeShowAnnotations() {
		switch (this)
		{
		case SHORT_NODES_WITH_ANNOTATIONS:
			return SHORT_NODES;
		case SHOW_ANNOTATIONS:
			return NORMAL;
		default:
			return this;
		}
	}

	public NodeDisplayMode addShortNodeContents() {
		switch (this)
		{
		case SHOW_ANNOTATIONS:
			return SHORT_NODES_WITH_ANNOTATIONS;
		case NORMAL:
			return SHORT_NODES;
		default:
			return this;
		}
	}

	public NodeDisplayMode removeShortNodeContents() {
		switch (this)
		{
		case SHORT_NODES_WITH_ANNOTATIONS :
			return SHOW_ANNOTATIONS;
		case SHORT_NODES:
			return NORMAL;
		default:
			return this;
		}
	}

	public NodeAndEdgeStringWithDescription<ExtendedInfo> getNodeAndEdgeString() {
		return nodeAndEdgeString;
	}

	
	// FIELD
	
	/**
	 * Holds the actual {@link NodeAndEdgeString} which prints the strings on each
	 * node and each edge of the parse-tree.
	 */
	private NodeAndEdgeStringWithDescription<ExtendedInfo> nodeAndEdgeString;
}
