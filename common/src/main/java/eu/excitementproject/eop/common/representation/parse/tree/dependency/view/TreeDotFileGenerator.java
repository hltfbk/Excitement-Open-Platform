package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.StringUtilException;


/**
 * Generates a ".dot" file, for a given dependency parse tree.
 * <P>
 * A ".dot" file is input to dot software, which is
 * part of GraphViz software, that can be downloaded from
 * http://www.graphviz.org
 * 
 * @author Asher Stern
 *
 */
public class TreeDotFileGenerator<I extends Info>
{
	
	///////////////// NESTED EXCEPTION CLASS //////////////////////////
	public static class TreeDotFileGeneratorException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public TreeDotFileGeneratorException(String message, Throwable cause)
		{
			super(message, cause);
		}

		public TreeDotFileGeneratorException(String message)
		{
			super(message);
		}
	}
	
	
	///////////////// NESTED HELPER CLASS /////////////////////
	protected static class NodeAndId<I extends Info>
	{
		public AbstractNode<? extends I,?> node;
		public int localId;
		public NodeAndId(AbstractNode<? extends I,?> node, int localId)
		{
			this.node = node;
			this.localId = localId;
		}
	}
	
	///////////////////////// CONSTANTS ///////////////////////////
	protected static final String NODE_PREFIX = "node_";
	protected static final String ATTRIBUTE_BEGIN = "[";
	protected static final String ATTRIBUTE_END = "]";
	protected static final String ATTRIBUTE_SEPERATOR = ",";
	protected static final String LABEL_ATTRIBUTE_NAME = "label";
	protected static final String ATTRIBUTE_EQUAL = "=";
	protected static final String COLOR_ATTRIBUTE_NAME = "color";
	protected static final String COLOR_ATTRIBUTE_VALUE = "tan";
	protected static final String STYLE_ATTRIBUTE_AND_VALUE = "style=filled";
	protected static final String END_LINE_MARKER = ";";
	protected static final String EDGE_MARKER = "--";
	protected static final String GRAPH_TERM = "graph";
	protected static final String GRAPH_CONTENT_BEGIN = "{";
	protected static final String GRAPH_CONTENT_END = "}";
	
	
	
	
	
	

	////////////// PROTECTED & PRIVATE PART //////////////////////
	
	protected NodeString<I> nodeString = null;
	protected NodeAndEdgeString<I> nodeAndEdgeString = null;
	protected AbstractNode<? extends I, ?> root;
	protected String graphLabel = null;
	protected Set<? extends AbstractNode<? extends I, ?>> coloredNodes=null;
	protected File destinationFile = null;
	protected String dotFileString; // the contents of the dot file.
	
	protected static String getNodeStringId(int localId)
	{
		return NODE_PREFIX+String.valueOf(localId);
	}
	
	protected String generateNodeLine(int localId, AbstractNode<? extends I,?> node)
	{
		String id = getNodeStringId(localId);
		String nodeRepresentation = null;
		if (nodeString!=null)
		{
			nodeString.set(node);
			nodeRepresentation = nodeString.getStringRepresentation();
		}
		else
		{
			nodeAndEdgeString.set(node);
			nodeRepresentation = nodeAndEdgeString.getNodeStringRepresentation();
		}
		
		String label = null;
		try {
			label = StringUtil.convertStringToCString(nodeRepresentation);
		} catch (StringUtilException e) {}
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(id).append(ATTRIBUTE_BEGIN).append(LABEL_ATTRIBUTE_NAME).append(ATTRIBUTE_EQUAL);
		sb.append("\"").append(label).append("\"");
		if (coloredNodes!=null){ if (coloredNodes.contains(node))
		{
			sb.append(ATTRIBUTE_SEPERATOR).append(STYLE_ATTRIBUTE_AND_VALUE);
			sb.append(ATTRIBUTE_SEPERATOR).append(COLOR_ATTRIBUTE_NAME).append(ATTRIBUTE_EQUAL).append(COLOR_ATTRIBUTE_VALUE);
		}}
		sb.append(ATTRIBUTE_END).append(END_LINE_MARKER);

		
		return sb.toString();
	}
	
	protected String generateEdgeLine(int parentLocalId, int childLocalId)
	{
		return getNodeStringId(parentLocalId) + EDGE_MARKER + getNodeStringId(childLocalId) + END_LINE_MARKER; 
	}

	protected String generateLabledEdgeLine(int parentLocalId, int childLocalId,AbstractNode<? extends I,?> node) throws TreeDotFileGeneratorException
	{
		nodeAndEdgeString.set(node);
		try {
			return getNodeStringId(parentLocalId) + EDGE_MARKER + getNodeStringId(childLocalId)
			+ ATTRIBUTE_BEGIN+LABEL_ATTRIBUTE_NAME+ATTRIBUTE_EQUAL+"\"" + StringUtil.convertStringToCString(nodeAndEdgeString.getEdgeStringRepresentation()) + "\""+ATTRIBUTE_END
			+ END_LINE_MARKER;
		} catch (StringUtilException e) {
			throw new TreeDotFileGeneratorException("error in StringUtil.convertStringToCString", e );
		} 
	}

	
	
	////////////////////// PUBLIC PART //////////////////////////

	
	/**
	 * Use this constructor to generate a ".dot" file, that can be
	 * used later as input to "dot" ("dot.exe") program, to generate
	 * the graph visualization.
	 * 
	 * @param nodeString an object that can get a node, and return a
	 * string representation of it. see: {@link NodeString}.
	 * 
	 * @param root the tree root node.
	 * @param graphLabel a label to be printed (be part of the image)
	 * by GraphViz (dot) program.
	 * @param destinationFile a file that the <B> input to dot <B> will
	 * be printed to.
	 * @throws TreeDotFileGeneratorException any error (except <code> RuntimeException </code>
	 */
	public TreeDotFileGenerator(NodeString<I> nodeString,
			AbstractNode<? extends I, ?> root,
			String graphLabel,
			File destinationFile
			) throws TreeDotFileGeneratorException
	
	{
		this(nodeString,root,graphLabel);
		if (destinationFile==null) throw new TreeDotFileGeneratorException("constructor bad parameter to TreeDotFileGenerator: destinationFile==null");
		this.destinationFile = destinationFile;
	}

	/**
	 * Use this constructor to generate a string, that you can get using
	 * {@link #getDotFileString()}. That string is exactly the contents
	 * of the dot file (the input to "dot" program). But no ".dot" file
	 * is generated.
	 * 
	 * @param nodeString an object that can get a node, and return a
	 * string representation of it. see: {@link NodeString}.
	 * 
	 * @param root the tree root node.
	 * @param graphLabel a label to be printed (be part of the image)
	 * by GraphViz (dot) program.
	 * @throws TreeDotFileGeneratorException any error (except <code> RuntimeException </code>
	 */
	public TreeDotFileGenerator(NodeString<I> nodeString,
			AbstractNode<? extends I, ?> root,
			String graphLabel
			) throws TreeDotFileGeneratorException
	
	{
		if (nodeString==null) throw new TreeDotFileGeneratorException("constructor bad parameter to TreeDotFileGenerator: nodeString==null");
		if (root==null) throw new TreeDotFileGeneratorException("constructor bad parameter to TreeDotFileGenerator: root==null");
		
		this.nodeString = nodeString;
		this.root = root;
		this.graphLabel = graphLabel;
		this.destinationFile = null;
	}

	
	
	public TreeDotFileGenerator(NodeAndEdgeString<I> nodeAndEdgeString,
			AbstractNode<? extends I, ?> root,
			String graphLabel
			) throws TreeDotFileGeneratorException
	
	{
		if (nodeAndEdgeString==null) throw new TreeDotFileGeneratorException("constructor bad parameter to TreeDotFileGenerator: nodeAndEdgeString==null");
		if (root==null) throw new TreeDotFileGeneratorException("constructor bad parameter to TreeDotFileGenerator: root==null");
		
		this.nodeAndEdgeString = nodeAndEdgeString;
		this.root = root;
		this.graphLabel = graphLabel;
		this.destinationFile = null;
	}
	
	
	public TreeDotFileGenerator(NodeAndEdgeString<I> nodeAndEdgeString,
			AbstractNode<? extends I, ?> root,
			String graphLabel,
			File destinationFile
			) throws TreeDotFileGeneratorException
	
	{
		this(nodeAndEdgeString,root,graphLabel);
		if (destinationFile==null) throw new TreeDotFileGeneratorException("constructor bad parameter to TreeDotFileGenerator: destinationFile==null");
		this.destinationFile = destinationFile;
	}
	
	
	public void setColoredNodes(Set<? extends AbstractNode<? extends I, ?>> coloredNodes)
	{
		this.coloredNodes = coloredNodes;
	}

	/**
	 * Generates the string (the dot file contents) and the file (if
	 * the constructor {@link #TreeDotFileGenerator(NodeString, AbstractNode, String, File)} was used)
	 * @throws TreeDotFileGeneratorException
	 */
	public void generate() throws TreeDotFileGeneratorException
	{
		try
		{
			StringBuffer bufferDotFileContents = new StringBuffer();
			bufferDotFileContents.append(GRAPH_TERM+" "+GRAPH_CONTENT_BEGIN+"\n");
			if (this.graphLabel!=null)
				try {
					bufferDotFileContents.append(GRAPH_TERM+" "+ATTRIBUTE_BEGIN+LABEL_ATTRIBUTE_NAME+ATTRIBUTE_EQUAL+"\""+StringUtil.convertStringToCString(this.graphLabel)+"\""+ATTRIBUTE_END+END_LINE_MARKER+"\n");
				} catch (StringUtilException e) {
					throw new TreeDotFileGeneratorException("error in StringUtil.convertStringToCString() with " + this.graphLabel, e);
				}
			int currentLocalId = 1;
			Queue<NodeAndId<I>> queue = new LinkedList<NodeAndId<I>>();
			bufferDotFileContents.append(generateNodeLine(currentLocalId,root)+"\n");
			queue.offer(new NodeAndId<I>(root, currentLocalId));
			currentLocalId++;
			
			NodeAndId<I> currentParent = queue.poll();
			while (currentParent != null)
			{
				if (currentParent.node.getChildren()!=null)
				{
					for (AbstractNode<? extends I, ?> child : currentParent.node.getChildren())
					{
						bufferDotFileContents.append(generateNodeLine(currentLocalId,child)+"\n");
						if (this.nodeString!=null)
							bufferDotFileContents.append(generateEdgeLine(currentParent.localId,currentLocalId)+"\n");
						else
							bufferDotFileContents.append(generateLabledEdgeLine(currentParent.localId,currentLocalId,child)+"\n");
						
						queue.offer(new NodeAndId<I>(child, currentLocalId));
						currentLocalId++;
					}
				}
				currentParent = queue.poll();
			}

			bufferDotFileContents.append(GRAPH_CONTENT_END+"\n");
			this.dotFileString = bufferDotFileContents.toString();

			if (this.destinationFile != null)
			{
				PrintStream output = new PrintStream(new FileOutputStream(this.destinationFile));
				try
				{
					output.print(this.dotFileString);
				}
				finally
				{
					output.close();
				}
			}
		}
		catch(FileNotFoundException e)
		{
			throw new TreeDotFileGeneratorException("dot file generation failed",e);
		}
	}

	/**
	 * Returns the dot file contents that was generated by {@link #generate()} method.
	 * @return the dot file contents.
	 */
	public String getDotFileString()
	{
		return dotFileString;
	}
	
	
	
	
}
