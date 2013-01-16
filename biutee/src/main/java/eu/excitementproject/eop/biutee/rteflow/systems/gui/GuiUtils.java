package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.NodeAndEdgeString;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeDotFileGenerator;
import eu.excitementproject.eop.common.utilities.OS;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.NodePrintUtilities;

/**
 * 
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and should be
 * improved. Need to re-design, make it more modular, adding documentation and
 * improve code.
 * 
 * @author Asher Stern
 * @since May 15, 2012
 *
 */
public class GuiUtils
{
	public static final char LINE_BREAK_CHAR = '-';
	public static final int MAX_CHARS_PER_ROW = 100;
	
	public GuiUtils(ActionsPerformer actionsPerformer)
	{
		super();
		this.actionsPerformer = actionsPerformer;
	}
	
	
	/**
	 * Creates an HTML table with triples (parent-relation-child) of the hypothesis
	 * that are missing in the text.<BR>
	 * The hypothesis and the missing triples are given as parameters, so this
	 * function merely creates a human-readable representation.
	 * 
	 * @param missingRelations
	 * @param originalHypothesisTree
	 * @return
	 */
	public static String buildMissingRelationText(Set<ExtendedNode> missingRelations, TreeAndParentMap<ExtendedInfo, ExtendedNode> originalHypothesisTree)
	{
		StringBuffer sb = new StringBuffer();
		if (missingRelations != null && !missingRelations.isEmpty()) {
			sb.append("Missing Elements from Hypothesis Tree:<BR>\n");

			// open a table
			sb.append("<table border=\"1\">\n");
			sb.append("<TR><TD>#</TD><TD>Missing node</TD><TD>relation</TD><TD>parent</TD></TR>");

			int count = 1;
			for (ExtendedNode missingHypothesisNode : missingRelations) {
				String id = (missingHypothesisNode.getInfo() != null && missingHypothesisNode.getInfo().getId() != null) ? missingHypothesisNode.getInfo().getId() : "unknown id";
				String lemma = InfoGetFields.getLemma(missingHypothesisNode.getInfo());
				String pos = InfoGetFields.getPartOfSpeech(missingHypothesisNode.getInfo());
				String relation = InfoGetFields.getRelation(missingHypothesisNode.getInfo());
				String missingHypoNodeString = StringUtil.escapeHTML(NodePrintUtilities.nodeDetailsToString(id, lemma, pos));
				
				String parentString = "null";
				ExtendedNode parent =  originalHypothesisTree.getParentMap().get(missingHypothesisNode);
				
				if (parent != null)
				{
					String parentId = (parent.getInfo() != null && parent.getInfo().getId() != null) ? parent.getInfo().getId() : "unknown id";
					String parentLemma = InfoGetFields.getLemma(parent.getInfo());
					String parentPos = InfoGetFields.getPartOfSpeech(parent.getInfo());
					parentString = StringUtil.escapeHTML(NodePrintUtilities.nodeDetailsToString(parentId, parentLemma, parentPos));
				}
				
				sb.append("<TR>");
				sb.append("<TD>");
				sb.append(count);
				sb.append("</TD>");
				sb.append("<TD>");
				sb.append(missingHypoNodeString);
				sb.append("</TD>");
				sb.append("<TD>");
				sb.append(relation);
				sb.append("</TD>");
				sb.append("<TD>");
				sb.append(parentString);
				sb.append("</TD>");
				sb.append("</TR>\n");
				
				++count;
			}
			sb.append("</table>\n");
		}
		return sb.toString();
	}
	
	/**
	 * Prints an HTML table with the feature-vector.
	 * This table is appended to a given {@link StringBuffer}.
	 * For each feature - if it is not identical to the feature that was before
	 * (as specified by the parameter <code>previousOperationFeatures</code>), then
	 * it is printed in <B>bold</B>
	 * 
	 * @param featureNames A map from each feature-index to its name.
	 * @param features the current feature vector
	 * @param previousOperationFeatures the feature vector before applying last transformation.
	 * @param stringBuffer A buffer into which the table will be appended.
	 */
	public static void printFeatures(Map<Integer, String> featureNames,
			Map<Integer, Double> features, Map<Integer, Double> previousOperationFeatures,
			StringBuffer stringBuffer)
	{
		if (null==featureNames)
		{
			featureNames = new LinkedHashMap<Integer, String>();
			for (Integer index : features.keySet())
			{
				featureNames.put(index, String.valueOf(index));
			}
		}
		stringBuffer.append("<TABLE BORDER=\"1\">");
		for (Integer index : features.keySet())
		{
			String name = featureNames.get(index);
			if (null==name) name = String.valueOf(index);
			Double featureValue = features.get(index);
			boolean bold = false;
			if ( (previousOperationFeatures!=null) && (featureValue!=null) )
			{
				if (!featureValue.equals(previousOperationFeatures.get(index)))
					bold = true;
			}
			
			stringBuffer.append("<TR>");
			
			stringBuffer.append("<TD>");
			if (bold) stringBuffer.append("<B>");
			stringBuffer.append(name);
			if (bold) stringBuffer.append("</B>");
			stringBuffer.append("</TD>");
			
			stringBuffer.append("<TD>");
			if (bold) stringBuffer.append("<B>");
			stringBuffer.append( String.format("%-4.5f",featureValue) );
			if (bold) stringBuffer.append("</B>");
			stringBuffer.append("</TD>");
			
			stringBuffer.append("</TR>").append("\n");
		}
		stringBuffer.append("</TABLE>");
	}
	
	
	public static Double getCostOfLastOperation(SingleTreeComponent treeComponent) throws VisualTracingToolException {

		if (null==treeComponent)
		{
			return null;
		}
		else if (null==treeComponent.getPrevious())
		{
			return null;
		}
		else
		{
			return treeComponent.getCost()-treeComponent.getPrevious().getCost();
		}
	}
	
	/**
	 * HTMLize the tool tip text in order to split it into readable lines
	 * @param toolTipText
	 * @return
	 */
	public static String htmlizeToolTipText(String toolTipText)
	{
		if (toolTipText == null)
			return toolTipText;
		
		StringBuilder sb = new StringBuilder();
		char[] caText = toolTipText.toCharArray();
		for (int index=0;index<caText.length;index+=MAX_CHARS_PER_ROW)
		{
			int endIndex = Math.min(index+MAX_CHARS_PER_ROW, caText.length);
			sb.append(toolTipText.substring(index, endIndex));
			
			if (endIndex<caText.length)
			{
				if ( Character.isLetter(caText[endIndex-1]) && Character.isLetter(caText[endIndex]) )
				{
					sb.append(LINE_BREAK_CHAR);
				}
				sb.append('\n');
			}
		}
		
		return "<HTML>" + StringUtil.escapeHTML(sb.toString()) + "</HTML>";
	}

	
	
	/**
	 * Given a parse tree - this method creates a {@link BufferedImage} that displays
	 * the tree.
	 * 
	 * @param tree
	 * @param sentence
	 * @param affectedNodes
	 * @return
	 * @throws VisualTracingToolException
	 */
	public BufferedImage createImage(ExtendedNode tree, String sentence, Set<ExtendedNode> affectedNodes) throws VisualTracingToolException
	{
		try
		{
			// Create a file with ".dot" extension, which represents the tree.
			File dotFile = File.createTempFile("treeDotFile", ".dot");
			NodeAndEdgeString<ExtendedInfo> nodeAndEdgeString = this.actionsPerformer.getNodeDisplayMode().getNodeAndEdgeString();
			TreeDotFileGenerator<ExtendedInfo> tdfg = new TreeDotFileGenerator<ExtendedInfo>(nodeAndEdgeString, tree, sentence, dotFile);
			if (affectedNodes != null)
			{
				tdfg.setColoredNodes(affectedNodes);
			}
			tdfg.generate();
			// The ".dot" file was generated. Wrap in try...finally the rest of the code, and delete the file in the finally clause.
			try
			{
				String[] commandArray = new String[] { "dot", "-Gcharset=latin1", "-T", "jpg", "-O", dotFile.getPath() };

				ProcessBuilder dotProcessBuilder = new ProcessBuilder(Arrays.asList(commandArray));
				File tempDir = new File(System.getProperty("java.io.tmpdir"));
				dotProcessBuilder.directory(tempDir);
				try
				{
					Process process = dotProcessBuilder.start();
					int exitValue = process.waitFor();
					if (exitValue != 0)
						throw new VisualTracingToolException("dot bad exit value: " + exitValue);
					File imageFile = new File(dotFile + ".jpg");
					if (!imageFile.exists())
						throw new VisualTracingToolException("Image was not created. Should be file: " + imageFile.getAbsolutePath());
					try
					{
						// The image file exists. Wrap in try...finally, and delete the image file in the finally clause.
						BufferedImage image = ImageIO.read(imageFile);
						if (image == null)
							throw new VisualTracingToolException("Null image");

						return image;
					}
					finally
					{
						boolean imageFileDeleted = imageFile.delete();
						if (!imageFileDeleted)
						{
							imageFile.deleteOnExit();
						}
					}
				}
				catch(IOException iox) // this catch matches the try ... Process ... start()
				{
					throw new VisualTracingToolException("Could not run the \"dot\" program.\nIs GraphViz installed on your computer?\nIs \""+ OS.programName("dot")+"\" in your PATH?\nNote that there might be other reasons, please see nested exception." , iox);
				}

			}
			finally
			{
				boolean dotFileDeleted = dotFile.delete();
				if (!dotFileDeleted)
				{
					dotFile.deleteOnExit();
				}
			}
		}
		catch (Exception ex)
		{
			throw new VisualTracingToolException("Could not create image", ex);
		}
	}


//	private final static String strLength(String str, int length, char c)
//	{
//		String app = "";
//		if (str.length()<length)
//		{
//			char[] append = new char[length-str.length()];
//			for (int index=0;index<append.length;++index)
//			{
//				append[index]=c;
//			}
//			app = new String(append);
//		}
//		return str+app;
//	}
	
	private ActionsPerformer actionsPerformer;
}
